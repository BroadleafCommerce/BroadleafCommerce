package org.broadleafcommerce.order.domain;

import java.math.BigDecimal;
import java.util.List;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.promotion.domain.Offer;
import org.broadleafcommerce.promotion.domain.OfferAudit;
import org.broadleafcommerce.type.OrderType;

public interface Order {

    public Long getId();

    public void setId(Long id);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);

    public String getStatus();

    public void setStatus(String orderStatus);

    public BigDecimal getTotal();

    public void setTotal(BigDecimal orderTotal);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public ContactInfo getContactInfo();

    public void setContactInfo(ContactInfo contactInfo);

    public OrderType getType();

    public void setType(OrderType type);
    
    public List<FulfillmentGroup> getFulfillmentGroups();
    
    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups);
    
    public List<Offer> getCandidateOffers();
    
    public void setCandaditeOffers(List<Offer> offers);
    
    public void addCandidateOffer(Offer offer);
    
    public List<OfferAudit> getAppliedOffers();
    
    public void setAppliedOffers(List<OfferAudit> offers);
    
    public void addAppliedOffer(OfferAudit offer);

}
