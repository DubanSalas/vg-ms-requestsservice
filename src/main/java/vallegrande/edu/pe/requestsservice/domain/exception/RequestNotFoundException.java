package vallegrande.edu.pe.requestsservice.domain.exception;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long id) {
        super("Request not found: " + id);
    }
}
