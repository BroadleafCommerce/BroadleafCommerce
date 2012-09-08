package org.broadleafcommerce.common.enumeration.domain;

/**
 * @author Jeff Fischer
 */
public interface DataDrivenEnumerationValue {
    String getDisplay();

    void setDisplay(String display);

    public Boolean getHidden();

    public void setHidden(Boolean hidden);

    Long getId();

    void setId(Long id);

    String getKey();

    void setKey(String key);

    DataDrivenEnumeration getType();

    void setType(DataDrivenEnumeration type);
}
