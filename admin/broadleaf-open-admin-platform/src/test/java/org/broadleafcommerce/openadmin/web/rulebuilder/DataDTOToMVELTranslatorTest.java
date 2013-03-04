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
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.CustomerFieldServiceImpl;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.OrderFieldServiceImpl;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.OrderItemFieldServiceImpl;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class DataDTOToMVELTranslatorTest extends TestCase {

    private OrderItemFieldServiceImpl orderItemFieldService;
    private CustomerFieldServiceImpl customerFieldService;
    private OrderFieldServiceImpl orderFieldService;

    @Override
    protected void setUp() {
        orderItemFieldService = new OrderItemFieldServiceImpl();
        customerFieldService = new CustomerFieldServiceImpl();
        orderFieldService = new OrderFieldServiceImpl();
    }

    /**
     * Tests the creation of an MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * Here's an example of a DataWrapper with a single DataDTO
     *
     * [{"quantity":"1",
     *  "groupOperator":"AND",
     *  "groups":[
     *      {"quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"category.name",
     *      "operator":"IEQUALS",
     *      "value":"merchandise"}]
     *  }]
     */
    public void testCreateMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        ExpressionDTO expressionDTO = new ExpressionDTO();
        expressionDTO.setName("category.name");
        expressionDTO.setOperator(BLCOperator.IEQUALS.name());
        expressionDTO.setValue("merchandise");

        String translated = translator.createMVEL("discreteOrderItem", expressionDTO, orderItemFieldService);
        String mvel = "MVEL.eval(\"toUpperCase()\",discreteOrderItem.category.name)==MVEL.eval(\"toUpperCase()\",\"merchandise\")";
        assert(mvel.equals(translated));
    }

    /**
     * Tests the creation of a Customer Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"quantity":null,
     *  "groupOperator":"AND",
     *  "groups":[
     *      {"quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"emailAddress",
     *      "operator":"NOT_EQUAL_FIELD",
     *      "value":"username"},
     *      {"quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"deactivated",
     *      "operator":"EQUALS",
     *      "value":"true"}]
     *  }]
     */
    public void testCustomerQualificationMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        DataDTO dataDTO = new DataDTO();
        dataDTO.setGroupOperator(BLCOperator.AND.name());

        ExpressionDTO e1 = new ExpressionDTO();
        e1.setName("emailAddress");
        e1.setOperator(BLCOperator.NOT_EQUAL_FIELD.name());
        e1.setValue("username");

        ExpressionDTO e2 = new ExpressionDTO();
        e2.setName("deactivated");
        e2.setOperator(BLCOperator.EQUALS.name());
        e2.setValue("true");

        dataDTO.getGroups().add(e1);
        dataDTO.getGroups().add(e2);

        String translated = translator.createMVEL("customer", dataDTO, customerFieldService);
        String mvel = "customer.emailAddress!=customer.username&&customer.deactivated==true";
        assert (mvel.equals(translated));
    }

    /**
     * Tests the creation of an Order Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"quantity":null,
     *  "groupOperator":"AND",
     *  "groups":[
     *      {"quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"subTotal",
     *      "operator":"GREATER_OR_EQUAL",
     *      "value":"100"},
     *      {"quantity":null,
     *      "groupOperator":"OR",
     *      "groups":[
     *          {"quantity":null,
     *          "groupOperator":null,
     *          "groups":null,
     *          "name":"currency.defaultFlag",
     *          "operator":"EQUALS",
     *          "value":"true"},
     *          {"quantity":null,
     *          "groupOperator":"null",
     *          "groups":null,
     *          "name":"locale.localeCode",
     *          "operator":"EQUALS",
     *          "value":"my"}]
     *      }]
     *  }]
     */
    public void testOrderQualificationMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        DataDTO dataDTO = new DataDTO();
        dataDTO.setGroupOperator(BLCOperator.AND.name());

        ExpressionDTO expressionDTO = new ExpressionDTO();
        expressionDTO.setName("subTotal");
        expressionDTO.setOperator(BLCOperator.GREATER_OR_EQUAL.name());
        expressionDTO.setValue("100");
        dataDTO.getGroups().add(expressionDTO);

        DataDTO d1 = new DataDTO();
        d1.setGroupOperator(BLCOperator.OR.name());

        ExpressionDTO e1 = new ExpressionDTO();
        e1.setName("currency.defaultFlag");
        e1.setOperator(BLCOperator.EQUALS.name());
        e1.setValue("true");

        ExpressionDTO e2 = new ExpressionDTO();
        e2.setName("locale.localeCode");
        e2.setOperator(BLCOperator.EQUALS.name());
        e2.setValue("my");

        d1.getGroups().add(e1);
        d1.getGroups().add(e2);

        dataDTO.getGroups().add(d1);

        String translated = translator.createMVEL("order", dataDTO, orderFieldService);
        String mvel = "order.subTotal.getAmount()>=100&&(order.currency.defaultFlag==true||order.locale.localeCode==\"my\")";
        assert (mvel.equals(translated));
    }
}
