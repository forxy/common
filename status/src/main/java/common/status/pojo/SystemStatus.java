package common.status.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Includes the information about System health status
 */
public class SystemStatus implements Serializable {

    private String name;

    private String location;

    private String version;

    private StatusType status;

    private List<ComponentStatus> componentStatuses;

    public SystemStatus() {
    }

    public SystemStatus(final String name, final String location, final String version, final StatusType status,
                        final List<ComponentStatus> componentStatuses) {
        this.name = name;
        this.location = location;
        this.version = version;
        this.status = status;
        this.componentStatuses = componentStatuses;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(final StatusType status) {
        this.status = status;
    }

    public List<ComponentStatus> getComponentStatuses() {
        return componentStatuses;
    }

    public void setComponentStatuses(final List<ComponentStatus> componentStatuses) {
        this.componentStatuses = componentStatuses;
    }

    public void addComponentStatuses(final ComponentStatus componentStatus) {
        if (componentStatuses == null) {
            componentStatuses = new ArrayList<ComponentStatus>();
        }
        componentStatuses.add(componentStatus);
    }
}
