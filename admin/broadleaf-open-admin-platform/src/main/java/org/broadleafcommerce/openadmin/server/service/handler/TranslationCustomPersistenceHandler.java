/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.server.service.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Resource;

/**
 * Custom persistence handler for Translations, it verifies on "add" that the combination of the 4 "key" fields
 * is not repeated (as in a software-enforced unique index, which is not utilized because of sandboxing and multitenancy concerns).
 * 
 * @author gdiaz
 */
@Component("blTranslationCustomPersistenceHandler")
public class TranslationCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private final Log LOG = LogFactory.getLog(TranslationCustomPersistenceHandler.class);

    @Resource(name = "blSystemPropertiesService")
    protected SystemPropertiesService spService;

    @Resource(name = "blTranslationService")
    protected TranslationService translationService;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    protected Boolean classMatches(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return Translation.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return classMatches(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return classMatches(persistencePackage);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            // Get an instance of SystemProperty with the updated values from the form
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Translation adminInstance = (Translation) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Translation.class.getName(), persistencePerspective);
            adminInstance = (Translation) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            // We only want to check for duplicates during a save
            if (!sandBoxHelper.isReplayOperation()) {
                Translation res = translationService.getTranslation(adminInstance.getEntityType(), adminInstance.getEntityId(), adminInstance.getFieldName(), adminInstance.getLocaleCode());
                if (res != null) {
                    Entity errorEntity = new Entity();
                    errorEntity.setType(new String[] { res.getClass().getName() });
                    errorEntity.addValidationError("localeCode", "translation.record.exists.for.locale");
                    return errorEntity;
                }
            }
            persistencePackage.setRequestingEntityName(adminInstance.getEntityType().getFriendlyType() + "|" + adminInstance.getFieldName() + "|" + adminInstance.getLocaleCode());
            adminInstance = dynamicEntityDao.merge(adminInstance);
            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform add for entity: " + Translation.class.getName(), e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Translation adminInstance = (Translation) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Translation.class.getName(), persistencePerspective);
            adminInstance = (Translation) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            if(StringUtils.isEmpty(persistencePackage.getRequestingEntityName())) {
                persistencePackage.setRequestingEntityName(adminInstance.getEntityType().getFriendlyType() + "|" + adminInstance.getFieldName() + "|" + adminInstance.getLocaleCode());
            }
            OperationType updateType = persistencePackage.getPersistencePerspective().getOperationTypes().getUpdateType();
            return helper.getCompatibleModule(updateType).update(persistencePackage);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform add for entity: " + Translation.class.getName(), e);
        }
    }
    
}
