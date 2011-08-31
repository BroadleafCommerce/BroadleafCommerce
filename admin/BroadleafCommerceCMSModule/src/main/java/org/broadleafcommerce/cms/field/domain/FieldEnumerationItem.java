package org.broadleafcommerce.cms.field.domain;

/**
 * Created by jfischer
 */
public interface FieldEnumerationItem {
    FieldEnumeration getFieldEnumeration();

    void setFieldEnumeration(FieldEnumeration fieldEnumeration);

    int getFieldOrder();

    void setFieldOrder(int fieldOrder);

    String getFriendlyName();

    void setFriendlyName(String friendlyName);

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);
}
