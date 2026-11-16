package lt.pcbuilder.domain;

public class PlanningException extends RuntimeException {
    private final String code;
    public PlanningException(String code, String message) {
        super(message);
        this.code = code;
    }
    public String code() { return code; }
}
