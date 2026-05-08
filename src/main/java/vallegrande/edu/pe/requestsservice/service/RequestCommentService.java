package vallegrande.edu.pe.requestsservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.RequestComment;

public interface RequestCommentService {
    Flux<RequestComment> findByRequestId(Long requestId);
    Mono<RequestComment> save(RequestComment comment);
}
