import java.util.concurrent.Callable;
public class RetryUtil {
    /**
     * Executes the given task with exponential backoff retry. 
     *
     *
     @param task         The task to execute
     *
     *
     *
     *
     *
     @param maxRetries   Maximum number of retry attempts
     @param baseDelayMs  Base delay in milliseconds (doubles each retry)
     @param maxDelayMs   Maximum delay in milliseconds (cap)
     @return The result of the task
     @throws Exception If all retries fail, throws the last exception
     */
    public static <T> T executeWithRetry(
            Callable<T> task,
            int maxRetries,
            long baseDelayMs,
            long maxDelayMs) throws Exception {
        for(int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return task.call();
            } catch (Exception e) {
                if(attempt == maxRetries - 1) throw e;
                long delay = Math.min(baseDelayMs * (1L << attempt), maxDelayMs);
                Thread.sleep(delay);
            }
        }
        return null;
    }
} 