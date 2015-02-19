package common.concurrent.impl

import common.concurrent.*

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Custom implementation of the {@code ThreadPoolExecutor}
 */
class TaskExecutor extends ThreadPoolExecutor implements ITaskExecutor {

    TaskExecutor(final int nThreads, final int nMaxThreads, final long keepAliveTime) {
        super(nThreads, nMaxThreads, keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy())
    }

    @Override
    ITaskStatusGroup createTasksGroup() {
        return new TaskStatusGroupImpl()
    }

    @Override
    List<ITaskStatus> executeAll(final List<? extends ITask> tasks, final IExecutionContext context) {
        if (tasks == null || tasks.size() == 0) {
            throw new IllegalArgumentException('Tasks list should not be empty')
        }

        List<ITaskStatus> statuses = []
        ITaskStatusGroup group = createTasksGroup()
        tasks.each {
            statuses.add(doLaunch(it, context, group))
        }
        group.waitAllTasksComplete()
        return statuses
    }

    @Override
    ITaskStatus execute(final ITask task, final IExecutionContext context) {
        TaskStatusImpl status = new TaskStatusImpl(task, null)
        TaskRunnableImpl runnable = new TaskRunnableImpl(task, context, status)
        runnable.runSynchronous()
        return status
    }

    @Override
    ITaskStatus launch(final ITask task, final IExecutionContext context) {
        return doLaunch(task, context, null)
    }

    @Override
    ITaskStatus launch(final ITask task, final IExecutionContext context, final ITaskStatusGroup group) {
        return doLaunch(task, context, group)
    }

    private ITaskStatus doLaunch(final ITask task, final IExecutionContext context, final ITaskStatusGroup group) {
        TaskStatusImpl status
        status = new TaskStatusImpl(task, group)
        final TaskRunnableImpl taskRunnable = new TaskRunnableImpl(task, context, status)
        group?.operationStatusAdd(status)
        submit(taskRunnable)
        return status
    }

    interface IExitNotificationCallback {
        /**
         * Determines whether the specified object is at an exit state
         *
         * @param param The object to evaluate.
         * @return Returns true if the specified object is at an exit state
         */
        boolean isExitState(final Object param)
    }

    private class TaskStatusImpl implements ITaskStatus {
        final ITask task
        final ITaskStatusGroup group
        boolean isCompleted
        Throwable unhandledThrowable = null

        TaskStatusImpl(final ITask task, final ITaskStatusGroup group) {
            this.task = task
            this.group = group
        }

        @Override
        boolean isRunning() {
            return !isCompleted
        }

        void setComplete(final Throwable unhandledThrowable) {
            synchronized (this) {
                this.unhandledThrowable = unhandledThrowable
                isCompleted = true
                notifyAll()
            }
        }
    }

    private class TaskRunnableImpl implements Runnable {
        private final ITask task
        private final IExecutionContext context
        private final TaskStatusImpl status

        TaskRunnableImpl(final ITask task, final IExecutionContext context, final TaskStatusImpl status) {
            this.task = task
            this.context = context
            this.status = status
        }

        @Override
        void run() {
            Exception asyncUnhandledException = null
            try {
                try {
                    runSynchronous()
                } catch (Exception ex) {
                    asyncUnhandledException = ex
                }
            } finally {
                status.setComplete(asyncUnhandledException)
                final ITaskStatusGroup group = status.getGroup()
                if (group != null) {
                    ((TaskStatusGroupImpl) group).operationStatusRemove(status)
                }
            }
        }

        void runSynchronous() {
            task.execute(context)
        }
    }

    private class TaskStatusGroupImpl implements ITaskStatusGroup {
        private final IExitNotificationCallback completeAllCallback = new IExitNotificationCallback() {
            @Override
            boolean isExitState(final Object param) {
                return ((TaskStatusGroupImpl) param).allOperationsAreDone()
            }
        }
        private final IExitNotificationCallback completeAnyCallback = new IExitNotificationCallback() {
            @Override
            boolean isExitState(final Object param) {
                return ((TaskStatusGroupImpl) param).anyOperationsAreDone()
            }
        }

        private final List<ITaskStatus> statuses = []
        private volatile boolean oneCompleted = false

        @Override
        void waitAllTasksComplete() {
            waitAllTasksComplete(Long.MAX_VALUE)
        }

        @Override
        boolean waitAllTasksComplete(final long timeout) {
            return waitForExitStateNoThrow(timeout, statuses, completeAllCallback, this)
        }

        @Override
        void waitAnyOperationCompletes() {
            waitAnyOperationCompletes(Long.MAX_VALUE)
        }

        @Override
        boolean waitAnyOperationCompletes(final long timeout) {
            synchronized (statuses) {
                final boolean anyDone = waitForExitStateNoThrow(timeout, statuses, completeAnyCallback, this)
                if (anyDone) {
                    oneCompleted = false
                }
                return anyDone
            }
        }

        private boolean allOperationsAreDone() {
            return statuses.size() == 0
        }

        private boolean anyOperationsAreDone() {
            return oneCompleted || statuses.size() == 0
        }

        void operationStatusAdd(final ITaskStatus status) {
            synchronized (statuses) {
                statuses.add(status)
                statuses.notifyAll()
            }
        }

        private void operationStatusRemove(final ITaskStatus status) {
            synchronized (statuses) {
                oneCompleted = true
                statuses.remove(status)
                statuses.notifyAll()
            }
        }
    }

    /**
     * Waits the specified amount of time for the exit state.
     *
     * @param timeout The time to wait in between checking whether the callback is in its exit state.
     * @param synchronizeObject The object to be synchronized on
     * @param callback The callback
     * @param param The parameter to use to determine whether the exit state has occured.
     * @return Returns true if the callback has reached its exit state.
     */
    private static boolean waitForExitStateNoThrow(final long timeout, final Object synchronizeObject,
                                                   final IExitNotificationCallback callback, final Object param) {
        try {
            return waitForExitState(timeout, synchronizeObject, callback, param)
        } catch (InterruptedException ignored) {
            return false
        }
    }

    /**
     * Waits the specified amount of time for the exit state.
     *
     * @param timeout The time to wait in between checking whether the callback is in its exit state.
     * @param synchronizeObject The object to be synchronized on.
     * @param callback The callback.
     * @param param The parameter to use to determine whether the exit state has occured.
     * @return Returns true if the callback has reached its exit state.
     * @throws InterruptedException
     */
    private static boolean waitForExitState(long timeout, final Object synchronizeObject,
                                            final IExitNotificationCallback callback, final Object param)
            throws InterruptedException {
        boolean inExitState = false
        final long waitStart = System.currentTimeMillis()
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (synchronizeObject) {
            while (true) {
                if (callback.isExitState(param)) {
                    inExitState = true
                    break
                }
                if (timeout > 0) {
                    synchronizeObject.wait(timeout)
                    timeout -= (System.currentTimeMillis() - waitStart)
                } else {
                    break
                }
            }
        }
        return inExitState
    }
}
