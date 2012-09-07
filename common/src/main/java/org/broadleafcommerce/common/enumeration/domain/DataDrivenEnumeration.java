package org.broadleafcommerce.common.enumeration.domain;

import java.io.Serializable;
import java.util.List;

public interface DataDrivenEnumeration extends Serializable {
    
    public Long getId();

    public void setId(Long id);

    public String getKey();

    public void setKey(String key);

    public String getDisplay();

    public void setDisplay(String display);

    public Boolean getModifiable();

    public void setModifiable(Boolean modifiable);

    public List<DataDrivenEnumerationValue> getOrderItems();

    public void setOrderItems(List<DataDrivenEnumerationValue> orderItems);

}
