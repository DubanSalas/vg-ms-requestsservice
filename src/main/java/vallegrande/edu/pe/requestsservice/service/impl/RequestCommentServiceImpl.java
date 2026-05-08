package vallegrande.edu.pe.requestsservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.RequestComment;
import vallegrande.edu.pe.requestsservice.repository.RequestCommentRepository;
import vallegrande.edu.pe.requestsservice.service.RequestCommentService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestCommentServiceImpl implements RequestCommentService {

    private final RequestCommentRepository repository;

    @Override
    public Flux<RequestComment> findByRequestId(Long requestId) {
        return repository.findByRequestId(requestId);
    }

    @Override
    public Mono<RequestComment> save(RequestComment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return repository.save(comment);
    }
}
