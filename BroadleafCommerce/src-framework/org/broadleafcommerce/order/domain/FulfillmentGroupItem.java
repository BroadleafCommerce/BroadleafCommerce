package org.broadleafcommerce.order.domain;

import java.math.BigDecimal;
import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;

public interface FulfillmentGroupItem {

    public Long getId();

    public void setId(Long id);

    public Long getFulfillmentGroupId();

    public void setFulfillmentGroupId(Long fulfillmentGroupId);

    public OrderItem getOrderItem();

    public void setOrderItem(OrderItem orderItem);

    public int getQuantity();

    public void setQuantity(int quantity);
    
    public BigDecimal getRetailPrice();
    
    public void setRetailPrice(BigDecimal retailPrice);
    
    public BigDecimal getSalePrice();
    
    public void setSalePrice(BigDecimal salePrice);
    
    public BigDecimal getPrice();
    
    public void setPrice(BigDecimal price);
    
    public List<Offer> getCandidateOffers();
    
    public void setCandidateOffers(List<Offer> offers);
    
    public void addCandidateOffer(Offer offer);
    
    public void removeCandidateOffer(Offer offer);
    
    public List<OfferAudit> getAppliedOffers();
    
    public void setAppliedOffers(List<OfferAudit> offers);
    
    public void addAppliedOffer(OfferAudit offer);
    
    public void removeAppliedOffer(OfferAudit offer);
}
