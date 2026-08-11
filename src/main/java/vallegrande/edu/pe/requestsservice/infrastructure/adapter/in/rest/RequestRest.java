package vallegrande.edu.pe.requestsservice.infrastructure.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.domain.model.Request;
import vallegrande.edu.pe.requestsservice.domain.port.in.RequestUseCase;
import vallegrande.edu.pe.requestsservice.infrastructure.config.SacramentClient;

import java.util.Map;
import java.util.UUID;

/**
 * Adaptador de entrada — convierte peticiones HTTP al caso de uso del dominio.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/requests")
@RequiredArgsConstructor
public class RequestRest {

    private final RequestUseCase  useCase;
    private final SacramentClient sacramentClient;

    // ── Proxy a sacramentos ──────────────────────────────────────────────────

    /** GET /v1/api/requests/types/sacraments?tenantId=1 */
    @GetMapping("/types/sacraments")
    public Flux<?> getSacraments(@RequestParam(required = false) Integer tenantId) {
        return tenantId != null
                ? sacramentClient.findByTenant(tenantId)
                : sacramentClient.findAll();
    }

    // ── Solicitudes ──────────────────────────────────────────────────────────

    @GetMapping
    public Flux<Request> findAll(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID sacramentId,
            @RequestParam(required = false) String massId) {

        if (tenantId != null && sacramentId != null)
            return useCase.findByTenantIdAndSacramentId(tenantId, sacramentId);
        if (tenantId != null && massId != null)
            return useCase.findByTenantIdAndMassId(tenantId, massId);
        if (tenantId != null && status != null)
            return useCase.findByTenantIdAndStatus(tenantId, status);
        if (tenantId != null)
            return useCase.findByTenantId(tenantId);
        if (status != null)
            return useCase.findByStatus(status);
        return useCase.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Request> findById(@PathVariable Long id) {
        return useCase.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Request> save(@RequestBody Request request) {
        return useCase.save(request);
    }

    @PutMapping("/{id}")
    public Mono<Request> update(@PathVariable Long id, @RequestBody Request request) {
        return useCase.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public Mono<Request> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return useCase.changeStatus(id, body.get("status"));
    }

    @PatchMapping("/{id}/priority")
    public Mono<Request> changePriority(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return useCase.changePriority(id, body.get("priority"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return useCase.delete(id);
    }
}
