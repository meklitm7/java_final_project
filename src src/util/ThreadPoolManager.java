package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    // Singleton instance
    private static final ThreadPoolManager instance = new ThreadPoolManager();

    // Thread pool with a fixed number of threads
    private final ExecutorService executorService;

    // Private constructor to prevent instantiation
    private ThreadPoolManager() {
        // Create a thread pool with 4 worker threads (adjust as needed)
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Get the singleton instance
    public static ThreadPoolManager getInstance() {
        return instance;
    }

    // Submit a task to the thread pool
    public void execute(Runnable task) {
        executorService.execute(task);
    }

    // Shutdown the thread pool (call this when the application exits)
    public void shutdown() {
        executorService.shutdown();
    }
}
