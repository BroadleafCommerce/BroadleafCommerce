/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    public void setPriority(Integer priority);

    public Integer getPriority();

    public ModuleConfigurationType getModuleConfigurationType();

    public void setAuditable(Auditable auditable);

    public Auditable getAuditable();

}
