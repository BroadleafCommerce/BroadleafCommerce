/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.core.offer.service.type.CustomerMaxUsesStrategyType;
import org.broadleafcommerce.core.offer.service.type.OfferAdjustmentType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.OrderItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Offer extends Status, Serializable,MultiTenantCloneable<Offer> {

    public void setId(Long id);

    public Long getId();

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public OfferType getType();

    public void setType(OfferType offerType);

    public OfferDiscountType getDiscountType();

    public void setDiscountType(OfferDiscountType type);

    public BigDecimal getValue();

    public void setValue(BigDecimal value);

    public int getPriority();

    public void setPriority(Integer priority);

    public Date getStartDate();

    public void setStartDate(Date startDate);

    public Date getEndDate();

    public void setEndDate(Date endDate);

    public String getTargetSystem();

    public void setTargetSystem(String targetSystem);

    public boolean getApplyDiscountToSalePrice();

    public void setApplyDiscountToSalePrice(boolean applyToSalePrice);

    public OfferItemRestrictionRuleType getOfferItemQualifierRuleType();

    public void setOfferItemQualifierRuleType(OfferItemRestrictionRuleType restrictionRuleType);

    public OfferItemRestrictionRuleType getOfferItemTargetRuleType();

    public void setOfferItemTargetRuleType(OfferItemRestrictionRuleType restrictionRuleType);

    /**
     * Returns whether or not this offer should apply to an {@link OrderItem}s child order items
     * @return applyToChildItems
     */
    Boolean getApplyToChildItems();

    /**
     * Sets whether or not this offer should apply to an {@link OrderItem}s child order items
     * @param applyToChildItems
     */
    void setApplyToChildItems(boolean applyToChildItems);

    /**
     * Returns false if this offer is not combinable with other offers of the same type.
     * For example, if this is an Item offer it could be combined with other Order or FG offers
     * but it cannot be combined with other Item offers.
     * 
     * @return
     */
    public boolean isCombinableWithOtherOffers();

    public void setCombinableWithOtherOffers(boolean combinableWithOtherOffers);

    /**
     * Returns true if the offer system should automatically add this offer for consideration (versus requiring a code or 
     * other delivery mechanism).    This does not guarantee that the offer will qualify.   All rules associated with this
     * offer must still pass.   A true value here just means that the offer will be considered.     
     * 
     * Returns null if false
     * 
     * @return
     */
    public boolean isAutomaticallyAdded();

    /**
     * Sets whether or not this offer should be automatically considered for consideration (versus requiring a code or 
     * other delivery mechanism).
     * @see #isAutomaticallyAdded()
     */
    public void setAutomaticallyAdded(boolean automaticallyAdded);

    /**
     * Returns the maximum number of times that this offer
     * can be used by the same customer.   This field
     * tracks the number of times the offer can be used
     * and not how many times it is applied.
     *
     * 0 or null indicates unlimited usage per customer.
     *
     * @return
     */
    public Long getMaxUsesPerCustomer();

    /**
     * Sets the maximum number of times that this offer
     * can be used by the same customer.  Intended as a transient
     * field that gets derived from the other persisted max uses fields
     * including maxUsesPerOrder and maxUsesPerCustomer.
     *
     * 0 or null indicates unlimited usage.
     *
     * @param maxUses
     */
    public void setMaxUsesPerCustomer(Long maxUses);

    /**
     * Enum that allows implementations to vary how customer max uses will
     * be interpreted.    If null, the strategy will be {@link CustomerMaxUsesStrategyType.CUSTOMER}.
     * 
     * @return
     */
    CustomerMaxUsesStrategyType getMaxUsesStrategyType();

    /**
     * Sets the strategy for determining customer max uses.
     * 
     * @param strategyType
     * @see #getMaxUsesStrategyType()
     */
    void setMaxUsesStrategyType(CustomerMaxUsesStrategyType strategyType);

    /**
     * Sets the minimum number of days that this offer can be used 
     * by the same customer before resetting the number of uses dictated by {@link Offer#getMaxUsesPerCustomer()}. 
     *
     * 0 or null indicates that the offer will never reset usages.
     *
     * @return
     */
    Long getMinimumDaysPerUsage();

    /**
     * Sets the minimum number of days that this offer can be used 
     * by the same customer before resetting the number of uses dictated by {@link Offer#getMaxUsesPerCustomer()}. 
     *
     * 0 or null indicates that the offer will never reset usages.
     * 
     * @param minimumDaysPerUsage
     */
    void setMinimumDaysPerUsage(Long minimumDaysPerUsage);

    /**
     * Indicates that there is no limit to how many times a customer can use this offer. By default this is true if
     * {@link #getMaxUsesPerCustomer()} == 0
     */
    public boolean isUnlimitedUsePerCustomer();
    
    /**
     * Whether or not this offer has limited use in an order. By default this is true if {@link #getMaxUsesPerCustomer()} > 0
     */
    public boolean isLimitedUsePerCustomer();

    /**
     * Returns the maximum number of times that this offer
     * can be used in the current order.
     *
     * 0 indicates unlimited usage.
     */
    public int getMaxUsesPerOrder();

    /**
     * Sets the maximum number of times that this offer
     * can be used in the current order.
     *
     * 0 indicates unlimited usage.
     *
     * @param maxUses
     */
    public void setMaxUsesPerOrder(int maxUsesPerOrder);
    
    /**
     * Indicates that there is no limit to how many times this offer can be applied to the order. By default this is true if
     * {@link #getMaxUsesPerOrder()} == 0
     */
    public boolean isUnlimitedUsePerOrder();
    
    /**
     * Whether or not this offer has limited use in an order. By default this is true if {@link #getMaxUsesPerOrder()} > 0
     */
    public boolean isLimitedUsePerOrder();

    Set<OfferQualifyingCriteriaXref> getQualifyingItemCriteriaXref();

    void setQualifyingItemCriteriaXref(Set<OfferQualifyingCriteriaXref> qualifyingItemCriteriaXref);

    Set<OfferTargetCriteriaXref> getTargetItemCriteriaXref();

    void setTargetItemCriteriaXref(Set<OfferTargetCriteriaXref> targetItemCriteriaXref);
    
    public Boolean isTotalitarianOffer();

    public void setTotalitarianOffer(Boolean totalitarianOffer);

    Map<String, OfferOfferRuleXref> getOfferMatchRulesXref();

    void setOfferMatchRulesXref(Map<String, OfferOfferRuleXref> offerMatchRulesXref);

    Boolean getUseListForDiscounts();

    void setUseListForDiscounts(Boolean useListForDiscounts);

    List<OfferPriceData> getOfferPriceData();

    void setOfferPriceData(List<OfferPriceData> offerPriceData);

    /**
     * Indicates the amount of items that must be purchased for this offer to
     * be considered for this order.
     * 
     * The system will find all qualifying items for the given offer and sum their prices before
     * any discounts are applied to make the determination.  
     * 
     * If the sum of the qualifying items is not greater than this value the offer is 
     * not considered by the offer processing algorithm.
     * @return
     */
    public Money getQualifyingItemSubTotal();
    
    public void setQualifyingItemSubTotal(Money qualifyingItemSubtotal);

    public Money getOrderMinSubTotal();

    public void setOrderMinSubTotal(Money orderMinSubTotal);

    public void setMarketingMessage(String marketingMessage);

    public String getMarketingMessage();

    public Money getTargetMinSubTotal();

    public void setTargetMinSubTotal(Money targetMinSubTotal);

    /**
     * Returns the offer codes that can be used to retrieve this Offer. These codes would be used in situations where
     * this Offer is not automatically considered (meaning {@link Offer#isAutomaticallyAdded()} is false}
     */
    public List<OfferCode> getOfferCodes();
    
    /**
     * Sets the offer codes that can be used to retrieve this Offer. These codes would be used in situations where
     * this Offer is not automatically considered (meaning {@link Offer#isAutomaticallyAdded()} is false}
     */
    public void setOfferCodes(List<OfferCode> offerCodes);

    public Boolean getRequiresRelatedTargetAndQualifiers();

    public void setRequiresRelatedTargetAndQualifiers(Boolean requiresRelatedTargetAndQualifiers);

    /**
     * This indicates how an Offer should be fulfilled to the customer, defaulting to order time discount.
     * Currently, this enumeration can be ORDER_DISCOUNT or FUTURE_CREDIT. "Future credit" means that the associated 
     * adjustment will be discounted at a later time to the customer via a credit. It is up to the implementor to 
     * decide how to achieve this. The adjustment entities have a new "isFutureCredit" field used to determine if an 
     * adjustment originated from an offer marked as FUTURE_CREDIT. Order, OrderItem and FulfillmentGroup have new 
     * accessor methods for retrieving the future credit values when they are needed to be fulfilled. 
     * 
     * Out-of-box, this field is disabled from admin and must be manually enabled to view, since it is not a typical 
     * requirement to most implementations. To enable, add the following to AdminConfig.java:
     *
     * @Merge("blAppConfigurationMap")
     * public Map<String, String> adminOfferAdjustmentType() {
     *     Map<String, String> appConfigMap = new HashMap<>();
     *     appConfigMap.put("admin.showIfProperty.offerAdjustmentType", "true");
     *     return appConfigMap;
     * }
     * 
     * @return
     */
    OfferAdjustmentType getAdjustmentType();

    /**
     * This indicates how an Offer should be fulfilled to the customer, defaulting to order time discount.
     * Currently, this enumeration can be ORDER_DISCOUNT or FUTURE_CREDIT. "Future credit" means that the associated 
     * adjustment will be discounted at a later time to the customer via a credit. It is up to the implementor to 
     * decide how to achieve this. The adjustment entities have a new "isFutureCredit" field used to determine if an 
     * adjustment originated from an offer marked as FUTURE_CREDIT. Order, OrderItem and FulfillmentGroup have new 
     * accessor methods for retrieving the future credit values when they are needed to be fulfilled. 
     *
     * Out-of-box, this field is disabled from admin and must be manually enabled to view, since it is not a typical 
     * requirement to most implementations. To enable, add the following to AdminConfig.java:
     *
     * @Merge("blAppConfigurationMap")
     * public Map<String, String> adminOfferAdjustmentType() {
     *     Map<String, String> appConfigMap = new HashMap<>();
     *     appConfigMap.put("admin.showIfProperty.offerAdjustmentType", "true");
     *     return appConfigMap;
     * }
     * 
     * @param adjustmentType
     */
    void setAdjustmentType(OfferAdjustmentType adjustmentType);

    /**
     * Future credit means that the associated adjustment will be discounted at a later time to the customer 
     * via a credit. It is up to the implementor to decide how to achieve this. The adjustment entities have 
     * a new "isFutureCredit" field used to determine if an adjustment originated from an offer marked as 
     * FUTURE_CREDIT. Order, OrderItem and FulfillmentGroup have new accessor methods for retrieving the future 
     * credit values when they are needed to be fulfilled.
     * 
     * See {@link Offer#getAdjustmentType()} for more info
     * 
     * @return
     */
    boolean isFutureCredit();

}
