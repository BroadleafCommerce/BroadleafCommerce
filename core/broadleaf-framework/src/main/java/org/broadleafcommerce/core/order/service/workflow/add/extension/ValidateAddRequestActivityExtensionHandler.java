package org.broadleafcommerce.core.order.service.workflow.add.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.ProcessContext;

/**
 * @author Nick Crum ncrum
 */
public interface ValidateAddRequestActivityExtensionHandler extends ExtensionHandler {

    /**
     * This extension allows for one to validate an add request without having to override or extend
     * {@link org.broadleafcommerce.core.order.service.workflow.add.ValidateAddRequestActivity}.
     * @param resultHolder
     * @return
     */
    public ExtensionResultStatusType validate(ProcessContext<CartOperationRequest> context, ExtensionResultHolder<Exception> resultHolder);
}
