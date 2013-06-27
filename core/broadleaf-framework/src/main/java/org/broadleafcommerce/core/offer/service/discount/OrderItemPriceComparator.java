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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;

import java.util.Comparator;

/**
 * 
 * @author jfischer
 *
 */
public class OrderItemPriceComparator implements Comparator<PromotableOrderItem> {
    
    private boolean applyToSalePrice = false;
    
    public OrderItemPriceComparator(boolean applyToSalePrice) {
        this.applyToSalePrice = applyToSalePrice;
    }

    public int compare(PromotableOrderItem c1, PromotableOrderItem c2) {
        
        Money price = c1.getPriceBeforeAdjustments(applyToSalePrice);
        Money price2 = c2.getPriceBeforeAdjustments(applyToSalePrice);
        
        // highest amount first
        return price2.compareTo(price);
    }

}
