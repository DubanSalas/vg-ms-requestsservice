package vallegrande.edu.pe.requestsservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("requests")
public class Request {

    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    @Column("people_id")
    private Long peopleId;

    /** Texto que indica el tipo misa — almacena "MISA" cuando aplica */
    @Column("mass_id")
    private String massId;

    /** UUID del sacramento (vg-ms-sacramentservice) */
    @Column("sacrament_id")
    private UUID sacramentId;

    /** Datos del sacramento enriquecidos (no se persiste) */
    @Transient
    private SacramentInfo sacrament;

    private String description;

    @Column("document_url")
    private String documentUrl;

    private String priority;

    @Column("request_date")
    private LocalDateTime requestDate;

    @Column("resolved_date")
    private LocalDateTime resolvedDate;

    private String status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Data
    public static class SacramentInfo {
        private String id;
        private String name;
        private String description;
    }
}
