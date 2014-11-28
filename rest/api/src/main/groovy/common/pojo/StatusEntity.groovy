package common.pojo

import javax.xml.bind.annotation.XmlRootElement

/**
 * Exception message entity
 */
@XmlRootElement(name = "error_message")
class StatusEntity implements Serializable {

    String code

    List<String> messages

    StatusEntity() {
    }

    StatusEntity(final String code, final String message) {
        this.code = code
        this.messages = Collections.singletonList(message)
    }

    StatusEntity(final String code, final List<String> messages) {
        this.code = code
        this.messages = messages
    }

    StatusEntity(final String code, final Throwable cause) {
        this.code = code
        this.messages = Collections.singletonList(cause.getMessage())
    }
}
