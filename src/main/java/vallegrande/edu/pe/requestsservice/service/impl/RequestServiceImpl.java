package vallegrande.edu.pe.requestsservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.Request;
import vallegrande.edu.pe.requestsservice.repository.RequestRepository;
import vallegrande.edu.pe.requestsservice.repository.RequestTypeRepository;
import vallegrande.edu.pe.requestsservice.service.RequestService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final RequestTypeRepository requestTypeRepository;

    private Mono<Request> enrichWithType(Request r) {
        if (r.getRequestTypeId() == null) return Mono.just(r);
        return requestTypeRepository.findById(r.getRequestTypeId())
                .doOnNext(r::setRequestType)
                .thenReturn(r);
    }

    @Override
    public Flux<Request> findAll() {
        return repository.findAll().flatMap(this::enrichWithType);
    }

    @Override
    public Flux<Request> findByStatus(String status) {
        return repository.findByStatus(status).flatMap(this::enrichWithType);
    }

    @Override
    public Flux<Request> findByTenantId(Long tenantId) {
        return repository.findByTenantId(tenantId).flatMap(this::enrichWithType);
    }

    @Override
    public Flux<Request> findByTenantIdAndStatus(Long tenantId, String status) {
        return repository.findByTenantIdAndStatus(tenantId, status).flatMap(this::enrichWithType);
    }

    @Override
    public Mono<Request> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Request not found: " + id)))
                .flatMap(this::enrichWithType);
    }

    @Override
    public Mono<Request> save(Request request) {
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        if (request.getStatus() == null) request.setStatus("PENDIENTE");
        if (request.getPriority() == null) request.setPriority("MEDIA");
        if (request.getRequestDate() == null) request.setRequestDate(LocalDateTime.now());
        // Si viene requestType con id, extraer el requestTypeId
        if (request.getRequestTypeId() == null && request.getRequestType() != null && request.getRequestType().getId() != null) {
            request.setRequestTypeId(request.getRequestType().getId());
        }
        return repository.save(request).flatMap(this::enrichWithType);
    }

    @Override
    public Mono<Request> update(Long id, Request request) {
        return findById(id).flatMap(existing -> {
            existing.setTenantId(request.getTenantId());
            existing.setPeopleId(request.getPeopleId());
            // Extraer requestTypeId del objeto requestType si viene
            if (request.getRequestTypeId() != null) {
                existing.setRequestTypeId(request.getRequestTypeId());
            } else if (request.getRequestType() != null && request.getRequestType().getId() != null) {
                existing.setRequestTypeId(request.getRequestType().getId());
            }
            existing.setDescription(request.getDescription());
            existing.setDocumentUrl(request.getDocumentUrl());
            existing.setPriority(request.getPriority());
            if (request.getRequestDate() != null) existing.setRequestDate(request.getRequestDate());
            existing.setStatus(request.getStatus());
            existing.setUpdatedAt(LocalDateTime.now());
            return repository.save(existing).flatMap(this::enrichWithType);
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
                    return repository.save(existing).flatMap(this::enrichWithType);
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
