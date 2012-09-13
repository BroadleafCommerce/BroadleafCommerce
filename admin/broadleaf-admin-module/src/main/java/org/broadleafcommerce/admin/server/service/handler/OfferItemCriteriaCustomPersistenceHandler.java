/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.admin.server.service.handler;

import java.util.Map;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

/**
 * @author jfischer
 */
public class OfferItemCriteriaCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname() != null && persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(OfferItemCriteria.class.getName());
    }
    
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    protected void removeHtmlEncoding(Entity entity) {
        Property prop = entity.findProperty("orderItemMatchRule");
        if (prop != null && prop.getValue() != null) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            prop.setValue(prop.getRawValue());
        }
    }

    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        removeHtmlEncoding(entity);
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            OfferItemCriteria offerItemCriteria = (OfferItemCriteria) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(OfferItemCriteria.class.getName(), persistencePerspective);
            offerItemCriteria = (OfferItemCriteria) helper.createPopulatedInstance(offerItemCriteria, entity, offerProperties, false);

            offerItemCriteria = (OfferItemCriteria) dynamicEntityDao.persist(offerItemCriteria);

            Entity offerEntity = helper.getRecord(offerProperties, offerItemCriteria, null, null);

            return offerEntity;
        } catch (Exception e) {
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        removeHtmlEncoding(entity);
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> offerProperties = helper.getSimpleMergedProperties(OfferItemCriteria.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, offerProperties);
            OfferItemCriteria offerItemCriteria = (OfferItemCriteria) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            offerItemCriteria = (OfferItemCriteria) helper.createPopulatedInstance(offerItemCriteria, entity, offerProperties, false);

            offerItemCriteria = (OfferItemCriteria) dynamicEntityDao.merge(offerItemCriteria);

            Entity offerEntity = helper.getRecord(offerProperties, offerItemCriteria, null, null);

            return offerEntity;
        } catch (Exception e) {
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }
}
