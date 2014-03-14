package org.broadleafcommerce.common.cache;

/**
 * Represents a block of work to execute during a call to
 * {@link org.broadleafcommerce.common.cache.AbstractCacheMissAware#getCachedObject(Class, String, String, PersistentRetrieval, String...)}
 * should a missed cache item not be detected. Should return an instance of the cache miss item type retrieved
 * from the persistent store.
 *
 * @see org.broadleafcommerce.common.cache.AbstractCacheMissAware
 * @author Jeff Fischer
 */
public interface PersistentRetrieval<T> {

    public T retrievePersistentObject();

}
