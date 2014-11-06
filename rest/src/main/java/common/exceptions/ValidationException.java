package common.exceptions;

import net.sf.oval.exception.ValidationFailedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic validation exception
 */
public class ValidationException extends ServiceException {

    private static final long serialVersionUID = 2500370557195275595L;

    private final List<String> messages;

    public ValidationException(List<String> messages) {
        super(HttpEventLogId.ValidationException);
        this.messages = messages;
    }

    public ValidationException(String message) {
        super(message, HttpEventLogId.ValidationException);
        messages = new ArrayList<>(1);
        messages.add(message);
    }

    public ValidationException(Throwable cause, String message) {
        super(cause, message, HttpEventLogId.ValidationException);
        messages = new ArrayList<>(1);
        messages.add(super.getMessage());
    }

    public ValidationException(Throwable cause, Object... args) {
        super(cause, HttpEventLogId.ValidationException, args);
        messages = new ArrayList<>(1);
        messages.add(super.getMessage());
    }

    public ValidationException(Object... args) {
        super(HttpEventLogId.ValidationException, args);
        messages = new ArrayList<>(1);
        messages.add(super.getMessage());
    }

    public ValidationException(Throwable cause) {
        super(cause, HttpEventLogId.ValidationException);
        messages = new ArrayList<>(1);
        messages.add(super.getMessage());
    }

    public List<String> getMessages() {
        return messages;
    }

    public static ValidationException build(final ValidationFailedException e) {
        final List<String> messages = new ArrayList<String>();
        Throwable cause = e;
        while (cause != null) {
            messages.add(cause.getMessage());
            cause = cause.getCause();
        }
        return new ValidationException(messages);
    }
}
