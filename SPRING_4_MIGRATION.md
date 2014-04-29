Requeired changes in application using BroadleafCommerce with spring 4
1. use servlet 3 api
             <dependency>
                 <groupId>javax.servlet</groupId>
-                <artifactId>servlet-api</artifactId>
-                <version>2.5</version>
+                <artifactId>javax.servlet-api</artifactId>
+                <version>3.0.1</version>
                 <type>jar</type>
                 <scope>provided</scope>
             </dependency>

2. use spring 4 and spring security 3.2 as dependencies

+        <spring.version>4.0.3.RELEASE</spring.version>
+        <spring.security.version>3.2.3.RELEASE</spring.security.version>

3. use spring security 3.2

-        http://www.springframework.org/schema/security/spring-security-3.1.xsd">
+        http://www.springframework.org/schema/security/spring-security-3.2.xsd">

4. MergeContextLoaderListener is removed and MergeContextLoader should be used directly instead in web.xml
     <listener>
-        <listener-class>org.broadleafcommerce.common.web.extensibility.MergeContextLoaderListener</listener-class>
+        <listener-class>org.broadleafcommerce.common.web.extensibility.MergeContextLoader</listener-class>
     </listener>

5. use themeleaf for spring 4

-<!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring3-3.dtd">
+<!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring4-4.dtd">
