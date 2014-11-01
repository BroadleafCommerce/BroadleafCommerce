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
package org.broadleafcommerce.common.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PomEvaluator {

    private static String SEPARATOR = "============================================================";
    
    private static Map<String, Category> knownLibraries = new HashMap<String,Category>();
    private static Map<LicenseType, List<Dependency>> licenseDependencyMap = new HashMap<LicenseType, List<Dependency>>();
    
    private static Category SPRING = new Category("Spring Framework", LicenseType.APACHE2, FrameworkType.GENERAL);
    private static Category HIBERNATE = new Category("Hibernate Framework", LicenseType.LGPL, FrameworkType.PERSISTENCE);
    private static Category GOOGLE = new Category("Google", LicenseType.APACHE2, FrameworkType.GENERAL);
    private static Category BROADLEAF_OPEN_SOURCE = new Category("Broadleaf Framework Open Source", LicenseType.APACHE2, FrameworkType.ECOMMERCE);
    private static Category BROADLEAF_COMMERCIAL = new Category("Broadleaf Framework Commercial", LicenseType.APACHE2, FrameworkType.ECOMMERCE);
    private static Category APACHE_FOUNDATION = new Category("Apache 2.0", LicenseType.APACHE2, FrameworkType.GENERAL);
    private static Category JAVAX = new Category("javax", LicenseType.JAVA_EXTENSION, FrameworkType.OTHER);
    private static Category THYMELEAF = new Category("thymeleaf", LicenseType.APACHE2, FrameworkType.UI);
    private static Category SLF4J = new Category("slfj", LicenseType.MIT, FrameworkType.LOGGING);
    private static Category LOG4J = new Category("log4j", LicenseType.APACHE2, FrameworkType.LOGGING);
    private static Category OTHER = new Category("Other", LicenseType.OTHER, FrameworkType.OTHER);
    private static Category YAHOO = new Category("Yahoo", LicenseType.YAHOO_YUI, FrameworkType.UI);
    
    // CODEHAUS is used by Apache and Spring Framework
    private static Category JACKSON = new Category("Codehaus Jackson Library", LicenseType.APACHE2, FrameworkType.XML, SPRING, APACHE_FOUNDATION);
    private static Category PLEXUS = new Category("Codehaus Plexus Library", LicenseType.APACHE2, FrameworkType.XML, SPRING, APACHE_FOUNDATION);
    private static Category ASM = new Category("OW2 ASM libraries", LicenseType.OW2, FrameworkType.GENERAL, APACHE_FOUNDATION, GOOGLE);
    private static Category CGLIB = new Category("CGLIB libraries", LicenseType.APACHE2, FrameworkType.GENERAL, SPRING, HIBERNATE);
    private static Category JERSEY = new Category("Jersey Libraries", LicenseType.LGPL, FrameworkType.XML);
    private static Category XSTREAM = new Category("Codehaus XML parsing library", LicenseType.XSTREAM_BSD, FrameworkType.XML);
    private static Category JODA_TIME = new Category("Date and time utilities", LicenseType.APACHE2, FrameworkType.UTILITY, APACHE_FOUNDATION);
    private static Category TRANSMORPH = new Category("Entropy Transmorph - SalesForce.com", LicenseType.APACHE2, FrameworkType.UTILITY);    
    private static Category QUARTZ = new Category("Teracotta Quartz", LicenseType.APACHE2, FrameworkType.SCHEDULER);
    private static Category EHCACHE = new Category("Teracotta ehCache", LicenseType.APACHE2, FrameworkType.CACHE);
    private static Category ANTLR = new Category("Antlr Runtime", LicenseType.ANTLR_BSD, FrameworkType.UTILITY, APACHE_FOUNDATION);
    private static Category ASPECTJ = new Category("Aspect J", LicenseType.ECLIPSE_PUBLIC, FrameworkType.GENERAL, SPRING);
    private static Category MVEL = new Category("MVEL rules evaluation", LicenseType.APACHE2, FrameworkType.RULES);
    private static Category ORO = new Category("ORO regular expressions", LicenseType.APACHE2, FrameworkType.RULES);
    private static Category JAVA_ASSIST = new Category("Java Assist", LicenseType.APACHE2, FrameworkType.GENERAL);
    private static Category ANTISAMMY = new Category("Anti-Sammy", LicenseType.ANTISAMMY_BSD, FrameworkType.GENERAL);
    
    


    private static void initializeKnownLibraries() {
        // Spring
        knownLibraries.put("org.springframework", SPRING);
        knownLibraries.put("org.springframework.security", SPRING);
        knownLibraries.put("org.springframework.social", SPRING);

        // Hibernate
        knownLibraries.put("org.hibernate", HIBERNATE);
        knownLibraries.put("org.hibernate.javax.persistence", HIBERNATE);

        // Broadleaf
        knownLibraries.put("org.broadleafcommerce", BROADLEAF_OPEN_SOURCE);
        knownLibraries.put("com.broadleafcommerce", BROADLEAF_COMMERCIAL);

        // Thymeleaf
        knownLibraries.put("org.thymeleaf", THYMELEAF);

        // JavaX
        knownLibraries.put("javax.xml.bind", JAVAX);
        knownLibraries.put("javax.mail", JAVAX);
        knownLibraries.put("javax.servlet", JAVAX);
        knownLibraries.put("javax.servlet.jsp", JAVAX);
        knownLibraries.put("javax.validation", JAVAX);
        knownLibraries.put("jstl", JAVAX);

        // Logging
        knownLibraries.put("org.slf4j", SLF4J);
        knownLibraries.put("log4j", LOG4J);

        // Apache
        knownLibraries.put("commons-validator", APACHE_FOUNDATION);
        knownLibraries.put("commons-collections", APACHE_FOUNDATION);
        knownLibraries.put("commons-beanutils", APACHE_FOUNDATION);
        knownLibraries.put("commons-cli", APACHE_FOUNDATION);
        knownLibraries.put("commons-fileupload", APACHE_FOUNDATION);
        knownLibraries.put("commons-dbcp", APACHE_FOUNDATION);
        knownLibraries.put("commons-codec", APACHE_FOUNDATION);
        knownLibraries.put("org.apache.commons", APACHE_FOUNDATION);
        knownLibraries.put("commons-lang", APACHE_FOUNDATION);
        knownLibraries.put("commons-digester", APACHE_FOUNDATION);
        knownLibraries.put("commons-logging", APACHE_FOUNDATION);
        knownLibraries.put("commons-pool", APACHE_FOUNDATION);
        knownLibraries.put("org.apache.geronimo.specs", APACHE_FOUNDATION);
        knownLibraries.put("org.apache.solr", APACHE_FOUNDATION);
        knownLibraries.put("org.apache.velocity", APACHE_FOUNDATION);
        knownLibraries.put("org.apache.xmlbeans", APACHE_FOUNDATION);
        knownLibraries.put("taglibs", APACHE_FOUNDATION);
        knownLibraries.put("jakarta-regexp", APACHE_FOUNDATION);
        knownLibraries.put("ant.ant", APACHE_FOUNDATION);

        // Google - Will retire
        knownLibraries.put("com.google.gwt", GOOGLE);
        knownLibraries.put("com.google.code.gwt-math", GOOGLE);
        knownLibraries.put("com.google.code.findbugs", GOOGLE);
        knownLibraries.put("net.sf.gwt-widget", GOOGLE);
        knownLibraries.put("com.google.guava", GOOGLE);

        // CodeHaus - JSON / XML processing
        knownLibraries.put("org.codehaus.jackson", JACKSON);
        knownLibraries.put("org.codehaus.plexus", PLEXUS);
        
        // ASM
        knownLibraries.put("asm", ASM);

        // CGLIB
        knownLibraries.put("cglib", CGLIB);

        // Jersey - used for REST services
        knownLibraries.put("com.sun.jersey", JERSEY);
        knownLibraries.put("com.sun.jersey.contribs", JERSEY);

        // XStream - used for REST services
        knownLibraries.put("com.thoughtworks.xstream", JERSEY);

        // Joda-Time
        knownLibraries.put("joda-time", JODA_TIME);

        // Cache
        knownLibraries.put("net.sf.jsr107cache", JAVAX);

        // Transmorph - Will retire with 3.0
        knownLibraries.put("net.sf.transmorph", TRANSMORPH);

        // Teracotta software
        knownLibraries.put("net.sf.ehcache", EHCACHE);
        knownLibraries.put("org.opensymphony.quartz", QUARTZ);

        // Antlr
        knownLibraries.put("org.antlr", ANTLR);

        // Aspect J
        knownLibraries.put("org.aspectj", ASPECTJ);

        // MVEL
        knownLibraries.put("org.mvel", MVEL);

        // ORO
        knownLibraries.put("oro", ORO);

        // Java Assist
        knownLibraries.put("org.javassist", JAVA_ASSIST);

        // OWASP
        knownLibraries.put("org.owasp.antisamy", ANTISAMMY);

        // OWASP
        knownLibraries.put("com.yahoo.platform.yui", YAHOO);

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        initializeKnownLibraries();
        BufferedReader br = null;
        
        try {
            String fileName = "/Users/brianpolster/blc-workspace/BroadleafCommerce/pom.xml";
            if (args.length > 0) {
                fileName = args[0];
            }

            br = new BufferedReader(new FileReader(fileName));

            forwardToTag("<dependencies>", br);

            List<Dependency> dependencies = populateDependencies(br);

            for (Dependency dependency : dependencies) {
                Category category = knownLibraries.get(dependency.groupId);
                if (category != null) {
                    category.dependencyList.add(dependency);
                    List<Dependency> licenseDependencyList = licenseDependencyMap.get(category.licenseType);
                    if (licenseDependencyList == null) {
                        licenseDependencyList = new ArrayList<Dependency>();
                        licenseDependencyList.add(dependency);
                        licenseDependencyMap.put(category.licenseType, licenseDependencyList);
                    }

                } else {

                    if (dependency.scope != null && (dependency.scope.equals("test") ||
                            dependency.scope.equals("provided"))) {
                        continue;
                    }
                    OTHER.dependencyList.add(dependency);
                }
            }

            Set<Category> categoryList = new HashSet<Category>(knownLibraries.values());

            System.out.println("Related Software Report\r");

            for (Category category : categoryList) {
                printOutDependencies(category, category.dependencyList);
            }

            if (OTHER.dependencyList.size() > 0) {
                printOutDependencies(OTHER, OTHER.dependencyList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void printOutDependencies(Category category, List<Dependency> dependencies) {
        List<String> dependencyNames = new ArrayList<String>();
        for (Dependency d : dependencies) {
            dependencyNames.add(d.toString());
        }
        
        Collections.sort(dependencyNames);

        System.out.println(category);
        System.out.println(SEPARATOR);
        
        for (String name : dependencyNames) {
            System.out.println(name);
        }
        System.out.println("Total count for category " + category.categoryName + ": " + dependencyNames.size() + "\r\r");
        
    }

    public static List<Dependency> populateDependencies(BufferedReader br) throws IOException {
        String currentLine;
        List<Dependency> dependencyList = new ArrayList<Dependency>();

        while (forwardToTag("<dependency", br)) {
            Dependency current = new Dependency();
            while ((currentLine = br.readLine()) != null) {
                if (currentLine.contains("</dependency>")) {
                    break;
                }
                current.scope = getTagValue("<scope>", currentLine, current.scope);
                current.groupId = getTagValue("<groupId>", currentLine, current.groupId);
                current.artifactId = getTagValue("<artifactId>", currentLine, current.artifactId);
                current.version = getTagValue("<version>", currentLine, current.version);
            }

            dependencyList.add(current);
        }
        return dependencyList;
    }

    public static String getTagValue(String tagName, String line, String currentValue) {
        int startPos = line.indexOf(tagName);
        if (startPos >= 0) {
            int endPos = line.indexOf("</", startPos + 1);
            if (endPos >= 0) {
                return line.substring(startPos + tagName.length(), endPos);
            }
        }
        return currentValue;
    }

    public static boolean forwardToTag(String tagName, BufferedReader br) throws IOException {
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            String lowerCaseLine = sCurrentLine.toLowerCase();
            if (lowerCaseLine.indexOf(tagName) >= 0) {
                return true;
            }
        }
        return false;
    }

    static class Dependency {

        String groupId;
        String artifactId;
        String version;
        String scope;
        List<Category> categoriesThatDependOnThis = new ArrayList<Category>();

        public String toString() {
            return groupId + "." + artifactId + "." + version + "  [" + scope + "]";
        }
    }

    static class LicenseType {

        private String name;
        private String url;

        public static LicenseType APACHE2 = new LicenseType("APACHE2", "http://www.apache.org/licenses/LICENSE-2.0.html");
        public static LicenseType LGPL = new LicenseType("LGPL", "http://www.gnu.org/licenses/lgpl-3.0.html, http://www.gnu.org/licenses/lgpl-2.1.html,");
        public static LicenseType MIT = new LicenseType("MIT", "http://opensource.org/licenses/MIT");
        public static LicenseType JAVA_EXTENSION = new LicenseType("JAVA_EXTENSION", "n/a");
        public static LicenseType OW2 = new LicenseType("OW2", "http://asm.ow2.org/license.html");
        public static LicenseType XSTREAM_BSD = new LicenseType("XSTREAM_BSD", "http://xstream.codehaus.org/license.html");
        public static LicenseType ANTLR_BSD = new LicenseType("ANTLR_BSD", "http://www.antlr.org/license.html");
        public static LicenseType ANTISAMMY_BSD = new LicenseType("ANTISAMMY_BSD", "http://opensource.org/licenses/bsd-license.php");
        public static LicenseType OTHER = new LicenseType("OTHER", "Unknown");
        public static LicenseType ECLIPSE_PUBLIC = new LicenseType("ECLIPSE PUBLIC", "http://www.eclipse.org/legal/epl-v10.html");
        public static LicenseType YAHOO_YUI = new LicenseType("YAHOO YUI", "http://yuilibrary.com/license/");

        public LicenseType(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String toString() {
            return name.toString() + ":" + url;
        }
    }

    static enum FrameworkType {
        PERSISTENCE,
        GENERAL,
        LOGGING,
        UI,
        XML,
        UTILITY,
        SCHEDULER,
        CACHE,
        RULES,
        ECOMMERCE,
        OTHER
    }        

    static class Category {

        String categoryName;
        LicenseType licenseType;
        FrameworkType frameworkType;
        List<Dependency> dependencyList = new ArrayList<Dependency>();
        Category[] usedByCategories;


        public Category(String categoryName, LicenseType type, FrameworkType frameworkType) {
            this.categoryName = categoryName;
            this.licenseType = type;
            this.frameworkType = frameworkType;
        }

        public Category(String categoryName, LicenseType type, FrameworkType frameworkType, Category... usedByCategories) {
            this(categoryName, type, frameworkType);
            this.usedByCategories = usedByCategories;
        }

        public String toString() {
            return "Category Name : " + categoryName +
                    "\rLicense Type : " + licenseType.name +
                    "\rLicense URL : " + licenseType.url;
        }
    }

}
