package common.concurrent.impl;

import common.concurrent.IExecutionContext;
import common.concurrent.ITaskExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Data transfer object that contains all the data required for dependent tasks interaction
 */
public class ExecutionContext implements IExecutionContext {
    private final ConcurrentMap<String, Object> m_objects = new ConcurrentHashMap<String, Object>();

    private final ITaskExecutor m_executor;

    public ExecutionContext(final ITaskExecutor executor) {
        m_executor = executor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(final String objectName) {
        return (T) m_objects.get(objectName);
    }

    @Override
    public void setObject(final String objectName, final Object object) {
        if (object == null) {
            m_objects.remove(objectName);
        } else {
            m_objects.put(objectName, object);
        }
    }

    @Override
    public ITaskExecutor getExecutor() {
        return m_executor;
    }
}
