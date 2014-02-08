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
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

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
                root.getFields();
        DataDTO dataDTO = new DataDTO();
        ExpressionDTO expressionDTO = new ExpressionDTO();
        boolean isExpression = false;
        while (elementsIterator.hasNext()) {
            Map.Entry<String, JsonNode> element=elementsIterator.next();
            String name = element.getKey();
            if ("name".equals(name)) {
                expressionDTO.setName(element.getValue().asText());
                isExpression = true;
            }
            if ("operator".equals(name)) {
                expressionDTO.setOperator(element.getValue().asText());
                isExpression = true;
            }
            if ("value".equals(name)) {
                expressionDTO.setValue(element.getValue().asText());
                isExpression = true;
            }
            if ("start".equals(name)) {
                expressionDTO.setStart(element.getValue().asText());
                isExpression = true;
            }
            if ("end".equals(name)) {
                expressionDTO.setEnd(element.getValue().asText());
                isExpression = true;
            }
            if ("id".equals(name)) {
                if ("null".equals(element.getValue().asText())) {
                    dataDTO.setId(null);
                } else {
                    dataDTO.setId(element.getValue().asLong());
                }
            }
            if ("quantity".equals(name)) {
                if ("null".equals(element.getValue().asText())) {
                    dataDTO.setQuantity(null);
                } else {
                    dataDTO.setQuantity(element.getValue().asInt());
                }
            }
            if ("groupOperator".equals(name)) {
                dataDTO.setGroupOperator(element.getValue().asText());
            }
            if ("groups".equals(name)){
                dataDTO.setGroups((ArrayList<DataDTO>)mapper.readValue(element.getValue(), new TypeReference<ArrayList<DataDTO>>(){}));
            }


        }

        if (isExpression) {
            return expressionDTO;
        } else {
            return dataDTO;
        }
    }

}
