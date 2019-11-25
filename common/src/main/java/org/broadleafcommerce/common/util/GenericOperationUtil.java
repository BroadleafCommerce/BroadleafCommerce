package org.broadleafcommerce.common.util;

/**
 * Provides a mechanism to allow for retriable logic.
 * 
 * @author Kelly Tisdell
 *
 */
public class GenericOperationUtil {
    
    public static <R> R executeRetryableOperation(final GenericOperation<R> operation, 
            final int retries, final long waitTime, final boolean isWaitTimesAdditive, final Class<? extends Exception>[] noRetriesForException) throws Exception {
        int tries = 0;
        long localWaitTime = waitTime;
        while (true) {
            try {
                return operation.execute();
            } catch (Exception e) {
                if (tries == retries) {
                    throw e;
                }
                
                if (noRetriesForException != null && noRetriesForException.length > 0) {
                    for (int i = 0; i < noRetriesForException.length; i++) {
                        if (noRetriesForException[i].isAssignableFrom(e.getClass())) {
                            throw e;
                        }
                    }
                }
                
                tries++;
                
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
