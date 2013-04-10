/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.broadleafcommerce.common.rule.SimpleRule;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.cto.FilterCriterionProviders;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.EntityValidatorService;
import org.broadleafcommerce.openadmin.server.service.type.RuleIdentifier;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTODeserializer;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELToDataWrapperTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceFactory;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMException;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;
import com.anasoft.os.daofusion.criteria.FilterCriterion;
import com.anasoft.os.daofusion.criteria.NestedPropertyCriteria;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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

import javax.annotation.Resource;
import javax.persistence.Embedded;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

/**
 * @author jfischer
 */
@Component("blBasicPersistenceModule")
@Scope("prototype")
public class BasicPersistenceModule implements PersistenceModule, RecordHelper, ApplicationContextAware {
    
    private static final Log LOG = LogFactory.getLog(BasicPersistenceModule.class);
    
    public static final String MAIN_ENTITY_NAME_PROPERTY = "MAIN_ENTITY_NAME";

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
    protected DecimalFormat decimalFormat;
    protected ApplicationContext applicationContext;
    protected PersistenceManager persistenceManager;

    @Resource(name = "blEntityValidatorService")
    protected EntityValidatorService entityValidatorService;

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    public BasicPersistenceModule() {
        decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("0.########");
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
    
    protected Date parseDate(String value) throws ParseException {
        return dateFormat.parse(value);
    }
    
    @Override
    public DecimalFormat getDecimalFormatter()  {
        return decimalFormat;
    }

    protected Map<String, FieldMetadata> filterOutCollectionMetadata(Map<String, FieldMetadata> metadata) {
        if (metadata == null) {
            return null;
        }
        Map<String, FieldMetadata> newMap = new HashMap<String, FieldMetadata>();
        for (Map.Entry<String, FieldMetadata> entry : metadata.entrySet()) {
            if (entry.getValue() instanceof BasicFieldMetadata) {
                newMap.put(entry.getKey(), entry.getValue());
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

    protected void populateQuantityBaseRuleCollection(DataDTOToMVELTranslator translator, String entityKey,
                                                      String fieldService, String jsonPropertyValue,
                                                      Collection<QuantityBasedRule> criteriaList, Class<?> memberType) {
        if (!StringUtils.isEmpty(jsonPropertyValue)) {
            DataWrapper dw = convertJsonToDataWrapper(jsonPropertyValue);
            if (dw != null) {
                List<QuantityBasedRule> updatedRules = new ArrayList<QuantityBasedRule>();
                for (DataDTO dto : dw.getData()) {
                    if (dto.getId() != null) {
                        checkId: {
                            //updates are comprehensive, even data that was not changed
                            //is submitted here
                            //Update Existing Criteria
                            for (QuantityBasedRule quantityBasedRule : criteriaList) {
                                if (dto.getId().equals(quantityBasedRule.getId())){
                                    //don't update if the data has not changed
                                    if (!quantityBasedRule.getQuantity().equals(dto.getQuantity())) {
                                        quantityBasedRule.setQuantity(dto.getQuantity());
                                    }
                                    try {
                                        String mvel = translator.createMVEL(entityKey, dto,
                                                    ruleBuilderFieldServiceFactory.createInstance(fieldService));
                                        if (!quantityBasedRule.getMatchRule().equals(mvel)) {
                                            quantityBasedRule.setMatchRule(mvel);
                                        }
                                    } catch (MVELTranslationException e) {
                                        throw new RuntimeException(e);
                                    }
                                    updatedRules.add(quantityBasedRule);
                                    break checkId;
                                }
                            }
                            throw new IllegalArgumentException("Unable to update the rule of type (" + memberType.getName() +
                                    ") because an update was requested for id (" + dto.getId() + "), which does not exist.");
                        }
                    } else {
                        //Create a new Criteria
                        QuantityBasedRule quantityBasedRule;
                        try {
                            quantityBasedRule = (QuantityBasedRule) memberType.newInstance();
                            quantityBasedRule.setQuantity(dto.getQuantity());
                            quantityBasedRule.setMatchRule(translator.createMVEL(entityKey, dto,
                                    ruleBuilderFieldServiceFactory.createInstance(fieldService)));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        criteriaList.add(quantityBasedRule);
                        updatedRules.add(quantityBasedRule);
                    }
                }
                //if an item was not included in the comprehensive submit from the client, we can assume that the
                //listing was deleted, so we remove it here.
                Iterator<QuantityBasedRule> itr = criteriaList.iterator();
                while(itr.hasNext()) {
                    checkForRemove: {
                        QuantityBasedRule original = itr.next();
                        for (QuantityBasedRule quantityBasedRule : updatedRules) {
                            if (original.equals(quantityBasedRule)) {
                                break checkForRemove;
                            }
                        }
                        itr.remove();
                    }
                }
            }
        }
    }

    protected String convertMatchRuleJsonToMvel(DataDTOToMVELTranslator translator, String entityKey,
                                                  String fieldService, String jsonPropertyValue) {
        String mvel = null;
        if (jsonPropertyValue != null) {
            DataWrapper dw = convertJsonToDataWrapper(jsonPropertyValue);
            //there can only be one DataDTO for an appliesTo* rule
            if (dw != null && dw.getData().size() == 1) {
                DataDTO dto = dw.getData().get(0);
                try {
                    mvel = translator.createMVEL(entityKey, dto,
                            ruleBuilderFieldServiceFactory.createInstance(fieldService));
                } catch (MVELTranslationException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return mvel;
    }

    protected DataWrapper convertJsonToDataWrapper(String json) {
        ObjectMapper mapper = new ObjectMapper();
        DataDTODeserializer dtoDeserializer = new DataDTODeserializer();
        SimpleModule module = new SimpleModule("DataDTODeserializerModule", new Version(1, 0, 0, null));
        module.addDeserializer(DataDTO.class, dtoDeserializer);
        mapper.registerModule(module);
        if (json == null || "[]".equals(json)){
            return null;
        }

        try {
            return mapper.readValue(json, DataWrapper.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Serializable createPopulatedInstance(Serializable instance, Entity entity, Map<String, FieldMetadata> unfilteredProperties, Boolean setId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, NumberFormatException, InstantiationException, ClassNotFoundException {
        Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(unfilteredProperties);
        FieldManager fieldManager = getFieldManager();
        filterRuleBuilderProperties(entity, mergedProperties);

        for (Property property : entity.getProperties()) {
            BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(property.getName());
            Class<?> returnType;
            if (!property.getName().contains(FieldManager.MAPFIELDSEPARATOR)) {
                Field field = fieldManager.getField(instance.getClass(), property.getName());
                if (field == null) {
                    LOG.debug("Unable to find a bean property for the reported property: " + property.getName() + ". Ignoring property.");
                    continue;
                }
                returnType = field.getType();
            } else {
                if (metadata == null) {
                    LOG.debug("Unable to find a metadata property for the reported property: " + property.getName() + ". Ignoring property.");
                    continue;
                }
                returnType = getBasicBroadleafType(metadata.getFieldType());
                if (returnType == null) {
                    returnType = getMapFieldType(instance, fieldManager, property);
                }
            }
            if (returnType == null) {
                throw new IllegalAccessException("Unable to determine the value type for the property ("+property.getName()+")");
            }
            String value = property.getValue();
            if (metadata != null) {
                Boolean mutable = metadata.getMutable();
                Boolean readOnly = metadata.getReadOnly();

                if (metadata.getFieldType().equals(SupportedFieldType.BOOLEAN)) {
                    if (value == null) {
                        value = "false";
                    }
                }

                if ((mutable == null || mutable) && (readOnly == null || !readOnly)) {
                    if (value != null) {
                        switch (metadata.getFieldType()) {
                            case BOOLEAN:
                                boolean v = Boolean.valueOf(value);
                                try {
                                    fieldManager.setFieldValue(instance, property.getName(), v);
                                } catch (IllegalArgumentException e) {
                                    char c = v ? 'Y' : 'N';
                                    fieldManager.setFieldValue(instance, property.getName(), c);
                                }
                                break;
                            case DATE:
                                fieldManager.setFieldValue(instance, property.getName(), parseDate(value));
                                break;
                            case DECIMAL:
                                if (BigDecimal.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), new BigDecimal(new Double(value)));
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), new Double(value));
                                }
                                break;
                            case MONEY:
                                if (BigDecimal.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), new BigDecimal(new Double(value)));
                                } else if (Double.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), new Double(value));
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), new Money(new Double(value)));
                                }
                                break;
                            case INTEGER:
                                if (int.class.isAssignableFrom(returnType) || Integer.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Integer.valueOf(value));
                                } else if (byte.class.isAssignableFrom(returnType) || Byte.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Byte.valueOf(value));
                                } else if (short.class.isAssignableFrom(returnType) || Short.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Short.valueOf(value));
                                } else if (long.class.isAssignableFrom(returnType) || Long.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Long.valueOf(value));
                                }
                                break;
                            default:
                                fieldManager.setFieldValue(instance, property.getName(), value);
                                break;
                            case EMAIL:
                                fieldManager.setFieldValue(instance, property.getName(), value);
                                break;
                            case FOREIGN_KEY: {
                                Serializable foreignInstance;
                                if (StringUtils.isEmpty(value)) {
                                    foreignInstance = null;
                                } else {
                                    if (SupportedFieldType.INTEGER.toString().equals(metadata.getSecondaryType().toString())) {
                                        foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), Long.valueOf(value));
                                    } else {
                                        foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), value);
                                    }
                                }

                                if (Collection.class.isAssignableFrom(returnType)) {
                                    Collection collection;
                                    try {
                                        collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
                                    } catch (FieldNotAvailableException e) {
                                        throw new IllegalArgumentException(e);
                                    }
                                    if (!collection.contains(foreignInstance)) {
                                        collection.add(foreignInstance);
                                    }
                                } else if (Map.class.isAssignableFrom(returnType)) {
                                    throw new IllegalArgumentException("Map structures are not supported for foreign key fields.");
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), foreignInstance);
                                }
                                break;
                            }
                            case ADDITIONAL_FOREIGN_KEY: {
                                Serializable foreignInstance;
                                if (StringUtils.isEmpty(value)) {
                                    foreignInstance = null;
                                } else {
                                    if (SupportedFieldType.INTEGER.toString().equals(metadata.getSecondaryType().toString())) {
                                        foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), Long.valueOf(value));
                                    } else {
                                        foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), value);
                                    }
                                }

                                if (Collection.class.isAssignableFrom(returnType)) {
                                    Collection collection;
                                    try {
                                        collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
                                    } catch (FieldNotAvailableException e) {
                                        throw new IllegalArgumentException(e);
                                    }
                                    if (!collection.contains(foreignInstance)) {
                                        collection.add(foreignInstance);
                                    }
                                } else if (Map.class.isAssignableFrom(returnType)) {
                                    throw new IllegalArgumentException("Map structures are not supported for foreign key fields.");
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), foreignInstance);
                                }
                                break;
                            }
                            case ID:
                                if (setId) {
                                    switch (metadata.getSecondaryType()) {
                                        case INTEGER:
                                            fieldManager.setFieldValue(instance, property.getName(), Long.valueOf(value));
                                            break;
                                        case STRING:
                                            fieldManager.setFieldValue(instance, property.getName(), value);
                                            break;
                                    }
                                }
                                break;
                            case RULE_WITH_QUANTITY:{
                                //currently, this only works with list fields
                                Class<?> valueType = getListFieldType(instance, fieldManager, property);
                                if (valueType == null) {
                                    throw new IllegalAccessException("Unable to determine the valueType for the rule field (" + property.getName() + ")");
                                }
                                DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
                                Collection<QuantityBasedRule> rules;
                                try {
                                    rules = (Collection<QuantityBasedRule>) fieldManager.getFieldValue(instance, property.getName());
                                } catch (FieldNotAvailableException e) {
                                    throw new IllegalArgumentException(e);
                                }
                                populateQuantityBaseRuleCollection(translator, RuleIdentifier.ENTITY_KEY_MAP.get(metadata.getRuleIdentifier()),
                                        metadata.getRuleIdentifier(), value, rules, valueType);
                                break;
                            }
                            case RULE_SIMPLE:{
                                DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
                                String mvel = convertMatchRuleJsonToMvel(translator, RuleIdentifier.ENTITY_KEY_MAP.get(metadata.getRuleIdentifier()),
                                                    metadata.getRuleIdentifier(), value);
                                Class<?> valueType = null;
                                //is this a regular field?
                                if (!property.getName().contains(FieldManager.MAPFIELDSEPARATOR)) {
                                    valueType = returnType;
                                } else {
                                    String valueClassName = metadata.getMapFieldValueClass();
                                    if (valueClassName != null) {
                                        valueType = Class.forName(valueClassName);
                                    }
                                    if (valueType == null) {
                                        //since not explicitly specified, try to figure out the class type from generics
                                        valueType = getMapFieldType(instance, fieldManager, property);
                                    }
                                }
                                if (valueType == null) {
                                    throw new IllegalAccessException("Unable to determine the valueType for the rule field (" + property.getName() + ")");
                                }
                                //This is a simple String field (or String map field)
                                if (String.class.isAssignableFrom(valueType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), mvel);
                                }
                                if (SimpleRule.class.isAssignableFrom(valueType)) {
                                    //see if there's an existing rule
                                    SimpleRule rule;
                                    try {
                                        rule = (SimpleRule) fieldManager.getFieldValue(instance, property.getName());
                                    } catch (FieldNotAvailableException e) {
                                        throw new IllegalArgumentException(e);
                                    }
                                    if (rule != null) {
                                        rule.setMatchRule(mvel);
                                    } else {
                                        //create a new instance, persist and set
                                        rule = (SimpleRule) valueType.newInstance();
                                        rule.setMatchRule(mvel);
                                        persistenceManager.getDynamicEntityDao().persist(rule);
                                        fieldManager.setFieldValue(instance, property.getName(), rule);
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        try {
                            if (fieldManager.getFieldValue(instance, property.getName()) != null && (metadata.getFieldType() != SupportedFieldType.ID || setId)) {
                                fieldManager.setFieldValue(instance, property.getName(), null);
                            }
                        } catch (FieldNotAvailableException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                }
            }
        }
        Map<String, Serializable> persistedEntities = fieldManager.persistMiddleEntities();
        for (Entry<String, Serializable> entry : persistedEntities.entrySet()) {
            fieldManager.setFieldValue(instance, entry.getKey(), entry.getValue());
        }
        return instance;
    }

    protected Class<?> getListFieldType(Serializable instance, FieldManager fieldManager, Property property) {
        Class<?> returnType = null;
        Field field = fieldManager.getField(instance.getClass(), property.getName());
        java.lang.reflect.Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];
            Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(clazz);
            if (!ArrayUtils.isEmpty(entities)) {
                returnType = entities[entities.length-1];
            }
        }
        return returnType;
    }

    protected Class<?> getMapFieldType(Serializable instance, FieldManager fieldManager, Property property) {
        Class<?> returnType = null;
        Field field = fieldManager.getField(instance.getClass(), property.getName().substring(0, property.getName().indexOf(FieldManager.MAPFIELDSEPARATOR)));
        java.lang.reflect.Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[1];
            Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(clazz);
            if (!ArrayUtils.isEmpty(entities)) {
                returnType = entities[entities.length-1];
            }
        }
        return returnType;
    }

    protected void filterRuleBuilderProperties(Entity entity, Map<String, FieldMetadata> mergedProperties) {
        //This may contain rule Json fields - convert and filter out
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        List<Property> propertyList = new ArrayList<Property>();
        propertyList.addAll(Arrays.asList(entity.getProperties()));
        Iterator<Property> itr = propertyList.iterator();
        List<Property> additionalProperties = new ArrayList<Property>();
        while(itr.hasNext()) {
            Property prop = itr.next();
            if (prop.getName().endsWith("Json")) {
                for (Entry<String, FieldMetadata> entry : mergedProperties.entrySet()) {
                    if (prop.getName().startsWith(entry.getKey())) {
                        BasicFieldMetadata originalFM = (BasicFieldMetadata) entry.getValue();
                        if (originalFM.getFieldType() == SupportedFieldType.RULE_SIMPLE ||
                                originalFM.getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY) {
                            Property orginalProp = entity.findProperty(originalFM.getName());
                            if (orginalProp == null) {
                                orginalProp = new Property();
                                orginalProp.setName(originalFM.getName());
                                additionalProperties.add(orginalProp);
                            }
                            orginalProp.setValue(prop.getValue());
                            itr.remove();
                            break;
                        }
                    }
                }
            }
        }
        propertyList.addAll(additionalProperties);
        entity.setProperties(propertyList.toArray(new Property[propertyList.size()]));
    }

    @Override
    public Entity getRecord(Map<String, FieldMetadata> primaryMergedProperties, Serializable record, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
        List<Serializable> records = new ArrayList<Serializable>(1);
        records.add(record);
        Entity[] productEntities = getRecords(primaryMergedProperties, records, alternateMergedProperties, pathToTargetObject);
        return productEntities[0];
    }

    @Override
    public Entity getRecord(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, Serializable record) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, NoSuchFieldException {
        Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective);
        return getRecord(mergedProperties, record, null, null);
    }

    @Override
    public Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<? extends Serializable> records) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, NoSuchFieldException {
        Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective);
        return getRecords(mergedProperties, records, null, null);
    }

    @Override
    public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        return persistenceManager.getDynamicEntityDao().getSimpleMergedProperties(entityName, persistencePerspective);
    }

    @Override
    public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
        return getRecords(primaryMergedProperties, records, null, null);
    }

    @Override
    public Entity[] getRecords(Map<String, FieldMetadata> primaryUnfilteredMergedProperties, List<? extends Serializable> records, Map<String, FieldMetadata> alternateUnfilteredMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
        Map<String, FieldMetadata> primaryMergedProperties = filterOutCollectionMetadata(primaryUnfilteredMergedProperties);
        Map<String, FieldMetadata> alternateMergedProperties = filterOutCollectionMetadata(alternateUnfilteredMergedProperties);
        Entity[] entities = new Entity[records.size()];
        int j = 0;
        for (Serializable recordEntity : records) {
            Serializable entity;
            if (pathToTargetObject != null) {
                try {
                    entity = (Serializable) getFieldManager().getFieldValue(recordEntity, pathToTargetObject);
                } catch (FieldNotAvailableException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                entity = recordEntity;
            }
            Entity entityItem = new Entity();
            entityItem.setType(new String[]{entity.getClass().getName()});
            entities[j] = entityItem;

            List<Property> props = new ArrayList<Property>(primaryMergedProperties.size());
            extractPropertiesFromPersistentEntity(primaryMergedProperties, entity, props);
            if (alternateMergedProperties != null) {
                extractPropertiesFromPersistentEntity(alternateMergedProperties, recordEntity, props);
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
            
            Property[] properties = new Property[props.size()];
            properties = props.toArray(properties);
            entityItem.setProperties(properties);
            j++;
        }

        return entities;
    }

    protected void extractPropertiesFromPersistentEntity(Map<String, FieldMetadata> mergedProperties, Serializable entity, List<Property> props) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalArgumentException, ClassNotFoundException {
        FieldManager fieldManager = getFieldManager();
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();
        ObjectMapper mapper = new ObjectMapper();
        for (Map.Entry<String, FieldMetadata> entry : mergedProperties.entrySet()) {
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
                String strVal = null;
                checkField:
                {
                    if (isFieldAccessible) {
                        Property propertyItem = new Property();
                        propertyItem.setName(property);
                        if (props.contains(propertyItem)) {
                            continue;
                        }
                        props.add(propertyItem);
                        String displayVal = null;
                        if (metadata.getFieldType()==SupportedFieldType.RULE_SIMPLE) {
                            if (value != null) {
                                if (value instanceof String) {
                                    strVal = (String) value;
                                    propertyItem.setValue(strVal);
                                    propertyItem.setDisplayValue(displayVal);
                                }
                                if (value instanceof SimpleRule) {
                                    SimpleRule simpleRule = (SimpleRule) value;
                                    if (simpleRule != null) {
                                        strVal = simpleRule.getMatchRule();
                                        propertyItem.setValue(strVal);
                                        propertyItem.setDisplayValue(displayVal);
                                    }
                                }
                            }
                            Property jsonProperty = convertSimpleRuleToJson(translator, mapper, strVal,
                                    metadata.getName() + "Json", metadata.getRuleIdentifier());
                            props.add(jsonProperty);

                            break checkField;
                        }
                        if (metadata.getFieldType()==SupportedFieldType.RULE_WITH_QUANTITY) {
                            if (value != null) {
                                if (value instanceof Collection) {
                                    //these quantity rules are in a list - this is a special, valid case for quantity rules
                                    Property jsonProperty = convertQuantityBasedRuleToJson(translator, mapper, (Collection<QuantityBasedRule>) value,
                                            metadata.getName() + "Json", metadata.getRuleIdentifier());
                                    props.add(jsonProperty);
                                } else {
                                    //TODO support a single quantity based rule
                                    throw new UnsupportedOperationException("RULE_WITH_QUANTITY type is currently only supported" +
                                            "on collection fields. A single field with this type is not currently supported.");
                                }
                            }

                            break checkField;
                        }
                        if (value != null) {
                            if (metadata.getForeignKeyCollection()) {
                                ((BasicFieldMetadata) propertyItem.getMetadata()).setFieldType(metadata.getFieldType());
                                strVal = null;
                            } else if (metadata.getFieldType().equals(SupportedFieldType.BOOLEAN) && value instanceof Character) {
                                strVal = (value.equals('Y')) ? "true" : "false";
                            } else if (Date.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format((Date) value);
                            } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format(new Date(((Timestamp) value).getTime()));
                            } else if (Calendar.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format(((Calendar) value).getTime());
                            } else if (Double.class.isAssignableFrom(value.getClass())) {
                                strVal = decimalFormat.format(value);
                            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                                strVal = decimalFormat.format(((BigDecimal) value).doubleValue());
                            } else if (metadata.getForeignKeyClass() != null) {
                                try {
                                    strVal = fieldManager.getFieldValue(value, metadata.getForeignKeyProperty()).toString();
                                    //see if there's a name property and use it for the display value
                                    Object temp = null;
                                    try {
                                        temp = fieldManager.getFieldValue(value, metadata.getForeignKeyDisplayValueProperty());
                                    } catch (FieldNotAvailableException e) {
                                        //do nothing
                                    }
                                    if (temp != null) {
                                        displayVal = temp.toString();
                                    }
                                } catch (FieldNotAvailableException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            } else {
                                strVal = value.toString();
                            }
                            propertyItem.setValue(strVal);
                            propertyItem.setDisplayValue(displayVal);
                            break checkField;
                        }
                    }
                    //try a direct property acquisition via reflection
                    try {
                        Method method;
                        try {
                            //try a 'get' prefixed mutator first
                            String temp = "get" + property.substring(0, 1).toUpperCase() + property.substring(1, property.length());
                            method = entity.getClass().getMethod(temp, new Class[]{});
                        } catch (NoSuchMethodException e) {
                            method = entity.getClass().getMethod(property, new Class[]{});
                        }
                        value = method.invoke(entity, new String[]{});
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
                                strVal = dateFormat.format((Date) value);
                            } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format(new Date(((Timestamp) value).getTime()));
                            } else if (Calendar.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format(((Calendar) value).getTime());
                            } else if (Double.class.isAssignableFrom(value.getClass())) {
                                strVal = decimalFormat.format(value);
                            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                                strVal = decimalFormat.format(((BigDecimal) value).doubleValue());
                            } else {
                                strVal = value.toString();
                            }
                        }
                        propertyItem.setValue(strVal);
                    } catch (NoSuchMethodException e) {
                        LOG.debug("Unable to find a specified property in the entity: " + property);
                        //do nothing - this property is simply not in the bean
                    }
                }
            }
        }
    }

    protected Property convertQuantityBasedRuleToJson(MVELToDataWrapperTranslator translator, ObjectMapper mapper,
                    Collection<QuantityBasedRule> quantityBasedRules, String jsonProp, String fieldService) {

        int k=0;
        Entity[] targetItemCriterias = new Entity[quantityBasedRules.size()];
        for (QuantityBasedRule quantityBasedRule : quantityBasedRules) {
            Property[] properties = new Property[3];
            Property mvelProperty = new Property();
            mvelProperty.setName("matchRule");
            mvelProperty.setValue(quantityBasedRule.getMatchRule());
            Property quantityProperty = new Property();
            quantityProperty.setName("quantity");
            quantityProperty.setValue(quantityBasedRule.getQuantity().toString());
            Property idProperty = new Property();
            idProperty.setName("id");
            idProperty.setValue(quantityBasedRule.getId().toString());
            properties[0] = mvelProperty;
            properties[1] = quantityProperty;
            properties[2] = idProperty;
            Entity criteria = new Entity();
            criteria.setProperties(properties);
            targetItemCriterias[k] = criteria;
            k++;
        }

        String json;
        try {
            DataWrapper oiWrapper = translator.createRuleData(targetItemCriterias, "matchRule", "quantity", "id",
                    ruleBuilderFieldServiceFactory.createInstance(fieldService));
            json = mapper.writeValueAsString(oiWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Property p = new Property();
        p.setName(jsonProp);
        p.setValue(json);

        return p;
    }

    protected Property convertSimpleRuleToJson(MVELToDataWrapperTranslator translator, ObjectMapper mapper,
                                               String matchRule, String jsonProp, String fieldService) {
        Entity[] matchCriteria = new Entity[1];
        Property[] properties = new Property[1];
        Property mvelProperty = new Property();
        mvelProperty.setName("matchRule");
        mvelProperty.setValue(matchRule == null?"":matchRule);
        properties[0] = mvelProperty;
        Entity criteria = new Entity();
        criteria.setProperties(properties);
        matchCriteria[0] = criteria;

        String json;
        try {
            DataWrapper orderWrapper = translator.createRuleData(matchCriteria, "matchRule", null, null,
                    ruleBuilderFieldServiceFactory.createInstance(fieldService));
            json = mapper.writeValueAsString(orderWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Property p = new Property();
        p.setName(jsonProp);
        p.setValue(json);

        return p;
    }

    protected Entity update(PersistencePackage persistencePackage, Object primaryKey) throws ServiceException {
        try {
            Entity entity = persistencePackage.getEntity();
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                persistencePackage.getCeilingEntityFullyQualifiedClassname(),
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
            if (primaryKey == null) {
                primaryKey = getPrimaryKey(entity, mergedProperties);
            }
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), primaryKey);
            instance = createPopulatedInstance(instance, entity, mergedProperties, false);
            boolean validated = validate(entity, instance, mergedProperties);
            if (validated) {
                instance = persistenceManager.getDynamicEntityDao().merge(instance);

                List<Serializable> entityList = new ArrayList<Serializable>(1);
                entityList.add(instance);

                return getRecords(mergedProperties, entityList, null, null)[0];
            } else {
                return entity;
            }
        } catch (Exception e) {
            LOG.error("Problem editing entity", e);
            throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
        }
    }

    @Override
    public Object getPrimaryKey(Entity entity, Map<String, FieldMetadata> mergedUnfilteredProperties) throws RuntimeException {
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
                switch(metaData.getSecondaryType()) {
                case INTEGER:
                    primaryKey = Long.valueOf(property.getValue());
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
    public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedUnfilteredProperties) throws ClassNotFoundException {
        return getCtoConverter(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedUnfilteredProperties, null);
    }

    @Override
    public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedUnfilteredProperties, FilterCriterionProviders criterionProviders) throws ClassNotFoundException {
        Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
        BaseCtoConverter ctoConverter = (BaseCtoConverter) applicationContext.getBean("blBaseCtoConverter");
        if (criterionProviders != null) {
            ctoConverter.setFilterCriterionProviders(criterionProviders);
        }
        for (String propertyId : cto.getPropertyIdSet()) {
            if (mergedProperties.containsKey(propertyId)) {
                buildProperty(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedProperties, ctoConverter, propertyId);
            } else {
                ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, propertyId);
            }
        }
        if (cto.getPropertyIdSet().isEmpty()) {
            ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, "");
        }
        return ctoConverter;
    }

    protected void buildProperty(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties, BaseCtoConverter ctoConverter, String propertyName) throws ClassNotFoundException {
        AssociationPath associationPath;
        int dotIndex = propertyName.lastIndexOf('.');
        StringBuilder property;
        Class clazz = Class.forName(mergedProperties.get(propertyName).getInheritedFromType());
        Field field = getFieldManager().getField(clazz, propertyName);
        Class<?> targetType = null;
        if (field != null) {
            targetType = field.getType();
        }
        if (dotIndex >= 0) {
            property = new StringBuilder(propertyName.substring(dotIndex + 1, propertyName.length()));
            String prefix = propertyName.substring(0, dotIndex);
            StringTokenizer tokens = new StringTokenizer(prefix, ".");
            List<AssociationPathElement> elementList = new ArrayList<AssociationPathElement>(20);
            StringBuilder sb = new StringBuilder(150);
            StringBuilder pathBuilder = new StringBuilder(150);
            while (tokens.hasMoreElements()) {
                String token = tokens.nextToken();
                sb.append(token);
                pathBuilder.append(token);
                field = getFieldManager().getField(clazz, pathBuilder.toString());
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
            property = new StringBuilder(propertyName);
            associationPath = AssociationPath.ROOT;
        }
        String convertedProperty = property.toString();
        BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(propertyName);
        switch (metadata.getFieldType()) {
            case BOOLEAN:
                if (targetType == null || targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
                    ctoConverter.addBooleanMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                } else {
                    ctoConverter.addCharacterMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                }
                break;
            case DATE:
                ctoConverter.addDateMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case DECIMAL:
                ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case MONEY:
                ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case INTEGER:
                ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            default:
                ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case EMAIL:
                ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case FOREIGN_KEY:
                if (cto.get(propertyName).getFilterValues().length > 0) {
                    ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
                    if (metadata.getForeignKeyCollection()) {
                        if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
                            ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
                        } else {
                            AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                            ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                        }
                    } else if (cto.get(propertyName).getFilterValues()[0] == null || "null".equals(cto.get(propertyName).getFilterValues()[0])) {
                        ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, propertyName);
                    } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addStringEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    } else {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    }
                } else {
                    ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, propertyName);
                }
                break;
            case ADDITIONAL_FOREIGN_KEY:
                if (cto.get(propertyName).getFilterValues().length > 0) {
                    int additionalForeignKeyIndexPosition = Arrays.binarySearch(persistencePerspective.getAdditionalForeignKeys(), new ForeignKey(propertyName, null, null), new Comparator<ForeignKey>() {

                        @Override
                        public int compare(ForeignKey o1, ForeignKey o2) {
                            return o1.getManyToField().compareTo(o2.getManyToField());
                        }
                    });
                    ForeignKey foreignKey = null;
                    if (additionalForeignKeyIndexPosition >= 0) {
                        foreignKey = persistencePerspective.getAdditionalForeignKeys()[additionalForeignKeyIndexPosition];
                    }
                    //in the case of a to-one lookup, an explicit ForeignKey is not passed in. The system should then default
                    //to just using a ForeignKeyRestrictionType.ID_EQ
                    if (metadata.getForeignKeyCollection()) {
                        if (foreignKey != null &&
                                ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
                            ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
                        } else {
                            AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                            ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                        }
                    } else if (cto.get(propertyName).getFilterValues()[0] == null || "null".equals(cto.get(propertyName).getFilterValues()[0])) {
                        ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, propertyName);
                    } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addStringEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    } else {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    }
                } else {
                    ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, propertyName);
                }
                break;
            case ID:
                switch (metadata.getSecondaryType()) {
                    case INTEGER:
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                        break;
                    case STRING:
                        ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                        break;
                }
                break;
        }
    }

    @Override
    public int getTotalRecords(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException {
        PersistentEntityCriteria countCriteria = getCountCriteria(persistencePackage, cto, ctoConverter);
        return persistenceManager.getDynamicEntityDao().count(countCriteria, Class.forName(StringUtils.isEmpty(persistencePackage.getFetchTypeFullyQualifiedClassname()) ? persistencePackage.getCeilingEntityFullyQualifiedClassname() : persistencePackage.getFetchTypeFullyQualifiedClassname()));
    }

    @Override
    public PersistentEntityCriteria getCountCriteria(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException {
        PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), persistencePackage.getCeilingEntityFullyQualifiedClassname());
        Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(Class.forName(persistencePackage.getCeilingEntityFullyQualifiedClassname()));
        boolean isArchivable = false;
        for (Class<?> entity : entities) {
            if (Status.class.isAssignableFrom(entity)) {
                isArchivable = true;
                break;
            }
        }
        if (isArchivable && !persistencePackage.getPersistencePerspective().getShowArchivedFields()) {
            SimpleFilterCriterionProvider criterionProvider = new  SimpleFilterCriterionProvider(SimpleFilterCriterionProvider.FilterDataStrategy.NONE, 0) {
                @Override
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return Restrictions.or(Restrictions.eq(targetPropertyName, 'N'), Restrictions.isNull(targetPropertyName));
                }
            };
            FilterCriterion filterCriterion = new FilterCriterion(AssociationPath.ROOT, "archiveStatus.archived", criterionProvider);
            ((NestedPropertyCriteria) countCriteria).add(filterCriterion);
        }
        return countCriteria;
    }

    @Override
    public void extractProperties(Class<?>[] inheritanceLine, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
        extractPropertiesFromMetadata(inheritanceLine, mergedProperties.get(MergedPropertyType.PRIMARY), properties, false, MergedPropertyType.PRIMARY);
    }

    protected void extractPropertiesFromMetadata(Class<?>[] inheritanceLine, Map<String, FieldMetadata> mergedProperties, List<Property> properties, Boolean isHiddenOverride, MergedPropertyType type) throws NumberFormatException {
        for (Map.Entry<String, FieldMetadata> entry : mergedProperties.entrySet()) {
            String property = entry.getKey();
            Property prop = new Property();
            FieldMetadata metadata = mergedProperties.get(property);
            prop.setName(property);
            Comparator<Property> comparator = new Comparator<Property>() {
                @Override
                public int compare(Property o1, Property o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            Collections.sort(properties, comparator);
            int pos = Collections.binarySearch(properties, prop, comparator);
            if (pos >= 0 && MergedPropertyType.MAPSTRUCTUREKEY != type && MergedPropertyType.MAPSTRUCTUREVALUE != type) {
                logWarn: {
                    if ((metadata instanceof BasicFieldMetadata) && SupportedFieldType.ID.equals(((BasicFieldMetadata) metadata).getFieldType())) {
                        //don't warn for id field collisions, but still ignore the colliding fields
                        break logWarn;
                    }
                    LOG.warn("Detected a field name collision (" + metadata.getTargetClass() + "." + property + ") during inspection for the inheritance line starting with (" + inheritanceLine[0].getName() + "). Ignoring the additional field. This can occur most commonly when using the @AdminPresentationAdornedTargetCollection and the collection type and target class have field names in common. This situation should be avoided, as the system will strip the repeated fields, which can cause unpredictable behavior.");
                }
                continue;
            }
            properties.add(prop);
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
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        return update(persistencePackage, null);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        try {
            Entity entity = persistencePackage.getEntity();
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedUnfilteredProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                persistencePackage.getCeilingEntityFullyQualifiedClassname(),
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
                //do nothing
            }
            if (primaryKey == null) {
                Serializable instance = (Serializable) Class.forName(entity.getType()[0]).newInstance();
                instance = createPopulatedInstance(instance, entity, mergedProperties, false);

                boolean validated = validate(entity, instance, mergedProperties);
                if (validated) {
                    instance = persistenceManager.getDynamicEntityDao().merge(instance);
                    List<Serializable> entityList = new ArrayList<Serializable>(1);
                    entityList.add(instance);

                    return getRecords(mergedProperties, entityList, null, null)[0];
                } else {
                    //return immediately to notify up the stack
                    return entity;
                }
            } else {
                return update(persistencePackage, primaryKey);
            }
        } catch (ServiceException e) {
            LOG.error("Problem adding new entity", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Problem adding new entity", e);
            throw new ServiceException("Problem adding new entity : " + e.getMessage(), e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        try {
            Entity entity = persistencePackage.getEntity();
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedUnfilteredProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                persistencePackage.getCeilingEntityFullyQualifiedClassname(),
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
            Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
            Object primaryKey = getPrimaryKey(entity, mergedProperties);
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), primaryKey);

            switch (persistencePerspective.getOperationTypes().getRemoveType()) {
                case NONDESTRUCTIVEREMOVE:
                    for (Property property : entity.getProperties()) {
                        String originalPropertyName = property.getName();
                        FieldManager fieldManager = getFieldManager();
                        if (fieldManager.getField(instance.getClass(), property.getName()) == null) {
                            LOG.debug("Unable to find a bean property for the reported property: " + originalPropertyName + ". Ignoring property.");
                            continue;
                        }
                        if (SupportedFieldType.FOREIGN_KEY == ((BasicFieldMetadata) mergedProperties.get(originalPropertyName)).getFieldType()) {
                            String value = property.getValue();
                            ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
                            Serializable foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(foreignKey.getForeignKeyClass()), Long.valueOf(value));
                            Collection collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
                            collection.remove(foreignInstance);
                            break;
                        }
                    }
                    break;
                case BASIC:
                    persistenceManager.getDynamicEntityDao().remove(instance);
                    break;
            }
        } catch (Exception e) {
            LOG.error("Problem removing entity", e);
            throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        Entity[] payload;
        int totalRecords;
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        if (StringUtils.isEmpty(persistencePackage.getFetchTypeFullyQualifiedClassname())) {
            persistencePackage.setFetchTypeFullyQualifiedClassname(ceilingEntityFullyQualifiedClassname);
        }
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
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
            BaseCtoConverter ctoConverter = getCtoConverter(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
            boolean isArchivable = false;
            for (Class<?> entity : entities) {
                if (Status.class.isAssignableFrom(entity)) {
                    isArchivable = true;
                    break;
                }
            }
            if (isArchivable && !persistencePerspective.getShowArchivedFields()) {
                SimpleFilterCriterionProvider criterionProvider = new  SimpleFilterCriterionProvider(SimpleFilterCriterionProvider.FilterDataStrategy.NONE, 0) {
                    @Override
                    public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                        return Restrictions.or(Restrictions.eq(targetPropertyName, 'N'), Restrictions.isNull(targetPropertyName));
                    }
                };
                FilterCriterion filterCriterion = new FilterCriterion(AssociationPath.ROOT, "archiveStatus.archived", criterionProvider);
                ((NestedPropertyCriteria) queryCriteria).add(filterCriterion);
            }
            List<Serializable> records = persistenceManager.getDynamicEntityDao().query(queryCriteria, Class.forName(persistencePackage.getFetchTypeFullyQualifiedClassname()));

            payload = getRecords(mergedProperties, records, null, null);
            totalRecords = getTotalRecords(persistencePackage, cto, ctoConverter);
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }

        return new DynamicResultSet(null, payload, totalRecords);
    }

    @Override
    public boolean validate(Entity entity, Serializable populatedInstance, Map<String, FieldMetadata> mergedProperties)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        entityValidatorService.validate(entity, populatedInstance, mergedProperties);
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
}
