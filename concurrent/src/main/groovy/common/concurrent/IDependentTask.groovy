package common.concurrent
/**
 * Thread execution unit that will start only after all the dependent tasks
 */
interface IDependentTask extends ITask {
    /**
     * @return list of dependent tasks that let the executor to complete them before the current task call
     */
    List<ITask> getDependencies()
}
