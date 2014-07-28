package org.broadleafcommerce.core.offer.dao;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public interface OfferCodeDaoExtensionHandler extends ExtensionHandler {

    /**
     * This allows for an alternative, or non-default query to be created / used to find an offer code by 
     * a code string.  An implementor may wish to use a different named query, or add a filter.
     * Implementors MUST return one of: ExtensionResultStatusType.HANDLED, ExtensionResultStatusType.HANDLED_STOP, or 
     * ExtensionResultStatusType.NOT_HANDLED.
     * 
     * ExtensionResultStatusType.HANDLED or ExtensionResultStatusType.HANDLED_STOP is returned, 
     * the resultHolder must be set with a valid instance of javax.persistence.Query. The cacheable and 
     * cacheRegion properties are hints and may be ignored by the implementor.
     * 
     * 
     * @param em
     * @param resultHolder
     * @param code
     * @param cacheable
     * @param cacheRegion
     * @return
     */
    public ExtensionResultStatusType createReadOfferCodeByCodeQuery(EntityManager em,
            ExtensionResultHolder<Query> resultHolder, String code, boolean cacheable, String cacheRegion);

}
