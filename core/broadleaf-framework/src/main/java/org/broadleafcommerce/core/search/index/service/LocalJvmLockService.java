package org.broadleafcommerce.core.search.index.service;

import java.util.HashSet;

/**
 * This component allows for the tracking of a lock reference for a single process ID or reference.
 * 
 * By definition, this component is non-distributed and only works in a single JVM.  Callers should 
 * acquire a lock by calling the lock method, and then, in a finally block, call the unlock method:
 * 
 * Object processId = FieldEntity.PRODUCT;
 * boolean obtained = false;
 * if (! lockService.isLocked(processId)) {
 *   try {
 *     lockService.lock(processId);
 *     obtained = true;
 *     ... //Do work...
 *   
 *   } catch (LockException e) {
 *     //Log the error and return or retry since this lock is already in use.
 *   } finally {
 *     if (obtained) {
 *       try {
 *         lockService.unlock(processId);
 *       } catch (LockException e) {
 *         //Log the error.  This should not happen unless another thread unlocked this.
 *       }
 *     }
 *   }
 * }
 * 
 * 
 * @author Kelly Tisdell
 *
 */
public class LocalJvmLockService implements LockService {
    
    protected static final HashSet<String> LOCKS = new HashSet<>();

    @Override
    public boolean isLocked(String reference) {
        if (reference == null) {
            throw new NullPointerException("The lock reference cannot be null.");
        }
        synchronized (LOCKS) {
            return LOCKS.contains(reference);
        }
    }

    @Override
    public void lock(String reference) throws LockException {
        if (reference == null) {
            throw new NullPointerException("The lock reference cannot be null.");
        }
        synchronized (LOCKS) {
            if (isLocked(reference)) {
                throw new LockException("There was already a lock for reference " + reference);
            }
            
            LOCKS.add(reference);
        }
    }

    @Override
    public void unlock(String reference) throws LockException {
        if (reference == null) {
            throw new NullPointerException("The lock reference cannot be null.");
        }
        synchronized (LOCKS) {
            if (! isLocked(reference)) {
                throw new LockException("There was no lock for reference " + reference);
            }
            
            LOCKS.remove(reference);
        }
        
    }

    @Override
    public final boolean isDistributed() {
        //By defininition this is non-distributed.
        return false;
    }

}
