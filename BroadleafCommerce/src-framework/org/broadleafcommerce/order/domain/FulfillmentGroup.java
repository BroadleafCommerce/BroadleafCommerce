package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.type.FulfillmentGroupType;
import org.broadleafcommerce.util.money.Money;

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

    public Money getRetailPrice();

    public void setRetailPrice(Money fulfillmentCost);

    public Money getSalePrice() ;

    public void setSalePrice(Money salePrice) ;

    public Money getPrice() ;

    public void setPrice(Money price) ;

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

    public void removeAllOffers();

}
