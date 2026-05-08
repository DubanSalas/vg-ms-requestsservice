package vallegrande.edu.pe.requestsservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("request_types")
public class RequestType {

    @Id
    private Long id;
    private String name;
    private String description;
}
