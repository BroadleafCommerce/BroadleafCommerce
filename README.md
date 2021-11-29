## Introduction

BroadleafCommerce is an e-commerce framework written entirely in Java and leveraging the Spring framework. It is targeted at facilitating the development of enterprise-class, commerce-driven sites by providing a robust data model, services and specialized tooling that take care of most of the "heavy lifting" work. To accomplish this goal, we have developed our platform based on the key feature sets required by world-class online retailers - and we're committed to continually expanding our feature offering. We've also taken extra steps to guarantee interoperability with today's enterprise by utilizing standards wherever possible and incorporating best-of-breed, open-source software libraries.

See overview and features of Broadleaf at [www.broadleafcommerce.com](http://broadleafcommerce.com) 

## Editions

Broadleaf has a number of editions available commercially including a Marketplace Edition, B2B Edition, B2C Edition, and Multi-Teant Edition.   Each of these are described on the [Editions Page](https://www.broadleafcommerce.com/editions) of the Broadleaf website.

## Getting Started

Check out our [Getting Started guide](https://www.broadleafcommerce.com/docs/core/6.1/tutorials/getting-started-tutorials) to quickly kick off your Broadleaf-enabled website.

## License

Broadleaf Commerce core is released under a dual license format. It may be used under the terms of the Fair Use License 1.0 (http://license.broadleafcommerce.org/fair_use_license-1.0.txt) unless the restrictions on use therein are violated and require payment to Broadleaf, in which case the Broadleaf End User License Agreement (EULA), Version 1.1 (http://license.broadleafcommerce.org/commercial_license-1.1.txt) shall apply. Alternatively, the Commercial License may be replaced with a mutually agreed upon license between you and Broadleaf Commerce.


## Support

We also offer various levels of [enterprise support licenses](http://broadleafcommerce.com/support). Please [contact us](http://broadleafcommerce.com/contact) for information.

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


We also offer various levels of [enterprise support licenses](http://broadleafcommerce.com/support). Please [contact us](http://broadleafcommerce.com/contact) for information.
