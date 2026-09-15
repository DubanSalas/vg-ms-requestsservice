package vallegrande.edu.pe.requestsservice.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.edu.pe.requestsservice.domain.exception.RequestNotFoundException;
import vallegrande.edu.pe.requestsservice.domain.model.Request;
import vallegrande.edu.pe.requestsservice.domain.port.out.RequestRepositoryPort;
import vallegrande.edu.pe.requestsservice.infrastructure.config.SacramentClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del RequestService (arquitectura hexagonal).
 *
 * Casos cubiertos:
 *  TC-01 (positivo)   — findAll retorna lista de solicitudes con datos correctos
 *  TC-02 (positivo)   — save persiste una solicitud nueva con valores por defecto
 *  TC-03 (negativo)   — save lanza excepción cuando massId y sacramentId son nulos
 *  TC-04 (excepción)  — changeStatus lanza excepción cuando la solicitud no existe
 *  TC-05 (positivo)   — changeStatus actualiza el estado a APROBADO correctamente
 *  TC-06 (positivo)   — findByStatus retorna solo solicitudes PENDIENTE
 *  TC-07 (negativo)   — findById lanza excepción cuando el ID no existe
 *  TC-08 (positivo)   — delete marca la solicitud como ELIMINADO sin borrarla físicamente
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RequestService (Hexagonal) — Pruebas Unitarias")
class RequestServiceTest {

    @Mock
    private RequestRepositoryPort repositoryPort;

    @Mock
    private SacramentClient sacramentClient;

    @InjectMocks
    private RequestService service;

    // ── fixtures ──────────────────────────────────────────────────────────────

    private Request buildRequest(Long id, String status, String priority) {
        Request r = new Request();
        r.setId(id);
        r.setTenantId(9L);
        r.setPeopleId(12L);
        r.setMassId("MISA");
        r.setDescription("Solicitud de misa dominical");
        r.setStatus(status);
        r.setPriority(priority);
        r.setRequestDate(LocalDateTime.now().plusDays(5));
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());
        return r;
    }

    // ── TC-01 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-01 [+] findAll debe retornar todas las solicitudes existentes")
    void findAll_debeRetornarTodasLasSolicitudes() {
        Request r1 = buildRequest(1L, "PENDIENTE", "MEDIA");
        Request r2 = buildRequest(2L, "APROBADO",  "ALTA");
        when(repositoryPort.findAll()).thenReturn(Flux.just(r1, r2));

        StepVerifier.create(service.findAll())
                .assertNext(r -> {
                    assertThat(r.getId()).isEqualTo(1L);
                    assertThat(r.getStatus()).isEqualTo("PENDIENTE");
                })
                .assertNext(r -> {
                    assertThat(r.getId()).isEqualTo(2L);
                    assertThat(r.getStatus()).isEqualTo("APROBADO");
                })
                .verifyComplete();

        verify(repositoryPort, times(1)).findAll();
    }

    // ── TC-02 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-02 [+] save debe persistir solicitud con estado PENDIENTE y prioridad MEDIA por defecto")
    void save_debeAsignarValoresPorDefecto() {
        Request input = new Request();
        input.setTenantId(9L);
        input.setPeopleId(12L);
        input.setMassId("MISA");
        input.setDescription("Solicitud de bautismo");

        Request saved = buildRequest(10L, "PENDIENTE", "MEDIA");
        saved.setDescription("Solicitud de bautismo");

        when(repositoryPort.save(any(Request.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(service.save(input))
                .assertNext(r -> {
                    assertThat(r.getStatus()).isEqualTo("PENDIENTE");
                    assertThat(r.getPriority()).isEqualTo("MEDIA");
                    assertThat(r.getId()).isEqualTo(10L);
                })
                .verifyComplete();

        verify(repositoryPort, times(1)).save(any(Request.class));
    }

    // ── TC-03 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-03 [-] save debe lanzar IllegalArgumentException cuando massId y sacramentId son nulos")
    void save_debeLanzarExcepcionSiNoHayTipoDeSolicitud() {
        Request invalid = new Request();
        invalid.setTenantId(9L);
        invalid.setPeopleId(12L);
        invalid.setDescription("Sin tipo definido");

        StepVerifier.create(service.save(invalid))
                .expectErrorMatches(ex ->
                    ex instanceof IllegalArgumentException &&
                    ex.getMessage().contains("mass_id or sacrament_id is required"))
                .verify();

        verify(repositoryPort, never()).save(any());
    }

    // ── TC-04 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-04 [excepción] changeStatus debe lanzar RequestNotFoundException cuando la solicitud no existe")
    void changeStatus_debeLanzarExcepcionSiNoExiste() {
        when(repositoryPort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(service.changeStatus(99L, "APROBADO"))
                .expectErrorMatches(ex -> ex instanceof RequestNotFoundException)
                .verify();

        verify(repositoryPort, times(1)).findById(99L);
        verify(repositoryPort, never()).save(any());
    }

    // ── TC-05 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-05 [+] changeStatus debe actualizar estado a APROBADO y registrar resolvedDate")
    void changeStatus_debeActualizarEstadoYFechaResolucion() {
        Request existing = buildRequest(5L, "PENDIENTE", "MEDIA");
        Request updated  = buildRequest(5L, "APROBADO",  "MEDIA");
        updated.setResolvedDate(LocalDateTime.now());

        when(repositoryPort.findById(5L)).thenReturn(Mono.just(existing));
        when(repositoryPort.save(any(Request.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(service.changeStatus(5L, "APROBADO"))
                .assertNext(r -> {
                    assertThat(r.getStatus()).isEqualTo("APROBADO");
                    assertThat(r.getResolvedDate()).isNotNull();
                })
                .verifyComplete();

        verify(repositoryPort, times(1)).findById(5L);
        verify(repositoryPort, times(1)).save(any(Request.class));
    }

    // ── TC-06 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-06 [+] findByStatus PENDIENTE debe retornar solo solicitudes pendientes")
    void findByStatus_debeRetornarSoloPendientes() {
        Request r1 = buildRequest(1L, "PENDIENTE", "MEDIA");
        Request r2 = buildRequest(2L, "PENDIENTE", "ALTA");
        when(repositoryPort.findByStatus("PENDIENTE")).thenReturn(Flux.just(r1, r2));

        StepVerifier.create(service.findByStatus("PENDIENTE"))
                .assertNext(r -> assertThat(r.getStatus()).isEqualTo("PENDIENTE"))
                .assertNext(r -> assertThat(r.getStatus()).isEqualTo("PENDIENTE"))
                .verifyComplete();

        verify(repositoryPort, times(1)).findByStatus("PENDIENTE");
    }

    // ── TC-07 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-07 [-] findById debe lanzar RequestNotFoundException cuando el ID no existe")
    void findById_debeLanzarExcepcionSiIdNoExiste() {
        when(repositoryPort.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(service.findById(999L))
                .expectErrorMatches(ex -> ex instanceof RequestNotFoundException)
                .verify();

        verify(repositoryPort, times(1)).findById(999L);
    }

    // ── TC-08 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-08 [+] delete debe marcar la solicitud como ELIMINADO sin borrarla físicamente")
    void delete_debeCambiarEstadoAEliminado() {
        Request existing = buildRequest(3L, "PENDIENTE", "MEDIA");
        Request deleted  = buildRequest(3L, "ELIMINADO", "MEDIA");

        when(repositoryPort.findById(3L)).thenReturn(Mono.just(existing));
        when(repositoryPort.save(any(Request.class))).thenReturn(Mono.just(deleted));

        StepVerifier.create(service.delete(3L))
                .verifyComplete();

        verify(repositoryPort, times(1)).findById(3L);
        verify(repositoryPort, times(1)).save(argThat(r -> "ELIMINADO".equals(r.getStatus())));
    }
}
