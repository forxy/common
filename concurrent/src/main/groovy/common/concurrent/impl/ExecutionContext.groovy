package common.concurrent.impl

import common.concurrent.IExecutionContext
import common.concurrent.ITaskExecutor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Data transfer object that contains all the data required for dependent tasks interaction
 */
class ExecutionContext implements IExecutionContext {
    private final ConcurrentMap<String, Object> objects = new ConcurrentHashMap<String, Object>()

    final ITaskExecutor executor

    ExecutionContext(final ITaskExecutor executor) {
        this.executor = executor
    }

    @Override
    public <T> T getObject(final String objectName) {
        return (T) objects.get(objectName)
    }

    @Override
    void setObject(final String objectName, final Object object) {
        if (object == null) {
            objects.remove(objectName)
        } else {
            objects.put(objectName, object)
        }
    }
}
