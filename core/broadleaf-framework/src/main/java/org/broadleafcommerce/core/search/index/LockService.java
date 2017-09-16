package org.broadleafcommerce.core.search.index;

import java.io.Serializable;

/**
 * Basic LockService interface, specifically to be used by the Index processes to ensure that no 
 * other threads or nodes can start a process.
 * 
 * @author Kelly Tisdell
 *
 */
public interface LockService {

    /**
     * Checks to see if a lock has already been acquired for the referenced object.
     * 
     * @param reference
     * @return
     */
    public boolean isLocked(Serializable reference);
    
    /**
     * Attempts to create a lock based on the seed.  This does not synchronize or otherwise block threads.  It just keeps 
     * track of the fact that there is a lock for the referenced parameter. The return value is the key to unlock the lock.
     * 
     * Throws a LockException if a lock could not be obtained.
     * 
     * Ensure that the reference passed in is not null, and properly implements the equals and hashCode methods.
     * 
     * @param reference
     * @return
     * @throws LockException
     */
    public Serializable lock(Serializable reference) throws LockException;
    
    /**
     * Indicates if the provided key will unlock a lock for the provided reference.
     * 
     * @param key
     * @return
     */
    public boolean isKeyValid(Serializable key, Serializable reference);
    
    /**
     * Attempts to remove a lock.  The key is the value returned from the lock method and the reference is 
     * the original reference used to obtain the lock.
     * 
     * Throws a LockException if a lock could not be removed.
     * 
     * @param key
     * @return
     * @throws LockException
     */
    public void unlock(Serializable key, Serializable reference) throws LockException;
    
    /**
     * Indicates if this LockService supports distributed locks (e.g. via a database, cache, Zookeeper, etc.).
     * @return
     */
    public boolean isDistributed();
}
