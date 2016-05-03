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
