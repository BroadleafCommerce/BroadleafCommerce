package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.cms.field.domain.FieldData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bpolster.
 */
public interface PageField extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getFieldKey();

    public void setFieldKey(String fieldKey);

    public Page getPage();

    public void setPage(Page page);

    public List<FieldData> getFieldDataList();

    public void addFieldData(FieldData fieldData);

    public void setFieldDataList(List<FieldData> fieldDataList);

    public PageField cloneEntity();
}
