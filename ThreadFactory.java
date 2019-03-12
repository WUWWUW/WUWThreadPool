package thread.threadpool;

/**
 * 提供创建线程的接口 来个性化地定制Thread
 */
@FunctionalInterface
public interface ThreadFactory {
    Thread createThread(Runnable runnable);
}
