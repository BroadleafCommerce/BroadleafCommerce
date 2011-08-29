package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.cms.page.domain.PageField;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public interface FieldData extends Serializable {
    
    public Long getId();

    public void setId(Long id);

    public String getValue();

    public void setValue(String value);

    public FieldData cloneEntity();

    public PageField getPageField();

    public void setPageField(PageField pageField);

    public int getFieldOrder();

    public void setFieldOrder(int fieldOrder);

}
