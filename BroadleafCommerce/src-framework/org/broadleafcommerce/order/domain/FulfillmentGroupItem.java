package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.util.money.Money;

public interface FulfillmentGroupItem {

    public Long getId();

    public void setId(Long id);

    public FulfillmentGroup getFulfillmentGroup();

    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup);

    public OrderItem getOrderItem();

    public void setOrderItem(OrderItem orderItem);

    public int getQuantity();

    public void setQuantity(int quantity);

    //TODO refactor these price method names to reflect that they represent shipping prices
    public Money getRetailPrice();

    public void setRetailPrice(Money retailPrice);

    public Money getSalePrice();

    public void setSalePrice(Money salePrice);

    public Money getPrice();

    public void setPrice(Money price);

    public List<Offer> getCandidateOffers();

    public void setCandidateOffers(List<Offer> offers);

    public void addCandidateOffer(Offer offer);

    public void removeCandidateOffer(Offer offer);

    public List<OfferAudit> getAppliedOffers();

    public void setAppliedOffers(List<OfferAudit> offers);

    public void addAppliedOffer(OfferAudit offer);

    public void removeAppliedOffer(OfferAudit offer);
}
