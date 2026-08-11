package vallegrande.edu.pe.requestsservice.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.domain.model.Request.SacramentInfo;

import java.util.UUID;

/**
 * Cliente HTTP hacia vg-ms-sacramentservice.
 * Vive en infrastructure porque depende de WebClient (framework externo).
 */
@Component
public class SacramentClient {

    private final WebClient webClient;

    public SacramentClient(@Value("${services.sacrament.url:http://localhost:8086}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<SacramentInfo> findById(UUID id) {
        return webClient.get()
                .uri("/api/sacraments/{id}", id)
                .retrieve()
                .bodyToMono(SacramentInfo.class)
                .onErrorResume(e -> Mono.empty());
    }

    public Flux<SacramentInfo> findAll() {
        return webClient.get()
                .uri("/api/sacraments")
                .retrieve()
                .bodyToFlux(SacramentInfo.class)
                .onErrorResume(e -> Flux.empty());
    }

    public Flux<SacramentInfo> findByTenant(Integer tenantId) {
        return webClient.get()
                .uri("/api/sacraments/tenant/{tenantId}", tenantId)
                .retrieve()
                .bodyToFlux(SacramentInfo.class)
                .onErrorResume(e -> Flux.empty());
    }
}
