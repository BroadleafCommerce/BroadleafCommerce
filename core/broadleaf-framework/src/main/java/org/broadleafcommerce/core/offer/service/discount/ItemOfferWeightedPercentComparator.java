/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.service.discount;

import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * @author Chad Harchar (charchar)
 */
public class ItemOfferWeightedPercentComparator implements Comparator<PromotableCandidateItemOffer> {

    public static ItemOfferWeightedPercentComparator INSTANCE = new ItemOfferWeightedPercentComparator();

    public int compare(PromotableCandidateItemOffer p1, PromotableCandidateItemOffer p2) {

        if (nullDetected(p1, p2)) {
            return nullCheckCompare(p1, p2);
        }

        Integer priority1 = p1.getPriority();
        Integer priority2 = p2.getPriority();

        int result = priority1.compareTo(priority2);
        
        if (result != 0) {
            return result;
        }

        BigDecimal weightedPercentSaved1 = p1.getWeightedPercentSaved();
        BigDecimal weightedPercentSaved2 = p2.getWeightedPercentSaved();

        if (nullDetected(weightedPercentSaved1, weightedPercentSaved2)) {
            return nullCheckCompare(weightedPercentSaved1, weightedPercentSaved2);
        }

        // highest weighted percent wins
        return weightedPercentSaved2.compareTo(weightedPercentSaved1);
    }

    private Boolean nullDetected(Object p1, Object p2) {
        return p1 == null || p2 == null;
    }

    private Integer nullCheckCompare(Object p1, Object p2) {
        if (p1 == null && p2 == null) {
            return 0;
        }

        if (p1 == null) {
            return 1;
        }

        return -1;
    }

}
