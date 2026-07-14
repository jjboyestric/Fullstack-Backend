package MIcroservicioB.BffOrquestador.service;

import MIcroservicioB.BffOrquestador.dto.ResumenAlumnoDTO;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para OrquestadorService.
 * Valida el comportamiento del Circuit Breaker (Resilience4j) y el Facade.
 * Usa StepVerifier (reactor-test) para Mono/Flux reactivos.
 */
@ExtendWith(MockitoExtension.class)
class OrquestadorServiceTest {

    private WebClient webClientNotas;
    private WebClient webClientAsistencia;
    private CircuitBreaker cbNotas;
    private CircuitBreaker cbAsistencia;
    private OrquestadorService orquestadorService;

    @SuppressWarnings("unchecked")
    private WebClient.RequestHeadersUriSpec reqUriNotas;
    @SuppressWarnings("unchecked")
    private WebClient.RequestHeadersUriSpec reqUriAsistencia;
    private WebClient.RequestHeadersSpec   reqNotas;
    private WebClient.RequestHeadersSpec   reqAsistencia;
    private WebClient.ResponseSpec         resNotas;
    private WebClient.ResponseSpec         resAsistencia;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        webClientNotas      = mock(WebClient.class);
        webClientAsistencia = mock(WebClient.class);

        // CB con sliding window de 2 y threshold 100% para ABRIR en el primer fallo
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(2)
                .failureRateThreshold(100)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(cbConfig);
        cbNotas      = registry.circuitBreaker("msNotas-test");
        cbAsistencia = registry.circuitBreaker("msAsistencia-test");

        orquestadorService = new OrquestadorService(
                webClientNotas, webClientAsistencia, cbNotas, cbAsistencia);

        reqUriNotas      = mock(WebClient.RequestHeadersUriSpec.class);
        reqUriAsistencia = mock(WebClient.RequestHeadersUriSpec.class);
        reqNotas         = mock(WebClient.RequestHeadersSpec.class);
        reqAsistencia    = mock(WebClient.RequestHeadersSpec.class);
        resNotas         = mock(WebClient.ResponseSpec.class);
        resAsistencia    = mock(WebClient.ResponseSpec.class);
    }

    // ── Test 1 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("FACADE: obtenerResumenAlumno() combina notas + asistencia en un DTO")
    @SuppressWarnings("unchecked")
    void testFacadeCombinaDatos() {
        Map<String, Object> notasMap = Map.of(
                "nombre", "María Pérez", "curso", "3°A",
                "promedioGeneral", 6.3, "totalEvaluaciones", 3);
        Map<String, Object> asistMap = Map.of(
                "asistidas", 18L, "faltadas", 2L, "porcentaje", 90.0);

        when(webClientNotas.get()).thenReturn(reqUriNotas);
        when(reqUriNotas.uri(anyString(), any(Object.class))).thenReturn(reqNotas);
        when(reqNotas.retrieve()).thenReturn(resNotas);
        when(resNotas.bodyToMono(Map.class)).thenReturn(Mono.just(notasMap));

        when(webClientAsistencia.get()).thenReturn(reqUriAsistencia);
        when(reqUriAsistencia.uri(anyString(), any(Object.class))).thenReturn(reqAsistencia);
        when(reqAsistencia.retrieve()).thenReturn(resAsistencia);
        when(resAsistencia.bodyToMono(Map.class)).thenReturn(Mono.just(asistMap));

        StepVerifier.create(orquestadorService.obtenerResumenAlumno(1L))
                .assertNext(dto -> {
                    assert dto.getNombreAlumno().equals("María Pérez");
                    assert dto.getCurso().equals("3°A");
                    assert dto.getPromedioGeneral() == 6.3;
                    assert dto.getPorcentajeAsistencia() == 90.0;
                    System.out.println("TEST PASS FACADE: " + dto.getNombreAlumno() +
                            " | promedio=" + dto.getPromedioGeneral() +
                            " | asistencia=" + dto.getPorcentajeAsistencia() + "%");
                })
                .verifyComplete();
    }

    // ── Test 2 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CIRCUIT BREAKER: fallback cuando MS Notas lanza excepción")
    @SuppressWarnings("unchecked")
    void testCircuitBreakerFallbackNotasFalla() {
        Map<String, Object> asistMap = Map.of(
                "asistidas", 15L, "faltadas", 5L, "porcentaje", 75.0);

        when(webClientNotas.get()).thenReturn(reqUriNotas);
        when(reqUriNotas.uri(anyString(), any(Object.class))).thenReturn(reqNotas);
        when(reqNotas.retrieve()).thenReturn(resNotas);
        when(resNotas.bodyToMono(Map.class))
                .thenReturn(Mono.error(new RuntimeException("MS Notas caído")));

        when(webClientAsistencia.get()).thenReturn(reqUriAsistencia);
        when(reqUriAsistencia.uri(anyString(), any(Object.class))).thenReturn(reqAsistencia);
        when(reqAsistencia.retrieve()).thenReturn(resAsistencia);
        when(resAsistencia.bodyToMono(Map.class)).thenReturn(Mono.just(asistMap));

        StepVerifier.create(orquestadorService.obtenerResumenAlumno(1L))
                .assertNext(dto -> {
                    assert dto.getPromedioGeneral() == 0.0 :
                            "fallback notas debe retornar promedioGeneral=0.0";
                    assert dto.getPorcentajeAsistencia() == 75.0 :
                            "asistencia debe seguir funcionando";
                    System.out.println("TEST PASS CB NOTAS: fallback promedio=0.0 | asistencia="
                            + dto.getPorcentajeAsistencia() + "%");
                })
                .verifyComplete();
    }

    // ── Test 3 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CIRCUIT BREAKER: fallback cuando MS Asistencia lanza excepción")
    @SuppressWarnings("unchecked")
    void testCircuitBreakerFallbackAsistenciaFalla() {
        Map<String, Object> notasMap = Map.of(
                "nombre", "Juan González", "curso", "3°A",
                "promedioGeneral", 5.0, "totalEvaluaciones", 2);

        when(webClientNotas.get()).thenReturn(reqUriNotas);
        when(reqUriNotas.uri(anyString(), any(Object.class))).thenReturn(reqNotas);
        when(reqNotas.retrieve()).thenReturn(resNotas);
        when(resNotas.bodyToMono(Map.class)).thenReturn(Mono.just(notasMap));

        when(webClientAsistencia.get()).thenReturn(reqUriAsistencia);
        when(reqUriAsistencia.uri(anyString(), any(Object.class))).thenReturn(reqAsistencia);
        when(reqAsistencia.retrieve()).thenReturn(resAsistencia);
        when(resAsistencia.bodyToMono(Map.class))
                .thenReturn(Mono.error(new RuntimeException("MS Asistencia caído")));

        StepVerifier.create(orquestadorService.obtenerResumenAlumno(2L))
                .assertNext(dto -> {
                    assert dto.getPromedioGeneral() == 5.0 :
                            "notas deben seguir funcionando";
                    assert dto.getPorcentajeAsistencia() == 0.0 :
                            "fallback asistencia debe retornar porcentaje=0.0";
                    System.out.println("TEST PASS CB ASISTENCIA: promedio=" + dto.getPromedioGeneral()
                            + " | fallback asistencia=0.0%");
                })
                .verifyComplete();
    }

    // ── Test 4 ────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("CIRCUIT BREAKER: estado inicial del CB es CLOSED")
    void testCircuitBreakerEstadoInicialCerrado() {
        assert cbNotas.getState() == CircuitBreaker.State.CLOSED :
                "CB debe iniciar en CLOSED";
        assert cbAsistencia.getState() == CircuitBreaker.State.CLOSED :
                "CB debe iniciar en CLOSED";
        System.out.println("TEST PASS CB ESTADO: msNotas=" + cbNotas.getState()
                + " | msAsistencia=" + cbAsistencia.getState());
    }
}
