---------------------------------------
Data Model
---------------------------------------
   The SpringCommerce data model is a heavily modified version of the 
   osCommerce data model. To view and/or modify the SpringCommerce data model 
   in the form of an ERD, you will need to download and install a free Eclipse plugin.
   
   1. Go to the Azzurri website and follow the installation instruction
   for the Azzurri Clay plugin: http://www.azzurri.jp/eclipse/en/index.html.
   2. Once you've installed the plugin, you will be able to open the 
   springcommerce.clay file located in the sql directory of the project. This
   is a diagram-based representation of the database structure for the project.
   3. Azzurri Clay can also be used to export SQL to create the database structure. 
   This step has already been executed for the first time, which yielded the file
   springcommerce.sql in the sql directory.
   4. Whenever the diagram is updated, springcommerce.sql should be regenerated so that other
   developers working on the project can inherit changes and update their local
   database instance. During the generate sql step, make sure to click the next button
   in the generate sql dialog. A series of checkboxes are made available in the dialog after
   clicking next - make sure all checkboxes are selected in the top "generate" section.
   
   Please note - in the description for the database, I've listed the current missing
   pieces in the data model. These pieces will need to be flushed out as we move
   forward. However, things like orders, pricing, tax, profile, catalog are certainly
   represented.
   
--------------------------------------
Database
--------------------------------------
   The database we'll be using for development is MySQL 5.1. Please download a copy
   from the MySQL site and install locally on your laptop. With a database instance running
   locally, you will facilitate your own iterative development on this project. Once installed
   and running, you will be able to create the SpringCommerce database by executing the
   springcommerce.sql file against your MySQL database instance.
      
   1. Launch a sql editor (Squirrel SQL is a good one - http://www.squirrelsql.org) and create
   the SpringCommerce database using the following SQL:
      a) create database springcommerce;
   2. Copy and paste the contents of springcommerce.sql into the sql editor and execute the
   script. This will create all the tables and relationships for the database