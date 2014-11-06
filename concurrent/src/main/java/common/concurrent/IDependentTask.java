package common.concurrent;

import java.util.List;

/**
 * Thread execution unit that will start only after all the dependent tasks
 */
public interface IDependentTask extends ITask {
    /**
     * @return list of dependent tasks that let the executor to complete them before the current task call
     */
    List<ITask> getDependencies();
}
