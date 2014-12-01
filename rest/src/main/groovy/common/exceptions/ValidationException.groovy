package common.exceptions

import net.sf.oval.exception.ValidationFailedException

/**
 * Generic validation exception
 */
class ValidationException extends ServiceException {

    final List<String> messages

    ValidationException(List<String> messages) {
        super(HttpEvent.ValidationException)
        this.messages = messages
    }

    ValidationException(Throwable cause, Object... args) {
        super(cause, HttpEvent.ValidationException, args)
        messages = [super.message]
    }

    ValidationException(Object... args) {
        super(HttpEvent.ValidationException, args)
        messages = [super.message]
    }

    ValidationException(Throwable cause) {
        super(cause, HttpEvent.ValidationException)
        messages = [super.message]
    }

    static ValidationException build(final ValidationFailedException e) {
        final List<String> messages = new ArrayList<String>()
        Throwable cause = e
        while (cause) {
            messages << cause.message
            cause = cause.cause
        }
        return new ValidationException(messages)
    }
}
