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
package org.broadleafcommerce.core.pricing.service.workflow;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Called during the pricing workflow to set the merchandise total for each FulfillmentGroup
 * in an Order. This activity should come before any activity dealing with pricing FulfillmentGroups
 * 
 * @author Phillip Verheyden
 * @see {@link FulfillmentGroup#setMerchandiseTotal(Money)}, {@link FulfillmentGroup#getMerchandiseTotal()}
 */
@Component("blFulfillmentGroupMerchandiseTotalActivity")
public class FulfillmentGroupMerchandiseTotalActivity extends BaseActivity<ProcessContext<Order>> {

    public static final int ORDER = 4000;
    
    public FulfillmentGroupMerchandiseTotalActivity() {
        setOrder(ORDER);
    }
    
    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();

        for(FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            Money merchandiseTotal = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, fulfillmentGroup.getOrder().getCurrency());
            for(FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                OrderItem item = fulfillmentGroupItem.getOrderItem();
                merchandiseTotal = merchandiseTotal.add(item.getTotalPrice());
            }
            fulfillmentGroup.setMerchandiseTotal(merchandiseTotal);
        }
        context.setSeedData(order);

        return context;
    }

}
