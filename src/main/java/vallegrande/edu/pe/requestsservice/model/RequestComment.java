package vallegrande.edu.pe.requestsservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Table("request_comments")
public class RequestComment {

    @Id
    private Long id;

    @Column("request_id")
    private Long requestId;

    private String comment;

    @Column("created_at")
    private LocalDateTime createdAt;
}
