package vallegrande.edu.pe.requestsservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.edu.pe.requestsservice.client.SacramentClient;
import vallegrande.edu.pe.requestsservice.model.Request;
import vallegrande.edu.pe.requestsservice.repository.RequestRepository;
import vallegrande.edu.pe.requestsservice.service.impl.RequestServiceImpl;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del RequestServiceImpl.
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
@DisplayName("RequestServiceImpl — Pruebas Unitarias")
class RequestServiceImplTest {

    @Mock
    private RequestRepository repository;

    @Mock
    private SacramentClient sacramentClient;

    @InjectMocks
    private RequestServiceImpl service;

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
        // ARRANGE
        Request r1 = buildRequest(1L, "PENDIENTE", "MEDIA");
        Request r2 = buildRequest(2L, "APROBADO",  "ALTA");
        when(repository.findAll()).thenReturn(Flux.just(r1, r2));

        // ACT & ASSERT
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

        verify(repository, times(1)).findAll();
    }

    // ── TC-02 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-02 [+] save debe persistir solicitud con estado PENDIENTE y prioridad MEDIA por defecto")
    void save_debeAsignarValoresPorDefecto() {
        // ARRANGE — solicitud sin estado ni prioridad
        Request input = new Request();
        input.setTenantId(9L);
        input.setPeopleId(12L);
        input.setMassId("MISA");
        input.setDescription("Solicitud de bautismo");

        Request saved = buildRequest(10L, "PENDIENTE", "MEDIA");
        saved.setDescription("Solicitud de bautismo");

        when(repository.save(any(Request.class))).thenReturn(Mono.just(saved));

        // ACT & ASSERT
        StepVerifier.create(service.save(input))
                .assertNext(r -> {
                    assertThat(r.getStatus()).isEqualTo("PENDIENTE");
                    assertThat(r.getPriority()).isEqualTo("MEDIA");
                    assertThat(r.getId()).isEqualTo(10L);
                })
                .verifyComplete();

        verify(repository, times(1)).save(any(Request.class));
    }

    // ── TC-03 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-03 [-] save debe lanzar IllegalArgumentException cuando massId y sacramentId son nulos")
    void save_debeLanzarExcepcionSiNoHayTipoDeSolicitud() {
        // ARRANGE — solicitud inválida (sin massId ni sacramentId)
        Request invalid = new Request();
        invalid.setTenantId(9L);
        invalid.setPeopleId(12L);
        invalid.setDescription("Sin tipo definido");
        // massId = null, sacramentId = null → debe fallar

        // ACT & ASSERT
        StepVerifier.create(service.save(invalid))
                .expectErrorMatches(ex ->
                    ex instanceof IllegalArgumentException &&
                    ex.getMessage().contains("mass_id or sacrament_id is required"))
                .verify();

        verify(repository, never()).save(any());
    }

    // ── TC-04 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-04 [excepción] changeStatus debe lanzar RuntimeException cuando la solicitud no existe")
    void changeStatus_debeLanzarExcepcionSiNoExiste() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Mono.empty());

        // ACT & ASSERT
        StepVerifier.create(service.changeStatus(99L, "APROBADO"))
                .expectErrorMatches(ex ->
                    ex instanceof RuntimeException &&
                    ex.getMessage().contains("Request not found: 99"))
                .verify();

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any());
    }

    // ── TC-05 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-05 [+] changeStatus debe actualizar estado a APROBADO y registrar resolvedDate")
    void changeStatus_debeActualizarEstadoYFechaResolucion() {
        // ARRANGE
        Request existing = buildRequest(5L, "PENDIENTE", "MEDIA");
        Request updated  = buildRequest(5L, "APROBADO",  "MEDIA");
        updated.setResolvedDate(LocalDateTime.now());

        when(repository.findById(5L)).thenReturn(Mono.just(existing));
        when(repository.save(any(Request.class))).thenReturn(Mono.just(updated));

        // ACT & ASSERT
        StepVerifier.create(service.changeStatus(5L, "APROBADO"))
                .assertNext(r -> {
                    assertThat(r.getStatus()).isEqualTo("APROBADO");
                    assertThat(r.getResolvedDate()).isNotNull();
                })
                .verifyComplete();

        verify(repository, times(1)).findById(5L);
        verify(repository, times(1)).save(any(Request.class));
    }

    // ── TC-06 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-06 [+] findByStatus PENDIENTE debe retornar solo solicitudes pendientes")
    void findByStatus_debeRetornarSoloPendientes() {
        // ARRANGE
        Request r1 = buildRequest(1L, "PENDIENTE", "MEDIA");
        Request r2 = buildRequest(2L, "PENDIENTE", "ALTA");
        when(repository.findByStatus("PENDIENTE")).thenReturn(Flux.just(r1, r2));

        // ACT & ASSERT
        StepVerifier.create(service.findByStatus("PENDIENTE"))
                .assertNext(r -> assertThat(r.getStatus()).isEqualTo("PENDIENTE"))
                .assertNext(r -> assertThat(r.getStatus()).isEqualTo("PENDIENTE"))
                .verifyComplete();

        verify(repository, times(1)).findByStatus("PENDIENTE");
    }

    // ── TC-07 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-07 [-] findById debe lanzar RuntimeException cuando el ID no existe")
    void findById_debeLanzarExcepcionSiIdNoExiste() {
        // ARRANGE
        when(repository.findById(999L)).thenReturn(Mono.empty());

        // ACT & ASSERT
        StepVerifier.create(service.findById(999L))
                .expectErrorMatches(ex ->
                    ex instanceof RuntimeException &&
                    ex.getMessage().contains("Request not found: 999"))
                .verify();

        verify(repository, times(1)).findById(999L);
    }

    // ── TC-08 ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-08 [+] delete debe marcar la solicitud como ELIMINADO sin borrarla físicamente")
    void delete_debeCambiarEstadoAEliminado() {
        // ARRANGE
        Request existing = buildRequest(3L, "PENDIENTE", "MEDIA");
        Request deleted  = buildRequest(3L, "ELIMINADO", "MEDIA");

        when(repository.findById(3L)).thenReturn(Mono.just(existing));
        when(repository.save(any(Request.class))).thenReturn(Mono.just(deleted));

        // ACT & ASSERT
        StepVerifier.create(service.delete(3L))
                .verifyComplete();

        // Verificar que se guardó (soft delete) y NO se llamó deleteById
        verify(repository, times(1)).findById(3L);
        verify(repository, times(1)).save(argThat(r -> "ELIMINADO".equals(r.getStatus())));
        verify(repository, never()).deleteById(anyLong());
    }
}
