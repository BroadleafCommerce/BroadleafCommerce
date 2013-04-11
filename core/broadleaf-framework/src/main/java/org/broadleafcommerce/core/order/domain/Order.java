/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferInfo;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.pricing.service.workflow.FulfillmentGroupPricingActivity;
import org.broadleafcommerce.core.pricing.service.workflow.TotalActivity;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Defines an order in Broadleaf.    There are several key items to be aware of with the BLC Order.
 * 
 * 1.  Carts are also Orders that are in a Pending status
 * 
 * 2.  Wishlists (and similar) are "NamedOrders"
 * 
 * 3.  Orders have several price related methods that are useful when displaying totals on the cart.
 * 3a.    getSubTotal() :  The total of all order items and their adjustments exclusive of taxes
 * 3b.    getOrderAdjustmentsValue() :  The total of all order adjustments
 * 3c.    getTotalTax() :  The total taxes being charged for the order
 * 3d.    getTotal() : The order total (equivalent of getSubTotal() - getOrderAdjustmentsValue() + getTotalTax())
 * 
 * 4.  Order payments are represented with PaymentInfo objects.
 * 
 * 5.  Order shipping (e.g. fulfillment) are represented with Fulfillment objects.
 */
public interface Order extends Serializable {

    public Long getId();

    public void setId(Long id);

    /**
     * Gets the name of the order, mainly in order to support wishlists.
     * 
     * @return the name of the order
     */
    public String getName();

    /**
     * Sets the name of the order in the context of a wishlist. In this fashion, a {@link Customer} can have multiple
     * wishlists like "Christmas" or "Gaming Computer" etc.
     * 
     * @param name
     */
    public void setName(String name);

    /**
     * Gets the auditable associated with this Order instance which tracks changes made to this Order (creation/update)
     * 
     * @return
     */
    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);

    /**
     * Returns the subtotal price for the order.  The subtotal price is the price of all order items
     * with item offers applied.  The subtotal does not take into account the order promotions, shipping costs or any
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

    /**
     * Assigns a final price to all the order items
     */
    public void assignOrderItemsFinalPrice();

    /**
     * Returns the sum of the item totals. 
     * 
     * @return
     */
    public Money calculateSubTotal();

    /**
     * The grand total of this {@link Order} which includes all shipping costs and taxes, as well as any adjustments from
     * promotions.
     * 
     * @return the grand total price of this {@link Order}
     */
    public Money getTotal();

    /**
     * Used in {@link TotalActivity} to set the grand total of this {@link Order}. This includes the prices of all of the
     * {@link OrderItem}s as well as any taxes, fees, shipping and adjustments for all 3.
     * 
     * @param orderTotal the total cost of this {@link Order}
     */
    public void setTotal(Money orderTotal);

    /**
     * Convenience method for determining how much is left on the Order based on the payments that have already been
     * applied. This takes {@link #getTotal()} and subtracts the sum of all the {@link PaymentInfo}s associated with this
     * Order.  Note that if an order has been fully paid for, this method will return zero.
     * 
     * @return {@link #getTotal()} minus the {@link PaymentInfo#getAmount()} for each {@link PaymentInfo} on this Order
     */
    public Money getRemainingTotal();

    /**
     * Convenience method for determining how much of the order total has been captured. This takes the {@link PaymentInfo}s
     * and checks the {@link org.broadleafcommerce.core.payment.domain.PaymentInfoDetailType} for captured records.
     *
     * @return
     */
    public Money getCapturedTotal();

    /**
     * Gets the {@link Customer} for this {@link Order}.
     * 
     * @return
     */
    public Customer getCustomer();

    /**
     * Sets the associated {@link Customer} for this Order.
     * 
     * @param customer
     */
    public void setCustomer(Customer customer);

    /**
     * Gets the status of the Order.
     * 
     * @return
     */
    public OrderStatus getStatus();

    /**
     * Sets the status of the Order
     * 
     * @param status
     */
    public void setStatus(OrderStatus status);

    /**
     * Gets all the {@link OrderItem}s included in this {@link Order}
     * 
     * @return
     */
    public List<OrderItem> getOrderItems();

    public void setOrderItems(List<OrderItem> orderItems);

    /**
     * Adds an {@link OrderItem} to the list of {@link OrderItem}s already associated with this {@link Order}
     * 
     * @param orderItem the {@link OrderItem} to add to this {@link Order}
     */
    public void addOrderItem(OrderItem orderItem);

    /**
     * Gets the {@link FulfillmentGroup}s associated with this {@link Order}. An {@link Order} can have many
     * {@link FulfillmentGroup}s associated with it in order to support multi-address (and multi-type) shipping.
     * 
     * @return the {@link FulfillmentGroup}s associated with this {@link Order}
     */
    public List<FulfillmentGroup> getFulfillmentGroups();

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups);

    /**
     * Sets the {@link Offer}s that could potentially apply to this {@link Order}
     * 
     * @param candidateOrderOffers
     */
    public void setCandidateOrderOffers(List<CandidateOrderOffer> candidateOrderOffers);

    /**
     * Gets the {@link Offer}s that could potentially apply to this {@link Order}. Used in the promotion engine.
     * 
     * @return
     */
    public List<CandidateOrderOffer> getCandidateOrderOffers();

    /**
     * Gets the date that this {@link Order} was submitted.  Note that if this date is non-null, then the following should
     * also be true:
     *  <ul>
     *      <li>{@link #getStatus()} should return {@link OrderStatus#SUBMITTED}</li>
     *      <li>{@link #getOrderNumber()} should return a non-null value</li>
     *  </ul>
     *  
     * @return
     */
    public Date getSubmitDate();

    /**
     * Set the date that this {@link Order} was submitted. Used in the blCheckoutWorkflow as the last step after everything
     * else has been completed (payments charged, integration systems notified, etc).
     * 
     * @param submitDate the date that this {@link Order} was submitted.
     */
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

    /**
     * @deprected - use {@link #getTotalFulfillmentCharges()} instead.
     */
    public Money getTotalShipping();

    /**
     * @deprecated - Use {@link #setTotalFulfillmentCharges(Money)} instead.
     * 
     * @param totalShipping
     */
    public void setTotalShipping(Money totalShipping);

    /**
     * Gets the total fulfillment costs that should be charged for this {@link Order}. This value should be equivalent to 
     * the summation of {@link FulfillmentGroup#getTotal()} for each {@link FulfillmentGroup} associated with this 
     * {@link Order}
     * 
     * @return the total fulfillment cost of this {@link Order}
     */
    public Money getTotalFulfillmentCharges();

    /**
     * Set the total fulfillment cost of this {@link Order}. Used in the {@link FulfillmentGroupPricingActivity} after the cost
     * of each {@link FulfillmentGroup} has been calculated.
     * 
     * @param totalShipping
     */
    public void setTotalFulfillmentCharges(Money totalFulfillmentCharges);

    /**
     * Gets all the {@link PaymentInfo}s associated with this {@link Order}. An {@link Order} can have many
     * {@link PaymentInfo}s associated with it to support things like paying with multiple cards or perhaps paying some of
     * this {@link Order} with a gift card and some with a credit card.
     * 
     * @return the {@link PaymentInfo}s associated with this {@link Order}.
     */
    public List<PaymentInfo> getPaymentInfos();

    /**
     * Sets the various payment types associated with this {@link Order}
     * 
     * @param paymentInfos
     */
    public void setPaymentInfos(List<PaymentInfo> paymentInfos);

    /**
     * Determines if this {@link Order} has an item in the given category.
     * 
     * @param categoryName the {@link Category#getName} to check
     * @return <b>true</b> if at least one {@link OrderItem} is in the given category, <b>false</b> otherwise.
     * @see {@link OrderItem#isInCategory(String)}
     */
    public boolean hasCategoryItem(String categoryName);

    /**
     * Returns a unmodifiable List of OrderAdjustment.  To modify the List of OrderAdjustment, please
     * use the addOrderAdjustments or removeAllOrderAdjustments methods.
     * 
     * @return a unmodifiable List of OrderItemAdjustment
     */
    public List<OrderAdjustment> getOrderAdjustments();

    /**
     * Returns all of the {@link OrderItem}s in this {@link Order} that are an instanceof {@link DiscreteOrderItem}. This
     * will also go into each {@link BundleOrderItem} (if there are any) and return all of the
     * {@link BundleOrderItem#getDiscreteOrderItems()} from each of those as well.
     * 
     * @return
     */
    public List<DiscreteOrderItem> getDiscreteOrderItems();
    
    /**
     * Checks the DiscreteOrderItems in the cart and returns whether or not the given SKU was found.
     * The equality of the SKUs is based on the .equals() method in SkuImpl. This includes checking the
     * {@link DiscreteOrderItem}s from {link {@link BundleOrderItem#getDiscreteOrderItems()}
     * 
     * @param sku The sku to check for
     * @return whether or not the given SKU exists in the cart
     */
    public boolean containsSku(Sku sku);

    public List<OfferCode> getAddedOfferCodes();

    public String getFulfillmentStatus();

    /**
     * The unique number associated with this {@link Order}. Generally preferred to use instead of just using {@link #getId()}
     * since that exposes unwanted information about your database.
     * 
     * @return the unique order number for this {@link Order}
     */
    public String getOrderNumber();

    /**
     * Set the unique order number for this {@link Order}
     * 
     * @param orderNumber
     */
    public void setOrderNumber(String orderNumber);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    public Map<Offer, OfferInfo> getAdditionalOfferInformation();

    public void setAdditionalOfferInformation(Map<Offer, OfferInfo> additionalOfferInformation);

    /**
     * Returns the discount value of all the applied item offers for this order.  This value is already
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

    /**
     * Updates all of the prices of the {@link OrderItem}s in this {@link Order}
     * @return <b>true</b> if at least 1 {@link OrderItem} returned true from {@link OrderItem#updatePrices}, <b>false</b>
     * otherwise.
     * @see {@link OrderItem#updatePrices()}
     */
    public boolean updatePrices();
    
    /**
     * Updates the averagePriceField for all order items.
     * @return
     */
    public boolean finalizeItemPrices();

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

    /**
     * The currency that the {@link Order} is priced in. Note that this is only on {@link Order} since all of the other
     * entities that are related (like {@link FulfillmentGroup} and {@link OrderItem} have a link back to here. This also
     * has the side effect that an {@link Order} can only be priced in a single currency.
     * 
     * @return
     */
    public BroadleafCurrency getCurrency();

    /**
     * Set the currency that the {@link Order} is priced in.
     * 
     * @param currency
     */
    public void setCurrency(BroadleafCurrency currency);

    public Locale getLocale();

    public void setLocale(Locale locale);

    /**
     * Returns true if this item has order adjustments.
     * @return
     */
    boolean getHasOrderAdjustments();
}
