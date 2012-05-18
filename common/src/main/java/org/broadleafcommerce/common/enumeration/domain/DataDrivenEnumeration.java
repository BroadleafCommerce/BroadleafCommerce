package org.broadleafcommerce.common.enumeration.domain;

import java.io.Serializable;

public interface DataDrivenEnumeration extends Serializable {
    
    public Long getId();

    public void setId(Long id);

    public DataDrivenEnumeration getType();

    public void setType(DataDrivenEnumeration type);

    public String getKey();

    public void setKey(String key);

    public String getDisplay();

    public void setDisplay(String display);

    public Boolean getHidden();

    public void setHidden(Boolean hidden);

    public Boolean getModifiable();

    public void setModifiable(Boolean modifiable);

}
