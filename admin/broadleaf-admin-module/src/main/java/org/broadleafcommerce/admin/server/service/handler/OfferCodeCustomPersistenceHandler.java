/*-
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.ClassCustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component("blOfferCodeCustomPersistenceHandler")
public class OfferCodeCustomPersistenceHandler extends ClassCustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(OfferCodeCustomPersistenceHandler.class);
    private static final String ERROR_MESSAGE_KEY = "OfferCode_Duplication_Validation_Failure";

    @Resource(name = "blOfferCodeDao")
    protected OfferCodeDao offerCodeDao;

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        try {
            return persistencePackage.getCeilingEntityFullyQualifiedClassname() != null
                    && OfferCode.class.isAssignableFrom(Class.forName(persistencePackage.getCeilingEntityFullyQualifiedClassname()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            OfferCode offerCodeInstance = (OfferCode) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> offerCodeProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), persistencePerspective);
            offerCodeInstance = (OfferCode) helper.createPopulatedInstance(offerCodeInstance, entity, offerCodeProperties, false);

            Entity errorEntity = validateOfferCode(entity, offerCodeInstance);
            if (errorEntity != null) {
                return errorEntity;
            }

            offerCodeInstance = dynamicEntityDao.merge(offerCodeInstance);
            return helper.getRecord(offerCodeProperties, offerCodeInstance, null, null);

        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> offerCodeProperties = helper.getSimpleMergedProperties(OfferCode.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, offerCodeProperties);
            OfferCode offerCodeInstance = (OfferCode) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            offerCodeInstance = (OfferCode) helper.createPopulatedInstance(offerCodeInstance, entity, offerCodeProperties, false);

            Entity errorEntity = validateOfferCode(entity, offerCodeInstance);
            if (errorEntity != null) {
                return errorEntity;
            }

            offerCodeInstance = dynamicEntityDao.merge(offerCodeInstance);
            return helper.getRecord(offerCodeProperties, offerCodeInstance, null, null);

        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }

    protected Entity validateOfferCode(Entity entity, OfferCode offerCode) {
        OfferCode existedCode = offerCodeDao.readOfferCodeByCode(offerCode.getOfferCode());
        if (existedCode != null && !offerCode.equals(existedCode)) {
            entity.addValidationError("offerCode", ERROR_MESSAGE_KEY);
            return entity;
        }
        return null;
    }

}
