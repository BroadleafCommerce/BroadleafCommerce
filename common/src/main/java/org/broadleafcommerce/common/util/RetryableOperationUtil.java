package org.broadleafcommerce.common.util;

/**
 * Provides a mechanism to allow for retriable logic.
 * 
 * @author Kelly Tisdell
 *
 */
public class RetryableOperationUtil {
    
    public static <R, T extends Exception> R executeRetryableOperation(final GenericOperation<R,T> operation, 
            final int retries, final long waitTime, final boolean isWaitTimesAdditive, final Class<? extends Exception>[] noRetriesForException) throws Exception {
        int count = 0;
        long localWaitTime = waitTime;
        while (true) {
            try {
                return operation.execute();
            } catch (Exception e) {
                if (count == retries) {
                    throw e;
                }
                
                if (noRetriesForException != null && noRetriesForException.length > 0) {
                    for (int i = 0; i < noRetriesForException.length; i++) {
                        if (noRetriesForException[i].isAssignableFrom(e.getClass())) {
                            throw e;
                        }
                    }
                }
                
                count++;
                
                try {
                    if (waitTime > 0L) {
                        Thread.sleep(localWaitTime);
                    }
                    if (isWaitTimesAdditive) {
                        localWaitTime += waitTime;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
                
            }
        }
    }
}
