package org.broadleafcommerce.core.order.service.workflow.add.extension;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * @author Nick Crum ncrum
 */
@Service("blValidateAddRequestActivityExtensionManager")
public class ValidateAddRequestActivityExtensionManager extends ExtensionManager<ValidateAddRequestActivityExtensionHandler> {

    public ValidateAddRequestActivityExtensionManager() {
        super(ValidateAddRequestActivityExtensionHandler.class);
    }
}
