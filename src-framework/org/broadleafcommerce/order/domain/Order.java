package org.broadleafcommerce.order.domain;

import java.util.Date;
import java.util.List;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.offer.domain.OrderAdjustment;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.util.money.Money;

public interface Order {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);

    public Money getSubTotal();

    public void setSubTotal(Money subTotal);

    public void assignOrderItemsFinalPrice();

    public Money calculateSubTotal();

    public Money getTotal();

    public Money getRemainingTotal();

    public void setTotal(Money orderTotal);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public String getStatus();

    public void setStatus(String status);

    public List<OrderItem> getOrderItems();

    public void setOrderItems(List<OrderItem> orderItems);

    public List<FulfillmentGroup> getFulfillmentGroups();

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups);

    public List<CandidateOrderOffer> getCandidateOrderOffers();

    public void setCandidateOffers(List<CandidateOrderOffer> offers);

    public void addCandidateOrderOffer(CandidateOrderOffer candidateOffer);

    public void removeAllCandidateOffers();

    public void removeAllOrderCandidateOffers();

    public boolean isMarkedForOffer();

    public void setMarkedForOffer(boolean markForOffer);

    public Date getSubmitDate();

    public void setSubmitDate(Date submitDate);

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

    public Money getTotalShipping();

    public void setTotalShipping(Money totalShipping);

    public Money getAdjustmentPrice();

    public void setAdjustmentPrice(Money adjustmentPrice);

    public List<PaymentInfo> getPaymentInfos();

    public void setPaymentInfos(List<PaymentInfo> paymentInfos);

    public boolean hasCategoryItem(String categoryName);

    public List<OrderAdjustment> getOrderAdjustments();

    public List<OrderAdjustment> addOrderAdjustments(OrderAdjustment orderAdjustment);

    public void reapplyOrderAdjustments();

    public void setOrderAdjustments(List<OrderAdjustment> orderAdjustments);

    public void removeAllAdjustments();

    public void removeAllOrderAdjustments();

    public void removeAllItemAdjustments();

    public boolean containsNotCombinableItemOffer();

    public boolean containsNotStackableOrderOffer();

    public List<DiscreteOrderItem> getDiscreteOrderItems();

}
