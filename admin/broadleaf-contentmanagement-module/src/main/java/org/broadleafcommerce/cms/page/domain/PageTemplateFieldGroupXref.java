/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author Kelly Tisdell
 *
 */
public interface PageTemplateFieldGroupXref extends Serializable, MultiTenantCloneable<PageTemplateFieldGroupXref> {

    public void setId(Long id);

    public Long getId();

    public void setPageTemplate(PageTemplate pageTemplate);

    public PageTemplate getPageTemplate();

    public void setFieldGroup(FieldGroup fieldGroup);

    public FieldGroup getFieldGroup();

    public void setGroupOrder(BigDecimal groupOrder);

    public BigDecimal getGroupOrder();

}
