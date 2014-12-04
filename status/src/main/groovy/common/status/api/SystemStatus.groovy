package common.status.api

import groovy.transform.TupleConstructor

/**
 * Includes the information about System health status
 */
@TupleConstructor
class SystemStatus implements Serializable {

    String name

    String location

    String version

    StatusType status

    List<ComponentStatus> componentStatuses

    void addComponentStatuses(final ComponentStatus componentStatus) {
        if (componentStatuses == null) {
            componentStatuses = new ArrayList<ComponentStatus>()
        }
        componentStatuses.add(componentStatus)
    }
}
