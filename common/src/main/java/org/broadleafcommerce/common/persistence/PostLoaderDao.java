/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

    /**
     * If within the context of a sandbox, return the sandbox entity by primary key and class.
     *
     * This purposefully uses the Entity Manager in order to trigger the hibernate filters.
     *
     * @param clazz
     * @param id
     * @param <T>
     * @return
     */
    <T> T findSandboxEntity(Class<T> clazz, Object id);

}
