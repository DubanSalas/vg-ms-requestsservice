package vallegrande.edu.pe.requestsservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.client.SacramentClient;
import vallegrande.edu.pe.requestsservice.model.Request;
import vallegrande.edu.pe.requestsservice.service.RequestService;

import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestRest {

    private final RequestService  service;
    private final SacramentClient sacramentClient;

    // ── Proxy a sacramentos (público) ────────────────────────────────────────

    /** GET /v1/api/requests/types/sacraments?tenantId=1 */
    @GetMapping("/types/sacraments")
    public Flux<?> getSacraments(@RequestParam(required = false) Integer tenantId) {
        return tenantId != null
                ? sacramentClient.findByTenant(tenantId)
                : sacramentClient.findAll();
    }

    // ── Solicitudes ──────────────────────────────────────────────────────────

    @PreAuthorize("hasAnyRole('SECRETARIO', 'PARROCO')")
    @GetMapping
    public Flux<Request> findAll(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID sacramentId,
            @RequestParam(required = false) String massId) {

        if (tenantId != null && sacramentId != null)
            return service.findByTenantIdAndSacramentId(tenantId, sacramentId);
        if (tenantId != null && massId != null)
            return service.findByTenantIdAndMassId(tenantId, massId);
        if (tenantId != null && status != null)
            return service.findByTenantIdAndStatus(tenantId, status);
        if (tenantId != null)
            return service.findByTenantId(tenantId);
        if (status != null)
            return service.findByStatus(status);
        return service.findAll();
    }

    @PreAuthorize("hasAnyRole('SECRETARIO', 'PARROCO')")
    @GetMapping("/{id}")
    public Mono<Request> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PreAuthorize("hasRole('SECRETARIO')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Request> save(@RequestBody Request request) {
        return service.save(request);
    }

    @PreAuthorize("hasRole('SECRETARIO')")
    @PutMapping("/{id}")
    public Mono<Request> update(@PathVariable Long id, @RequestBody Request request) {
        return service.update(id, request);
    }

    @PreAuthorize("hasRole('PARROCO')")
    @PatchMapping("/{id}/status")
    public Mono<Request> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.changeStatus(id, body.get("status"));
    }

    @PreAuthorize("hasRole('SECRETARIO')")
    @PatchMapping("/{id}/priority")
    public Mono<Request> changePriority(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.changePriority(id, body.get("priority"));
    }

    @PreAuthorize("hasRole('SECRETARIO')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
