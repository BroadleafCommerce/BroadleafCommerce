/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    SupportedFieldType getFieldType();

    void setFieldType(SupportedFieldType fieldType);

    void setFieldType(String fieldType);

    String getFieldTypeVal();

    String getSecurityLevel();

    void setSecurityLevel(String securityLevel);

    Boolean getHiddenFlag();

    void setHiddenFlag(Boolean hiddenFlag);

    String getValidationRegEx();

    void setValidationRegEx(String validationRegEx);

    Integer getMaxLength();

    void setMaxLength(Integer maxLength);

    String getColumnWidth();

    void setColumnWidth(String columnWidth);

    Boolean getTextAreaFlag();

    void setTextAreaFlag(Boolean textAreaFlag);

    Boolean getRequiredFlag();

    void setRequiredFlag(Boolean requiredFlag);

    DataDrivenEnumeration getDataDrivenEnumeration();

    void setDataDrivenEnumeration(DataDrivenEnumeration dataDrivenEnumeration);

    Boolean getAllowMultiples();

    void setAllowMultiples(Boolean allowMultiples);

    String getFriendlyName();

    void setFriendlyName(String friendlyName);

    String getValidationErrorMesageKey();

    void setValidationErrorMesageKey(String validationErrorMesageKey);

    FieldGroup getFieldGroup();

    void setFieldGroup(FieldGroup fieldGroup);

    int getFieldOrder();

    void setFieldOrder(int fieldOrder);

    String getTooltip();

    void setTooltip(String tooltip);

    String getHelpText();

    void setHelpText(String helpText);

    String getHint();

    void setHint(String hint);

    String getAdditionalForeignKeyClass();

    void setAdditionalForeignKeyClass(String className);

}
