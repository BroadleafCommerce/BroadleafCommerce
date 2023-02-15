/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.order.service.type.OrderStatus;

import java.util.Date;
import java.util.List;

public interface OrderItemDao {

    OrderItem readOrderItemById(Long orderItemId);

    OrderItem save(OrderItem orderItem);

    void delete(OrderItem orderItem);

    OrderItem create(OrderItemType orderItemType);

    OrderItem saveOrderItem(OrderItem orderItem);
    
    PersonalMessage createPersonalMessage();

    OrderItemPriceDetail createOrderItemPriceDetail();

    OrderItemQualifier createOrderItemQualifier();

    /**
     * Sets the initial orderItemPriceDetail for the item.
     */
    OrderItemPriceDetail initializeOrderItemPriceDetails(OrderItem item);

    List<OrderItem> readOrderItemsForCustomersInDateRange(List<Long> customerIds, Date startDate, Date endDate);

    Long readNumberOfOrderItems();

    List<OrderItem> readBatchOrderItems(int start, int count, List<OrderStatus> statuses);
}
