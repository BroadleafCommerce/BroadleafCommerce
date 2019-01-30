/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;

import java.util.Map;

/**
 * Create a production duplicate of an entity. In the case of the enterprise module, the entity to be duplicated may not
 * be a sandbox copy. In the case of the multitenant module, the entity to be duplicated must not be referenced in a derived
 * catalog (apart from a standard site override) and must not be owned by another site.
 * </p>
 * The feature must first be enabled before use. To enable, add the following property to your Spring property file:
 * </p>
 * {@code admin.entity.duplication.isactive=true}
 *
 * @author Jeff Fischer
 */
public interface EntityDuplicator {

    /**
     * Validate whether or not this feature is enabled and whether or not the passed entity params are valid for duplication.
     *
     * @param entityClass
     * @param id
     * @return
     */
    boolean validate(Class<?> entityClass, Long id);

    /**
     * Validate whether or not this feature is enabled and whether or not the passed entity params are valid for duplication.
     *
     * @param entity
     * @return
     */
    boolean validate(Object entity);

    /**
     * Create a production duplicate of the entity specified in the params. The is the most oft used copy method.
     *
     * @param entityClass the class for the entity
     * @param id the primary key
     * @param copyHints hints used to fine tune copying - generally support for hints is included in {@link MultiTenantCloneable#createOrRetrieveCopyInstance(org.broadleafcommerce.common.copy.MultiTenantCopyContext)} implementations.
     * @param modifiers list of modifications to perform to the resulting duplicate prior to persistence
     * @param <T> the entity type
     * @return the duplicated entity
     */
    <T> T copy(Class<T> entityClass, Long id, Map<String, String> copyHints, EntityDuplicateModifier... modifiers);

    /**
     * Create a production duplicate of the entity specified in the params. The is the least oft used copy method.
     *
     * @param context prepopulated copy context that is setup for catalog and/or site
     * @param entity the instance to duplicate
     * @param copyHints hints used to fine tune copying - generally support for hints is included in {@link MultiTenantCloneable#createOrRetrieveCopyInstance(org.broadleafcommerce.common.copy.MultiTenantCopyContext)} implementations.
     * @param modifiers list of modifications to perform to the resulting duplicate prior to persistence
     * @param <T> the entity type
     * @return the duplicated entity
     */
    <T> T copy(MultiTenantCopyContext context, MultiTenantCloneable<T> entity, Map<String, String> copyHints, EntityDuplicateModifier... modifiers);

}
