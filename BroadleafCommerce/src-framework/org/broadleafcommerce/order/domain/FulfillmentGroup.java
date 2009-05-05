package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.util.money.Money;

public interface FulfillmentGroup {

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    public Address getAddress();

    public void setAddress(Address address);

    public Phone getPhone();

    public void setPhone(Phone phone);

    public List<FulfillmentGroupItem> getFulfillmentGroupItems();

    public void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems);

    public void addFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem);

    public String getMethod();

    public void setMethod(String fulfillmentMethod);

    // TODO refactor these price method names to reflect that they represent
    // shipping prices
    public Money getRetailPrice();

    public void setRetailPrice(Money fulfillmentCost);

    public Money getSalePrice();

    public void setSalePrice(Money salePrice);

    public Money getPrice();

    public void setPrice(Money price);

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

    public Money getCityTax();

    public void setCityTax(Money cityTax);

    public Money getCountyTax();

    public void setCountyTax(Money countyTax);

    public Money getStateTax();

    public void setStateTax(Money stateTax);

    public Money getCountryTax();

    public void setCountryTax(Money countryTax);

    public Money getTotalTax();

    public void setTotalTax(Money totalTax);

    public String getDeliveryInstruction();

    public void setDeliveryInstruction(String deliveryInstruction);

    public PersonalMessage getPersonalMessage();

    public void setPersonalMessage(PersonalMessage personalMessage);
}
