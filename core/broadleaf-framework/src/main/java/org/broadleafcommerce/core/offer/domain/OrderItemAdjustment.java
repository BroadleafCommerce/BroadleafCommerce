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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.OrderItem;

public interface OrderItemAdjustment extends Adjustment {

    public OrderItem getOrderItem();

    public void init(OrderItem orderItem, Offer offer, String reason);

    public void setOrderItem(OrderItem orderItem);

    /**
     * Even for items that are on sale, it is possible that an adjustment was made
     * to the retail price that gave the customer a better offer.
     *
     * Since some offers can be applied to the sale price and some only to the
     * retail price, this setting provides the required value.
     *
     * @return true if this adjustment was applied to the sale price
     */
    public boolean isAppliedToSalePrice();

    public void setAppliedToSalePrice(boolean appliedToSalePrice);

    /**
     * Value of this adjustment relative to the retail price.
     * @return
     */
    public Money getRetailPriceValue();

    public void setRetailPriceValue(Money retailPriceValue);

    /**
     * Value of this adjustment relative to the sale price.
     *
     * @return
     */
    public Money getSalesPriceValue();

    public void setSalesPriceValue(Money salesPriceValue);
}
