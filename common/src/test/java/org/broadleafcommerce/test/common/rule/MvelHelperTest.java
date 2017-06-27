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
package org.broadleafcommerce.test.common.rule;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.RequestDTOImpl;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.rule.MvelHelper;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class MvelHelperTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(MvelHelperTest.class);

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

    /**
     * Confirms MVEL failure for special overloaded method case.
     * </p>
     * During compilation, if an mvel expression contains a method calls with params, mvel will attempt to identify a perfect
     * method signature match based on the types of the params. However, if the method is overloaded and one or more of
     * the params are null, mvel will not be able to 100% identify the right method version if the overloaded methods
     * have the same quantity of params and have type overlap. Take this example:
     * </p>
     * {@code SelectizeCollectionUtils#intersection(String param, Iterable param2);}
     * {@code SelectizeCollectionUtils#intersection(Iterable param, Iterable param2);}
     * </p>
     * If a rule is executed calling the intersection method and a null is passed in for the first parameter, mvel will
     * not know which method to pick, so it will end up picking one of them. This has proven to be undetermined at runtime,
     * which has made the problem even harder to identify. Furthermore, once the choice is made during compilation,
     * that compiled expression will utilize the "incorrect" choice going forward, which will cause the expression to
     * fail, even when neither param is null. In fact, in the case above, mvel will "coerce" the first param Iterable
     * instance into a String (i.e. flatten the list representation into a comma delimited String) in order to continue
     * calling the method identified during compilation. This causes the expression to permanently be in an incorrect
     * state from which it will never recover.
     */
    public void testMvelMethodOverloadFailureCase() throws IOException {
        //Test multiple iterations to make sure we hit the undetermined ordering case we need to confirm
        String output = "";
        for (int j=0; j<100; j++) {
            output = executeExternalJavaProcess(MvelOverloadFailureReproduction.class);
            boolean result = Boolean.valueOf(output.trim());
            if (result) {
                //We found the case. Exit the loop, since we don't need to try anymore.
                break;
            }
        }
        assertEquals("true", output);
    }
    
    /**
     * Confirms repeated success for method overload workaround in SelectizeCollectionUtils
     * </p>
     * See {@link #testMvelMethodOverloadFailureCase()} for a more complete description of the problem case.
     */
    public void testMvelMethodOverloadWorkaroundCase() throws IOException {
        //Test multiple iterations to make sure we no longer fail at all
        for (int j=0; j<20; j++) {
            String output = executeExternalJavaProcess(MvelOverloadWorkaroundReproduction.class);
            assertEquals("true", output);
        }
    }
    
    protected String executeExternalJavaProcess(Class<?> mainClass) throws IOException {
        String classpath = MvelTestUtils.getClassPath();
        
        //See javadoc on MvelOverloadFailureReproduction for description of why we need to execute the test in a new JVM
        CommandLine cmdLine = new CommandLine("java");
        cmdLine.addArgument("-cp");
        cmdLine.addArgument(classpath, true);
        cmdLine.addArgument(mainClass.getName());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Executor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler(baos));
        try {
            executor.execute(cmdLine, new HashMap<String, String>());
        } catch (IOException e) {
            throw new IOException(new String(baos.toByteArray()));
        }
        return new String(baos.toByteArray());
    }
}
