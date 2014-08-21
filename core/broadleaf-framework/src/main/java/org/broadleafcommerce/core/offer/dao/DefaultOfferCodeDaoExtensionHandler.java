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
