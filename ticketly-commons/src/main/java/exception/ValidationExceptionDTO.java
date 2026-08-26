package exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationExceptionDTO extends ExceptionDTO {

    private static final String MESSAGE = "validation errors";

    private final List<ViolationInfo> violations = new ArrayList<>();

    public ValidationExceptionDTO() {
        super(MESSAGE);
    }

    public void addViolation(String field, String message) {
        violations.add(new ViolationInfo(field, message));
    }

    private record ViolationInfo(String field, String message) {
    }
}
