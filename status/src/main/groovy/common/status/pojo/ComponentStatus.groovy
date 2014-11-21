package common.status.pojo
/**
 * Includes the information about Component health status
 */
class ComponentStatus implements Serializable {

    enum ComponentType {
        DB, service, cache
    }

    String name

    String location

    StatusType status

    Map<String, String> componentConfiguration

    ComponentType componentType

    long responseTime

    Date lastUpdated

    String exceptionMessage

    String exceptionDetails

    ComponentStatus() {
    }

    ComponentStatus(final String name, final String location, final StatusType status,
                    final Map<String, String> componentConfiguration, final ComponentType componentType,
                    final long responseTime, final Date lastUpdated, final String exceptionMessage,
                    final String exceptionDetails) {
        this.name = name
        this.location = location
        this.status = status
        this.componentConfiguration = componentConfiguration
        this.componentType = componentType
        this.responseTime = responseTime
        this.lastUpdated = lastUpdated
        this.exceptionMessage = exceptionMessage
        this.exceptionDetails = exceptionDetails
    }
}
