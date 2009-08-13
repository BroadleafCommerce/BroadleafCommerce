Although Spring allows you to configure a datasource inside the Spring
configuration it is generally best practice to configure things like this at
the server level as there is sensitive database password information.  Using
JNDI to look up the data source is a common practice.  This allows you to
promote the war file from environment to environment without having to rebuild
or repackage with new database configuration files.

JNDI is standard across all Java applications.  However, the configuration of
JNDI and Data Sources is server specific.  In order to configure a datasource
for Tomcat, you may configure it in a Tomcat Context, or in a global (x-Context)
 configuration.

For simplicity, we will configure the datasource for this application in a
global context:

1. Copy $CATALINA_HOME/conf/server.xml to $CATALINA_HOME/conf/server.bak
2. In the server.xml file, near the top of the file is an element called
   <GlobalNamingResources>
3. Add the following configuration inside this element:

<!-- Datasource connection for the application -->
<Resource name="jdbc/Broadleaf"
  auth="Container"
  type="javax.sql.DataSource"
  username="broadleaf"
  password="broadleaf"
  driverClassName="com.mysql.jdbc.Driver"
  url="jdbc:mysql://localhost:3306/Broadleaf?autoReconnect=true&amp; \n
          useUnicode=true&amp;characterEncoding=UTF8"
  maxActive="100" maxIdle="30" maxWait="10000"/>

(You must replace the username, password, driver, and URL depending on the
 environment and whether you are pointing to a MySql or SQL Server database.
 These settings should work for dev.  Please be sure to configure you dev
 database this way.)

4. Copy $CATALINA_HOME/conf/context.xml to $CATALINA_HOME/conf/context.bak
5. Edit $CATALINA_HOME/conf/context.xml
6. Add the following configuration to the context.xml file under the <Context>
   element

<!-- DataSource link for the application -->
<ResourceLink
  global="jdbc/Broadleaf"
  name="jdbc/Broadleaf"
  type="javax.sql.DataSource"/>