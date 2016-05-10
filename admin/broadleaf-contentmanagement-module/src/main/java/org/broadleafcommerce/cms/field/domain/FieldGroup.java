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
package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bpolster.
 */
public interface FieldGroup extends Serializable, MultiTenantCloneable<FieldGroup> {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public Boolean getInitCollapsedFlag();

    public void setInitCollapsedFlag(Boolean initCollapsedFlag);

    public List<FieldDefinition> getFieldDefinitions();

    public void setFieldDefinitions(List<FieldDefinition> fieldDefinitions);

    public Boolean isMasterFieldGroup();

    public void setIsMasterFieldGroup(Boolean isMasterFieldGroup);
}
