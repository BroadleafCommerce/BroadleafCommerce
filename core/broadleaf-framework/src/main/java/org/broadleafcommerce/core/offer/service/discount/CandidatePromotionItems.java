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

package org.broadleafcommerce.core.offer.service.discount;

import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class CandidatePromotionItems {
    
    protected HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<PromotableOrderItem>>();
    protected boolean isMatchedQualifier = false;
    protected List<PromotableOrderItem> candidateTargets = new ArrayList<PromotableOrderItem>();
    protected boolean isMatchedTarget = false;
    
    public void addQualifier(OfferItemCriteria criteria, PromotableOrderItem item) {
        List<PromotableOrderItem> itemList = candidateQualifiersMap.get(criteria);
        if (itemList == null) {
            itemList = new ArrayList<PromotableOrderItem>();
            candidateQualifiersMap.put(criteria, itemList);
        }
        itemList.add(item);
    }

    public boolean isMatchedQualifier() {
        return isMatchedQualifier;
    }

    public void setMatchedQualifier(boolean isMatchedCandidate) {
        this.isMatchedQualifier = isMatchedCandidate;
    }

    public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap() {
        return candidateQualifiersMap;
    }
    
    public void addTarget(PromotableOrderItem item) {
        candidateTargets.add(item);
    }

    public boolean isMatchedTarget() {
        return isMatchedTarget;
    }

    public void setMatchedTarget(boolean isMatchedCandidate) {
        this.isMatchedTarget = isMatchedCandidate;
    }

    public List<PromotableOrderItem> getCandidateTargets() {
        return candidateTargets;
    }

}
