/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.rule.SelectizeCollectionUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
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

    private static final Log LOG = LogFactory.getLog(MvelTestUtils.class);
    
    public static void exerciseFailure() {
        System.setProperty("mvel2.disable.jit", "true");
        String rule = "CollectionUtils.intersection(level1.level2.getMultiValueSkuAttributes()[\"TEST-VALID\"],[\"TEST-VALID\"]).size()>0";
        ParserContext context = new ParserContext();
        context.addImport("CollectionUtils", MvelTestOverloadUtils.class);
        Serializable exp = MVEL.compileExpression(rule, context);
        executeTestCase(exp, "TEST-INVALID");
        boolean response = executeTestCase(exp, "TEST-VALID");
        if (response) {
            //We did not receive the expected, corrupted expression state. This can happen sometimes, since the ordering of methods
            //returned from the call to Class#getMethods for SelectizeCollectionUtilsTest is undetermined. Return false
            //since we did not validate the expected test results in this run.
            System.out.print("false");
        } else {
            //We received the expected, corrupted expression state, so return true to validate the expected test results
            System.out.print("true");
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
        if (response) {
            //The expression should never be corrupted now that we've removed the overloaded method (this is the workaround)
            System.out.print("true");
        } else {
            //With the workaround, we should never get here
            System.out.print("false");
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
                URL location = MvelOverloadFailureReproduction.class.getProtectionDomain().getCodeSource().getLocation();
                String testClasses = location.getPath();
                if (testClasses.endsWith("/")) {
                    testClasses = testClasses.substring(0, testClasses.lastIndexOf("/"));
                }
                String root = testClasses.substring(0, testClasses.lastIndexOf("/")) + "/classes";
                String[] paths = main.getValue("Class-Path").split(" ");
                String maven = ApplicationContext.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                String str = ".m2";
                int endIndex = maven.indexOf(str);
                if(endIndex==-1){
                    str="m";
                    endIndex = maven.indexOf(str);
                }
                maven = maven.substring(0, endIndex);
                for (String path : paths) {
                    if (path.indexOf(str) > 0) {
                        path = path.substring(path.indexOf(str));
                    }
                    path = maven + path;
                    assembleClassPathElement(buffer, path);
                }
                classpath = cleanUpClassPathString(buffer);
                if(!classpath.contains(testClasses)){
                    classpath=testClasses+System.getProperty("path.separator")+root+System.getProperty("path.separator")+classpath;

                }

                break;
            }
        }
        if (buffer.length() == 0) {
            for (String path : getAllPaths()) {
                assembleClassPathElement(buffer, path);
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

    private static void assembleClassPathElement(StringBuilder buffer, String path) {
        path = path.replace("%20", " ");
        path = path.replace("%40", "@");
        buffer.append(path);
        buffer.append(System.getProperty("path.separator"));
    }

    private static String[] getAllPaths() {
        return System.getProperty("java.class.path")
                .split(System.getProperty("path.separator"));
    }

}
