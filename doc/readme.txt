Broadleaf Commerce, release %%version%%
--------------------------------------------------
http://www.broadleafcommerce.org

1. INTRODUCTION-

BroadleafCommerce is an open-source, e-commerce framework written entirely in Java. 
It is targeted at facilitating the development of enterprise-class, commerce-driven 
sites by providing a robust data model, services and specialized tooling that take care 
of most of the "heavy lifting" work. To accomplish this goal, we have developed our 
platform based on the key feature sets required by world-class online retailers - and 
we're committed to continually expanding our feature offering. We've also taken extra 
steps to guarantee interoperability with today's enterprise by utilizing standards wherever 
possible and incorporating best-of-breed, open-source software libraries from the community.

Spring Framework -- Spring is the enterprise Java platform on which BroadleafCommerce is based. 
It provides numerous features, including dependency injection and transaction control.

Security -- Spring Security provides a robust security framework for controlling authentication 
and authorization at both the code and page level and is utilized by BroadleafCommerce for access 
control.

Persistence -- JPA and Hibernate represent the BroadleafCommerce ORM infrastructure for 
controlling persistence of our rich domain model.

Asynchronous Messaging -- BroadleafCommerce achieves asynchronous processing of application 
messages via interaction with a modern JMS broker through Spring JMS.

Search -- Flexible domain search capabilities in BroadleafCommerce are provided through integration
with the popular Compass and Lucene projects.

Task Scheduling -- Scheduling of repetitive tasks in BroadleafCommerce is offered through the 
Quartz job scheduling system.

Email -- Email support is provided throughout the BroadleafCommerce framework in either synchronous 
or asynchronous (JMS) modes. Email presentation customization is achieved via Velocity template 
utilization. Full target email open and link click tracking is supported out-of-the-box.

Modular Design -- Important e-commerce touchpoints are embodied in the concept of BroadleafCommerce 
"Modules". A module can provide interaction with a credit card processor, or even a shipping provider. 
The USPS shipping support is a great example of the pluggable architecture BroadleafCommerce employs. 
Any number of custom modules may be developed and utilized with BroadleafCommerce.

Configurable Workflows -- Key areas in the e-commerce lifecycle are represented as configurable 
workflows. Implementors have full control over the keys steps in pricing and checkout, allowing 
manipulation of module ordering, overriding existing module behavior and custom module execution. 
Composite workflows are also supported to achieve more exotic, nested behavior.

Extendible Design -- BroadleafCommerce is designed from the ground-up with extensibility in mind. 
Almost every aspect of BroadleafCommerce can be overridden, added to or otherwise modified to enhance 
or change the default behavior to best fit your needs. This includes all of our services, data access 
objects and entities. Please refer to the extensibility section of our documentation.

Configuration Merging -- As an extra bonus to our extensibility model, we offer a custom merge facility 
for Spring configuration files. We minimize the BroadleafCommerce configuration semantics that an 
implementer must be aware of, allowing our users to focus on their own configuration particulars. 
BroadleafCommerce will intelligently merge its own configuration information with that provided by 
the implementer at runtime.

Runtime Configuration Management -- BroadleafCommerce exposes configurable properties for services, 
modules and other subsystems through JMX so that BroadleafCommerce administrators can alter application 
behavior without having to bring down the system.

Presentation Layer Support -- BroadleafCommerce also includes a number of pre-written SpringMVC 
controllers that help to speed development of the presentation layer of your own BroadleafCommerce-driven 
site.

QOS -- BroadleafCommerce also provides quality of service monitoring for modules (both custom and 
default modules) and provides support for several QOS handlers out-of-the-box: logging and email. 
Additional, custom QOS handlers may be added through our open API.

Promotion System -- BroadleafCommerce includes a highly-configurable system for including your pricing 
promotions. We provide several standard levels at which promotions may be applied: Order level, Order 
Item level and Fulfillment Group level. In addition, your promotion business rules are represented in 
a flexible and standardized way using the MVEL expression language.

PCI Considerations -- We have taken measures in the construction and design of BroadleafCommerce to 
help you achieve PCI compliance, should you decide to store and use sensitive customer financial 
account information. Payment account information is referenced separately, allowing you to segregate 
confidential data onto a separate, secure database platform. API methods have been added to allow 
inclusion of any PCI compliant encryption scheme. Also, verbose logging is included to track payment 
interaction history.

Admin Platform -- BroadleafCommerce includes a wholely extendible administrative application based
on Google Web Toolkit. Developers can continue to leverage the power of the Java programming and the
standard Object Oriented Programming practices of extension and override that they already enjoy
in the core BroadleafCommerce platform. The admin application also provides an easy-to-use interface
for catalog, order and customer functions and provides a robust, rule-driven environment for creating
and managing discount promotions.

Content Management -- BroadleafCommerce includes a robus content management system for creating and
managing static pages and content. We also include a powerful content targeting feature that allows
business users to dynamically drive the most appropriate content to users.

2. RELEASE INFORMATION

Broadleaf Commerce %%version%% requires JDK 1.5 (or above) to build and/or run.

Basic release contents (~7 MB):
* "dist" contains the Broadleaf Commerce binary jar files

Contents of the "-with-docs" distribution (~18 MB):
* "dist" contains the Broadleaf Commerce binary jar files, as well as corresponding source jars
* "docs" contains the Broadleaf Commerce API javadocs

Contents of the "-with-dependencies" distribution (~72 MB):
* "dist" contains the Broadleaf Commerce binary jar files, as well as corresponding source jars
* "docs" contains the Broadleaf Commerce API javadocs
* "lib" contains all third-party libraries needed for building the framework and/or running the demo
* "sql" contains DDL scripts for Broadleaf Commerce schema creation for a number of popular database platforms
* "src" contains the general Java source files for the framework

The "lib" directory is only included in the "-with-dependencies" download. The "-with-dependencies"
download is required to build and run the demo application. 

Latest info is available at the public website: http://www.broadleafcommerce.org
Project info at the SourceForge site: http://sourceforge.net/projects/broadleafcommerce

Broadleaf Commerce is released under the terms of the Apache Software License 2 (see license.txt).
All libraries included in the "-with-dependencies" download are subject to their respective licenses.
This product includes software developed by the Apache Software Foundation (http://www.apache.org).

3. DISTRIBUTION JAR FILES

The "dist" directory contains the following distinct jar files for use in applications. 

* "broadleaf-framework"
- Contents: Core Broadleaf Commerce framework classes
- Dependencies: broadleaf-profile

* "broadleaf-framework-web"
- Contents: Spring MVC controllers (and related items) supporting the core Broadleaf Commerce classes
- Dependencies: broadleaf-framework, broadleaf-profile, broadleaf-profile-web

* "broadleaf-profile"
- Contents: Broadleaf Commerce Customer profile related classes, utility classes, email, configuration merge

* "broadleaf-profile-web"
- Contents: Spring MVC controllers (and related items) supporting the core Broadleaf Commerce profile classes
- Dependencies: broadleaf-profile

* "broadleaf-usps"
- Contents: Broadleaf Commerce shipping modules and support classes for USPS
- Dependencies: broadleaf-framework, broadleaf-usps-schemas

* "broadleaf-usps-schemas"
- Contents: Compiled XMLBeans support for USPS shipping API

* "broadleaf-cybersource"
- Contents: Broadleaf Commerce payment modules and support classes for CyberSource
- Dependencies: broadleaf-framework, broadleaf-cybersource-api

* "broadleaf-cybersource-api"
- Contents: Compiled axis support for CyberSource API

* "broadleaf-open-admin-platform"
- Contents: The base framework for our administrative application platform - based on GWT

* "broadleaf-admin-module"
- Contents: The core modules and pages for the administrative application

* "broadleaf-contentmanagement-module"
- Contents: The modules and pages for the content management portion of the administrative application

4. WHERE TO START?

* Please refer to the Broadleaf Commerce documentation wiki for information on running the demonstration and developing applications on top of Broadleaf Commerce (http://www.broadleafcommerce.org/confluence/display/BLC15/Home).
