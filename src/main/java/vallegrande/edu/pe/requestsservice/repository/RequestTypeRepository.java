package vallegrande.edu.pe.requestsservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import vallegrande.edu.pe.requestsservice.model.RequestType;

public interface RequestTypeRepository extends ReactiveCrudRepository<RequestType, Long> {
}
