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
import org.broadleafcommerce.openadmin.web.rulebuilder.service.CustomerFieldServiceImpl;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.OrderItemFieldServiceImpl;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class MVELToDataWrapperTranslatorTest extends TestCase {

    private OrderItemFieldServiceImpl orderItemFieldService;
    private CustomerFieldServiceImpl customerFieldService;

    @Override
    protected void setUp() {
        orderItemFieldService = new OrderItemFieldServiceImpl();
        customerFieldService = new CustomerFieldServiceImpl();
    }

    /**
     * Tests the creation of a DataWrapper given an mvel/quantity property
     * @throws MVELTranslationException
     */
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

    public void testCustomerQualificationDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[1];
        Property mvelProperty = new Property();
        mvelProperty.setName("matchRule");
        mvelProperty.setValue("customer.emailAddress!=customer.username&&customer.deactivated==true");
        properties[0] = mvelProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "matchRule", null, customerFieldService);
        assert(dataWrapper.getData().size() == 1);
        assert(dataWrapper.getData().get(0).getQuantity() == null);
        assert(dataWrapper.getData().get(0).getGroupOperator().equals(BLCOperator.AND.name()));
        assert(dataWrapper.getData().get(0).getGroups().size()==2);

        assert(dataWrapper.getData().get(0).getGroups().get(0) instanceof ExpressionDTO);
        ExpressionDTO e1 = (ExpressionDTO) dataWrapper.getData().get(0).getGroups().get(0);
        assert(e1.getName().equals("emailAddress"));
        assert(e1.getOperator().equals(BLCOperator.NOT_EQUAL_FIELD.name()));
        assert(e1.getValue().equals("username"));

        assert(dataWrapper.getData().get(0).getGroups().get(1) instanceof ExpressionDTO);
        ExpressionDTO e2 = (ExpressionDTO) dataWrapper.getData().get(0).getGroups().get(1);
        assert(e2.getName().equals("deactivated"));
        assert(e2.getOperator().equals(BLCOperator.EQUALS.name()));
        assert(e2.getValue().equals("true"));

    }
}
