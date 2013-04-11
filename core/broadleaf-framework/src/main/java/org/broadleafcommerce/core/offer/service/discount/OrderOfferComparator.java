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

import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;

import java.util.Comparator;

/**
 * 
 * @author jfischer
 *
 */
public class OrderOfferComparator implements Comparator<PromotableCandidateOrderOffer> {
    
    public static OrderOfferComparator INSTANCE = new OrderOfferComparator();

    public int compare(PromotableCandidateOrderOffer p1, PromotableCandidateOrderOffer p2) {
        
        Integer priority1 = p1.getPriority();
        Integer priority2 = p2.getPriority();
        
        int result = priority1.compareTo(priority2);
        
        if (result == 0) {
            // highest potential savings wins
            return p2.getPotentialSavings().compareTo(p1.getPotentialSavings());
        }
        return result;
    }

}
