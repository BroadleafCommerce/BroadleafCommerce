--
-- The Archetype is configured with "hibernate.hbm2ddl.auto" value="create-drop" in "persistence.xml".
--
-- This will cause hibernate to populate the database when the application is started by processing the files that
-- were configured in the hibernate.hbm2ddl.import_files property.
--
-- This file is responsible for initializing table sequences.   The numbers here need
-- to be greater than the ids that are inserted as part of load_catalog_data.sql (or other load files)
-- to prevent duplicate key exceptions.
--

--
-- Initialize SEQUENCE_GENERATOR values
--

INSERT INTO SEQUENCE_GENERATOR VALUES ('CustomFieldImpl',100);


