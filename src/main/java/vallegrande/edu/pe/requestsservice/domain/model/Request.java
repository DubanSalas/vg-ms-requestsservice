package vallegrande.edu.pe.requestsservice.domain.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Request {

    private Long id;
    private Long tenantId;
    private Long peopleId;
    private String massId;
    private UUID sacramentId;
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
