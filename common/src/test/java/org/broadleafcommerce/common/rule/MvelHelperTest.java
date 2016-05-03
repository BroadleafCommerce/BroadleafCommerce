/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.rule;

import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.RequestDTOImpl;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class MvelHelperTest extends TestCase {


    /**
     * Test that a blank rule is true.
     */
    public void testBlankRule() {
        boolean result = MvelHelper.evaluateRule("", null);
        assertTrue(result);
    }

    /**
     * Test that a null rule is true.
     */
    public void testNullRule() {
        boolean result = MvelHelper.evaluateRule(null, null);
        assertTrue(result);
    }

    /**
     * Test rule with parse errors
     */
    public void testRuleWithParseErrors() {
        MvelHelper.setTestMode(true);
        boolean result = MvelHelper.evaluateRule("BadFunction(xyz)", null);
        MvelHelper.setTestMode(false);
        assertFalse(result);
    }

    /**
     * Test rule that evaluates to true
     */
    public void testRuleThatEvaluatesToTrue() {
        // Locale used as an illustrative domain class only.  Any object could have been used.
        Locale testLocale = new LocaleImpl();
        testLocale.setLocaleCode("US");

        Map parameters = new HashMap();
        parameters.put("locale", testLocale);

        boolean result = MvelHelper.evaluateRule("locale.localeCode == 'US'", parameters);
        assertTrue(result);
    }

    /**
     * Test rule that evaluates to true
     */
    public void testRuleThatEvaluatesToFalse() {
        // Locale used as an illustrative domain class only.  Any object could have been used.
        Locale testLocale = new LocaleImpl();
        testLocale.setLocaleCode("GB");

        Map parameters = new HashMap();
        parameters.put("locale", testLocale);

        boolean result = MvelHelper.evaluateRule("locale.localeCode == 'US'", parameters);
        assertFalse(result);
    }

    /**
     * Tests MVEL syntax for accessing request property map values.   
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testRequestMapProperty() {
        BroadleafRequestContext.setBroadleafRequestContext(new BroadleafRequestContext());
        RequestDTO dto = new RequestDTOImpl();
        dto.getProperties().put("blcSearchTerm", "hot");

        Map parameters = new HashMap();
        parameters.put("request", dto);

        // If the "key" property doesn't contain an underscore, the expression returns true
        boolean result = MvelHelper.evaluateRule("request.properties['blcSearchTerm'] == 'hot'", parameters);
        assertTrue(result);
    }
}
