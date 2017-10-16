/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.rulebuilder;

import org.broadleafcommerce.admin.web.rulebuilder.service.CustomerFieldServiceImpl;
import org.broadleafcommerce.admin.web.rulebuilder.service.FulfillmentGroupFieldServiceImpl;
import org.broadleafcommerce.admin.web.rulebuilder.service.OrderFieldServiceImpl;
import org.broadleafcommerce.admin.web.rulebuilder.service.OrderItemFieldServiceImpl;
import org.broadleafcommerce.common.presentation.RuleOperatorType;
import org.broadleafcommerce.common.presentation.RuleOptionType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;

import junit.framework.TestCase;

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
     * [{"pk":"100",
     *  "quantity":"1",
     *  "condition":"AND",
     *  "rules":[
     *      {"pk":null,
     *      "quantity":null,
     *      "condition":null,
     *      "rules":null,
     *      "id":"category.name",
     *      "operator":"IEQUALS",
     *      "value":"merchandise"}]
     *  }]
     */
    public void testCreateMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        ExpressionDTO expressionDTO = new ExpressionDTO();
        expressionDTO.setId("category.name");
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
     * [{"pk":null,
     *  "quantity":null,
     *  "condition":"AND",
     *  "rules":[
     *      {"pk":null,
     *      "quantity":null,
     *      "condition":null,
     *      "rules":null,
     *      "id":"emailAddress",
     *      "operator":"NOT_EQUAL_FIELD",
     *      "value":"username"}]
     *  }]
     */
    public void testCustomerQualificationMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        DataDTO dataDTO = new DataDTO();
        dataDTO.setCondition(BLCOperator.AND.name());

        //not currently supported
//        ExpressionDTO e1 = new ExpressionDTO();
//        e1.setName("emailAddress");
//        e1.setOperator(BLCOperator.NOT_EQUAL_FIELD.name());
//        e1.setValue("username");

        // Not supported
//        ExpressionDTO e2 = new ExpressionDTO();
//        e2.setName("deactivated");
//        e2.setOperator(BLCOperator.EQUALS.name());
//        e2.setValue("true");

        //dataDTO.getGroups().add(e1);
//        dataDTO.getGroups().add(e2);

        // Not supported
//        String translated = translator.createMVEL("customer", dataDTO, customerFieldService);
//        String mvel = "customer.?deactivated==true";
//        assert (mvel.equals(translated));
    }

    /**
     * Tests the creation of an Order Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"pk":null,
     *  "quantity":null,
     *  "condition":"AND",
     *  "rules":[
     *      {"pk":null,
     *      "quantity":null,
     *      "condition":null,
     *      "rules":null,
     *      "id":"subTotal",
     *      "operator":"GREATER_OR_EQUAL",
     *      "value":"100"}]
     *  }]
     */
    public void testOrderQualificationMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        DataDTO dataDTO = new DataDTO();
        dataDTO.setCondition(BLCOperator.AND.name());

        ExpressionDTO expressionDTO = new ExpressionDTO();
        expressionDTO.setId("subTotal");
        expressionDTO.setOperator(BLCOperator.GREATER_OR_EQUAL.name());
        expressionDTO.setValue("100");
        dataDTO.getRules().add(expressionDTO);

        String translated = translator.createMVEL("order", dataDTO, orderFieldService);
        String mvel = "order.?subTotal.getAmount()>=100";
        assert (mvel.equals(translated));
    }

    /**
     * Tests the creation of an Item Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"pk":100,
     *  "quantity":1,
     *  "condition":"AND",
     *  "rules":[
     *      {"pk":null,
     *      "quantity":null,
     *      "condition":null,
     *      "rules":null,
     *      "id":"category.name",
     *      "operator":"EQUALS",
     *      "value":"test category"
     *      }]
     *  },
     *  {"pk":"200",
     *  "quantity":2,
     *  "condition":"NOT",
     *  "rules":[
     *      {"pk":null,
     *      "quantity":null,
     *      "condition":null,
     *      "rules":null,
     *      "id":"product.manufacturer",
     *      "operator":"EQUALS",
     *      "value":"test manufacturer"}]
     *  }]
     */
    public void testItemQualificationMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();

        DataDTO d1 = new DataDTO();
        d1.setQuantity(1);
        d1.setCondition(BLCOperator.AND.name());
        ExpressionDTO d1e1 = new ExpressionDTO();
        d1e1.setId("category.name");
        d1e1.setOperator(BLCOperator.EQUALS.name());
        d1e1.setValue("test category");
        d1.getRules().add(d1e1);

        String d1Translated = translator.createMVEL("discreteOrderItem", d1, orderItemFieldService);
        String d1Mvel = "discreteOrderItem.?category.?name==\"test category\"";
        assert(d1Mvel.equals(d1Translated));

        DataDTO d2 = new DataDTO();
        d2.setQuantity(1);
        d2.setCondition(BLCOperator.NOT.name());
        ExpressionDTO d2e1 = new ExpressionDTO();
        d2e1.setId("product.manufacturer");
        d2e1.setOperator(BLCOperator.EQUALS.name());
        d2e1.setValue("test manufacturer");
        d2.getRules().add(d2e1);

        String d2Translated = translator.createMVEL("discreteOrderItem", d2, orderItemFieldService);
        String d2Mvel = "!(discreteOrderItem.?product.?manufacturer==\"test manufacturer\")";
        assert (d2Mvel.equals(d2Translated));

    }

    /**
     * Tests the creation of a Fulfillment Group Qualification MVEL expression from a DataDTO
     * @throws MVELTranslationException
     *
     * [{"pk":null,
     *  "quantity":null,
     *  "condition":"AND",
     *  "rules":[
     *      {"pk":null,
     *      "quantity":null,
     *      "condition":null,
     *      "rules":null,
     *      "id":"address.state.name",
     *      "operator":"EQUALS",
     *      "value":["Texas"]},
     *      {"pk":null,
     *      "quantity":null,
     *      "condition":null,
     *      "rules":null,
     *      "id":"retailShippingPrice",
     *      "operator":"BETWEEN_INCLUSIVE",
     *      "value":"[99,199]"}]
     *  }]
     */
    public void testFulfillmentQualificationMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();
        DataDTO dataDTO = new DataDTO();
        dataDTO.setCondition(BLCOperator.AND.name());

        ExpressionDTO e1 = new ExpressionDTO();
        e1.setId("address.state.name");
        e1.setOperator(BLCOperator.EQUALS.name());
        e1.setValue("Texas");

        ExpressionDTO e2 = new ExpressionDTO();
        e2.setId("retailFulfillmentPrice");
        e2.setOperator(BLCOperator.BETWEEN_INCLUSIVE.name());
        e2.setValue("[99,199]");

        dataDTO.getRules().add(e1);
        dataDTO.getRules().add(e2);

        String translated = translator.createMVEL("fulfillmentGroup", dataDTO, fulfillmentGroupFieldService);
        String mvel = "fulfillmentGroup.?address.?state.?name==\"Texas\"&&(fulfillmentGroup.?retailFulfillmentPrice.getAmount()>=99&&fulfillmentGroup.?retailFulfillmentPrice.getAmount()<=199)";
        assert (mvel.equals(translated));
    }

    public void testItemQualificationCollectionMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();

        DataDTO d1 = new DataDTO();
        d1.setQuantity(1);
        d1.setCondition(BLCOperator.AND.name());
        ExpressionDTO d1e1 = new ExpressionDTO();
        d1e1.setId("category.name");
        d1e1.setOperator(BLCOperator.COLLECTION_IN.name());
        d1e1.setValue("[\"test category\", \"test category 2\"]");
        d1.getRules().add(d1e1);

        String d1Translated = translator.createMVEL("discreteOrderItem", d1, orderItemFieldService);
        String d1Mvel = "CollectionUtils.intersection(discreteOrderItem.?category.?name,[\"test category\", \"test category 2\"]).size()>0";
        assert(d1Mvel.equals(d1Translated));

    }

    public void testWithinDaysMVEL() throws MVELTranslationException {
        DataDTOToMVELTranslator translator = new DataDTOToMVELTranslator();

        DataDTO d1 = new DataDTO();
        d1.setQuantity(0);
        d1.setCondition(BLCOperator.AND.name());
        ExpressionDTO d1e1 = new ExpressionDTO();
        d1e1.setId("getCustomerAttributes()---invoice_date");
        d1e1.setOperator(BLCOperator.WITHIN_DAYS.name());
        d1e1.setValue("12");
        d1.getRules().add(d1e1);

        customerFieldService.getFields().add(new FieldData.Builder()
                .label("Customer - invoice date")
                .name("getCustomerAttributes()---invoice_date")
                .operators(RuleOperatorType.DATE)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.DATE)
                .build());

        String d1Translated = translator.createMVEL("customer", d1, customerFieldService);
        
        String d1Mvel = "(MvelHelper.convertField(\"DATE\",customer.?getCustomerAttributes()[\"invoice_date\"])" +
                ">MvelHelper.convertField(\"DATE\",MvelHelper.subtractFromCurrentTime(12))" +
                "&&MvelHelper.convertField(\"DATE\",customer.?getCustomerAttributes()[\"invoice_date\"])" +
                "<MvelHelper.convertField(\"DATE\",MvelHelper.currentTime()))";

        customerFieldService.init();
        
        assert(d1Mvel.equals(d1Translated));

    }

}
