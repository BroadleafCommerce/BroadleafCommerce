package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.offer.domain.OfferAuditImpl;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.type.FulfillmentGroupType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_GROUP")
public class FulfillmentGroupImpl implements FulfillmentGroup, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @OneToMany(mappedBy = "id", targetEntity = FulfillmentGroupItemImpl.class)
    @MapKey(name = "id")
    private List<FulfillmentGroupItem> fulfillmentGroupItems;

    @ManyToOne(targetEntity = AddressImpl.class)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @Column(name = "METHOD")
    private String method;

    @Column(name = "RETAIL_PRICE")
    private BigDecimal retailPrice;

    @Column(name = "SALE_PRICE")
    private BigDecimal salePrice;
    
    @Column(name = "PRICE")
    private BigDecimal price;
    
    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private FulfillmentGroupType type;
    
    @OneToMany(mappedBy = "id", targetEntity = OfferImpl.class)
    @MapKey(name = "id")
    private List<Offer> candidateOffers;
    
    @OneToMany(mappedBy = "id", targetEntity = OfferAuditImpl.class)
    @MapKey(name = "id")
    private List<OfferAudit> appliedOffers;
    

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

    public List<FulfillmentGroupItem> getFulfillmentGroupItems() {
        return fulfillmentGroupItems;
    }

    public void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems) {
        this.fulfillmentGroupItems = fulfillmentGroupItems;
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
    public void setMethod(String fulfillmentMethod) {
        this.method = fulfillmentMethod;
    }

    @Override
    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    @Override
    public void setRetailPrice(BigDecimal fulfillmentCost) {
        this.retailPrice = fulfillmentCost;
    }

    @Override
    public FulfillmentGroupType getType() {
        return type;
    }

    @Override
    public void setType(FulfillmentGroupType type) {
        this.type = type;
    }
    
	@Override
	public void addCandidateOffer(Offer offer) {
		candidateOffers.add(offer);
	}

	@Override
	public List<OfferAudit> getAppliedOffers() {
		return appliedOffers;
	}

	@Override
	public List<Offer> getCandidateOffers() {
		return candidateOffers;
	}

	@Override
	public void setAppliedOffers(List<OfferAudit> offers) {
		this.appliedOffers = offers;
		
	}

	@Override
	public void setCandaditeOffers(List<Offer> offers) {
		this.candidateOffers = offers;
		
	}

	@Override
	public void addAppliedOffer(OfferAudit offer) {
		appliedOffers.add(offer);
		
	}

	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setCandidateOffers(List<Offer> candidateOffers) {
		this.candidateOffers = candidateOffers;
	}
	
	
	    
}
