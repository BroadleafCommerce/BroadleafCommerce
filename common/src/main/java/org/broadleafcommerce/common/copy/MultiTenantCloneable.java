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
package org.broadleafcommerce.common.copy;

/**
 * Describes an entity (or @Embeddable) that is able to be cloned for the purpose of multiple tenancy.
 *
 * @author Jeff Fischer
 */
public interface MultiTenantCloneable<T> {

    /**
     * Clone this entity for the purpose of multiple tenancy. Note, extending classes should follow this pattern:
     * </p>
     * <code>
     * public CreateResponse&lt;MyClass&gt; createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
     *      CreateResponse&lt;MyClass&gt; createResponse = super.createOrRetrieveCopyInstance(context);
     *      if (createResponse.isAlreadyPopulated()) {
     *          return createResponse;
     *      }
     *      MyClass myClone = createResponse.getClone();
     *
     *      //copy extended field values on myClone here
     *
     *      return createResponse;
     * }
     * </code>
     * </p>
     * Support should also be added for @Embeddable classes that contribute fields (collections or basic) to a cloneable entity:
     * </p>
     * <code>
     * public CreateResponse&lt;G extends MyClass&gt; createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
     *      CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
     *      MyClass myClone = createResponse.getClone();
     *
     *      //copy extended field values on myClone here
     *
     *      return createResponse;
     * }
     * </code>
     *
     * @param context a context object providing persistence and library functionality for copying entities
     * @return the resulting copy container, possibly already persisted
     * @throws CloneNotSupportedException if there's a problem detected with the cloning configuration
     */
    public <G extends T> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException;
}
