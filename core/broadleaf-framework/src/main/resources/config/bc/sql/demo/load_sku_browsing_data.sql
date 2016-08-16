--
-- The Archetype is configured with "hibernate.hbm2ddl.auto" value="create-drop" in "persistence.xml".
--
-- This will cause hibernate to populate the database when the application is started by processing the files that
-- were configured in the hibernate.hbm2ddl.import_files property.
--
-- This file is responsible for updating the the catalog data so that it can be used for sku browsing.
--
-- The following is only used when indexing and searching by sku rather than by product.
--

UPDATE BLC_FIELD SET ENTITY_TYPE = 'SKU', PROPERTY_NAME = 'product.manufacturer' WHERE FIELD_ID = 1;
UPDATE BLC_FIELD SET ENTITY_TYPE = 'SKU', PROPERTY_NAME = 'skuAttributes(heatRange).value' WHERE FIELD_ID = 2;
UPDATE BLC_FIELD SET ENTITY_TYPE = 'SKU', PROPERTY_NAME = 'retailPrice' WHERE FIELD_ID = 3;
UPDATE BLC_FIELD SET ENTITY_TYPE = 'SKU', PROPERTY_NAME = 'name' WHERE FIELD_ID = 4;
UPDATE BLC_FIELD SET ENTITY_TYPE = 'SKU', PROPERTY_NAME = 'product.model' WHERE FIELD_ID = 5;
UPDATE BLC_FIELD SET ENTITY_TYPE = 'SKU', PROPERTY_NAME = 'description' WHERE FIELD_ID = 6;
UPDATE BLC_FIELD SET ENTITY_TYPE = 'SKU', PROPERTY_NAME = 'longDescription' WHERE FIELD_ID = 7;
UPDATE BLC_FIELD SET ENTITY_TYPE = 'SKU', PROPERTY_NAME = 'skuAttributes.color' WHERE FIELD_ID = 8;