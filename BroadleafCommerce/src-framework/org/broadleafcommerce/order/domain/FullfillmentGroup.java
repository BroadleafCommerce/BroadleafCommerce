package org.broadleafcommerce.order.domain;

import java.math.BigDecimal;
import java.util.List;

import org.broadleafcommerce.profile.domain.Address;

public interface FullfillmentGroup {

    public Long getId();

    public void setId(Long id);

    public Long getOrderId();

    public void setOrderId(Long orderId);

    public Address getAddress();

    public void setAddress(Address address);

    public List<FullfillmentGroupItem> getFullfillmentGroupItems();

    public void setFullfillmentGroupItems(List<FullfillmentGroupItem> fullfillmentGroupItems);

    public String getMethod();

    public void setMethod(String fullfillmentMethod);

    public BigDecimal getCost();

    public void setCost(BigDecimal fullfillmentCost);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    public String getType();

    public void setType(String type);

}
