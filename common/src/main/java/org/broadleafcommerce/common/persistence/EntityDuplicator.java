/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
 * <p>
 *      Creates a production duplicate of an entity. In the case of the enterprise module, the 
 *      entity to be duplicated may not be a sandbox copy. In the case of the multitenant module, 
 *      the entity to be duplicated must not be referenced in a derived catalog (apart from a 
 *      standard site override) and must not be owned by another site.
 * </p>
 * </p>
 *      The feature must first be enabled before use. To enable, add the following property to your 
 *      Spring property file: {@code admin.entity.duplication.isactive=true}. Furthermore, 
 *      for any entity you wish to be able to duplicate, you must implement an
 *      {@link EntityDuplicationHelper}. Consider extending {@link AbstractEntityDuplicationHelper} 
 *      because it implements {@link EntityDuplicationHelper#getCopyHints()} and 
 *      {@link EntityDuplicationHelper#addCopyHint(String, String)} for you. 
 * </p>
 * <p>
 *     CopyHints can be added to change the behavior of 
 *     {@link MultiTenantCloneable#createOrRetrieveCopyInstance(MultiTenantCopyContext)} for a
 *     specific entity. See {@code org.broadleafcommerce.core.offer.service.OfferDuplicateModifier}
 *     and {@code org.broadleafcommerce.core.offer.domain.OfferImpl} for an example.
 * </p>
 *
 * @author Jeff Fischer
 */
public interface EntityDuplicator {

    /**
     * Validate whether or not this feature is enabled and whether the passed entity params are 
     * valid for duplication.
     *
     * @param entityClass
     * @param id
     *
     * @return
     */
    boolean validate(Class<?> entityClass, Long id);

    /**
     * Validate whether or not this feature is enabled and whether or not the passed entity params 
     * are valid for duplication.
     *
     * @param entity
     *
     * @return
     */
    boolean validate(Object entity);

    /**
     * @deprecated use {@link #copy(Class, Long)}. Modifiers have been moved to a list bean 
     * to allow easier inclusion (see {@code EntityDuplicationHelpers}) and copy hints can be added 
     * to implementations of {@link EntityDuplicationHelper}s
     */
    @Deprecated
    <T> T copy(Class<T> entityClass,
            Long id,
            Map<String, String> copyHints,
            EntityDuplicateModifier... modifiers);

    /**
     * @deprecated use {@link #copy(MultiTenantCopyContext, MultiTenantCloneable)}.
     * Modifiers have been moved to a list bean 
     * to allow easier inclusion (see {@code EntityDuplicationHelpers}) and copy hints can be added 
     * to implementations of {@link EntityDuplicationHelper}s
     */
    @Deprecated
    <T> T copy(MultiTenantCopyContext context,
            MultiTenantCloneable<T> entity,
            Map<String, String> copyHints,
            EntityDuplicateModifier... modifiers);

    /**
     * Create a production duplicate of the entity specified in the params. 
     * The is the most oft used copy method.
     *
     * @param entityClass the class for the entity
     * @param id          the primary key
     * @param <T>         the entity type
     *
     * @return the duplicated entity
     */
    <T> T copy(Class<T> entityClass, Long id);

    /**
     * Create a production duplicate of the entity specified in the params. 
     * The is the least oft used copy method.
     *
     * @param context   prepopulated copy context that is setup for catalog and/or site
     * @param entity    the instance to duplicate
     * @param <T>       the entity type
     *
     * @return the duplicated entity
     */
    <T> T copy(MultiTenantCopyContext context, MultiTenantCloneable<T> entity);
}
