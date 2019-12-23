/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.util;

/**
 * Provides a mechanism to allow for retriable logic.
 * 
 * @author Kelly Tisdell
 *
 */
public class GenericOperationUtil {
    
    /**
     * Executes the provided operation up to 5 times if there are exceptions, waiting an additive 100 ms between tries.
     * 
     * Total wait time, assuming no successful iterations, will be 1500 milliseconds (100 + 200 + 300 + 400 + 500).
     * 
     * This will never retry for an {@link InterruptedException}.
     * 
     * @param operation
     * @return
     * @throws Exception
     */
    public static <R> R executeRetryableOperation(final GenericOperation<R> operation) throws Exception {
        return executeRetryableOperation(operation, 5, 100L, true, null);
    }
    
    /**
     * Executes the provided operation up to 5 times if there are exceptions, waiting an additive 100 ms between tries.
     * 
     * Total wait time, assuming no successful iterations, will be 1500 milliseconds (100 + 200 + 300 + 400 + 500).
     * 
     * This will never retry for an {@link InterruptedException}.
     * 
     * @param operation
     * @param noRetriesForException
     * @return
     * @throws Exception
     */
    public static <R> R executeRetryableOperation(final GenericOperation<R> operation, 
            final Class<? extends Exception>[] noRetriesForException) throws Exception {
        return executeRetryableOperation(operation, 5, 100L, true, noRetriesForException);
    }
    
    /**
     * Executes the provided operation up to as many times as the retries argument.  The method will return upon successful completion.  However, it will retry up to 
     * <code>retries</code> times.  The wait time is the amount of time that this method will wait between tries.  If <code>isWaitTimesAdditive</code>, then the waitTime parameter 
     * will be multiplied by the current retry iteration each time.  Otherwise, the waitTime will not change between tries.
     * 
     * If retries == 5, waitTime == 100, and isWaitTimesAdditive == false, then the total wait time, assuming no successful iterations, will be 500 milliseconds (100 + 100 + 100 + 100 + 100).
     * If retries == 5, waitTime == 100, and isWaitTimesAdditive == true, then the total wait time, assuming no successful iterations, will be 1500 milliseconds (100 + 200 + 300 + 400 + 500).
     * 
     * This will never retry for an {@link InterruptedException}.
     * 
     * @param operation
     * @param retries
     * @param waitTime
     * @param isWaitTimesAdditive
     * @param noRetriesForException
     * @return
     * @throws Exception
     */
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
                
                if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                    Thread.currentThread().interrupt();
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
