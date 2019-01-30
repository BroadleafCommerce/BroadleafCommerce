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
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELToDataWrapperTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;

import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class MVELToDataWrapperTranslatorTest extends TestCase {

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
        BroadleafRequestContext.getBroadleafRequestContext().setTimeZone(TimeZone.getDefault());
    }

    /**
     * Tests the creation of a DataWrapper given an mvel/quantity property
     * @throws MVELTranslationException
     */
    public void testCreateRuleData() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[3];
        Property mvelProperty = new Property();
        mvelProperty.setName("orderItemMatchRule");
        mvelProperty.setValue("MVEL.eval(\"toUpperCase()\",discreteOrderItem.?category.?name)==MVEL.eval(\"toUpperCase()\",\"merchandise\")");
        Property quantityProperty = new Property();
        quantityProperty.setName("quantity");
        quantityProperty.setValue("1");
        Property idProperty = new Property();
        idProperty.setName("id");
        idProperty.setValue("100");
        properties[0] = mvelProperty;
        properties[1] = quantityProperty;
        properties[2] = idProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "orderItemMatchRule", "quantity", "id", orderItemFieldService);
        assert(dataWrapper.getData().size() == 1);
        assert(dataWrapper.getData().get(0).getQuantity() == 1);
        assert(dataWrapper.getData().get(0).getRules().size()==1);
        assert(dataWrapper.getData().get(0).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO exp = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
        assert(exp.getId().equals("category.name"));
        assert(exp.getOperator().equals(BLCOperator.IEQUALS.name()));
        assert(exp.getValue().equals("merchandise"));
    }

    public void testCustomerQualificationDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[1];
        Property mvelProperty = new Property();
        mvelProperty.setName("matchRule");
        mvelProperty.setValue("customer.emailAddress!=customer.username");
        properties[0] = mvelProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "matchRule", null, null, customerFieldService);
        assert(dataWrapper.getData().size() == 1);
        assert(dataWrapper.getData().get(0).getQuantity() == null);
        assert(dataWrapper.getData().get(0).getCondition().equals(BLCOperator.AND.name()));

        assert(dataWrapper.getData().get(0).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO e1 = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
        assert(e1.getId().equals("emailAddress"));
        assert(e1.getOperator().equals(BLCOperator.NOT_EQUAL_FIELD.name()));
        assert(e1.getValue().equals("username"));

    }

    public void testOrderQualificationDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[1];
        Property mvelProperty = new Property();
        mvelProperty.setName("matchRule");
        mvelProperty.setValue("order.subTotal.getAmount()>=100");
        properties[0] = mvelProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "matchRule", null, null, orderFieldService);
        assert(dataWrapper.getData().size() == 1);
        assert(dataWrapper.getData().get(0).getQuantity() == null);

        assert(dataWrapper.getData().get(0).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO e1 = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
        assert(e1.getId().equals("subTotal"));
        assert(e1.getOperator().equals(BLCOperator.GREATER_OR_EQUAL.name()));
        assert(e1.getValue().equals("100"));

    }

    public void testItemQualificationDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] p1 = new Property[3];
        Property m1 = new Property();
        m1.setName("orderItemMatchRule");
        m1.setValue("discreteOrderItem.category.name==\"test category\"");
        Property q1 = new Property();
        q1.setName("quantity");
        q1.setValue("1");
        Property i1 = new Property();
        i1.setName("id");
        i1.setValue("100");
        p1[0] = m1;
        p1[1] = q1;
        p1[2] = i1;
        Entity e1 = new Entity();
        e1.setProperties(p1);

        Property[] p2 = new Property[3];
        Property m2 = new Property();
        m2.setName("orderItemMatchRule");
        m2.setValue("CollectionUtils.intersection(orderItem.?product.?manufacturer,[\"test manufacturer\"]).size()==0");
        Property q2 = new Property();
        q2.setName("quantity");
        q2.setValue("2");
        Property i2 = new Property();
        i2.setName("id");
        i2.setValue("200");
        p2[0] = m2;
        p2[1] = q2;
        p2[2] = i2;
        Entity e2 = new Entity();
        e2.setProperties(p2);

        Entity[] entities = new Entity[2];
        entities[0] = e1;
        entities[1] = e2;

        DataWrapper dataWrapper = translator.createRuleData(entities, "orderItemMatchRule", "quantity", "id", orderItemFieldService);

        assert(dataWrapper.getData().size() == 2);

        assert(dataWrapper.getData().get(0).getQuantity() == 1);
        assert(dataWrapper.getData().get(0).getCondition().equals(BLCOperator.AND.name()));
        assert(dataWrapper.getData().get(0).getRules().size()==1);
        assert(dataWrapper.getData().get(0).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO exp1 = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
        assert(exp1.getId().equals("category.name"));
        assert(exp1.getOperator().equals(BLCOperator.EQUALS.name()));
        assert(exp1.getValue().equals("test category"));

        assert(dataWrapper.getData().get(1).getQuantity() == 2);
        assert(dataWrapper.getData().get(1).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO expd1e1 = (ExpressionDTO) dataWrapper.getData().get(1).getRules().get(0);
        assert(expd1e1.getId().equals("product.manufacturer"));
        assert(expd1e1.getOperator().equals(BLCOperator.COLLECTION_NOT_IN.name()));
        assert(expd1e1.getValue().equals("[\"test manufacturer\"]"));

    }

    public void testNestedExpressionExceptionForItemQualificationDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] p1 = new Property[3];
        Property m1 = new Property();
        m1.setName("orderItemMatchRule");
        m1.setValue("discreteOrderItem.category.name==\"test category\"");
        Property q1 = new Property();
        q1.setName("quantity");
        q1.setValue("1");
        Property i1 = new Property();
        i1.setName("id");
        i1.setValue("100");
        p1[0] = m1;
        p1[1] = q1;
        p1[2] = i1;
        Entity e1 = new Entity();
        e1.setProperties(p1);

        Property[] p2 = new Property[3];
        Property m2 = new Property();
        m2.setName("orderItemMatchRule");
        m2.setValue("!(discreteOrderItem.product.manufacturer==\"test manufacturer\")");
        Property q2 = new Property();
        q2.setName("quantity");
        q2.setValue("2");
        Property i2 = new Property();
        i2.setName("id");
        i2.setValue("200");
        p2[0] = m2;
        p2[1] = q2;
        p2[2] = i2;
        Entity e2 = new Entity();
        e2.setProperties(p2);

        Entity[] entities = new Entity[2];
        entities[0] = e1;
        entities[1] = e2;

        DataWrapper dataWrapper = translator.createRuleData(entities, "orderItemMatchRule", "quantity", "id", orderItemFieldService);
        assert(dataWrapper.getError().equals(MVELToDataWrapperTranslator.SUB_GROUP_MESSAGE));

    }

    public void testFulfillmentGroupQualificationDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[1];
        Property mvelProperty = new Property();
        mvelProperty.setName("matchRule");
        mvelProperty.setValue("fulfillmentGroup.address.state.name==\"Texas\"&&fulfillmentGroup.retailFulfillmentPrice.getAmount()>=99&&fulfillmentGroup.retailFulfillmentPrice.getAmount()<=199");
        properties[0] = mvelProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "matchRule", null, null, fulfillmentGroupFieldService);

        assert(dataWrapper.getData().size() == 1);
        assert(dataWrapper.getData().get(0).getQuantity() == null);
        assert(dataWrapper.getData().get(0).getCondition().equals(BLCOperator.AND.name()));
        assert(dataWrapper.getData().get(0).getRules().size()==2);

        assert(dataWrapper.getData().get(0).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO e1 = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
        assert(e1.getId().equals("address.state.name"));
        assert(e1.getOperator().equals(BLCOperator.EQUALS.name()));
        assert(e1.getValue().equals("Texas"));

        assert(dataWrapper.getData().get(0).getRules().get(1) instanceof ExpressionDTO);
        ExpressionDTO e2 = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(1);
        assert(e2.getId().equals("retailFulfillmentPrice"));
        assert(e2.getOperator().equals(BLCOperator.BETWEEN_INCLUSIVE.name()));
        assert(e2.getValue().equals("[99,199]"));
    }

    public void testNestedExpressionExceptionForFulfillmentGroupQualificationDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[1];
        Property mvelProperty = new Property();
        mvelProperty.setName("matchRule");
        mvelProperty.setValue("fulfillmentGroup.address.state.name==\"Texas\"&&(fulfillmentGroup.retailFulfillmentPrice.getAmount()>=99&&fulfillmentGroup.retailFulfillmentPrice.getAmount()<=199)");
        properties[0] = mvelProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "matchRule", null, null, fulfillmentGroupFieldService);
        assert(dataWrapper.getError().equals(MVELToDataWrapperTranslator.SUB_GROUP_MESSAGE));
    }

    public void testItemQualificationCollectionDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] p1 = new Property[3];
        Property m1 = new Property();
        m1.setName("orderItemMatchRule");
        m1.setValue("CollectionUtils.intersection(discreteOrderItem.?category.?name,[\"test category\", \"test category 2\"]).size()>0&&discreteOrderItem.?quantity>5");
        Property q1 = new Property();
        q1.setName("quantity");
        q1.setValue("1");
        Property i1 = new Property();
        i1.setName("id");
        i1.setValue("100");
        p1[0] = m1;
        p1[1] = q1;
        p1[2] = i1;
        Entity e1 = new Entity();
        e1.setProperties(p1);

        Property[] p2 = new Property[3];
        Property m2 = new Property();
        m2.setName("orderItemMatchRule");
        m2.setValue("CollectionUtils.intersection(orderItem.?product.?manufacturer,[\"test manufacturer\"]).size()==0");
        Property q2 = new Property();
        q2.setName("quantity");
        q2.setValue("2");
        Property i2 = new Property();
        i2.setName("id");
        i2.setValue("200");
        p2[0] = m2;
        p2[1] = q2;
        p2[2] = i2;
        Entity e2 = new Entity();
        e2.setProperties(p2);

        Entity[] entities = new Entity[2];
        entities[0] = e1;
        entities[1] = e2;

        DataWrapper dataWrapper = translator.createRuleData(entities, "orderItemMatchRule", "quantity", "id", orderItemFieldService);

        assert(dataWrapper.getData().size() == 2);

        assert(dataWrapper.getData().get(0).getQuantity() == 1);
        assert(dataWrapper.getData().get(0).getCondition().equals(BLCOperator.AND.name()));
        assert(dataWrapper.getData().get(0).getRules().size()==2);
        assert(dataWrapper.getData().get(0).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO exp1 = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
        assert(exp1.getId().equals("category.name"));
        assert(exp1.getOperator().equals(BLCOperator.COLLECTION_IN.name()));
        assert(exp1.getValue().equals("[\"test category\", \"test category 2\"]"));

        assert(dataWrapper.getData().get(0).getRules().get(1) instanceof ExpressionDTO);
        ExpressionDTO exp2 = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(1);
        assert(exp2.getId().equals("quantity"));
        assert(exp2.getOperator().equals(BLCOperator.GREATER_THAN.name()));
        assert(exp2.getValue().equals("5"));

        assert(dataWrapper.getData().get(1).getQuantity() == 2);

        assert(dataWrapper.getData().get(1).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO expd1e1 = (ExpressionDTO) dataWrapper.getData().get(1).getRules().get(0);
        assert(expd1e1.getId().equals("product.manufacturer"));
        assert(expd1e1.getOperator().equals(BLCOperator.COLLECTION_NOT_IN.name()));
        assert(expd1e1.getValue().equals("[\"test manufacturer\"]"));

    }

    public void testNestedExpressionExceptionForItemQualificationCollectionDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] p1 = new Property[3];
        Property m1 = new Property();
        m1.setName("orderItemMatchRule");
        m1.setValue("CollectionUtils.intersection(discreteOrderItem.?category.?name,[\"test category\", \"test category 2\"]).size()>0&&discreteOrderItem.?quantity>5");
        Property q1 = new Property();
        q1.setName("quantity");
        q1.setValue("1");
        Property i1 = new Property();
        i1.setName("id");
        i1.setValue("100");
        p1[0] = m1;
        p1[1] = q1;
        p1[2] = i1;
        Entity e1 = new Entity();
        e1.setProperties(p1);

        Property[] p2 = new Property[3];
        Property m2 = new Property();
        m2.setName("orderItemMatchRule");
        m2.setValue("!(discreteOrderItem.product.manufacturer==\"test manufacturer\")");
        Property q2 = new Property();
        q2.setName("quantity");
        q2.setValue("2");
        Property i2 = new Property();
        i2.setName("id");
        i2.setValue("200");
        p2[0] = m2;
        p2[1] = q2;
        p2[2] = i2;
        Entity e2 = new Entity();
        e2.setProperties(p2);

        Entity[] entities = new Entity[2];
        entities[0] = e1;
        entities[1] = e2;

        DataWrapper dataWrapper = translator.createRuleData(entities, "orderItemMatchRule", "quantity", "id", orderItemFieldService);
        assert(dataWrapper.getError().equals(MVELToDataWrapperTranslator.SUB_GROUP_MESSAGE));

    }

    public void testBetweenDatesDataWrapper() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] p1 = new Property[1];
        Property m1 = new Property();
        m1.setName("matchRule");
        m1.setValue("(MvelHelper.convertField(\"DATE\",customer.?getCustomerAttributes()[\"invoice_date\"])>MvelHelper" +
                ".convertField(\"DATE\",\"2017.10.14 16:38:00 -0500\")&&MvelHelper.convertField(\"DATE\",customer" +
                ".?getCustomerAttributes()[\"invoice_date\"])<MvelHelper.convertField(\"DATE\"," +
                "\"2017.10.16 16:38:00 -0500\"))&&(MvelHelper.convertField(\"DATE\",customer.?getCustomerAttributes()" +
                "[\"invoice_date\"])>=MvelHelper.convertField(\"DATE\",\"2017.10.24 16:39:00 -0500\")&&MvelHelper" +
                ".convertField(\"DATE\",customer.?getCustomerAttributes()[\"invoice_date\"])<=MvelHelper" +
                ".convertField(\"DATE\",\"2017.10.25 16:40:00 -0500\"))");
        Property q1 = new Property();
        q1.setName("quantity");
        q1.setValue("1");
        Property i1 = new Property();
        i1.setName("id");
        i1.setValue("100");
        p1[0] = m1;

        Entity e1 = new Entity();
        e1.setProperties(p1);

        Entity[] entities = new Entity[1];
        entities[0] = e1;

        customerFieldService.getFields().add(new FieldData.Builder()
                .label("Customer - invoice date")
                .name("getCustomerAttributes()---invoice_date")
                .operators(RuleOperatorType.DATE)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.DATE)
                .build());

        DataWrapper dataWrapper = translator.createRuleData(entities, "matchRule", null, null, customerFieldService);

        customerFieldService.init();

        assert(dataWrapper.getData().get(0).getRules().size() == 2);

    }

    public void testInBetweenRuleOrderLessThenAndGreaterThenAndCurrency() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[3];
        Property mvelProperty = new Property();
        mvelProperty.setName("orderMatchRule");
        mvelProperty.setValue("order.?subTotal.getAmount()<75&&order.?subTotal.getAmount()>45&&order.?currency.?currencyCode==\"USD\"");
        Property quantityProperty = new Property();
        quantityProperty.setName("quantity");
        quantityProperty.setValue("1");
        Property idProperty = new Property();
        idProperty.setName("id");
        idProperty.setValue("100");
        properties[0] = mvelProperty;
        properties[1] = quantityProperty;
        properties[2] = idProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "orderMatchRule", "quantity", "id", orderFieldService);
        assert(dataWrapper.getData().size() == 1);
        assert(dataWrapper.getData().get(0).getQuantity() == 1);
        assert(dataWrapper.getData().get(0).getRules().size()==2);
        assert(dataWrapper.getData().get(0).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO exp = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
        assert(exp.getId().equals("subTotal"));
        assert(exp.getOperator().equals(BLCOperator.BETWEEN.name()));
        assert(exp.getValue().equals("[45,75]"));
    }

    public void testInBetweenRuleOrderGreaterThenAndLessThenAndCurrency() throws MVELTranslationException {
        MVELToDataWrapperTranslator translator = new MVELToDataWrapperTranslator();

        Property[] properties = new Property[3];
        Property mvelProperty = new Property();
        mvelProperty.setName("orderMatchRule");
        mvelProperty.setValue("order.?subTotal.getAmount()>45&&order.?subTotal.getAmount()<75&&order.?currency.?currencyCode==\"USD\"");
        Property quantityProperty = new Property();
        quantityProperty.setName("quantity");
        quantityProperty.setValue("1");
        Property idProperty = new Property();
        idProperty.setName("id");
        idProperty.setValue("100");
        properties[0] = mvelProperty;
        properties[1] = quantityProperty;
        properties[2] = idProperty;
        Entity[] entities = new Entity[1];
        Entity entity = new Entity();
        entity.setProperties(properties);
        entities[0] = entity;

        DataWrapper dataWrapper = translator.createRuleData(entities, "orderMatchRule", "quantity", "id", orderFieldService);
        assert(dataWrapper.getData().size() == 1);
        assert(dataWrapper.getData().get(0).getQuantity() == 1);
        assert(dataWrapper.getData().get(0).getRules().size() == 2);
        assert(dataWrapper.getData().get(0).getRules().get(0) instanceof ExpressionDTO);
        ExpressionDTO exp = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
        assert(exp.getId().equals("subTotal"));
        assert(exp.getOperator().equals(BLCOperator.BETWEEN.name()));
        assert(exp.getValue().equals("[45,75]"));
    }

}
