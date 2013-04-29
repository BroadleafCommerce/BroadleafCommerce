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

package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.Embedded;

/**
 * @author Jeff Fischer
 */
@Component("blBasicFieldPersistenceProvider")
@Scope("prototype")
public class BasicFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        BasicFieldMetadata metadata = populateValueRequest.getMetadata();
        Property property = populateValueRequest.getProperty();
        //don't handle map fields here - we'll get them in a separate provider
        return detectBasicType(metadata, property);
    }

    protected boolean detectBasicType(BasicFieldMetadata metadata, Property property) {
        return (metadata.getFieldType() == SupportedFieldType.BOOLEAN ||
                metadata.getFieldType() == SupportedFieldType.DATE ||
                metadata.getFieldType() == SupportedFieldType.DECIMAL ||
                metadata.getFieldType() == SupportedFieldType.MONEY ||
                metadata.getFieldType() == SupportedFieldType.INTEGER ||
                metadata.getFieldType() == SupportedFieldType.EMAIL ||
                metadata.getFieldType() == SupportedFieldType.FOREIGN_KEY ||
                metadata.getFieldType() == SupportedFieldType.ADDITIONAL_FOREIGN_KEY ||
                metadata.getFieldType() == SupportedFieldType.STRING || 
                metadata.getFieldType() == SupportedFieldType.ID) &&
                (property == null ||
                !property.getName().contains(FieldManager.MAPFIELDSEPARATOR));
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        BasicFieldMetadata metadata = extractValueRequest.getMetadata();
        
        // MoneyFieldPersistenceProvider is responsible for handling extraction of Money fields. However, persisting
        // Money fields can be handled in this basic field persistence provider.
        if (metadata.getFieldType() == SupportedFieldType.MONEY) {
            return false;
        }
        
        // don't handle map fields here - we'll get them in a separate provider
        return detectBasicType(metadata, property);
    }

    protected boolean canHandleSearchMapping(AddSearchMappingRequest addSearchMappingRequest, BaseCtoConverter ctoConverter) {
        BasicFieldMetadata metadata = (BasicFieldMetadata) addSearchMappingRequest.getMergedProperties().get(addSearchMappingRequest.getPropertyName());
        Property property = null;
        //don't handle map fields here - we'll get them in a separate provider
        return detectBasicType(metadata, property);
    }

    @Override
    public FieldProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        try {
            switch (populateValueRequest.getMetadata().getFieldType()) {
                case BOOLEAN:
                    boolean v = Boolean.valueOf(populateValueRequest.getRequestedValue());
                    try {
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), v);
                    } catch (IllegalArgumentException e) {
                        char c = v ? 'Y' : 'N';
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), c);
                    }
                    break;
                case DATE:
                    populateValueRequest.getFieldManager().setFieldValue(instance,
                            populateValueRequest.getProperty().getName(), populateValueRequest.getDataFormatProvider().getSimpleDateFormatter().parse(populateValueRequest.getRequestedValue()));
                    break;
                case DECIMAL:
                    if (BigDecimal.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), new BigDecimal(new Double(populateValueRequest.getRequestedValue())));
                    } else {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), new Double(populateValueRequest.getRequestedValue()));
                    }
                    break;
                case MONEY:
                    if (BigDecimal.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), new BigDecimal(new Double(populateValueRequest.getRequestedValue())));
                    } else if (Double.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), new Double(populateValueRequest.getRequestedValue()));
                    } else {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), new Money(new Double(populateValueRequest.getRequestedValue())));
                    }
                    break;
                case INTEGER:
                    if (int.class.isAssignableFrom(populateValueRequest.getReturnType()) || Integer.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), Integer.valueOf(populateValueRequest.getRequestedValue()));
                    } else if (byte.class.isAssignableFrom(populateValueRequest.getReturnType()) || Byte.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), Byte.valueOf(populateValueRequest.getRequestedValue()));
                    } else if (short.class.isAssignableFrom(populateValueRequest.getReturnType()) || Short.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), Short.valueOf(populateValueRequest.getRequestedValue()));
                    } else if (long.class.isAssignableFrom(populateValueRequest.getReturnType()) || Long.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), Long.valueOf(populateValueRequest.getRequestedValue()));
                    }
                    break;
                case STRING:
                case EMAIL:
                    populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), populateValueRequest.getRequestedValue());
                    break;
                case FOREIGN_KEY: {
                    Serializable foreignInstance;
                    if (StringUtils.isEmpty(populateValueRequest.getRequestedValue())) {
                        foreignInstance = null;
                    } else {
                        if (SupportedFieldType.INTEGER.toString().equals(populateValueRequest.getMetadata().getSecondaryType().toString())) {
                            foreignInstance = populateValueRequest.getPersistenceManager().getDynamicEntityDao().retrieve(Class.forName(populateValueRequest.getMetadata().getForeignKeyClass()), Long.valueOf(populateValueRequest.getRequestedValue()));
                        } else {
                            foreignInstance = populateValueRequest.getPersistenceManager().getDynamicEntityDao().retrieve(Class.forName(populateValueRequest.getMetadata().getForeignKeyClass()), populateValueRequest.getRequestedValue());
                        }
                    }

                    if (Collection.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Collection collection;
                        try {
                            collection = (Collection) populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName());
                        } catch (FieldNotAvailableException e) {
                            throw new IllegalArgumentException(e);
                        }
                        if (!collection.contains(foreignInstance)) {
                            collection.add(foreignInstance);
                        }
                    } else if (Map.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        throw new IllegalArgumentException("Map structures are not supported for foreign key fields.");
                    } else {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), foreignInstance);
                    }
                    break;
                }
                case ADDITIONAL_FOREIGN_KEY: {
                    Serializable foreignInstance;
                    if (StringUtils.isEmpty(populateValueRequest.getRequestedValue())) {
                        foreignInstance = null;
                    } else {
                        if (SupportedFieldType.INTEGER.toString().equals(populateValueRequest.getMetadata().getSecondaryType().toString())) {
                            foreignInstance = populateValueRequest.getPersistenceManager().getDynamicEntityDao().retrieve(Class.forName(populateValueRequest.getMetadata().getForeignKeyClass()), Long.valueOf(populateValueRequest.getRequestedValue()));
                        } else {
                            foreignInstance = populateValueRequest.getPersistenceManager().getDynamicEntityDao().retrieve(Class.forName(populateValueRequest.getMetadata().getForeignKeyClass()), populateValueRequest.getRequestedValue());
                        }
                    }

                    if (Collection.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Collection collection;
                        try {
                            collection = (Collection) populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName());
                        } catch (FieldNotAvailableException e) {
                            throw new IllegalArgumentException(e);
                        }
                        if (!collection.contains(foreignInstance)) {
                            collection.add(foreignInstance);
                        }
                    } else if (Map.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        throw new IllegalArgumentException("Map structures are not supported for foreign key fields.");
                    } else {
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), foreignInstance);
                    }
                    break;
                }
                case ID:
                    if (populateValueRequest.getSetId()) {
                        switch (populateValueRequest.getMetadata().getSecondaryType()) {
                            case INTEGER:
                                populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), Long.valueOf(populateValueRequest.getRequestedValue()));
                                break;
                            case STRING:
                                populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), populateValueRequest.getRequestedValue());
                                break;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        try {
            if (extractValueRequest.getRequestedValue() != null) {
                String val = null;
                if (extractValueRequest.getMetadata().getForeignKeyCollection()) {
                    ((BasicFieldMetadata) property.getMetadata()).setFieldType(extractValueRequest.getMetadata().getFieldType());
                } else if (extractValueRequest.getMetadata().getFieldType().equals(SupportedFieldType.BOOLEAN) && extractValueRequest.getRequestedValue() instanceof Character) {
                    val = (extractValueRequest.getRequestedValue().equals('Y')) ? "true" : "false";
                } else if (Date.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = extractValueRequest.getDataFormatProvider().getSimpleDateFormatter
                            ().format((Date) extractValueRequest.getRequestedValue());
                } else if (Timestamp.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = extractValueRequest.getDataFormatProvider().getSimpleDateFormatter
                            ().format(new Date(((Timestamp) extractValueRequest.getRequestedValue()).getTime()));
                } else if (Calendar.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = extractValueRequest.getDataFormatProvider().getSimpleDateFormatter
                            ().format(((Calendar) extractValueRequest.getRequestedValue()).getTime());
                } else if (Double.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = extractValueRequest.getDataFormatProvider().getDecimalFormatter().format(extractValueRequest.getRequestedValue());
                } else if (BigDecimal.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = extractValueRequest.getDataFormatProvider().getDecimalFormatter().format(((BigDecimal) extractValueRequest.getRequestedValue()).doubleValue());
                } else if (extractValueRequest.getMetadata().getForeignKeyClass() != null) {
                    try {
                        val = extractValueRequest.getFieldManager().getFieldValue
                                (extractValueRequest.getRequestedValue(), extractValueRequest.getMetadata().getForeignKeyProperty()).toString();
                        //see if there's a name property and use it for the display value
                        Object temp = null;
                        try {
                            temp = extractValueRequest.getFieldManager().getFieldValue(extractValueRequest.getRequestedValue(), extractValueRequest.getMetadata().getForeignKeyDisplayValueProperty());
                        } catch (FieldNotAvailableException e) {
                            //do nothing
                        }
                        if (temp != null) {
                            extractValueRequest.setDisplayVal(temp.toString());
                        }
                    } catch (FieldNotAvailableException e) {
                        throw new IllegalArgumentException(e);
                    }
                } else {
                    val = extractValueRequest.getRequestedValue().toString();
                }
                property.setValue(val);
                property.setDisplayValue(extractValueRequest.getDisplayVal());
            }
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse addSearchMapping(AddSearchMappingRequest addSearchMappingRequest, BaseCtoConverter ctoConverter) {
        if (!canHandleSearchMapping(addSearchMappingRequest, ctoConverter)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        AssociationPath associationPath;
        int dotIndex = addSearchMappingRequest.getPropertyName().lastIndexOf('.');
        StringBuilder property;
        Class clazz;
        try {
            clazz = Class.forName(addSearchMappingRequest.getMergedProperties().get(addSearchMappingRequest
                    .getPropertyName()).getInheritedFromType());
        } catch (ClassNotFoundException e) {
            throw new PersistenceException(e);
        }
        Field field = addSearchMappingRequest.getFieldManager().getField(clazz, addSearchMappingRequest.getPropertyName());
        Class<?> targetType = null;
        if (field != null) {
            targetType = field.getType();
        }
        if (dotIndex >= 0) {
            property = new StringBuilder(addSearchMappingRequest.getPropertyName().substring(dotIndex + 1,
                    addSearchMappingRequest.getPropertyName().length()));
            String prefix = addSearchMappingRequest.getPropertyName().substring(0, dotIndex);
            StringTokenizer tokens = new StringTokenizer(prefix, ".");
            List<AssociationPathElement> elementList = new ArrayList<AssociationPathElement>(20);
            StringBuilder sb = new StringBuilder(150);
            StringBuilder pathBuilder = new StringBuilder(150);
            while (tokens.hasMoreElements()) {
                String token = tokens.nextToken();
                sb.append(token);
                pathBuilder.append(token);
                field = addSearchMappingRequest.getFieldManager().getField(clazz, pathBuilder.toString());
                Embedded embedded = field.getAnnotation(Embedded.class);
                if (embedded != null) {
                    sb.append('.');
                } else {
                    elementList.add(new AssociationPathElement(sb.toString()));
                    sb = new StringBuilder(150);
                }
                pathBuilder.append('.');
            }
            if (!elementList.isEmpty()) {
                AssociationPathElement[] elements = elementList.toArray(new AssociationPathElement[elementList.size()]);
                associationPath = new AssociationPath(elements);
            } else {
                property = property.insert(0, sb.toString());
                associationPath = AssociationPath.ROOT;
            }
        } else {
            property = new StringBuilder(addSearchMappingRequest.getPropertyName());
            associationPath = AssociationPath.ROOT;
        }
        String convertedProperty = property.toString();
        BasicFieldMetadata metadata = (BasicFieldMetadata) addSearchMappingRequest.getMergedProperties().get(addSearchMappingRequest.getPropertyName());
        switch (metadata.getFieldType()) {
            case BOOLEAN:
                if (targetType == null || targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
                    ctoConverter.addBooleanMapping(addSearchMappingRequest
                            .getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty);
                } else {
                    ctoConverter.addCharacterMapping(addSearchMappingRequest
                            .getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty);
                }
                break;
            case DATE:
                ctoConverter.addDateMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty, addSearchMappingRequest.getDataFormatProvider());
                break;
            case DECIMAL:
                ctoConverter.addDecimalMapping(addSearchMappingRequest
                        .getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(),
                        associationPath, convertedProperty);
                break;
            case MONEY:
                ctoConverter.addDecimalMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty);
                break;
            case INTEGER:
                ctoConverter.addLongMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty);
                break;
            default:
                ctoConverter.addStringLikeMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty);
                break;
            case STRING:    
            case EMAIL:
                ctoConverter.addStringLikeMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty);
                break;
            case FOREIGN_KEY:
                if (addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName()).getFilterValues().length > 0) {
                    ForeignKey foreignKey = (ForeignKey) addSearchMappingRequest.getPersistencePerspective().getPersistencePerspectiveItems().get
                            (PersistencePerspectiveItemType.FOREIGNKEY);
                    if (metadata.getForeignKeyCollection()) {
                        if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey
                                .getRestrictionType().toString())) {
                            ctoConverter.addCollectionSizeEqMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), AssociationPath.ROOT, addSearchMappingRequest.getPropertyName());
                        } else {
                            AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement
                                    (addSearchMappingRequest.getPropertyName()));
                            ctoConverter.addLongMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), foreignCategory, metadata.getForeignKeyProperty());

                        }
                    } else if (addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName())
                            .getFilterValues()[0] == null || "null".equals(addSearchMappingRequest.getRequestedCto().get
                            (addSearchMappingRequest.getPropertyName()).getFilterValues()[0])) {
                        ctoConverter.addNullMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, addSearchMappingRequest.getPropertyName());

                    } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(addSearchMappingRequest.getPropertyName()));
                        ctoConverter.addStringEQMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), foreignCategory, metadata.getForeignKeyProperty());
                    } else {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(addSearchMappingRequest.getPropertyName()));
                        ctoConverter.addLongEQMapping(addSearchMappingRequest
                                .getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), foreignCategory, metadata.getForeignKeyProperty());
                    }
                } else {
                    ctoConverter.addEmptyMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName());
                }
                break;
            case ADDITIONAL_FOREIGN_KEY:
                if (addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName()).getFilterValues().length > 0) {
                    int additionalForeignKeyIndexPosition = Arrays.binarySearch(addSearchMappingRequest.getPersistencePerspective()
                            .getAdditionalForeignKeys(), new ForeignKey(addSearchMappingRequest.getPropertyName(), null, null),
                            new Comparator<ForeignKey>() {

                        @Override
                        public int compare(ForeignKey o1, ForeignKey o2) {
                            return o1.getManyToField().compareTo(o2.getManyToField());
                        }
                    });
                    ForeignKey foreignKey = null;
                    if (additionalForeignKeyIndexPosition >= 0) {
                        foreignKey = addSearchMappingRequest.getPersistencePerspective().getAdditionalForeignKeys()[additionalForeignKeyIndexPosition];
                    }
                    //in the case of a to-one lookup, an explicit ForeignKey is not passed in. The system should then
                    // default
                    //to just using a ForeignKeyRestrictionType.ID_EQ
                    if (metadata.getForeignKeyCollection()) {
                        if (foreignKey != null &&
                                ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
                            ctoConverter.addCollectionSizeEqMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), AssociationPath.ROOT, addSearchMappingRequest.getPropertyName());
                        } else {
                            AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement
                                    (addSearchMappingRequest.getPropertyName()));
                            ctoConverter.addLongMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), foreignCategory, metadata.getForeignKeyProperty());
                        }
                    } else if (addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName()).getFilterValues()[0] == null || "null".equals(addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName()).getFilterValues()[0])) {
                        ctoConverter.addNullMapping(addSearchMappingRequest
                                .getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName()
                                , associationPath, addSearchMappingRequest.getPropertyName());
                    } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(addSearchMappingRequest.getPropertyName()));
                        ctoConverter.addStringEQMapping(addSearchMappingRequest
                                .getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), foreignCategory, metadata.getForeignKeyProperty());
                    } else {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(addSearchMappingRequest.getPropertyName()));
                        ctoConverter.addLongEQMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), foreignCategory, metadata.getForeignKeyProperty());
                    }
                } else {
                    ctoConverter.addEmptyMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName());
                }
                break;
            case ID:
                switch (metadata.getSecondaryType()) {
                    case INTEGER:
                        ctoConverter.addLongEQMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty);
                        break;
                    case STRING:
                        ctoConverter.addStringEQMapping(addSearchMappingRequest.getCeilingEntityFullyQualifiedClassname(), addSearchMappingRequest.getPropertyName(), associationPath, convertedProperty);
                        break;
                }
                break;
        }
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.BASIC;
    }
}
