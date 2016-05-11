/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface iFieldMetadata extends Serializable {

    String[] getAvailableToTypes();

    void setAvailableToTypes(String[] availableToTypes);

    String getInheritedFromType();

    void setInheritedFromType(String inheritedFromType);

    Boolean getExcluded();

    void setExcluded(Boolean excluded);

    String getShowIfProperty();

    void setShowIfProperty(String showIfProperty);

    String getCurrencyCodeField();

    void setCurrencyCodeField(String currencyCodeField);

    String getFriendlyName();

    void setFriendlyName(String friendlyName);

    String getSecurityLevel();

    void setSecurityLevel(String securityLevel);

    Integer getOrder();

    void setOrder(Integer order);

    String getTargetClass();

    void setTargetClass(String targetClass);

    String getFieldName();

    void setFieldName(String fieldName);

    String getOwningClassFriendlyName();

    void setOwningClassFriendlyName(String owningClassFriendlyName);

    String getOwningClass();

    void setOwningClass(String owningClass);

    String getPrefix();

    void setPrefix(String prefix);

    Boolean getChildrenExcluded();

    void setChildrenExcluded(Boolean childrenExcluded);

    String getTab();

    void setTab(String tab);

    Integer getTabOrder();

    void setTabOrder(Integer tabOrder);

    FieldMetadata cloneFieldMetadata();
}
