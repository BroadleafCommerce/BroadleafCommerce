--
-- The Archetype is configured with "hibernate.hbm2ddl.auto" value="create-drop" in "persistence-mycompany.xml".
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
INSERT INTO SEQUENCE_GENERATOR VALUES ('CustomerId',1);
INSERT INTO SEQUENCE_GENERATOR VALUES ('OrderId',2);
INSERT INTO SEQUENCE_GENERATOR VALUES ('OrderItemId',9);
INSERT INTO SEQUENCE_GENERATOR VALUES ('FulfillmentGroupId',3);
INSERT INTO SEQUENCE_GENERATOR VALUES ('AddressId',3);
INSERT INTO SEQUENCE_GENERATOR VALUES ('PhoneId',1);
INSERT INTO SEQUENCE_GENERATOR VALUES ('FulfillmentGroupItemId',1);
INSERT INTO SEQUENCE_GENERATOR VALUES ('PersonalMessageId',1);
INSERT INTO SEQUENCE_GENERATOR VALUES ('RatingSummaryImpl',10);
INSERT INTO SEQUENCE_GENERATOR VALUES ('RatingDetailImpl',10);
INSERT INTO SEQUENCE_GENERATOR VALUES ('ReviewDetailImpl',10);
INSERT INTO SEQUENCE_GENERATOR VALUES ('MediaId',1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('CategoryImpl',10000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('ProductImpl',1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('CodeTypeId',100);
INSERT INTO SEQUENCE_GENERATOR VALUES ('OfferImpl',500);
INSERT INTO SEQUENCE_GENERATOR VALUES ('OfferCodeImpl',5);
INSERT INTO SEQUENCE_GENERATOR VALUES ('CategoryProductImpl',500);
INSERT INTO SEQUENCE_GENERATOR VALUES ('SearchInterceptImpl',1);
INSERT INTO SEQUENCE_GENERATOR VALUES ('SkuImpl',10000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('FeaturedProductImpl',5);
INSERT INTO SEQUENCE_GENERATOR VALUES ('CrossSaleProductImpl',5);
INSERT INTO SEQUENCE_GENERATOR VALUES ('UpSaleProductImpl',5);
INSERT INTO SEQUENCE_GENERATOR VALUES ('PageImpl', 1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('FieldGroupImpl', 1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('FieldDefinitionImpl', 1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('PageFieldImpl', 1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('FieldEnumerationImpl', 1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('FieldEnumerationItemImpl', 1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('StructuredContentImpl', 1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('StructuredContentFieldImpl', 1000);
INSERT INTO SEQUENCE_GENERATOR VALUES ('SandBoxImpl', 100);
INSERT INTO SEQUENCE_GENERATOR VALUES ('AdminUserImpl',100);
INSERT INTO SEQUENCE_GENERATOR VALUES ('AdminRoleImpl',100);
INSERT INTO SEQUENCE_GENERATOR VALUES ('AdminPermissionImpl',100);
INSERT INTO SEQUENCE_GENERATOR VALUES ('ProductAttributeImpl',1000);
