package vallegrande.edu.pe.requestsservice.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.domain.model.Request;
import vallegrande.edu.pe.requestsservice.domain.port.out.RequestRepositoryPort;

import java.util.UUID;

/**
 * Adaptador de salida — implementa el puerto de repositorio del dominio
 * usando el repositorio R2DBC.
 */
@Component
@RequiredArgsConstructor
public class RequestPersistenceAdapter implements RequestRepositoryPort {

    private final RequestR2dbcRepo repo;

    // ── Mapeo Entity ↔ Domain ─────────────────────────────────────────────────

    private Request toDomain(RequestEntity e) {
        Request r = new Request();
        r.setId(e.getId());
        r.setTenantId(e.getTenantId());
        r.setPeopleId(e.getPeopleId());
        r.setMassId(e.getMassId());
        r.setSacramentId(e.getSacramentId());
        r.setDescription(e.getDescription());
        r.setDocumentUrl(e.getDocumentUrl());
        r.setPriority(e.getPriority());
        r.setRequestDate(e.getRequestDate());
        r.setResolvedDate(e.getResolvedDate());
        r.setStatus(e.getStatus());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }

    private RequestEntity toEntity(Request r) {
        RequestEntity e = new RequestEntity();
        e.setId(r.getId());
        e.setTenantId(r.getTenantId());
        e.setPeopleId(r.getPeopleId());
        e.setMassId(r.getMassId());
        e.setSacramentId(r.getSacramentId());
        e.setDescription(r.getDescription());
        e.setDocumentUrl(r.getDocumentUrl());
        e.setPriority(r.getPriority());
        e.setRequestDate(r.getRequestDate());
        e.setResolvedDate(r.getResolvedDate());
        e.setStatus(r.getStatus());
        e.setCreatedAt(r.getCreatedAt());
        e.setUpdatedAt(r.getUpdatedAt());
        return e;
    }

    // ── Puerto ────────────────────────────────────────────────────────────────

    @Override
    public Flux<Request> findAll() {
        return repo.findAll().map(this::toDomain);
    }

    @Override
    public Flux<Request> findByStatus(String status) {
        return repo.findByStatus(status).map(this::toDomain);
    }

    @Override
    public Flux<Request> findByTenantId(Long tenantId) {
        return repo.findByTenantId(tenantId).map(this::toDomain);
    }

    @Override
    public Flux<Request> findByTenantIdAndStatus(Long tenantId, String status) {
        return repo.findByTenantIdAndStatus(tenantId, status).map(this::toDomain);
    }

    @Override
    public Flux<Request> findByTenantIdAndSacramentId(Long tenantId, UUID sacramentId) {
        return repo.findByTenantIdAndSacramentId(tenantId, sacramentId).map(this::toDomain);
    }

    @Override
    public Flux<Request> findByTenantIdAndMassId(Long tenantId, String massId) {
        return repo.findByTenantIdAndMassId(tenantId, massId).map(this::toDomain);
    }

    @Override
    public Mono<Request> findById(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public Mono<Request> save(Request request) {
        return repo.save(toEntity(request)).map(this::toDomain);
    }
}
