package org.broadleafcommerce.openadmin.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface iFieldMetadata extends IsSerializable, Serializable {
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
