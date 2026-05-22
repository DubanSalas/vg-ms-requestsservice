package vallegrande.edu.pe.requestsservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.Request;

import java.util.UUID;

public interface RequestService {
    Flux<Request> findAll();
    Flux<Request> findByStatus(String status);
    Flux<Request> findByTenantId(Long tenantId);
    Flux<Request> findByTenantIdAndStatus(Long tenantId, String status);
    Flux<Request> findByTenantIdAndCategory(Long tenantId, String category);
    Flux<Request> findByTenantIdAndCategoryAndStatus(Long tenantId, String category, String status);
    Flux<Request> findByTenantIdAndSacramentId(Long tenantId, UUID sacramentId);
    Mono<Request> findById(Long id);
    Mono<Request> save(Request request);
    Mono<Request> update(Long id, Request request);
    Mono<Request> changeStatus(Long id, String status);
    Mono<Void> delete(Long id);
}
