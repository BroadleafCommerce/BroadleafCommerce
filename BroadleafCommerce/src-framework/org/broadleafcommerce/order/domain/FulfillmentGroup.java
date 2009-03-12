package org.broadleafcommerce.order.domain;

import java.math.BigDecimal;
import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;
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

    public BigDecimal getRetailPrice();

    public void setRetailPrice(BigDecimal fulfillmentCost);

	public BigDecimal getSalePrice() ;

	public void setSalePrice(BigDecimal salePrice) ;

	public BigDecimal getPrice() ;

	public void setPrice(BigDecimal price) ;
    
    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    public FulfillmentGroupType getType();

    public void setType(FulfillmentGroupType type);
    
    public List<Offer> getCandidateOffers();
    
    public void setCandaditeOffers(List<Offer> offers);
    
    public void addCandidateOffer(Offer offer);
    
    public List<OfferAudit> getAppliedOffers();
    
    public void setAppliedOffers(List<OfferAudit> offers);
    
    public void addAppliedOffer(OfferAudit offer);
    
}
