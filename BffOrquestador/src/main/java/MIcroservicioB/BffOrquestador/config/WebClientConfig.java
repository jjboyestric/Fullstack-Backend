package MIcroservicioB.BffOrquestador.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  PATRÓN: FACTORY METHOD                                             ║
 * ║  Centraliza y encapsula la creación de instancias WebClient.        ║
 * ║  Cada microservicio obtiene su propio cliente configurado con:      ║
 * ║    - URL base inyectada desde application.properties                ║
 * ║    - Timeouts de conexión y lectura (Netty)                         ║
 * ║    - Filtro de logging de requests/responses (ExchangeFilterFunction)║
 * ║    - Cabecera Content-Type por defecto                              ║
 * ║  Los @Bean son Singletons gestionados por el contenedor Spring IoC. ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${microservicio.notas.url}")
    private String notasUrl;

    @Value("${microservicio.asistencia.url}")
    private String asistenciaUrl;

    @Value("${webclient.connect-timeout-ms:3000}")
    private int connectTimeoutMs;

    @Value("${webclient.read-timeout-ms:5000}")
    private int readTimeoutMs;

    // ── Factory Method 1: WebClient para MS Notas ───────────────────────────
    @Bean(name = "webClientNotas")
    public WebClient webClientNotas() {
        log.info("FACTORY METHOD: Creando WebClient para MS Notas → {}", notasUrl);
        return buildWebClient(notasUrl, "MS-Notas");
    }

    // ── Factory Method 2: WebClient para MS Asistencia ─────────────────────
    @Bean(name = "webClientAsistencia")
    public WebClient webClientAsistencia() {
        log.info("FACTORY METHOD: Creando WebClient para MS Asistencia → {}", asistenciaUrl);
        return buildWebClient(asistenciaUrl, "MS-Asistencia");
    }

    /**
     * Método de fábrica privado: construye un WebClient con Netty (timeouts)
     * y un ExchangeFilterFunction que registra cada request/response en el log.
     *
     * @param baseUrl    URL base del microservicio destino
     * @param clientName Nombre para identificar el cliente en los logs
     * @return WebClient configurado y listo para usar
     */
    private WebClient buildWebClient(String baseUrl, String clientName) {

        // Netty HttpClient con timeouts configurados desde properties
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(readTimeoutMs))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept",       "application/json")
                .filter(logRequest(clientName))
                .filter(logResponse(clientName))
                .build();
    }

    // ── ExchangeFilterFunction: log de request saliente ─────────────────────
    private ExchangeFilterFunction logRequest(String clientName) {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.debug("[{}] >>> {} {}", clientName, req.method(), req.url());
            return Mono.just(req);
        });
    }

    // ── ExchangeFilterFunction: log de response entrante ────────────────────
    private ExchangeFilterFunction logResponse(String clientName) {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            log.debug("[{}] <<< HTTP {}", clientName, res.statusCode());
            return Mono.just(res);
        });
    }
}
