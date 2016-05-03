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
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;

import java.io.Serializable;
import java.util.Date;

public interface ModuleConfiguration extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getModuleName();

    public void setModuleName(String name);

    public void setActiveStartDate(Date startDate);

    public Date getActiveStartDate();

    public void setActiveEndDate(Date startDate);

    public Date getActiveEndDate();

    public void setIsDefault(Boolean isDefault);

    public Boolean getIsDefault();
    
    public boolean isDefault();

    public void setPriority(Integer priority);

    public Integer getPriority();

    public ModuleConfigurationType getModuleConfigurationType();

    public void setAuditable(Auditable auditable);

    public Auditable getAuditable();

}
