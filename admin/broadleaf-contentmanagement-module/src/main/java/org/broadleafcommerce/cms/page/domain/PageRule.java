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
import org.broadleafcommerce.common.rule.SimpleRule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implementations hold the values for a rule used to determine if a <code>Page</code>
 * should be displayed.
 * <br>
 * The rule is represented as a valid MVEL string.    The Content Management System by default
 * is able to process rules based on the current customer, product,
 * {@link org.broadleafcommerce.common.TimeDTO time}, or {@link org.broadleafcommerce.common.RequestDTO request}
 *
 * @see org.broadleafcommerce.cms.web.structure.DisplayContentTag
 * @see org.broadleafcommerce.cms.structure.service.PageServiceImpl#evaluateAndPriortizePages(java.util.List, int, java.util.Map)
 * @author bpolster
 *
 */
public interface PageRule extends SimpleRule,MultiTenantCloneable<PageRule> {

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    @Nullable
    public Long getId();

    /**
     * Sets the primary key.
     *
     * @param id the new primary key
     */
    public void setId(@Nullable Long id);

    /**
     * Builds a copy of this content rule.   Used by the content management system when an
     * item is edited.
     *
     * @return a copy of this rule
     */
    @Nonnull
    public PageRule cloneEntity();

}
