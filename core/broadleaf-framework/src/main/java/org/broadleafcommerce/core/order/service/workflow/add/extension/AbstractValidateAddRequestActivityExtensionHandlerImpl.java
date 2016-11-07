package org.broadleafcommerce.core.order.service.workflow.add.extension;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.workflow.ProcessContext;

/**
 * @author Nick Crum ncrum
 */
public abstract class AbstractValidateAddRequestActivityExtensionHandlerImpl extends AbstractExtensionHandler
        implements ValidateAddRequestActivityExtensionHandler {

    @Override
    public ExtensionResultStatusType validate(ProcessContext<CartOperationRequest> context, ExtensionResultHolder<Exception> resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
