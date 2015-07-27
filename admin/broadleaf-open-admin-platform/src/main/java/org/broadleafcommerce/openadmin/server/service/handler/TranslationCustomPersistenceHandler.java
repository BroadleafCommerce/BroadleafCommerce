/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.i18n.service.TranslationService;
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
        return false;
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

            Translation res = translationService.getTranslation(adminInstance.getEntityType(), adminInstance.getEntityId(), adminInstance.getFieldName(), adminInstance.getLocaleCode());
            if (res == null) {
                adminInstance = dynamicEntityDao.merge(adminInstance);
                Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);
                return adminEntity;
            } else {
                Entity errorEntity = new Entity();
                errorEntity.addValidationError("localeCode", "translation.record.exists.for.locale");
                return errorEntity;
            }
        } catch (Exception e) {
            throw new ServiceException("Unable to perform add for entity: " + Translation.class.getName(), e);
        }
    }

}
