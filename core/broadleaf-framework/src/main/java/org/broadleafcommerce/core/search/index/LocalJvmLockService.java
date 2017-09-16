package org.broadleafcommerce.core.search.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * This component allows for the tracking of a lock reference for a single process ID or reference.
 * 
 * By definition, this component is non-distributed and only works in a single JVM.  Callers should 
 * acquire a lock by calling the lock method, and then, in a finally block, call the unlock method:
 * 
 * Object processId = FieldEntity.PRODUCT;
 * boolean obtained = false;
 * Serializable key = null;
 * if (! lockService.isLocked(processId)) {
 *   try {
 *     key = lockService.lock(processId);
 *     obtained = true;
 *     ... //Do work...
 *   
 *   } catch (LockException e) {
 *     //Log the error and return or retry since this lock is already in use.
 *   } finally {
 *     if (obtained) {
 *       try {
 *         lockService.unlock(key, processId);
 *       } catch (LockException e) {
 *         //Log the error. //This should not happen unless the underlying key/reference relationship has been removed....
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
    
    private static final HashMap<Serializable, Serializable> LOCK_MAP = new HashMap<>();

    @Override
    public boolean isLocked(Serializable reference) {
        if (reference == null) {
            throw new NullPointerException("The lock reference cannot be null.");
        }
        synchronized (LOCK_MAP) {
            return LOCK_MAP.values().contains(reference);
        }
    }
    
    @Override
    public boolean isKeyValid(Serializable key, Serializable reference) {
        if (key == null || reference == null) {
            throw new NullPointerException("Neither the key nor reference can be null.");
        }
        synchronized (LOCK_MAP) {
            if (LOCK_MAP.containsKey(key) && LOCK_MAP.get(key).equals(reference)) {
                return true;
            }
            return false;
        }
    }

    @Override
    public Serializable lock(Serializable reference) throws LockException {
        if (reference == null) {
            throw new NullPointerException("The lock reference cannot be null.");
        }
        synchronized (LOCK_MAP) {
            if (isLocked(reference)) {
                throw new LockException("There was already a lock for reference " + reference);
            }
            
            Serializable key = generateKey(reference);
            LOCK_MAP.put(key, reference);
            return key;
        }
    }

    @Override
    public void unlock(Serializable key, Serializable reference) throws LockException {
        if (key == null || reference == null) {
            throw new NullPointerException("Neither the key nor reference can be null.");
        }
        synchronized (LOCK_MAP) {
            if (! isKeyValid(key, reference)) {
                throw new LockException("There was no lock for key " + key + " and reference " + reference);
            }
            
            LOCK_MAP.remove(key);
        }
        
    }

    @Override
    public final boolean isDistributed() {
        //By defininition this is non-distributed.
        return false;
    }
    
    /**
     * This can be overridden.  The default behavior is to generate a UUID.  This MUST NOT return null.
     * @param reference
     * @return
     */
    protected Serializable generateKey(Serializable reference) {
        //Default is just to use a UUID
        return UUID.randomUUID().toString();
    }
}
