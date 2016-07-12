package org.broadleafcommerce.core.web.controller.cart.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.service.call.ConfigurableOrderItemRequest;

/**
 * @author Jon Fleschler (jfleschler)
 */
public interface BroadleafCartControllerExtensionHandler extends ExtensionHandler {

    /**
     *
     *
     * @param configurableOrderItem
     * @return
     */
    public ExtensionResultStatusType modifyOrderItemRequest(ConfigurableOrderItemRequest configurableOrderItem);
}
