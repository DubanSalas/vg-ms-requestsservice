package vallegrande.edu.pe.requestsservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.client.MassClient;
import vallegrande.edu.pe.requestsservice.client.SacramentClient;
import vallegrande.edu.pe.requestsservice.model.Request;
import vallegrande.edu.pe.requestsservice.service.RequestService;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/requests")
@RequiredArgsConstructor
public class RequestRest {

    private final RequestService  service;
    private final SacramentClient sacramentClient;
    private final MassClient      massClient;

    // ── Proxy a microservicios de compañeros ─────────────────────────────────

    /** GET /v1/api/requests/types/sacraments?tenantId=1 */
    @GetMapping("/types/sacraments")
    public Flux<?> getSacraments(@RequestParam(required = false) Integer tenantId) {
        return tenantId != null
                ? sacramentClient.findByTenant(tenantId)
                : sacramentClient.findAll();
    }

    /** GET /v1/api/requests/types/masses?tenantId=1 */
    @GetMapping("/types/masses")
    public Flux<?> getMasses(@RequestParam(required = false) Long tenantId) {
        return tenantId != null
                ? massClient.findByTenant(tenantId)
                : massClient.findActive();
    }

    // ── Solicitudes ──────────────────────────────────────────────────────────

    @GetMapping
    public Flux<Request> findAll(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sacramentId,
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

    @GetMapping("/{id}")
    public Mono<Request> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Request> save(@RequestBody Request request) {
        return service.save(request);
    }

    @PutMapping("/{id}")
    public Mono<Request> update(@PathVariable Long id, @RequestBody Request request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public Mono<Request> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.changeStatus(id, body.get("status"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
