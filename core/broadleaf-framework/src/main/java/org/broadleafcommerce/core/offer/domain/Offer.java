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

package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;

public interface Offer extends Serializable {

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

    public void setPriority(int priority);

    public Date getStartDate();

    public void setStartDate(Date startDate);

    public Date getEndDate();

    public void setEndDate(Date endDate);

    public boolean isStackable();

    public void setStackable(boolean stackable);

    public String getTargetSystem();

    public void setTargetSystem(String targetSystem);

    public boolean getApplyDiscountToSalePrice();

    public void setApplyDiscountToSalePrice(boolean applyToSalePrice);

    @Deprecated
    public String getAppliesToOrderRules();

    @Deprecated
    public void setAppliesToOrderRules(String appliesToRules);

    @Deprecated
    public String getAppliesToCustomerRules();

    @Deprecated
    public void setAppliesToCustomerRules(String appliesToCustomerRules);

    @Deprecated
    public boolean isApplyDiscountToMarkedItems();

    @Deprecated
    public void setApplyDiscountToMarkedItems(boolean applyDiscountToMarkedItems);
    
    public OfferItemRestrictionRuleType getOfferItemQualifierRuleType();

    public void setOfferItemQualifierRuleType(OfferItemRestrictionRuleType restrictionRuleType);
    
    public OfferItemRestrictionRuleType getOfferItemTargetRuleType();

    public void setOfferItemTargetRuleType(OfferItemRestrictionRuleType restrictionRuleType);

    public boolean isCombinableWithOtherOffers();

    public void setCombinableWithOtherOffers(boolean combinableWithOtherOffers);

    public OfferDeliveryType getDeliveryType();

    public void setDeliveryType(OfferDeliveryType deliveryType);

    public int getMaxUses() ;

    public void setMaxUses(int maxUses) ;

    @Deprecated
    public int getUses() ;

    @Deprecated
    public void setUses(int uses) ;

    public Set<OfferItemCriteria> getQualifyingItemCriteria();

    public void setQualifyingItemCriteria(Set<OfferItemCriteria> qualifyingItemCriteria);

    public OfferItemCriteria getTargetItemCriteria();

    public void setTargetItemCriteria(OfferItemCriteria targetItemCriteria);
    
    public Boolean isTotalitarianOffer();

    public void setTotalitarianOffer(Boolean totalitarianOffer);
    
    public Map<String, OfferRule> getOfferMatchRules();

    public void setOfferMatchRules(Map<String, OfferRule> offerMatchRules);
    
    public Boolean getTreatAsNewFormat();

    public void setTreatAsNewFormat(Boolean treatAsNewFormat);
    
}
