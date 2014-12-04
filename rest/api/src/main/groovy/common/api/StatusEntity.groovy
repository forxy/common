package common.api

import javax.xml.bind.annotation.XmlRootElement

/**
 * Exception message entity
 */
@XmlRootElement(name = "error_message")
class StatusEntity implements Serializable {

    int code = 200

    List<String> messages

    StatusEntity() {
    }

    StatusEntity(final String message) {
        this.messages = Collections.singletonList(message)
    }

    StatusEntity(final int code, final String message) {
        this.code = code
        this.messages = Collections.singletonList(message)
    }

    StatusEntity(final int code, final List<String> messages) {
        this.code = code
        this.messages = messages
    }

    StatusEntity(final int code, final Throwable cause) {
        this.code = code
        this.messages = Collections.singletonList(cause.getMessage())
    }
}
