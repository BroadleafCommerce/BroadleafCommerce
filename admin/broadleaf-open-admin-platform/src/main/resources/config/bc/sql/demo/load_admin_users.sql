--
-- The Archetype is configured with "hibernate.hbm2ddl.auto" value="create-drop" in "persistence.xml".
--
-- This will cause hibernate to populate the database when the application is started by processing the files that
-- were configured in the hibernate.hbm2ddl.import_files property.
--
-- This file is responsible for loading the BLC out-of-box Admin users.   You need at least one
-- admin setup in the system in order to use the BLC admin.   You can use this file initially and
-- then setup your own admin users, but you should always disable these default accounts in a real,
-- production system.
--

--
-- Superadmin User
--
INSERT INTO BLC_ADMIN_USER (ADMIN_USER_ID, EMAIL, LOGIN, NAME, PASSWORD, ACTIVE_STATUS_FLAG) VALUES (-1,'admin@yourdomain.com','admin','Administrator','admin', true);

--
-- Roles for sample admin users.
--
INSERT INTO BLC_ADMIN_USER_ROLE_XREF (ADMIN_ROLE_ID, ADMIN_USER_ID) VALUES (-1,-1);


--INSERT INTO BLC_ADMIN_USER_ROLE_XREF (ADMIN_ROLE_ID, ADMIN_USER_ID) VALUES (7,2);
