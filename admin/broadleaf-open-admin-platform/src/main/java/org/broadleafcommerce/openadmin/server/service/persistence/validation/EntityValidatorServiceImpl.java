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
package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.security.service.RowLevelSecurityService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * This implementation validates each {@link Property} from the given {@link Entity} according to the
 * {@link ValidationConfiguration}s associated with it.
 *
 * @author Phillip Verheyden
 * @see {@link EntityValidatorService}
 * @see {@link ValidationConfiguration}
 */
@Service("blEntityValidatorService")
public class EntityValidatorServiceImpl implements EntityValidatorService {
    protected static final Log LOG = LogFactory.getLog(EntityValidatorServiceImpl.class);

    @Resource(name = "blGlobalEntityPropertyValidators")
    protected List<GlobalPropertyValidator> globalEntityValidators;

    @Autowired
    protected ApplicationContext applicationContext;

    @Resource(name = "blRowLevelSecurityService")
    protected RowLevelSecurityService securityService;

    private Map<String, List<BroadleafEntityValidator<?>>> broadleafValidatorMap;

    @PostConstruct
    public void populateBroadleafValidatorMap() {
        String[] beanNames = applicationContext.getBeanNamesForType(BroadleafEntityValidator.class);
        broadleafValidatorMap = new HashMap<>(beanNames.length);

        for (String beanName : beanNames) {
            BroadleafEntityValidator<?> broadleafValidator = applicationContext.getBean(beanName,
                                                                                        BroadleafEntityValidator.class);
            Class<?> entityType = GenericTypeResolver.resolveTypeArgument(broadleafValidator.getClass(),
                                                                          BroadleafEntityValidator.class);
            if (entityType != null) {
                String entityClassName = entityType.getName();
                LOG.info(String.format("Registering validator %s for entity type %s",
                                       broadleafValidator.getClass().getName(), entityClassName));

                List<BroadleafEntityValidator<?>> registeredValidatorsForType = broadleafValidatorMap
                        .get(entityClassName);
                if (registeredValidatorsForType == null) {
                    registeredValidatorsForType = new ArrayList<>();
                    broadleafValidatorMap.put(entityClassName, registeredValidatorsForType);
                }
                registeredValidatorsForType.add(broadleafValidator);
            } else {
                LOG.warn("Could not determine entity type for " + broadleafValidator.getClass().getName());
            }
        }
    }

    @Override
    public void validate(Entity submittedEntity, @Nullable Serializable instance,
                         Map<String, FieldMetadata> propertiesMetadata, RecordHelper recordHelper,
                         boolean validateUnsubmittedProperties) {
        Object idValue = null;
        if (instance != null) {
            String idField = (String) ((BasicPersistenceModule) recordHelper.getCompatibleModule(OperationType.BASIC))
                    .getPersistenceManager().getDynamicEntityDao().getIdMetadata(instance.getClass()).get("name");
            try {
                idValue = recordHelper.getFieldManager(false).getFieldValue(instance, idField);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (FieldNotAvailableException e) {
                throw new RuntimeException(e);
            }
        }
        Entity entity;
        if (idValue == null) {
            // This is for an add, or if the instance variable is null (e.g. PageTemplateCustomPersistenceHandler)
            entity = submittedEntity;
        } else {
            if (validateUnsubmittedProperties) {
                // This is for an update, as the submittedEntity instance will likely only contain the dirty properties
                entity = recordHelper.getRecord(propertiesMetadata, instance, null, null);
                // acquire any missing properties not harvested from the instance and add to the entity. A use case for
                // this would be the confirmation field for a password validation
                for (Map.Entry<String, FieldMetadata> entry : propertiesMetadata.entrySet()) {
                    if (entity.findProperty(entry.getKey()) == null) {
                        Property myProperty = submittedEntity.findProperty(entry.getKey());
                        if (myProperty != null) {
                            entity.addProperty(myProperty);
                        }
                    } else if (submittedEntity.findProperty(entry.getKey()) != null) {
                        entity.findProperty(entry.getKey())
                                .setValue(submittedEntity.findProperty(entry.getKey()).getValue());
                        entity.findProperty(entry.getKey())
                                .setIsDirty(submittedEntity.findProperty(entry.getKey()).getIsDirty());
                    }
                }
            } else {
                entity = submittedEntity;
            }
        }

        List<String> types = getTypeHierarchy(entity);
        // validate each individual property according to their validation configuration
        for (Entry<String, FieldMetadata> metadataEntry : propertiesMetadata.entrySet()) {
            FieldMetadata metadata = metadataEntry.getValue();

            // Don't test this field if it was not inherited from our polymorphic type (or supertype)
            if (instance != null && (types.contains(metadata.getInheritedFromType())
                                     || instance.getClass().getName().equals(metadata.getInheritedFromType()))) {

                Property property = entity.getPMap().get(metadataEntry.getKey());

                // This property should be set to false only in the case where we are adding a member to a collection
                // that has type of lookup. In this case, we don't have the properties from the target in our entity,
                // and we don't need to validate them.
                if (!validateUnsubmittedProperties && property == null) {
                    continue;
                }

                // for radio buttons, it's possible that the entity property was never populated in the first place from
                // the POST and so it will be null
                String propertyName = metadataEntry.getKey();
                String propertyValue = (property == null) ? null : property.getValue();

                if (metadata instanceof BasicFieldMetadata) {
                    // First execute the global field validators
                    if (CollectionUtils.isNotEmpty(globalEntityValidators)) {
                        for (GlobalPropertyValidator validator : globalEntityValidators) {
                            PropertyValidationResult result = validator.validate(entity, instance, propertiesMetadata,
                                                                                 (BasicFieldMetadata) metadata, propertyName, propertyValue);
                            if (!result.isValid()) {
                                submittedEntity.addValidationError(propertyName, result.getErrorMessage());
                            }
                        }
                    }

                    // Now execute the validators configured for this particular field
                    Map<String, List<Map<String, String>>> validations = ((BasicFieldMetadata) metadata)
                            .getValidationConfigurations();
                    for (Map.Entry<String, List<Map<String, String>>> validation : validations.entrySet()) {
                        String validationImplementation = validation.getKey();

                        for (Map<String, String> configuration : validation.getValue()) {

                            PropertyValidator validator = null;

                            // attempt bean resolution to find the validator
                            if (applicationContext.containsBean(validationImplementation)) {
                                validator = applicationContext.getBean(validationImplementation,
                                                                       PropertyValidator.class);
                            }

                            // not a bean, attempt to instantiate the class
                            if (validator == null) {
                                try {
                                    validator = (PropertyValidator) Class.forName(validationImplementation)
                                            .newInstance();
                                } catch (Exception e) {
                                    // do nothing
                                }
                            }

                            if (validator == null) {
                                throw new PersistenceException("Could not find validator: " + validationImplementation
                                                               + " for property: " + propertyName);
                            }

                            PropertyValidationResult result = validator.validate(entity, instance, propertiesMetadata,
                                                                                 configuration, (BasicFieldMetadata) metadata, propertyName, propertyValue);
                            if (!result.isValid()) {
                                for (String message : result.getErrorMessages()) {
                                    submittedEntity.addValidationError(propertyName, message);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (instance != null) {
            List<BroadleafEntityValidator<?>> broadleafValidators = broadleafValidatorMap
                    .get(instance.getClass().getName());
            if (broadleafValidators != null) {
                for (BroadleafEntityValidator<?> broadleafValidator : broadleafValidators) {
                    LOG.debug("Calling validator " + broadleafValidator.getClass().getName());
                    broadleafValidator.validate(submittedEntity, instance, propertiesMetadata, recordHelper,
                                                validateUnsubmittedProperties);
                }
            }
        }
    }

    /**
     * <p>
     * Returns the type hierarchy of the given <b>entity</b> in ascending order of type, stopping at Object
     *
     * <p>
     * For instance, if this entity's {@link Entity#getType()} is {@link ProductBundleImpl}, then the result will be:
     *
     * [org.broadleafcommerce.core.catalog.domain.ProductBundleImpl,
     * org.broadleafcommerce.core.catalog.domain.ProductImpl]
     *
     * @param entity
     * @return
     */
    protected List<String> getTypeHierarchy(Entity entity) {
        List<String> types = new ArrayList<String>();
        Class<?> myType;
        try {
            myType = Class.forName(entity.getType()[0]);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        types.add(myType.getName());
        boolean eof = false;
        while (!eof) {
            myType = myType.getSuperclass();
            if (myType != null && !myType.getName().equals(Object.class.getName())) {
                types.add(myType.getName());
            } else {
                eof = true;
            }
        }
        return types;
    }

    @Override
    public List<GlobalPropertyValidator> getGlobalEntityValidators() {
        return globalEntityValidators;
    }

    @Override
    public void setGlobalEntityValidators(List<GlobalPropertyValidator> globalEntityValidators) {
        this.globalEntityValidators = globalEntityValidators;
    }
}
