package vallegrande.edu.pe.requestsservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("request_types")
public class RequestType {

    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    private String name;
    private String description;
    private Boolean active;
}
