package org.broadleafcommerce.common.enumeration.domain;

/**
 * @author Jeff Fischer
 */
public interface DataDrivenEnumerationValue {
    String getDisplay();

    void setDisplay(String display);

    public Boolean getVisible();

    public void setVisible(Boolean visible);

    Long getId();

    void setId(Long id);

    String getKey();

    void setKey(String key);

    DataDrivenEnumeration getType();

    void setType(DataDrivenEnumeration type);
}
