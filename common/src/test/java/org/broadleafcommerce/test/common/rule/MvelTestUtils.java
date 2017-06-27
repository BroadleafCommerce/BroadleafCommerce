/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.apache.commons.collections.map.MultiValueMap;
import org.broadleafcommerce.common.rule.SelectizeCollectionUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Introduce some common testing util methods here.
 *
 * @see MvelOverloadFailureReproduction
 * @see MvelOverloadWorkaroundReproduction
 * @author Jeff Fischer
 */
public class MvelTestUtils {

    public static void exerciseFailure() {
        System.setProperty("mvel2.disable.jit", "true");
        String rule = "CollectionUtils.intersection(level1.level2.getMultiValueSkuAttributes()[\"TEST-VALID\"],[\"TEST-VALID\"]).size()>0";
        ParserContext context = new ParserContext();
        context.addImport("CollectionUtils", MvelTestOverloadUtils.class);
        Serializable exp = MVEL.compileExpression(rule, context);
        executeTestCase(exp, "TEST-INVALID");
        boolean response = executeTestCase(exp, "TEST-VALID");
        if (!response) {
            //We received the expected, corrupted expression state, so return true to validate the expected test results
            System.out.print("true");
        } else {
            //We did not receive the expected, corrupted expression state. This can happen sometimes, since the ordering of methods
            //returned from the call to Class#getMethods for SelectizeCollectionUtilsTest is undetermined. Return false
            //since we did not validate the expected test results in this run.
            System.out.print("false");
        }
    }

    public static void exerciseWorkaround() {
        System.setProperty("mvel2.disable.jit", "true");
        String rule = "CollectionUtils.intersection(level1.level2.getMultiValueSkuAttributes()[\"TEST-VALID\"],[\"TEST-VALID\"]).size()>0";
        ParserContext context = new ParserContext();
        context.addImport("CollectionUtils", SelectizeCollectionUtils.class);
        Serializable exp = MVEL.compileExpression(rule, context);
        executeTestCase(exp, "TEST-INVALID");
        boolean response = executeTestCase(exp, "TEST-VALID");
        if (!response) {
            //With the workaround, we should never get here
            System.out.print("false");
        } else {
            //The expression should never be corrupted now that we've removed the overloaded method (this is the workaround)
            System.out.print("true");
        }
    }

    private static boolean executeTestCase(Serializable exp, String val) {
        final Map multiValueMap = new MultiValueMap();
        multiValueMap.put(val, val);
        final Level2 level2 = new Level2() {
            @Override
            public Map getMultiValueSkuAttributes() {
                return multiValueMap;
            }
        };
        Level1 level1 = new Level1() {
            @Override
            public Level2 getLevel2() {
                return level2;
            }
        };
        Map parameters = new HashMap();
        parameters.put("level1", level1);

        return (Boolean) MVEL.executeExpression(exp, parameters);
    }

    public interface Level2 {
        Map<String, String> getMultiValueSkuAttributes();
    }

    public interface Level1 {
        Level2 getLevel2();
    }

    public static String getClassPath() throws IOException {
        ClassLoader cl = MvelTestUtils.class.getClassLoader();
        String classpath = null;
        StringBuilder buffer = new StringBuilder();
        Enumeration<URL> resources = cl.getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            Manifest manifest = new Manifest(resources.nextElement().openStream());
            Attributes main = manifest.getMainAttributes();
            String mainClass = main.getValue("Main-Class");
            if ("org.apache.maven.surefire.booter.ForkedBooter".equals(mainClass)) {
                String[] urls = main.getValue("Class-Path").split(" ");
                for (String url : urls) {
                    assembleClassPathElement(buffer, new URL(url));
                }
                classpath = cleanUpClassPathString(buffer);
                break;
            }
        }
        if (buffer.length() == 0) {
            for (URL url : ((URLClassLoader) cl).getURLs()) {
                assembleClassPathElement(buffer, url);
            }
            classpath = cleanUpClassPathString(buffer);
        }
        return classpath;
    }

    private static String cleanUpClassPathString(StringBuilder buffer) {
        String classpath;
        classpath = buffer.toString();
        int toIndex = classpath.lastIndexOf(System.getProperty("path.separator"));
        classpath = classpath.substring(0, toIndex);
        return classpath;
    }

    private static void assembleClassPathElement(StringBuilder buffer, URL val) {
        String path = val.getPath();
        path = path.replace("%20", " ");
        path = path.replace("%40", "@");
        buffer.append(path);
        buffer.append(System.getProperty("path.separator"));
    }
}
