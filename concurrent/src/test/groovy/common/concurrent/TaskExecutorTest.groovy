package common.concurrent

import common.concurrent.impl.AbstractDependentTask
import common.concurrent.impl.ExecutionContext
import common.concurrent.impl.TaskExecutor
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

/**
 * Unit test for TaskExecutor - custom implementation of concurrent ExecutorService
 */
class TaskExecutorTest {

    private static TaskExecutor s_taskExecutor = null

    private class SimpleTask extends AbstractDependentTask {
        private final String name
        private final boolean shouldFail

        SimpleTask(final String name, final boolean shouldFail) {
            this.name = name
            this.shouldFail = shouldFail
        }

        @Override
        protected void executeBeforeDependencies(final IExecutionContext executionContext) {
        }

        @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
        @Override
        protected void executeAfterDependencies(final IExecutionContext executionContext) {
            try {
                Thread.sleep(new Random().nextInt(2000))
            } catch (Exception ignored) //NOPMD
            {

            }
            if (shouldFail) {
                throw new RuntimeException(getName() + "-fail")
            }
            executionContext.setObject(getName(), getName())
        }

        @Override
        String getName() {
            return name
        }

        protected boolean shouldFail() {
            return shouldFail
        }
    }

    @BeforeClass
    static void initialize() throws Exception {
        s_taskExecutor = new TaskExecutor(15, 30, 0L)
    }

    @AfterClass
    static void deinitialize() throws Exception {
        s_taskExecutor.shutdown()
    }

    @Test
    void testDependenciesExceptions() throws Exception {
        IExecutionContext context = new ExecutionContext(s_taskExecutor)
        int numTasks = 10

        ITaskStatusGroup group = s_taskExecutor.createTasksGroup()
        List<ITaskStatus> statuses = new ArrayList<ITaskStatus>()
        for (int i in 0..numTasks - 1) {
            final int fi = i
            SimpleTask task = new SimpleTask("RootTask-" + i, i % 2 == 1)
            for (int j in 0..numTasks - 1) {
                final int fj = j
                task.dependencies << new AbstractDependentTask() {
                    @Override
                    String getName() {
                        return "DependentTask-" + fi + "-" + fj
                    }

                    @Override
                    protected void executeBeforeDependencies(final IExecutionContext executionContext) {
                    }

                    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
                    @Override
                    protected void executeAfterDependencies(final IExecutionContext executionContext) {
                        try {
                            Thread.sleep(new Random().nextInt(20 * fj))
                        } catch (Exception ignored) //NOPMD
                        {
                        }
                        if (fj % 2 == 0) {
                            throw new RuntimeException(name + "-fail")
                        }
                    }
                }
            }
            ITaskStatus status = s_taskExecutor.launch(task, context, group)
            statuses.add(status)
        }
        group.waitAllTasksComplete()

        Assert.assertEquals(numTasks, statuses.size())
        for (ITaskStatus status : statuses) {
            Assert.assertFalse(status.running)
            Assert.assertTrue(status.task instanceof SimpleTask)
            SimpleTask task = (SimpleTask) status.task
            if (task.shouldFail()) {
                //noinspection ThrowableResultOfMethodCallIgnored
                Assert.assertFalse(status.unhandledThrowable == null)
            }
        }
    }

    @Test
    void testTaskExecution() throws Exception {
        final IExecutionContext context = new ExecutionContext(s_taskExecutor)
        final List<ITaskStatus> statuses = new ArrayList<ITaskStatus>()
        final int taskCount = 5

        ITaskStatusGroup group = s_taskExecutor.createTasksGroup()
        for (int i in 0..taskCount - 1) {
            final String taskName = "SimpleTask-" + i
            ITaskStatus status = s_taskExecutor.launch(new ITask() {
                @Override
                void execute(final IExecutionContext executionContext) {
                    try {
                        Thread.sleep(new Random().nextInt(200))
                    } catch (Exception ignored) //NOPMD
                    {
                    }
                    executionContext.setObject(name, name)
                }

                @Override
                String getName() {
                    return taskName
                }
            }, context, group)
            statuses.add(status)
        }
        group.waitAllTasksComplete()

        Assert.assertEquals("Completed tasks", taskCount, statuses.size())
        for (ITaskStatus status : statuses) {
            Assert.assertNotNull(status)
            Assert.assertFalse(status.running)
            //noinspection ThrowableResultOfMethodCallIgnored
            Assert.assertTrue(status.unhandledThrowable == null)
            Assert.assertTrue(context.getObject(status.task.name) != null)
        }
    }

    @Test
    void testTaskExecutionThrowException() throws Exception {
        final IExecutionContext context = new ExecutionContext(s_taskExecutor)
        final List<ITaskStatus> statuses = new ArrayList<ITaskStatus>()
        final int taskCount = 5

        ITaskStatusGroup group = s_taskExecutor.createTasksGroup()
        for (int i in 0..taskCount - 1) {
            final int index = i
            final ITaskStatus status = s_taskExecutor.launch(new ITask() {
                final String name = "MyTask_" + index

                @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
                @Override
                void execute(final IExecutionContext executionContext) {
                    throw new RuntimeException(name)
                }
            }, context, group)

            statuses.add(status)
        }
        group.waitAllTasksComplete()

        Assert.assertTrue("Exceptions empty", statuses.size() > 0)
        Assert.assertEquals("Completed futures size", taskCount, statuses.size())
        for (final ITaskStatus status : statuses) {
            Assert.assertFalse(status.running)
            Assert.assertNotNull("Future last exceptions not null", status.unhandledThrowable)
        }
    }
}
