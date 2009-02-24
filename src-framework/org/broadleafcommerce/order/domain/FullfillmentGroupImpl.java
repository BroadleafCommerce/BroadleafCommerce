package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "BLC_FULLFILLMENT_GROUP")
public class FullfillmentGroupImpl implements FullfillmentGroup, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @OneToMany(mappedBy = "id", targetEntity = FullfillmentGroupItemImpl.class)
    @MapKey(name = "id")
    private List<FullfillmentGroupItem> fullfillmentGroupItems;

    @ManyToOne(targetEntity = AddressImpl.class)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @Column(name = "METHOD")
    private String method;

    @Column(name = "COST")
    private BigDecimal cost;

    @Column(name = "TYPE")
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
