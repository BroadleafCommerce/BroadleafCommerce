/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.domain.OfferInfo;
import org.broadleafcommerce.offer.domain.OrderAdjustment;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.util.money.Money;

public interface Order extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);

    /**
     * Returns the subtotal price for the order.  The subtotal price is the price of all order items
     * with item offers applied.  The subtotal does not take into account the order offers.
     *
     * @return the total item price with offers applied
     */
    public Money getSubTotal();

    /**
     * Sets the subtotal price for the order.  The subtotal price is the price of all order items
     * with item offers applied.  The subtotal does not take into account the order offers.
     *
     * @param subTotal
     */
    public void setSubTotal(Money subTotal);

    public void assignOrderItemsFinalPrice();

    public Money calculateOrderItemsCurrentPrice();

    public Money calculateOrderItemsFinalPrice();

    public Money getTotal();

    public Money getRemainingTotal();

    public void setTotal(Money orderTotal);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public OrderStatus getStatus();

    public void setStatus(OrderStatus status);

    public List<OrderItem> getOrderItems();

    public void setOrderItems(List<OrderItem> orderItems);

    public void addOrderItem(OrderItem orderItem);

    public List<FulfillmentGroup> getFulfillmentGroups();

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups);

    public void setCandidateOrderOffers(List<CandidateOrderOffer> candidateOrderOffers);

    public void addCandidateOrderOffer(CandidateOrderOffer candidateOrderOffer);

    public List<CandidateOrderOffer> getCandidateOrderOffers();

    public void removeAllCandidateOffers();

    public void removeAllCandidateOrderOffers();

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

    /**
     * Returns the price of the order with the order offers applied (item offers are not applied).
     * @return the order price with the order offers applied (item offers are not applied)
     */
    public Money getAdjustmentPrice();

    public void setAdjustmentPrice(Money adjustmentPrice);

    public List<PaymentInfo> getPaymentInfos();

    public void setPaymentInfos(List<PaymentInfo> paymentInfos);

    public boolean hasCategoryItem(String categoryName);

    //public List<OrderAdjustment> getOrderAdjustments();

    public List<OrderAdjustment> addOrderAdjustments(OrderAdjustment orderAdjustment);

    //public void setOrderAdjustments(List<OrderAdjustment> orderAdjustments);

    public void removeAllAdjustments();

    public void removeAllOrderAdjustments();

    public void removeAllItemAdjustments();

    public boolean containsNotStackableOrderOffer();

    public List<DiscreteOrderItem> getDiscreteOrderItems();

    public List<DiscreteOrderItem> getDiscountableDiscreteOrderItems();

    public List<OfferCode> getAddedOfferCodes();

    public void addAddedOfferCode(OfferCode addedOfferCode);

    public void removeAllAddedOfferCodes();

    public String getFulfillmentStatus();

    public String getOrderNumber();

    public void setOrderNumber(String orderNumber);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    public Map<Offer, OfferInfo> getAdditionalOfferInformation();

    public void setAdditionalOfferInformation(Map<Offer, OfferInfo> additionalOfferInformation);

    /**
     * Returns the discount value of all the applied item offers for this order.  The value is already
     * deducted from the order subTotal.
     *
     * @return the discount value of all the applied item offers for this order
     */
    public Money getItemAdjustmentsValue();

    /**
     * Returns the discount value of all the applied order offers.  The value returned from this
     * method should be subtracted from the getSubTotal() to get the order price with all item and
     * order offers applied.
     *
     * @return the discount value of all applied order offers.
     */
    public Money getOrderAdjustmentsValue();

    /**
     * Returns the total discount value for all applied item and order offers in the order.  The return
     * value should not be used with getSubTotal() to calculate the final price, since getSubTotal()
     * already takes into account the applied item offers.
     *
     * @return the total discount of all applied item and order offers
     */
    public Money getTotalAdjustmentsValue();

    public boolean isNotCombinableOfferApplied();

    public boolean isHasOrderAdjustments();

    public boolean updatePrices();

    public void setShippingPriceWithoutOffers(Money shippingPriceWithoutOffers);
    public Money getShippingPriceWithoutOffers();
    public void addToShippingPriceWithoutOffers(Money shippingPriceWithoutOffers);
}
