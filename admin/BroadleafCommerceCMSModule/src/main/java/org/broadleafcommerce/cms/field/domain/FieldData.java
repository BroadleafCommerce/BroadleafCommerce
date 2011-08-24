package org.broadleafcommerce.cms.field.domain;

/**
 * Created by bpolster.
 */
public interface FieldData {
    public Long getId();

    public void setId(Long id);

    public String getValue();

    public void setValue(String value);

    public FieldData cloneEntity();
}
