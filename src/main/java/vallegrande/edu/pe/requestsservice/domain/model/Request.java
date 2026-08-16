package vallegrande.edu.pe.requestsservice.domain.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * POJO puro del dominio — sin anotaciones de persistencia ni frameworks.
 */
@Data
public class Request {

    private Long id;
    private Long tenantId;
    private Long peopleId;

    /** "MISA" cuando la solicitud es de tipo misa */
    private String massId;

    /** UUID del sacramento cuando la solicitud es de tipo sacramento o acta */
    private UUID sacramentId;

    /** Datos enriquecidos del sacramento (no se persiste) */
    private SacramentInfo sacrament;

    private String description;
    private String documentUrl;
    private String priority;
    private LocalDateTime requestDate;
    private LocalDateTime resolvedDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class SacramentInfo {
        private String id;
        private String name;
        private String description;
    }
}
