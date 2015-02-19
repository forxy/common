package common.concurrent

/**
 * Basic thread execution unit
 */
interface ITask {
    /**
     * @return task name to simplify orientation within concurrent execution
     */
    String getName()

    /**
     * This code block will execute in the separate thread
     *
     * @param executionContext - data transfer object needs for threads interaction
     */
    void execute(final IExecutionContext executionContext)
}
