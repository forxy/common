package common.api

import common.exceptions.EventLogBase
import groovy.transform.Canonical

/**
 * Exception message entity
 */
@Canonical
class StatusEntity implements Serializable {

    EventLogBase error
    List<String> messages

    StatusEntity() {
    }

    StatusEntity(final String message) {
        this.messages = Collections.singletonList(message)
    }

    StatusEntity(final EventLogBase error, final String message) {
        this.error = error
        this.messages = Collections.singletonList(message)
    }

    StatusEntity(final EventLogBase error, final List<String> messages) {
        this.error = error
        this.messages = messages
    }

    StatusEntity(final EventLogBase error, final Throwable cause) {
        this.error = error
        this.messages = Collections.singletonList(cause.getMessage())
    }
}
