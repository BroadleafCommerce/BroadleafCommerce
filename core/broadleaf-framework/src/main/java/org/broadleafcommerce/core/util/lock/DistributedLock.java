/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.util.lock;

import org.springframework.core.env.Environment;

import java.util.concurrent.locks.Lock;

/**
 * Interface to define a lock as distributed and safe to use across nodes or JVMs.
 * 
 * @author Kelly Tisdell
 *
 */
public interface DistributedLock extends Lock {
    
    /**
     * Default property name to determine, globally, whether this environment (JVM) can obtain a lock of this type.
     */
    public static final String GLOBAL_ENV_CAN_OBTAIN_LOCK_PROPERTY_NAME = DistributedLock.class.getName() + ".canParticipate";
    
    /**
     * Indicates if the current thread, JVM, or environment can use this lock.  Callers may call this method to know whether they can obtain a lock.
     * Internally, implementations must continue to respect the normal lock semantics provided by the {@link Lock} interface.  For example if this method 
     * returns false and someone calls {@link DistributedLock#lockInterruptibly()}, then the thread must block interruptably, but should never provide a lock. 
     * Similarly, a call to {@link DistributedLock#tryLock(5000L, TimeUnit.MILLISECONDS)}, then the thread must block for 5000 milliseconds, and then return false.
     * 
     * This allows someone to determine if a Thread, a JVM, an environment, etc. can ever obtain a lock.  Implementations are typically driven by a property, e.g. provided by 
     * Spring's {@link Environment} object.
     * 
     * @return
     */
    public boolean canParticipate();
    
    /**
     * Indicates if the current thread holds the lock.
     * 
     * @return
     */
    public boolean currentThreadHoldsLock();

    /**
     * RuntimeException to identify that there was an issue obtaining or otherwise releasing a distributed lock.
     * 
     * @author Kelly Tisdell
     *
     */
    public class DistributedLockException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public DistributedLockException() {
            super();
        }

        public DistributedLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        public DistributedLockException(String message, Throwable cause) {
            super(message, cause);
        }

        public DistributedLockException(String message) {
            super(message);
        }

        public DistributedLockException(Throwable cause) {
            super(cause);
        }
        
        
    }
}
