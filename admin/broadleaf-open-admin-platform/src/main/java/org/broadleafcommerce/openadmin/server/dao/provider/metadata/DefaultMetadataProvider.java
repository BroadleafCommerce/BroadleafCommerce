/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blDefaultMetadataProvider")
@Scope("prototype")
public class DefaultMetadataProvider extends BasicMetadataProvider {

    private static final Log LOG = LogFactory.getLog(DefaultMetadataProvider.class);

    @Override
    public boolean addMetadata(AddMetadataRequest addMetadataRequest) {
        FieldInfo info = buildFieldInfo(addMetadataRequest.getRequestedField());
        BasicFieldMetadata metadata = new BasicFieldMetadata();
        metadata.setName(addMetadataRequest.getRequestedField().getName());
        metadata.setExcluded(false);
        addMetadataRequest.getRequestedMetadata().put(addMetadataRequest.getRequestedField().getName(), metadata);
        setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), addMetadataRequest.getRequestedMetadata(), info);
        return true;
    }

    public void overrideExclusionsFromXml(OverrideViaXmlRequest overrideViaXmlRequest) {
        //override any and all exclusions derived from xml
        Map<String, FieldMetadataOverride> overrides = getTargetedOverride(overrideViaXmlRequest.getRequestedConfigKey(), overrideViaXmlRequest.getRequestedCeilingEntity());
        if (overrides != null) {
            for (String propertyName : overrides.keySet()) {
                final FieldMetadataOverride localMetadata = overrides.get(propertyName);
                Boolean excluded = localMetadata.getExcluded();
                for (String key : overrideViaXmlRequest.getRequestedMetadata().keySet()) {
                    String testKey = overrideViaXmlRequest.getPrefix() + key;
                    if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded != null && excluded) {
                        FieldMetadata metadata = overrideViaXmlRequest.getRequestedMetadata().get(key);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("setExclusionsBasedOnParents:Excluding " + key + "because an override annotation declared "+ testKey + " to be excluded");
                        }
                        metadata.setExcluded(true);
                        continue;
                    }
                    if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded != null && !excluded) {
                        FieldMetadata metadata = overrideViaXmlRequest.getRequestedMetadata().get(key);
                        if (!overrideViaXmlRequest.getParentExcluded()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("setExclusionsBasedOnParents:Showing " + key + "because an override annotation declared " + testKey + " to not be excluded");
                            }
                            metadata.setExcluded(false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean addMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest) {
        BasicFieldMetadata fieldMetadata = (BasicFieldMetadata) addMetadataFromMappingDataRequest
                .getRequestedMetadata();
        fieldMetadata.setFieldType(addMetadataFromMappingDataRequest.getType());
        fieldMetadata.setSecondaryType(addMetadataFromMappingDataRequest.getSecondaryType());
        if (addMetadataFromMappingDataRequest.getRequestedEntityType() != null && !addMetadataFromMappingDataRequest.getRequestedEntityType().isCollectionType()) {
            Column column = null;
            for (Property property : addMetadataFromMappingDataRequest.getComponentProperties()) {
                if (property.getName().equals(addMetadataFromMappingDataRequest.getPropertyName())) {
                    column = (Column) property.getColumnIterator().next();
                    break;
                }
            }
            if (column != null) {
                fieldMetadata.setLength(column.getLength());
                fieldMetadata.setScale(column.getScale());
                fieldMetadata.setPrecision(column.getPrecision());
                fieldMetadata.setRequired(!column.isNullable());
                fieldMetadata.setUnique(column.isUnique());
            }
            fieldMetadata.setForeignKeyCollection(false);
        } else {
            fieldMetadata.setForeignKeyCollection(true);
        }
        fieldMetadata.setMutable(true);
        fieldMetadata.setMergedPropertyType(addMetadataFromMappingDataRequest.getMergedPropertyType());
        if (SupportedFieldType.BROADLEAF_ENUMERATION.equals(addMetadataFromMappingDataRequest.getType())) {
            try {
                setupBroadleafEnumeration(fieldMetadata.getBroadleafEnumeration(), fieldMetadata,
                        addMetadataFromMappingDataRequest.getDynamicEntityDao());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public boolean addMetadataFromFieldType(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest) {
        if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.UNKNOWN &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.BOOLEAN &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.INTEGER &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.DATE &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.STRING &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.MONEY &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.DECIMAL &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.FOREIGN_KEY &&
                        addMetadataFromFieldTypeRequest.getExplicitType() != SupportedFieldType.ADDITIONAL_FOREIGN_KEY
                ) {
            addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                    addMetadataFromFieldTypeRequest.getDynamicEntityDao()
                    .getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest.getPrefix(),
                            addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                            addMetadataFromFieldTypeRequest.getComponentProperties(),
                            addMetadataFromFieldTypeRequest.getExplicitType(), addMetadataFromFieldTypeRequest.getType(),
                            addMetadataFromFieldTypeRequest.getTargetClass(),
                            addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(), addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
        } else if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() == SupportedFieldType.BOOLEAN
                        ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Boolean.class) ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Character.class)
                ) {
            addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                    addMetadataFromFieldTypeRequest.getDynamicEntityDao()
                    .getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest.getPrefix(),
                            addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                            addMetadataFromFieldTypeRequest.getComponentProperties(),
                            SupportedFieldType.BOOLEAN, addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(),
                            addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                            addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
        } else if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() == SupportedFieldType.INTEGER
                        ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Byte.class) ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Short.class) ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Integer.class) ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Long.class)
                ) {
            if (addMetadataFromFieldTypeRequest.getRequestedPropertyName().equals(addMetadataFromFieldTypeRequest.getIdProperty())) {
                addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                        addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest
                                .getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                                addMetadataFromFieldTypeRequest.getComponentProperties(),
                                SupportedFieldType.ID, SupportedFieldType.INTEGER, addMetadataFromFieldTypeRequest.getType(),
                                addMetadataFromFieldTypeRequest.getTargetClass(), addMetadataFromFieldTypeRequest.getPresentationAttribute(),
                                addMetadataFromFieldTypeRequest.getMergedPropertyType(), addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
            } else {
                addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                        addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest
                                .getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                                addMetadataFromFieldTypeRequest.getComponentProperties(),
                                SupportedFieldType.INTEGER, addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(), addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                                addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
            }
        } else if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() == SupportedFieldType.DATE
                        ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Calendar.class) ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Date.class) ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Timestamp.class)
                ) {
            addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                    addMetadataFromFieldTypeRequest.getDynamicEntityDao()
                    .getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest.getPrefix(),
                            addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                            addMetadataFromFieldTypeRequest.getComponentProperties(),
                            SupportedFieldType.DATE, addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(),
                            addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                            addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
        } else if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() == SupportedFieldType.STRING
                        ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(String.class)
                ) {
            if (addMetadataFromFieldTypeRequest.getRequestedPropertyName().equals(addMetadataFromFieldTypeRequest.getIdProperty())) {
                addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                        addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest
                                .getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                                addMetadataFromFieldTypeRequest.getComponentProperties(),
                                SupportedFieldType.ID, SupportedFieldType.STRING, addMetadataFromFieldTypeRequest.getType(),
                                addMetadataFromFieldTypeRequest.getTargetClass(), addMetadataFromFieldTypeRequest.getPresentationAttribute(),
                                addMetadataFromFieldTypeRequest.getMergedPropertyType(), addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
            } else {
                addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                        addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest
                                .getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                                addMetadataFromFieldTypeRequest.getComponentProperties(),
                                SupportedFieldType.STRING, addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(), addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                                addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
            }
        } else if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() == SupportedFieldType.MONEY
                        ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Money.class)
                ) {
            addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                    addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest.getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                    addMetadataFromFieldTypeRequest.getComponentProperties(),
                    SupportedFieldType.MONEY, addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(), addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                    addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
        } else if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() == SupportedFieldType.DECIMAL
                        ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(Double.class) ||
                        addMetadataFromFieldTypeRequest.getReturnedClass().equals(BigDecimal.class)
                ) {
            addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                    addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest.getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                    addMetadataFromFieldTypeRequest.getComponentProperties(),
                    SupportedFieldType.DECIMAL, addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(), addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                    addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
        } else if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() == SupportedFieldType.FOREIGN_KEY
                        ||
                        addMetadataFromFieldTypeRequest.getForeignField() != null &&
                                addMetadataFromFieldTypeRequest.isPropertyForeignKey()
                ) {
            ClassMetadata foreignMetadata;
            String foreignKeyClass;
            String lookupDisplayProperty;
            if (addMetadataFromFieldTypeRequest.getForeignField() == null) {
                Class<?>[] entities = addMetadataFromFieldTypeRequest.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(addMetadataFromFieldTypeRequest.getType().getReturnedClass());
                foreignMetadata = addMetadataFromFieldTypeRequest.getDynamicEntityDao().getSessionFactory().getClassMetadata(entities
                        [entities.length - 1]);
                foreignKeyClass = entities[entities.length - 1].getName();
                lookupDisplayProperty = ((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getPresentationAttribute()).getLookupDisplayProperty();
            } else {
                try {
                    foreignMetadata = addMetadataFromFieldTypeRequest.getDynamicEntityDao().getSessionFactory().getClassMetadata(Class.forName(addMetadataFromFieldTypeRequest.getForeignField()
                            .getForeignKeyClass()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                foreignKeyClass = addMetadataFromFieldTypeRequest.getForeignField().getForeignKeyClass();
                lookupDisplayProperty = addMetadataFromFieldTypeRequest.getForeignField().getDisplayValueProperty();
            }
            Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
            if (foreignResponseType.equals(String.class)) {
                addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                        addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest
                                .getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                                addMetadataFromFieldTypeRequest.getComponentProperties(),
                                SupportedFieldType.FOREIGN_KEY, SupportedFieldType.STRING, addMetadataFromFieldTypeRequest.getType(),
                                addMetadataFromFieldTypeRequest.getTargetClass(),
                                addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                                addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
            } else {
                addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(), addMetadataFromFieldTypeRequest
                        .getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest.getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                                addMetadataFromFieldTypeRequest.getComponentProperties(),
                                SupportedFieldType.FOREIGN_KEY, SupportedFieldType.INTEGER, addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(),

                                addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                                addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
            }
            ((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getRequestedProperties().get(addMetadataFromFieldTypeRequest.getRequestedPropertyName())).setForeignKeyProperty(foreignMetadata
                    .getIdentifierPropertyName());
            ((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getRequestedProperties().get(addMetadataFromFieldTypeRequest.getRequestedPropertyName()))
                    .setForeignKeyClass(foreignKeyClass);
            ((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getRequestedProperties().get(addMetadataFromFieldTypeRequest.getRequestedPropertyName())).setForeignKeyDisplayValueProperty(lookupDisplayProperty);
        } else if (
                addMetadataFromFieldTypeRequest.getExplicitType() != null &&
                        addMetadataFromFieldTypeRequest.getExplicitType() == SupportedFieldType.ADDITIONAL_FOREIGN_KEY
                        ||
                        addMetadataFromFieldTypeRequest.getAdditionalForeignFields() != null &&
                                addMetadataFromFieldTypeRequest.getAdditionalForeignKeyIndexPosition() >= 0
                ) {
            if (!addMetadataFromFieldTypeRequest.getType().isEntityType()) {
                throw new IllegalArgumentException("Only ManyToOne and OneToOne fields can be marked as a " +
                        "SupportedFieldType of ADDITIONAL_FOREIGN_KEY");
            }
            ClassMetadata foreignMetadata;
            String foreignKeyClass;
            String lookupDisplayProperty;
            if (addMetadataFromFieldTypeRequest.getAdditionalForeignKeyIndexPosition() < 0) {
                Class<?>[] entities = addMetadataFromFieldTypeRequest.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling
                        (addMetadataFromFieldTypeRequest.getType().getReturnedClass());
                foreignMetadata = addMetadataFromFieldTypeRequest.getDynamicEntityDao().getSessionFactory().getClassMetadata(entities[entities.length - 1]);
                foreignKeyClass = entities[entities.length - 1].getName();
                lookupDisplayProperty = ((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getPresentationAttribute()).getLookupDisplayProperty();
            } else {
                try {
                    foreignMetadata = addMetadataFromFieldTypeRequest.getDynamicEntityDao().getSessionFactory().getClassMetadata(Class.forName
                            (addMetadataFromFieldTypeRequest.getAdditionalForeignFields()[addMetadataFromFieldTypeRequest.getAdditionalForeignKeyIndexPosition()].getForeignKeyClass()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                foreignKeyClass = addMetadataFromFieldTypeRequest.getAdditionalForeignFields()[addMetadataFromFieldTypeRequest
                        .getAdditionalForeignKeyIndexPosition()].getForeignKeyClass();
                lookupDisplayProperty = addMetadataFromFieldTypeRequest.getAdditionalForeignFields()[addMetadataFromFieldTypeRequest
                        .getAdditionalForeignKeyIndexPosition()]
                        .getDisplayValueProperty();
            }
            Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
            if (foreignResponseType.equals(String.class)) {
                addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                        addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest
                                .getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                                addMetadataFromFieldTypeRequest.getComponentProperties(), SupportedFieldType.ADDITIONAL_FOREIGN_KEY,
                                SupportedFieldType.STRING,
                                addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(),
                                addMetadataFromFieldTypeRequest.getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                                addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
            } else {
                addMetadataFromFieldTypeRequest.getRequestedProperties().put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(), addMetadataFromFieldTypeRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(addMetadataFromFieldTypeRequest.getPrefix(), addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                        addMetadataFromFieldTypeRequest.getComponentProperties(), SupportedFieldType.ADDITIONAL_FOREIGN_KEY, SupportedFieldType.INTEGER,
                        addMetadataFromFieldTypeRequest.getType(), addMetadataFromFieldTypeRequest.getTargetClass(), addMetadataFromFieldTypeRequest
                        .getPresentationAttribute(), addMetadataFromFieldTypeRequest.getMergedPropertyType(), addMetadataFromFieldTypeRequest.getDynamicEntityDao()));
            }
            ((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getRequestedProperties().get(addMetadataFromFieldTypeRequest.getRequestedPropertyName())).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
            ((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getRequestedProperties().get(addMetadataFromFieldTypeRequest.getRequestedPropertyName())).setForeignKeyClass(foreignKeyClass);
            ((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getRequestedProperties().get(addMetadataFromFieldTypeRequest.getRequestedPropertyName())).setForeignKeyDisplayValueProperty(lookupDisplayProperty);
        }
        //return type not supported - just skip this property
        return true;
    }

}
