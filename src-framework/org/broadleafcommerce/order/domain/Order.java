package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.type.OrderType;
import org.broadleafcommerce.util.money.Money;

public interface Order {

    public Long getId();

    public void setId(Long id);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);

    public String getStatus();

    public void setStatus(String orderStatus);

    public Money getSubTotal();

    public void setSubTotal(Money subTotal);

    public Money getTotal();

    public void setTotal(Money orderTotal);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public ContactInfo getContactInfo();

    public void setContactInfo(ContactInfo contactInfo);

    public OrderType getType();

    public void setType(OrderType type);

    public List<OrderItem> getOrderItems();

    public void setOrderItems(List<OrderItem> orderItems);

    public List<FulfillmentGroup> getFulfillmentGroups();

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups);

    public List<Offer> getCandidateOffers();

    public void setCandaditeOffers(List<Offer> offers);

    public void addCandidateOffer(Offer offer);

    public void removeAllOffers();
    
    public boolean isMarkedForOffer();
    
    public void setMarkedForOffer(boolean markForOffer);
}
