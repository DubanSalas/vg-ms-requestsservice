package vallegrande.edu.pe.requestsservice.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.domain.model.Request;

import java.util.UUID;

/**
 * Puerto de salida (driven port).
 * Define lo que la aplicación necesita de la capa de persistencia.
 */
public interface RequestRepositoryPort {
    Flux<Request> findAll();
    Flux<Request> findByStatus(String status);
    Flux<Request> findByTenantId(Long tenantId);
    Flux<Request> findByTenantIdAndStatus(Long tenantId, String status);
    Flux<Request> findByTenantIdAndSacramentId(Long tenantId, UUID sacramentId);
    Flux<Request> findByTenantIdAndMassId(Long tenantId, String massId);
    Mono<Request> findById(Long id);
    Mono<Request> save(Request request);
}
