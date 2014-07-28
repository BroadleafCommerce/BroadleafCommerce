package org.broadleafcommerce.core.offer.dao;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Default implementation of OfferCodeDaoExtensionHandler.
 * 
 * @author Kelly Tisdell
 *
 */
public class DefaultOfferCodeDaoExtensionHandler extends AbstractExtensionHandler implements OfferCodeDaoExtensionHandler {

    @Override
    public ExtensionResultStatusType createReadOfferCodeByCodeQuery(EntityManager em, ExtensionResultHolder<Query> resultHolder, String code, boolean cacheable, String cacheRegion) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
