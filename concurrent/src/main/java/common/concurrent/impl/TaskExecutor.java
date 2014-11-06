package common.concurrent.impl;

import common.concurrent.IExecutionContext;
import common.concurrent.ITask;
import common.concurrent.ITaskExecutor;
import common.concurrent.ITaskStatus;
import common.concurrent.ITaskStatusGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Custom implementation of the {@code ThreadPoolExecutor}
 */
public class TaskExecutor extends ThreadPoolExecutor implements ITaskExecutor {

    public TaskExecutor(final int nThreads, final int nMaxThreads, final long keepAliveTime) {
        super(nThreads, nMaxThreads, keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public ITaskStatusGroup createTasksGroup() {
        return new TaskStatusGroupImpl();
    }

    @Override
    public List<ITaskStatus> executeAll(final List<? extends ITask> tasks, final IExecutionContext context) {
        if (tasks == null || tasks.size() == 0) {
            throw new IllegalArgumentException("Tasks list should not be empty");
        }

        List<ITaskStatus> statuses = new ArrayList<ITaskStatus>();
        ITaskStatusGroup group = createTasksGroup();
        for (ITask task : tasks) {
            statuses.add(doLaunch(task, context, group));
        }
        group.waitAllTasksComplete();
        return statuses;
    }

    @Override
    public ITaskStatus execute(final ITask task, final IExecutionContext context) {
        TaskStatusImpl status = new TaskStatusImpl(task, null);
        TaskRunnableImpl runnable = new TaskRunnableImpl(task, context, status);
        runnable.runSynchronous();
        return status;
    }

    @Override
    public ITaskStatus launch(final ITask task, final IExecutionContext context) {
        return doLaunch(task, context, null);
    }

    @Override
    public ITaskStatus launch(final ITask task, final IExecutionContext context, final ITaskStatusGroup group) {
        return doLaunch(task, context, group);
    }

    private ITaskStatus doLaunch(final ITask task, final IExecutionContext context, final ITaskStatusGroup group) {
        TaskStatusImpl status;
        status = new TaskStatusImpl(task, group);
        final TaskRunnableImpl taskRunnable = new TaskRunnableImpl(task, context, status);
        if (group != null) {
            ((TaskStatusGroupImpl) group).operationStatusAdd(status);
        }
        submit(taskRunnable);
        return status;
    }

    interface IExitNotificationCallback {
        /**
         * Determines whether the specified object is at an exit state
         *
         * @param param The object to evaluate.
         * @return Returns true if the specified object is at an exit state
         */
        boolean isExitState(final Object param);
    }

    private class TaskStatusImpl implements ITaskStatus {
        private final ITask m_task;
        private final ITaskStatusGroup m_group;
        private boolean m_isCompleted;
        private Throwable m_unhandledThrowable = null;

        TaskStatusImpl(final ITask task, final ITaskStatusGroup group) {
            m_task = task;
            m_group = group;
        }

        @Override
        public ITask getTask() {
            return m_task;
        }

        protected ITaskStatusGroup getGroup() {
            return m_group;
        }

        @Override
        public Throwable getUnhandledThrowable() {
            return m_unhandledThrowable;
        }

        @Override
        public boolean isRunning() {
            return !m_isCompleted;
        }

        public void setComplete(final Throwable unhandledThrowable) {
            synchronized (this) {
                m_unhandledThrowable = unhandledThrowable;
                m_isCompleted = true;
                notifyAll();
            }
        }
    }

    private class TaskRunnableImpl implements Runnable {
        private final ITask m_task;
        private final IExecutionContext m_context;
        private final TaskStatusImpl m_status;

        TaskRunnableImpl(final ITask task, final IExecutionContext context, final TaskStatusImpl status) {
            m_task = task;
            m_context = context;
            m_status = status;
        }

        @Override
        public void run() {
            Exception asyncUnhandledException = null;
            try {
                try {
                    runSynchronous();
                } catch (Exception ex) {
                    asyncUnhandledException = ex;
                }
            } finally {
                m_status.setComplete(asyncUnhandledException);
                final ITaskStatusGroup group = m_status.getGroup();
                if (group != null) {
                    ((TaskStatusGroupImpl) group).operationStatusRemove(m_status);
                }
            }
        }

        public void runSynchronous() {
            m_task.execute(m_context);
        }
    }

    private class TaskStatusGroupImpl implements ITaskStatusGroup {
        private final IExitNotificationCallback m_completeAllCallback = new IExitNotificationCallback() {
            @Override
            public boolean isExitState(final Object param) {
                return ((TaskStatusGroupImpl) param).allOperationsAreDone();
            }
        };
        private final IExitNotificationCallback m_completeAnyCallback = new IExitNotificationCallback() {
            @Override
            public boolean isExitState(final Object param) {
                return ((TaskStatusGroupImpl) param).anyOperationsAreDone();
            }
        };

        private final List<ITaskStatus> m_statuses = new ArrayList<ITaskStatus>();
        private volatile boolean m_oneCompleted = false;

        @Override
        public void waitAllTasksComplete() {
            waitAllTasksComplete(Long.MAX_VALUE);
        }

        @Override
        public boolean waitAllTasksComplete(final long timeout) {
            return waitForExitStateNoThrow(timeout, m_statuses, m_completeAllCallback, this);
        }

        @Override
        public void waitAnyOperationCompletes() {
            waitAnyOperationCompletes(Long.MAX_VALUE);
        }

        @Override
        public boolean waitAnyOperationCompletes(final long timeout) {
            synchronized (m_statuses) {
                final boolean anyDone = waitForExitStateNoThrow(timeout, m_statuses, m_completeAnyCallback, this);
                if (anyDone) {
                    m_oneCompleted = false;
                }
                return anyDone;
            }
        }

        private boolean allOperationsAreDone() {
            return m_statuses.size() == 0;
        }

        private boolean anyOperationsAreDone() {
            return m_oneCompleted || m_statuses.size() == 0;
        }

        private void operationStatusAdd(final ITaskStatus status) {
            synchronized (m_statuses) {
                m_statuses.add(status);
                m_statuses.notifyAll();
            }
        }

        private void operationStatusRemove(final ITaskStatus status) {
            synchronized (m_statuses) {
                m_oneCompleted = true;
                m_statuses.remove(status);
                m_statuses.notifyAll();
            }
        }
    }

    /**
     * Waits the specified amount of time for the exit state.
     *
     * @param timeout           The time to wait in between checking whether the callback is in its exit state.
     * @param synchronizeObject The object to be synchronized on
     * @param callback          The callback
     * @param param             The parameter to use to determine whether the exit state has occured.
     * @return Returns true if the callback has reached its exit state.
     */
    private static boolean waitForExitStateNoThrow(final long timeout, final Object synchronizeObject,
                                                   final IExitNotificationCallback callback, final Object param) {
        try {
            return waitForExitState(timeout, synchronizeObject, callback, param);
        } catch (InterruptedException ex) {
            return false;
        }
    }

    /**
     * Waits the specified amount of time for the exit state.
     *
     * @param timeout           The time to wait in between checking whether the callback is in its exit state.
     * @param synchronizeObject The object to be synchronized on.
     * @param callback          The callback.
     * @param param             The parameter to use to determine whether the exit state has occured.
     * @return Returns true if the callback has reached its exit state.
     * @throws InterruptedException
     */
    private static boolean waitForExitState(long timeout, final Object synchronizeObject,
                                            final IExitNotificationCallback callback, final Object param)
            throws InterruptedException {
        boolean inExitState = false;
        final long waitStart = System.currentTimeMillis();
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (synchronizeObject) {
            while (true) {
                if (callback.isExitState(param)) {
                    inExitState = true;
                    break;
                }
                if (timeout > 0) {
                    synchronizeObject.wait(timeout);
                    timeout -= (System.currentTimeMillis() - waitStart);
                } else {
                    break;
                }
            }
        }
        return inExitState;
    }
}
