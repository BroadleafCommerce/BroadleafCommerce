/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.rule;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;

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
        boolean result = MvelHelper.evaluateRule("BadFunction(xyz)", null);
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
}
