package common.concurrent;

/**
 * @author v-srabukha
 * @author v-dchabrovsky
 */
public interface ITaskStatusGroup {
    /**
     * This method will return when all the operations that have been associated
     * with this group have completed.
     */
    void waitAllTasksComplete();

    /**
     * This method will return when all the operations that have been associated
     * with this group have completed.
     *
     * @param timeout The number of milliseconds to wait for all operations to
     *                complete.  If the operations have not all completed within
     *                this time, this method returns false.
     * @return True if all operations complete before the timeout, false otherwise.
     */
    boolean waitAllTasksComplete(final long timeout);

    /**
     * This method will return when any operation associated with this status group
     * completes. When this method returns, it clears the "completed" flag such that
     * any operation completing after this method returns will trigger it to return
     * immediately if you call it again.  The net result of this is that if you are
     * processing the individual IActivityOperationStatus instances that are part of
     * this group to determine who completed, you might get a false positive if one
     * completes after this method returns and before you check it.  Being aware of
     * this behavior will allow for smooth running.
     */
    void waitAnyOperationCompletes();

    /**
     * This method will return when any operation associated with this status group
     * completes. When this method returns, it clears the "completed" flag such that
     * any operation completing after this method returns will trigger it to return
     * immediately if you call it again.  The net result of this is that if you are
     * processing the individual IActivityOperationStatus instances that are part of
     * this group to determine who completed, you might get a false positive if one
     * completes after this method returns and before you check it.  Being aware of
     * this behavior will allow for smooth running.
     *
     * @param timeout The number of milliseconds to wait for any operation to
     *                complete.  If no operations have been completed within
     *                this time, this method returns false.
     * @return True if an operation completed before the timeout, false otherwise.
     */
    boolean waitAnyOperationCompletes(final long timeout);
}
