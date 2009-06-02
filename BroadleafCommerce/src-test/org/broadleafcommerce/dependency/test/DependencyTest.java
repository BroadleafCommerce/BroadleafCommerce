/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.dependency.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.broadleafcommerce.test.integration.BaseTest;
import org.objectweb.asm.ClassReader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test target packages to make sure they do not have dependencies on
 * improper classes. For example, service classes should not have dependencies
 * on HttpServletRequest. In this case, one could test the service package
 * against javax.servlet (which is the containing package for HttpServletRequest).
 * 
 * @author jfischer
 *
 */
public class DependencyTest extends BaseTest {

    private List<String> testPackages;
    private List<String> targetPackages;
    private List<String> acceptablePackages;

    @Override
    @BeforeClass
    public void setup() {
        super.setup();
        testPackages = new ArrayList<String>();
        targetPackages = new ArrayList<String>();
        acceptablePackages = new ArrayList<String>();

        testPackages.add("javax.servlet");
        testPackages.add("org.springframework.web");

        targetPackages.add("org.broadleafcommerce.catalog");
        targetPackages.add("org.broadleafcommerce.checkout");
        targetPackages.add("org.broadleafcommerce.common");
        targetPackages.add("org.broadleafcommerce.email");
        targetPackages.add("org.broadleafcommerce.inventory");
        targetPackages.add("org.broadleafcommerce.marketing");
        targetPackages.add("org.broadleafcommerce.offer");
        targetPackages.add("org.broadleafcommerce.order");
        targetPackages.add("org.broadleafcommerce.payment");
        targetPackages.add("org.broadleafcommerce.pricing");
        //targetPackages.add("org.broadleafcommerce.promotion");
        targetPackages.add("org.broadleafcommerce.rules");
        targetPackages.add("org.broadleafcommerce.search");
        targetPackages.add("org.broadleafcommerce.util");
        targetPackages.add("org.broadleafcommerce.workflow");
        targetPackages.add("org.broadleafcommerce.profile");

        acceptablePackages.add("org.broadleafcommerce.catalog.web");
        acceptablePackages.add("org.broadleafcommerce.email.web");
        acceptablePackages.add("org.broadleafcommerce.order.web");
        acceptablePackages.add("org.broadleafcommerce.profile.web");
    }

    @Test
    public void testDependencies() throws Exception {
        for(String targetPackage : targetPackages) {
            List<String> finalClasses = findCandidateClasses(targetPackage);
            validateDependencies(targetPackage, finalClasses);
        }
    }

    private void validateDependencies(String targetPackage, List<String> finalClasses) throws IOException {
        DependencyVisitor v = new DependencyVisitor();
        for (String clazz : finalClasses) {
            new ClassReader(clazz).accept(v, false);
        }

        Set<String> classPackages = v.getPackages();
        String[] classNames = classPackages.toArray(new String[classPackages.size()]);
        for (String className : classNames) {
            className = className.replace('/', '.');
            for (String testPackage : testPackages) {
                if (className.startsWith(testPackage) || testPackage.startsWith(className)) {
                    throw new RuntimeException("Improper dependency (" + className + ") found in package (" + targetPackage + ")");
                }
            }
        }
    }

    private List<String> findCandidateClasses(String targetPackage) throws ClassNotFoundException {
        List<String> classes = getClasses(targetPackage);
        List<String> finalClasses = new ArrayList<String>();
        /*
         * remove acceptable packages
         */
        for (String clazz : classes) {
            testPackage: {
            for (String acceptablePackage : acceptablePackages) {
                if (clazz.startsWith(acceptablePackage)) {
                    break testPackage;
                }
            }
            finalClasses.add(clazz);
        }
        }
        return finalClasses;
    }

    private List<String> getClasses(String pckgname) throws ClassNotFoundException {
        ArrayList<String> classes=new ArrayList<String>();
        try {
            URL url = DependencyVisitor.class.getResource('/'+pckgname.replace('.', '/'));
            if (url.getProtocol().equals("jar")) {
                String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                JarFile jar = new JarFile(jarPath);
                addClasses(jar, classes, pckgname);
            } else {
                File directory= new File(url.getFile());
                addClasses(directory, classes, pckgname);
            }
        } catch(Exception x) {
            throw new ClassNotFoundException(pckgname+" does not appear to be a valid package", x);
        }
        return classes;
    }

    private void addClasses(JarFile jar, List<String> classes, String pckgname) throws ClassNotFoundException {
        Enumeration<JarEntry> entries = jar.entries();
        while(entries.hasMoreElements()) {
            String name = entries.nextElement().getName().replace('/', '.');
            if (name.startsWith(pckgname) && name.endsWith(".class")) {
                classes.add(name.substring(0, name.length()-6));
            }
        }
    }

    private void addClasses(File directory, List<String> classes, String pckgname) throws ClassNotFoundException {
        File[] files=directory.listFiles();
        for(File file : files) {
            if(file.getName().endsWith(".class")) {
                String fileName = file.getName();
                classes.add(pckgname + "." + fileName.substring(0, fileName.length()-6));
            } else if (file.isDirectory()) {
                addClasses(file, classes, pckgname + "." + file.getName());
            }
        }
    }

}
