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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.broadleafcommerce.common.rule.SimpleRule;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.ParentEntityPersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.extension.RuleFieldPersistenceProviderCascadeExtensionManager;
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
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.Embeddable;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

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
                populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE ||
                populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE_TIME;
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY ||
                extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE ||
                extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.RULE_SIMPLE_TIME;
    }

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Resource(name = "blRuleFieldExtractionUtility")
    protected RuleFieldExtractionUtility ruleFieldExtractionUtility;
    
    @Resource(name = "blRuleFieldPersistenceProviderExtensionManager")
    protected RuleFieldPersistenceProviderExtensionManager extensionManager;

    @Resource(name = "blRuleFieldPersistenceProviderCascadeExtensionManager")
    protected RuleFieldPersistenceProviderCascadeExtensionManager cascadeExtensionManager;

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
                case RULE_SIMPLE:{
                    dirty = populateSimpleRule(populateValueRequest, instance);
                    break;
                }
                case RULE_SIMPLE_TIME:{
                    dirty = populateSimpleRule(populateValueRequest, instance);
                    break;
                }
            }
        } catch (Exception e) {
            throw ExceptionHelper.refineException(PersistenceException.class, PersistenceException.class, e);
        }
        populateValueRequest.getProperty().setIsDirty(!populateValueRequest.getPreAdd() && dirty);

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
                extractValueRequest.getMetadata().getFieldType()== SupportedFieldType.RULE_SIMPLE_TIME) {
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
                    String propName = prop.getName().substring(0, prop.getName().length()-4);
                    if (propName.equals(entry.getKey())) {
                        BasicFieldMetadata originalFM = (BasicFieldMetadata) entry.getValue();
                        if (originalFM.getFieldType() == SupportedFieldType.RULE_SIMPLE ||
                                originalFM.getFieldType() == SupportedFieldType.RULE_SIMPLE_TIME ||
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
        return MetadataProviderResponse.HANDLED;
    }

    protected void extractSimpleRule(ExtractValueRequest extractValueRequest, Property property, ObjectMapper mapper, MVELToDataWrapperTranslator translator) {
        Property jsonProperty;
        if (extractValueRequest.getRequestedValue() != null) {
            if (extractValueRequest.getRequestedValue() instanceof String) {
                String val = (String) extractValueRequest.getRequestedValue();
                property.setValue(val);
                property.setDisplayValue(extractValueRequest.getDisplayVal());
                jsonProperty = ruleFieldExtractionUtility.convertSimpleRuleToJson(translator, mapper, val,
                                property.getName() + "Json", extractValueRequest.getMetadata().getRuleIdentifier());
            } else {
                Object simpleRule = extractValueRequest.getRequestedValue();
                if (simpleRule != null) {
                    if (simpleRule instanceof SimpleRule) {
                        String val = ((SimpleRule) simpleRule).getMatchRule();
                        property.setValue(val);
                        property.setDisplayValue(extractValueRequest.getDisplayVal());
                        jsonProperty = convertSimpleRuleToJson(translator, mapper, (SimpleRule) simpleRule, property.getName() + "Json", extractValueRequest.getMetadata().getRuleIdentifier());
                    } else {
                        throw new UnsupportedOperationException("RULE_SIMPLE type is currently only supported on " +
                                "fields of type SimpleRule");
                    }
                } else {
                    jsonProperty = ruleFieldExtractionUtility.convertSimpleRuleToJson(translator, mapper, null,
                                property.getName() + "Json", extractValueRequest.getMetadata().getRuleIdentifier());
                }
            }
        } else {
            jsonProperty = ruleFieldExtractionUtility.convertSimpleRuleToJson(translator, mapper, null,
                                property.getName() + "Json", extractValueRequest.getMetadata().getRuleIdentifier());
        }
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
                // Restore JSON rule after AntiSamy policy
                if (!populateValueRequest.getProperty().getValue().equals(populateValueRequest.getProperty().getUnHtmlEncodedValue())) {
                    populateValueRequest.getProperty().setValue(populateValueRequest.getProperty().getUnHtmlEncodedValue());
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
                            //Since this class explicitly removes the simple rule - we must also preserve the id of the element
                            //as the CacheInvalidationProducer will need this in order to remove the member cache instance as well.
                            BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
                            context.getAdditionalProperties().put("deletedSimpleRule", rule);

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

                        EntityManager em = populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager();
                        Long id = getRuleId(rule, em);
                        Long containedId = getContainedRuleId(rule, em);

                        DataDTO dto = dw.getData().get(0);
                        if (persist && cascadeExtensionManager != null) {
                            ExtensionResultHolder resultHolder = new ExtensionResultHolder();
                            cascadeExtensionManager.getProxy().postCascadeAdd(rule, dto, resultHolder);
                        }
                        dto.setPk(id);
                        dto.setContainedPk(containedId);
                        ObjectMapper mapper = new ObjectMapper();
                        String json;
                        try {
                            json = mapper.writeValueAsString(dw);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        populateValueRequest.getProperty().setValue(json);
                    }
                }
            }
        }
        return dirty;
    }

    protected Long getRuleId(SimpleRule rule, EntityManager em) {
        if (!em.contains(rule)) {
            rule = em.merge(rule);
        }

        Long id = (Long) em.unwrap(Session.class).getIdentifier(rule);
        id = transformId(id, rule);
        return id;
    }

    protected Long getContainedRuleId(SimpleRule simpleRule, EntityManager em) {
        Long containedId = null;

        Object containedRule = findContainedRuleIfApplicable(simpleRule);
        if (containedRule != null) {
            if (!em.contains(containedRule)) {
                containedRule = em.merge(containedRule);
            }

            containedId = (Long) em.unwrap(Session.class).getIdentifier(containedRule);
            containedId = transformId(containedId, containedRule);

        }

        return containedId;
    }

    protected Long transformId(Long id, Object rule) {
        if (extensionManager != null) {
            ExtensionResultHolder<Long> resultHolder = new ExtensionResultHolder<Long>();
            ExtensionResultStatusType result = extensionManager.getProxy().transformId(rule, resultHolder);
            if (ExtensionResultStatusType.NOT_HANDLED != result && resultHolder.getResult() != null) {
                id = resultHolder.getResult();
            }
        }
        return id;
    }

    /**
     * This method is intended to find the object that the field is supposed to be populated on. Typically, this is the
     * instance itself, but sometimes it may be a property of an object this instance relates to.
     *
     * This method ignores parent candidates that implement {@link Embeddable} as the proper parent for these
     * fields is the object itself and not the embedded object.
     *
     * @param populateValueRequest the {@link PopulateValueRequest}
     * @param instance the Object we are populating field values on
     * @return the proper parent for the {@link PopulateValueRequest}
     * @throws IllegalAccessException
     * @throws FieldNotAvailableException
     */
    protected Object extractParent(PopulateValueRequest populateValueRequest, Serializable instance)
            throws IllegalAccessException, FieldNotAvailableException {
        Object parent = recursivelyExtractParent(populateValueRequest, instance);
        if (!populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager().contains(parent)) {
            try {
                populateValueRequest.getPersistenceManager().getDynamicEntityDao().persist(parent);
            } catch (Exception e) {
                throw new ParentEntityPersistenceException("Unable to Persist the parent entity during rule builder field population", e);
            }
        }
        return parent;
    }

    /**
     * This method is responsible for recursively tracing the properties parents to find the correct parent to apply
     * the field update to.
     *
     * @param populateValueRequest the populate value request
     * @param instance the entity that to populate a field value on
     * @return the proper parent or original instance if no closer parent is found
     * @throws FieldNotAvailableException
     * @throws IllegalAccessException
     */
    protected Object recursivelyExtractParent(PopulateValueRequest populateValueRequest, Serializable instance)
            throws FieldNotAvailableException, IllegalAccessException {
        String propertyName = populateValueRequest.getProperty().getName();

        while(StringUtils.contains(propertyName, ".")) {
            propertyName = parseParentProperty(propertyName);

            Object candidate = populateValueRequest.getFieldManager().getFieldValue(instance, propertyName);

            if (candidate != null && !isEmbeddable(candidate.getClass())) {
                return candidate;
            }
        }

        return instance;
    }

    protected String parseParentProperty(String propertyName) {
        return propertyName.substring(0, propertyName.lastIndexOf("."));
    }

    /**
     * This method is responsible for determining whether the class is embeddable. If it is we don't want to check if
     * the entity manager contains the instance since embeddables are not entities.
     *
     * @param clazz the parent class of the populate value request
     * @return whether the class is embeddable
     */
    protected boolean isEmbeddable(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, Embeddable.class) != null;
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
        EntityManager entityManager = populateValueRequest.getPersistenceManager().getDynamicEntityDao().getStandardEntityManager();
        String fieldService = populateValueRequest.getMetadata().getRuleIdentifier();
        String entityKey = RuleIdentifier.ENTITY_KEY_MAP.get(fieldService);
        Property ruleProperty = populateValueRequest.getProperty();
        String jsonPropertyValue = ruleProperty.getUnHtmlEncodedValue();
        String mappedByEntity = oneToMany.mappedBy();
        dirty = updateQuantityRule(
                entityManager, translator, entityKey,
                fieldService, jsonPropertyValue, rules, valueType, parent,
                mappedByEntity, ruleProperty);
        return dirty;
    }

    protected Property convertSimpleRuleToJson(MVELToDataWrapperTranslator translator, ObjectMapper mapper, SimpleRule simpleRule, String jsonProp, String fieldService) {
        String matchRule = simpleRule.getMatchRule();
        Entity[] matchCriteria = new Entity[1];
        Property[] properties = new Property[3];

        Property mvelProperty = new Property();
        mvelProperty.setName("matchRule");
        mvelProperty.setValue(matchRule == null ? "" : matchRule);
        properties[0] = mvelProperty;

        Entity criteria = new Entity();
        criteria.setProperties(properties);
        matchCriteria[0] = criteria;

        EntityManager em = PersistenceManagerFactory.getDefaultPersistenceManager().getDynamicEntityDao().getStandardEntityManager();
        Long id = getRuleId(simpleRule, em);
        Property idProperty = new Property();
        idProperty.setName("id");
        idProperty.setValue(String.valueOf(id));
        properties[1] = idProperty;

        Long containedId = getContainedRuleId(simpleRule, em);
        Property containedIdProperty = new Property();
        containedIdProperty.setName("containedId");
        containedIdProperty.setValue(String.valueOf(containedId));
        properties[2] = containedIdProperty;

        String json;
        try {
            DataWrapper orderWrapper = translator.createRuleData(matchCriteria, "matchRule", null, "id", "containedId", ruleBuilderFieldServiceFactory.createInstance(fieldService));
            json = mapper.writeValueAsString(orderWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Property p = new Property();
        p.setName(jsonProp);
        p.setValue(json);

        return p;
    }

    protected Property convertQuantityBasedRuleToJson(MVELToDataWrapperTranslator translator, ObjectMapper mapper,
                        Collection<QuantityBasedRule> quantityBasedRules, String jsonProp, String fieldService) {

        int k=0;
        Entity[] targetItemCriterias = new Entity[quantityBasedRules.size()];
        for (QuantityBasedRule quantityBasedRule : quantityBasedRules) {
            Property[] properties = new Property[4];

            Property mvelProperty = new Property();
            mvelProperty.setName("matchRule");
            mvelProperty.setValue(quantityBasedRule.getMatchRule());

            Property quantityProperty = new Property();
            quantityProperty.setName("quantity");
            quantityProperty.setValue(quantityBasedRule.getQuantity().toString());

            Property idProperty = new Property();
            idProperty.setName("id");
            Long id = quantityBasedRule.getId();
            id = transformId(id, quantityBasedRule);
            idProperty.setValue(String.valueOf(id));

            Object containedRule = findContainedRuleIfApplicable(quantityBasedRule);
            Property containedIdProperty = new Property();
            if (containedRule != null) {
                containedIdProperty.setName("containedId");
                EntityManager em = PersistenceManagerFactory.getDefaultPersistenceManager().getDynamicEntityDao().getStandardEntityManager();
                Long containedId = (Long) em.unwrap(Session.class).getIdentifier(containedRule);
                containedId = transformId(containedId, containedRule);
                containedIdProperty.setValue(String.valueOf(containedId));
            }

            properties[0] = mvelProperty;
            properties[1] = quantityProperty;
            properties[2] = idProperty;
            properties[3] = containedIdProperty;
            Entity criteria = new Entity();
            criteria.setProperties(properties);
            targetItemCriterias[k] = criteria;
            k++;
        }

        String json;
        try {
            DataWrapper oiWrapper = translator.createRuleData(targetItemCriterias, "matchRule", "quantity", "id", "containedId", ruleBuilderFieldServiceFactory.createInstance(fieldService));
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
                                boolean isParentRelated = sandBoxHelper.isRelatedToParentCatalogIds(quantityBasedRule, dto.getPk());
                                boolean isMatch = isParentRelated || dto.getPk().equals(quantityBasedRule.getId());
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
                                        // pre-merge (can result in a clone for enterprise)
                                        quantityBasedRule = em.merge(quantityBasedRule);

                                        // update the quantity based rule
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
                        Object contained = findContainedRuleIfApplicable(quantityBasedRule);
                        if (contained != null) {
                            dto.setContainedPk((Long) em.unwrap(Session.class).getIdentifier(contained));
                        }
                        if (extensionManager != null) {
                            ExtensionResultHolder resultHolder = new ExtensionResultHolder();
                            extensionManager.getProxy().postAdd(quantityBasedRule, resultHolder);
                            if (resultHolder.getResult() != null) {
                                quantityBasedRule = (QuantityBasedRule) resultHolder.getResult();
                            }
                        }
                        if (cascadeExtensionManager != null) {
                            ExtensionResultHolder resultHolder = new ExtensionResultHolder();
                            cascadeExtensionManager.getProxy().postCascadeAdd(quantityBasedRule, dto, resultHolder);
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
                //Since this class explicitly removes the quantity based rule - we must also preserve the id of the element
                //as the CacheInvalidationProducer will need this in order to remove each collection member cache instance as well.
                BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
                context.getAdditionalProperties().put("deletedQuantityBasedRules", new HashSet<QuantityBasedRule>());
                Set<String> otherChangeSetProps =
                        (Set<String>) context.getAdditionalProperties().get("otherChangeSetProps");
                if (otherChangeSetProps == null || (otherChangeSetProps != null && !otherChangeSetProps
                        .contains(property.getName()))) {
                    while (itr.hasNext()) {
                        checkForRemove:
                        {
                            QuantityBasedRule original = itr.next();
                            for (QuantityBasedRule quantityBasedRule : updatedRules) {
                                Long id = sandBoxHelper.getOriginalId(quantityBasedRule);
                                Long origId = sandBoxHelper.getOriginalId(original);
                                boolean isMatch = original.getId().equals(id) || original.getId()
                                        .equals(quantityBasedRule.getId()) ||
                                        (id != null && id.equals(origId));
                                if (isMatch) {
                                    break checkForRemove;
                                }
                            }
                            ((Set<QuantityBasedRule>) context.getAdditionalProperties()
                                    .get("deletedQuantityBasedRules")).add(original);
                            em.remove(original);
                            itr.remove();
                            dirty = true;
                        }
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

    public static Object findContainedRuleIfApplicable(Object rule) {
        Object response = null;
        for (Field field : getAllFields(rule.getClass())) {
            field.setAccessible(true);
            Object test = null;
            try {
                test = field.get(rule);
            } catch (IllegalAccessException e) {
                throw ExceptionHelper.refineException(e);
            }
            if (test != null && (test instanceof SimpleRule || test instanceof QuantityBasedRule)) {
                response = test;
                break;
            }
        }
        return response;
    }

    private static Field[] getAllFields(Class<?> targetClass) {
        Field[] allFields = new Field[]{};
        boolean eof = false;
        Class<?> currentClass = targetClass;
        while (!eof) {
            Field[] fields = currentClass.getDeclaredFields();
            allFields = (Field[]) ArrayUtils.addAll(allFields, fields);
            if (currentClass.getSuperclass() != null) {
                currentClass = currentClass.getSuperclass();
            } else {
                eof = true;
            }
        }

        return allFields;
    }
}
