package org.broadleafcommerce.openadmin.server.service.extension;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Component;

/**
 * @author Jacob Mitash
 */
@Component("blCriteriaTransferObjectExtensionManager")
public class CriteriaTransferObjectExtensionManager extends ExtensionManager<CriteriaTransferObjectExtensionHandler> {

    public CriteriaTransferObjectExtensionManager() {
        super(CriteriaTransferObjectExtensionHandler.class);
    }
}
