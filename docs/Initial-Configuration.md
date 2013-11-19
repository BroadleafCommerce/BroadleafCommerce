# Enabling the Custom Field Module 

## Steps to enable this module

**Step 1.** Add the following to the depedency management section of your **parent** `pom.xml`:

```xml
    <dependency>
        <groupId>com.broadleafcommerce</groupId>
        <artifactId>broadleaf-custom-field</artifactId>
        <version>1.1.0-SNAPSHOT</version>
        <type>jar</type>
        <scope>compile</scope>
    </dependency>
```

**Step 2.** Pull this depedency into your `core/pom.xml`:

```xml
    <dependency>
        <groupId>com.broadleafcommerce</groupId>
        <artifactId>broadleaf-custom-field</artifactId>
    </dependency>
```

**Step 3.** Include the necessary `patchConfigLocation` files in your `admin/web.xml`:
    
```xml
    classpath:/bl-custom-field-applicationContext.xml
    classpath:/bl-custom-field-admin-applicationContext.xml
```
>Note: These two lines should go before the `classpath:/applicationContext.xml` line

**Step 4.** Include the necessary `patchConfigLocation` file in your `site/web.xml`:
    
```xml
    classpath:/bl-custom-field-applicationContext.xml
```
>Note: This line should go before the `classpath:/applicationContext.xml` line


**Step 5.** Reference the additional required SQL file in `development-shared.properties` in the `blPU.hibernate.hbm2ddl.import_files` property:
    
```
    /config/bc/sql/load_custom_field_admin_security.sql
```
