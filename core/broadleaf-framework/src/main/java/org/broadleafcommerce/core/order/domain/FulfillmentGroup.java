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

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Phone;

import java.io.Serializable;
import java.util.List;

/**
 * This is the main entity used to hold fulfillment information about an Order. An Order can have
 * multiple FulfillmentGroups to support shipping items to multiple addresses along with fulfilling
 * items multiple ways (ship some overnight, deliver some with digital download). This constraint means
 * that a FulfillmentGroup is unique based on an Address and FulfillmentOption combination. This
 * also means that in the common case for Orders that are being delivered to a single Address and
 * a single way (shipping everything express; ie a single FulfillmentOption) then there will be
 * only 1 FulfillmentGroup for that Order.
 *
 * @author Phillip Verheyden
 * @see {@link Order}, {@link FulfillmentOption}, {@link Address}, {@link FulfillmentGroupItem}
 */
public interface FulfillmentGroup extends Serializable, MultiTenantCloneable<FulfillmentGroup> {

    Long getId();

    void setId(Long id);

    Order getOrder();

    void setOrder(Order order);

    Integer getSequence();

    void setSequence(Integer sequence);

    FulfillmentOption getFulfillmentOption();

    void setFulfillmentOption(FulfillmentOption fulfillmentOption);

    Address getAddress();

    void setAddress(Address address);

    /**
     * @deprecated use {@link Address#getPhonePrimary()} instead.
     */
    @Deprecated
    Phone getPhone();

    /**
     * @param phone
     * @deprecated use {@link Address#getPhonePrimary()} instead
     */
    @Deprecated
    void setPhone(Phone phone);

    List<FulfillmentGroupItem> getFulfillmentGroupItems();

    void setFulfillmentGroupItems(List<FulfillmentGroupItem> fulfillmentGroupItems);

    void addFulfillmentGroupItem(FulfillmentGroupItem fulfillmentGroupItem);

    /**
     * @see {@link FulfillmentOption}
     * @deprecated Should use {@link #getFulfillmentOption()} instead
     */
    @Deprecated
    String getMethod();

    /**
     * @see {@link FulfillmentOption}
     * @deprecated Should use {@link #setFulfillmentOption()} instead
     */
    @Deprecated
    void setMethod(String fulfillmentMethod);

    /**
     * Returns the retail price for this fulfillmentGroup.   The retail and sale concepts used
     * for item pricing are not generally used with fulfillmentPricing but supported
     * nonetheless.    Typically only a retail price would be set on a fulfillment group.
     *
     * @return
     */
    Money getRetailFulfillmentPrice();

    /**
     * Sets the retail price for this fulfillmentGroup.
     *
     * @param fulfillmentPrice
     */
    void setRetailFulfillmentPrice(Money fulfillmentPrice);

    /**
     * Returns the sale price for this fulfillmentGroup.
     * Typically this will be null or equal to the retailFulfillmentPrice
     *
     * @return
     */
    Money getSaleFulfillmentPrice();

    /**
     * Sets the sale price for this fulfillmentGroup.  Typically not used.
     *
     * @param fulfillmentPrice
     * @see #setRetailFulfillmentPrice(Money)
     */
    void setSaleFulfillmentPrice(Money fulfillmentPrice);

    /**
     * Gets the price to charge for this fulfillmentGroup.   Includes the effects of any adjustments such as those that
     * might have been applied by the promotion engine (e.g. free shipping)
     *
     * @return
     */
    Money getFulfillmentPrice();

    /**
     * Sets the price to charge for this fulfillmentGroup.  Typically set internally by the Broadleaf pricing and
     * promotion engines.
     *
     * @return
     */
    void setFulfillmentPrice(Money fulfillmentPrice);

    /**
     * @return
     * @deprecated - use {@link #getRetailFulfillmentPrice()} instead.   Deprecated as the price might be for other
     * fulfillment types such as PickUpAtStore fees or download fees.
     */
    @Deprecated
    Money getRetailShippingPrice();

    /**
     * @return
     * @deprecated - use {@link #setRetailFulfillmentPrice(Money)} instead.
     */
    @Deprecated
    void setRetailShippingPrice(Money retailShippingPrice);

    /**
     * @return
     * @deprecated - use {@link #getSaleFulfillmentPrice()} instead.
     */
    @Deprecated
    Money getSaleShippingPrice();

    /**
     * @param saleShippingPrice
     * @deprecated - use {@link #setSaleFulfillmentPrice(Money)} instead.
     */
    @Deprecated
    void setSaleShippingPrice(Money saleShippingPrice);

    /**
     * @return
     * @deprecated - use {@link #getFulfillmentPrice()} instead.
     */
    @Deprecated
    Money getShippingPrice();

    /**
     * @param shippingPrice
     * @deprecated - use {@link #setRetailFulfillmentPrice(Money)} instead.
     */
    @Deprecated
    void setShippingPrice(Money shippingPrice);

    String getReferenceNumber();

    void setReferenceNumber(String referenceNumber);

    FulfillmentType getType();

    void setType(FulfillmentType type);

    List<CandidateFulfillmentGroupOffer> getCandidateFulfillmentGroupOffers();

    void setCandidateFulfillmentGroupOffer(List<CandidateFulfillmentGroupOffer> candidateOffers);

    void addCandidateFulfillmentGroupOffer(CandidateFulfillmentGroupOffer candidateOffer);

    void removeAllCandidateOffers();

    List<FulfillmentGroupAdjustment> getFulfillmentGroupAdjustments();

    void setFulfillmentGroupAdjustments(List<FulfillmentGroupAdjustment> fulfillmentGroupAdjustments);

    /**
     * Returns a List of FulfillmentGroupAdjustment originating from FUTURE_CREDIT Offers.
     * <p>
     * See {@link org.broadleafcommerce.core.offer.domain.Offer#getAdjustmentType()} for more info on future credit
     *
     * @return a List of FulfillmentGroupAdjustment
     */
    List<FulfillmentGroupAdjustment> getFutureCreditFulfillmentGroupAdjustments();

    void removeAllAdjustments();

    /**
     * Gets a list of TaxDetail objects, which are taxes that apply directly to this fulfillment group.
     * An example of a such a tax would be a shipping tax.
     *
     * @return a list of taxes that apply to this fulfillment group
     */
    List<TaxDetail> getTaxes();

    /**
     * Gets the list of TaxDetail objects, which are taxes that apply directly to this fulfillment group.
     * An example of a such a tax would be a shipping tax.
     *
     * @param taxes the list of taxes on this fulfillment group
     */
    void setTaxes(List<TaxDetail> taxes);

    /**
     * Gets the total tax for this fulfillment group, which is the sum of the taxes on all fulfillment
     * group items, fees, and taxes on this fulfillment group itself (such as a shipping tax).
     * This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for the fulfillment group
     */
    Money getTotalTax();

    /**
     * Sets the total tax for this fulfillment group, which is the sum of the taxes on all fulfillment
     * group items, fees, and taxes on this fulfillment group itself (such as a shipping tax).
     * This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param totalTax the total tax for this fulfillment group
     */
    void setTotalTax(Money totalTax);

    /**
     * Gets the total item tax for this fulfillment group, which is the sum of the taxes on all fulfillment
     * group items. This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for this fulfillment group
     */
    Money getTotalItemTax();

    /**
     * Sets the total item tax for this fulfillment group, which is the sum of the taxes on all fulfillment
     * group items. This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param totalItemTax the total tax for this fulfillment group
     */
    void setTotalItemTax(Money totalItemTax);

    /**
     * Gets the total fee tax for this fulfillment group, which is the sum of the taxes on all fulfillment
     * group fees. This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for this fulfillment group
     */
    Money getTotalFeeTax();

    /**
     * Sets the total fee tax for this fulfillment group, which is the sum of the taxes on all fulfillment
     * group fees. This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param totalFeeTax the total tax for this fulfillment group
     */
    void setTotalFeeTax(Money totalFeeTax);

    /**
     * Gets the total fulfillment group tax for this fulfillment group, which is the sum of the taxes
     * on this fulfillment group itself (such as a shipping tax) only. It does not include the taxes on
     * items or fees in this fulfillment group. This total is calculated in the TotalActivity stage of the pricing workflow.
     *
     * @return the total tax for this fulfillment group
     */
    Money getTotalFulfillmentGroupTax();

    /**
     * Sets the total fulfillment group tax for this fulfillment group, which is the sum of the taxes
     * on this fulfillment group itself (such as a shipping tax) only. It does not include the taxes on
     * items or fees in this fulfillment group. This total should only be set during the TotalActivity stage of the pricing workflow.
     *
     * @param totalFulfillmentGroupTax the total tax for this fulfillment group
     */
    void setTotalFulfillmentGroupTax(Money totalFulfillmentGroupTax);

    String getDeliveryInstruction();

    void setDeliveryInstruction(String deliveryInstruction);

    PersonalMessage getPersonalMessage();

    void setPersonalMessage(PersonalMessage personalMessage);

    boolean isPrimary();

    void setPrimary(boolean primary);

    Money getMerchandiseTotal();

    void setMerchandiseTotal(Money merchandiseTotal);

    Money getTotal();

    void setTotal(Money orderTotal);

    FulfillmentGroupStatusType getStatus();

    void setStatus(FulfillmentGroupStatusType status);

    List<FulfillmentGroupFee> getFulfillmentGroupFees();

    void setFulfillmentGroupFees(List<FulfillmentGroupFee> fulfillmentGroupFees);

    void addFulfillmentGroupFee(FulfillmentGroupFee fulfillmentGroupFee);

    void removeAllFulfillmentGroupFees();

    Boolean isShippingPriceTaxable();

    void setIsShippingPriceTaxable(Boolean isShippingPriceTaxable);

    /**
     * @see {@link FulfillmentOption}
     * @deprecated Should use {@link #getFulfillmentOption()} instead
     */
    @Deprecated
    String getService();

    /**
     * @see {@link FulfillmentOption}
     * @deprecated Should use {@link #setFulfillmentOption()} instead
     */
    @Deprecated
    void setService(String service);

    List<DiscreteOrderItem> getDiscreteOrderItems();

    Money getFulfillmentGroupAdjustmentsValue();

    /**
     * Returns the discount value of the applied future credit offers for this fulfillment group.
     * <p>
     * See {@link org.broadleafcommerce.core.offer.domain.Offer#getAdjustmentType()} for more info on future credit
     *
     * @return the discount value of the applied future credit offers for this fulfillment group
     */
    Money getFutureCreditFulfillmentGroupAdjustmentsValue();

    /**
     * @return whether or not to override the shipping calculation
     */
    Boolean getShippingOverride();

    /**
     * Sets whether or not to override the shipping calculation
     *
     * @param shippingOverride
     */
    void setShippingOverride(Boolean shippingOverride);

}
