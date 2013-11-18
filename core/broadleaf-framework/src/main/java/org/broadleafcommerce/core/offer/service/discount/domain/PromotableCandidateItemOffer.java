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
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public interface PromotableCandidateItemOffer extends Serializable {

    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap();

    public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

    public List<PromotableOrderItem> getCandidateTargets();

    public void setCandidateTargets(List<PromotableOrderItem> candidateTargets);

    public Money getPotentialSavings();

    public void setPotentialSavings(Money savings);

    public boolean hasQualifyingItemCriteria();

    /**
     * Public only for unit testing - not intended to be called
     */
    public Money calculateSavingsForOrderItem(PromotableOrderItem orderItem, int qtyToReceiveSavings);

    public int calculateMaximumNumberOfUses();
    
    /**
     * Returns the number of item quantities that qualified as targets for 
     * this promotion.
     */
    public int calculateTargetQuantityForTieredOffer();

    /**
     * Determines the max number of times this itemCriteria might apply.    This calculation does 
     * not take into account other promotions.   It is useful only to assist in prioritizing the order to process
     * the promotions. 
     * 
     * @param itemCriteria
     * @param promotion
     * @return
     */
    public int calculateMaxUsesForItemCriteria(OfferItemCriteria itemCriteria, Offer promotion);
    
    public int getPriority();
    
    public Offer getOffer();

    public int getUses();

    public void addUse();
    
    public boolean isLegacyOffer();
}