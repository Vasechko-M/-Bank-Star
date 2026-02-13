package pro.sky.manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RuleAlreadyExistsException extends RuntimeException {

    public RuleAlreadyExistsException(String message) {
        super(message);
    }

    public RuleAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}