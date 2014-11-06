package common.concurrent;

import org.junit.Assert;
import org.junit.Test;
import common.concurrent.impl.AbstractDependentTask;
import common.concurrent.impl.ExecutionContext;
import common.concurrent.impl.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Simple test that shows how is ForkJoin framework working
 */
public class ForkJoinTest {

    private static final int ARRAY_SIZE = 1000000;

    public class RecursiveSortingTask<T extends Comparable<T>> extends RecursiveTask<List<T>> {

        private static final long serialVersionUID = 4377807349894860018L;
        private List<T> array;
        private int first;
        private int last;

        public RecursiveSortingTask(List<T> array, int first, int last) {
            this.array = array;
            this.first = first;
            this.last = last;
        }

        @Override
        protected List<T> compute() {
            int i = first;
            int j = last;
            T middle = array.get((first + last) / 2);
            while (i <= j) {
                while (array.get(i).compareTo(middle) < 0) {
                    i++;
                }
                while (array.get(j).compareTo(middle) > 0) {
                    j--;
                }
                if (i <= j) {
                    T itemToSwap = array.get(i);
                    array.set(i, array.get(j));
                    array.set(j, itemToSwap);
                    i++;
                    j--;
                }
            }
            final List<RecursiveSortingTask<T>> tasks = new ArrayList<RecursiveSortingTask<T>>(2);
            if (first < j) {
                tasks.add(new RecursiveSortingTask<T>(array, first, j));
            }
            if (i < last) {
                tasks.add(new RecursiveSortingTask<T>(array, i, last));
            }
            invokeAll(tasks);
            return array;
        }
    }

    public class DependentSortingTask<T extends Comparable<T>> extends AbstractDependentTask {

        private final List<T> array;
        private final int first;
        private final int last;

        public DependentSortingTask(final List<T> array, final int first, final int last) {
            this.array = array;
            this.first = first;
            this.last = last;
        }

        @Override
        protected void executeBeforeDependencies(final IExecutionContext executionContext) {
            int i = first;
            int j = last;
            T middle = array.get((first + last) / 2);
            while (i <= j) {
                while (array.get(i).compareTo(middle) < 0) {
                    i++;
                }
                while (array.get(j).compareTo(middle) > 0) {
                    j--;
                }
                if (i <= j) {
                    T itemToSwap = array.get(i);
                    array.set(i, array.get(j));
                    array.set(j, itemToSwap);
                    i++;
                    j--;
                }
            }
            List<DependentSortingTask<T>> tasks = new ArrayList<DependentSortingTask<T>>(2);
            if (first < j) {
                tasks.add(new DependentSortingTask<T>(array, first, j));
            }
            if (i < last) {
                tasks.add(new DependentSortingTask<T>(array, i, last));
            }
            getDependencies().addAll(tasks);
        }

        @Override
        protected void executeAfterDependencies(IExecutionContext executionContext) {
        }

        @Override
        public String getName() {
            return getClass().getName();
        }
    }

    private static <T extends Comparable<T>> void qsort(final List<T> array, final int first, final int last) {
        int i = first;
        int j = last;
        T middle = array.get((first + last) / 2);
        while (i <= j) {
            while (array.get(i).compareTo(middle) < 0) {
                i++;
            }
            while (array.get(j).compareTo(middle) > 0) {
                j--;
            }
            if (i <= j) {
                T itemToSwap = array.get(i);
                array.set(i, array.get(j));
                array.set(j, itemToSwap);
                i++;
                j--;
            }
        }
        if (first < j) {
            qsort(array, first, j);
        }
        if (i < last) {
            qsort(array, i, last);
        }
    }

    @Test
    public void testForkJoin() {
        final ForkJoinPool fjp = new ForkJoinPool(128);
        final List<Integer> array = new ArrayList<Integer>(ARRAY_SIZE);
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array.add(i);
        }

        shuffle(array);
        fjp.invoke(new RecursiveSortingTask<Integer>(array, 0, array.size() - 1));

        shuffle(array);
        fjp.invoke(new RecursiveSortingTask<Integer>(array, 0, array.size() - 1));

        shuffle(array);
        fjp.invoke(new RecursiveSortingTask<Integer>(array, 0, array.size() - 1));

        for (int i = 0; i < array.size() - 1; i++) {
            Assert.assertTrue("Not sorted", array.get(i) < array.get(i + 1));
        }
    }

    @Test
    public void testCustomExecutor() {
        final TaskExecutor taskExecutor = new TaskExecutor(15, 30, 0L);
        final IExecutionContext executionContext = new ExecutionContext(taskExecutor);
        final ITaskStatusGroup group = taskExecutor.createTasksGroup();

        final List<Integer> array = new ArrayList<Integer>(ARRAY_SIZE);
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array.add(i);
        }

        shuffle(array);
        taskExecutor.launch(new DependentSortingTask<Integer>(array, 0, array.size() - 1), executionContext, group);
        group.waitAllTasksComplete();

        shuffle(array);
        taskExecutor.launch(new DependentSortingTask<Integer>(array, 0, array.size() - 1), executionContext, group);
        group.waitAllTasksComplete();

        shuffle(array);
        taskExecutor.launch(new DependentSortingTask<Integer>(array, 0, array.size() - 1), executionContext, group);
        group.waitAllTasksComplete();

        for (int i = 0; i < array.size() - 1; i++) {
            Assert.assertTrue("Not sorted", array.get(i) < array.get(i + 1));
        }
    }

    @Test
    public void testSynchronously() {
        final List<Integer> array = new ArrayList<Integer>(ARRAY_SIZE);
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array.add(i);
        }

        shuffle(array);
        qsort(array, 0, array.size() - 1);

        shuffle(array);
        qsort(array, 0, array.size() - 1);

        shuffle(array);
        qsort(array, 0, array.size() - 1);

        for (int i = 0; i < array.size() - 1; i++) {
            Assert.assertTrue("Not sorted", array.get(i) < array.get(i + 1));
        }
    }

    @Test
    public void testString() {
        final String s =
                "{\"email\":\"test@mail.com\",\"password\":\"Dgkr2M87xdgd/cO7W+O78SmJSCGucfwwqXtHW22ve68=\",\"login\":\"Stacy\",\"firstName\":\"Stacy\",\"lastName\":\"Wrights\",\"gender\":\"F\",\"birthDate\":\"1983-03-18\"}";
        if (s.indexOf("\"email\"") > 0) {
            String value = s.replaceAll(
                    ".*?\"email\":\\s*?\"([a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*)\".*",
                    "$1");
            Assert.assertEquals("test@mail.com", value);
        }
    }

    private static <T> void shuffle(final List<T> array) {
        final Random rand = new Random(ForkJoinTest.class.hashCode());
        for (int i = 0; i < array.size() - 1; i++) {
            int j = rand.nextInt(array.size() - i - 1);
            T itemToSwap = array.get(i + j);
            array.set(i + j, array.get(i));
            array.set(i, itemToSwap);
        }
    }
}
