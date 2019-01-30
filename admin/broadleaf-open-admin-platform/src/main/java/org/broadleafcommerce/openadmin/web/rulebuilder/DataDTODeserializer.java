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
package org.broadleafcommerce.openadmin.web.rulebuilder;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class DataDTODeserializer extends StdDeserializer<DataDTO> {

    public DataDTODeserializer() {
        super(DataDTO.class);
    }

    @Override
    public DataDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = (ObjectNode) mapper.readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> elementsIterator =
                root.fields();
        DataDTO dataDTO = new DataDTO();
        ExpressionDTO expressionDTO = new ExpressionDTO();
        boolean isExpression = false;
        while (elementsIterator.hasNext()) {
            Map.Entry<String, JsonNode> element=elementsIterator.next();
            String name = element.getKey();
            if ("id".equals(name)) {
                expressionDTO.setId(getNullAwareText(element.getValue()));
                isExpression = true;
            }

            if ("operator".equals(name)) {
                expressionDTO.setOperator(getNullAwareText(element.getValue()));
                isExpression = true;
            }

            if ("value".equals(name)) {
                expressionDTO.setValue(getNullAwareText(element.getValue()));
                isExpression = true;
            }

            if ("pk".equals(name)) {
                if (getNullAwareText(element.getValue()) == null ||
                        StringUtils.isBlank(element.getValue().asText())) {
                    dataDTO.setPk(null);
                } else {
                    dataDTO.setPk(element.getValue().asLong());
                }
            }
            if ("previousPk".equals(name)) {
                if (getNullAwareText(element.getValue()) == null ||
                        StringUtils.isBlank(element.getValue().asText())) {
                    dataDTO.setPreviousPk(null);
                } else {
                    dataDTO.setPreviousPk(element.getValue().asLong());
                }
            }
            if ("containedPk".equals(name)) {
                if (getNullAwareText(element.getValue()) == null ||
                        StringUtils.isBlank(element.getValue().asText())) {
                    dataDTO.setContainedPk(null);
                } else {
                    dataDTO.setContainedPk(element.getValue().asLong());
                }
            }
            if ("previousContainedPk".equals(name)) {
                if (getNullAwareText(element.getValue()) == null ||
                        StringUtils.isBlank(element.getValue().asText())) {
                    dataDTO.setPreviousContainedPk(null);
                } else {
                    dataDTO.setPreviousContainedPk(element.getValue().asLong());
                }
            }
            if ("quantity".equals(name)) {
                if (getNullAwareText(element.getValue()) == null) {
                    dataDTO.setQuantity(null);
                } else {
                    dataDTO.setQuantity(element.getValue().asInt());
                }
            }

            if ("condition".equals(name)) {
                dataDTO.setCondition(getNullAwareText(element.getValue()));
            }

            if ("rules".equals(name)){
                CollectionType dtoCollectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, DataDTO.class);
                dataDTO.setRules((ArrayList<DataDTO>) mapper.readValue(element.getValue().traverse(jp.getCodec()), dtoCollectionType));
            }
        }

        if (isExpression) {
            return expressionDTO;
        } else {
            return dataDTO;
        }
    }
    
    /**
     * Handles the string "null" when using asText() in a JsonNode and returns the literal null instead
     */
    protected String getNullAwareText(JsonNode node) {
        return "null".equals(node.asText()) ? null : node.asText();
    }

}
