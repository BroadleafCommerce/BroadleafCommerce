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
