package MIcroservicioB.BffOrquestador.service;

import MIcroservicioB.BffOrquestador.dto.ResumenAlumnoDTO;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║  PATRONES IMPLEMENTADOS EN ESTA CLASE                                   ║
 * ║                                                                          ║
 * ║  1. FACADE                                                               ║
 * ║     El frontend llama a un único endpoint (/api/bff/alumno/{id}/resumen) ║
 * ║     y este servicio oculta que internamente hace 2 llamadas HTTP         ║
 * ║     en paralelo a microservicios distintos.                              ║
 * ║                                                                          ║
 * ║  2. CIRCUIT BREAKER (Resilience4j)                                       ║
 * ║     Cada llamada a un microservicio pasa por su CircuitBreaker:          ║
 * ║       - CLOSED   → flujo normal                                          ║
 * ║       - OPEN     → bloqueado; retorna fallback inmediatamente            ║
 * ║       - HALF_OPEN → prueba recuperación con llamadas de prueba           ║
 * ║     El CB se aplica con transformIfNotEmpty(CircuitBreakerOperator)      ║
 * ║     que es el operador oficial de Resilience4j para Project Reactor.     ║
 * ║                                                                          ║
 * ║  3. REACTIVE ORCHESTRATION (Mono.zip / Mono.zip paralelo)                ║
 * ║     Las dos llamadas HTTP se ejecutan en paralelo.                       ║
 * ║     La latencia total = max(tNotas, tAsistencia), no la suma.            ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Slf4j
@Service
public class OrquestadorService {

    private final WebClient webClientNotas;
    private final WebClient webClientAsistencia;
    private final CircuitBreaker cbNotas;
    private final CircuitBreaker cbAsistencia;

    // Fallbacks usados cuando el CB está ABIERTO o cuando el MS falla
    private static final Map<String, Object> FALLBACK_NOTAS =
            Map.of("promedioGeneral", 0.0, "nombre", "No disponible", "curso", "N/A",
                   "totalEvaluaciones", 0);

    private static final Map<String, Object> FALLBACK_ASISTENCIA =
            Map.of("asistidas", 0L, "faltadas", 0L, "totalClases", 0L, "porcentaje", 0.0);

    public OrquestadorService(
            @Qualifier("webClientNotas")      WebClient webClientNotas,
            @Qualifier("webClientAsistencia") WebClient webClientAsistencia,
            @Qualifier("cbNotas")             CircuitBreaker cbNotas,
            @Qualifier("cbAsistencia")        CircuitBreaker cbAsistencia) {
        this.webClientNotas      = webClientNotas;
        this.webClientAsistencia = webClientAsistencia;
        this.cbNotas             = cbNotas;
        this.cbAsistencia        = cbAsistencia;
    }

    // ── FACADE + CIRCUIT BREAKER: resumen consolidado ──────────────────────
    @SuppressWarnings("unchecked")
    public Mono<ResumenAlumnoDTO> obtenerResumenAlumno(Long alumnoId) {
        log.info("FACADE: Orquestando resumen para alumno ID={} | CB-Notas={} | CB-Asistencia={}",
                alumnoId, cbNotas.getState(), cbAsistencia.getState());

        // Llamada 1: MS Notas — protegida por Circuit Breaker "msNotas"
        Mono<Map> notasMono = webClientNotas.get()
                .uri("/api/notas/alumno/{id}/promedio", alumnoId)
                .retrieve()
                .bodyToMono(Map.class)
                .transformDeferred(CircuitBreakerOperator.of(cbNotas))   // ← CB real
                .onErrorResume(ex -> {
                    log.warn("CIRCUIT BREAKER [msNotas] activó fallback para alumno={}: {}",
                            alumnoId, ex.getMessage());
                    return Mono.just(FALLBACK_NOTAS);
                });

        // Llamada 2: MS Asistencia — protegida por Circuit Breaker "msAsistencia"
        Mono<Map> asistenciaMono = webClientAsistencia.get()
                .uri("/api/asistencia/alumno/{id}/resumen", alumnoId)
                .retrieve()
                .bodyToMono(Map.class)
                .transformDeferred(CircuitBreakerOperator.of(cbAsistencia)) // ← CB real
                .onErrorResume(ex -> {
                    log.warn("CIRCUIT BREAKER [msAsistencia] activó fallback para alumno={}: {}",
                            alumnoId, ex.getMessage());
                    return Mono.just(FALLBACK_ASISTENCIA);
                });

        // Mono.zip ejecuta las 2 llamadas EN PARALELO y combina los resultados
        return Mono.zip(notasMono, asistenciaMono, (notas, asistencia) -> {
            ResumenAlumnoDTO dto = new ResumenAlumnoDTO();
            dto.setAlumnoId(alumnoId);
            dto.setNombreAlumno((String)  notas.getOrDefault("nombre",          "Alumno " + alumnoId));
            dto.setCurso((String)          notas.getOrDefault("curso",           "N/A"));
            dto.setPromedioGeneral(
                    ((Number) notas.getOrDefault("promedioGeneral", 0.0)).doubleValue());
            dto.setTotalClasesAsistidas(
                    ((Number) asistencia.getOrDefault("asistidas",    0L)).longValue());
            dto.setTotalClasesFaltadas(
                    ((Number) asistencia.getOrDefault("faltadas",     0L)).longValue());
            dto.setPorcentajeAsistencia(
                    ((Number) asistencia.getOrDefault("porcentaje",   0.0)).doubleValue());
            log.info("FACADE: ResumenAlumnoDTO construido para '{}' | promedio={} | asistencia={}%",
                    dto.getNombreAlumno(), dto.getPromedioGeneral(), dto.getPorcentajeAsistencia());
            return dto;
        });
    }

    // ── CIRCUIT BREAKER: notas del alumno ──────────────────────────────────
    @SuppressWarnings("unchecked")
    public Flux<Object> obtenerNotasAlumno(Long alumnoId) {
        log.debug("CB-Notas estado={} | solicitando notas alumno={}", cbNotas.getState(), alumnoId);
        return webClientNotas.get()
                .uri("/api/notas/alumno/{id}", alumnoId)
                .retrieve()
                .bodyToFlux(Object.class)
                .transformDeferred(CircuitBreakerOperator.of(cbNotas))
                .onErrorResume(ex -> {
                    log.error("CIRCUIT BREAKER [msNotas] fallback Flux para alumno={}: {}", alumnoId, ex.getMessage());
                    return Flux.fromIterable(List.of());
                });
    }

    // ── CIRCUIT BREAKER: asistencia del alumno ─────────────────────────────
    @SuppressWarnings("unchecked")
    public Flux<Object> obtenerAsistenciaAlumno(Long alumnoId) {
        log.debug("CB-Asistencia estado={} | solicitando asistencia alumno={}", cbAsistencia.getState(), alumnoId);
        return webClientAsistencia.get()
                .uri("/api/asistencia/alumno/{id}", alumnoId)
                .retrieve()
                .bodyToFlux(Object.class)
                .transformDeferred(CircuitBreakerOperator.of(cbAsistencia))
                .onErrorResume(ex -> {
                    log.error("CIRCUIT BREAKER [msAsistencia] fallback Flux para alumno={}: {}", alumnoId, ex.getMessage());
                    return Flux.fromIterable(List.of());
                });
    }
}
