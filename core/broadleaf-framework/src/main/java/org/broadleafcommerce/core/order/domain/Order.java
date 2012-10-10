/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferInfo;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.profile.core.domain.Customer;

public interface Order extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);

    /**
     * Returns the subtotal price for the order.  The subtotal price is the price of all order items
     * with item offers applied.  The subtotal does not take into account the order offers or any
     * taxes that apply to this order.
     *
     * @return the total item price with offers applied
     */
    public Money getSubTotal();

    /**
     * Sets the subtotal price for the order.  The subtotal price is the price of all order items
     * with item offers applied.  The subtotal does not take into account the order offers or any
     * taxes that apply to this order.
     *
     * @param subTotal
     */
    public void setSubTotal(Money subTotal);

    public void assignOrderItemsFinalPrice();

    public Money calculateOrderItemsFinalPrice(boolean includeNonTaxableItems);

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

    public List<CandidateOrderOffer> getCandidateOrderOffers();

    public Date getSubmitDate();

    public void setSubmitDate(Date submitDate);

    /**
     * Gets the total tax for this order, which is the sum of the taxes on all fulfillment 
     * groups. This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for the order
     */
    public Money getTotalTax();

    /**
     * Sets the total tax of this order, which is the sum of the taxes on all fulfillment
     * groups. This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param the total tax for this order
     */
    public void setTotalTax(Money totalTax);

    public Money getTotalShipping();

    public void setTotalShipping(Money totalShipping);

    public List<PaymentInfo> getPaymentInfos();

    public void setPaymentInfos(List<PaymentInfo> paymentInfos);

    public boolean hasCategoryItem(String categoryName);

    /**
     * Returns a unmodifiable List of OrderAdjustment.  To modify the List of OrderAdjustment, please
     * use the addOrderAdjustments or removeAllOrderAdjustments methods.
     * 
     * @return a unmodifiable List of OrderItemAdjustment
     */
    public List<OrderAdjustment> getOrderAdjustments();

    public List<DiscreteOrderItem> getDiscreteOrderItems();
    
    /**
     * Checks the DiscreteOrderItems in the cart and returns whether or not the given SKU was found.
     * The equality of the SKUs is based on the .equals() method in SkuImpl
     * 
     * @param sku The sku to check for
     * @return whether or not the given SKU exists in the cart
     */
	public boolean containsSku(Sku sku);

    public List<OfferCode> getAddedOfferCodes();

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

	public boolean updatePrices();
	
	public Money getFulfillmentGroupAdjustmentsValue();
	
	public void addOfferCode(OfferCode addedOfferCode);
	
	@Deprecated
	public void addAddedOfferCode(OfferCode offerCode);

    /**
     * A list of arbitrary attributes added to this order.
     */
    public Map<String,OrderAttribute> getOrderAttributes();

    /**
     * Sets the map of order attributes.
     *
     * @param orderAttributes
     */
    public void setOrderAttributes(Map<String,OrderAttribute> orderAttributes);
    
    /**
     * This method returns the total number of items in this order. It iterates through all of the
     * discrete order items and sums up the quantity. This method is useful for display to the customer
     * the current number of "physical" items in the cart
     * 
     * @return the number of items in the order
     */
    public int getItemCount();

    BroadleafCurrency getCurrency();

    void setCurrency(BroadleafCurrency currency);

    public PriceList getPriceList();

    public void setPriceList(PriceList priceList);

    public Locale getLocale();

    public void setLocale(Locale locale);


}
