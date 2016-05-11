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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.rule.QuantityBasedRule;

import javax.annotation.Nonnull;

/**
 * Implementations of this interface contain item rule data that is used for targeting
 * <code>Page</code>s.
 * <br>
 * <br>
 * For example, a <code>Page</code>  could be setup to only show to user's
 * who have a particular product in their cart.
 *
 * @see org.broadleafcommerce.core.order.service.PageCartRuleProcessor
 * @author bpolster
 */
public interface PageItemCriteria extends QuantityBasedRule,MultiTenantCloneable<PageItemCriteria> {

    /**
     * Returns the parent <code>Page</code> to which this
     * field belongs.
     *
     * @return
     */
    @Nonnull
    public Page getPage();

    /**
     * Sets the parent <code>Page</code>.
     * @param page
     */
    public void setPage(@Nonnull Page page);

    /**
     * Builds a copy of this item.   Used by the content management system when an
     * item is edited.
     *
     * @return a copy of this item
     */
    @Nonnull
    public PageItemCriteria cloneEntity();
    
}
