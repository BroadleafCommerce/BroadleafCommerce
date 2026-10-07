/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.util.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.GenericOperation;
import org.broadleafcommerce.common.util.GenericOperationUtil;
import org.broadleafcommerce.core.util.dao.LockDao;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

public class ReentrantDistributedDatabaseLock implements DistributedLock {

    private static final Log LOG = LogFactory.getLog(ReentrantDistributedDatabaseLock.class);

    private final ThreadLocal<AtomicInteger> THREAD_LOCK_PERMITS = new ThreadLocal<>();
    private final Object NON_PARTICIPANT_LOCK_MONITOR = new Object();
    private final Object LOCK_MONITOR = new Object();
    private final Environment env;
    private final String lockName;
    private final String lockAccessPropertyName;
    private LockDao lockDao;

    public ReentrantDistributedDatabaseLock(String lockName, Environment env, LockDao lockDao) {
        Assert.notNull(lockName, "The lockName cannot be null.");
        lockName = lockName.trim();
        Assert.hasText(lockName, "The lockName must not be empty and should not contain white spaces.");
        this.lockDao = lockDao;
        this.lockName = lockName;
        this.env = env;
        this.lockAccessPropertyName = DistributedLock.class.getName() + '.' + this.lockName + ".canParticipate";
    }

    @Override
    public void lock() {
        try {
            lockInternally(-1L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DistributedLockException("Thread was interrupted trying to obtain distributed lock from the database.", e);
        }
    }

    @Override
    public void unlock() {
        try {
            synchronized (LOCK_MONITOR) {
                if (!currentThreadHoldsLock()) {
                    throw new DistributedLockException("The current thread did not obtain this lock and therefore cannot unlock it.");
                }

                if (THREAD_LOCK_PERMITS.get().get() > 1) {
                    THREAD_LOCK_PERMITS.get().decrementAndGet();
                    return;
                }

                lockDao.unlock(lockName);

                if (THREAD_LOCK_PERMITS.get().decrementAndGet() < 1) {
                    THREAD_LOCK_PERMITS.remove();
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Database lock was released by " + Thread.currentThread().getName() + ". The lock name was " + lockName);
                }
            }
        } catch (Exception e) {
            LOG.error("An error occurred trying to unlock a distributed lock stored in the database. The lock has not been released and manual intervention may be required. Lock name is: " + lockName, e);
            throw new DistributedLockException("An error occurred trying to unlock a distributed lock stored in the database. The lock has not been released and manual intervention may be required. Lock name is: " + lockName, e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("Thread was interrupted prior to trying to acquire the lock.");
        }
        lockInternally(-1L);
    }

    @Override
    public boolean tryLock() {
        try {
            return lockInternally(0L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        if (time < 0L) {
            throw new IllegalArgumentException("Wait time must be positive.");
        }
        try {
            return lockInternally(TimeUnit.MILLISECONDS.convert(time, unit));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("By default, conditions are not supported with this type of lock.");
    }

    protected boolean lockInternally(final long waitTime) throws InterruptedException {
        if (!canParticipate()) {
            if (waitTime < 0L) {
                synchronized (NON_PARTICIPANT_LOCK_MONITOR) {
                    NON_PARTICIPANT_LOCK_MONITOR.wait();
                }
            } else if (waitTime > 0L) {
                synchronized (NON_PARTICIPANT_LOCK_MONITOR) {
                    NON_PARTICIPANT_LOCK_MONITOR.wait(waitTime);
                }
            }
            return false;
        }

        if (THREAD_LOCK_PERMITS.get() != null) {
            THREAD_LOCK_PERMITS.get().incrementAndGet();
            return true;
        }

        try {
            synchronized (LOCK_MONITOR) {
                long timeToWait = waitTime;
                while (true) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }

                    boolean lockAcquired = GenericOperationUtil.executeRetryableOperation(new GenericOperation<Boolean>() {
                        @Override
                        public Boolean execute() throws Exception {
                            lockDao.lock(lockName);
                            return true;
                        }
                    }, getFailureRetries(), getRetryWaitTime(), isAdditiveWaitTimes(), null);

                    if (lockAcquired) {
                        THREAD_LOCK_PERMITS.set(new AtomicInteger(1));
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Database lock was acquired by " + Thread.currentThread().getName() + ". The lock name is " + lockName);
                        }
                        return true;
                    } else {
                        if (waitTime == 0L) {
                            return false;
                        } else if (waitTime < 0L) {
                            LOCK_MONITOR.wait();
                        } else {
                            long beginWait = System.currentTimeMillis();
                            LOCK_MONITOR.wait(timeToWait);
                            long endWait = System.currentTimeMillis();
                            timeToWait -= (endWait - beginWait);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error occurred trying to obtain a distributed lock from the database.", e);
            if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                throw (InterruptedException) e;
            }
            return false;
        }
    }

    @Override
    public boolean canParticipate() {
        if (getEnvironment() != null) {
            boolean lockNameParticipation = getEnvironment().getProperty(getLockAccessPropertyName(), Boolean.class, true);
            if (lockNameParticipation) {
                return getEnvironment().getProperty(DistributedLock.GLOBAL_ENV_CAN_OBTAIN_LOCK_PROPERTY_NAME, Boolean.class, true);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean currentThreadHoldsLock() {
        synchronized (LOCK_MONITOR) {
            return getCurrentThreadLockPermits() > 0;
        }
    }

    public int getCurrentThreadLockPermits() {
        synchronized (LOCK_MONITOR) {
            if (THREAD_LOCK_PERMITS.get() == null) {
                return 0;
            }
            return THREAD_LOCK_PERMITS.get().get();
        }
    }

    protected Environment getEnvironment() {
        return env;
    }

    protected String getLockName() {
        return lockName;
    }

    protected int getFailureRetries() {
        return 5;
    }

    protected long getRetryWaitTime() {
        return 100L;
    }

    protected boolean isAdditiveWaitTimes() {
        return true;
    }

    protected String getLockAccessPropertyName() {
        return lockAccessPropertyName;
    }

    public LockDao getLockDao() {
        return lockDao;
    }

    public void setLockDao(LockDao lockDao) {
        this.lockDao = lockDao;
    }
}
