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

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.call.ConfigurableOrderItemRequest;

/**
 * @author Jon Fleschler (jfleschler)
 */
public abstract class AbstractOrderItemServiceExtensionHandler extends AbstractExtensionHandler
        implements OrderItemServiceExtensionHandler {

    @Override
    public ExtensionResultStatusType modifyOrderItemPrices(OrderItem item) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType applyAdditionalOrderItemProperties(OrderItem orderItem) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyOrderItemRequest(ConfigurableOrderItemRequest configurableOrderItem) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType mergeOrderItemRequest(ConfigurableOrderItemRequest itemRequest, OrderItem orderItem) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
