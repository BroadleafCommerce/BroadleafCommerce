This is a practical demonstration of how a SpringCommerce framework user would
go about customizing the data access layer. The demonstration is broken into
2 parts:

1. A demonstration application showing the normal framework operation - without customization
2. A demonstration application showing the framework operation with customized data access components

PREREQUISITES

1. Eclipse
2. Running instance of MySQL 5.X

DEMO

  To execute the first demonstration, you must first create the demo database in MySQL. Please execute
the following SQL using your favorite SQL editor:

drop database if exists datademo;
create database datademo;

Next, you will launch the main application "DemoFramework.java" (located in the src-framework-perspective folder) from
your Eclipse environment. The application will create 2 tables in your new database: Person and Catalog. Note - 
these 2 tables are configured differently in the app. Person is configured for JPA via xml, while Catalog is
configured via annotations. This difference is meant to be merely instructional and has no direct meaning
to the functionality of the demo.

  To execute the second demonstration, execute the same database creation SQL again to refresh the database. Next,
you will launch the main application "DemoUser.java" (located in the src-user-perspective folder) from your Eclipse
environment. The application will create 4 tables in your database: Person, Catalog, SpecializedPerson and
ProprietaryCatalog. Person and Catalog remain empty and are created because generateDdl is enabled for the
autocreation of tables, even if they're not used. Under normal circumstances, generateDdl would not be turned
on and these extra tables would not be generated. 

  You will also notice the demo user app continues to show 2 different styles of configuration. 
SpecializedPerson is configured via xml while ProprietaryCatalog is configured via annotations. This is an 
important point. SpringCommerce users are able to customize data access using the full power of JPA and can
configure it using annotations, xml, or a mixture of both.

  In the demo user app, I've done several things to customize the data access:
  
1. I override the Dao implementations to customize the creation and retrieval of new custom domain objects.
2. I have new custom domain object implementations. These new objects inherit from the same Abstract objects
as their domain object counterparts in the default framework implementation. These new objects may
extend or override as necessary to achieve new table/column mapping combinations while still adhering to
the interface contract required by the framework.
3. I have new processors that override the default processors from the framework. Think of these processors
as a business rule processing pipeline. They are called by the service implementation and are responsible for
"doing" something with the domain object. By overriding the default processor, I'm able to achieve new behavior.
This concept becomes interesting with my custom domain objects (since an instance of the custom domain object
will be passed to my processor method). With my custom processor, I can evaluate new fields I may have added
to achieve some new result.

OUTSTANDING ISSUES

1. The XMLMerge third party library performs robust merges for elements, but is not very robust for attributes.
When a merge is performed, the attributes value from the patch will always replace the source attribute value when a 
matching attribute name is found. This is normally fine, but presents a problem when merging schemaLocations in
applicationContext files. Therefore, it is currently necessary to make sure that every applicationContext file
involved in a merge contains all the possible schemaLocations used. The desired behavior would be for the merge
to mix the schemaLocation values instead of performing a simple replace. To fix this issue, some new code should
be added to the XMLMergeMod project to account for this new functionality.

2. I'm not 100% on having the user override the Dao implementations from the framework for creation and retrieval.
It seems reasonable that under the circumstances where new information/fields are being used for a domain object,
the framework wouldn't know the proper way to construct the new object or what class to cast it to. Therefore, it
seemed correct that the user would control how these events take place. And since this stuff normally happens
in the Dao, it seemed like the logical choice. Still, it seems a bit invasive...

3. Currently, the Dao implementations must be specified in xml. If the @Repository annotation is used instead,
Spring will throw an exception since it doesn't know how to rectify two Dao beans with the same name.

