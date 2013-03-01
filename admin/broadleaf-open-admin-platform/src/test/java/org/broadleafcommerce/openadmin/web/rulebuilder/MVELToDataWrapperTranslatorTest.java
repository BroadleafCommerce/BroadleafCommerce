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

package org.broadleafcommerce.openadmin.web.rulebuilder;

import junit.framework.TestCase;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.OrderItemFieldServiceImpl;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class MVELToDataWrapperTranslatorTest extends TestCase {

    private OrderItemFieldServiceImpl orderItemFieldService;

    @Override
    protected void setUp() {
        orderItemFieldService = new OrderItemFieldServiceImpl();
    }

    public void testCreateRuleData() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[2];
        Property mvelProperty = new Property();
        mvelProperty.setName("orderItemMatchRule");
        mvelProperty.setValue("MVEL.eval(\"toUpperCase()\",discreteOrderItem.category.name)==MVEL.eval(\"toUpperCase()\",\"merchandise\")");
        Property quantityProperty = new Property();
        quantityProperty.setName("quantity");
        quantityProperty.setValue("1");
        properties[0] = mvelProperty;
        properties[1] = quantityProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "orderItemMatchRule", "quantity", orderItemFieldService);
        assert(dataWrapper.getData().size() == 1);
        assert(dataWrapper.getData().get(0).getQuantity() == 1);
        assert(dataWrapper.getData().get(0).getGroups().size()==1);
        assert(dataWrapper.getData().get(0).getGroups().get(0) instanceof ExpressionDTO);
        ExpressionDTO exp = (ExpressionDTO) dataWrapper.getData().get(0).getGroups().get(0);
        assert(exp.getName().equals("category.name"));
        assert(exp.getOperator().equals(BLCOperator.IEQUALS.name()));
        assert(exp.getValue().equals("merchandise"));
    }
}
