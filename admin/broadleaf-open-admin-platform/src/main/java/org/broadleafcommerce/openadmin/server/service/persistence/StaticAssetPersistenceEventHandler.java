/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.springframework.stereotype.Component;

/**
* This handler removes the given media asset from disk when the entities that have been archived.
* {@link BroadleafFileService}
* 
* @author JSebastian (johnsebastian)
*/

@Component("blStaticAssetPersistenceEventHandler")
public class StaticAssetPersistenceEventHandler extends PersistenceManagerEventHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(StaticAssetPersistenceEventHandler.class);

    @Resource(name = "blFileService")
    protected BroadleafFileService blcFileService;

    @Override
    public PersistenceManagerEventHandlerResponse postRemove(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException {
        if (persistencePackage.getCeilingEntityFullyQualifiedClassname().endsWith("StaticAsset")) {
            FileWorkArea fileWorkArea = blcFileService.initializeWorkArea();
            blcFileService.removeResource(persistencePackage.getRequestingEntityName());
            blcFileService.closeWorkArea(fileWorkArea);
        }
        return new PersistenceManagerEventHandlerResponse().
                withStatus(PersistenceManagerEventHandlerResponse.PersistenceManagerEventHandlerResponseStatus.HANDLED);
    }

}
