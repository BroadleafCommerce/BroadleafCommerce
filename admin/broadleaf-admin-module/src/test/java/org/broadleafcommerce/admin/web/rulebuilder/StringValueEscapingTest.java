/*-
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.rulebuilder;

import org.broadleafcommerce.admin.web.rulebuilder.service.CustomerFieldServiceImpl;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELToDataWrapperTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * Tests that single string values are escaped when translated to MVEL, so they cannot break out of their string
 * literal, and that they are restored when the MVEL is translated back for the rule builder.
 */
public class StringValueEscapingTest extends TestCase {

    private static final String PROPERTY_KEY = "blStringValueEscapingTest";

    private CustomerFieldServiceImpl customerFieldService;

    @Override
    protected void setUp() {
        customerFieldService = new CustomerFieldServiceImpl();
        customerFieldService.init();
        BroadleafRequestContext.getBroadleafRequestContext().setTimeZone(TimeZone.getDefault());
    }

    public void testQuotesAndBackslashesAreEscaped() throws MVELTranslationException {
        assertEquals("?customer.?emailAddress==\"x\\\"+System.exit(0)+\\\"y\"",
                translate(BLCOperator.EQUALS, "x\"+System.exit(0)+\"y"));
        assertEquals("?customer.?emailAddress==\"a\\\\b\"", translate(BLCOperator.EQUALS, "a\\b"));
        assertEquals("?customer.?emailAddress==\"x\\\\\"", translate(BLCOperator.EQUALS, "x\\"));
        assertEquals("?customer.?emailAddress==\"\\\"quoted\\\"\"", translate(BLCOperator.EQUALS, "\"quoted\""));
        assertEquals("org.apache.commons.lang3.StringUtils.contains(?customer.?emailAddress,\"a\\\"b\")",
                translate(BLCOperator.CONTAINS, "a\"b"));
        assertEquals("MvelHelper.toUpperCase(?customer.?emailAddress)==MvelHelper.toUpperCase(\"a\\\"b\")",
                translate(BLCOperator.IEQUALS, "a\"b"));
    }

    public void testValuesWithoutQuotesOrBackslashesAreUnchanged() throws MVELTranslationException {
        assertEquals("?customer.?emailAddress==\"test@test.com\"", translate(BLCOperator.EQUALS, "test@test.com"));
        assertEquals("([\"a\",\"b\"] contains ?customer.?emailAddress)", translate(BLCOperator.EQUALS, "[\"a\",\"b\"]"));
        assertEquals("?customer.?loggedIn==true", translate("loggedIn", BLCOperator.EQUALS, "true"));
    }

    /**
     * The proof of concept from the report, and splitting a value over two rules with a trailing backslash, must
     * produce a string comparison that MVEL evaluates without running any code.
     */
    public void testInjectedCodeIsNotExecuted() throws MVELTranslationException {
        String[] values = {
                "x\"+System.setProperty(\"" + PROPERTY_KEY + "\",\"1\")+\"y",
                "x\\\"+System.setProperty(\"" + PROPERTY_KEY + "\",\"1\")+\"y",
                "\"+System.setProperty('" + PROPERTY_KEY + "','1')+'';//"
        };
        try {
            for (String value : values) {
                System.clearProperty(PROPERTY_KEY);
                assertEquals(value, Boolean.TRUE, evaluate(translate(BLCOperator.EQUALS, value), value));
                assertNull(value, System.getProperty(PROPERTY_KEY));
            }

            DataDTO group = new DataDTO();
            group.setCondition(BLCOperator.AND.name());
            group.getRules().add(expression("emailAddress", BLCOperator.EQUALS, "x\\"));
            group.getRules().add(expression("firstName", BLCOperator.EQUALS, values[2]));
            String mvel = new DataDTOToMVELTranslator().createMVEL("customer", group, customerFieldService);
            System.clearProperty(PROPERTY_KEY);
            Map<String, Object> customer = new HashMap<>();
            customer.put("emailAddress", "x\\");
            customer.put("firstName", values[2]);
            assertEquals(mvel, Boolean.TRUE, MVEL.eval(mvel, singletonVariables(customer)));
            assertNull(mvel, System.getProperty(PROPERTY_KEY));
        } finally {
            System.clearProperty(PROPERTY_KEY);
        }
    }

    /**
     * The value shown in the rule builder must be the value that was entered
     */
    public void testValuesAreRestoredForTheRuleBuilder() throws MVELTranslationException {
        String[] values = {"x\"+System.exit(0)+\"y", "a\\b", "x\\", "\"quoted\"", "a\\\"b", "plain"};
        for (String value : values) {
            String mvel = translate(BLCOperator.EQUALS, value);
            Property property = new Property();
            property.setName("matchRule");
            property.setValue(mvel);
            Entity entity = new Entity();
            entity.setProperties(new Property[]{property});

            DataWrapper dataWrapper = new MVELToDataWrapperTranslator().createRuleData(
                    new Entity[]{entity}, "matchRule", null, null, customerFieldService);
            ExpressionDTO restored = (ExpressionDTO) dataWrapper.getData().get(0).getRules().get(0);
            assertEquals(mvel, value, restored.getValue());
        }
    }

    private String translate(BLCOperator operator, String value) throws MVELTranslationException {
        return translate("emailAddress", operator, value);
    }

    private String translate(String field, BLCOperator operator, String value) throws MVELTranslationException {
        DataDTO group = new DataDTO();
        group.setCondition(BLCOperator.AND.name());
        group.getRules().add(expression(field, operator, value));
        return new DataDTOToMVELTranslator().createMVEL("customer", group, customerFieldService);
    }

    private ExpressionDTO expression(String field, BLCOperator operator, String value) {
        ExpressionDTO expression = new ExpressionDTO();
        expression.setId(field);
        expression.setOperator(operator.name());
        expression.setValue(value);
        return expression;
    }

    private Object evaluate(String mvel, String emailAddress) {
        Map<String, Object> customer = new HashMap<>();
        customer.put("emailAddress", emailAddress);
        return MVEL.eval(mvel, singletonVariables(customer));
    }

    private Map<String, Object> singletonVariables(Map<String, Object> customer) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customer", customer);
        return variables;
    }

}
