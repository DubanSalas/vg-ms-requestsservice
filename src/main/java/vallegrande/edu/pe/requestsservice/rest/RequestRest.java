package vallegrande.edu.pe.requestsservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.requestsservice.model.Request;
import vallegrande.edu.pe.requestsservice.service.RequestService;

import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/requests")
@RequiredArgsConstructor
public class RequestRest {

    private final RequestService service;

    /**
     * GET /v1/api/requests
     * Parámetros opcionales:
     *   tenantId, status, category (MISA|ACTA_SACRAMENTAL|SACRAMENTO), sacramentId (UUID)
     */
    @GetMapping
    public Flux<Request> findAll(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UUID sacramentId) {

        if (tenantId != null && category != null && status != null)
            return service.findByTenantIdAndCategoryAndStatus(tenantId, category, status);
        if (tenantId != null && category != null)
            return service.findByTenantIdAndCategory(tenantId, category);
        if (tenantId != null && sacramentId != null)
            return service.findByTenantIdAndSacramentId(tenantId, sacramentId);
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
