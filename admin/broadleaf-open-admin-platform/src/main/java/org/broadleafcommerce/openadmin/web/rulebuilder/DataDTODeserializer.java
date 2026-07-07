/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.type.CollectionType;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class DataDTODeserializer extends StdDeserializer<DataDTO> {

    public DataDTODeserializer() {
        super(DataDTO.class);
    }

    @Override
    public DataDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws JacksonException {
        ObjectNode root = (ObjectNode) ctxt.readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> elementsIterator =
                root.properties().iterator();
        DataDTO dataDTO = new DataDTO();
        ExpressionDTO expressionDTO = new ExpressionDTO();
        boolean isExpression = false;
        while (elementsIterator.hasNext()) {
            Map.Entry<String, JsonNode> element = elementsIterator.next();
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
                        StringUtils.isBlank(element.getValue().asString())) {
                    dataDTO.setPk(null);
                } else {
                    dataDTO.setPk(element.getValue().asLong());
                }
            }
            if ("previousPk".equals(name)) {
                if (getNullAwareText(element.getValue()) == null ||
                        StringUtils.isBlank(element.getValue().asString())) {
                    dataDTO.setPreviousPk(null);
                } else {
                    dataDTO.setPreviousPk(element.getValue().asLong());
                }
            }
            if ("containedPk".equals(name)) {
                if (getNullAwareText(element.getValue()) == null ||
                        StringUtils.isBlank(element.getValue().asString())) {
                    dataDTO.setContainedPk(null);
                } else {
                    dataDTO.setContainedPk(element.getValue().asLong());
                }
            }
            if ("previousContainedPk".equals(name)) {
                if (getNullAwareText(element.getValue()) == null ||
                        StringUtils.isBlank(element.getValue().asString())) {
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

            if ("rules".equals(name)) {
                CollectionType dtoCollectionType = ctxt.getTypeFactory().constructCollectionType(
                        ArrayList.class, DataDTO.class
                );
                dataDTO.setRules(ctxt.readValue(element.getValue().traverse(ctxt), dtoCollectionType));
            }
        }

        if (isExpression) {
            return expressionDTO;
        } else {
            return dataDTO;
        }
    }

    /**
     * Handles the string "null" when using asString() in a JsonNode and returns the literal null instead
     */
    protected String getNullAwareText(JsonNode node) {
        return "null".equals(node.asString()) ? null : node.asString();
    }

}
