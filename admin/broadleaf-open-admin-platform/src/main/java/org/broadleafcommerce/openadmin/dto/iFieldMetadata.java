/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
