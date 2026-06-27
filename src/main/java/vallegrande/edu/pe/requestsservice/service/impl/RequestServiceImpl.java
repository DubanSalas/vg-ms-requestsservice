package vallegrande.edu.pe.requestsservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.client.MassClient;
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
    private final SacramentClient   sacramentClient;
    private final MassClient        massClient;

    private Mono<Request> enrich(Request r) {
        if (r.getMassId() != null) {
            return massClient.findById(r.getMassId())
                    .doOnNext(r::setMass)
                    .thenReturn(r);
        }
        if (r.getSacramentId() != null) {
            return sacramentClient.findById(r.getSacramentId())
                    .doOnNext(r::setSacrament)
                    .thenReturn(r);
        }
        return Mono.just(r);
    }

    @Override @Transactional(readOnly = true)
    public Flux<Request> findAll() {
        return repository.findAll().flatMap(this::enrich);
    }

    @Override @Transactional(readOnly = true)
    public Flux<Request> findByStatus(String status) {
        return repository.findByStatus(status).flatMap(this::enrich);
    }

    @Override @Transactional(readOnly = true)
    public Flux<Request> findByTenantId(Long tenantId) {
        return repository.findByTenantId(tenantId).flatMap(this::enrich);
    }

    @Override @Transactional(readOnly = true)
    public Flux<Request> findByTenantIdAndStatus(Long tenantId, String status) {
        return repository.findByTenantIdAndStatus(tenantId, status).flatMap(this::enrich);
    }

    @Override @Transactional(readOnly = true)
    public Flux<Request> findByTenantIdAndSacramentId(Long tenantId, UUID sacramentId) {
        return repository.findByTenantIdAndSacramentId(tenantId, sacramentId).flatMap(this::enrich);
    }

    @Override @Transactional(readOnly = true)
    public Flux<Request> findByTenantIdAndMassId(Long tenantId, String massId) {
        return repository.findByTenantIdAndMassId(tenantId, massId).flatMap(this::enrich);
    }

    @Override @Transactional(readOnly = true)
    public Mono<Request> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Request not found: " + id)))
                .flatMap(this::enrich);
    }

    @Override @Transactional
    public Mono<Request> save(Request request) {
        if (request.getMassId() == null && request.getSacramentId() == null)
            return Mono.error(new IllegalArgumentException("mass_id or sacrament_id is required"));
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        if (request.getStatus() == null)      request.setStatus("PENDIENTE");
        if (request.getPriority() == null)    request.setPriority("MEDIA");
        if (request.getRequestDate() == null) request.setRequestDate(LocalDateTime.now());
        return repository.save(request).flatMap(this::enrich);
    }

    @Override @Transactional
    public Mono<Request> update(Long id, Request request) {
        return findById(id).flatMap(existing -> {
            existing.setTenantId(request.getTenantId());
            existing.setPeopleId(request.getPeopleId());
            existing.setMassId(request.getMassId());
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

    @Override @Transactional
    public Mono<Request> changeStatus(Long id, String status) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Request not found: " + id)))
                .flatMap(existing -> {
                    existing.setStatus(status);
                    existing.setUpdatedAt(LocalDateTime.now());
                    if ("APROBADO".equals(status) || "RECHAZADO".equals(status))
                        existing.setResolvedDate(LocalDateTime.now());
                    return repository.save(existing).flatMap(this::enrich);
                });
    }

    @Override @Transactional
    public Mono<Request> changePriority(Long id, String priority) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Request not found: " + id)))
                .flatMap(existing -> {
                    existing.setPriority(priority);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing).flatMap(this::enrich);
                });
    }

    @Override @Transactional
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
