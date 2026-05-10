package vallegrande.edu.pe.requestsservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.requestsservice.model.Request;

public interface RequestRepository extends ReactiveCrudRepository<Request, Long> {
    Flux<Request> findByStatus(String status);
    Flux<Request> findByTenantId(Long tenantId);
    Flux<Request> findByTenantIdAndStatus(Long tenantId, String status);
}
