package vallegrande.edu.pe.requestsservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.RequestType;
import vallegrande.edu.pe.requestsservice.service.RequestTypeService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/request-types")
@RequiredArgsConstructor
public class RequestTypeRest {

    private final RequestTypeService service;

    @GetMapping
    public Flux<RequestType> findAll(@RequestParam(required = false) Long tenantId) {
        if (tenantId != null) return service.findByTenantId(tenantId);
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<RequestType> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RequestType> save(@RequestBody RequestType requestType) {
        return service.save(requestType);
    }

    @PutMapping("/{id}")
    public Mono<RequestType> update(@PathVariable Long id, @RequestBody RequestType requestType) {
        return service.update(id, requestType);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
