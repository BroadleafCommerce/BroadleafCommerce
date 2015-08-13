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
package org.broadleafcommerce.openadmin.web.rulebuilder;

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
                if (getNullAwareText(element.getValue()) == null) {
                    dataDTO.setPk(null);
                } else {
                    dataDTO.setPk(element.getValue().asLong());
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
