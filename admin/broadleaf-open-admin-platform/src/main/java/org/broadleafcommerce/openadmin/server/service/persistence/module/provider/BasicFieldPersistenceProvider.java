/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.EmptyFilterValues;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.RestrictionType;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.IsNotNullPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.IsNullPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.extension.BasicFieldPersistenceProviderExtensionManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component("blBasicFieldPersistenceProvider")
@Scope("prototype")
public class BasicFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    @Resource(name = "blBasicFieldPersistenceProviderExtensionManager")
    protected BasicFieldPersistenceProviderExtensionManager extensionManager;

    protected static final Log LOG = LogFactory.getLog(BasicFieldPersistenceProvider.class);

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        BasicFieldMetadata metadata = populateValueRequest.getMetadata();
        Property property = populateValueRequest.getProperty();
        //don't handle map fields here - we'll get them in a separate provider
        boolean response = detectBasicType(metadata, property);
        if (!response) {
            //we'll allow this provider to handle money filter mapping for persistence
            response = metadata.getFieldType() == SupportedFieldType.MONEY;
        }
        return response;
    }

    protected boolean detectBasicType(FieldMetadata md, Property property) {
        if (!(md instanceof BasicFieldMetadata)) {
            return false;
        }
        BasicFieldMetadata metadata = (BasicFieldMetadata) md;
        return (metadata.getFieldType() == SupportedFieldType.BOOLEAN ||
                metadata.getFieldType() == SupportedFieldType.DATE ||
                metadata.getFieldType() == SupportedFieldType.INTEGER ||
                metadata.getFieldType() == SupportedFieldType.DECIMAL ||
                metadata.getFieldType() == SupportedFieldType.EMAIL ||
                metadata.getFieldType() == SupportedFieldType.FOREIGN_KEY ||
                metadata.getFieldType() == SupportedFieldType.ADDITIONAL_FOREIGN_KEY ||
                metadata.getFieldType() == SupportedFieldType.STRING ||
                metadata.getFieldType() == SupportedFieldType.CODE ||
                metadata.getFieldType() == SupportedFieldType.HTML ||
                metadata.getFieldType() == SupportedFieldType.HTML_BASIC ||
                metadata.getFieldType() == SupportedFieldType.MONEY ||
                metadata.getFieldType() == SupportedFieldType.ASSET_URL ||
                metadata.getFieldType() == SupportedFieldType.ID) &&
                (property == null ||
                !property.getName().contains(FieldManager.MAPFIELDSEPARATOR));
    }

    protected boolean detectAdditionalSearchTypes(FieldMetadata md, Property property) {
        if (!(md instanceof BasicFieldMetadata)) {
            return false;
        }
        BasicFieldMetadata metadata = (BasicFieldMetadata) md;
        return (metadata.getFieldType() == SupportedFieldType.BROADLEAF_ENUMERATION ||
                metadata.getFieldType() == SupportedFieldType.EXPLICIT_ENUMERATION ||
                metadata.getFieldType() == SupportedFieldType.DATA_DRIVEN_ENUMERATION) &&
                (property == null || !property.getName().contains(FieldManager.MAPFIELDSEPARATOR));
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        BasicFieldMetadata metadata = extractValueRequest.getMetadata();
        //don't handle map fields here - we'll get them in a separate provider
        return detectBasicType(metadata, property);
    }

    protected boolean canHandleSearchMapping(AddSearchMappingRequest addSearchMappingRequest,
            List<FilterMapping> filterMappings) {
        FieldMetadata metadata = addSearchMappingRequest.getMergedProperties().get
                (addSearchMappingRequest.getPropertyName());
        Property property = null;
        //don't handle map fields here - we'll get them in a separate provider
        boolean response = detectBasicType(metadata, property) || detectAdditionalSearchTypes(metadata, property);

        return response;
    }

    @Override
    public MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        boolean dirty = false;
        try {
            Property prop = populateValueRequest.getProperty();
            Object origInstanceValue = populateValueRequest.getFieldManager().getFieldValue(instance, prop.getName());
            switch (populateValueRequest.getMetadata().getFieldType()) {
                case BOOLEAN:
                    boolean v = Boolean.valueOf(populateValueRequest.getRequestedValue());
                    prop.setOriginalValue(String.valueOf(origInstanceValue));
                    prop.setOriginalDisplayValue(prop.getOriginalValue());
                    try {
                        dirty = checkDirtyState(populateValueRequest, instance, v);
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), v);
                    } catch (IllegalArgumentException e) {
                        boolean isChar = populateValueRequest.getRequestedValue().toCharArray().length > 1 ? false : true;
                        char c;
                        if (isChar) {
                            c = populateValueRequest.getRequestedValue().toCharArray()[0];
                        } else {
                            c = Boolean.valueOf(populateValueRequest.getRequestedValue()) ? 'Y' : 'N';
                        }
                        dirty = checkDirtyState(populateValueRequest, instance, c);
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), c);
                    }
                    break;
                case DATE:
                    Date date = (Date) populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName());
                    String oldValue = null;

                    DateFormat dateFormat = getDateFormatToPopulateValue(populateValueRequest, instance);

                    if (date != null) {
                        oldValue = dateFormat.format(date);
                    }

                    prop.setOriginalValue(oldValue);
                    prop.setOriginalDisplayValue(prop.getOriginalValue());
                    dirty = !StringUtils.equals(oldValue, populateValueRequest.getRequestedValue());
                    populateValueRequest.getFieldManager().setFieldValue(instance,
                            populateValueRequest.getProperty().getName(), dateFormat.parse(populateValueRequest.getRequestedValue()));
                    break;
                case DECIMAL:
                    if (origInstanceValue != null) {
                        prop.setOriginalValue(String.valueOf(origInstanceValue));
                        prop.setOriginalDisplayValue(prop.getOriginalValue());
                    }
                    if (BigDecimal.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        DecimalFormat format = populateValueRequest.getDataFormatProvider().getDecimalFormatter();
                        format.setParseBigDecimal(true);
                        BigDecimal val = (BigDecimal) format.parse(populateValueRequest.getRequestedValue());
                        dirty = checkDirtyState(populateValueRequest, instance, val);

                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), val);
                        format.setParseBigDecimal(false);
                    } else {
                        Double val = populateValueRequest.getDataFormatProvider().getDecimalFormatter().parse(populateValueRequest.getRequestedValue()).doubleValue();
                        dirty = checkDirtyState(populateValueRequest, instance, val);
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), val);
                    }
                    break;
                case MONEY:
                    if (origInstanceValue != null) {
                        prop.setOriginalValue(String.valueOf(origInstanceValue));
                        prop.setOriginalDisplayValue(prop.getOriginalValue());
                    }
                    if (BigDecimal.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        DecimalFormat format = populateValueRequest.getDataFormatProvider().getDecimalFormatter();
                        format.setParseBigDecimal(true);
                        BigDecimal val = (BigDecimal) format.parse(populateValueRequest.getRequestedValue());
                        dirty = checkDirtyState(populateValueRequest, instance, val);
                        populateValueRequest.getFieldManager()
                                .setFieldValue(instance, populateValueRequest.getProperty().getName(), val);
                        format.setParseBigDecimal(true);
                    } else if (Double.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Double val = populateValueRequest.getDataFormatProvider().getDecimalFormatter().parse(populateValueRequest.getRequestedValue()).doubleValue();
                        dirty = checkDirtyState(populateValueRequest, instance, val);
                        LOG.warn("The requested Money field is of type double and could result in a loss of precision." +
                                " Broadleaf recommends that the type of all Money fields be 'BigDecimal' in order to avoid" +
                                " this loss of precision that could occur.");
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), val);
                    } else {
                        DecimalFormat format = populateValueRequest.getDataFormatProvider().getDecimalFormatter();
                        format.setParseBigDecimal(true);
                        BigDecimal val = (BigDecimal) format.parse(populateValueRequest.getRequestedValue());
                        dirty = checkDirtyState(populateValueRequest, instance, val);
                        populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), new Money(val));
                        format.setParseBigDecimal(false);
                    }
                    break;
                case INTEGER:
                    if (origInstanceValue != null) {
                        prop.setOriginalValue(String.valueOf(origInstanceValue));
                        prop.setOriginalDisplayValue(prop.getOriginalValue());
                    }
                    if (int.class.isAssignableFrom(populateValueRequest.getReturnType()) || Integer.class
                            .isAssignableFrom(populateValueRequest.getReturnType())) {
                        dirty = checkDirtyState(populateValueRequest, instance, Integer.valueOf(populateValueRequest.getRequestedValue()));
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), Integer.valueOf(populateValueRequest
                                        .getRequestedValue()));
                    } else if (byte.class.isAssignableFrom(populateValueRequest.getReturnType()) || Byte.class
                            .isAssignableFrom(populateValueRequest.getReturnType())) {
                        dirty = checkDirtyState(populateValueRequest, instance, Byte.valueOf(populateValueRequest.getRequestedValue()));
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), Byte.valueOf(populateValueRequest
                                        .getRequestedValue()));
                    } else if (short.class.isAssignableFrom(populateValueRequest.getReturnType()) || Short.class
                            .isAssignableFrom(populateValueRequest.getReturnType())) {
                        dirty = checkDirtyState(populateValueRequest, instance, Short.valueOf(populateValueRequest.getRequestedValue()));
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), Short.valueOf(populateValueRequest
                                        .getRequestedValue()));
                    } else if (long.class.isAssignableFrom(populateValueRequest.getReturnType()) || Long.class
                            .isAssignableFrom(populateValueRequest.getReturnType())) {
                        dirty = checkDirtyState(populateValueRequest, instance, Long.valueOf(populateValueRequest.getRequestedValue()));
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), Long.valueOf(populateValueRequest
                                        .getRequestedValue()));
                    }
                    break;
                case CODE:
                    // **NOTE** We want to fall through in this case, do not break.
                    setNonDisplayableValues(populateValueRequest);
                case STRING:
                case HTML_BASIC:
                case HTML:
                case EMAIL:
                    if (origInstanceValue != null) {
                        prop.setOriginalValue(String.valueOf(origInstanceValue));
                        prop.setOriginalDisplayValue(prop.getOriginalValue());
                    }
                    dirty = checkDirtyState(populateValueRequest, instance, populateValueRequest.getRequestedValue());
                    populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty()
                            .getName(), populateValueRequest.getRequestedValue());
                    break;
                case FOREIGN_KEY: {
                    if (origInstanceValue != null) {
                        prop.setOriginalValue(String.valueOf(origInstanceValue));
                    }
                    Serializable foreignInstance;
                    if (StringUtils.isEmpty(populateValueRequest.getRequestedValue())) {
                        foreignInstance = null;
                    } else {
                        if (SupportedFieldType.INTEGER.toString().equals(populateValueRequest.getMetadata()
                                .getSecondaryType().toString())) {
                            foreignInstance = populateValueRequest.getPersistenceManager().getDynamicEntityDao()
                                    .retrieve(Class.forName(populateValueRequest.getMetadata().getForeignKeyClass()),
                                            Long.valueOf(populateValueRequest.getRequestedValue()));
                        } else {
                            foreignInstance = populateValueRequest.getPersistenceManager().getDynamicEntityDao()
                                    .retrieve(Class.forName(populateValueRequest.getMetadata().getForeignKeyClass()),
                                            populateValueRequest.getRequestedValue());
                        }
                    }

                    if (Collection.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Collection collection;
                        try {
                            collection = (Collection) populateValueRequest.getFieldManager().getFieldValue(instance,
                                    populateValueRequest.getProperty().getName());
                        } catch (FieldNotAvailableException e) {
                            throw new IllegalArgumentException(e);
                        }
                        if (!collection.contains(foreignInstance)) {
                            collection.add(foreignInstance);
                            dirty = true;
                        }
                    } else if (Map.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        throw new IllegalArgumentException("Map structures are not supported for foreign key fields.");
                    } else {
                        dirty = checkDirtyState(populateValueRequest, instance, foreignInstance);
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), foreignInstance);
                    }
                    break;
                }
                case ADDITIONAL_FOREIGN_KEY: {
                    Serializable foreignInstance;
                    if (StringUtils.isEmpty(populateValueRequest.getRequestedValue())) {
                        foreignInstance = null;
                    } else {
                        if (SupportedFieldType.INTEGER.toString().equals(populateValueRequest.getMetadata()
                                .getSecondaryType().toString())) {
                            foreignInstance = populateValueRequest.getPersistenceManager().getDynamicEntityDao()
                                    .retrieve(Class.forName(populateValueRequest.getMetadata().getForeignKeyClass()),
                                            Long.valueOf(populateValueRequest.getRequestedValue()));
                        } else {
                            foreignInstance = populateValueRequest.getPersistenceManager().getDynamicEntityDao()
                                    .retrieve(Class.forName(populateValueRequest.getMetadata().getForeignKeyClass()),
                                            populateValueRequest.getRequestedValue());
                        }
                    }

                    // Best guess at grabbing the original display value
                    String fkProp = populateValueRequest.getMetadata().getForeignKeyDisplayValueProperty();
                    Object origDispVal = null;
                    if (origInstanceValue != null) {
                        if (AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY.equals(fkProp)) {
                            if (origInstanceValue instanceof AdminMainEntity) {
                                origDispVal = ((AdminMainEntity) origInstanceValue).getMainEntityName();
                            }
                        } else {
                            origDispVal = populateValueRequest.getFieldManager().getFieldValue(origInstanceValue, fkProp);
                        }
                    }
                    if (origDispVal != null) {
                        prop.setOriginalDisplayValue(String.valueOf(origDispVal));
                        Session session = populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager().unwrap(Session.class);
                        String originalValueFromSession = String.valueOf(session.getIdentifier(origInstanceValue));
                        prop.setOriginalValue(originalValueFromSession);
                    }

                    if (Collection.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        Collection collection;
                        try {
                            collection = (Collection) populateValueRequest.getFieldManager().getFieldValue(instance,
                                    populateValueRequest.getProperty().getName());
                        } catch (FieldNotAvailableException e) {
                            throw new IllegalArgumentException(e);
                        }
                        if (!collection.contains(foreignInstance)) {
                            collection.add(foreignInstance);
                            dirty = true;
                        }
                    } else if (Map.class.isAssignableFrom(populateValueRequest.getReturnType())) {
                        throw new IllegalArgumentException("Map structures are not supported for foreign key fields.");
                    } else {
                        dirty = checkDirtyState(populateValueRequest, instance, foreignInstance);
                        populateValueRequest.getFieldManager().setFieldValue(instance,
                                populateValueRequest.getProperty().getName(), foreignInstance);
                    }
                    break;
                }
                case ID:
                    if (populateValueRequest.getSetId()) {
                        switch (populateValueRequest.getMetadata().getSecondaryType()) {
                            case INTEGER:
                                dirty = checkDirtyState(populateValueRequest, instance, Long.valueOf(populateValueRequest.getRequestedValue()));
                                populateValueRequest.getFieldManager().setFieldValue(instance,
                                        populateValueRequest.getProperty().getName(),
                                        Long.valueOf(populateValueRequest.getRequestedValue()));
                                break;
                            case STRING:
                                dirty = checkDirtyState(populateValueRequest, instance, populateValueRequest.getRequestedValue());
                                populateValueRequest.getFieldManager().setFieldValue(instance,
                                        populateValueRequest.getProperty().getName(),
                                        populateValueRequest.getRequestedValue());
                                break;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        populateValueRequest.getProperty().setIsDirty(dirty);
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest,
                                              Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        try {
            if (extractValueRequest.getRequestedValue() != null) {
                String val = null;

                DateFormat dataFormat = getDateFormatToExtractValue(extractValueRequest);

                if (extractValueRequest.getMetadata().getForeignKeyCollection()) {
                    ((BasicFieldMetadata) property.getMetadata()).setFieldType(extractValueRequest.getMetadata()
                            .getFieldType());
                } else if (extractValueRequest.getMetadata().getFieldType().equals(SupportedFieldType.BOOLEAN) &&
                        extractValueRequest.getRequestedValue() instanceof Character) {
                    val = (extractValueRequest.getRequestedValue().equals('Y')) ? "true" : "false";
                } else if (Date.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = dataFormat.format((Date) extractValueRequest.getRequestedValue());
                } else if (Timestamp.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = dataFormat.format(new Date(((Timestamp) extractValueRequest.getRequestedValue()).getTime()));
                } else if (Calendar.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = dataFormat.format(((Calendar) extractValueRequest.getRequestedValue()).getTime());
                } else if (Double.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    val = extractValueRequest.getDataFormatProvider().getDecimalFormatter().format
                            (extractValueRequest.getRequestedValue());
                } else if (BigDecimal.class.isAssignableFrom(extractValueRequest.getRequestedValue().getClass())) {
                    BigDecimal decimal = (BigDecimal) extractValueRequest.getRequestedValue();
                    DecimalFormat format = extractValueRequest.getDataFormatProvider().getDecimalFormatter();
                    //track all the decimal places in the scale of the BigDecimal - even if they're all zeros
                    StringBuilder sb = new StringBuilder();
                    sb.append("0");
                    if (decimal.scale() > 0) {
                        sb.append(".");
                        for (int j = 0; j < decimal.scale(); j++) {
                            sb.append("0");
                        }
                    }
                    format.applyPattern(sb.toString());
                    val = format.format(extractValueRequest.getRequestedValue());
                } else if (extractValueRequest.getMetadata().getForeignKeyClass() != null) {
                    try {
                        val = extractValueRequest.getFieldManager().getFieldValue
                                (extractValueRequest.getRequestedValue(), extractValueRequest.getMetadata()
                                        .getForeignKeyProperty()).toString();
                        if (extensionManager != null) {
                            ExtensionResultHolder<Serializable> resultHolder = new
                                    ExtensionResultHolder<Serializable>();
                            ExtensionResultStatusType result = extensionManager.getProxy().transformForeignKey
                                    (extractValueRequest, property, resultHolder);
                            if (ExtensionResultStatusType.NOT_HANDLED != result && resultHolder.getResult() != null) {
                                val = String.valueOf(resultHolder.getResult());
                            }
                        }

                        //see if there's a name property and use it for the display value
                        String entityName = null;
                        if (extractValueRequest.getRequestedValue() instanceof AdminMainEntity) {
                            entityName = ((AdminMainEntity) extractValueRequest.getRequestedValue())
                                    .getMainEntityName();
                        }

                        Object temp = null;
                        if (!StringUtils.isEmpty(extractValueRequest.getMetadata().getForeignKeyDisplayValueProperty
                                ())) {
                            String nameProperty = extractValueRequest.getMetadata().getForeignKeyDisplayValueProperty();
                            try {
                                temp = extractValueRequest.getFieldManager().getFieldValue(extractValueRequest
                                        .getRequestedValue(), nameProperty);
                            } catch (FieldNotAvailableException e) {
                                //do nothing
                            }
                        }

                        if (temp == null && StringUtils.isEmpty(entityName)) {
                            try {
                                temp = extractValueRequest.getFieldManager().getFieldValue(extractValueRequest
                                        .getRequestedValue(), "name");
                            } catch (FieldNotAvailableException e) {
                                //do nothing
                            }
                        }

                        if (temp != null) {
                            extractValueRequest.setDisplayVal(temp.toString());
                        } else if (!StringUtils.isEmpty(entityName)) {
                            extractValueRequest.setDisplayVal(entityName);
                        }
                    } catch (FieldNotAvailableException e) {
                        throw new IllegalArgumentException(e);
                    }
                } else if (SupportedFieldType.ID == extractValueRequest.getMetadata().getFieldType()) {
                    val = extractValueRequest.getRequestedValue().toString();
                    if (extensionManager != null) {
                        ExtensionResultHolder<Serializable> resultHolder = new
                                ExtensionResultHolder<Serializable>();
                        ExtensionResultStatusType result = extensionManager.getProxy().transformId
                                (extractValueRequest, property, resultHolder);
                        if (ExtensionResultStatusType.NOT_HANDLED != result && resultHolder.getResult() != null) {
                            val = String.valueOf(resultHolder.getResult());
                        }
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
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse addSearchMapping(AddSearchMappingRequest addSearchMappingRequest,
                                                  List<FilterMapping> filterMappings) {
        if (!canHandleSearchMapping(addSearchMappingRequest, filterMappings)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        Class clazz;
        try {
            clazz = Class.forName(addSearchMappingRequest.getMergedProperties().get(addSearchMappingRequest
                    .getPropertyName()).getInheritedFromType());
        } catch (ClassNotFoundException e) {
            throw new PersistenceException(e);
        }
        Field field = addSearchMappingRequest.getFieldManager().getField(clazz,
                addSearchMappingRequest.getPropertyName());
        Class<?> targetType = null;
        if (field != null) {
            targetType = field.getType();
        }
        BasicFieldMetadata metadata = (BasicFieldMetadata) addSearchMappingRequest.getMergedProperties().get
                (addSearchMappingRequest.getPropertyName());

        FilterAndSortCriteria fasc = addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName());

        FilterMapping filterMapping = new FilterMapping()
                .withInheritedFromClass(clazz)
                .withFullPropertyName(addSearchMappingRequest.getPropertyName())
                .withFilterValues(fasc.getFilterValues())
                .withSortDirection(fasc.getSortDirection())
                .withOrder(fasc.getOrder())
                .withNullsLast(fasc.isNullsLast());
        filterMappings.add(filterMapping);

        if (fasc.hasSpecialFilterValue()) {
            filterMapping.setDirectFilterValues(new EmptyFilterValues());

            // Handle special values on a case by case basis
            List<String> specialValues = fasc.getSpecialFilterValues();
            if (specialValues.contains(FilterAndSortCriteria.IS_NULL_FILTER_VALUE)) {
                filterMapping.setRestriction(new Restriction().withPredicateProvider(new IsNullPredicateProvider()));
            }
            if (specialValues.contains(FilterAndSortCriteria.IS_NOT_NULL_FILTER_VALUE)) {
                filterMapping.setRestriction(new Restriction().withPredicateProvider(new IsNotNullPredicateProvider()));
            }
        } else {
            if (fasc.getRestrictionType() != null) {
                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().
                        getRestriction(fasc.getRestrictionType().getType(),
                                addSearchMappingRequest.getPropertyName()));
            } else {
                switch (metadata.getFieldType()) {
                    case BOOLEAN:
                        if (targetType == null || targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {

                            filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                    (RestrictionType.BOOLEAN.getType(), addSearchMappingRequest.getPropertyName()));
                        } else {
                            filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                    (RestrictionType.CHARACTER.getType(), addSearchMappingRequest.getPropertyName()));
                        }
                        break;
                    case DATE:
                        filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                (RestrictionType.DATE.getType(), addSearchMappingRequest.getPropertyName()));
                        break;
                    case DECIMAL:
                    case MONEY:
                        filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                (RestrictionType.DECIMAL.getType(), addSearchMappingRequest.getPropertyName()));
                        break;
                    case INTEGER:
                        filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                (RestrictionType.LONG.getType(), addSearchMappingRequest.getPropertyName()));
                        break;
                    case BROADLEAF_ENUMERATION:
                        filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction(RestrictionType.STRING_EQUAL.getType(), addSearchMappingRequest.getPropertyName()));
                        break;
                    default:
                        filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                (RestrictionType.STRING_LIKE.getType(), addSearchMappingRequest.getPropertyName()));
                        break;
                    case FOREIGN_KEY:
                        if (!addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName())
                                .getFilterValues().isEmpty()) {
                            ForeignKey foreignKey = (ForeignKey) addSearchMappingRequest.getPersistencePerspective()
                                    .getPersistencePerspectiveItems().get
                                    (PersistencePerspectiveItemType.FOREIGNKEY);
                            if (metadata.getForeignKeyCollection()) {
                                if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey
                                        .getRestrictionType().toString())) {
                                    filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory()
                                            .getRestriction(RestrictionType.COLLECTION_SIZE_EQUAL.getType(),
                                                    addSearchMappingRequest.getPropertyName()));
                                    filterMapping.setFieldPath(new FieldPath());
                                } else {
                                    filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory()
                                            .getRestriction(RestrictionType.LONG.getType(),
                                                    addSearchMappingRequest.getPropertyName()));
                                    filterMapping.setFieldPath(new FieldPath().withTargetProperty
                                            (addSearchMappingRequest
                                                    .getPropertyName() + "." + metadata.getForeignKeyProperty()));
                                }
                            } else if (addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName())
                                    .getFilterValues().get(0) == null || "null".equals(addSearchMappingRequest
                                    .getRequestedCto().get
                                    (addSearchMappingRequest.getPropertyName()).getFilterValues().get(0))) {
                                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                        (RestrictionType.IS_NULL_LONG.getType(), addSearchMappingRequest.getPropertyName()));
                            } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                        (RestrictionType.STRING_EQUAL.getType(), addSearchMappingRequest.getPropertyName()));
                                filterMapping.setFieldPath(new FieldPath().withTargetProperty(addSearchMappingRequest
                                        .getPropertyName() + "." + metadata.getForeignKeyProperty()));
                            } else {
                                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction
                                        (RestrictionType.LONG_EQUAL.getType(), addSearchMappingRequest.getPropertyName()));
                                filterMapping.setFieldPath(new FieldPath().withTargetProperty(addSearchMappingRequest
                                        .getPropertyName() + "." + metadata.getForeignKeyProperty()));
                            }
                        }
                        break;
                    case ADDITIONAL_FOREIGN_KEY:
                        int additionalForeignKeyIndexPosition = Arrays.binarySearch(addSearchMappingRequest
                                .getPersistencePerspective()
                                .getAdditionalForeignKeys(), new ForeignKey(addSearchMappingRequest
                                .getPropertyName(),
                                null, null),
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
                        // in the case of a to-one lookup, an explicit ForeignKey is not passed in. The system should then
                        // default to just using a ForeignKeyRestrictionType.ID_EQ
                        if (metadata.getForeignKeyCollection()) {
                            if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey
                                    .getRestrictionType().toString())) {
                                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory()
                                        .getRestriction(RestrictionType.COLLECTION_SIZE_EQUAL.getType(),
                                                addSearchMappingRequest.getPropertyName()));
                                filterMapping.setFieldPath(new FieldPath());
                            } else {
                                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction(RestrictionType.LONG.getType(), addSearchMappingRequest.getPropertyName()));
                                filterMapping.setFieldPath(new FieldPath().withTargetProperty(addSearchMappingRequest.getPropertyName() + "." + metadata.getForeignKeyProperty()));
                            }
                        } else if (CollectionUtils.isEmpty(addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName()).getFilterValues()) ||
                                addSearchMappingRequest.getRequestedCto().get(addSearchMappingRequest.getPropertyName
                                        ()).getFilterValues().get(0) == null || "null".equals(addSearchMappingRequest.getRequestedCto().get
                                        (addSearchMappingRequest.getPropertyName()).getFilterValues().get(0))) {
                            filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction(RestrictionType.IS_NULL_LONG.getType(), addSearchMappingRequest.getPropertyName()));
                        } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                            filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction(RestrictionType.STRING_EQUAL.getType(), addSearchMappingRequest.getPropertyName()));
                            filterMapping.setFieldPath(new FieldPath().withTargetProperty(addSearchMappingRequest.getPropertyName() + "." + metadata.getForeignKeyProperty()));
                        } else {
                            filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().getRestriction(RestrictionType.LONG_EQUAL.getType(), addSearchMappingRequest.getPropertyName()));
                            filterMapping.setFieldPath(new FieldPath().withTargetProperty(addSearchMappingRequest
                                    .getPropertyName() + "." + metadata.getForeignKeyProperty()));
                        }
                        break;
                    case ID:
                        switch (metadata.getSecondaryType()) {
                            case INTEGER:
                                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory()
                                        .getRestriction
                                        (RestrictionType.LONG_EQUAL.getType(), addSearchMappingRequest.getPropertyName()));
                                break;
                            case STRING:
                                filterMapping.setRestriction(addSearchMappingRequest.getRestrictionFactory().
                                        getRestriction(RestrictionType.STRING_EQUAL.getType(),
                                                addSearchMappingRequest.getPropertyName()));
                                break;
                        }
                        break;
                }
            }
        }
        return MetadataProviderResponse.HANDLED;
    }

    protected SimpleDateFormat getDateFormatToPopulateValue(PopulateValueRequest populateValueRequest, Serializable instance) {
        return populateValueRequest.getDataFormatProvider().getSimpleDateFormatter();
    }

    protected SimpleDateFormat getDateFormatToExtractValue(ExtractValueRequest extractValueRequest) {
        return extractValueRequest.getDataFormatProvider().getSimpleDateFormatter();
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.BASIC;
    }
}
