--
-- The Sample Project is configured with "hibernate.hbm2ddl.auto" value="create-drop" in "persistence.xml".
--
-- This will cause hibernate to populate the database when the application is started by processing the files that
-- were configured in the hibernate.hbm2ddl.import_files property.
--
-- This file is responsible for creating the content structure which includes page-types (e.g. template
-- mappings) and structured-content-types (e.g. ad placement among other things).
--

--
-- Load Currencies:  Your site will need currencies defined in order to use price lists.
-- Currencies need to be defined before locale if they are using a currency code.
-- i18n standard abbreviations should be used.
--
INSERT INTO BLC_CURRENCY(CURRENCY_CODE, FRIENDLY_NAME, DEFAULT_FLAG) VALUES('USD', 'US Dollar', TRUE);

--
-- Load Locales:  Your site must have at least one Locale with DEFAULT_FLAG set to TRUE
-- You can have as many locales as you like. Currency can be set  to null if none have
-- been defined. i18n standard abbreviations should be used.
--
INSERT INTO BLC_LOCALE (LOCALE_CODE, DEFAULT_FLAG, FRIENDLY_NAME, CURRENCY_CODE) VALUES ('en_US', TRUE, 'English US', 'USD');
INSERT INTO BLC_LOCALE (LOCALE_CODE, DEFAULT_FLAG, FRIENDLY_NAME, CURRENCY_CODE) VALUES ('en', FALSE, 'English', 'USD');

--
-- The following items create page templates.   The key is to map a JSP template (TMPLT_PATH) to
-- a LOCALE_CODE.   In the example below, there is only one JSP template "basic".   The full
-- path to the template is .../WEB_INF/jsp/templates/basic.jsp.
--
INSERT INTO BLC_PAGE_TMPLT (PAGE_TMPLT_ID, TMPLT_NAME, TMPLT_DESCR, TMPLT_PATH) VALUES (1, 'Default Template', 'This template provides the default layout with header and footer surrounding the content and title. This template also includes featured products below the page content.', 'content/default') ;
INSERT INTO BLC_PAGE_TMPLT (PAGE_TMPLT_ID, TMPLT_NAME, TMPLT_DESCR, TMPLT_PATH) VALUES (2, 'Plain Template', 'This template provides a plain layout with header and footer surrounding the content and title.', 'content/plain') ;

--
-- Field groups define a list of dynamic fields.    Field groups can be associated with page
-- templates or structured content types.    The field group below defines two fields named
-- "body", which is a rich text edit field, and "title", which is a string field.
-- This will tell the Broadleaf admin how to generate the view for a given template
--
INSERT INTO BLC_FLD_GROUP(FLD_GROUP_ID, NAME, INIT_COLLAPSED_FLAG) VALUES (1, 'Content', FALSE);
INSERT INTO BLC_FLD_DEF(FLD_DEF_ID, NAME, FRIENDLY_NAME, FLD_TYPE, SECURITY_LEVEL, HIDDEN_FLAG, VLDTN_REGEX, VLDTN_ERROR_MSSG_KEY, MAX_LENGTH, COLUMN_WIDTH, TEXT_AREA_FLAG, ENUM_ID, ALLOW_MULTIPLES, FLD_GROUP_ID, FLD_ORDER) VALUES (3, 'title', 'Title', 'STRING', NULL, FALSE, null, null, null, '*', FALSE, NULL, FALSE, 1, 0);
INSERT INTO BLC_FLD_DEF(FLD_DEF_ID, NAME, FRIENDLY_NAME, FLD_TYPE, SECURITY_LEVEL, HIDDEN_FLAG, VLDTN_REGEX, VLDTN_ERROR_MSSG_KEY, MAX_LENGTH, COLUMN_WIDTH, TEXT_AREA_FLAG, ENUM_ID, ALLOW_MULTIPLES, FLD_GROUP_ID, FLD_ORDER) VALUES (2, 'body', 'Body', 'HTML', NULL, FALSE, null, null, null, '*', FALSE, NULL, FALSE, 1, 1);

--
-- Map both the english template to this field group.
--
INSERT INTO BLC_PGTMPLT_FLDGRP_XREF(PG_TMPLT_FLD_GRP_ID, PAGE_TMPLT_ID, FLD_GROUP_ID, GROUP_ORDER) VALUES (-100,1,1,0);
INSERT INTO BLC_PGTMPLT_FLDGRP_XREF(PG_TMPLT_FLD_GRP_ID, PAGE_TMPLT_ID, FLD_GROUP_ID, GROUP_ORDER) VALUES (-200,2,1,0);


-----------------------------------------------------------------------------------------------------------------------------------
-- Structured Content Step 1:   Create Structured Content Field Groups
-----------------------------------------------------------------------------------------------------------------------------------
-- Create Ad Fields - Defining the ad field group as a group of two fields: Image URL and Target URL
INSERT INTO BLC_FLD_GROUP(FLD_GROUP_ID, NAME, INIT_COLLAPSED_FLAG) VALUES (4, 'Ad Fields', FALSE);
INSERT INTO BLC_FLD_DEF(FLD_DEF_ID, NAME, FRIENDLY_NAME, FLD_TYPE, SECURITY_LEVEL, HIDDEN_FLAG, VLDTN_REGEX, VLDTN_ERROR_MSSG_KEY, MAX_LENGTH, COLUMN_WIDTH, TEXT_AREA_FLAG, ENUM_ID, ALLOW_MULTIPLES, FLD_GROUP_ID, FLD_ORDER) VALUES (7, 'imageUrl', 'Image URL', 'ASSET_LOOKUP', NULL, FALSE, null, null, 150, '*', FALSE, NULL, FALSE, 4, 0);
INSERT INTO BLC_FLD_DEF(FLD_DEF_ID, NAME, FRIENDLY_NAME, FLD_TYPE, SECURITY_LEVEL, HIDDEN_FLAG, VLDTN_REGEX, VLDTN_ERROR_MSSG_KEY, MAX_LENGTH, COLUMN_WIDTH, TEXT_AREA_FLAG, ENUM_ID, ALLOW_MULTIPLES, FLD_GROUP_ID, FLD_ORDER) VALUES (8, 'targetUrl', 'Target URL', 'STRING', NULL, FALSE, null, null, 150, '*', FALSE, NULL, FALSE, 4, 1);

-- Create HTML Fields - Defining the message field group as a single field called messageText
INSERT INTO BLC_FLD_GROUP(FLD_GROUP_ID, NAME, INIT_COLLAPSED_FLAG) VALUES (5, 'HTML Fields', FALSE);
INSERT INTO BLC_FLD_DEF(FLD_DEF_ID, NAME, FRIENDLY_NAME, FLD_TYPE, SECURITY_LEVEL, HIDDEN_FLAG, VLDTN_REGEX, VLDTN_ERROR_MSSG_KEY, MAX_LENGTH, COLUMN_WIDTH, TEXT_AREA_FLAG, ENUM_ID, ALLOW_MULTIPLES, FLD_GROUP_ID, FLD_ORDER) VALUES (10, 'htmlContent', 'HTML Content', 'HTML', NULL, FALSE, null, null, null, '*', FALSE, NULL, FALSE, 5, 0);

-- Create Message Fields - Defining the message field group as a single field called messageText
INSERT INTO BLC_FLD_GROUP(FLD_GROUP_ID, NAME, INIT_COLLAPSED_FLAG) VALUES (6, 'Message Fields', FALSE);
INSERT INTO BLC_FLD_DEF(FLD_DEF_ID, NAME, FRIENDLY_NAME, FLD_TYPE, SECURITY_LEVEL, HIDDEN_FLAG, VLDTN_REGEX, VLDTN_ERROR_MSSG_KEY, MAX_LENGTH, COLUMN_WIDTH, TEXT_AREA_FLAG, ENUM_ID, ALLOW_MULTIPLES, FLD_GROUP_ID, FLD_ORDER) VALUES (9, 'messageText', 'Message Text', 'STRING', NULL, FALSE, null, null, 150, '*', FALSE, NULL, FALSE, 6, 0);

-----------------------------------------------------------------------------------------------------------------------------------
-- Structured Content Step 2:   Create Templates.     The examples below create field templates for Ads, Messages, and HTML blocks.
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SC_FLD_TMPLT(SC_FLD_TMPLT_ID, NAME) VALUES(-1, 'Ad Template');
INSERT INTO BLC_SC_FLD_TMPLT(SC_FLD_TMPLT_ID, NAME) VALUES(-2, 'HTML Template');
INSERT INTO BLC_SC_FLD_TMPLT(SC_FLD_TMPLT_ID, NAME) VALUES(-3, 'Message Template');


-----------------------------------------------------------------------------------------------------------------------------------
-- Structured Content Step 3:   Add Field Groups to Templates
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SC_FLDGRP_XREF(BLC_SC_FLDGRP_XREF_ID, SC_FLD_TMPLT_ID, FLD_GROUP_ID, GROUP_ORDER) VALUES (-1,-1,4,0);
INSERT INTO BLC_SC_FLDGRP_XREF(BLC_SC_FLDGRP_XREF_ID, SC_FLD_TMPLT_ID, FLD_GROUP_ID, GROUP_ORDER) VALUES (-2,-2,5,0);
INSERT INTO BLC_SC_FLDGRP_XREF(BLC_SC_FLDGRP_XREF_ID, SC_FLD_TMPLT_ID, FLD_GROUP_ID, GROUP_ORDER) VALUES (-3,-3,6,0);

-----------------------------------------------------------------------------------------------------------------------------------
-- Structured Content Step 4:   Create Types (These represent areas on a page or general types:  e.g 'Homepage Banner Ad')
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SC_TYPE (SC_TYPE_ID, NAME, DESCRIPTION, SC_FLD_TMPLT_ID) VALUES (1, 'Homepage Banner Ad', NULL, -1);
INSERT INTO BLC_SC_TYPE (SC_TYPE_ID, NAME, DESCRIPTION, SC_FLD_TMPLT_ID) VALUES (2, 'Homepage Middle Promo Snippet', NULL, -2);
INSERT INTO BLC_SC_TYPE (SC_TYPE_ID, NAME, DESCRIPTION, SC_FLD_TMPLT_ID) VALUES (3, 'Homepage Featured Products Title', NULL, -3);
INSERT INTO BLC_SC_TYPE (SC_TYPE_ID, NAME, DESCRIPTION, SC_FLD_TMPLT_ID) VALUES (4, 'Right Hand Side Banner Ad', NULL, -1);
