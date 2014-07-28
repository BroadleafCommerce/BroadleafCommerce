/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
