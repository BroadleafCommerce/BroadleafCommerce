package org.broadleafcommerce.order.domain;

import java.math.BigDecimal;
import java.util.List;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.type.FulfillmentGroupType;

public interface FulfillmentGroup {

    public Long getId();

    public void setId(Long id);

    public Long getOrderId();

    public void setOrderId(Long orderId);

    public Address getAddress();

    public void setAddress(Address address);

    public List<FulfillmentGroupItem> getFulfillmentGroupItems();

    public void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems);

    public String getMethod();

    public void setMethod(String fulfillmentMethod);

    public BigDecimal getCost();

    public void setCost(BigDecimal fulfillmentCost);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    public FulfillmentGroupType getType();

    public void setType(FulfillmentGroupType type);
}
