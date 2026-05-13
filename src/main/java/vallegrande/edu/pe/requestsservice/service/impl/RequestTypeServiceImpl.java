package vallegrande.edu.pe.requestsservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.RequestType;
import vallegrande.edu.pe.requestsservice.repository.RequestTypeRepository;
import vallegrande.edu.pe.requestsservice.service.RequestTypeService;

@Service
@RequiredArgsConstructor
public class RequestTypeServiceImpl implements RequestTypeService {

    private final RequestTypeRepository repository;

    @Override
    public Flux<RequestType> findAll() {
        return repository.findAll();
    }

    @Override
    public Flux<RequestType> findByTenantId(Long tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @Override
    public Mono<RequestType> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("RequestType not found: " + id)));
    }

    @Override
    public Mono<RequestType> save(RequestType requestType) {
        return repository.save(requestType);
    }

    @Override
    public Mono<RequestType> update(Long id, RequestType requestType) {
        return findById(id).flatMap(existing -> {
            existing.setName(requestType.getName());
            existing.setDescription(requestType.getDescription());
            if (requestType.getActive() != null) existing.setActive(requestType.getActive());
            return repository.save(existing);
        });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }
}
