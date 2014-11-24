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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.broadleafcommerce.common.rule.SimpleRule;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

/**
 * @author Jeff Fischer
 */
@Component("blRuleFieldPersistenceProvider")
@Scope("prototype")
public class RuleFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        return populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY ||
                populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE;
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY ||
                extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE;
    }

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Resource(name = "blRuleFieldExtractionUtility")
    protected RuleFieldExtractionUtility ruleFieldExtractionUtility;

    @Override
    public FieldProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) throws PersistenceException {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        boolean dirty = false;
        try {
            setNonDisplayableValues(populateValueRequest);
            switch (populateValueRequest.getMetadata().getFieldType()) {
                case RULE_WITH_QUANTITY:{
                    //currently, this only works with Collection fields
                    Class<?> valueType = getListFieldType(instance, populateValueRequest
                            .getFieldManager(), populateValueRequest.getProperty(), populateValueRequest.getPersistenceManager());
                    if (valueType == null) {
                        throw new IllegalAccessException("Unable to determine the valueType for the rule field (" +
                                populateValueRequest.getProperty().getName() + ")");
                    }
                    DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
                    Collection<QuantityBasedRule> rules;
                    try {
                        rules = (Collection<QuantityBasedRule>) populateValueRequest.getFieldManager().getFieldValue
                                (instance, populateValueRequest.getProperty().getName());
                    } catch (FieldNotAvailableException e) {
                        throw new IllegalArgumentException(e);
                    }
                    //AntiSamy HTML encodes the rule JSON - pass the unHTMLEncoded version
                    dirty = populateQuantityBaseRuleCollection(populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager(),
                            translator,
                            RuleIdentifier.ENTITY_KEY_MAP.get(populateValueRequest.getMetadata().getRuleIdentifier()),
                            populateValueRequest.getMetadata().getRuleIdentifier(),
                            populateValueRequest.getProperty().getUnHtmlEncodedValue(),
                            rules,
                            valueType);
                    break;
                }
                case RULE_SIMPLE:{
                    DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
                    //AntiSamy HTML encodes the rule JSON - pass the unHTMLEncoded version
                    DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(populateValueRequest.getProperty().getUnHtmlEncodedValue());
                    if (dw == null || StringUtils.isEmpty(dw.getError())) {
                        String mvel = ruleFieldExtractionUtility.convertSimpleMatchRuleJsonToMvel(translator, RuleIdentifier.ENTITY_KEY_MAP.get(populateValueRequest.getMetadata().getRuleIdentifier()),
                                populateValueRequest.getMetadata().getRuleIdentifier(), dw);
                        Class<?> valueType = null;
                        //is this a regular field?
                        if (!populateValueRequest.getProperty().getName().contains(FieldManager.MAPFIELDSEPARATOR)) {
                            valueType = populateValueRequest.getReturnType();
                        } else {
                            String valueClassName = populateValueRequest.getMetadata().getMapFieldValueClass();
                            if (valueClassName != null) {
                                valueType = Class.forName(valueClassName);
                            }
                            if (valueType == null) {
                                valueType = populateValueRequest.getReturnType();
                            }
                        }
                        if (valueType == null) {
                            throw new IllegalAccessException("Unable to determine the valueType for the rule field (" + populateValueRequest.getProperty().getName() + ")");
                        }
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
                            //see if there's an existing rule
                            SimpleRule rule;
                            try {
                                rule = (SimpleRule) populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName());
                            } catch (FieldNotAvailableException e) {
                                throw new IllegalArgumentException(e);
                            }
                            if (mvel == null) {
                                //cause the rule to be deleted
                                dirty = populateValueRequest.getFieldManager().getFieldValue(instance, populateValueRequest.getProperty().getName()) != null;
                                populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), null);
                            } else if (rule != null) {
                                dirty = !rule.getMatchRule().equals(mvel);
                                rule.setMatchRule(mvel);
                            } else {
                                //create a new instance, persist and set
                                dirty = true;
                                rule = (SimpleRule) valueType.newInstance();
                                rule.setMatchRule(mvel);
                                populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(rule);
                                populateValueRequest.getFieldManager().setFieldValue(instance, populateValueRequest.getProperty().getName(), rule);
                            }
                        }
                    }
                    break;
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
        if (!canHandleExtraction(extractValueRequest, property)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        String val = null;
        ObjectMapper mapper = new ObjectMapper();
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();
        if (extractValueRequest.getMetadata().getFieldType()== SupportedFieldType.RULE_SIMPLE) {
            if (extractValueRequest.getRequestedValue() != null) {
                if (extractValueRequest.getRequestedValue() instanceof String) {
                    val = (String) extractValueRequest.getRequestedValue();
                    property.setValue(val);
                    property.setDisplayValue(extractValueRequest.getDisplayVal());
                }
                if (extractValueRequest.getRequestedValue() instanceof SimpleRule) {
                    SimpleRule simpleRule = (SimpleRule) extractValueRequest.getRequestedValue();
                    if (simpleRule != null) {
                        val = simpleRule.getMatchRule();
                        property.setValue(val);
                        property.setDisplayValue(extractValueRequest.getDisplayVal());
                    }
                }
            }
            Property jsonProperty = ruleFieldExtractionUtility.convertSimpleRuleToJson(translator,
                    mapper,
                    val,
                    property.getName() + "Json",
                    extractValueRequest.getMetadata().getRuleIdentifier());
            
            extractValueRequest.getProps().add(jsonProperty);
        }
        if (extractValueRequest.getMetadata().getFieldType()==SupportedFieldType.RULE_WITH_QUANTITY) {
            if (extractValueRequest.getRequestedValue() != null) {
                if (extractValueRequest.getRequestedValue() instanceof Collection) {
                    //these quantity rules are in a list - this is a special, valid case for quantity rules
                    Property jsonProperty = ruleFieldExtractionUtility.convertQuantityBasedRuleToJson(translator,
                            mapper,
                            (Collection<QuantityBasedRule>) extractValueRequest.getRequestedValue(),
                            extractValueRequest.getMetadata().getName() + "Json",
                            extractValueRequest.getMetadata().getRuleIdentifier());
                    
                    extractValueRequest.getProps().add(jsonProperty);
                } else {
                    //TODO support a single quantity based rule
                    throw new UnsupportedOperationException("RULE_WITH_QUANTITY type is currently only supported" +
                            "on collection fields. A single field with this type is not currently supported.");
                }
            }
        }
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest, Map<String, FieldMetadata> properties) {
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
                                originalFM.getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY) {
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
        return FieldProviderResponse.HANDLED;
    }


    protected boolean populateQuantityBaseRuleCollection(EntityManager em, DataDTOToMVELTranslator translator, String entityKey,
                                                          String fieldService, String jsonPropertyValue,
                                                          Collection<QuantityBasedRule> criteriaList, Class<?> memberType) {
        boolean dirty = false;
        if (!StringUtils.isEmpty(jsonPropertyValue)) {
            DataWrapper dw = ruleFieldExtractionUtility.convertJsonToDataWrapper(jsonPropertyValue);
            if (dw != null && StringUtils.isEmpty(dw.getError())) {
                List<QuantityBasedRule> updatedRules = new ArrayList<QuantityBasedRule>();
                for (DataDTO dto : dw.getData()) {
                    if (dto.getId() != null && !CollectionUtils.isEmpty(criteriaList)) {
                        checkId: {
                            //updates are comprehensive, even data that was not changed
                            //is submitted here
                            //Update Existing Criteria
                            for (QuantityBasedRule quantityBasedRule : criteriaList) {
                                //make compatible with enterprise module
                                Long sandBoxVersionId = sandBoxHelper.getSandBoxVersionId(em, quantityBasedRule.getClass(), dto.getId());
                                if (sandBoxVersionId == null) {
                                    sandBoxVersionId = dto.getId();
                                }
                                if (sandBoxVersionId.equals(quantityBasedRule.getId())){
                                    //don't update if the data has not changed
                                    if (!quantityBasedRule.getQuantity().equals(dto.getQuantity())) {
                                        quantityBasedRule.setQuantity(dto.getQuantity());
                                        dirty = true;
                                    }
                                    try {
                                        String mvel = ruleFieldExtractionUtility.convertDTOToMvelString(translator, entityKey, dto, fieldService);
                                        if (!quantityBasedRule.getMatchRule().equals(mvel)) {
                                            quantityBasedRule.setMatchRule(mvel);
                                            dirty = true;
                                        }
                                    } catch (MVELTranslationException e) {
                                        throw new RuntimeException(e);
                                    }
                                    //make compatible with enterprise module
                                    em.flush();
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
                            quantityBasedRule.setMatchRule(ruleFieldExtractionUtility.convertDTOToMvelString(translator, entityKey, dto, fieldService));
                            if (StringUtils.isEmpty(quantityBasedRule.getMatchRule()) && !StringUtils.isEmpty(dw.getRawMvel())) {
                                quantityBasedRule.setMatchRule(dw.getRawMvel());
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        sandBoxHelper.setupSandBoxState(quantityBasedRule, em);
                        em.persist(quantityBasedRule);
                        criteriaList.add(quantityBasedRule);
                        updatedRules.add(quantityBasedRule);
                        dirty = true;
                    }
                }
                //if an item was not included in the comprehensive submit from the client, we can assume that the
                //listing was deleted, so we remove it here.
                Iterator<QuantityBasedRule> itr = criteriaList.iterator();
                while(itr.hasNext()) {
                    checkForRemove: {
                        QuantityBasedRule original = itr.next();
                        for (QuantityBasedRule quantityBasedRule : updatedRules) {
                            if (String.valueOf(original.getId()).equals(String.valueOf(quantityBasedRule.getId()))) {
                                break checkForRemove;
                            }
                        }
                        sandBoxHelper.archiveObject(original, em);
                        itr.remove();
                        dirty = true;
                    }
                }
            }
        }
        return dirty;
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.RULE;
    }
}
