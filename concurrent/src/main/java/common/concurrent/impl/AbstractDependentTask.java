package common.concurrent.impl;

import common.concurrent.IDependentTask;
import common.concurrent.IExecutionContext;
import common.concurrent.ITask;
import common.concurrent.ITaskExecutor;
import common.concurrent.ITaskStatusGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for any dependent task
 */
public abstract class AbstractDependentTask implements IDependentTask {
    private List<ITask> m_dependencies;

    /**
     * Executes all the dependent tasks on the same thread pool
     *
     * @param executionContext - data transfer object needs for dependent threads interaction
     */
    @Override
    public void execute(final IExecutionContext executionContext) {
        executeBeforeDependencies(executionContext);
        List<ITask> dependencies = getDependencies();
        if (dependencies.size() > 0) {
            ITaskExecutor taskExecutor = executionContext.getExecutor();
            if (taskExecutor != null) {
                ITaskStatusGroup group = taskExecutor.createTasksGroup();
                for (ITask dependency : getDependencies()) {
                    taskExecutor.launch(dependency, executionContext, group);
                }
                group.waitAllTasksComplete();
            } else {
                throw new IllegalStateException(
                        "There is no TaskExecutor in ExecutionContext for dependent tasks processing.");
            }
        }
        executeAfterDependencies(executionContext);
    }

    protected abstract void executeBeforeDependencies(final IExecutionContext executionContext);

    protected abstract void executeAfterDependencies(final IExecutionContext executionContext);

    @Override
    public List<ITask> getDependencies() {
        if (m_dependencies == null) {
            m_dependencies = new ArrayList<ITask>();
        }
        return m_dependencies;
    }
}
