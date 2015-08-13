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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.broadleafcommerce.common.rule.SimpleRule;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.ParentEntityPersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.extension.RuleFieldPersistenceProviderExtensionManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELToDataWrapperTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;

/**
 * Provides persistence (read/write) behavior for rule builder fields. This includes two types: Rule with quantity, and
 * simple rule. OfferImpl#targetItemCriteria and OfferImpl#offerMatchRules are examples of each, respectively. This class
 * is only compatible with quantity-based rules modeled using a Set and @OneToMany, and with simple rules modeled using
 * a Map and @OneToMany.
 *
 * @author Jeff Fischer
 */
@Component("blRuleFieldPersistenceProvider")
@Scope("prototype")
public class RuleFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        return populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY ||
            populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITHOUT_QUESTION ||
            populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE;
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY ||
            extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITHOUT_QUESTION ||
            extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE;
    }

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Resource(name = "blRuleFieldExtractionUtility")
    protected RuleFieldExtractionUtility ruleFieldExtractionUtility;
    
    @Resource(name = "blRuleFieldPersistenceProviderExtensionManager")
    protected RuleFieldPersistenceProviderExtensionManager extensionManager;

    @Override
    public MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) throws PersistenceException {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        boolean dirty = false;
        try {
            setNonDisplayableValues(populateValueRequest);
            switch (populateValueRequest.getMetadata().getFieldType()) {
                case RULE_WITH_QUANTITY:{
                    dirty = populateQuantityRule(populateValueRequest, instance);
                    break;
                }
                case RULE_WITHOUT_QUESTION:{
                    dirty = populateSimpleRule(populateValueRequest, instance);
                    break;
                }
                case RULE_SIMPLE:{
                    dirty = populateSimpleRule(populateValueRequest, instance);
                    break;
                }
            }
        } catch (Exception e) {
            throw ExceptionHelper.refineException(PersistenceException.class, PersistenceException.class, e);
        }
        populateValueRequest.getProperty().setIsDirty(dirty);

        return MetadataProviderResponse.HANDLED_BREAK;
    }

    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        ObjectMapper mapper = new ObjectMapper();
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();
        if (extractValueRequest.getMetadata().getFieldType()== SupportedFieldType.RULE_SIMPLE ||
            extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITHOUT_QUESTION) {
            extractSimpleRule(extractValueRequest, property, mapper, translator);
        }
        if (extractValueRequest.getMetadata().getFieldType()==SupportedFieldType.RULE_WITH_QUANTITY) {
            extractQuantityRule(extractValueRequest, mapper, translator);
        }
        return MetadataProviderResponse.HANDLED_BREAK;
    }

    @Override
    public MetadataProviderResponse filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest, Map<String, FieldMetadata> properties) {
        //This may contain rule Json fields - convert and filter out
        List<Property> propertyList = new ArrayList<Property>();
        propertyList.addAll(Arrays.asList(addFilterPropertiesRequest.getEntity().getProperties()));
        Iterator<Property> itr = propertyList.iterator();
        List<Property> additionalProperties = new ArrayList<Property>();
        while(itr.hasNext()) {
            Property prop = itr.next();
            if (prop.getName().endsWith("Json")) {
                for (Map.Entry<String, FieldMetadata> entry : properties.entrySet()) {
                    if (prop.getName().startsWith(entry.getKey())) {
                        BasicFieldMetadata originalFM = (BasicFieldMetadata) entry.getValue();
                        if (originalFM.getFieldType() == SupportedFieldType.RULE_SIMPLE ||
                                originalFM.getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY ||
                                originalFM.getFieldType() == SupportedFieldType.RULE_WITHOUT_QUESTION) {
                            Property originalProp = addFilterPropertiesRequest.getEntity().findProperty(entry.getKey());
                            if (originalProp == null) {
                                originalProp = new Property();
                                originalProp.setName(entry.getKey());
                                additionalProperties.add(originalProp);
                            }
                            originalProp.setValue(prop.getValue());
                            originalProp.setRawValue(prop.getRawValue());
                            originalProp.setUnHtmlEncodedValue(prop.getUnHtmlEncodedValue());
                            itr.remove();
                            break;
                        }
                    }
                }
            }
        }
        propertyList.addAll(additionalProperties);
        addFilterPropertiesRequest.getEntity().setProperties(propertyList.toArray(new Property[propertyList.size()]));
        return MetadataProviderResponse.HANDLED;
    }

    protected void extractSimpleRule(ExtractValueRequest extractValueRequest, Property property, ObjectMapper mapper, MVELToDataWrapperTranslator translator) {
        String val = null;
        if (extractValueRequest.getRequestedValue() != null) {
            if (extractValueRequest.getRequestedValue() instanceof String) {
                val = (String) extractValueRequest.getRequestedValue();
                property.setValue(val);
                property.setDisplayValue(extractValueRequest.getDisplayVal());
            } else {
                Object simpleRule = extractValueRequest.getRequestedValue();
                if (simpleRule != null) {
                    if (simpleRule instanceof SimpleRule) {
                        val = ((SimpleRule) simpleRule).getMatchRule();
                        property.setValue(val);
                        property.setDisplayValue(extractValueRequest.getDisplayVal());
                    } else {
                        throw new UnsupportedOperationException("RULE_SIMPLE type is currently only supported on " +
                                "fields of type SimpleRule");
                    }
                }
            }
        }
        Property jsonProperty = ruleFieldExtractionUtility.convertSimpleRuleToJson(translator, mapper, val,
                property.getName() + "Json", extractValueRequest.getMetadata().getRuleIdentifier());
        extractValueRequest.getProps().add(jsonProperty);
    }

    protected void extractQuantityRule(ExtractValueRequest extractValueRequest, ObjectMapper mapper, MVELToDataWrapperTranslator translator) {
        if (extractValueRequest.getRequestedValue() != null) {
            if (extractValueRequest.getRequestedValue() instanceof Collection) {
                //these quantity rules are in a list - this is a special, valid case for quantity rules
                Property jsonProperty = convertQuantityBasedRuleToJson(translator,
                        mapper, (Collection<QuantityBasedRule>) extractValueRequest
                        .getRequestedValue(),
                        extractValueRequest.getMetadata().getName() + "Json", extractValueRequest.getMetadata()
                        .getRuleIdentifier());
                extractValueRequest.getProps().add(jsonProperty);
            } else {
                //TODO support a single quantity based rule
                throw new UnsupportedOperationException("RULE_WITH_QUANTITY type is currently only supported" +
                        "on collection fields. A single field with this type is not currently supported.");
            }
        }
    }

    protected boolean populateSimpleRule(PopulateValueRequest populateValueRequest, Serializable instance) throws Exception {
        boolean dirty = false;
        String prop = populateValueRequest.getProperty().getName();
        if (prop.contains(FieldManager.MAPFIELDSEPARATOR)) {
            Field field = populateValueRequest.getFieldManager().getField(instance.getClass(), prop.substring(0, prop.indexOf(FieldManager.MAPFIELDSEPARATOR)));
            if (field.getAnnotation(OneToMany.class) == null) {
                throw new UnsupportedOperationException("RuleFieldPersistenceProvider is currently only compatible with map fields when modelled using @OneToMany");
            }
        }
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        //AntiSamy HTML encodes the rule JSON - pass the unHTMLEncoded version
        DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(populateValueRequest.getProperty().getUnHtmlEncodedValue());
        if (dw == null || StringUtils.isEmpty(dw.getError())) {
            String mvel = ruleFieldExtractionUtility.convertSimpleMatchRuleJsonToMvel(translator, RuleIdentifier.ENTITY_KEY_MAP.get(populateValueRequest.getMetadata().getRuleIdentifier()),
                    populateValueRequest.getMetadata().getRuleIdentifier(), dw);
            Class<?> valueType = getStartingValueType(populateValueRequest);
            //This is a simple String field (or String map field)
            if (String.class.isAssignableFrom(valueType)) {
                //first check if the property is null and the mvel is null
                if (instance != null && mvel == null) {
                    Object value = populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName());
                    dirty = value != null;
                } else {
                    dirty = checkDirtyState(populateValueRequest, instance, mvel);
                }
                populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), mvel);
            }
            if (SimpleRule.class.isAssignableFrom(valueType)) {
                boolean persist = false;
                SimpleRule rule;
                try {
                    rule = (SimpleRule) populateValueRequest.getFieldManager().getFieldValue(instance,
                            populateValueRequest.getProperty().getName());
                    if (rule == null) {
                        rule = (SimpleRule) valueType.newInstance();
                        Field field = populateValueRequest.getFieldManager().getField(instance.getClass(),
                                prop.substring(0, prop.indexOf(FieldManager.MAPFIELDSEPARATOR)));
                        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                        Object parent = extractParent(populateValueRequest, instance);
                        populateValueRequest.getFieldManager().setFieldValue(rule, oneToMany.mappedBy(), parent);
                        populateValueRequest.getFieldManager().setFieldValue(rule, populateValueRequest.getMetadata().
                                getMapKeyValueProperty(), prop.substring(prop.indexOf(
                                FieldManager.MAPFIELDSEPARATOR) + FieldManager.MAPFIELDSEPARATOR.length(),
                                prop.length()));

                        persist = true;
                    }
                } catch (FieldNotAvailableException e) {
                    throw new IllegalArgumentException(e);
                }
                if (mvel == null) {
                    //cause the rule to be deleted
                    dirty = populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName()) != null;
                    if (dirty) {
                        if (!populateValueRequest.getProperty().getName().contains(FieldManager.MAPFIELDSEPARATOR)) {
                            populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), null);
                        } else {
                            populateValueRequest.getPersistenceManager().getDynamicEntityDao().remove(rule);
                        }
                    }
                } else if (rule != null) {
                    dirty = !mvel.equals(rule.getMatchRule());
                    if (!dirty && extensionManager != null) {
                        ExtensionResultHolder<Boolean> resultHolder = new ExtensionResultHolder<Boolean>();
                        ExtensionResultStatusType result = extensionManager.getProxy().establishDirtyState(rule,
                                resultHolder);
                        if (ExtensionResultStatusType.NOT_HANDLED != result && resultHolder.getResult() != null) {
                            dirty = resultHolder.getResult();
                        }
                    }
                    if (dirty) {
                        updateSimpleRule(populateValueRequest, mvel, persist, rule);
                    }
                }
            }
        }
        return dirty;
    }

    protected Object extractParent(PopulateValueRequest populateValueRequest, Serializable instance) throws IllegalAccessException, FieldNotAvailableException {
        Object parent = instance;
        String parentName = populateValueRequest.getProperty().getName();
        if (parentName.contains(".")) {
            parent = populateValueRequest.getFieldManager().getFieldValue(instance,
                    parentName.substring(0, parentName.lastIndexOf(".")));
        }
        if (!populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager().contains(parent)) {
            try {
                populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(parent);
            } catch (Exception e) {
                throw new ParentEntityPersistenceException("Unable to Persist the parent entity during rule builder field population", e);
            }
        }
        return parent;
    }

    protected boolean populateQuantityRule(PopulateValueRequest populateValueRequest, Serializable instance) throws FieldNotAvailableException, IllegalAccessException {
        String prop = populateValueRequest.getProperty().getName();
        Field field = populateValueRequest.getFieldManager().getField(instance.getClass(), prop);
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (oneToMany == null) {
            throw new UnsupportedOperationException("RuleFieldPersistenceProvider is currently only compatible with collection fields when modelled using @OneToMany");
        }
        boolean dirty;//currently, this only works with Collection fields
        Class<?> valueType = getListFieldType(instance, populateValueRequest
                .getFieldManager(), populateValueRequest.getProperty(), populateValueRequest.getPersistenceManager());
        if (valueType == null) {
            throw new IllegalAccessException("Unable to determine the valueType for the rule field (" +
                    populateValueRequest.getProperty().getName() + ")");
        }
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        Collection<QuantityBasedRule> rules;
        rules = (Collection<QuantityBasedRule>) populateValueRequest.getFieldManager().getFieldValue
                (instance, populateValueRequest.getProperty().getName());
        Object parent = extractParent(populateValueRequest, instance);
        //AntiSamy HTML encodes the rule JSON - pass the unHTMLEncoded version
        dirty = updateQuantityRule(
                populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager(),
                translator, RuleIdentifier.ENTITY_KEY_MAP.get(populateValueRequest.getMetadata().getRuleIdentifier()),
                populateValueRequest.getMetadata().getRuleIdentifier(),
                populateValueRequest.getProperty().getUnHtmlEncodedValue(), rules, valueType, parent,
                oneToMany.mappedBy(),
                populateValueRequest.getProperty());
        return dirty;
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
            Long id = quantityBasedRule.getId();
            if (extensionManager != null) {
                ExtensionResultHolder<Long> resultHolder = new ExtensionResultHolder<Long>();
                ExtensionResultStatusType result = extensionManager.getProxy().transformId(quantityBasedRule, resultHolder);
                if (ExtensionResultStatusType.NOT_HANDLED != result && resultHolder.getResult() != null) {
                    id = resultHolder.getResult();
                }
            }
            idProperty.setValue(String.valueOf(id));
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

    protected boolean updateQuantityRule(EntityManager em, DataDTOToMVELTranslator translator, String entityKey,
                                         String fieldService, String jsonPropertyValue,
                                         Collection<QuantityBasedRule> criteriaList, Class<?> memberType,
                                         Object parent, String mappedBy, Property property) {
        boolean dirty = false;
        if (!StringUtils.isEmpty(jsonPropertyValue)) {
            //avoid lazy init exception on the criteria list for criteria created during an add
            criteriaList.size();
            DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(jsonPropertyValue);
            if (dw != null && StringUtils.isEmpty(dw.getError())) {
                List<QuantityBasedRule> updatedRules = new ArrayList<QuantityBasedRule>();
                for (DataDTO dto : dw.getData()) {
                    if (dto.getPk() != null && !CollectionUtils.isEmpty(criteriaList)) {
                        checkId: {
                            //updates are comprehensive, even data that was not changed
                            //is submitted here
                            //Update Existing Criteria
                            for (QuantityBasedRule quantityBasedRule : criteriaList) {
                                //make compatible with enterprise module
                                Long id = sandBoxHelper.getOriginalId(quantityBasedRule);
                                boolean isMatch = dto.getPk().equals(id) || dto.getPk().equals(quantityBasedRule.getId());
                                if (isMatch){
                                    String mvel;
                                    //don't update if the data has not changed
                                    if (!quantityBasedRule.getQuantity().equals(dto.getQuantity())) {
                                        dirty = true;
                                    }
                                    try {
                                        mvel = ruleFieldExtractionUtility.convertDTOToMvelString(translator, entityKey, dto, fieldService);
                                        if (!quantityBasedRule.getMatchRule().equals(mvel)) {
                                            dirty = true;
                                        }
                                    } catch (MVELTranslationException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if (!dirty && extensionManager != null) {
                                        ExtensionResultHolder<Boolean> resultHolder = new ExtensionResultHolder<Boolean>();
                                        ExtensionResultStatusType result = extensionManager.getProxy().establishDirtyState(quantityBasedRule,
                                                resultHolder);
                                        if (ExtensionResultStatusType.NOT_HANDLED != result && resultHolder.getResult() != null) {
                                            dirty = resultHolder.getResult();
                                        }
                                    }
                                    if (dirty) {
                                        quantityBasedRule.setQuantity(dto.getQuantity());
                                        quantityBasedRule.setMatchRule(mvel);
                                        quantityBasedRule = em.merge(quantityBasedRule);
                                    }
                                    updatedRules.add(quantityBasedRule);
                                    break checkId;
                                }
                            }
                            throw new IllegalArgumentException("Unable to update the rule of type (" + memberType.getName() +
                                    ") because an update was requested for id (" + dto.getPk() + "), which does not exist.");
                        }
                    } else {
                        //Create a new Criteria
                        QuantityBasedRule quantityBasedRule;
                        try {
                            quantityBasedRule = (QuantityBasedRule) memberType.newInstance();
                            quantityBasedRule.setQuantity(dto.getQuantity());
                            quantityBasedRule.setMatchRule(ruleFieldExtractionUtility.convertDTOToMvelString(translator, entityKey, dto, fieldService));
                            if (StringUtils.isEmpty(quantityBasedRule.getMatchRule()) && !StringUtils.isEmpty(dw.getRawMvel())) {
                                quantityBasedRule.setMatchRule(dw.getRawMvel());
                            }
                            PropertyUtils.setNestedProperty(quantityBasedRule, mappedBy, parent);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        em.persist(quantityBasedRule);
                        dto.setPk(quantityBasedRule.getId());
                        if (extensionManager != null) {
                            ExtensionResultHolder resultHolder = new ExtensionResultHolder();
                            extensionManager.getProxy().postAdd(quantityBasedRule, resultHolder);
                            if (resultHolder.getResult() != null) {
                                quantityBasedRule = (QuantityBasedRule) resultHolder.getResult();
                            }
                        }
                        updatedRules.add(quantityBasedRule);
                        dirty = true;
                    }
                }
                //if an item was not included in the comprehensive submit from the client, we can assume that the
                //listing was deleted, so we remove it here.
                Iterator<QuantityBasedRule> itr = criteriaList.iterator();
                while (itr.hasNext()) {
                    checkForRemove:
                    {
                        QuantityBasedRule original = itr.next();
                        for (QuantityBasedRule quantityBasedRule : updatedRules) {
                            Long id = sandBoxHelper.getOriginalId(quantityBasedRule);
                            boolean isMatch = original.getId().equals(id) || original.getId().equals(quantityBasedRule.getId());
                            if (isMatch) {
                                break checkForRemove;
                            }
                        }
                        em.remove(original);
                        itr.remove();
                        dirty = true;
                    }
                }
                ObjectMapper mapper = new ObjectMapper();
                String json;
                try {
                    json = mapper.writeValueAsString(dw);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                property.setValue(json);
            }
        }
        return dirty;
    }

    protected void updateSimpleRule(PopulateValueRequest populateValueRequest, String mvel, boolean persist,
                               SimpleRule rule) throws IllegalAccessException, FieldNotAvailableException {
        if (!persist) {
            //pre-merge (can result in a clone for enterprise)
            rule = populateValueRequest.getPersistenceManager().getDynamicEntityDao().merge(rule);
        }
        rule.setMatchRule(mvel);
        if (persist) {
            populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(rule);
            if (extensionManager != null) {
                ExtensionResultHolder resultHolder = new ExtensionResultHolder();
                extensionManager.getProxy().postAdd(rule, resultHolder);
            }
        }
    }

    protected Class<?> getStartingValueType(PopulateValueRequest populateValueRequest) throws ClassNotFoundException, IllegalAccessException {
        Class<?> startingValueType = null;
        if (!populateValueRequest.getProperty().getName().contains(FieldManager.MAPFIELDSEPARATOR)) {
            startingValueType = populateValueRequest.getReturnType();
        } else {
            String valueClassName = populateValueRequest.getMetadata().getMapFieldValueClass();
            if (valueClassName != null) {
                startingValueType = Class.forName(valueClassName);
            }
            if (startingValueType == null) {
                startingValueType = populateValueRequest.getReturnType();
            }
        }
        if (startingValueType == null) {
            throw new IllegalAccessException("Unable to determine the valueType for the rule field (" + populateValueRequest.getProperty().getName() + ")");
        }
        return startingValueType;
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.RULE;
    }
}
