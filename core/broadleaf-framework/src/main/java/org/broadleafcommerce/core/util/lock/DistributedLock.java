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
