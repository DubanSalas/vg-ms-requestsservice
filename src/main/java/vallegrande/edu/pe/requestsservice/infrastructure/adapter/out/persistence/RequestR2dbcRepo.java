package vallegrande.edu.pe.requestsservice.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Repositorio R2DBC — solo trabaja con RequestEntity.
 */
public interface RequestR2dbcRepo extends ReactiveCrudRepository<RequestEntity, Long> {
    Flux<RequestEntity> findByStatus(String status);
    Flux<RequestEntity> findByTenantId(Long tenantId);
    Flux<RequestEntity> findByTenantIdAndStatus(Long tenantId, String status);
    Flux<RequestEntity> findByTenantIdAndSacramentId(Long tenantId, UUID sacramentId);
    Flux<RequestEntity> findByTenantIdAndMassId(Long tenantId, String massId);
}
