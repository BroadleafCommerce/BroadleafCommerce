/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumeration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public interface FieldDefinition extends Serializable, MultiTenantCloneable<FieldDefinition> {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public SupportedFieldType getFieldType();

    String getFieldTypeVal();

    public void setFieldType(SupportedFieldType fieldType);

    void setFieldType(String fieldType);

    public String getSecurityLevel();

    public void setSecurityLevel(String securityLevel);

    public Boolean getHiddenFlag();

    public void setHiddenFlag(Boolean hiddenFlag);

    public String getValidationRegEx();

    public void setValidationRegEx(String validationRegEx);

    public Integer getMaxLength();

    public void setMaxLength(Integer maxLength);

    public String getColumnWidth();

    public void setColumnWidth(String columnWidth);

    public Boolean getTextAreaFlag();

    public void setTextAreaFlag(Boolean textAreaFlag);
    
    public Boolean getRequiredFlag();

    public void setRequiredFlag(Boolean requiredFlag);

    public DataDrivenEnumeration getDataDrivenEnumeration();
    
    public void setDataDrivenEnumeration(DataDrivenEnumeration dataDrivenEnumeration);

    public Boolean getAllowMultiples();

    public void setAllowMultiples(Boolean allowMultiples);

    public String getFriendlyName();

    public void setFriendlyName(String friendlyName);

    public String getValidationErrorMesageKey();

    public void setValidationErrorMesageKey(String validationErrorMesageKey);

    public FieldGroup getFieldGroup();

    public void setFieldGroup(FieldGroup fieldGroup);

    public int getFieldOrder();

    public void setFieldOrder(int fieldOrder);

    public String getTooltip();

    public void setTooltip(String tooltip);

    public String getHelpText();

    public void setHelpText(String helpText);

    public String getHint();

    public void setHint(String hint);

    public String getAdditionalForeignKeyClass();

    public void setAdditionalForeignKeyClass(String className);

}
