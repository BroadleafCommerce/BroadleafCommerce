package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public interface PageFieldData extends Serializable {
    
    public Long getId();

    public void setId(Long id);

    public String getValue();

    public void setValue(String value);

    public PageFieldData cloneEntity();

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);
}
