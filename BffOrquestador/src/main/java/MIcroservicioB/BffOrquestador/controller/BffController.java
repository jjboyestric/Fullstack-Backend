package MIcroservicioB.BffOrquestador.controller;

import MIcroservicioB.BffOrquestador.dto.ResumenAlumnoDTO;
import MIcroservicioB.BffOrquestador.service.OrquestadorService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Controlador BFF — expone los endpoints del orquestador.
 * Incluye /estado-circuitos para monitorear el estado real de los CB.
 */
@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "http://localhost:5173")
public class BffController {

    private final OrquestadorService orquestadorService;
    private final CircuitBreaker cbNotas;
    private final CircuitBreaker cbAsistencia;

    public BffController(
            OrquestadorService orquestadorService,
            @Qualifier("cbNotas")       CircuitBreaker cbNotas,
            @Qualifier("cbAsistencia")  CircuitBreaker cbAsistencia) {
        this.orquestadorService = orquestadorService;
        this.cbNotas            = cbNotas;
        this.cbAsistencia       = cbAsistencia;
    }

    // ── Resumen consolidado del alumno (Facade + Circuit Breaker) ───────────
    @GetMapping("/alumno/{id}/resumen")
    public Mono<ResumenAlumnoDTO> obtenerResumen(@PathVariable Long id) {
        return orquestadorService.obtenerResumenAlumno(id);
    }

    // ── Notas del alumno (Circuit Breaker) ──────────────────────────────────
    @GetMapping("/alumno/{id}/notas")
    public Flux<Object> obtenerNotas(@PathVariable Long id) {
        return orquestadorService.obtenerNotasAlumno(id);
    }

    // ── Asistencia del alumno (Circuit Breaker) ──────────────────────────────
    @GetMapping("/alumno/{id}/asistencia")
    public Flux<Object> obtenerAsistencia(@PathVariable Long id) {
        return orquestadorService.obtenerAsistenciaAlumno(id);
    }

    // ── Health check básico ──────────────────────────────────────────────────
    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("BFF Orquestador - Colegio Bernardo O'Higgins: OK");
    }

    /**
     * Estado en tiempo real de los Circuit Breakers.
     * Útil para monitoreo y defensa oral.
     * GET /api/bff/estado-circuitos
     *
     * Respuesta ejemplo cuando todo OK:
     *   { "msNotas": "CLOSED", "msAsistencia": "CLOSED" }
     *
     * Respuesta cuando MS Notas está caído (≥50% fallos en ventana de 5):
     *   { "msNotas": "OPEN", "msAsistencia": "CLOSED" }
     */
    @GetMapping("/estado-circuitos")
    public Mono<Map<String, String>> estadoCircuitos() {
        return Mono.just(Map.of(
                "msNotas",      cbNotas.getState().name(),
                "msAsistencia", cbAsistencia.getState().name()
        ));
    }
}
