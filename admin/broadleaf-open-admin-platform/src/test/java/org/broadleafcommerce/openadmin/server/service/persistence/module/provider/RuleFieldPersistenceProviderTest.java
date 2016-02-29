/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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

import static org.junit.Assert.assertEquals;

import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class RuleFieldPersistenceProviderTest {

    @Test
    public void testJSONToDataDTOTranslation() throws JsonGenerationException, JsonMappingException, IOException {
        String json = "{\"data\":[{\"id\":1,\"quantity\":1,\"groupOperator\":\"AND\",\"groups\":[{\"id\":null,\"quantity\":null,\"groupOperator\":null,\"groups\":[],\"name\":\"category.name\",\"operator\":\"IEQUALS\",\"value\":\"merchandise\"}]}]}";
        DataWrapper expected = new DataWrapper();
        DataDTO leftSide = new DataDTO();
        leftSide.setPk(1l);
        leftSide.setQuantity(1);
        leftSide.setCondition("AND");
        ExpressionDTO rightSide = new ExpressionDTO();
        rightSide.setId("category.name");
        rightSide.setOperator("IEQUALS");
        rightSide.setValue("merchandise");
        leftSide.setRules(new ArrayList<DataDTO>(Arrays.asList((DataDTO) rightSide)));
        expected.setData(new ArrayList<DataDTO>(Arrays.asList(leftSide)));
        
        RuleFieldExtractionUtility extractor = new RuleFieldExtractionUtility();
        DataWrapper wrapper = extractor.convertJsonToDataWrapper(json);
        
        assertEquals(expected, wrapper);
        // This JSON is slightly different than the JSON above since it contains the full serialization result of an ExpressionDTO
        // (as opposed to the excluded nulls above)
        String expectedJson = "{\"data\":[{\"id\":1,\"quantity\":1,\"groupOperator\":\"AND\",\"groups\":[{\"id\":null,\"quantity\":null,\"groupOperator\":null,\"groups\":[],\"name\":\"category.name\",\"operator\":\"IEQUALS\",\"value\":\"merchandise\",\"start\":null,\"end\":null}]}],\"error\":null,\"rawMvel\":null}";
        String serializedWrapperJson = wrapper.serialize();
        assertEquals(expectedJson, serializedWrapperJson);
        
        // ensure that serialized can go back to the original data wrapper
        DataWrapper serializedWrapper = extractor.convertJsonToDataWrapper(serializedWrapperJson);
        assertEquals(expected, serializedWrapper);
    }
    
}
