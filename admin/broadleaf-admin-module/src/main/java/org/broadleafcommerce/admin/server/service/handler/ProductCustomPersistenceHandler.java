/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class ProductCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(ProductCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return !ArrayUtils.isEmpty(customCriteria) && "productDirectEdit".equals(customCriteria[0]) && Product.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Product adminInstance = (Product) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Product.class.getName(), persistencePerspective);
            adminInstance = (Product) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            if (adminInstance.getDefaultCategory() != null && !adminInstance.getAllParentCategories().contains(adminInstance.getDefaultCategory())) {
                adminInstance.getAllParentCategories().add(adminInstance.getDefaultCategory());
            }

            adminInstance = (Product) dynamicEntityDao.merge(adminInstance);

            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            LOG.error("Unable to add entity for " + entity.getType()[0], e);
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Product.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Product adminInstance = (Product) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            adminInstance = (Product) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            if (adminInstance.getDefaultCategory() != null && !adminInstance.getAllParentCategories().contains(adminInstance.getDefaultCategory())) {
                adminInstance.getAllParentCategories().add(adminInstance.getDefaultCategory());
            }

            adminInstance = (Product) dynamicEntityDao.merge(adminInstance);

            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            LOG.error("Unable to update entity for " + entity.getType()[0], e);
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }
}
