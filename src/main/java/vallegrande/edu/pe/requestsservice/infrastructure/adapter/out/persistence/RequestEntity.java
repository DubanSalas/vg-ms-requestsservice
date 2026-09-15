package vallegrande.edu.pe.requestsservice.infrastructure.adapter.out.persistence;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de persistencia R2DBC — aquí sí van las anotaciones de BD.
 */
@Data
@Table("requests")
public class RequestEntity {

    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    @Column("people_id")
    private Long peopleId;

    @Column("mass_id")
    private String massId;

    @Column("sacrament_id")
    private UUID sacramentId;

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
}
