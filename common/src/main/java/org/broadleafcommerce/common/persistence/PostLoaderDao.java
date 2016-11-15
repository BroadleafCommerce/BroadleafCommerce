package org.broadleafcommerce.common.persistence;

/**
 * Utility class for working with proxied entities.
 *
 * The {@link DefaultPostLoaderDao} in core delegates functionally to
 * {@link javax.persistence.EntityManager}, while more interesting
 * functionality is provided by the enterprise version.
 *
 * @see DefaultPostLoaderDao
 * @author Nathan Moore (nathanmoore).
 */
public interface PostLoaderDao {
    /**
     * Find the entity by primary key and class, and, if found in
     * the persistence context, return the deproxied version.
     *
     * @param clazz entity class
     * @param id primary key
     * @return deproxied entity or null if not found
     */
    <T> T find(Class<T> clazz, Object id);
}
