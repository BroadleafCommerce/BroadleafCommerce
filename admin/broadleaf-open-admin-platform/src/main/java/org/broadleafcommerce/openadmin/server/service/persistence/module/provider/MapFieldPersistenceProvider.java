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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

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
        boolean dirty = false;
        try {
            //handle some additional field settings (if applicable)
            Class<?> valueType = null;
            String valueClassName = populateValueRequest.getMetadata().getMapFieldValueClass();
            if (valueClassName != null) {
                valueType = Class.forName(valueClassName);
            }
            if (valueType == null) {
                valueType = populateValueRequest.getReturnType();
            }
            if (valueType == null) {
                throw new IllegalAccessException("Unable to determine the valueType for the rule field (" + populateValueRequest.getProperty().getName() + ")");
            }
            if (ValueAssignable.class.isAssignableFrom(valueType)) {
                ValueAssignable assignableValue;
                try {
                    assignableValue = (ValueAssignable) populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName());
                } catch (FieldNotAvailableException e) {
                    throw new IllegalArgumentException(e);
                }
                String key = populateValueRequest.getProperty().getName().substring(populateValueRequest.getProperty().getName().indexOf(FieldManager.MAPFIELDSEPARATOR) + FieldManager.MAPFIELDSEPARATOR.length(), populateValueRequest.getProperty().getName().length());
                boolean persistValue = false;
                if (assignableValue == null) {
                    assignableValue = (ValueAssignable) valueType.newInstance();
                    persistValue = true;
                    dirty = true;
                } else {
                    dirty = assignableValue.getValue().equals(populateValueRequest.getProperty().getValue());
                    populateValueRequest.getProperty().setOriginalValue(String.valueOf(assignableValue));
                    populateValueRequest.getProperty().setOriginalDisplayValue(String.valueOf(assignableValue));
                }
                assignableValue.setName(key);
                assignableValue.setValue(populateValueRequest.getProperty().getValue());
                String fieldName = populateValueRequest.getProperty().getName().substring(0, populateValueRequest.getProperty().getName().indexOf(FieldManager.MAPFIELDSEPARATOR));
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
                        middleInstance = populateValueRequest.getFieldManager().getFieldValue(instance, propertyName);
                    }
                    populateValueRequest.getFieldManager().setFieldValue(assignableValue, manyToField, middleInstance);
                }
                if (Searchable.class.isAssignableFrom(valueType)) {
                    ((Searchable) assignableValue).setSearchable(populateValueRequest.getMetadata().getSearchable());
                }
                if (persistValue) {
                    populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(assignableValue);
                    populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), assignableValue);
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
    public FieldProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (extractValueRequest.getRequestedValue() != null && extractValueRequest.getRequestedValue() instanceof ValueAssignable) {
            ValueAssignable assignableValue = (ValueAssignable) extractValueRequest.getRequestedValue();
            String val = (String) assignableValue.getValue();
            property.setValue(val);
            property.setDisplayValue(extractValueRequest.getDisplayVal());
        } else {
            if (FieldProviderResponse.NOT_HANDLED==super.extractValue(extractValueRequest, property)) {
                return FieldProviderResponse.NOT_HANDLED;
            }
        }
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse addSearchMapping(AddSearchMappingRequest addSearchMappingRequest, List<FilterMapping> filterMappings) {
        return FieldProviderResponse.NOT_HANDLED;
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.MAP_FIELD;
    }
}
