/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.order.service.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.ConfigurableOrderItemRequest;

/**
 * @author Jon Fleschler (jfleschler)
 */
public interface OrderItemServiceExtensionHandler extends ExtensionHandler {

    /**
     *
     *
     * @param item
     * @return
     */
    public ExtensionResultStatusType modifyOrderItemPrices(OrderItem item);

    /**
     * Allows a module to append additional rule variables that may be needed for order item evaluation
     * @param orderItem - the promotable order item in consideration
     * @return
     */
    ExtensionResultStatusType applyAdditionalOrderItemProperties(OrderItem orderItem);


    /**
     *
     *
     * @param configurableOrderItem
     * @return
     */
    public ExtensionResultStatusType modifyOrderItemRequest(ConfigurableOrderItemRequest configurableOrderItem);

    /**
     *
     *
     * @param itemRequest
     * @param orderItem
     * @return
     */
    public ExtensionResultStatusType mergeOrderItemRequest(ConfigurableOrderItemRequest itemRequest, OrderItem orderItem);

}
