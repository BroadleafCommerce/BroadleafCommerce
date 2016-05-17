## Introduction

BroadleafCommerce is an open-source, e-commerce framework written entirely in Java on top of the Spring framework. It is targeted at facilitating the development of enterprise-class, commerce-driven sites by providing a robust data model, services and specialized tooling that take care of most of the "heavy lifting" work. To accomplish this goal, we have developed our platform based on the key feature sets required by world-class online retailers - and we're committed to continually expanding our feature offering. We've also taken extra steps to guarantee interoperability with today's enterprise by utilizing standards wherever possible and incorporating best-of-breed, open-source software libraries from the community.

## Getting Started

Check out our [Getting Started guide](http://docs.broadleafcommerce.org/current/Getting-Started.html) to quickly kick off your Broadleaf-enabled website.

## Support

Broadleaf commerce offers [commercial support and training](http://broadleafcommerce.com/services) which can also include professional services. Community support is offered through the [Broadleaf forums](http://forum.broadleafcommerce.org) and/or [our GitHub issues](https://github.com/BroadleafCommerce/BroadleafCommerce).

## Key Features and Technologies

### Spring Framework
Spring is the enterprise Java platform on which BroadleafCommerce is based.  It provides numerous features, including dependency injection and transaction control.

### Security
Spring Security provides a robust security framework for controlling authentication and authorization at both the code and page level and is utilized by BroadleafCommerce for access control.

### Persistence
JPA and Hibernate represent the BroadleafCommerce ORM infrastructure for 
controlling persistence of our rich domain model.

### Search
Flexible domain search capabilities in BroadleafCommerce are provided through integration
with Solr.

### Task Scheduling
Scheduling of repetitive tasks in BroadleafCommerce is offered through the 
Quartz job scheduling system.

### Email
Email support is provided throughout the BroadleafCommerce framework in either synchronous 
or asynchronous (JMS) modes. Email presentation customization is achieved via Thymeleaf templates.

### Modular Design
Important e-commerce touchpoints are embodied in the concept of BroadleafCommerce 
"Modules". A module can provide interaction with a credit card processor, or even a shipping provider. 
Any number of custom modules may be developed and utilized with BroadleafCommerce.

### Configurable Workflows
Key areas in the e-commerce lifecycle are represented as configurable 
workflows. Implementors have full control over the keys steps in pricing and checkout, allowing 
manipulation of module ordering, overriding existing module behavior and custom module execution. 
Composite workflows are also supported to achieve more exotic, nested behavior.

### Extendible Design
BroadleafCommerce is designed from the ground-up with extensibility in mind. 
Almost every aspect of BroadleafCommerce can be overridden, added to or otherwise modified to enhance 
or change the default behavior to best fit your needs. This includes all of our services, data access 
objects and entities. Please refer to the extensibility section of our documentation.

### Configuration Merging
As an extra bonus to our extensibility model, we offer a custom merge facility 
for Spring configuration files. We minimize the BroadleafCommerce configuration semantics that an 
implementer must be aware of, allowing our users to focus on their own configuration particulars. 
BroadleafCommerce will intelligently merge its own configuration information with that provided by 
the implementer at runtime.

### Presentation Layer Support
BroadleafCommerce also includes a number of pre-written Spring MVC 
controllers that help to speed development of the presentation layer of your own BroadleafCommerce-driven 
site.

### QOS
BroadleafCommerce also provides quality of service monitoring for modules (both custom and 
default modules) and provides support for several QOS handlers out-of-the-box: logging and email. 
Additional, custom QOS handlers may be added through our open API.

### Promotion System
BroadleafCommerce includes a highly-configurable system for including your pricing 
promotions. We provide several standard levels at which promotions may be applied: Order level, Order 
Item level and Fulfillment Group level. In addition, your promotion business rules are represented in 
a flexible and standardized way using the MVEL expression language.

### PCI Considerations
We have taken measures in the construction and design of BroadleafCommerce to 
help you achieve PCI compliance, should you decide to store and use sensitive customer financial 
account information. Payment account information is referenced separately, allowing you to segregate 
confidential data onto a separate, secure database platform. API methods have been added to allow 
inclusion of any PCI compliant encryption scheme. Also, verbose logging is included to track payment 
interaction history.

### Admin Platform
BroadleafCommerce includes a wholely extendible administrative application built with Spring MVC. The admin application also provides an easy-to-use interface
for catalog, order and customer functions and provides a robust, rule-driven environment for creating
and managing discount promotions.

### Admin Customization
BroadleafCommerce provides a robust set of admin presentation annotations that allow configuration of domain
class display and persistence semantics without touching any admin code. This provides an easy-to-consume approach
for introducing entity extensions and additional fields into the admin forms so that your business users can immediately
start to benefit. We also provide a full annotation or xml-based approach for overriding the admin config declared
inside BroadleafCommerce so that you can have an impact on our defaults. And for more advanced customizations, our admin
platform is based on Spring MVC, so your Spring knowledge will translate here as well when it comes to adding additional
controllers, and the like.

### Content Management
BroadleafCommerce includes a robust content management system for creating and
managing static pages and content. We also include a powerful content targeting feature that allows
business users to dynamically drive the most appropriate content to users.


## Local Framework Development

Broadleaf Commerce recommends JDK 1.7, but will build/run with JDK 1.6 or 1.7. At this time Broadleaf has not been tested with Java 8.

The easiest way to get the Broadleaf framework locally set up with the DemoSite is to clone this repository and execute a clean install via Maven:

```sh
mvn clean install
```

If you are using JRebel for development, add the Maven profile 'blc-development' to include the necessary rebel.xml files in the built jars:

```sh
mvn clean install -Pblc-development
```

Our integration tests take a while to execute so you might also want to temporarily skip tests:

```sh
mvn clean install -Pblc-development -DskipTests
```

> Note: all contributed code must have passing tests via Maven

If you need to use a specific version of Broadleaf, simply check out that version using either the branch or tag. All releases are tagged 'broadleaf-<version>' whereas all active development versions are tagged 'BroadleafCommerce-<major.minor>.x'. For instance, if you would like to actively develop on the Broadleaf 2.2 line in order for your changes to be in the next GA patch release of Broadleaf 2.2, you would check out the 'BroadleafCommerce-2.2.x' branch.

```sh
git clone git@github.com:BroadleafCommerce/BroadleafCommerce
cd BroadleafCommerce

# use 2.2.x-SNAPSHOT
git checkout BroadleafCommerce-2.2.x
mvn clean install -Pblc-development

# switch to a GA version
git checkout broadleaf-2.2.0-GA
mvn clean install -Pblc-development
```

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md)

## License

Broadleaf Commerce core is released under the terms of the Apache Software License 2 (see license.txt). However, various commercial modules that are also available (for instance, price list management) are released under a different commercial license. These are not included with the core Broadleaf framework.

We also offer various levels of [enterprise support licenses](http://broadleafcommerce.com/support). Please [contact us](http://broadleafcommerce.com/contact) for information.

## New Version

Our latest version is 5.0 and is licensed under a new dual license format. It may be used under the terms of the Fair Use License 1.0 (http://license.broadleafcommerce.org/fair_use_license-1.0.txt) unless the restrictions on use therein are violated and require payment to Broadleaf, in which case the Broadleaf End User License Agreement (EULA), Version 1.1 (http://license.broadleafcommerce.org/commercial_license-1.1.txt) shall apply. Alternatively, the Commercial License may be replaced with a mutually agreed upon license between you and Broadleaf Commerce.

To get started with the latest version of Broadleaf Commerce, please visit our getting started page on the web: http://www.broadleafcommerce.com/docs/core/current/getting-started
