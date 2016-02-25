/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import javax.persistence.PersistenceException;

/**
 * When deleting a {@link SearchFacet}, there needs to be a check to see if it is applied to a
 * {@link org.broadleafcommerce.core.catalog.domain.Category} already. If so, then trying to removed it should cause a
 * validation error letting the user know that the {@link SearchFacet} cannot be deleted until its reference is removed.
 * Otherwise, a Hibernate exception will be displayed to users, which will not contain meaningful information for them.
 *
 * @author Nathan Moore (nathandmoore)
 */
@Component("blSearchFacetCustomPersistenceHandler")
public class SearchFacetCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SearchFacetCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname() != null
                && persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(SearchFacet.class.getName());
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper)
            throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            OperationType opType = persistencePackage.getPersistencePerspective().getOperationTypes().getRemoveType();
            if (opType != null) {
                helper.getCompatibleModule(opType).remove(persistencePackage);
            } else {
                helper.getCompatibleModule(OperationType.BASIC).remove(persistencePackage);
            }
        } catch (Exception e) {
            if (e.getCause() instanceof PersistenceException) {
                // If a persistence exception (should be a foreign key constraint)
                LOG.error("Unable to execute persistence activity because entity in use", e);
                throw new ValidationException(entity, "Unable to remove entity for this Search Facet: In use by a Category.");
            } else {
                LOG.error("Unable to execute persistence activity", e);
                throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
            }
        }
    }
}
