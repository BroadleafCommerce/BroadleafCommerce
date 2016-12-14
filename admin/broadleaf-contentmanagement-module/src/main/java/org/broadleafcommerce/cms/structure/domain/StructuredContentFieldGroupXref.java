/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;



/**
 * <p>
 * XREF entity between a {@link StructuredContentFieldTemplate} and a {@link FieldGroup}
 * 
 * <p>
 * This was created to facilitate specifying ordering for the {@link FieldGroup}s within a {@link StructuredContentFieldTemplate}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface StructuredContentFieldGroupXref extends Serializable, MultiTenantCloneable<StructuredContentFieldGroupXref> {

    /**
     * The order that this field group should have within this template
     */
    Integer getGroupOrder();

    void setGroupOrder(Integer groupOrder);

    StructuredContentFieldTemplate getTemplate();

    void setTemplate(StructuredContentFieldTemplate template);

    FieldGroup getFieldGroup();
    
    void setFieldGroup(FieldGroup fieldGroup);
    
}
