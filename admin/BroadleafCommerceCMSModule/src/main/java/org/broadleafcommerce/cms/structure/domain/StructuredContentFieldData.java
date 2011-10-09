package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public interface StructuredContentFieldData extends Serializable {
    
    public Long getId();

    public void setId(Long id);

    public String getValue();

    public void setValue(String value);

    public StructuredContentFieldData cloneEntity();

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);
}
