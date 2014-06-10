/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class EntityValidatorServiceImpl implements EntityValidatorService, ApplicationContextAware {
    
    @Resource(name = "blGlobalEntityPropertyValidators")
    protected List<GlobalPropertyValidator> globalEntityValidators;
    
    protected ApplicationContext applicationContext;

    @Resource(name = "blRowLevelSecurityService")
    protected RowLevelSecurityService securityService;
    
    @Override
    public void validate(Entity submittedEntity, Serializable instance, Map<String, FieldMetadata> propertiesMetadata,
            RecordHelper recordHelper, boolean validateUnsubmittedProperties) {
        Object idValue = null;
        if (instance != null) {
            String idField = (String) ((BasicPersistenceModule) recordHelper.getCompatibleModule(OperationType.BASIC)).
                getPersistenceManager().getDynamicEntityDao().getIdMetadata(instance.getClass()).get("name");
            try {
                idValue = recordHelper.getFieldManager().getFieldValue(instance, idField);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (FieldNotAvailableException e) {
                throw new RuntimeException(e);
            }
        }
        Entity entity;
        boolean isUpdateRequest;
        if (idValue == null) {
            //This is for an add, or if the instance variable is null (e.g. PageTemplateCustomPersistenceHandler)
            entity = submittedEntity;
            isUpdateRequest = false;
        } else {
            //This is for an update, as the submittedEntity instance will likely only contain the dirty properties
            entity = recordHelper.getRecord(propertiesMetadata, instance, null, null);
            //acquire any missing properties not harvested from the instance and add to the entity. A use case for this
            //would be the confirmation field for a password validation
            for (Map.Entry<String, FieldMetadata> entry : propertiesMetadata.entrySet()) {
                if (entity.findProperty(entry.getKey()) == null) {
                    Property myProperty = submittedEntity.findProperty(entry.getKey());
                    if (myProperty != null) {
                        entity.addProperty(myProperty);
                    }
                } else if (submittedEntity.findProperty(entry.getKey()) != null ){
                    // Set the dirty state of the property
                    entity.findProperty(entry.getKey()).setIsDirty(submittedEntity.findProperty(entry.getKey()).getIsDirty());
                }
            }
            isUpdateRequest = true;
        }
            
        List<String> types = getTypeHierarchy(entity);
        //validate each individual property according to their validation configuration
        for (Entry<String, FieldMetadata> metadataEntry : propertiesMetadata.entrySet()) {
            FieldMetadata metadata = metadataEntry.getValue();

            //Don't test this field if it was not inherited from our polymorphic type (or supertype)
            if (types.contains(metadata.getInheritedFromType())
                    || instance.getClass().getName().equals(metadata.getInheritedFromType())) {
                
                Property property = entity.getPMap().get(metadataEntry.getKey());

                // This property should be set to false only in the case where we are adding a member to a collection
                // that has type of lookup. In this case, we don't have the properties from the target in our entity,
                // and we don't need to validate them.
                if (!validateUnsubmittedProperties && property == null) {
                    continue;
                }

                //for radio buttons, it's possible that the entity property was never populated in the first place from the POST
                //and so it will be null
                String propertyName = metadataEntry.getKey();
                String propertyValue = (property == null) ? null : property.getValue();

                if (metadata instanceof BasicFieldMetadata) {
                    //First execute the global field validators
                    if (CollectionUtils.isNotEmpty(globalEntityValidators)) {
                        for (GlobalPropertyValidator validator : globalEntityValidators) {
                            PropertyValidationResult result = validator.validate(entity,
                                    instance,
                                    propertiesMetadata,
                                    (BasicFieldMetadata)metadata,
                                    propertyName,
                                    propertyValue);
                            if (!result.isValid()) {
                                submittedEntity.addValidationError(propertyName, result.getErrorMessage());
                            }
                        }
                    }

                    //Now execute the validators configured for this particular field
                    Map<String, Map<String, String>> validations =
                            ((BasicFieldMetadata) metadata).getValidationConfigurations();
                    for (Map.Entry<String, Map<String, String>> validation : validations.entrySet()) {
                        String validationImplementation = validation.getKey();
                        Map<String, String> configuration = validation.getValue();

                        PropertyValidator validator = null;

                        //attempt bean resolution to find the validator
                        if (applicationContext.containsBean(validationImplementation)) {
                            validator = applicationContext.getBean(validationImplementation, PropertyValidator.class);
                        }

                        //not a bean, attempt to instantiate the class
                        if (validator == null) {
                            try {
                                validator = (PropertyValidator) Class.forName(validationImplementation).newInstance();
                            } catch (Exception e) {
                                //do nothing
                            }
                        }

                        if (validator == null) {
                            throw new PersistenceException("Could not find validator: " + validationImplementation +
                                    " for property: " + propertyName);
                        }

                        PropertyValidationResult result = validator.validate(entity,
                                                                        instance,
                                                                        propertiesMetadata,
                                                                        configuration,
                                                                        (BasicFieldMetadata)metadata,
                                                                        propertyName,
                                                                        propertyValue);
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

    /**
     * <p>
     * Returns the type hierarchy of the given <b>entity</b> in ascending order of type, stopping at Object
     * 
     * <p>
     * For instance, if this entity's {@link Entity#getType()} is {@link ProductBundleImpl}, then the result will be:
     * 
     * [org.broadleafcommerce.core.catalog.domain.ProductBundleImpl, org.broadleafcommerce.core.catalog.domain.ProductImpl]
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
