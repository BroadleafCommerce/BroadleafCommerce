/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;

public interface Offer extends Serializable {

    void setId(Long id);

    Long getId();

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    OfferType getType();

    void setType(OfferType offerType);

    OfferDiscountType getDiscountType();

    void setDiscountType(OfferDiscountType type);

    BigDecimal getValue();

    void setValue(BigDecimal value);

    int getPriority();

    void setPriority(int priority);

    Date getStartDate();

    void setStartDate(Date startDate);

    Date getEndDate();

    void setEndDate(Date endDate);

    /**
     * @deprecated
     * Use isCombinable instead.
     * @return
     */
    boolean isStackable();

    /**
     * @deprecated
     * calls {@link #setCombinableWithOtherOffers(boolean)}
     * @param stackable
     */
    void setStackable(boolean stackable);

    String getTargetSystem();

    void setTargetSystem(String targetSystem);

    boolean getApplyDiscountToSalePrice();

    void setApplyDiscountToSalePrice(boolean applyToSalePrice);

    @Deprecated
    String getAppliesToOrderRules();

    @Deprecated
    void setAppliesToOrderRules(String appliesToRules);

    @Deprecated
    String getAppliesToCustomerRules();

    @Deprecated
    void setAppliesToCustomerRules(String appliesToCustomerRules);

    @Deprecated
    boolean isApplyDiscountToMarkedItems();

    @Deprecated
    void setApplyDiscountToMarkedItems(boolean applyDiscountToMarkedItems);
    
    OfferItemRestrictionRuleType getOfferItemQualifierRuleType();

    void setOfferItemQualifierRuleType(OfferItemRestrictionRuleType restrictionRuleType);
    
    OfferItemRestrictionRuleType getOfferItemTargetRuleType();

    void setOfferItemTargetRuleType(OfferItemRestrictionRuleType restrictionRuleType);

    /**
     * Returns false if this offer is not combinable with other offers of the same type.
     * For example, if this is an Item offer it could be combined with other Order or FG offers
     * but it cannot be combined with other Item offers.
     * 
     * @return
     */
    boolean isCombinableWithOtherOffers();

    void setCombinableWithOtherOffers(boolean combinableWithOtherOffers);

    /**
     * Returns true if the offer system should automatically add this offer for consideration (versus requiring a code or 
     * other delivery mechanism).    This does not guarantee that the offer will qualify.   All rules associated with this
     * offer must still pass.   A true value here just means that the offer will be considered.
     * 
     * For backwards compatibility, if the underlying property is null, this method will check the 
     * {@link #getDeliveryType()} method and return true if that value is set to AUTOMATIC.    
     * 
     * If still null, this value will return false.
     * 
     * @return
     */
    boolean isAutomaticallyAdded();

    /**
     * Sets whether or not this offer should be automatically considered for consideration (versus requiring a code or 
     * other delivery mechanism).
     * @see #isAutomaticallyAdded()
     */
    void setAutomaticallyAdded(boolean automaticallyAdded);

    /**
     * @deprecated Replaced by isAutomaticallyApplied property.   In prior versions of Broadleaf deliveryType was used to 
     * differentiate "automatic" orders from those requiring a code.   If the underlying property is null, 
     * this method will return a delivery type based on the isAutomatic property. 
     * @return
     */
    OfferDeliveryType getDeliveryType();

    /**
     * @deprecated Replaced by setAutomaticallyApplied(boolean val).
     * @param deliveryType
     */
    void setDeliveryType(OfferDeliveryType deliveryType);

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
    Long getMaxUsesPerCustomer();

    /**
     * Sets the maximum number of times that this offer
       can be used by the same customer.  Intended as a transient
     * field that gets derived from the other persisted max uses fields
     * including maxUsesPerOrder and maxUsesPerCustomer.
     *
     * 0 or null indicates unlimited usage.
     *
     * @param maxUses
     */
    void setMaxUsesPerCustomer(Long maxUses);

    /**
     * Returns the maximum number of times that this offer
     * can be used in the current order.
     *
     * 0 indicates unlimited usage.
     *
     * @return
     */
    int getMaxUses() ;

    /**
     * Sets the maximum number of times that this offer
     * can be used in the current order.
     *
     * 0 indicates unlimited usage.
     *
     * @param maxUses
     */
    void setMaxUses(int maxUses) ;

    @Deprecated
    int getUses() ;

    @Deprecated
    void setUses(int uses) ;

    Set<OfferItemCriteria> getQualifyingItemCriteria();

    void setQualifyingItemCriteria(Set<OfferItemCriteria> qualifyingItemCriteria);

    Set<OfferItemCriteria> getTargetItemCriteria();

    void setTargetItemCriteria(Set<OfferItemCriteria> targetItemCriteria);
    
    Boolean isTotalitarianOffer();

    void setTotalitarianOffer(Boolean totalitarianOffer);
    
    Map<String, OfferRule> getOfferMatchRules();

    void setOfferMatchRules(Map<String, OfferRule> offerMatchRules);
    
    Boolean getTreatAsNewFormat();

    void setTreatAsNewFormat(Boolean treatAsNewFormat);
    
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
    Money getQualifyingItemSubTotal();
    
    void setQualifyingItemSubTotal(Money qualifyingItemSubtotal);

    void setMarketingMessage(String marketingMessage);

    String getMarketingMessage();

    List<OfferCode> getOfferCodes();

    void setOfferCodes(List<OfferCode> offerCodes);

    public Boolean getRequiresRelatedTargetAndQualifiers();

    public void setRequiresRelatedTargetAndQualifiers(Boolean requiresRelatedTargetAndQualifiers);

}
