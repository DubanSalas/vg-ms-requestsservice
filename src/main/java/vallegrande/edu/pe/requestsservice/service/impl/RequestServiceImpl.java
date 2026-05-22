package vallegrande.edu.pe.requestsservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.client.SacramentClient;
import vallegrande.edu.pe.requestsservice.model.Request;
import vallegrande.edu.pe.requestsservice.repository.RequestRepository;
import vallegrande.edu.pe.requestsservice.service.RequestService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final SacramentClient sacramentClient;

    /** Enriquece la solicitud con los datos del sacramento desde vg-ms-sacramentservice */
    private Mono<Request> enrich(Request r) {
        if (r.getSacramentId() == null) return Mono.just(r);
        return sacramentClient.findById(r.getSacramentId())
                .doOnNext(r::setSacrament)
                .thenReturn(r);
    }

    @Override
    public Flux<Request> findAll() {
        return repository.findAll().flatMap(this::enrich);
    }

    @Override
    public Flux<Request> findByStatus(String status) {
        return repository.findByStatus(status).flatMap(this::enrich);
    }

    @Override
    public Flux<Request> findByTenantId(Long tenantId) {
        return repository.findByTenantId(tenantId).flatMap(this::enrich);
    }

    @Override
    public Flux<Request> findByTenantIdAndStatus(Long tenantId, String status) {
        return repository.findByTenantIdAndStatus(tenantId, status).flatMap(this::enrich);
    }

    @Override
    public Flux<Request> findByTenantIdAndCategory(Long tenantId, String category) {
        return repository.findByTenantIdAndRequestCategory(tenantId, category).flatMap(this::enrich);
    }

    @Override
    public Flux<Request> findByTenantIdAndCategoryAndStatus(Long tenantId, String category, String status) {
        return repository.findByTenantIdAndRequestCategoryAndStatus(tenantId, category, status).flatMap(this::enrich);
    }

    @Override
    public Flux<Request> findByTenantIdAndSacramentId(Long tenantId, UUID sacramentId) {
        return repository.findByTenantIdAndSacramentId(tenantId, sacramentId).flatMap(this::enrich);
    }

    @Override
    public Mono<Request> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Request not found: " + id)))
                .flatMap(this::enrich);
    }

    @Override
    public Mono<Request> save(Request request) {
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        if (request.getStatus() == null)         request.setStatus("PENDIENTE");
        if (request.getPriority() == null)        request.setPriority("MEDIA");
        if (request.getRequestDate() == null)     request.setRequestDate(LocalDateTime.now());
        if (request.getRequestCategory() == null) request.setRequestCategory("MISA");
        return repository.save(request).flatMap(this::enrich);
    }

    @Override
    public Mono<Request> update(Long id, Request request) {
        return findById(id).flatMap(existing -> {
            existing.setTenantId(request.getTenantId());
            existing.setPeopleId(request.getPeopleId());
            existing.setRequestCategory(request.getRequestCategory());
            existing.setSacramentId(request.getSacramentId());
            existing.setDescription(request.getDescription());
            existing.setDocumentUrl(request.getDocumentUrl());
            existing.setPriority(request.getPriority());
            if (request.getRequestDate() != null) existing.setRequestDate(request.getRequestDate());
            existing.setStatus(request.getStatus());
            existing.setUpdatedAt(LocalDateTime.now());
            return repository.save(existing).flatMap(this::enrich);
        });
    }

    @Override
    public Mono<Request> changeStatus(Long id, String status) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Request not found: " + id)))
                .flatMap(existing -> {
                    existing.setStatus(status);
                    existing.setUpdatedAt(LocalDateTime.now());
                    if (status.equals("APROBADO") || status.equals("RECHAZADO")) {
                        existing.setResolvedDate(LocalDateTime.now());
                    }
                    return repository.save(existing).flatMap(this::enrich);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Request not found: " + id)))
                .flatMap(existing -> {
                    existing.setStatus("ELIMINADO");
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing).then();
                });
    }
}
