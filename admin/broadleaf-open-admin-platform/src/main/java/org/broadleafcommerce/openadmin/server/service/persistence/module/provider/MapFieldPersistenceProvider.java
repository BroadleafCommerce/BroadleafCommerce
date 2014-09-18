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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.value.Searchable;
import org.broadleafcommerce.common.value.ValueAssignable;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * @author Jeff Fischer
 */
@Component("blMapFieldPersistenceProvider")
@Scope("prototype")
public class MapFieldPersistenceProvider extends BasicFieldPersistenceProvider {

    @Override
    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        return populateValueRequest.getProperty().getName().contains(FieldManager.MAPFIELDSEPARATOR);
    }

    @Override
    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return property.getName().contains(FieldManager.MAPFIELDSEPARATOR);
    }

    @Override
    public FieldProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        boolean dirty = false;
        try {
            Class<?> startingValueType = getStartingValueType(populateValueRequest);
            Class<?> valueType = getValueType(populateValueRequest, startingValueType);

            if (ValueAssignable.class.isAssignableFrom(valueType)) {
                boolean persistValue = false;
                ValueAssignable assignableValue;
                Object parent;
                try {
                    parent = populateValueRequest.getFieldManager().getFieldValue(instance,
                            populateValueRequest.getProperty().getName());
                    if (parent == null) {
                        parent = startingValueType.newInstance();
                        if (!startingValueType.equals(valueType)) {
                            setupJoinEntityParent(populateValueRequest, instance, parent);
                        }
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), parent);
                        persistValue = true;
                    }
                    assignableValue = establishAssignableValue(populateValueRequest, parent);
                } catch (FieldNotAvailableException e) {
                    throw new IllegalArgumentException(e);
                }
                dirty = persistValue || (assignableValue != null && assignableValue.getValue().equals(
                        populateValueRequest.getProperty().getValue()));
                if (dirty) {
                    updateAssignableValue(populateValueRequest, instance, parent, valueType, persistValue, assignableValue);
                }
            } else {
                //handle the map value set itself
                if (FieldProviderResponse.NOT_HANDLED==super.populateValue(populateValueRequest, instance)) {
                    return FieldProviderResponse.NOT_HANDLED;
                }
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        populateValueRequest.getProperty().setIsDirty(dirty);
        return FieldProviderResponse.HANDLED_BREAK;
    }

    @Override
    public FieldProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property)
            throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        checkValue:{
            if (extractValueRequest.getRequestedValue() != null) {
                Object requestedValue = extractValueRequest.getRequestedValue();
                if (!StringUtils.isEmpty(extractValueRequest.getMetadata().getToOneTargetProperty())) {
                    try {
                        requestedValue = extractValueRequest.getFieldManager().getFieldValue(requestedValue,
                                extractValueRequest.getMetadata().getToOneTargetProperty());
                    } catch (IllegalAccessException e) {
                        throw ExceptionHelper.refineException(e);
                    } catch (FieldNotAvailableException e) {
                        throw ExceptionHelper.refineException(e);
                    }
                }
                if (requestedValue instanceof ValueAssignable) {
                    ValueAssignable assignableValue = (ValueAssignable) requestedValue;
                    String val = (String) assignableValue.getValue();
                    property.setValue(val);
                    property.setDisplayValue(extractValueRequest.getDisplayVal());
                    break checkValue;
                }
            }
            if (FieldProviderResponse.NOT_HANDLED==super.extractValue(extractValueRequest, property)) {
                return FieldProviderResponse.NOT_HANDLED;
            }
        }
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse addSearchMapping(AddSearchMappingRequest addSearchMappingRequest,
                                                  List<FilterMapping> filterMappings) {
        return FieldProviderResponse.NOT_HANDLED;
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.MAP_FIELD;
    }

    protected void updateAssignableValue(PopulateValueRequest populateValueRequest, Serializable instance, Object parent, Class<?>
            valueType, boolean persistValue, ValueAssignable assignableValue)
            throws IllegalAccessException, FieldNotAvailableException, InstantiationException {
        if (!persistValue) {
            //pre-merge (can result in a clone for enterprise)
            parent = populateValueRequest.getPersistenceManager().getDynamicEntityDao().merge(parent);
            assignableValue = establishAssignableValue(populateValueRequest, parent);
        }
        String key = populateValueRequest.getProperty().getName().substring(populateValueRequest
                .getProperty().getName().indexOf(FieldManager.MAPFIELDSEPARATOR) + FieldManager
                .MAPFIELDSEPARATOR.length(), populateValueRequest.getProperty().getName().length());
        populateValueRequest.getProperty().setOriginalValue(String.valueOf(assignableValue));
        populateValueRequest.getProperty().setOriginalDisplayValue(String.valueOf(assignableValue));
        assignableValue.setName(key);
        assignableValue.setValue(populateValueRequest.getProperty().getValue());
        String fieldName = populateValueRequest.getProperty().getName().substring(0,
                populateValueRequest.getProperty().getName().indexOf(FieldManager.MAPFIELDSEPARATOR));
        Field field = populateValueRequest.getFieldManager().getField(instance.getClass(), fieldName);
        FieldInfo fieldInfo = buildFieldInfo(field);
        String manyToField = null;
        if (populateValueRequest.getMetadata().getManyToField() != null) {
            manyToField = populateValueRequest.getMetadata().getManyToField();
        }
        if (manyToField == null) {
            manyToField = fieldInfo.getManyToManyMappedBy();
        }
        if (manyToField == null) {
            manyToField = fieldInfo.getOneToManyMappedBy();
        }
        if (manyToField != null) {
            String propertyName = populateValueRequest.getProperty().getName();
            Object middleInstance = instance;
            if (propertyName.contains(".")) {
                propertyName = propertyName.substring(0, propertyName.lastIndexOf("."));
                middleInstance = populateValueRequest.getFieldManager().getFieldValue(instance,
                        propertyName);
            }
            populateValueRequest.getFieldManager().setFieldValue(assignableValue, manyToField,
                    middleInstance);
            if (!populateValueRequest.getPersistenceManager().getDynamicEntityDao()
                    .getStandardEntityManager().contains(middleInstance)) {
                //if this is part of an add for the manyToField object, don't persist this map value,
                // since it would result in a
                //transient object exception on the manyToField object (which itself has not been saved yet)
                persistValue = false;
            }
        }
        if (Searchable.class.isAssignableFrom(valueType)) {
            ((Searchable) assignableValue).setSearchable(populateValueRequest.getMetadata().getSearchable());
        }
        if (persistValue) {
            populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(assignableValue);
        }
    }

    protected ValueAssignable establishAssignableValue(PopulateValueRequest populateValueRequest, Object parent)
            throws IllegalAccessException, FieldNotAvailableException {
        ValueAssignable assignableValue;
        if (!StringUtils.isEmpty(populateValueRequest.getMetadata().getToOneTargetProperty())) {
            assignableValue = (ValueAssignable) populateValueRequest.getFieldManager().getFieldValue(parent,
                    populateValueRequest.getMetadata().getToOneTargetProperty());
        } else {
            assignableValue = (ValueAssignable) parent;
        }
        return assignableValue;
    }

    protected void setupJoinEntityParent(PopulateValueRequest populateValueRequest, Serializable instance, Object parent)
            throws IllegalAccessException, FieldNotAvailableException, InstantiationException {
        //this is a join-entity type
        Object parentsParent = instance;
        String parentsParentName = populateValueRequest.getProperty().getName();
        if (parentsParentName.contains(".")) {
            parentsParent = populateValueRequest.getFieldManager().getFieldValue(instance,
                    parentsParentName.substring(0, parentsParentName.lastIndexOf(".")));
        }
        populateValueRequest.getFieldManager().setFieldValue(parent, populateValueRequest.getMetadata().
                getToOneParentProperty(), parentsParent);
        populateValueRequest.getFieldManager().setFieldValue(parent, populateValueRequest.getMetadata().
                getMapKeyValueProperty(), parentsParentName.substring(parentsParentName.indexOf(
                        FieldManager.MAPFIELDSEPARATOR) + FieldManager.MAPFIELDSEPARATOR.length(),
                parentsParentName.length()));
        populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(parent);
    }

    protected Class<?> getValueType(PopulateValueRequest populateValueRequest, Class<?> startingValueType) {
        Class<?> valueType = startingValueType;
        if (!StringUtils.isEmpty(populateValueRequest.getMetadata().getToOneTargetProperty())) {
            Field nestedField = FieldManager.getSingleField(valueType, populateValueRequest.getMetadata()
                    .getToOneTargetProperty());
            ManyToOne manyToOne = nestedField.getAnnotation(ManyToOne.class);
            if (manyToOne != null && !manyToOne.targetEntity().getName().equals(void.class.getName())) {
                valueType = manyToOne.targetEntity();
            } else {
                OneToOne oneToOne = nestedField.getAnnotation(OneToOne.class);
                if (oneToOne != null && !oneToOne.targetEntity().getName().equals(void.class.getName())) {
                    valueType = oneToOne.targetEntity();
                }
            }
        }
        return valueType;
    }

    protected Class<?> getStartingValueType(PopulateValueRequest populateValueRequest)
            throws ClassNotFoundException, IllegalAccessException {
        Class<?> startingValueType = null;
        String valueClassName = populateValueRequest.getMetadata().getMapFieldValueClass();
        if (valueClassName != null) {
            startingValueType = Class.forName(valueClassName);
        }
        if (startingValueType == null) {
            startingValueType = populateValueRequest.getReturnType();
        }
        if (startingValueType == null) {
            throw new IllegalAccessException("Unable to determine the valueType for the rule field (" +
                    populateValueRequest.getProperty().getName() + ")");
        }
        return startingValueType;
    }
}
