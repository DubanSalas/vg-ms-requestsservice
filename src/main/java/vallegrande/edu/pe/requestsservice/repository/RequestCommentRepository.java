package vallegrande.edu.pe.requestsservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.requestsservice.model.RequestComment;

public interface RequestCommentRepository extends ReactiveCrudRepository<RequestComment, Long> {
    Flux<RequestComment> findByRequestId(Long requestId);
}
