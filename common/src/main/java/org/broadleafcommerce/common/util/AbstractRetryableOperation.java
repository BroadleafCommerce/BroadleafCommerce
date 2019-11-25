package org.broadleafcommerce.common.util;

import org.springframework.util.Assert;

/**
 * Operation that may be retry-able.  See {@link RetryableOperationUtil}
 * @author Kelly Tisdell
 *
 * @param <R>
 * @param <T>
 */
public abstract class AbstractRetryableOperation<R, T extends Throwable> implements GenericOperation<R, T> {
    
    private final int retries;
    private final long waitTime;
    private final boolean additiveWaitTtimes;
    private final Class<? extends Throwable>[] throwablesToIgnore;
    
    public AbstractRetryableOperation(int retries, long waitTime, boolean additiveWaitTimes) {
        this(retries, waitTime, additiveWaitTimes, null);
    }
    
    public AbstractRetryableOperation(int retries, long waitTime, boolean additiveWaitTimes, Class<? extends Throwable>[] throwablesToIgnore) {
        Assert.isTrue(retries >= 0, "Wait times must be greater than or equal to zero.");
        Assert.isTrue(waitTime >= 0L, "");
        this.retries = retries;
        this.waitTime = waitTime;
        this.additiveWaitTtimes = additiveWaitTimes;
        this.throwablesToIgnore = throwablesToIgnore;
    }
    
    public int getRetries() {
        return retries;
    }
    
    public long getWaitTime() {
        return waitTime;
    }
    
    public boolean isAdditiveWaitTtimes() {
        return additiveWaitTtimes;
    }
    
    public Class<? extends Throwable>[] getThrowablesToIgnore() {
        return throwablesToIgnore;
    }
    
}
