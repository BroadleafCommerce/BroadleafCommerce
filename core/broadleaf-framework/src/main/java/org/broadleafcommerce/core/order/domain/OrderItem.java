/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.ProratedOrderItemAdjustment;
import org.broadleafcommerce.core.order.service.type.OrderItemType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface OrderItem extends Serializable, Cloneable, MultiTenantCloneable<OrderItem> {

    /**
     * The unique identifier of this OrderItem
     *
     * @return
     */
    Long getId();

    /**
     * Sets the unique id of the OrderItem.   Typically left null for new items and Broadleaf will
     * set using the next sequence number.
     *
     * @param id
     */
    void setId(Long id);

    /**
     * Reference back to the containing order.
     *
     * @return
     */
    Order getOrder();

    /**
     * Sets the order for this orderItem.
     *
     * @param order
     */
    void setOrder(Order order);

    /**
     * The retail price of the item that was added to the {@link Order} at the time that this was added. This is preferable
     * to use as opposed to checking the price of the item that was added from the catalog domain (like in
     * {@link DiscreteOrderItem}, using {@link DiscreteOrderItem#getSku()}'s retail price) since the price in the catalog
     * domain could have changed since the item was added to the {@link Order}.
     *
     * @return
     */
    Money getRetailPrice();

    /**
     * Calling this method will manually set the retailPrice.   To avoid the pricing engine
     * resetting this price, you should also make a call to
     * {@link #setRetailPriceOverride(true)}
     * <p>
     * Consider also calling {@link #setDiscountingAllowed(boolean)} with a value of false to restrict
     * discounts after manually setting the retail price.
     *
     * @param retailPrice
     */
    void setRetailPrice(Money retailPrice);

    /**
     * Returns true if the retail price was manually set.   If the retail price is manually set,
     * calls to updatePrices() will not do anything.
     *
     * @return
     */
    boolean isRetailPriceOverride();

    /**
     * Indicates that the retail price was manually set.  A typical usage might be for a
     * CSR who has override privileges to control setting the price.   This will automatically be set
     * by calling {@link #setRetailPrice(Money)}
     */
    void setRetailPriceOverride(boolean override);

    /**
     * Returns the salePrice for this item.    Note this method will return the lower of the retailPrice
     * or salePrice.    It will return the retailPrice instead of null.
     * <p>
     * For SKU based pricing, a call to {@link #updateSaleAndRetailPrices()} will ensure that the
     * retailPrice being used is current.
     *
     * @return
     */
    Money getSalePrice();

    /**
     * Calling this method will manually set the salePrice.    It will also make a call to
     * {@link #setSalePriceSetManually(true)}
     * <p>
     * To avoid the pricing engine resetting this price, you should also make a call to
     * {@link #setSalePriceOverride(true)}
     * <p>
     * Typically for {@link DiscreteOrderItem}s, the prices will be set with a call to {@link #updateSaleAndRetailPrices()}
     * which will use the Broadleaf dynamic pricing engine or the values directly tied to the SKU.
     *
     * @param salePrice
     */
    void setSalePrice(Money salePrice);

    /**
     * Returns true if the sale price was manually set.   If the retail price is manually set,
     * calls to updatePrices() will not do anything.
     *
     * @return
     */
    boolean isSalePriceOverride();

    /**
     * Indicates that the sale price was manually set.  A typical usage might be for a
     * CSR who has override privileges to control setting the price.
     * <p>
     * Consider also calling {@link #setDiscountingAllowed(boolean)} with a value of false to restrict
     * discounts after manually setting the retail price.
     * <p>
     * If the salePrice is not lower than the retailPrice, the system will return the retailPrice when
     * a call to {@link #getSalePrice()} is made.
     */
    void setSalePriceOverride(boolean override);

    /**
     * Returns a list of all prorated adjustments for this order item.
     *
     * @return
     */
    List<ProratedOrderItemAdjustment> getProratedOrderItemAdjustments();

    /**
     * Sets the list of prorated order item adjustments for this order item.
     *
     * @param proratedOrderItemAdjustments
     */
    void setProratedOrderItemAdjustments(List<ProratedOrderItemAdjustment> proratedOrderItemAdjustments);

    /**
     * @return
     * @deprecated Delegates to {@link #getAverageAdjustmentValue()} instead but this method is of little
     * or no value in most practical applications unless only simple promotions are being used.
     */
    @Deprecated
    Money getAdjustmentValue();

    /**
     * @return
     * @deprecated Delegates to {@link #getAveragePrice()}
     */
    @Deprecated
    Money getPrice();

    /**
     * Calling this method is the same as calling the following:
     * <p>
     * {@link #setRetailPrice(Money)}
     * {@link #setSalePrice(Money)}
     * {@link #setRetailPriceOverride(true)}
     * {@link #setSalePriceOverride(true)}
     * {@link #setDiscountingAllowed(false)}
     * <p>
     * This has the effect of setting the price in a way that no discounts or adjustments can be made.
     *
     * @param price
     */
    void setPrice(Money price);

    /**
     * The quantity of this {@link OrderItem}.
     *
     * @return
     */
    int getQuantity();

    /**
     * Sets the quantity of this item
     */
    void setQuantity(int quantity);

    /**
     * Collection of priceDetails for this orderItem.
     * <p>
     * Without discounts, an orderItem would have exactly 1 ItemPriceDetail.   When orderItem discounting or
     * tax-calculations result in an orderItem having multiple prices like in a buy-one-get-one free example,
     * the orderItem will get an additional ItemPriceDetail.
     * <p>
     * Generally, an OrderItem will have 1 ItemPriceDetail record for each uniquely priced version of the item.
     */
    List<OrderItemPriceDetail> getOrderItemPriceDetails();

    /**
     * Returns the list of orderItem price details.
     *
     * @param orderItemPriceDetails
     * @see {@link #getOrderItemPriceDetails()}
     */
    void setOrderItemPriceDetails(List<OrderItemPriceDetail> orderItemPriceDetails);

    Category getCategory();

    void setCategory(Category category);

    List<CandidateItemOffer> getCandidateItemOffers();

    void setCandidateItemOffers(List<CandidateItemOffer> candidateItemOffers);

    /**
     * Returns item level adjustment for versions of Broadleaf Commerce prior to 2.3.0 which replaced
     * this concept with OrderItemPriceDetail adjustments.
     *
     * @return a List of OrderItemAdjustment
     */
    @Deprecated
    List<OrderItemAdjustment> getOrderItemAdjustments();

    /**
     * @deprecated Item level adjustments are now stored at the OrderItemPriceDetail level instead to
     * prevent unnecessary item splitting of OrderItems when evaluating promotions
     * in the pricing engine.
     */
    @Deprecated
    void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments);

    /**
     * If any quantity of this item was used to qualify for an offer, then this returned list
     * will indicate the offer and the relevant quantity.
     * <p>
     * As an example, a BuyOneGetOneFree offer would have 1 qualifier and 1 adjustment.
     *
     * @return a List of OrderItemAdjustment
     */
    List<OrderItemQualifier> getOrderItemQualifiers();

    /**
     * Sets the list of OrderItemQualifiers
     */
    void setOrderItemQualifiers(List<OrderItemQualifier> orderItemQualifiers);

    PersonalMessage getPersonalMessage();

    void setPersonalMessage(PersonalMessage personalMessage);

    boolean isInCategory(String categoryName);

    GiftWrapOrderItem getGiftWrapOrderItem();

    void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem);

    OrderItemType getOrderItemType();

    void setOrderItemType(OrderItemType orderItemType);

    /**
     * @return
     * @deprecated If the item is taxable, returns {@link #getAveragePrice()}
     * <p>
     * It is recommended instead that tax calculation engines use the {@link #getTotalTaxableAmount()} which provides the taxable
     * total for all quantities of this item.    This method suffers from penny rounding errors in some
     * situations.
     */
    @Deprecated
    Money getTaxablePrice();

    /**
     * Default implementation uses {@link #getSalePrice()} &lt; {@link #getRetailPrice()}
     *
     * @return
     */
    boolean getIsOnSale();

    /**
     * If true, this item can be discounted..
     */
    boolean isDiscountingAllowed();

    /**
     * Turns off discount processing for this line item.
     *
     * @param discountingAllowed
     */
    void setDiscountingAllowed(boolean discountingAllowed);

    /**
     * Returns true if this item received a discount.
     *
     * @return
     */
    boolean getIsDiscounted();

    /**
     * Generally copied from the Sku.getName()
     *
     * @return
     */
    String getName();

    /**
     * Sets the name of this order item.
     *
     * @param name
     */
    void setName(String name);

    /**
     * Used to reset the base price of the item that the pricing engine uses.
     * <p>
     * Generally, this will update the retailPrice and salePrice based on the
     * corresponding value in the SKU.
     * <p>
     * If the retail or sale price was manually set, this method will not change
     * those prices.
     * <p>
     * For non-manually set prices, prices can change based on system activities such as
     * locale changes and customer authentication, this method is used to
     * ensure that all cart items reflect the current base price before
     * executing other pricing / adjustment operations.
     * <p>
     * Other known scenarios that can effect the base prices include the automatic bundling
     * or loading a stale cart from the database.
     * <p>
     * See notes in subclasses for specific behavior of this method.
     *
     * @return true if the base prices changed as a result of this call
     */
    boolean updateSaleAndRetailPrices();

    /**
     * Called by the pricing engine after prices have been computed.   Allows the
     * system to set the averagePrice that is stored for the item.
     */
    void finalizePrice();

    OrderItem clone();

    /**
     * Used to set the final price of the item and corresponding details.
     */
    void assignFinalPrice();

    /**
     * Returns the unit price of this item.   If the parameter allowSalesPrice is true, will
     * return the sale price if one exists.
     * <p>
     * Note that retail and sale prices are initialized for an {@link OrderItem} during the first execution of
     * any method that depends on the sale or retail price including this one.
     *
     * @param allowSalesPrice
     * @return
     * @see OrderItem#updateSaleAndRetailPrices()
     */
    Money getPriceBeforeAdjustments(boolean allowSalesPrice);

    Money getPriceBeforeAdjustments(boolean allowSalesPrice, boolean includeChildren);

    /**
     * Used by the promotion engine to add offers that might apply to this orderItem.
     *
     * @param candidateItemOffer
     */
    void addCandidateItemOffer(CandidateItemOffer candidateItemOffer);

    /**
     * Removes all candidate offers.   Used by the promotion engine which subsequently adds
     * the candidate offers that might apply back to this item.
     */
    void removeAllCandidateItemOffers();

    /**
     * Removes all adjustment for this order item and reset the adjustment price.
     */
    int removeAllAdjustments();

    /**
     * A list of arbitrary attributes added to this item.
     */
    Map<String, OrderItemAttribute> getOrderItemAttributes();

    /**
     * Sets the map of order item attributes.
     *
     * @param orderItemAttributes
     */
    void setOrderItemAttributes(Map<String, OrderItemAttribute> orderItemAttributes);

    /**
     * Returns the average unit display price for the item.
     * <p>
     * Some implementations may choose to show this on a view cart screen.    Due to fractional discounts,
     * it the display could represent a unit price that is off.
     * <p>
     * For example, if this OrderItem had 3 items at $1 each and also received a $1 discount.   The net
     * effect under the default rounding scenario would be an average price of $0.666666
     * <p>
     * Most systems would represent this as $0.67 as the discounted unit price; however, this amount times 3 ($2.01)
     * would not equal the getTotalPrice() which would be $2.00.    For this reason, it is not recommended
     * that implementations utilize this field.    Instead, they may choose not to show the unit price or show
     * multiple prices by looping through the OrderItemPriceDetails.
     *
     * @return
     */
    Money getAveragePrice();

    /**
     * Returns the average unit item adjustments.
     * <p>
     * For example, if this item has a quantity of 2 at a base-price of $10 and a 10% discount applies to both
     * then this method would return $1.
     * <p>
     * Some implementations may choose to show this on a view cart screen.    Due to fractional discounts,
     * the display could represent a unit adjustment value that is off due to rounding.    See {@link #getAveragePrice()}
     * for an example of this.
     * <p>
     * Implementations wishing to show unit prices may choose instead to show the individual OrderItemPriceDetails
     * instead of this value to avoid the rounding problem.    This would result in multiple cart item
     * display rows for each OrderItem.
     * <p>
     * Alternatively, the cart display should use {@link #getTotalAdjustmentValue()}.
     *
     * @return
     */
    Money getAverageAdjustmentValue();

    /**
     * Returns the total for all item level adjustments.
     * <p>
     * For example, if the item has a 2 items priced at $10 a piece and a 10% discount applies to both
     * quantities.    This method would return $2.
     *
     * @return
     */
    Money getTotalAdjustmentValue();

    /**
     * Returns the total for all item level adjustments.
     *
     * @param includeChildren
     * @return
     */
    Money getTotalAdjustmentValue(boolean includeChildren);

    /**
     * Returns the total for future credit item level adjustments.
     * <p>
     * For example, if the item has a 2 items priced at $10 a piece and a 10% discount applies to both
     * quantities.    This method would return $2.
     * <p>
     * See {@link org.broadleafcommerce.core.offer.domain.Offer#getAdjustmentType()} for more info on future credit
     *
     * @return
     */
    Money getFutureCreditTotalAdjustmentValue();

    /**
     * Returns the total for future credit item level adjustments.
     * <p>
     * See {@link org.broadleafcommerce.core.offer.domain.Offer#getAdjustmentType()} for more info on future credit
     *
     * @param includeChildren
     * @return
     */
    Money getFutureCreditTotalAdjustmentValue(boolean includeChildren);

    /**
     * Returns the total price to be paid for this order item including item-level adjustments.
     * <p>
     * It does not include the effect of order level adjustments.   Calculated by looping through
     * the orderItemPriceDetails
     *
     * @return
     */
    Money getTotalPrice();

    /**
     * Returns the total price to be paid for this order item including item-level adjustments.
     *
     * @param includeChildren
     * @return
     */
    Money getTotalPrice(boolean includeChildren);

    /**
     * Returns whether or not this item is taxable. If this flag is not set, it returns true by default
     *
     * @return the taxable flag. If null, returns true
     */
    Boolean isTaxable();

    /**
     * Sets whether or not this item is taxable.   Generally, this has been copied from the
     * setting of the relevant SKU at the time it was added.
     *
     * @param taxable
     */
    void setTaxable(Boolean taxable);

    /**
     * Returns the total price to be paid before adjustments.
     *
     * @return
     */
    Money getTotalPriceBeforeAdjustments(boolean allowSalesPrice);

    /**
     * Returns a boolean indicating whether this sku is active.  This is used to determine whether a user
     * the sku can add the sku to their cart.
     */
    boolean isSkuActive();

    /**
     * @return the list of orderitems that are dependent on this order item
     */
    List<OrderItem> getChildOrderItems();

    /**
     * Sets the list of orderitems that are dependent on this order item
     *
     * @param childOrderItems
     */
    void setChildOrderItems(List<OrderItem> childOrderItems);

    /**
     * @return the parent order item for this item (potentially null)
     */
    OrderItem getParentOrderItem();

    /**
     * Sets the parent order item for this order item
     *
     * @param parentOrderItem
     */
    void setParentOrderItem(OrderItem parentOrderItem);

    /**
     * @return whether or not this order item has an error
     */
    Boolean getHasValidationError();

    /**
     * Sets whether or not this order item has an error associated with it
     *
     * @param hasValidationError
     */
    void setHasValidationError(Boolean hasValidationError);

    /**
     * @param candidateChild
     * @return true if the candidateChild is a child of the hierarchy starting from this OrderItem
     */
    boolean isAParentOf(OrderItem candidateChild);

    /**
     * @return true if the OrderItem has a parent
     */
    boolean isChildOrderItem();

    /**
     * @return the list of messages that should be displayed in the cart
     */
    List<String> getCartMessages();

    /**
     * Sets the list of messages that should be displayed in the cart
     *
     * @param cartMessages
     */
    void setCartMessages(List<String> cartMessages);

    /**
     * @return the Auditable date for this record
     */
    Auditable getAuditable();

    /**
     * Sets the Auditable date object - typially set by Hibernate through the AuditableListener
     *
     * @param auditable
     */
    void setAuditable(Auditable auditable);

}
