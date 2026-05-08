package vallegrande.edu.pe.requestsservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Table("requests")
public class Request {

    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    @Column("people_id")
    private Long peopleId;

    @Column("request_type_id")
    private Long requestTypeId;

    @Transient
    private RequestType requestType;

    private String description;

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
