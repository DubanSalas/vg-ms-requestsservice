package vallegrande.edu.pe.requestsservice.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.edu.pe.requestsservice.domain.exception.RequestNotFoundException;
import vallegrande.edu.pe.requestsservice.domain.model.Request;
import vallegrande.edu.pe.requestsservice.domain.port.out.RequestRepositoryPort;
import vallegrande.edu.pe.requestsservice.infrastructure.config.SacramentClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas parametrizadas del RequestService.
 *
 * Una prueba parametrizada reutiliza la misma lógica de verificación
 * ejecutándola con distintos conjuntos de datos. Esto evita duplicar
 * código y garantiza que el comportamiento sea consistente ante múltiples
 * escenarios: válidos, inválidos y de límite.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RequestService — Pruebas Parametrizadas")
class RequestServiceParameterizedTest {

    @Mock
    private RequestRepositoryPort repositoryPort;

    @Mock
    private SacramentClient sacramentClient;

    @InjectMocks
    private RequestService service;

    // ── Parametrizada 1: changeStatus con distintos estados válidos ────────────
    /**
     * Verifica que changeStatus actualice correctamente el estado de una solicitud
     * para todos los estados válidos del sistema: APROBADO, RECHAZADO, PENDIENTE,
     * EN_REVISION y CANCELADO.
     * Los estados APROBADO y RECHAZADO también deben registrar resolvedDate.
     */
    @ParameterizedTest(name = "changeStatus → estado={0}, esperaResolvedDate={1}")
    @DisplayName("P-01: changeStatus acepta todos los estados válidos del sistema")
    @CsvSource({
            "APROBADO,  true",
            "RECHAZADO, true",
            "PENDIENTE, false",
            "EN_REVISION, false",
            "CANCELADO, false"
    })
    void changeStatus_aceptaTodosLosEstadosValidos(String nuevoEstado, boolean esperaResolvedDate) {
        Request existing = new Request();
        existing.setId(1L);
        existing.setMassId("MISA");
        existing.setStatus("PENDIENTE");
        existing.setPriority("MEDIA");
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        Request updated = new Request();
        updated.setId(1L);
        updated.setMassId("MISA");
        updated.setStatus(nuevoEstado);
        updated.setPriority("MEDIA");
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(LocalDateTime.now());
        if (esperaResolvedDate) updated.setResolvedDate(LocalDateTime.now());

        when(repositoryPort.findById(1L)).thenReturn(Mono.just(existing));
        when(repositoryPort.save(any(Request.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(service.changeStatus(1L, nuevoEstado))
                .assertNext(r -> {
                    assertThat(r.getStatus()).isEqualTo(nuevoEstado);
                    if (esperaResolvedDate) {
                        assertThat(r.getResolvedDate()).isNotNull();
                    }
                })
                .verifyComplete();
    }

    // ── Parametrizada 2: changeStatus con ID inexistente siempre lanza excepción ──
    /**
     * Verifica que changeStatus lanza RequestNotFoundException para cualquier
     * estado cuando el ID no existe. El comportamiento debe ser consistente
     * independientemente del estado solicitado.
     */
    @ParameterizedTest(name = "changeStatus ID inexistente → estado={0}")
    @DisplayName("P-02: changeStatus lanza excepción para cualquier estado si el ID no existe")
    @CsvSource({
            "APROBADO",
            "RECHAZADO",
            "CANCELADO"
    })
    void changeStatus_lanzaExcepcionSiIdNoExiste(String estado) {
        when(repositoryPort.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(service.changeStatus(999L, estado))
                .expectErrorMatches(ex -> ex instanceof RequestNotFoundException)
                .verify();
    }

    // ── Parametrizada 3: changePriority con distintas prioridades válidas ──────
    /**
     * Verifica que changePriority actualice correctamente la prioridad para
     * todos los niveles válidos: BAJA, MEDIA y ALTA.
     */
    @ParameterizedTest(name = "changePriority → prioridad={0}")
    @DisplayName("P-03: changePriority acepta todos los niveles de prioridad válidos")
    @CsvSource({
            "BAJA",
            "MEDIA",
            "ALTA"
    })
    void changePriority_aceptaTodosLosNiveles(String prioridad) {
        Request existing = new Request();
        existing.setId(2L);
        existing.setMassId("MISA");
        existing.setStatus("PENDIENTE");
        existing.setPriority("MEDIA");
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        Request updated = new Request();
        updated.setId(2L);
        updated.setMassId("MISA");
        updated.setStatus("PENDIENTE");
        updated.setPriority(prioridad);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(LocalDateTime.now());

        when(repositoryPort.findById(2L)).thenReturn(Mono.just(existing));
        when(repositoryPort.save(any(Request.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(service.changePriority(2L, prioridad))
                .assertNext(r -> assertThat(r.getPriority()).isEqualTo(prioridad))
                .verifyComplete();
    }
}
