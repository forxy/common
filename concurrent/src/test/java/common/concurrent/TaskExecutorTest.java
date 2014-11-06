package common.concurrent;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import common.concurrent.impl.AbstractDependentTask;
import common.concurrent.impl.ExecutionContext;
import common.concurrent.impl.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Unit test for TaskExecutor - custom implementation of concurrent ExecutorService
 */
public class TaskExecutorTest {

    private static TaskExecutor s_taskExecutor = null;

    private class SimpleTask extends AbstractDependentTask {
        private final String m_name;
        private final boolean m_shouldFail;

        public SimpleTask(final String name, final boolean shouldFail) {
            m_name = name;
            m_shouldFail = shouldFail;
        }

        @Override
        protected void executeBeforeDependencies(final IExecutionContext executionContext) {
        }

        @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
        @Override
        protected void executeAfterDependencies(final IExecutionContext executionContext) {
            try {
                Thread.sleep(new Random().nextInt(2000));
            } catch (Exception ignored) //NOPMD
            {

            }
            if (m_shouldFail) {
                throw new RuntimeException(getName() + "-fail");
            }
            executionContext.setObject(getName(), getName());
        }

        @Override
        public String getName() {
            return m_name;
        }

        protected boolean shouldFail() {
            return m_shouldFail;
        }
    }

    @BeforeClass
    public static void initialize() throws Exception {
        s_taskExecutor = new TaskExecutor(15, 30, 0L);
    }

    @AfterClass
    public static void deinitialize() throws Exception {
        s_taskExecutor.shutdown();
    }

    @Test
    public void testDependenciesExceptions() throws Exception {
        IExecutionContext executionContext = new ExecutionContext(s_taskExecutor);
        int numTasks = 10;

        ITaskStatusGroup group = s_taskExecutor.createTasksGroup();
        List<ITaskStatus> statuses = new ArrayList<ITaskStatus>();
        for (int i = 0; i < numTasks; i++) {
            final int fi = i;
            SimpleTask task = new SimpleTask("RootTask-" + i, i % 2 == 1);
            for (int j = 0; j < numTasks; j++) {
                final int fj = j;
                task.getDependencies().add(new AbstractDependentTask() {
                    @Override
                    public String getName() {
                        return "DependentTask-" + fi + "-" + fj;
                    }

                    @Override
                    protected void executeBeforeDependencies(final IExecutionContext executionContext) {
                    }

                    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
                    @Override
                    protected void executeAfterDependencies(final IExecutionContext executionContext) {
                        try {
                            Thread.sleep(new Random().nextInt(20 * fj));
                        } catch (Exception ignored) //NOPMD
                        {
                        }
                        if (fj % 2 == 0) {
                            throw new RuntimeException(getName() + "-fail");
                        }
                    }
                });
            }
            ITaskStatus status = s_taskExecutor.launch(task, executionContext, group);
            statuses.add(status);
        }
        group.waitAllTasksComplete();

        Assert.assertEquals(numTasks, statuses.size());
        for (ITaskStatus status : statuses) {
            Assert.assertFalse(status.isRunning());
            Assert.assertTrue(status.getTask() instanceof SimpleTask);
            SimpleTask task = (SimpleTask) status.getTask();
            if (task.shouldFail()) {
                //noinspection ThrowableResultOfMethodCallIgnored
                Assert.assertFalse(status.getUnhandledThrowable() == null);
            }
        }
    }

    @Test
    public void testTaskExecution() throws Exception {
        final IExecutionContext executionContext = new ExecutionContext(s_taskExecutor);
        final List<ITaskStatus> statuses = new ArrayList<ITaskStatus>();
        final int taskCount = 5;

        ITaskStatusGroup group = s_taskExecutor.createTasksGroup();
        for (int i = 0; i < taskCount; i++) {
            final String taskName = "SimpleTask-" + i;
            ITaskStatus status = s_taskExecutor.launch(new ITask() {
                @Override
                public void execute(final IExecutionContext executionContext) {
                    try {
                        Thread.sleep(new Random().nextInt(200));
                    } catch (Exception ignored) //NOPMD
                    {
                    }
                    executionContext.setObject(getName(), getName());
                }

                @Override
                public String getName() {
                    return taskName;
                }
            }, executionContext, group);
            statuses.add(status);
        }
        group.waitAllTasksComplete();

        Assert.assertEquals("Completed tasks", taskCount, statuses.size());
        for (ITaskStatus status : statuses) {
            Assert.assertNotNull(status);
            Assert.assertFalse(status.isRunning());
            //noinspection ThrowableResultOfMethodCallIgnored
            Assert.assertTrue(status.getUnhandledThrowable() == null);
            Assert.assertTrue(executionContext.getObject(status.getTask().getName()) != null);
        }
    }

    @Test
    public void testTaskExecutionThrowException() throws Exception {
        final IExecutionContext executionContext = new ExecutionContext(s_taskExecutor);
        final List<ITaskStatus> statuses = new ArrayList<ITaskStatus>();
        final int taskCount = 5;

        ITaskStatusGroup group = s_taskExecutor.createTasksGroup();
        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            final ITaskStatus status = s_taskExecutor.launch(new ITask() {
                private final String m_name = "MyTask_" + index;

                @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
                @Override
                public void execute(final IExecutionContext executionContext) {
                    throw new RuntimeException(getName());
                }

                @Override
                public String getName() {
                    return m_name;
                }
            }, executionContext, group);

            statuses.add(status);
        }
        group.waitAllTasksComplete();

        Assert.assertTrue("Exceptions empty", statuses.size() > 0);
        Assert.assertEquals("Completed futures size", taskCount, statuses.size());
        for (final ITaskStatus status : statuses) {
            Assert.assertFalse(status.isRunning());
            Assert.assertNotNull("Future last exceptions not null", status.getUnhandledThrowable());
        }
    }
}
