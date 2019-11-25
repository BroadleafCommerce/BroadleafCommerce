package org.broadleafcommerce.common.util;

/**
 * Provides a mechanism to allow for retriable logic.
 * 
 * @author Kelly Tisdell
 *
 */
public class RetryableOperationUtil {

    public static <R> R executeRetryableOperation(final AbstractRetryableOperation<R, Exception> operation) throws Exception {
        int count = 0;
        long localWaitTime = operation.getWaitTime();
        while (true) {
            try {
                return operation.execute();
            } catch (Exception e) {
                if (count == operation.getRetries()) {
                    throw e;
                }
                
                if (operation.getThrowablesToIgnore() != null && operation.getThrowablesToIgnore().length > 0) {
                    for (int i = 0; i < operation.getThrowablesToIgnore().length; i++) {
                        if (operation.getThrowablesToIgnore()[i].isAssignableFrom(e.getClass())) {
                            throw e;
                        }
                    }
                }
                
                count++;
                
                try {
                    if (localWaitTime > 0L) {
                        Thread.sleep(localWaitTime);
                    }
                    if (operation.isAdditiveWaitTtimes()) {
                        localWaitTime += operation.getWaitTime();
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
                
            }
        }
    }
}
