package common.concurrent

/**
 * Task execution status
 */
public interface ITaskStatus {
    /**
     * Returns task this status corresponds to
     *
     * @return task
     */
    ITask getTask()

    /**
     * Gets the unhandled Throwable if it exists.
     *
     * @return Returns the unhandled throwable.
     */
    Throwable getUnhandledThrowable()

    /**
     * Determines whether or not the current activity operation is running.
     *
     * @return True if the current operation is running.
     */
    boolean isRunning()
}