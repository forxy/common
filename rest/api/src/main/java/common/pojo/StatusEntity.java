package common.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Exception message entity
 */
@XmlRootElement(name = "error_message")
public class StatusEntity implements Serializable {
    private String code;
    private List<String> messages;

    public StatusEntity() {
    }

    public StatusEntity(final String code, final String message) {
        this.code = code;
        this.messages = Collections.singletonList(message);
    }

    public StatusEntity(final String code, final List<String> messages) {
        this.code = code;
        this.messages = messages;
    }

    public StatusEntity(final String code, final Throwable cause) {
        this.code = code;
        this.messages = Collections.singletonList(cause.getMessage());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
