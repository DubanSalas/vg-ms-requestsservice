package vallegrande.edu.pe.requestsservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.Request.MassInfo;

@Component
public class MassClient {

    private final WebClient webClient;

    public MassClient(@Value("${services.community.url:http://localhost:8090}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<MassInfo> findById(String id) {
        return webClient.get()
                .uri("/api/v1/masses/{id}", id)
                .retrieve()
                .bodyToMono(MassInfo.class)
                .onErrorResume(e -> Mono.empty());
    }

    public Flux<MassInfo> findByTenant(Long tenantId) {
        return webClient.get()
                .uri("/api/v1/masses/tenant/{tenantId}", tenantId)
                .retrieve()
                .bodyToFlux(MassInfo.class)
                .onErrorResume(e -> Flux.empty());
    }

    public Flux<MassInfo> findActive() {
        return webClient.get()
                .uri("/api/v1/masses/active")
                .retrieve()
                .bodyToFlux(MassInfo.class)
                .onErrorResume(e -> Flux.empty());
    }
}
