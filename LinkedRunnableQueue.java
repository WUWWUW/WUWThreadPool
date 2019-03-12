package thread.threadpool;

import java.util.LinkedList;

public class LinkedRunnableQueue implements RunnableQueue {
    //任务队列中最大容量
    private final int limit;

    //拒绝策略
    private final DenyPolicy denyPolicy;

    //存放任务的队列
    private final LinkedList<Runnable> runnableList=new LinkedList<>();

    private final ThreadPool threadPool;

    public LinkedRunnableQueue(int limit, DenyPolicy denyPolicy, ThreadPool threadPool) {
        this.limit = limit;
        this.denyPolicy = denyPolicy;
        this.threadPool = threadPool;
    }

    @Override
    public void offer(Runnable runnable) {
        synchronized (runnableList){
            if(runnableList.size()>=limit){
                //任务队列已经满了   执行拒绝策略
                denyPolicy.reject(runnable,threadPool);
            }else {
                //将任务加入队尾 并且唤醒阻塞中的线程
                runnableList.addLast(runnable);
                runnableList.notifyAll();
            }
        }
    }

    @Override
    public Runnable take() throws InterruptedException{
        synchronized (runnableList){
            while (runnableList.isEmpty()){
                try{
                    //如果任务队列中没有可执行的任务 则当前线程被挂起 进入runnableList关联的monitor waitset中唤等待唤醒（新的任务加入）
                    runnableList.wait();
                }catch (InterruptedException e){
                    //被中断时需要将异常抛出
                    throw  e;
                }
            }
        }
        return runnableList.removeFirst();
    }

    @Override
    public int size() {
        synchronized (runnableList){
            //返回当前任务队列中的任务数
            return runnableList.size();
        }
    }
}
