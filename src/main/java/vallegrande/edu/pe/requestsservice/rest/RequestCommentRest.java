package vallegrande.edu.pe.requestsservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.RequestComment;
import vallegrande.edu.pe.requestsservice.service.RequestCommentService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/request-comments")
@RequiredArgsConstructor
public class RequestCommentRest {

    private final RequestCommentService service;

    @GetMapping("/{requestId}")
    public Flux<RequestComment> findByRequestId(@PathVariable Long requestId) {
        return service.findByRequestId(requestId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RequestComment> save(@RequestBody RequestComment comment) {
        return service.save(comment);
    }
}
