package vallegrande.edu.pe.requestsservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.RequestType;

public interface RequestTypeService {
    Flux<RequestType> findAll();
    Flux<RequestType> findByTenantId(Long tenantId);
    Mono<RequestType> findById(Long id);
    Mono<RequestType> save(RequestType requestType);
    Mono<RequestType> update(Long id, RequestType requestType);
    Mono<Void> delete(Long id);
}
