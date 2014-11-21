package common.status.pojo
/**
 * Includes the information about System health status
 */
class SystemStatus implements Serializable {

    String name

    String location

    String version

    StatusType status

    List<ComponentStatus> componentStatuses

    SystemStatus() {
    }

    SystemStatus(final String name, final String location, final String version, final StatusType status,
                 final List<ComponentStatus> componentStatuses) {
        this.name = name
        this.location = location
        this.version = version
        this.status = status
        this.componentStatuses = componentStatuses
    }

    void addComponentStatuses(final ComponentStatus componentStatus) {
        if (componentStatuses == null) {
            componentStatuses = new ArrayList<ComponentStatus>()
        }
        componentStatuses.add(componentStatus)
    }
}
