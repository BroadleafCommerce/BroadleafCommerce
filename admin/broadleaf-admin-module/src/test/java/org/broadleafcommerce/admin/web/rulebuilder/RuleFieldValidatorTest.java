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
import org.broadleafcommerce.admin.web.rulebuilder.service.OrderItemFieldServiceImpl;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.RuleFieldValidator;
import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;
import org.broadleafcommerce.openadmin.web.rulebuilder.DataDTOToMVELTranslator;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.ExpressionDTO;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService;

import junit.framework.TestCase;

/**
 * Tests the checks {@link RuleFieldValidator} applies to the mvel translated from the rule builder
 */
public class RuleFieldValidatorTest extends TestCase {

    private TestRuleFieldValidator validator;
    private CustomerFieldServiceImpl customerFieldService;
    private OrderItemFieldServiceImpl orderItemFieldService;

    @Override
    protected void setUp() {
        validator = new TestRuleFieldValidator();
        customerFieldService = new CustomerFieldServiceImpl();
        customerFieldService.init();
        orderItemFieldService = new OrderItemFieldServiceImpl();
        orderItemFieldService.init();
    }

    public void testRuleBuilderRulesAreAllowed() throws MVELTranslationException {
        assertTrue(validator.isAllowed(translate("customer", customerFieldService, "emailAddress",
                BLCOperator.EQUALS, "test@test.com")));
        assertTrue(validator.isAllowedQuantityRule(translate("discreteOrderItem", orderItemFieldService,
                "category.name", BLCOperator.COLLECTION_IN, "[\"new\",\"clear\"]")));
        assertTrue(validator.isAllowed(null));
        assertTrue(validator.isAllowed(""));
    }

    /**
     * The proof of concept from the report, saved as a customer offer rule. The translator escapes the value, so it
     * is a harmless string literal, while the mvel it produced before the value was escaped is rejected.
     */
    public void testReportedPayload() throws MVELTranslationException {
        String payload = "x\"+Runtime.getRuntime().exec(new String[]{\"/bin/sh\",\"-c\",\"id > /tmp/broadleaf_http_rce.txt\"})+\"y";
        String mvel = translate("customer", customerFieldService, "emailAddress", BLCOperator.EQUALS, payload);
        assertEquals("?customer.?emailAddress==\"x\\\"+Runtime.getRuntime().exec(new String[]{\\\"/bin/sh\\\",\\\"-c\\\","
                + "\\\"id > /tmp/broadleaf_http_rce.txt\\\"})+\\\"y\"", mvel);
        assertTrue(validator.isAllowed(mvel));
        assertFalse(validator.isAllowed("?customer.?emailAddress==\"" + payload + "\""));
    }

    public void testStatementSeparatorIsRejected() throws MVELTranslationException {
        String mvel = translate("customer", customerFieldService, "loggedIn", BLCOperator.EQUALS,
                "true; java.lang.System.exit(0)");
        assertFalse(validator.isAllowed(mvel));
    }

    /**
     * Raw mvel is stored as is for new quantity based rules, so it must be checked too
     */
    public void testRawMvelIsChecked() {
        assertTrue(validator.isAllowed("?discreteOrderItem.?category.?name==\"merchandise\""));
        assertFalse(validator.isAllowed("Runtime.getRuntime().exec(\"id\") != null"));
    }

    /**
     * Quantity based rules are checked after the quotes are unescaped, the way they are stored
     */
    public void testQuantityRuleIsCheckedAsStored() {
        // safe as translated, but code once \" is unescaped to "
        String translated = "?discreteOrderItem.?category.?name==\"x\\\"+System.setProperty(\\\"a\\\",\\\"1\\\")+\\\"y\"";
        assertTrue(validator.isAllowed(translated));
        assertFalse(validator.isAllowedQuantityRule(translated));
    }

    private String translate(String entityKey, RuleBuilderFieldService fieldService, String field,
            BLCOperator operator, String value) throws MVELTranslationException {
        DataDTO group = new DataDTO();
        group.setCondition(BLCOperator.AND.name());
        ExpressionDTO expression = new ExpressionDTO();
        expression.setId(field);
        expression.setOperator(operator.name());
        expression.setValue(value);
        group.getRules().add(expression);
        return new DataDTOToMVELTranslator().createMVEL(entityKey, group, fieldService);
    }

    private static class TestRuleFieldValidator extends RuleFieldValidator {

        @Override
        public boolean isAllowed(String mvel) {
            return super.isAllowed(mvel);
        }

        @Override
        public boolean isAllowedQuantityRule(String mvel) {
            return super.isAllowedQuantityRule(mvel);
        }
    }

}
