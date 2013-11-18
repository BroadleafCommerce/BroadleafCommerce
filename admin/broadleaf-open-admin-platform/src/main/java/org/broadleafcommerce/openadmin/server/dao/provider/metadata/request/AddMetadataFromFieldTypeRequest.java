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
package org.broadleafcommerce.openadmin.server.dao.provider.metadata.request;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Contains the requested field, property name and support classes.
 *
 * @author Jeff Fischer
 */
public class AddMetadataFromFieldTypeRequest {

    private final Field requestedField;
    private final Class<?> targetClass;
    private final ForeignKey foreignField;
    private final ForeignKey[] additionalForeignFields;
    private final MergedPropertyType mergedPropertyType;
    private final List<Property> componentProperties;
    private final String idProperty;
    private final String prefix;
    private final String requestedPropertyName;
    private final Type type;
    private final boolean propertyForeignKey;
    private final int additionalForeignKeyIndexPosition;
    private final Map<String, FieldMetadata> presentationAttributes;
    private final FieldMetadata presentationAttribute;
    private final SupportedFieldType explicitType;
    private final Class<?> returnedClass;
    private final DynamicEntityDao dynamicEntityDao;

    public AddMetadataFromFieldTypeRequest(Field requestedField, Class<?> targetClass, ForeignKey foreignField,
                                           ForeignKey[] additionalForeignFields,
                                           MergedPropertyType mergedPropertyType, List<Property> componentProperties,
                                           String idProperty,
                                           String prefix, String requestedPropertyName, Type type,
                                           boolean propertyForeignKey, int additionalForeignKeyIndexPosition,
                                           Map<String, FieldMetadata> presentationAttributes,
                                           FieldMetadata presentationAttribute, SupportedFieldType explicitType, 
                                           Class<?> returnedClass, DynamicEntityDao dynamicEntityDao) {
        this.requestedField = requestedField;
        this.targetClass = targetClass;
        this.foreignField = foreignField;
        this.additionalForeignFields = additionalForeignFields;
        this.mergedPropertyType = mergedPropertyType;
        this.componentProperties = componentProperties;
        this.idProperty = idProperty;
        this.prefix = prefix;
        this.requestedPropertyName = requestedPropertyName;
        this.type = type;
        this.propertyForeignKey = propertyForeignKey;
        this.additionalForeignKeyIndexPosition = additionalForeignKeyIndexPosition;
        this.presentationAttributes = presentationAttributes;
        this.presentationAttribute = presentationAttribute;
        this.explicitType = explicitType;
        this.returnedClass = returnedClass;
        this.dynamicEntityDao = dynamicEntityDao;
    }

    public Field getRequestedField() {
        return requestedField;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public ForeignKey getForeignField() {
        return foreignField;
    }

    public ForeignKey[] getAdditionalForeignFields() {
        return additionalForeignFields;
    }

    public MergedPropertyType getMergedPropertyType() {
        return mergedPropertyType;
    }

    public List<Property> getComponentProperties() {
        return componentProperties;
    }

    public String getIdProperty() {
        return idProperty;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRequestedPropertyName() {
        return requestedPropertyName;
    }

    public Type getType() {
        return type;
    }

    public boolean isPropertyForeignKey() {
        return propertyForeignKey;
    }

    public int getAdditionalForeignKeyIndexPosition() {
        return additionalForeignKeyIndexPosition;
    }

    public Map<String, FieldMetadata> getPresentationAttributes() {
        return presentationAttributes;
    }

    public FieldMetadata getPresentationAttribute() {
        return presentationAttribute;
    }

    public SupportedFieldType getExplicitType() {
        return explicitType;
    }

    public Class<?> getReturnedClass() {
        return returnedClass;
    }

    public DynamicEntityDao getDynamicEntityDao() {
        return dynamicEntityDao;
    }
}
