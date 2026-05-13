package vallegrande.edu.pe.requestsservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.requestsservice.model.RequestType;

public interface RequestTypeRepository extends ReactiveCrudRepository<RequestType, Long> {
    Flux<RequestType> findByTenantId(Long tenantId);
}
