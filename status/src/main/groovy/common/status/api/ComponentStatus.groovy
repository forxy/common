package common.status.api

import groovy.transform.TupleConstructor

/**
 * Includes the information about Component health status
 */
@TupleConstructor
class ComponentStatus implements Serializable {

    enum ComponentType {
        DB, Service, Cache
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
}
