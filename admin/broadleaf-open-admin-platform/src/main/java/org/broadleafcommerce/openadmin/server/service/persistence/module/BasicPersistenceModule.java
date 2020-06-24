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

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.TranslationImpl;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.FormatUtil;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.util.ValidationUtil;
import org.broadleafcommerce.common.util.dao.TQJoin;
import org.broadleafcommerce.common.util.dao.TQOrder;
import org.broadleafcommerce.common.util.dao.TQRestriction;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.EntityResult;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SortDirection;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.AdvancedCollectionFieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.persistence.ParentEntityPersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaConversionException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslator;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.RestrictionFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.converter.FilterValueConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.EqPredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.LikePredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.extension.BasicPersistenceModuleExtensionManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.EntityValidatorService;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PopulateValueRequestValidator;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidationResult;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * @author jfischer
 */
@Primary
@Component("blBasicPersistenceModule")
@Scope("prototype")
public class BasicPersistenceModule implements PersistenceModule, RecordHelper, ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(BasicPersistenceModule.class);

    public static final String MAIN_ENTITY_NAME_PROPERTY = "MAIN_ENTITY_NAME";
    public static final String ALTERNATE_ID_PROPERTY = "ALTERNATE_ID";

    protected ApplicationContext applicationContext;
    protected PersistenceManager persistenceManager;

    @Resource(name = "blEntityValidatorService")
    protected EntityValidatorService entityValidatorService;

    @Resource(name = "blPersistenceProviders")
    protected List<FieldPersistenceProvider> fieldPersistenceProviders = new ArrayList<FieldPersistenceProvider>();

    @Resource(name = "blPopulateValueRequestValidators")
    protected List<PopulateValueRequestValidator> populateValidators;

    @Resource(name = "blDefaultFieldPersistenceProvider")
    protected FieldPersistenceProvider defaultFieldPersistenceProvider;

    @Resource(name = "blCriteriaTranslator")
    protected CriteriaTranslator criteriaTranslator;

    @Resource(name = "blRestrictionFactory")
    protected RestrictionFactory restrictionFactory;

    @Resource(name = "blBasicPersistenceModuleExtensionManager")
    protected BasicPersistenceModuleExtensionManager extensionManager;

    @Resource(name = "blFetchWrapper")
    protected FetchWrapper fetchWrapper;

    @Value("${use.translation.search:false}")
    protected boolean useTranslationSearch;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    @PostConstruct
    public void init() {
        Collections.sort(fieldPersistenceProviders, new Comparator<FieldPersistenceProvider>() {

            @Override
            public int compare(FieldPersistenceProvider o1, FieldPersistenceProvider o2) {
                return Integer.compare(o1.getOrder(), o2.getOrder());
            }
        });
        Collections.sort(populateValidators, new Comparator<PopulateValueRequestValidator>() {

            @Override
            public int compare(PopulateValueRequestValidator o1, PopulateValueRequestValidator o2) {
                return Integer.compare(o1.getOrder(), o2.getOrder());
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean isCompatible(OperationType operationType) {
        return OperationType.BASIC == operationType || OperationType.NONDESTRUCTIVEREMOVE == operationType;
    }

    @Override
    public FieldManager getFieldManager() {
        return persistenceManager.getDynamicEntityDao().getFieldManager();
    }

    @Override
    public FieldManager getFieldManager(boolean cleanFieldManger) {
        return persistenceManager.getDynamicEntityDao().getFieldManager(cleanFieldManger);
    }

    @Override
    public DecimalFormat getDecimalFormatter() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        Locale locale = brc.getJavaLocale();
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(locale);
        format.applyPattern("0.########");
        format.setGroupingUsed(false);
        return format;
    }

    @Override
    public SimpleDateFormat getSimpleDateFormatter() {
        return FormatUtil.getDateFormat();
    }

    protected Map<String, FieldMetadata> filterOutCollectionMetadata(Map<String, FieldMetadata> metadata) {
        if (metadata == null) {
            return null;
        }
        Map<String, FieldMetadata> newMap = new HashMap<String, FieldMetadata>();
        for (Map.Entry<String, FieldMetadata> entry : metadata.entrySet()) {
            String fieldName = entry.getKey();
            FieldMetadata md = entry.getValue();
            // Detect instances where the actual metadata for the field is some sort of CollectionMetadata but also corresponds
            // to a ForeignKey and ensure that gets included in the filtered map. That way the {@link BasicPersistenceModule}
            // can appropriate handle filtration and population
            if (entry.getValue() instanceof BasicFieldMetadata) {
                newMap.put(fieldName, md);
            } else if (md.getAdditionalMetadata().containsKey(AdvancedCollectionFieldMetadataProvider.FOREIGN_KEY_ADDITIONAL_METADATA_KEY)) {
                newMap.put(fieldName,
                        (BasicFieldMetadata) md.getAdditionalMetadata().get(AdvancedCollectionFieldMetadataProvider.FOREIGN_KEY_ADDITIONAL_METADATA_KEY));
            }
        }

        return newMap;
    }

    protected Class<?> getBasicBroadleafType(SupportedFieldType fieldType) {
        Class<?> response;
        switch (fieldType) {
            case BOOLEAN:
                response = Boolean.TYPE;
                break;
            case DATE:
                response = Date.class;
                break;
            case DECIMAL:
                response = BigDecimal.class;
                break;
            case MONEY:
                response = Money.class;
                break;
            case INTEGER:
                response = Integer.TYPE;
                break;
            case UNKNOWN:
                response = null;
                break;
            default:
                response = String.class;
                break;
        }

        return response;
    }

    @Override
    public Serializable createPopulatedInstance(Serializable instance, Entity entity,
            Map<String, FieldMetadata> unfilteredProperties, Boolean setId) throws ValidationException {
        return createPopulatedInstance(instance, entity, unfilteredProperties, setId, true);
    }

    @Override
    public Serializable createPopulatedInstance(Serializable instance, Entity entity,
            Map<String, FieldMetadata> unfilteredProperties, Boolean setId, Boolean validateUnsubmittedProperties) throws ValidationException {
        final Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(unfilteredProperties);
        FieldManager fieldManager = getFieldManager();
        boolean handled = false;
        for (FieldPersistenceProvider fieldPersistenceProvider : fieldPersistenceProviders) {
            MetadataProviderResponse response = fieldPersistenceProvider.filterProperties(new AddFilterPropertiesRequest(entity), unfilteredProperties);
            if (MetadataProviderResponse.NOT_HANDLED != response) {
                handled = true;
            }
            if (MetadataProviderResponse.HANDLED_BREAK == response) {
                break;
            }
        }
        if (!handled) {
            defaultFieldPersistenceProvider.filterProperties(new AddFilterPropertiesRequest(entity), unfilteredProperties);
        }
        //Order media field, map field and rule builder fields last, as they will have some validation components that depend on previous values
        Property[] sortedProperties = entity.getProperties();
        Arrays.sort(sortedProperties, new Comparator<Property>() {

            @Override
            public int compare(Property o1, Property o2) {
                BasicFieldMetadata mo1 = (BasicFieldMetadata) mergedProperties.get(o1.getName());
                BasicFieldMetadata mo2 = (BasicFieldMetadata) mergedProperties.get(o2.getName());
                boolean isLate1 = mo1 != null && mo1.getFieldType() != null && mo1.getName() != null && (SupportedFieldType.RULE_SIMPLE==mo1.getFieldType() ||
                        SupportedFieldType.RULE_WITH_QUANTITY==mo1.getFieldType() ||
                        SupportedFieldType.RULE_SIMPLE_TIME==mo1.getFieldType() ||
                        SupportedFieldType.MEDIA==mo1.getFieldType() || o1.getName().contains(FieldManager.MAPFIELDSEPARATOR));
                boolean isLate2 = mo2 != null && mo2.getFieldType() != null && mo2.getName() != null && (SupportedFieldType.RULE_SIMPLE==mo2.getFieldType() ||
                        SupportedFieldType.RULE_WITH_QUANTITY==mo2.getFieldType() ||
                        SupportedFieldType.RULE_SIMPLE_TIME==mo2.getFieldType() ||
                        SupportedFieldType.MEDIA==mo2.getFieldType() || o2.getName().contains(FieldManager.MAPFIELDSEPARATOR));
                if (isLate1 && !isLate2) {
                    return 1;
                } else if (!isLate1 && isLate2) {
                    return -1;
                }
                return 0;
            }
        });
        Session session = getPersistenceManager().getDynamicEntityDao().getStandardEntityManager().unwrap(Session.class);
        FlushMode originalFlushMode = session.getHibernateFlushMode();
        try {
            session.setHibernateFlushMode(FlushMode.MANUAL);
            RuntimeException entityPersistenceException = null;
            for (Property property : sortedProperties) {
                BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(property.getName());
                Class<?> returnType;
                if (!property.getName().contains(FieldManager.MAPFIELDSEPARATOR) && !property.getName().startsWith("__")) {
                    Field field = fieldManager.getField(instance.getClass(), property.getName());
                    if (field == null) {
                        LOG.debug("Unable to find a bean property for the reported property: " + StringUtil.sanitize(property.getName()) + ". Ignoring property.");
                        continue;
                    }
                    returnType = field.getType();
                } else {
                    if (metadata == null) {
                        LOG.debug("Unable to find a metadata property for the reported property: " + StringUtil.sanitize(property.getName()) + ". Ignoring property.");
                        continue;
                    }
                    returnType = getMapFieldType(instance, fieldManager, property);
                    if (returnType == null) {
                        returnType = getBasicBroadleafType(metadata.getFieldType());
                    }
                }
                if (returnType == null) {
                    throw new IllegalAccessException("Unable to determine the value type for the property (" + property.getName() + ")");
                }
                String value = property.getValue();
                if (metadata != null) {

                    if (metadata.getFieldType().equals(SupportedFieldType.BOOLEAN)) {
                        if (value == null) {
                            String defaultValue = metadata.getDefaultValue();
                            value = StringUtils.isBlank(defaultValue)? "false" : defaultValue;
                        }
                    } else if (metadata.getFieldType().equals(SupportedFieldType.DATE)) {
                        if (StringUtils.isEmpty(value)) {
                            value = null;
                        }
                    }

                    if (attemptToPopulateValue(property, fieldManager, instance, setId, metadata, entity, value)) {
                        boolean isValid = true;
                        PopulateValueRequest request = new PopulateValueRequest(setId, fieldManager, property, metadata, returnType, value, persistenceManager, this, entity.isPreAdd());
                        handled = false;
                        if (value != null) {
                            for (PopulateValueRequestValidator validator : populateValidators) {
                                PropertyValidationResult validationResult = validator.validate(request, instance);
                                if (!validationResult.isValid()) {
                                    entity.addValidationError(property.getName(), validationResult.getErrorMessage());
                                    isValid = false;
                                }
                            }
                        }
                        if (isValid) {
                            try {
                                boolean isBreakDetected = false;
                                for (FieldPersistenceProvider fieldPersistenceProvider : fieldPersistenceProviders) {
                                    if ((!isBreakDetected || fieldPersistenceProvider.alwaysRun()) && (value != null || fieldPersistenceProvider.canHandlePopulateNull())) {
                                        MetadataProviderResponse response = fieldPersistenceProvider.populateValue(request, instance);
                                        if (MetadataProviderResponse.NOT_HANDLED != response) {
                                            handled = true;
                                        }
                                        if (MetadataProviderResponse.HANDLED_BREAK == response) {
                                            isBreakDetected = true;
                                        }
                                    }
                                }
                                if (!handled) {
                                    if (value == null) {
                                        property.setIsDirty(true);
                                    }
                                    defaultFieldPersistenceProvider.populateValue(new PopulateValueRequest(setId, fieldManager, property, metadata, returnType, value, persistenceManager, this, entity.isPreAdd()), instance);
                                    if (value == null) {
                                        fieldManager.setFieldValue(instance, property.getName(), null);
                                    }
                                }
                            } catch (ParentEntityPersistenceException | javax.validation.ValidationException e) {
                                entityPersistenceException = e;
                                cleanupFailedPersistenceAttempt(instance);
                                break;
                            }
                        }
                    }
                }
            }
            // Only check validation if not the initial add
            if (!entity.isPreAdd()) {
                validate(entity, instance, mergedProperties, validateUnsubmittedProperties);
            }
            //if validation failed, refresh the current instance so that none of the changes will be persisted
            if (entity.isValidationFailure()) {
                //only refresh the instance if it was managed to begin with
                if (persistenceManager.getDynamicEntityDao().getStandardEntityManager().contains(instance)) {
                    persistenceManager.getDynamicEntityDao().refresh(instance);
                }

                //re-initialize the valid properties for the entity in order to deal with the potential of not
                //completely sending over all checkbox/radio fields
                List<Serializable> entityList = new ArrayList<Serializable>(1);
                entityList.add(instance);
                Entity invalid = getRecords(mergedProperties, entityList, null, null, null)[0];
                invalid.setPropertyValidationErrors(entity.getPropertyValidationErrors());
                invalid.setGlobalValidationErrors(entity.getGlobalValidationErrors());
                invalid.overridePropertyValues(entity);

                String message = ValidationUtil.buildErrorMessage(invalid.getPropertyValidationErrors(), invalid.getGlobalValidationErrors());
                throw new ValidationException(invalid, message);
            } else if (entityPersistenceException != null) {
                throw ExceptionHelper.refineException(entityPersistenceException.getCause());
            } else {
                fieldManager.persistMiddleEntities();
            }
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        } catch (InstantiationException e) {
            throw new PersistenceException(e);
        } finally {
            session.setHibernateFlushMode(originalFlushMode);
        }
        return instance;
    }

    protected boolean attemptToPopulateValue(Property property, FieldManager fieldManager, Serializable instance,
                                             Boolean setId, BasicFieldMetadata metadata, Entity entity, String value) throws IllegalAccessException {
        Boolean mutable = metadata.getMutable();
        Boolean readOnly = metadata.getReadOnly();
        boolean generalConditionsMet = (mutable == null || mutable) && (readOnly == null || !readOnly) && property.getEnabled();

        if (generalConditionsMet && value == null) {
            boolean currentValueIsNotNull = false;
            try {
                currentValueIsNotNull = fieldManager.getFieldValue(instance, property.getName()) != null;
            } catch (FieldNotAvailableException e) {
                throw new IllegalArgumentException(e);
            }

            boolean valueIsNotNullId = metadata.getFieldType() != SupportedFieldType.ID || setId;
            boolean valueIsNotPassword = metadata.getFieldType() != SupportedFieldType.PASSWORD;

            return currentValueIsNotNull && !entity.isPreAdd() && valueIsNotNullId && valueIsNotPassword;
        }
        return generalConditionsMet;
    }

    @Override
    public Entity getRecord(Map<String, FieldMetadata> primaryMergedProperties, Serializable record, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) {
        List<Serializable> records = new ArrayList<Serializable>(1);
        records.add(record);
        Entity[] productEntities = getRecords(primaryMergedProperties, records, alternateMergedProperties, pathToTargetObject, null);
        return productEntities[0];
    }

    @Override
    public Entity getRecord(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, Serializable record) {
        Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective);
        return getRecord(mergedProperties, record, null, null);
    }

    @Override
    public Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<? extends Serializable> records) {
        Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective);
        return getRecords(mergedProperties, records, null, null, null);
    }

    @Override
    public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) {
        return persistenceManager.getDynamicEntityDao().getSimpleMergedProperties(entityName, persistencePerspective);
    }

    @Override
    public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records) {
        return getRecords(primaryMergedProperties, records, null, null, null);
    }

    @Override
    public Entity[] getRecords(Map<String, FieldMetadata> primaryUnfilteredMergedProperties,
                               List<? extends Serializable> records,
                               Map<String, FieldMetadata> alternateUnfilteredMergedProperties,
                               String pathToTargetObject) {
        return getRecords(primaryUnfilteredMergedProperties, records, alternateUnfilteredMergedProperties, pathToTargetObject, null);
    }

    @Override
    public Entity[] getRecords(Map<String, FieldMetadata> primaryUnfilteredMergedProperties,
                               List<? extends Serializable> records,
                               Map<String, FieldMetadata> alternateUnfilteredMergedProperties,
                               String pathToTargetObject,
                               String[] customCriteria) {
        Map<String, FieldMetadata> primaryMergedProperties = filterOutCollectionMetadata(primaryUnfilteredMergedProperties);
        Map<String, FieldMetadata> alternateMergedProperties = filterOutCollectionMetadata(alternateUnfilteredMergedProperties);
        Entity[] entities = new Entity[records.size()];
        int j = 0;
        for (Serializable recordEntity : records) {
            Serializable entity;
            if (pathToTargetObject != null) {
                try {
                    entity = (Serializable) getFieldManager().getFieldValue(recordEntity, pathToTargetObject);
                } catch (Exception e) {
                    throw new PersistenceException(e);
                }
            } else {
                entity = recordEntity;
            }
            Entity entityItem = new Entity();
            entityItem.setType(new String[] { entity.getClass().getName() });
            entities[j] = entityItem;

            List<Property> props = new ArrayList<Property>(primaryMergedProperties.size());
            extractPropertiesFromPersistentEntity(primaryMergedProperties, entity, props, customCriteria);
            if (alternateMergedProperties != null) {
                extractPropertiesFromPersistentEntity(alternateMergedProperties, recordEntity, props, customCriteria);
            }

            // Try to add the "main name" property. Log a debug message if we can't
            try {
                Property p = new Property();
                p.setName(MAIN_ENTITY_NAME_PROPERTY);
                String mainEntityName = (String) MethodUtils.invokeMethod(entity, "getMainEntityName");
                p.setValue(mainEntityName);
                props.add(p);
            } catch (Exception e) {
                LOG.debug(String.format("Could not execute the getMainEntityName() method for [%s]",
                        entity.getClass().getName()), e);
            }

            // Try to add the alternate id property if available
            if (alternateMergedProperties != null) {
                for (Entry<String, FieldMetadata> entry : alternateMergedProperties.entrySet()) {
                    if (entry.getValue() instanceof BasicFieldMetadata) {
                        if (((BasicFieldMetadata) entry.getValue()).getFieldType() == SupportedFieldType.ID) {
                            Map<String, FieldMetadata> alternateOnEntity = new HashMap<String, FieldMetadata>();
                            alternateOnEntity.put(entry.getKey(), entry.getValue());
                            List<Property> props2 = new ArrayList<Property>();
                            extractPropertiesFromPersistentEntity(alternateOnEntity, recordEntity, props2, customCriteria);
                            List<Property> filtered = new ArrayList<Property>();
                            for (Property prop : props2) {
                                if (!prop.getName().startsWith("__")) {
                                    filtered.add(prop);
                                }
                            }
                            if (filtered.size() == 1 && !filtered.get(0).getName().contains(".")) {
                                Property alternateIdProp = filtered.get(0);
                                alternateIdProp.setName(ALTERNATE_ID_PROPERTY);
                                props.add(alternateIdProp);
                            }
                        }
                    }
                }
            }

            Property[] properties = new Property[props.size()];
            properties = props.toArray(properties);
            entityItem.setProperties(properties);
            j++;
        }

        return entities;
    }

    @Override
    public Entity[] getRecords(FetchExtractionRequest fetchExtractionRequest) {
        return fetchWrapper.getRecords(fetchExtractionRequest);
    }

    protected void extractPropertiesFromPersistentEntity(Map<String, FieldMetadata> mergedProperties,
                                                         Serializable entity,
                                                         List<Property> props,
                                                         String[] customCriteria) {
        FieldManager fieldManager = getFieldManager();
        try {
            if (entity instanceof AdminMainEntity) {
                //Create an invisible property for the admin main entity name, if applicable.
                //This is useful for ToOneLookups if that ToOneLookup uses AdminMainEntity to drive
                //its display name.
                try {
                    Property propertyItem = new Property();
                    propertyItem.setName(AdminMainEntity.MAIN_ENTITY_NAME_PROPERTY);
                    propertyItem.setValue(((AdminMainEntity) entity).getMainEntityName());
                    props.add(propertyItem);
                } catch (Exception e) {
                    //do nothing here except for not add the property. Exceptions could occur when there is a validation
                    //issue and some properties/relationships that are used for gleaning the main entity name end up
                    //not being set
                }
            }
            for (Entry<String, FieldMetadata> entry : mergedProperties.entrySet()) {
                String property = entry.getKey();
                BasicFieldMetadata metadata = (BasicFieldMetadata) entry.getValue();
                if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(entity.getClass()) || entity.getClass().isAssignableFrom(Class.forName(metadata.getInheritedFromType()))) {
                    boolean proceed = true;
                    if (property.contains(".")) {
                        StringTokenizer tokens = new StringTokenizer(property, ".");
                        Object testObject = entity;
                        while (tokens.hasMoreTokens()) {
                            String token = tokens.nextToken();
                            if (tokens.hasMoreTokens()) {
                                try {
                                    testObject = fieldManager.getFieldValue(testObject, token);
                                } catch (FieldNotAvailableException e) {
                                    proceed = false;
                                    break;
                                }
                                if (testObject == null) {
                                    Property propertyItem = new Property();
                                    propertyItem.setName(property);
                                    if (props.contains(propertyItem)) {
                                        proceed = false;
                                        break;
                                    }
                                    propertyItem.setValue(null);
                                    props.add(propertyItem);
                                    proceed = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (!proceed) {
                        continue;
                    }

                    boolean isFieldAccessible = true;
                    Object value = null;
                    try {
                        value = fieldManager.getFieldValue(entity, property);
                    } catch (FieldNotAvailableException e) {
                        isFieldAccessible = false;
                    }
                    checkField:
                    {
                        if (isFieldAccessible) {
                            Property propertyItem = new Property();
                            propertyItem.setName(property);
                            if (props.contains(propertyItem)) {
                                continue;
                            }
                            props.add(propertyItem);
                            String displayVal = propertyItem.getDisplayValue();
                            boolean handled = false;
                            for (FieldPersistenceProvider fieldPersistenceProvider : fieldPersistenceProviders) {
                                MetadataProviderResponse response = fieldPersistenceProvider.extractValue(
                                        new ExtractValueRequest(props, fieldManager, metadata, value, displayVal,
                                                persistenceManager, this, entity, customCriteria), propertyItem);
                                if (MetadataProviderResponse.NOT_HANDLED != response) {
                                    handled = true;
                                }
                                if (MetadataProviderResponse.HANDLED_BREAK == response) {
                                    break;
                                }
                            }
                            if (!handled) {
                                defaultFieldPersistenceProvider.extractValue(
                                        new ExtractValueRequest(props, fieldManager, metadata, value, displayVal,
                                                persistenceManager, this, entity, customCriteria), propertyItem);
                            }
                            break checkField;
                        }
                        //try a direct property acquisition via reflection
                        try {
                            String strVal = null;
                            Method method;
                            try {
                                //try a 'get' prefixed mutator first
                                String temp = "get" + property.substring(0, 1).toUpperCase() + property.substring(1, property.length());
                                method = entity.getClass().getMethod(temp, new Class[] {});
                            } catch (NoSuchMethodException e) {
                                method = entity.getClass().getMethod(property, new Class[] {});
                            }
                            value = method.invoke(entity, new String[] {});
                            Property propertyItem = new Property();
                            propertyItem.setName(property);
                            if (props.contains(propertyItem)) {
                                continue;
                            }
                            props.add(propertyItem);
                            if (value == null) {
                                strVal = null;
                            } else {
                                if (Date.class.isAssignableFrom(value.getClass())) {
                                    strVal = getSimpleDateFormatter().format((Date) value);
                                } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
                                    strVal = getSimpleDateFormatter().format(new Date(((Timestamp) value).getTime()));
                                } else if (Calendar.class.isAssignableFrom(value.getClass())) {
                                    strVal = getSimpleDateFormatter().format(((Calendar) value).getTime());
                                } else if (Double.class.isAssignableFrom(value.getClass())) {
                                    strVal = getDecimalFormatter().format(value);
                                } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                                    strVal = getDecimalFormatter().format(value);
                                } else {
                                    strVal = value.toString();
                                }
                            }
                            propertyItem.setValue(strVal);
                        } catch (NoSuchMethodException e) {
                            LOG.debug("Unable to find a specified property in the entity: " + StringUtil.sanitize(property));
                            //do nothing - this property is simply not in the bean
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new PersistenceException(e);
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        } catch (InvocationTargetException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public String getStringValueFromGetter(Serializable instance, String propertyName)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = PropertyUtils.getProperty(instance, propertyName);
        return formatValue(value);
    }

    @Override
    public String formatValue(Object value) {
        String strVal;
        if (value == null) {
            strVal = null;
        } else {
            if (Date.class.isAssignableFrom(value.getClass())) {
                strVal = getSimpleDateFormatter().format((Date) value);
            } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
                strVal = getSimpleDateFormatter().format(new Date(((Timestamp) value).getTime()));
            } else if (Calendar.class.isAssignableFrom(value.getClass())) {
                strVal = getSimpleDateFormatter().format(((Calendar) value).getTime());
            } else if (Double.class.isAssignableFrom(value.getClass())) {
                strVal = getDecimalFormatter().format(value);
            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                strVal = getDecimalFormatter().format(value);
            } else {
                strVal = value.toString();
            }
        }
        return strVal;
    }

    protected EntityResult update(PersistencePackage persistencePackage, Object primaryKey, boolean includeRealEntity) throws ServiceException {
        EntityResult entityResult = new EntityResult();
        Entity entity = persistencePackage.getEntity();
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
        if (foreignKey != null && !foreignKey.getMutable()) {
            throw new SecurityServiceException("Entity not mutable");
        }
        try {
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    persistencePackage.getCeilingEntityFullyQualifiedClassname(),
                    entities,
                    foreignKey,
                    persistencePerspective.getAdditionalNonPersistentProperties(),
                    persistencePerspective.getAdditionalForeignKeys(),
                    MergedPropertyType.PRIMARY,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
                    );
            if (primaryKey == null) {
                primaryKey = getPrimaryKey(entity, mergedProperties);
            }
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), primaryKey);

            Assert.isTrue(instance != null, "Entity not found");

            if (!entity.isValidationFailure()) {
                //Re-Balance the list if it is a Foreign Key toMany collection with a sort field property
                if (foreignKey != null && foreignKey.getSortField() != null &&
                        entity.findProperty(foreignKey.getSortField()) != null &&
                        entity.findProperty(foreignKey.getSortField()).getValue() != null) {
                    ExtensionResultHolder<Serializable> result = new ExtensionResultHolder<Serializable>();
                    extensionManager.getProxy().rebalanceForUpdate(this, persistencePackage, instance,
                            mergedProperties, primaryKey, result);
                    instance = result.getResult();
                } else {
                    instance = createPopulatedInstance(instance, entity, mergedProperties, false, persistencePackage.isValidateUnsubmittedProperties());
                }

                instance = persistenceManager.getDynamicEntityDao().merge(instance);
                if (includeRealEntity) {
                    entityResult.setEntityBackingObject(instance);
                }

                List<Serializable> entityList = new ArrayList<Serializable>(1);
                entityList.add(instance);

                entity = getRecords(mergedProperties, entityList, null, null, null)[0];
                entityResult.setEntity(entity);
                return entityResult;
            } else {
                entityResult.setEntity(entity);
                return entityResult;
            }
        } catch (Exception e) {
            throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
        }
    }

    @Override
    public String getIdPropertyName(String entityClass) {
        return persistenceManager.getIdPropertyName(entityClass);
    }

    public String getIdPropertyName(Map<String, FieldMetadata> mergedUnfilteredProperties) {
        Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
        for (String property : mergedProperties.keySet()) {
            BasicFieldMetadata temp = (BasicFieldMetadata) mergedProperties.get(property);
            if (temp.getFieldType() == SupportedFieldType.ID && !property.contains(".")) {
                return property;
            }
        }

        throw new RuntimeException("Could not find a primary key property in the passed merged properties list");
    }

    @Override
    public Object getPrimaryKey(Entity entity, Map<String, FieldMetadata> mergedUnfilteredProperties) {
        Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
        Object primaryKey = null;
        String idPropertyName = null;
        BasicFieldMetadata metaData = null;
        for (String property : mergedProperties.keySet()) {
            BasicFieldMetadata temp = (BasicFieldMetadata) mergedProperties.get(property);
            if (temp.getFieldType() == SupportedFieldType.ID && !property.contains(".")) {
                idPropertyName = property;
                metaData = temp;
                break;
            }
        }
        if (idPropertyName == null) {
            throw new RuntimeException("Could not find a primary key property in the passed entity with type: " + entity.getType()[0]);
        }
        for (Property property : entity.getProperties()) {
            if (property.getName().equals(idPropertyName)) {
                switch (metaData.getSecondaryType()) {
                    case INTEGER:
                        primaryKey = (property.getValue() == null) ? null : Long.valueOf(property.getValue());
                        break;
                    case STRING:
                        primaryKey = property.getValue();
                        break;
                }
                break;
            }
        }
        if (primaryKey == null) {
            throw new RuntimeException("Could not find the primary key property (" + idPropertyName + ") in the passed entity with type: " + entity.getType()[0]);
        }
        return primaryKey;
    }

    @Override
    public List<FilterMapping> getFilterMappings(PersistencePerspective persistencePerspective,
            CriteriaTransferObject cto,
            String ceilingEntityFullyQualifiedClassname,
            Map<String, FieldMetadata> mergedUnfilteredProperties,
            RestrictionFactory customRestrictionFactory) {
        Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
        List<FilterMapping> filterMappings = new ArrayList<FilterMapping>();

        for (String propertyId : cto.getCriteriaMap().keySet()) {
            if (mergedProperties.containsKey(propertyId)) {
                boolean handled = false;
                for (FieldPersistenceProvider fieldPersistenceProvider : fieldPersistenceProviders) {
                    MetadataProviderResponse response = fieldPersistenceProvider.addSearchMapping(
                            new AddSearchMappingRequest(persistencePerspective, cto,
                                    ceilingEntityFullyQualifiedClassname, mergedProperties,
                                    propertyId, getFieldManager(), this, this, customRestrictionFactory==null?restrictionFactory
                                    :customRestrictionFactory), filterMappings);
                    if (MetadataProviderResponse.NOT_HANDLED != response) {
                        handled = true;
                    }
                    if (MetadataProviderResponse.HANDLED_BREAK == response) {
                        break;
                    }
                }
                if (!handled) {
                    defaultFieldPersistenceProvider.addSearchMapping(
                            new AddSearchMappingRequest(persistencePerspective, cto,
                                    ceilingEntityFullyQualifiedClassname, mergedProperties, propertyId,
                                    getFieldManager(), this, this, customRestrictionFactory == null ? restrictionFactory
                                            : customRestrictionFactory), filterMappings);
                }
            }
        }
        return filterMappings;
    }

    @Override
    public List<FilterMapping> getFilterMappings(PersistencePerspective persistencePerspective,
            CriteriaTransferObject cto,
            String ceilingEntityFullyQualifiedClassname,
            Map<String, FieldMetadata> mergedUnfilteredProperties) {
        return getFilterMappings(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedUnfilteredProperties, null);
    }

    @Override
    public void extractProperties(Class<?>[] inheritanceLine, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) {
        extractPropertiesFromMetadata(inheritanceLine, mergedProperties.get(MergedPropertyType.PRIMARY), properties, false, MergedPropertyType.PRIMARY);
    }

    protected void extractPropertiesFromMetadata(Class<?>[] inheritanceLine, Map<String, FieldMetadata> mergedProperties, List<Property> properties, Boolean isHiddenOverride, MergedPropertyType type) {
        Comparator<Property> comparator = new Comparator<Property>() {

            @Override
            public int compare(Property o1, Property o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Collections.sort(properties, comparator);
        for (Map.Entry<String, FieldMetadata> entry : mergedProperties.entrySet()) {
            String property = entry.getKey();
            Property prop = new Property();
            FieldMetadata metadata = mergedProperties.get(property);
            prop.setName(property);
 
            int pos = Collections.binarySearch(properties, prop, comparator);
            if (pos >= 0 && MergedPropertyType.MAPSTRUCTUREKEY != type && MergedPropertyType.MAPSTRUCTUREVALUE != type) {
                logWarn: {
                    if ((metadata instanceof BasicFieldMetadata) && SupportedFieldType.ID.equals(((BasicFieldMetadata) metadata).getFieldType())) {
                        //don't warn for id field collisions, but still ignore the colliding fields
                        break logWarn;
                    }
                    //LOG.warn("Detected a field name collision (" + metadata.getTargetClass() + "." + property + ") during inspection for the inheritance line starting with (" + inheritanceLine[0].getName() + "). Ignoring the additional field. This can occur most commonly when using the @AdminPresentationAdornedTargetCollection and the collection type and target class have field names in common. This situation should be avoided, as the system will strip the repeated fields, which can cause unpredictable behavior.");
                }
                continue;
            } else if (pos < 0) {
                pos = -pos - 1; // calculate position to insert
            }
            properties.add(pos, prop);
            prop.setMetadata(metadata);
            if (isHiddenOverride && prop.getMetadata() instanceof BasicFieldMetadata) {
                //this only makes sense for non collection types
                ((BasicFieldMetadata) prop.getMetadata()).setVisibility(VisibilityEnum.HIDDEN_ALL);
            }
        }
    }

    @Override
    public void updateMergedProperties(PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    ceilingEntityFullyQualifiedClassname,
                    entities,
                    (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY),
                    persistencePerspective.getAdditionalNonPersistentProperties(),
                    persistencePerspective.getAdditionalForeignKeys(),
                    MergedPropertyType.PRIMARY,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
                    );
            allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
        } catch (Exception e) {
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public EntityResult update(PersistencePackage persistencePackage, boolean includeRealEntityObject) throws ServiceException {
        return update(persistencePackage, null, true);
    }

    @Override
    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        EntityResult er = update(persistencePackage, null, false);
        return er.getEntity();
    }

    @Override
    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        EntityResult entityResult = add(persistencePackage, false);
        return entityResult.getEntity();
    }

    @Override
    public EntityResult add(PersistencePackage persistencePackage, boolean includeRealEntityObject) throws ServiceException {
        EntityResult entityResult = new EntityResult();
        Entity entity = persistencePackage.getEntity();
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
        if (foreignKey != null && !foreignKey.getMutable()) {
            throw new SecurityServiceException("Entity not mutable");
        }
        try {
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedUnfilteredProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    persistencePackage.getCeilingEntityFullyQualifiedClassname(),
                    entities,
                    foreignKey,
                    persistencePerspective.getAdditionalNonPersistentProperties(),
                    persistencePerspective.getAdditionalForeignKeys(),
                    MergedPropertyType.PRIMARY,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
                    );
            Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);

            String idProperty = null;
            for (String property : mergedProperties.keySet()) {
                if (((BasicFieldMetadata) mergedProperties.get(property)).getFieldType() == SupportedFieldType.ID) {
                    idProperty = property;
                    break;
                }
            }
            if (idProperty == null) {
                throw new RuntimeException("Could not find a primary key property in the passed entity with type: " + entity.getType()[0]);
            }
            Object primaryKey = null;
            try {
                primaryKey = getPrimaryKey(entity, mergedProperties);
            } catch (Exception e) {
                //don't do anything - this is a valid case
            }
            if (primaryKey == null) {

                Serializable instance = (Serializable) Class.forName(entity.getType()[0]).newInstance();

                instance = createPopulatedInstance(instance, entity, mergedProperties, false);

                if (foreignKey != null && foreignKey.getSortField() != null) {
                    ExtensionResultHolder<Serializable> result = new ExtensionResultHolder<Serializable>();
                    extensionManager.getProxy().rebalanceForAdd(this, persistencePackage, instance, mergedProperties, result);
                    instance = result.getResult();
                }

                instance = persistenceManager.getDynamicEntityDao().merge(instance);
                if (includeRealEntityObject) {
                    entityResult.setEntityBackingObject(instance);
                }
                List<Serializable> entityList = new ArrayList<Serializable>(1);
                entityList.add(instance);

                entity = getRecords(mergedProperties, entityList, null, null, null)[0];
                entityResult.setEntity(entity);
                return entityResult;
            } else {
                return update(persistencePackage, primaryKey, includeRealEntityObject);
            }
        } catch (Exception e) {
            throw new ServiceException("Problem adding new entity : " + e.getMessage(), e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
        if (foreignKey != null && !foreignKey.getMutable()) {
            throw new SecurityServiceException("Entity not mutable");
        }
        try {
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedUnfilteredProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    persistencePackage.getCeilingEntityFullyQualifiedClassname(),
                    entities,
                    foreignKey,
                    persistencePerspective.getAdditionalNonPersistentProperties(),
                    persistencePerspective.getAdditionalForeignKeys(),
                    MergedPropertyType.PRIMARY,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
                    );
            Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
            Object primaryKey = getPrimaryKey(entity, mergedProperties);
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), primaryKey);

            Assert.isTrue(instance != null, "Entity not found");

            switch (persistencePerspective.getOperationTypes().getRemoveType()) {
                case NONDESTRUCTIVEREMOVE:
                    FieldManager fieldManager = getFieldManager();
                    FieldMetadata manyToFieldMetadata = mergedUnfilteredProperties.get(foreignKey.getManyToField());
                    Object foreignKeyValue = entity.getPMap().get(foreignKey.getManyToField()).getValue();
                    try {
                        foreignKeyValue = Long.valueOf((String) foreignKeyValue);
                    } catch (NumberFormatException e) {
                        LOG.warn("Foreign primary key is not of type Long, assuming String for remove lookup");
                    }
                    Serializable foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(foreignKey.getForeignKeyClass()), foreignKeyValue);
                    Collection collection = (Collection) fieldManager.getFieldValue(foreignInstance, foreignKey.getOriginatingField());
                    collection.remove(instance);
                    // if this is a bi-directional @OneToMany/@ManyToOne and there is no @JoinTable (just a foreign key on
                    // the @ManyToOne side) then it will not be updated. In that instance, we have to explicitly
                    // set the manyTo field to null so that subsequent lookups will not find it
                    if (manyToFieldMetadata instanceof BasicFieldMetadata) {
                        if (BooleanUtils.isTrue(((BasicFieldMetadata) manyToFieldMetadata).getRequired())) {
                            throw new ServiceException("Could not remove from the collection as the ManyToOne side is a"
                                    + " non-optional relationship. Consider changing 'optional=true' in the @ManyToOne annotation"
                                    + " or nullable=true within the @JoinColumn annotation");
                        }
                        //Since this is occuring on a remove persistence package, merge up-front (before making a change) for proper operation in the presence of the enterprise module
                        instance = persistenceManager.getDynamicEntityDao().merge(instance);
                        Field manyToField = fieldManager.getField(instance.getClass(), foreignKey.getManyToField());
                        Object manyToObject = manyToField.get(instance);
                        if (manyToObject != null && !(manyToObject instanceof Collection) && !(manyToObject instanceof Map)) {
                            manyToField.set(instance, null);
                            instance = persistenceManager.getDynamicEntityDao().merge(instance);
                        }
                    }
                    break;
                case BASIC:
                    persistenceManager.getDynamicEntityDao().remove(instance);
                    break;
            }
        } catch (Exception e) {
            throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
        }
    }

    public Map<String, FieldMetadata> getMergedProperties(PersistencePackage persistencePackage,
            CriteriaTransferObject cto) throws ServiceException {
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();

        if (StringUtils.isEmpty(persistencePackage.getFetchTypeFullyQualifiedClassname())) {
            persistencePackage.setFetchTypeFullyQualifiedClassname(ceilingEntityFullyQualifiedClassname);
        }

        try {
            Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));

            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    ceilingEntityFullyQualifiedClassname,
                    entities,
                    (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY),
                    persistencePerspective.getAdditionalNonPersistentProperties(),
                    persistencePerspective.getAdditionalForeignKeys(),
                    MergedPropertyType.PRIMARY,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
                    );

            return mergedProperties;
        } catch (Exception e) {
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        Entity[] payload;
        int totalRecords;
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);

        try {
            if (foreignKey != null && foreignKey.getSortField() != null) {
                FilterAndSortCriteria sortCriteria = cto.get(foreignKey.getSortField());
                sortCriteria.setSortAscending(foreignKey.getSortAscending());
            }

            Map<String, FieldMetadata> mergedProperties = getMergedProperties(persistencePackage, cto);
            if (useTranslationSearch) {
                addTranslationSearchIfNeeded(cto, mergedProperties);
            }
            List<FilterMapping> filterMappings = getFilterMappings(persistencePerspective, cto, persistencePackage
                    .getFetchTypeFullyQualifiedClassname(), mergedProperties);
            List<FilterMapping> standardFilterMappings = new ArrayList<FilterMapping>(filterMappings);
            if (CollectionUtils.isNotEmpty(cto.getAdditionalFilterMappings())) {
                standardFilterMappings.addAll(cto.getAdditionalFilterMappings());
            }
            if (CollectionUtils.isNotEmpty(cto.getNonCountAdditionalFilterMappings())) {
                standardFilterMappings.addAll(cto.getNonCountAdditionalFilterMappings());
            }

            FetchRequest fetchRequest = new FetchRequest(persistencePackage, cto,
                    persistencePackage.getFetchTypeFullyQualifiedClassname(), standardFilterMappings);
            List<Serializable> records = getPersistentRecords(fetchRequest);

            List<FilterMapping> countFilterMappings = new ArrayList<FilterMapping>(filterMappings);
            if (CollectionUtils.isNotEmpty(cto.getAdditionalFilterMappings())) {
                countFilterMappings.addAll(cto.getAdditionalFilterMappings());
            }
            FetchRequest countFetchRequest = new FetchRequest(persistencePackage, cto,
                    persistencePackage.getFetchTypeFullyQualifiedClassname(), countFilterMappings);
            totalRecords = getTotalRecords(countFetchRequest);

            FetchExtractionRequest fetchExtractionRequest = new FetchExtractionRequest(persistencePackage, cto,
                    persistencePackage.getFetchTypeFullyQualifiedClassname(), mergedProperties, records);
            payload = getRecords(fetchExtractionRequest);
        } catch (Exception e) {
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }

        return new DynamicResultSet(null, payload, totalRecords);
    }

    private void addTranslationSearchIfNeeded(CriteriaTransferObject cto, Map<String, FieldMetadata> mergedProperties) {
        Map<String, FilterAndSortCriteria> criteriaMap = cto.getCriteriaMap();
        FilterAndSortCriteria fsc = criteriaMap.get("translationLocale");
        List<String> filterValues = new ArrayList<>();
        if (fsc != null) {
            filterValues = fsc.getFilterValues();
            criteriaMap.remove("translationLocale");
        }
        //If locale is generic - than we use all locales of this language
        //For example, if locale = "en" than we use also "en_GB" and "en_US"
        if (filterValues.size() > 0 && filterValues.get(0).indexOf("_") < 0) {
            String currentLocaleCode = filterValues.get(0);
            List<org.broadleafcommerce.common.locale.domain.Locale> locales = localeService.findAllLocales();
            for (org.broadleafcommerce.common.locale.domain.Locale locale : locales) {
                if (!locale.getLocaleCode().equals(currentLocaleCode) && currentLocaleCode.equals(locale.getLocaleCode().substring(0,2))) {
                    filterValues.add(locale.getLocaleCode());
                }
            }
        }
        Iterator<Entry<String, FilterAndSortCriteria>> iterator = criteriaMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, FilterAndSortCriteria> next = iterator.next();

            final String key = next.getKey();
            FieldMetadata fieldMetadata = mergedProperties.get(key);
            if (fieldMetadata != null && fieldMetadata instanceof BasicFieldMetadata && ((BasicFieldMetadata) fieldMetadata).getTranslatable()!=null && ((BasicFieldMetadata) fieldMetadata).getTranslatable()) {
                BasicFieldMetadata basicFieldMetadata = ((BasicFieldMetadata) fieldMetadata);
                if (next.getValue().getFilterValues().size() > 0) {
                    final String value = next.getValue().getFilterValues().get(0);
                    final String targetClass = basicFieldMetadata.getTargetClass();
                    final String fieldName = basicFieldMetadata.getFieldName();
                    final String friendlyType = getTranslationFriendlyType(targetClass);
                    final List<String> localeValues = filterValues;
                    if (friendlyType != null) {
                        iterator.remove();
                        cto.getAdditionalFilterMappings().add(new FilterMapping()
                                .withDirectFilterValues(new EmptyFilterValues())
                                .withRestriction(
                                        new Restriction().withPredicateProvider(new PredicateProvider() {
                                            @Override
                                            public Predicate buildPredicate(CriteriaBuilder builder,
                                                                            FieldPathBuilder fieldPathBuilder,
                                                                            From root,
                                                                            String ceilingEntity,
                                                                            String fullPropertyName,
                                                                            Path explicitPath,
                                                                            List directValues) {
                                                Subquery subquery =
                                                        fieldPathBuilder.getCriteria().subquery(Long.class);
                                                Root transRoot = subquery.from(TranslationImpl.class);
                                                subquery.select(builder.count(transRoot.get("id")));
                                                Predicate type =
                                                        builder.equal(transRoot.get("entityType"), friendlyType);
                                                Predicate name =
                                                        builder.equal(transRoot.get("fieldName"), fieldName);
                                                String[] split = key.split("\\.");
                                                Path x = null;
                                                for (int i = 0; i < split.length; i++) {
                                                    if (x == null) {
                                                        x = root.get(split[i]);
                                                    } else {
                                                        x = x.get(split[i]);
                                                    }
                                                }

                                                String likeValue = "%" + value + "%";
                                                Predicate transValue =
                                                        builder.like(transRoot.get("translatedValue"), likeValue);
                                                Predicate localeValue =
                                                        builder.isTrue(transRoot.get("localeCode").in(localeValues));
                                                Path y = split.length > 1 ? root.get(split[0]).get("id") : root.get("id");
                                                Predicate entityId = builder.equal(transRoot.get("entityId"),
                                                        y);
                                                if (localeValues.size() > 0) {
                                                    subquery.where(builder.and(type, entityId, transValue, name, localeValue));
                                                } else {
                                                    subquery.where(builder.and(type, entityId, transValue, name));
                                                }

                                                Predicate like = builder.like(x,
                                                        likeValue);
                                                Predicate predicate = builder.or(like, builder.greaterThan(subquery, 0));
                                                return predicate;
                                            }
                                        })));
                    }
                }
                System.out.println("");
            }
        }
    }

    private String getTranslationFriendlyType(String targetClass) {

        TranslatedEntity instance = TranslatedEntity.getInstance(targetClass);
        if (instance == null) {
            try {
                Class<?>[] interfaces = Class.forName(targetClass).getInterfaces();
                int i = 0;
                while (instance == null && i < interfaces.length) {
                    instance = TranslatedEntity.getInstance(interfaces[i].getName());
                    i++;
                }
            } catch (ClassNotFoundException e) {

            }
        }

        return instance == null ? null : instance.getFriendlyType();
    }

    @Override
    public Integer getTotalRecords(FetchRequest fetchRequest) {
        return fetchWrapper.getTotalRecords(fetchRequest);
    }

    @Override
    public Integer getTotalRecords(String ceilingEntity, List<FilterMapping> filterMappings) {
        try {
            return ((Long) criteriaTranslator.translateCountQuery(persistenceManager.getDynamicEntityDao(),
                    ceilingEntity, filterMappings).getSingleResult()).intValue();
        } catch (CriteriaConversionException e) {
            TypedQueryBuilder builder = getSpecialCaseQueryBuilder(e.getFieldPath(), filterMappings, ceilingEntity);
            return ((Long) builder.toCountQuery(getPersistenceManager().getDynamicEntityDao().getStandardEntityManager()).getSingleResult()).intValue();
        }
    }

    @Override
    public Serializable getMaxValue(String ceilingEntity, List<FilterMapping> filterMappings, String maxField) {
        return criteriaTranslator.translateMaxQuery(persistenceManager.getDynamicEntityDao(),
                ceilingEntity, filterMappings, maxField).getSingleResult();
    }

    @Override
    public List<Serializable> getPersistentRecords(FetchRequest fetchRequest) {
        return fetchWrapper.getPersistentRecords(fetchRequest);
    }

    @Override
    public List<Serializable> getPersistentRecords(String ceilingEntity, List<FilterMapping> filterMappings, Integer firstResult, Integer maxResults) {
        try {
            return criteriaTranslator.translateQuery(persistenceManager.getDynamicEntityDao(), ceilingEntity, filterMappings, firstResult, maxResults).getResultList();
        } catch (CriteriaConversionException e) {
            TypedQueryBuilder builder = getSpecialCaseQueryBuilder(e.getFieldPath(), filterMappings, ceilingEntity);
            return builder.toQuery(getPersistenceManager().getDynamicEntityDao().getStandardEntityManager()).getResultList();
        }
    }

    @Override
    public boolean validate(Entity entity, Serializable populatedInstance, Map<String, FieldMetadata> mergedProperties) {
        return validate(entity, populatedInstance, mergedProperties, true);
    }

    @Override
    public boolean validate(Entity entity, Serializable populatedInstance, Map<String, FieldMetadata> mergedProperties,
            boolean validateUnsubmittedProperties) {
        entityValidatorService.validate(entity, populatedInstance, mergedProperties, this, validateUnsubmittedProperties);
        return !entity.isValidationFailure();
    }

    @Override
    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    @Override
    public PersistenceModule getCompatibleModule(OperationType operationType) {
        return ((InspectHelper) persistenceManager).getCompatibleModule(operationType);
    }

    public FieldPersistenceProvider getDefaultFieldPersistenceProvider() {
        return defaultFieldPersistenceProvider;
    }

    public void setDefaultFieldPersistenceProvider(FieldPersistenceProvider defaultFieldPersistenceProvider) {
        this.defaultFieldPersistenceProvider = defaultFieldPersistenceProvider;
    }

    public List<FieldPersistenceProvider> getFieldPersistenceProviders() {
        return fieldPersistenceProviders;
    }

    public void setFieldPersistenceProviders(List<FieldPersistenceProvider> fieldPersistenceProviders) {
        this.fieldPersistenceProviders = fieldPersistenceProviders;
    }

    public CriteriaTranslator getCriteriaTranslator() {
        return criteriaTranslator;
    }

    public void setCriteriaTranslator(CriteriaTranslator criteriaTranslator) {
        this.criteriaTranslator = criteriaTranslator;
    }

    public EntityValidatorService getEntityValidatorService() {
        return entityValidatorService;
    }

    public void setEntityValidatorService(EntityValidatorService entityValidatorService) {
        this.entityValidatorService = entityValidatorService;
    }

    public RestrictionFactory getRestrictionFactory() {
        return restrictionFactory;
    }

    public void setRestrictionFactory(RestrictionFactory restrictionFactory) {
        this.restrictionFactory = restrictionFactory;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    /**
     * Use an alternate approach to generating a fetch query for a collection located inside of an @Embeddable object. Related
     * to https://hibernate.atlassian.net/browse/HHH-8802. The alternate approach leverages HQL rather than JPA criteria,
     * which seems to alleviate the problem.
     *
     * @param embeddedCollectionPath the path to the collection field itself
     * @param filterMappings all the fetch restrictions for this request
     * @param collectionClass the type of the collection members
     * @return the builder capable of generating an appropriate HQL query
     */
    protected TypedQueryBuilder getSpecialCaseQueryBuilder(FieldPath embeddedCollectionPath, List<FilterMapping> filterMappings, String collectionClass) {
        String specialPath = embeddedCollectionPath.getTargetProperty();
        String[] pieces = specialPath.split("\\.");
        if (pieces.length != 3) {
            throw new CriteriaConversionException(String.format("Expected to find a target property of format [embedded field].[collection field].[property] for the embedded collection path (%s)", specialPath), embeddedCollectionPath);
        }
        String expression = specialPath.substring(0, specialPath.lastIndexOf("."));
        TypedQueryBuilder builder;
        try {
            builder = new TypedQueryBuilder(Class.forName(collectionClass), "specialEntity")
                    .addJoin(new TQJoin("specialEntity." + expression, "embeddedCollection"));
        } catch (Exception e) {
            throw ExceptionHelper.refineException(e);
        }
        for (TQRestriction restriction : buildSpecialRestrictions(expression, filterMappings)) {
            builder = builder.addRestriction(restriction);
        }
        for (TQRestriction restriction : buildStandardRestrictions(embeddedCollectionPath, filterMappings)) {
            builder = builder.addRestriction(restriction);
        }
        for (FilterMapping mapping : filterMappings) {
            if (mapping.getSortDirection() != null) {
                String mappingProperty = mapping.getFieldPath() == null ? null : mapping.getFieldPath().getTargetProperty();
                if (StringUtils.isEmpty(mappingProperty)) {
                    mappingProperty = mapping.getFullPropertyName();
                }
                builder = builder.addOrder(new TQOrder("specialEntity." + mappingProperty, SortDirection.ASCENDING == mapping.getSortDirection()));
            }
        }

        return builder;
    }

    /**
     * Generate LIKE or EQUALS restrictions for any filter property specified on the root entity (not the collection field in the @Embeddable object)
     *
     * @see #getSpecialCaseQueryBuilder(org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath, java.util.List, String)
     * @param embeddedCollectionPath the path for the collection field in the @Embeddable object - this is what caused the whole thing
     * @param filterMappings all the fetch restrictions for this request
     * @return the list of restrictions on the root entity
     */
    protected List<TQRestriction> buildStandardRestrictions(FieldPath embeddedCollectionPath, List<FilterMapping> filterMappings) {
        String expression = embeddedCollectionPath.getTargetProperty().substring(0, embeddedCollectionPath.getTargetProperty().lastIndexOf("."));
        List<TQRestriction> restrictions = new ArrayList<TQRestriction>();
        for (FilterMapping mapping : filterMappings) {
            checkProperty: {
                String mappingProperty = mapping.getFieldPath() == null ? null : mapping.getFieldPath().getTargetProperty();
                if (StringUtils.isEmpty(mappingProperty)) {
                    mappingProperty = mapping.getFullPropertyName();
                }
                if (!embeddedCollectionPath.getTargetProperty().equals(mappingProperty) && !StringUtils.isEmpty(mappingProperty)) {
                    PredicateProvider predicateProvider = mapping.getRestriction().getPredicateProvider();
                    if (predicateProvider != null) {
                        FilterValueConverter converter = mapping.getRestriction().getFilterValueConverter();
                        if (converter != null && CollectionUtils.isNotEmpty(mapping.getFilterValues())) {
                            Object val = converter.convert(mapping.getFilterValues().get(0));
                            if (predicateProvider instanceof LikePredicateProvider) {
                                restrictions.add(new TQRestriction("specialEntity." + mappingProperty, "LIKE", val + "%"));
                                break checkProperty;
                            } else if (predicateProvider instanceof EqPredicateProvider) {
                                restrictions.add(new TQRestriction("specialEntity." + mappingProperty, "=", val));
                                break checkProperty;
                            }
                        }
                    }
                    LOG.warn(String.format("Unable to filter the embedded collection (%s) on an additional property (%s)",
                            StringUtil.sanitize(expression),
                            StringUtil.sanitize(mappingProperty)));
                }
            }
        }

        return restrictions;
    }

    /**
     * Generate EQUALS restrictions for any filter property specified on the entity member of the collection field in the @Embeddable object
     *
     * @see #getSpecialCaseQueryBuilder(org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath, java.util.List, String)
     * @param specialExpression the String representation of the path for the collection field in the @Embeddable object
     * @param filterMappings all the fetch restrictions for this request
     * @return the list of restrictions on the collection in the @Embeddable object
     */
    protected List<TQRestriction> buildSpecialRestrictions(String specialExpression, List<FilterMapping> filterMappings) {
        List<TQRestriction> restrictions = new ArrayList<TQRestriction>();
        for (FilterMapping mapping : filterMappings) {
            if (mapping.getFieldPath() != null && mapping.getFieldPath().getTargetProperty() != null && mapping.getFieldPath().getTargetProperty().startsWith(specialExpression)) {
                FilterValueConverter converter = mapping.getRestriction().getFilterValueConverter();
                if (converter != null && CollectionUtils.isNotEmpty(mapping.getFilterValues())) {
                    Object val = converter.convert(mapping.getFilterValues().get(0));
                    String property = mapping.getFieldPath().getTargetProperty().substring(mapping.getFieldPath().getTargetProperty().lastIndexOf(".") + 1, mapping.getFieldPath().getTargetProperty().length());
                    restrictions.add(new TQRestriction("embeddedCollection." + property, "=", val));
                }
            }
        }
        return restrictions;
    }

    protected void cleanupFailedPersistenceAttempt(Serializable instance) throws IllegalAccessException {
        //Remove the entity from ORM management - no further attempts to persist
        if (getPersistenceManager().getDynamicEntityDao().getStandardEntityManager().contains(instance)) {
            getPersistenceManager().getDynamicEntityDao().getStandardEntityManager().detach(instance);
        }
        //Remove the id field value, if it's set
        String idFieldName = (String) getPersistenceManager().getDynamicEntityDao().getIdMetadata(instance.getClass()).get("name");
        Field idField = FieldUtils.getField(instance.getClass(), idFieldName, true);
        if (idField == null) {
            throw ExceptionHelper.refineException(new NoSuchFieldException("Entity " + instance.getClass().getName() + " does not contain id field " + idFieldName));
        }
        idField.setAccessible(true);
        if (idField.get(instance) != null) {
            idField.set(instance, null);
        }
    }

    protected Class<?> getMapFieldType(Serializable instance, FieldManager fieldManager, Property property) {
        Class<?> returnType = null;
        Field field = fieldManager.getField(instance.getClass(), property.getName().substring(0, property.getName().indexOf(FieldManager.MAPFIELDSEPARATOR)));
        java.lang.reflect.Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> clazz;
            if (pType.getActualTypeArguments().length < 2) {
                clazz = (Class<?>) pType.getActualTypeArguments()[0];
            } else {
                clazz = (Class<?>) pType.getActualTypeArguments()[1];
            }
            Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(clazz);
            if (!ArrayUtils.isEmpty(entities)) {
                returnType = entities[entities.length - 1];
            }
        }
        return returnType;
    }
}
