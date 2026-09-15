package vallegrande.edu.pe.requestsservice.domain.port.in;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.domain.model.Request;

import java.util.UUID;

/**
 * Puerto de entrada (driving port).
 * Define lo que la aplicación ofrece al mundo exterior.
 */
public interface RequestUseCase {
    Flux<Request> findAll();
    Flux<Request> findByStatus(String status);
    Flux<Request> findByTenantId(Long tenantId);
    Flux<Request> findByTenantIdAndStatus(Long tenantId, String status);
    Flux<Request> findByTenantIdAndSacramentId(Long tenantId, UUID sacramentId);
    Flux<Request> findByTenantIdAndMassId(Long tenantId, String massId);
    Mono<Request> findById(Long id);
    Mono<Request> save(Request request);
    Mono<Request> update(Long id, Request request);
    Mono<Request> changeStatus(Long id, String status);
    Mono<Request> changePriority(Long id, String priority);
    Mono<Void>    delete(Long id);
}
