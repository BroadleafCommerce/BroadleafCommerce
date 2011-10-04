package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;

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

    public List<PageFieldData> getFieldDataList();

    public void addFieldData(PageFieldData fieldData);

    public void setFieldDataList(List<PageFieldData> fieldDataList);

    public PageField cloneEntity();

    public String getValue();

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);
}
