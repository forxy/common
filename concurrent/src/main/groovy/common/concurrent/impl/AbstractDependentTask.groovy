package common.concurrent.impl

import common.concurrent.*

/**
 * Base class for any dependent task
 */
abstract class AbstractDependentTask implements IDependentTask {
    List<ITask> dependencies = []

    /**
     * Executes all the dependent tasks on the same thread pool
     *
     * @param executionContext - data transfer object needs for dependent threads interaction
     */
    @Override
    void execute(final IExecutionContext executionContext) {
        executeBeforeDependencies(executionContext)
        List<ITask> dependencies = this.dependencies
        if (dependencies.size() > 0) {
            ITaskExecutor taskExecutor = executionContext.executor
            if (taskExecutor != null) {
                ITaskStatusGroup group = taskExecutor.createTasksGroup()
                for (ITask dependency : this.dependencies) {
                    taskExecutor.launch(dependency, executionContext, group)
                }
                group.waitAllTasksComplete()
            } else {
                throw new IllegalStateException(
                        'There is no TaskExecutor in ExecutionContext for dependent tasks processing.')
            }
        }
        executeAfterDependencies(executionContext)
    }

    protected abstract void executeBeforeDependencies(final IExecutionContext executionContext)

    protected abstract void executeAfterDependencies(final IExecutionContext executionContext)
}
