/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.web.rulebuilder;

import junit.framework.TestCase;
import org.broadleafcommerce.admin.web.rulebuilder.service.CustomerFieldServiceImpl;
import org.broadleafcommerce.admin.web.rulebuilder.service.FulfillmentGroupFieldServiceImpl;
import org.broadleafcommerce.admin.web.rulebuilder.service.OrderFieldServiceImpl;
import org.broadleafcommerce.admin.web.rulebuilder.service.OrderItemFieldServiceImpl;
import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class DataDTOToMVELTranslatorTest extends TestCase {

    private OrderItemFieldServiceImpl orderItemFieldService;
    private CustomerFieldServiceImpl customerFieldService;
    private OrderFieldServiceImpl orderFieldService;
    private FulfillmentGroupFieldServiceImpl fulfillmentGroupFieldService;

    @Override
    protected void setUp() {
        orderItemFieldService = new OrderItemFieldServiceImpl();
        orderItemFieldService.init();
        customerFieldService = new CustomerFieldServiceImpl();
        customerFieldService.init();
        orderFieldService = new OrderFieldServiceImpl();
        orderFieldService.init();
        fulfillmentGroupFieldService = new FulfillmentGroupFieldServiceImpl();
        fulfillmentGroupFieldService.init();
    }

    /**
     * Tests the creation of an MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * Here's an example of a DataWrapper with a single DataDTO
     *
     * [{"id":"100",
     *  "quantity":"1",
     *  "groupOperator":"AND",
     *  "groups":[
     *      {"id":null,
     *      "quantity":null,
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
        String mvel = "MvelHelper.toUpperCase(discreteOrderItem.?category.?name)==MvelHelper.toUpperCase(\"merchandise\")";
        assert(mvel.equals(translated));
    }

    /**
     * Tests the creation of a Customer Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"id":null,
     *  "quantity":null,
     *  "groupOperator":"AND",
     *  "groups":[
     *      {"id":null,
     *      "quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"emailAddress",
     *      "operator":"NOT_EQUAL_FIELD",
     *      "value":"username"},
     *      {"id":null,
     *      "quantity":null,
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

        //not currently supported
//        ExpressionDTO e1 = new ExpressionDTO();
//        e1.setName("emailAddress");
//        e1.setOperator(BLCOperator.NOT_EQUAL_FIELD.name());
//        e1.setValue("username");

        ExpressionDTO e2 = new ExpressionDTO();
        e2.setName("deactivated");
        e2.setOperator(BLCOperator.EQUALS.name());
        e2.setValue("true");

        //dataDTO.getGroups().add(e1);
        dataDTO.getGroups().add(e2);

        String translated = translator.createMVEL("customer", dataDTO, customerFieldService);
        String mvel = "customer.?deactivated==true";
        assert (mvel.equals(translated));
    }

    /**
     * Tests the creation of an Order Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"id":null,
     *  "quantity":null,
     *  "groupOperator":"AND",
     *  "groups":[
     *      {"id":null,
     *      "quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"subTotal",
     *      "operator":"GREATER_OR_EQUAL",
     *      "value":"100"},
     *      {"id":null,
     *      "quantity":null,
     *      "groupOperator":"OR",
     *      "groups":[
     *          {"id":null,
     *          "quantity":null,
     *          "groupOperator":null,
     *          "groups":null,
     *          "name":"currency.defaultFlag",
     *          "operator":"EQUALS",
     *          "value":"true"},
     *          {"id":null,
     *          "quantity":null,
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
        String mvel = "order.?subTotal.getAmount()>=100&&(order.?currency.?defaultFlag==true||order.?locale.?localeCode==\"my\")";
        assert (mvel.equals(translated));
    }

    /**
     * Tests the creation of an Item Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"id":100,
     *  "quantity":1,
     *  "groupOperator":"AND",
     *  "groups":[
     *      {"id":null,
     *      "quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"category.name",
     *      "operator":"EQUALS",
     *      "value":"test category"
     *      }]
     *  },
     *  {"id":"200",
     *  "quantity":2,
     *  "groupOperator":"NOT",
     *  "groups":[
     *      {"id":null,
     *      "quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"product.manufacturer",
     *      "operator":"EQUALS",
     *      "value":"test manufacturer"},
     *      {"id":null,
     *      "quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"product.model",
     *      "operator":"EQUALS",
     *      "value":"test model"
     *      }]
     *  }]
     */
    public void testItemQualificationMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();

        DataDTO d1 = new DataDTO();
        d1.setQuantity(1);
        d1.setGroupOperator(BLCOperator.AND.name());
        ExpressionDTO d1e1 = new ExpressionDTO();
        d1e1.setName("category.name");
        d1e1.setOperator(BLCOperator.EQUALS.name());
        d1e1.setValue("test category");
        d1.getGroups().add(d1e1);

        String d1Translated = translator.createMVEL("discreteOrderItem", d1, orderItemFieldService);
        String d1Mvel = "discreteOrderItem.?category.?name==\"test category\"";
        assert(d1Mvel.equals(d1Translated));

        DataDTO d2 = new DataDTO();
        d2.setQuantity(2);
        d2.setGroupOperator(BLCOperator.NOT.name());
        ExpressionDTO d2e1 = new ExpressionDTO();
        d2e1.setName("product.manufacturer");
        d2e1.setOperator(BLCOperator.EQUALS.name());
        d2e1.setValue("test manufacturer");
        ExpressionDTO d2e2 = new ExpressionDTO();
        d2e2.setName("product.model");
        d2e2.setOperator(BLCOperator.EQUALS.name());
        d2e2.setValue("test model");
        d2.getGroups().add(d2e1);
        d2.getGroups().add(d2e2);

        String d2Translated = translator.createMVEL("discreteOrderItem", d2, orderItemFieldService);
        String d2Mvel = "!(discreteOrderItem.?product.?manufacturer==\"test manufacturer\"&&discreteOrderItem.?product.?model==\"test model\")";
        assert (d2Mvel.equals(d2Translated));

    }

    /**
     * Tests the creation of a Fulfillment Group Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"id":null,
     *  "quantity":null,
     *  "groupOperator":"AND",
     *  "groups":[
     *      {"id":null,
     *      "quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"address.state.name",
     *      "operator":"EQUALS",
     *      "value":"Texas"},
     *      {"id":null,
     *      "quantity":null,
     *      "groupOperator":null,
     *      "groups":null,
     *      "name":"retailShippingPrice",
     *      "operator":"BETWEEN_INCLUSIVE",
     *      "start":"99",
     *      "end":"199"}]
     *  }]
     */
    public void testFulfillmentQualificationMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        DataDTO dataDTO = new DataDTO();
        dataDTO.setGroupOperator(BLCOperator.AND.name());

        ExpressionDTO e1 = new ExpressionDTO();
        e1.setName("address.state.name");
        e1.setOperator(BLCOperator.EQUALS.name());
        e1.setValue("Texas");

        ExpressionDTO e2 = new ExpressionDTO();
        e2.setName("retailFulfillmentPrice");
        e2.setOperator(BLCOperator.BETWEEN_INCLUSIVE.name());
        e2.setStart("99");
        e2.setEnd("199");

        dataDTO.getGroups().add(e1);
        dataDTO.getGroups().add(e2);

        String translated = translator.createMVEL("fulfillmentGroup", dataDTO, fulfillmentGroupFieldService);
        String mvel = "fulfillmentGroup.?address.?state.?name==\"Texas\"&&(fulfillmentGroup.?retailFulfillmentPrice.getAmount()>=99&&fulfillmentGroup.?retailFulfillmentPrice.getAmount()<=199)";
        assert (mvel.equals(translated));
    }
}
