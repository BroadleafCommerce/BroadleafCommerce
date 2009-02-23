package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.broadleafcommerce.profile.domain.Address;

public class BroadleafFullfillmentGroup implements FullfillmentGroup, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long orderId;

    private String referenceNumber;

    private List<FullfillmentGroupItem> fullfillmentGroupItems;

    private Address address;

    private String method;

    private BigDecimal cost;

    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public List<FullfillmentGroupItem> getFullfillmentGroupItems() {
        return fullfillmentGroupItems;
    }

    public void setFullfillmentGroupItems(List<FullfillmentGroupItem> fullfillmentGroupItems) {
        this.fullfillmentGroupItems = fullfillmentGroupItems;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String fullfillmentMethod) {
        this.method = fullfillmentMethod;
    }

    @Override
    public BigDecimal getCost() {
        return cost;
    }

    @Override
    public void setCost(BigDecimal fullfillmentCost) {
        this.cost = fullfillmentCost;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
