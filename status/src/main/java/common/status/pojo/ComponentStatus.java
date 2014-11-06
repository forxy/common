package common.status.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Includes the information about Component health status
 */
public class ComponentStatus implements Serializable {

    public enum ComponentType {
        DB, service, cache
    }

    private String name;

    private String location;

    private StatusType status;

    private Map<String, String> componentConfiguration;

    private ComponentType componentType;

    private long responseTime;

    private Date lastUpdated;

    private String exceptionMessage;

    private String exceptionDetails;

    public ComponentStatus() {
    }

    public ComponentStatus(final String name, final String location, final StatusType status,
                           final Map<String, String> componentConfiguration, final ComponentType componentType,
                           final long responseTime, final Date lastUpdated, final String exceptionMessage,
                           final String exceptionDetails) {
        this.name = name;
        this.location = location;
        this.status = status;
        this.componentConfiguration = componentConfiguration;
        this.componentType = componentType;
        this.responseTime = responseTime;
        this.lastUpdated = lastUpdated;
        this.exceptionMessage = exceptionMessage;
        this.exceptionDetails = exceptionDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(final StatusType status) {
        this.status = status;
    }

    public Map<String, String> getComponentConfiguration() {
        return componentConfiguration;
    }

    public void setComponentConfiguration(final Map<String, String> componentConfiguration) {
        this.componentConfiguration = componentConfiguration;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(final ComponentType componentType) {
        this.componentType = componentType;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(final long responseTime) {
        this.responseTime = responseTime;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(final String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionDetails() {
        return exceptionDetails;
    }

    public void setExceptionDetails(final String exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
    }
}
