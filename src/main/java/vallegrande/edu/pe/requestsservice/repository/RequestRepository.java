package vallegrande.edu.pe.requestsservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.requestsservice.model.Request;

import java.util.UUID;

public interface RequestRepository extends ReactiveCrudRepository<Request, Long> {
    Flux<Request> findByStatus(String status);
    Flux<Request> findByTenantId(Long tenantId);
    Flux<Request> findByTenantIdAndStatus(Long tenantId, String status);
    Flux<Request> findByTenantIdAndRequestCategory(Long tenantId, String requestCategory);
    Flux<Request> findByTenantIdAndRequestCategoryAndStatus(Long tenantId, String requestCategory, String status);
    Flux<Request> findByTenantIdAndSacramentId(Long tenantId, UUID sacramentId);
}
