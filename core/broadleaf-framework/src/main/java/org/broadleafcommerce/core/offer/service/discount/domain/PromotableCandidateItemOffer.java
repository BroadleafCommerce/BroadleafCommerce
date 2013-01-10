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

package org.broadleafcommerce.core.offer.service.discount.domain;

import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.money.Money;

public interface PromotableCandidateItemOffer {

    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap();

    public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

    public List<PromotableOrderItem> getCandidateTargets();

    public void setCandidateTargets(List<PromotableOrderItem> candidateTargets);

    public Money calculateSavingsForOrderItem(PromotableOrderItem orderItem, int qtyToReceiveSavings);

    public Money getPotentialSavings();

    public CandidateItemOffer getDelegate();
    
    public void reset();
    
    public Money calculatePotentialSavings();
    
    public int calculateMaximumNumberOfUses();
    
    public int calculateMaxUsesForItemCriteria(OfferItemCriteria itemCriteria, Offer promotion);
    
    public void setOrderItem(PromotableOrderItem orderItem);
    
    public PromotableCandidateItemOffer clone();
    
    public int getPriority();
    
    public Offer getOffer();
    
    public void setOffer(Offer offer);
    
    public PromotableOrderItem getOrderItem();

    public int getUses();

    public void addUse();
}