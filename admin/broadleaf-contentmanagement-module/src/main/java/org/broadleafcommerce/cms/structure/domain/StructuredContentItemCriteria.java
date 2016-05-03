/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.rule.QuantityBasedRule;

import javax.annotation.Nonnull;

/**
 * Implementations of this interface contain item rule data that is used for targeting
 * <code>StructuredContent</code> items.
 * <br>
 * <br>
 * For example, a <code>StructuredContent</code> item could be setup to only show to user's
 * who have a particular product in their cart.
 *
 * @see org.broadleafcommerce.core.order.service.StructuredContentCartRuleProcessor
 * @author bpolster
 */
public interface StructuredContentItemCriteria extends QuantityBasedRule,MultiTenantCloneable<StructuredContentItemCriteria> {

    /**
     * Returns the parent <code>StructuredContent</code> item to which this
     * field belongs.
     *
     * @return
     */
    @Nonnull
    public StructuredContent getStructuredContent();

    /**
     * Sets the parent <code>StructuredContent</code> item.
     * @param structuredContent
     */
    public void setStructuredContent(@Nonnull StructuredContent structuredContent);

    /**
     * Builds a copy of this item.   Used by the content management system when an
     * item is edited.
     *
     * @return a copy of this item
     */
    @Nonnull
    public StructuredContentItemCriteria cloneEntity();
    
}
