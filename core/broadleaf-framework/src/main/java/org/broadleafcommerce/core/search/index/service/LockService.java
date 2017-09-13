package org.broadleafcommerce.core.search.index.service;

public interface LockService {

    /**
     * Checks to see if a lock has already been acquired.
     * 
     * @param reference
     * @return
     */
    public boolean isLocked(String reference);
    
    /**
     * Attempts to create a lock based on the seed.  This does not synchronize or otherwise block threads.  It just keeps 
     * track of the fact that there is a lock for the referenced parameter. 
     * 
     * Throws a LockException if a lock could not be obtained.
     * 
     * Ensure that the reference passed in is not null, and properly implements the equals and hashCode methods.
     * 
     * @param reference
     * @throws LockException
     */
    public void lock(String reference) throws LockException;
    
    /**
     * Attempts to remove a lock.  The lock value is the value returned from the lock method.
     * 
     * Throws a LockException if a lock could not be removed.
     * 
     * @param reference
     * @return
     * @throws LockException
     */
    public void unlock(String reference) throws LockException;
    
    /**
     * Indicates if this LockService supports distributed locks (e.g. via a database, cache, Zookeeper, etc.).
     * @return
     */
    public boolean isDistributed();
}
