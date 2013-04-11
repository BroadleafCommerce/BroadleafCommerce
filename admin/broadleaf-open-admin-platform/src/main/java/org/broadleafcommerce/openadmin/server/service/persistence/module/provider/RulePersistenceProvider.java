package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.broadleafcommerce.common.rule.SimpleRule;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blRulePersistenceProvider")
@Scope("prototype")
public class RulePersistenceProvider extends AbstractPersistenceProvider {

    @Override
    public boolean canHandlePersistence(Object instance, BasicFieldMetadata metadata) {
        return metadata.getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY ||
                metadata.getFieldType() == SupportedFieldType.RULE_SIMPLE;
    }

    @Override
    public boolean canHandleFilterMapping(BasicFieldMetadata metadata) {
        return false;
    }

    @Override
    public boolean canHandleFilterProperties(Entity entity, Map<String, FieldMetadata> unfilteredProperties) {
        return true;
    }

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    public void populateValue(Serializable instance, Boolean setId, FieldManager fieldManager, Property property,
                              BasicFieldMetadata metadata, Class<?> returnType, String value, PersistenceManager persistenceManager,
                              DataFormatProvider dataFormatProvider) throws PersistenceException {
        try {
            switch (metadata.getFieldType()) {
                case RULE_WITH_QUANTITY:{
                    //currently, this only works with Collection fields
                    Class<?> valueType = getListFieldType(instance, fieldManager, property, persistenceManager);
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
                            valueType = returnType;
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
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void extractValue(List<Property> props, FieldManager fieldManager, MVELToDataWrapperTranslator translator,
                             ObjectMapper mapper, BasicFieldMetadata metadata, Object value, String strVal, Property propertyItem,
                             String displayVal, PersistenceManager persistenceManager, DataFormatProvider dataFormatProvider) throws PersistenceException {
        if (metadata.getFieldType()== SupportedFieldType.RULE_SIMPLE) {
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
        }
    }

    @Override
    public void addFilterMapping(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String
            ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties, BaseCtoConverter
                                             ctoConverter, String propertyName, FieldManager fieldManager) {
        //do nothing
    }

    @Override
    public void filterProperties(Entity entity, Map<String, FieldMetadata> mergedProperties) {
        //This may contain rule Json fields - convert and filter out
        List<Property> propertyList = new ArrayList<Property>();
        propertyList.addAll(Arrays.asList(entity.getProperties()));
        Iterator<Property> itr = propertyList.iterator();
        List<Property> additionalProperties = new ArrayList<Property>();
        while(itr.hasNext()) {
            Property prop = itr.next();
            if (prop.getName().endsWith("Json")) {
                for (Map.Entry<String, FieldMetadata> entry : mergedProperties.entrySet()) {
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
}
