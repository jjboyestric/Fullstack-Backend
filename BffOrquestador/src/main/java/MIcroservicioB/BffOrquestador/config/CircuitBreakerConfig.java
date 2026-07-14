package MIcroservicioB.BffOrquestador.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║  PATRÓN: CIRCUIT BREAKER (Resilience4j)                                ║
 * ║                                                                          ║
 * ║  Un Circuit Breaker tiene 3 estados:                                    ║
 * ║    CLOSED  → llamadas fluyen normalmente                                ║
 * ║    OPEN    → llamadas bloqueadas (microservicio caído)                  ║
 * ║    HALF_OPEN → prueba si el servicio se recuperó                        ║
 * ║                                                                          ║
 * ║  Configuración (application.properties):                                ║
 * ║    sliding-window-size=5     → evalúa últimas 5 llamadas               ║
 * ║    failure-rate-threshold=50 → abre si ≥50% fallaron                   ║
 * ║    wait-duration=10s         → espera 10s antes de pasar a HALF_OPEN   ║
 * ║    permitted-half-open=2     → 2 llamadas de prueba en HALF_OPEN       ║
 * ║                                                                          ║
 * ║  Este bean registra listeners que loguean cada cambio de estado         ║
 * ║  (CLOSED→OPEN, OPEN→HALF_OPEN, HALF_OPEN→CLOSED).                     ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Slf4j
@Configuration
public class CircuitBreakerConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerConfig(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    /**
     * Circuit Breaker para MS Notas.
     * Se configura automáticamente desde application.properties
     * (instancia: resilience4j.circuitbreaker.instances.msNotas).
     */
    @Bean(name = "cbNotas")
    public CircuitBreaker circuitBreakerNotas() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("msNotas");
        registrarListeners(cb, "MS-Notas");
        log.info("CIRCUIT BREAKER registrado: msNotas | Estado inicial: {}", cb.getState());
        return cb;
    }

    /**
     * Circuit Breaker para MS Asistencia.
     * Se configura automáticamente desde application.properties
     * (instancia: resilience4j.circuitbreaker.instances.msAsistencia).
     */
    @Bean(name = "cbAsistencia")
    public CircuitBreaker circuitBreakerAsistencia() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("msAsistencia");
        registrarListeners(cb, "MS-Asistencia");
        log.info("CIRCUIT BREAKER registrado: msAsistencia | Estado inicial: {}", cb.getState());
        return cb;
    }

    /**
     * Registra listeners de eventos en el Circuit Breaker
     * para loguear cada transición de estado.
     */
    private void registrarListeners(CircuitBreaker cb, String nombre) {
        cb.getEventPublisher()
                .onStateTransition(event -> log.warn(
                        "[CIRCUIT BREAKER - {}] Transición: {} → {}",
                        nombre,
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()
                ))
                .onCallNotPermitted(event -> log.error(
                        "[CIRCUIT BREAKER - {}] LLAMADA BLOQUEADA — circuito ABIERTO", nombre
                ))
                .onError(event -> log.error(
                        "[CIRCUIT BREAKER - {}] Error registrado: {} (tasa de fallos: {}%)",
                        nombre, event.getThrowable().getMessage(), event.getCircuitBreakerName()
                ))
                .onSuccess(event -> log.debug(
                        "[CIRCUIT BREAKER - {}] Llamada exitosa en {}ms", nombre, event.getElapsedDuration().toMillis()
                ));
    }
}
