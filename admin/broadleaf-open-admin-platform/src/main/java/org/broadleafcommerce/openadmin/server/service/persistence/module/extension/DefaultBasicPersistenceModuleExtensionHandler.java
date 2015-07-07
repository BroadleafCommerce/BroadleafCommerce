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
package org.broadleafcommerce.openadmin.server.service.persistence.module.extension;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.util.TypedPredicate;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Default implementation for the core framework.
 *
 * @see org.broadleafcommerce.openadmin.server.service.persistence.module.extension.BasicPersistenceModuleExtensionHandler
 * @author Jeff Fischer
 */
@Service("blDefaultBasicPersistenceModuleExtensionHandler")
public class DefaultBasicPersistenceModuleExtensionHandler extends AbstractBasicPersistenceModuleExtensionHandler {

    @Resource(name = "blBasicPersistenceModuleExtensionManager")
    protected BasicPersistenceModuleExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        setPriority(BasicPersistenceModuleExtensionHandler.DEFAULT_PRIORITY);
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType rebalanceForAdd(BasicPersistenceModule basicPersistenceModule,
                                                     PersistencePackage persistencePackage, Serializable instance,
                                                     Map<String, FieldMetadata> mergedProperties,
                                                     ExtensionResultHolder<Serializable> resultHolder) {
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get
                    (PersistencePerspectiveItemType.FOREIGNKEY);
            CriteriaTransferObject cto = new CriteriaTransferObject();
            FilterAndSortCriteria sortCriteria = cto.get(foreignKey.getSortField());
            sortCriteria.setSortAscending(foreignKey.getSortAscending());
            List<FilterMapping> filterMappings = basicPersistenceModule.getFilterMappings(persistencePerspective,
                    cto, persistencePackage.getCeilingEntityFullyQualifiedClassname(), mergedProperties);
            int totalRecords = basicPersistenceModule.getTotalRecords(persistencePackage
                    .getCeilingEntityFullyQualifiedClassname(), filterMappings);
            Class<?> type = basicPersistenceModule.getFieldManager().getField(instance.getClass(),
                    foreignKey.getSortField()).getType();
            boolean isBigDecimal = BigDecimal.class.isAssignableFrom(type);
            basicPersistenceModule.getFieldManager().setFieldValue(instance, foreignKey.getSortField(),
                    isBigDecimal ? new BigDecimal(totalRecords + 1) : Long.valueOf(totalRecords + 1));

            resultHolder.setResult(instance);

        } catch (IllegalAccessException e) {
            throw ExceptionHelper.refineException(e);
        } catch (InstantiationException e) {
            throw ExceptionHelper.refineException(e);
        }

        return ExtensionResultStatusType.HANDLED;
    }

    @Override
    public ExtensionResultStatusType rebalanceForUpdate(final BasicPersistenceModule basicPersistenceModule,
                                                        PersistencePackage persistencePackage, Serializable instance,
                                                        Map<String, FieldMetadata> mergedProperties,
                                                        Object primaryKey, ExtensionResultHolder<Serializable> resultHolder) {
        try {
            Entity entity = persistencePackage.getEntity();
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get
                    (PersistencePerspectiveItemType.FOREIGNKEY);
            Integer requestedSequence = Integer.valueOf(entity.findProperty(foreignKey.getSortField()).getValue());
            Integer previousSequence = new BigDecimal(String.valueOf(basicPersistenceModule.getFieldManager()
                    .getFieldValue(instance, foreignKey.getSortField()))).intValue();
            final String idPropertyName = basicPersistenceModule.getIdPropertyName(mergedProperties);
            final Object pKey = primaryKey;

            instance = basicPersistenceModule.createPopulatedInstance(instance, entity, mergedProperties, false,
                    persistencePackage.isValidateUnsubmittedProperties());

            if (!previousSequence.equals(requestedSequence)) {
                // Sequence has changed. Rebalance the list
                Serializable manyToField = (Serializable) basicPersistenceModule.getFieldManager().getFieldValue
                        (instance, foreignKey.getManyToField());
                List<Serializable> records = (List<Serializable>) basicPersistenceModule.getFieldManager()
                        .getFieldValue(manyToField, foreignKey.getOriginatingField());

                Serializable myRecord = (Serializable) CollectionUtils.find(records,
                        new TypedPredicate<Serializable>() {

                            @Override
                            public boolean eval(Serializable record) {
                                try {
                                    return (pKey.equals(basicPersistenceModule.getFieldManager().getFieldValue(record,
                                            idPropertyName)));
                                } catch (IllegalAccessException e) {
                                    return false;
                                } catch (FieldNotAvailableException e) {
                                    return false;
                                }

                            }

                        }
                );

                records.remove(myRecord);
                if (CollectionUtils.isEmpty(records)) {
                    records.add(myRecord);
                } else {
                    records.add(requestedSequence - 1, myRecord);
                }

                int index = 1;
                Class<?> type = basicPersistenceModule.getFieldManager().getField(myRecord.getClass(),
                        foreignKey.getSortField()).getType();
                boolean isBigDecimal = BigDecimal.class.isAssignableFrom(type);
                for (Serializable record : records) {
                    basicPersistenceModule.getFieldManager().setFieldValue(record, foreignKey.getSortField(),
                            isBigDecimal ? new BigDecimal(index) : Long.valueOf(index));
                    index++;
                }
            }

            resultHolder.setResult(instance);

        } catch (IllegalAccessException e) {
            throw ExceptionHelper.refineException(e);
        } catch (FieldNotAvailableException e) {
            throw ExceptionHelper.refineException(e);
        } catch (ValidationException e) {
            throw ExceptionHelper.refineException(e);
        } catch (InstantiationException e) {
            throw ExceptionHelper.refineException(e);
        }

        return ExtensionResultStatusType.HANDLED;
    }

}
