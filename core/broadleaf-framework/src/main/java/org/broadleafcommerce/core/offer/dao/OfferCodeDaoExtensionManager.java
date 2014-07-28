package org.broadleafcommerce.core.offer.dao;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Component;

@Component("blOfferCodeDaoExtensionManager")
public class OfferCodeDaoExtensionManager extends ExtensionManager<OfferCodeDaoExtensionHandler> {

    public OfferCodeDaoExtensionManager() {
        super(OfferCodeDaoExtensionHandler.class);
    }
}
