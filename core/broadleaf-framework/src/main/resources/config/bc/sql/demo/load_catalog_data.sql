--
-- The Archetype is configured with "hibernate.hbm2ddl.auto" value="create-drop" in "persistence.xml".
--
-- This will cause hibernate to populate the database when the application is started by processing the files that
-- were configured in the hibernate.hbm2ddl.import_files property.
--
-- This file is responsible for loading the the catalog data used in the Archetype.   Implementers can change this file
-- to load their initial catalog.
--

-- Custom store navigation (default template uses these for the header navigation)
INSERT INTO BLC_CATEGORY (CATEGORY_ID,DESCRIPTION,NAME,URL,ACTIVE_START_DATE,DISPLAY_TEMPLATE,ROOT_DISPLAY_ORDER, OVERRIDE_GENERATED_URL) VALUES (2001,'Home','Home','/',CURRENT_TIMESTAMP, 'layout/homepage', -5.000000, FALSE);
INSERT INTO BLC_CATEGORY (CATEGORY_ID,DESCRIPTION,NAME,URL,ACTIVE_START_DATE,ROOT_DISPLAY_ORDER, OVERRIDE_GENERATED_URL) VALUES (2002,'Hot Sauces','Hot Sauces','/hot-sauces',CURRENT_TIMESTAMP, -4.000000, FALSE);
INSERT INTO BLC_CATEGORY (CATEGORY_ID,DESCRIPTION,NAME,URL,ACTIVE_START_DATE,ROOT_DISPLAY_ORDER, OVERRIDE_GENERATED_URL) VALUES (2003,'Merchandise','Merchandise','/merchandise',CURRENT_TIMESTAMP, -3.000000, FALSE);
INSERT INTO BLC_CATEGORY (CATEGORY_ID,DESCRIPTION,NAME,URL,ACTIVE_START_DATE,ROOT_DISPLAY_ORDER, OVERRIDE_GENERATED_URL) VALUES (2004,'Clearance','Clearance','/clearance',CURRENT_TIMESTAMP, -2.000000, FALSE);

INSERT INTO BLC_CATEGORY (CATEGORY_ID,DESCRIPTION,NAME,URL,ACTIVE_START_DATE, OVERRIDE_GENERATED_URL) VALUES (2007,'Mens','Mens','/mens',CURRENT_TIMESTAMP, FALSE);
INSERT INTO BLC_CATEGORY (CATEGORY_ID,DESCRIPTION,NAME,URL,ACTIVE_START_DATE, OVERRIDE_GENERATED_URL) VALUES (2008,'Womens','Womens','/womens',CURRENT_TIMESTAMP, FALSE);

-- Builds the category hierarchy (simple in this case) - Merchandise --> Mens/Womens
INSERT INTO BLC_CATEGORY_XREF (CATEGORY_XREF_ID, SUB_CATEGORY_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (8,2007,2003,-7.000000,TRUE);
INSERT INTO BLC_CATEGORY_XREF (CATEGORY_XREF_ID, SUB_CATEGORY_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (9,2008,2003,-6.000000,TRUE);

-- Add in any applicable search facets for the given category
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION) VALUES (1, 'PRODUCT', 'manufacturer', 'Manufacturer', 'mfg');
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION) VALUES (2, 'PRODUCT', 'productAttributes(heatRange).value', 'Heat Range', 'heatRange');
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION) VALUES (3, 'PRODUCT', 'defaultSku.price', 'Price', 'price');
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION, TRANSLATABLE) VALUES (4, 'PRODUCT', 'defaultSku.name', 'Product Name', 'name', TRUE);
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION) VALUES (5, 'PRODUCT', 'model', 'Model', 'model');
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION, TRANSLATABLE) VALUES (6, 'PRODUCT', 'defaultSku.description', 'Description', 'desc', TRUE);
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION, TRANSLATABLE) VALUES (7, 'PRODUCT', 'defaultSku.longDescription', 'Long Description', 'ldesc', TRUE);
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION) VALUES (8, 'PRODUCT', 'productOptionValuesMap(COLOR)', 'Color', 'color');
INSERT INTO BLC_FIELD (FIELD_ID, ENTITY_TYPE, PROPERTY_NAME, FRIENDLY_NAME, ABBREVIATION) VALUES (9, 'PRODUCT', 'margin', 'Margin', 'margin');

-- All of the above BLC_FIELD entries also happen to be indexable (added to the search index)
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (1, 1, TRUE);
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (2, 2, FALSE);
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (3, 3, FALSE);
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (4, 4, TRUE);
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (5, 5, TRUE);
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (6, 6, TRUE);
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (7, 7, TRUE);
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (8, 8, FALSE);
INSERT INTO BLC_INDEX_FIELD (INDEX_FIELD_ID, FIELD_ID, SEARCHABLE) VALUES (9, 9, FALSE);

INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (1, 1, 't');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (2, 1, 's');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (3, 2, 'i');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (4, 3, 'p');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (5, 4, 't');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (6, 5, 't');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (7, 6, 't');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (8, 7, 't');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (9, 8, 'ss');
INSERT INTO BLC_INDEX_FIELD_TYPE (INDEX_FIELD_TYPE_ID, INDEX_FIELD_ID, FIELD_TYPE) VALUES (10, 9, 'p');

INSERT INTO BLC_SEARCH_FACET (SEARCH_FACET_ID, LABEL, SHOW_ON_SEARCH, MULTISELECT, SEARCH_DISPLAY_PRIORITY, INDEX_FIELD_TYPE_ID, NAME, USE_FACET_RANGES) VALUES (1, 'Manufacturer', TRUE, TRUE, 0, 2, 'Manufacturer Facet', FALSE);
INSERT INTO BLC_CAT_SEARCH_FACET_XREF (CATEGORY_SEARCH_FACET_ID, CATEGORY_ID, SEARCH_FACET_ID, SEQUENCE) VALUES (1, 2002, 1, 1);

INSERT INTO BLC_SEARCH_FACET (SEARCH_FACET_ID, LABEL, SHOW_ON_SEARCH, MULTISELECT, SEARCH_DISPLAY_PRIORITY, INDEX_FIELD_TYPE_ID, NAME, USE_FACET_RANGES) VALUES (2, 'Heat Range', FALSE, TRUE, 0, 3, 'Heat Range Facet', FALSE);
INSERT INTO BLC_CAT_SEARCH_FACET_XREF (CATEGORY_SEARCH_FACET_ID, CATEGORY_ID, SEARCH_FACET_ID, SEQUENCE) VALUES (2, 2002, 2, 2);

INSERT INTO BLC_SEARCH_FACET (SEARCH_FACET_ID, LABEL, SHOW_ON_SEARCH, MULTISELECT, SEARCH_DISPLAY_PRIORITY, INDEX_FIELD_TYPE_ID, NAME, USE_FACET_RANGES) VALUES (4, 'Color', TRUE, TRUE, 0, 9, 'Color Facet', FALSE);
INSERT INTO BLC_CAT_SEARCH_FACET_XREF (CATEGORY_SEARCH_FACET_ID, CATEGORY_ID, SEARCH_FACET_ID, SEQUENCE) VALUES (4, 2003, 4, 1);

INSERT INTO BLC_SEARCH_FACET (SEARCH_FACET_ID, LABEL, SHOW_ON_SEARCH, MULTISELECT, SEARCH_DISPLAY_PRIORITY, INDEX_FIELD_TYPE_ID, NAME, USE_FACET_RANGES) VALUES (3, 'Price', TRUE, TRUE, 1, 4, 'Price Facet', TRUE);
INSERT INTO BLC_CAT_SEARCH_FACET_XREF (CATEGORY_SEARCH_FACET_ID, CATEGORY_ID, SEARCH_FACET_ID, SEQUENCE) VALUES (5, 2002, 3, 3);
INSERT INTO BLC_CAT_SEARCH_FACET_XREF (CATEGORY_SEARCH_FACET_ID, CATEGORY_ID, SEARCH_FACET_ID, SEQUENCE) VALUES (6, 2003, 3, 3);
INSERT INTO BLC_CAT_SEARCH_FACET_XREF (CATEGORY_SEARCH_FACET_ID, CATEGORY_ID, SEARCH_FACET_ID, SEQUENCE) VALUES (7, 2004, 3, 3);

INSERT INTO BLC_SEARCH_FACET_RANGE (SEARCH_FACET_RANGE_ID, SEARCH_FACET_ID, MIN_VALUE, MAX_VALUE) VALUES (1, 3, 0, 5);
INSERT INTO BLC_SEARCH_FACET_RANGE (SEARCH_FACET_RANGE_ID, SEARCH_FACET_ID, MIN_VALUE, MAX_VALUE) VALUES (2, 3, 5, 10);
INSERT INTO BLC_SEARCH_FACET_RANGE (SEARCH_FACET_RANGE_ID, SEARCH_FACET_ID, MIN_VALUE, MAX_VALUE) VALUES (3, 3, 10, 15);
INSERT INTO BLC_SEARCH_FACET_RANGE (SEARCH_FACET_RANGE_ID, SEARCH_FACET_ID, MIN_VALUE, MAX_VALUE) VALUES (4, 3, 15, null);

------------------------------------------------------------------------------------------------------------------
-- Inserting products manually involves five steps which are outlined below.   Typically, products are loaded 
-- up front in the project and then managed via the Broadleaf Commerce admin.   
--
-- Loading through this script is a convenient way to get started when prototyping and can be useful in development
-- as a way to share data-setup without requiring a shared DB connection. 
------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------
-- Load Catalog - Step 1:  Create the products
-- =============================================
-- In this step, we are also populating the manufacturer for the product
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (1,'/hot-sauces/sudden_death_sauce','Blair''s',TRUE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (2,'/hot-sauces/sweet_death_sauce','Blair''s',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (3,'/hot-sauces/hoppin_hot_sauce','Salsa Express',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (4,'/hot-sauces/day_of_the_dead_chipotle_hot_sauce','Spice Exchange',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (5,'/hot-sauces/day_of_the_dead_habanero_hot_sauce','Spice Exchange',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (6,'/hot-sauces/day_of_the_dead_scotch_bonnet_sauce','Spice Exchange',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (7,'/hot-sauces/green_ghost','Garden Row',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (8,'/hot-sauces/blazin_saddle_hot_habanero_pepper_sauce','D. L. Jardine''s',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (9,'/hot-sauces/armageddon_hot_sauce_to_end_all','Figueroa Brothers',TRUE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (10,'/hot-sauces/dr_chilemeisters_insane_hot_sauce','Figueroa Brothers',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (11,'/hot-sauces/bull_snort_cowboy_cayenne_pepper_hot_sauce','Brazos Legends',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (12,'/hot-sauces/cafe_louisiane_sweet_cajun_blackening_sauce','Garden Row',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (13,'/hot-sauces/bull_snort_smokin_toncils_hot_sauce','Brazos Legends',TRUE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (14,'/hot-sauces/cool_cayenne_pepper_hot_sauce','Dave''s Gourmet',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (15,'/hot-sauces/roasted_garlic_hot_sauce','Dave''s Gourmet',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (16,'/hot-sauces/scotch_bonnet_hot_sauce','Dave''s Gourmet',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (17,'/hot-sauces/insanity_sauce','Dave''s Gourmet',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (18,'/hot-sauces/hurtin_jalepeno_hot_sauce','Dave''s Gourmet',FALSE, FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT, OVERRIDE_GENERATED_URL) VALUES (19,'/hot-sauces/roasted_red_pepper_chipotle_hot_sauce','Dave''s Gourmet',FALSE, FALSE);

INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT) VALUES (100,'/merchandise/hawt_like_a_habanero_mens','The Heat Clinic',FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT) VALUES (200,'/merchandise/hawt_like_a_habanero_womens','The Heat Clinic',FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT) VALUES (300,'/merchandise/heat_clinic_hand-drawn_mens','The Heat Clinic',FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT) VALUES (400,'/merchandise/heat_clinic_hand-drawn_womens','The Heat Clinic',FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT) VALUES (500,'/merchandise/heat_clinic_mascot_mens','The Heat Clinic',FALSE);
INSERT INTO BLC_PRODUCT (PRODUCT_ID, URL, MANUFACTURE, IS_FEATURED_PRODUCT) VALUES (600,'/merchandise/heat_clinic_mascot_womens','The Heat Clinic',FALSE);

------------------------------------------------------------------------------------------------------------------
-- Load Catalog - Step 2:  Create "default" SKUs
-- =============================================
-- The Broadleaf Commerce product model is setup such that every product has a default SKU.   For many products,
-- a product only has one SKU.    SKUs hold the pricing information for the product and are the actual entity
-- that is added to the cart.    Inventory, Pricing, and Fulfillment concerns are done at the SKU level
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE, INVENTORY_TYPE) VALUES (1,1,'Sudden Death Sauce','As my Chilipals know, I am never one to be satisfied. Hence, the creation of Sudden Death. When you need to go beyond... Sudden Death will deliver! ',10.99,3.89,'Y','Y',CURRENT_TIMESTAMP, 'CHECK_QUANTITY');
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE, INVENTORY_TYPE) VALUES (2,2,'Sweet Death Sauce','The perfect topper for chicken, fish, burgers or pizza. A great blend of Habanero, Mango, Passion Fruit and more make this Death Sauce an amazing tropical treat.',10.99,3.79,'Y','Y',CURRENT_TIMESTAMP, 'CHECK_QUANTITY');
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (3,3,'Hoppin'' Hot Sauce','Tangy, ripe cayenne peppes flow together with garlic, onion, tomato paste and a hint of cane sugar to make this a smooth sauce with a bite.  Wonderful on eggs, poultry, pork, or fish, this sauce blends to make rich marinades and soups.',4.99,3.00,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (4,4,'Day of the Dead Chipotle Hot Sauce','When any pepper is dried and smoked, it is referred to as a Chipotle. Usually with a wrinkled, drak brown appearance, the Chipotle delivers a smokey, sweet flavor which is generally used for adding a smokey, roasted flavor to salsas, stews and marinades.',6.99,4.50,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (5,5,'Day of the Dead Habanero Hot Sauce','If you want hot, this is the chile to choose. Native to the Carribean, Yucatan and Northern Coast of South America, the Habanero presents itself in a variety of colors ranging from light green to a bright red. The Habanero''s bold heat, unique flavor and aroma has made it the favorite of chile lovers.',6.99,5.50,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (6,6,'Day of the Dead Scotch Bonnet Hot Sauce','Often mistaken for the Habanero, the Scotch Bonnet has a deeply inverted tip as opposed to the pointed end of the Habanero. Ranging in many colors from green to yellow-orange, the Scotch Bonnet is a staple in West Indies and Barbados style pepper sauces.',6.99,5.40,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,SALE_PRICE,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (7,7,'Green Ghost','Made with Naga Bhut Jolokia, the World''s Hottest pepper.',11.99,8.10,9.99,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,SALE_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (8,8,'Blazin'' Saddle XXX Hot Habanero Pepper Sauce','You bet your boots, this hot sauce earned its name from folks that appreciate an outstanding hot sauce. What you''ll find here is a truly original zesty flavor, not an overpowering pungency that is found in those ordinary Tabasco pepper sauces - even though the pepper used in this product was tested at 285,000 Scoville units. So, saddle up for a ride to remember. To make sure we brought you only the finest Habanero pepper sauce, we went to the foothills of the Mayan mountains in Belize, Central America. This product is prepared entirely by hand using only fresh vegetables and all natural ingredients.',4.99,3.99,3.00,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (9,9,'Armageddon The Hot Sauce To End All','All Hell is breaking loose, fire &amp; brimstone rain down? prepare to meet your maker.',12.99,5.30,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,SALE_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (10,10,'Dr. Chilemeister''s Insane Hot Sauce','Here is the Prescription for those who enjoy intolerable heat. Dr. Chilemeister''s sick and evil deadly brew should be used with caution. Pain can become addictive!',12.99,10.99,6.89,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,SALE_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (11,11,'Bull Snort Cowboy Cayenne Pepper Hot Sauce','Been there, roped that. Hotter than a buckin'' mare in heat! Sprinkle on meat entrees, seafood and vegetables. Use as additive in barbecue sauce or any food that needs a spicy flavor. Start with a few drops and work up to the desired flavor.',3.99,2.99,2.29,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (12,12,'Cafe Louisiane Sweet Cajun Blackening Sauce','One of the more unusual sauces we sell. The original was an old style Cajun sauce and this is it''s updated blackening version. It''s sweet but you get a great hit of cinnamon and cloves with a nice kick of cayenne heat. Use on all foods to give that Cajun flair!',4.99,3.09,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (13,13,'Bull Snort Smokin'' Toncils Hot Sauce','Everything is bigger in Texas, even the burn of a Bull Snortin'' Hot Sauce! shower on that Texas sized steak they call the Ole 96er or your plane Jane vegetables. If you are a fan on making BBQ sauce from scratch like I am, you can use Bull Snort Smokin'' Tonsils Hot Sauce as an additive. Red hot habaneros and cayenne peppers give this tonsil tingler it''s famous flavor and red hot heat. Bull Snort Smokin'' Tonsils Hot Sauce''ll have your bowels buckin'' with just a drop!',3.99,2.99,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (14,14,'Cool Cayenne Pepper Hot Sauce','This sauce gets its great flavor from aged peppers and cane vinegar. It will enhance the flavor of most any meal.',5.99,3.99,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (15,15,'Roasted Garlic Hot Sauce','This sauce gets its great flavor from aged peppers and cane vinegar. It will enhance the flavor of most any meal.',5.99,4.29,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (16,16,'Scotch Bonnet Hot Sauce','This sauce gets its great flavor from aged peppers and cane vinegar. It will enhance the flavor of most any meal.',5.99,2.89,'Y','Y',CURRENT_TIMESTAMP);

------------------------------------------------------------------------------------------------------------------
-- Give some of the SKUs a sale price
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,SALE_PRICE,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (17,17,'Insanity Sauce','This sauce gets its great flavor from aged peppers and cane vinegar. It will enhance the flavor of most any meal.',5.99,3.50,4.99,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,SALE_PRICE,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (18,18,'Hurtin'' Jalapeno Hot Sauce','This sauce gets its great flavor from aged peppers and cane vinegar. It will enhance the flavor of most any meal.',5.99,3.25,4.49,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,SALE_PRICE,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (19,19,'Roasted Red Pepper & Chipotle Hot Sauce','This sauce gets its great flavor from aged peppers and cane vinegar. It will enhance the flavor of most any meal.',5.99,2.59,4.09,'Y','Y',CURRENT_TIMESTAMP);

------------------------------------------------------------------------------------------------------------------
-- Some SKUs (such as merchandise) may be product options based on one product. For example, there may be a 
-- "Men's Hand drawn Heat Clinic Shirt" product that has up to 12 SKUs showing the options of 
-- Red/Black/Silver, and Small/Medium/Large/X-Large
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_PRODUCT_OPTION (PRODUCT_OPTION_ID, OPTION_TYPE, ATTRIBUTE_NAME, LABEL, NAME, REQUIRED, VALIDATION_STRATEGY_TYPE) VALUES (1, 'COLOR', 'COLOR', 'Shirt Color', 'Shirt Color', TRUE, 'NONE');
INSERT INTO BLC_PRODUCT_OPTION (PRODUCT_OPTION_ID, OPTION_TYPE, ATTRIBUTE_NAME, LABEL, NAME, REQUIRED, VALIDATION_STRATEGY_TYPE) VALUES (2, 'SIZE', 'SIZE', 'Shirt Size', 'Shirt Size', TRUE, 'NONE');
INSERT INTO BLC_PRODUCT_OPTION (PRODUCT_OPTION_ID, OPTION_TYPE, ATTRIBUTE_NAME, LABEL, NAME, REQUIRED, USE_IN_SKU_GENERATION, VALIDATION_STRATEGY_TYPE, VALIDATION_TYPE, VALIDATION_STRING, ERROR_MESSAGE, ERROR_CODE) VALUES (3, 'TEXT', 'NAME', 'Personalized Name', 'Personalized Name', FALSE, FALSE, 'SUBMIT_ORDER', 'REGEX', '[a-zA-Z ]{0,30}', 'Name must be less than 30 characters, with only letters and spaces', 'INVALID_NAME');

INSERT INTO BLC_PRODUCT_OPTION_VALUE (PRODUCT_OPTION_VALUE_ID, ATTRIBUTE_VALUE, DISPLAY_ORDER, PRODUCT_OPTION_ID) VALUES (1, 'Black', 1, 1);
INSERT INTO BLC_PRODUCT_OPTION_VALUE (PRODUCT_OPTION_VALUE_ID, ATTRIBUTE_VALUE, DISPLAY_ORDER, PRODUCT_OPTION_ID) VALUES (2, 'Red', 2, 1);
INSERT INTO BLC_PRODUCT_OPTION_VALUE (PRODUCT_OPTION_VALUE_ID, ATTRIBUTE_VALUE, DISPLAY_ORDER, PRODUCT_OPTION_ID) VALUES (3, 'Silver', 3, 1);
INSERT INTO BLC_PRODUCT_OPTION_VALUE (PRODUCT_OPTION_VALUE_ID, ATTRIBUTE_VALUE, DISPLAY_ORDER, PRODUCT_OPTION_ID) VALUES (11, 'S', 1, 2);
INSERT INTO BLC_PRODUCT_OPTION_VALUE (PRODUCT_OPTION_VALUE_ID, ATTRIBUTE_VALUE, DISPLAY_ORDER, PRODUCT_OPTION_ID) VALUES (12, 'M', 2, 2);
INSERT INTO BLC_PRODUCT_OPTION_VALUE (PRODUCT_OPTION_VALUE_ID, ATTRIBUTE_VALUE, DISPLAY_ORDER, PRODUCT_OPTION_ID) VALUES (13, 'L', 3, 2);
INSERT INTO BLC_PRODUCT_OPTION_VALUE (PRODUCT_OPTION_VALUE_ID, ATTRIBUTE_VALUE, DISPLAY_ORDER, PRODUCT_OPTION_ID) VALUES (14, 'XL', 4, 2);

INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (100,100,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,4.99,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (200,200,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,4.69,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (300,300,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,5.29,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (400,400,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,5.49,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (500,500,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,4.89,'Y','Y',CURRENT_TIMESTAMP);
INSERT INTO BLC_SKU (SKU_ID,DEFAULT_PRODUCT_ID,NAME,LONG_DESCRIPTION,RETAIL_PRICE,COST,TAXABLE_FLAG,DISCOUNTABLE_FLAG,ACTIVE_START_DATE) VALUES (600,600,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,4.99,'Y','Y',CURRENT_TIMESTAMP);

------------------------------------------------------------------------------------------------------------------
-- Update the DEFAULT_SKU_ID on the products
------------------------------------------------------------------------------------------------------------------
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 1 WHERE PRODUCT_ID = 1;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 2 WHERE PRODUCT_ID = 2;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 3 WHERE PRODUCT_ID = 3;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 4 WHERE PRODUCT_ID = 4;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 5 WHERE PRODUCT_ID = 5;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 6 WHERE PRODUCT_ID = 6;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 7 WHERE PRODUCT_ID = 7;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 8 WHERE PRODUCT_ID = 8;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 9 WHERE PRODUCT_ID = 9;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 10 WHERE PRODUCT_ID = 10;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 11 WHERE PRODUCT_ID = 11;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 12 WHERE PRODUCT_ID = 12;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 13 WHERE PRODUCT_ID = 13;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 14 WHERE PRODUCT_ID = 14;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 15 WHERE PRODUCT_ID = 15;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 16 WHERE PRODUCT_ID = 16;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 17 WHERE PRODUCT_ID = 17;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 18 WHERE PRODUCT_ID = 18;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 19 WHERE PRODUCT_ID = 19;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 20 WHERE PRODUCT_ID = 20;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 21 WHERE PRODUCT_ID = 21;

UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 9992 WHERE PRODUCT_ID = 992;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 9993 WHERE PRODUCT_ID = 993;

UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 100 WHERE PRODUCT_ID = 100;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 200 WHERE PRODUCT_ID = 200;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 300 WHERE PRODUCT_ID = 300;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 400 WHERE PRODUCT_ID = 400;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 500 WHERE PRODUCT_ID = 500;
UPDATE BLC_PRODUCT SET DEFAULT_SKU_ID = 600 WHERE PRODUCT_ID = 600;

------------------------------------------------------------------------------------------------------------------
-- Create non-default SKUs for some merchandise. In this case, we're stating that all XL shirts are $1.00 more
-- All other combinations have no special properties, but we must create them so we can track inventory on a 
-- per-SKU level. Generally, either you have only a default SKU or SKUs for all permutations of product options
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (114,100,16.99,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.','Y','/black_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (124,100,16.99,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.','Y','/red_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (134,100,16.99,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.','Y','/silver_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (214,200,16.99,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.','Y','/black_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (224,200,16.99,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.','Y','/red_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (234,200,16.99,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.','Y','/silver_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (314,300,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors','Y','/black_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (324,300,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors','Y','/red_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (334,300,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors','Y','/silver_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (414,400,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors','Y','/black_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (424,400,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors','Y','/red_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (434,400,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors','Y','/silver_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (514,500,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!','Y','/black_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (524,500,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!','Y','/red_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (534,500,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!','Y','/silver_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (614,600,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!','Y','/black_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (624,600,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!','Y','/red_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,RETAIL_PRICE,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,TAXABLE_FLAG,URL_KEY) VALUES (634,600,16.99,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!','Y','/silver_xl');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (111,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/black_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (112,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/black_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (113,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/black_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (121,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/red_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (122,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/red_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (123,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/red_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (131,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/silver_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (132,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/silver_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (133,100,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Men''s)','Men''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/silver_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (211,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/black_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (212,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/black_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (213,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/black_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (221,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/red_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (222,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/red_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (223,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/red_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (231,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/silver_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (232,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/silver_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (233,200,'Y',CURRENT_TIMESTAMP,'Hawt Like a Habanero Shirt (Women''s)','Women''s Habanero collection standard short sleeve screen-printed tee shirt in soft 30 singles cotton in regular fit.',14.99,'Y','/silver_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (311,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/black_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (312,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/black_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (313,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/black_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (321,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/red_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (322,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/red_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (323,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/red_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (331,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/silver_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (332,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/silver_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (333,300,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Men''s)','This hand-drawn logo shirt for men features a regular fit in three different colors',15.99,'Y','/silver_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (411,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/black_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (412,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/black_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (413,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/black_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (421,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/red_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (422,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/red_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (423,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/red_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (431,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/silver_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (432,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/silver_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (433,400,'Y',CURRENT_TIMESTAMP,'Heat Clinic Hand-Drawn (Women''s)','This hand-drawn logo shirt for women features a regular fit in three different colors',15.99,'Y','/silver_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (511,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/black_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (512,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/black_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (513,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/black_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (521,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/red_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (522,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/red_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (523,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/red_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (531,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/silver_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (532,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/silver_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (533,500,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Men''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/silver_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (611,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/black_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (612,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/black_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (613,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/black_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (621,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/red_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (622,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/red_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (623,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/red_l');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (631,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/silver_s');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (632,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/silver_m');
INSERT INTO BLC_SKU (SKU_ID,ADDL_PRODUCT_ID,DISCOUNTABLE_FLAG,ACTIVE_START_DATE,NAME,LONG_DESCRIPTION,RETAIL_PRICE,TAXABLE_FLAG,URL_KEY) VALUES (633,600,'Y',CURRENT_TIMESTAMP,'Heat Clinic Mascot (Women''s)','Don''t you just love our mascot? Get your very own shirt today!',15.99,'Y','/silver_l');

------------------------------------------------------------------------------------------------------------------
-- Associate the appropriate option values for the skus
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (1, 111, 1), (2, 111, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (3, 112, 1), (4, 112, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (5, 113, 1), (6, 113, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (7, 114, 1), (8, 114, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (9, 121, 2), (10, 121, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (11, 122, 2), (12, 122, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (13, 123, 2), (14, 123, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (15, 124, 2), (16, 124, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (17, 131, 3), (18, 131, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (19, 132, 3), (20, 132, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (21, 133, 3), (22, 133, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (23, 134, 3), (24, 134, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (25, 211, 1), (26, 211, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (27, 212, 1), (28, 212, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (29, 213, 1), (30, 213, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (31, 214, 1), (32, 214, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (33, 221, 2), (34, 221, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (35, 222, 2), (36, 222, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (37, 223, 2), (38, 223, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (39, 224, 2), (40, 224, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (41, 231, 3), (42, 231, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (43, 232, 3), (44, 232, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (45, 233, 3), (46, 233, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (47, 234, 3), (48, 234, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (49, 311, 1), (50, 311, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (51, 312, 1), (52, 312, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (53, 313, 1), (54, 313, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (55, 314, 1), (56, 314, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (57, 321, 2), (58, 321, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (59, 322, 2), (60, 322, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (61, 323, 2), (62, 323, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (63, 324, 2), (64, 324, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (65, 331, 3), (66, 331, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (67, 332, 3), (68, 332, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (69, 333, 3), (70, 333, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (71, 334, 3), (72, 334, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (73, 411, 1), (74, 411, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (75, 412, 1), (76, 412, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (77, 413, 1), (78, 413, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (79, 414, 1), (80, 414, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (81, 421, 2), (82, 421, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (83, 422, 2), (84, 422, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (85, 423, 2), (86, 423, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (87, 424, 2), (88, 424, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (89, 431, 3), (90, 431, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (91, 432, 3), (92, 432, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (93, 433, 3), (94, 433, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (95, 434, 3), (96, 434, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (97, 511, 1), (98, 511, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (99, 512, 1), (100, 512, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (101, 513, 1), (102, 513, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (103, 514, 1), (104, 514, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (105, 521, 2), (106, 521, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (107, 522, 2), (108, 522, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (109, 523, 2), (110, 523, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (111, 524, 2), (112, 524, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (113, 531, 3), (114, 531, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (115, 532, 3), (116, 532, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (117, 533, 3), (118, 533, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (119, 534, 3), (120, 534, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (121, 611, 1), (122, 611, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (123, 612, 1), (124, 612, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (125, 613, 1), (126, 613, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (127, 614, 1), (128, 614, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (129, 621, 2), (130, 621, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (131, 622, 2), (132, 622, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (133, 623, 2), (134, 623, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (135, 624, 2), (136, 624, 14);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (137, 631, 3), (138, 631, 11);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (139, 632, 3), (140, 632, 12);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (141, 633, 3), (142, 633, 13);
INSERT INTO BLC_SKU_OPTION_VALUE_XREF (SKU_OPTION_VALUE_XREF_ID, SKU_ID, PRODUCT_OPTION_VALUE_ID) VALUES (143, 634, 3), (144, 634, 14);

------------------------------------------------------------------------------------------------------------------
-- Add some heat levels to all the products
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (1, 1, 'heatRange', 4);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (2, 2, 'heatRange', 1);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (3, 3, 'heatRange', 2);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (4, 4, 'heatRange', 2);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (5, 5, 'heatRange', 4);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (6, 6, 'heatRange', 4);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (7, 7, 'heatRange', 3);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (8, 8, 'heatRange', 4);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (9, 9, 'heatRange', 5);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (10, 10, 'heatRange', 5);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (11, 11, 'heatRange', 2);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (12, 12, 'heatRange', 1);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (13, 13, 'heatRange', 2);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (14, 14, 'heatRange', 2);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (15, 15, 'heatRange', 1);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (16, 16, 'heatRange', 3);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (17, 17, 'heatRange', 5);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (18, 18, 'heatRange', 3);
INSERT INTO BLC_PRODUCT_ATTRIBUTE (PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE) VALUES (19, 19, 'heatRange', 1);

------------------------------------------------------------------------------------------------------------------
-- Add some heat levels to all the hot sauce skus
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (1, 1, 'heatRange', 4);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (2, 2, 'heatRange', 1);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (3, 3, 'heatRange', 2);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (4, 4, 'heatRange', 2);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (5, 5, 'heatRange', 4);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (6, 6, 'heatRange', 4);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (7, 7, 'heatRange', 3);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (8, 8, 'heatRange', 4);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (9, 9, 'heatRange', 5);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (10, 10, 'heatRange', 5);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (11, 11, 'heatRange', 2);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (12, 12, 'heatRange', 1);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (13, 13, 'heatRange', 2);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (14, 14, 'heatRange', 2);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (15, 15, 'heatRange', 1);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (16, 16, 'heatRange', 3);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (17, 17, 'heatRange', 5);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (18, 18, 'heatRange', 3);
INSERT INTO BLC_SKU_ATTRIBUTE (SKU_ATTR_ID, SKU_ID, NAME, VALUE) VALUES (19, 19, 'heatRange', 1);

------------------------------------------------------------------------------------------------------------------
-- Associate the merchandise products with their appropriate available product options
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (1, 1, 100);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (2, 1, 200);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (3, 1, 300);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (4, 1, 400);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (5, 1, 500);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (6, 1, 600);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (7, 2, 100);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (8, 2, 200);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (9, 2, 300);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (10, 2, 400);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (11, 2, 500);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (12, 2, 600);
INSERT INTO BLC_PRODUCT_OPTION_XREF (PRODUCT_OPTION_XREF_ID, PRODUCT_OPTION_ID, PRODUCT_ID) VALUES (13, 3, 100);

------------------------------------------------------------------------------------------------------------------
-- Load Catalog - Step 3:  Create Category/Product Mapping
-- ========================================================
-- Add all hot-sauce items to the hot-sauce category
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (1,1,2002,1,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (2,2,2002,2,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (3,3,2002,3,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (4,4,2002,4,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (5,5,2002,5,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (6,6,2002,6,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (7,7,2002,7,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (8,8,2002,8,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (9,9,2002,9,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (10,10,2002,10,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (11,11,2002,11,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (12,12,2002,12,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (13,13,2002,13,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (14,14,2002,14,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (15,15,2002,15,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (16,16,2002,16,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (17,17,2002,17,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (18,18,2002,18,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (19,19,2002,19,TRUE);

-- home page items
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (20,3,2001,1);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (21,6,2001,2);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (22,9,2001,3);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (23,12,2001,4);

-- clearance items
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID) VALUES (24,7,2004);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID) VALUES (25,8,2004);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID) VALUES (26,10,2004);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID) VALUES (27,11,2004);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID) VALUES (28,18,2004);

-- merchandise items
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (29,100,2003,1,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (30,200,2003,2,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (31,300,2003,3,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (32,400,2003,4,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (33,500,2003,5,TRUE);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER, DEFAULT_REFERENCE) VALUES (34,600,2003,6,TRUE);

-- Submenu merchandise items
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (35,100,2007,1);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (36,200,2008,1);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (37,300,2007,2);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (38,400,2008,2);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (39,500,2007,3);
INSERT INTO BLC_CATEGORY_PRODUCT_XREF (CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER) VALUES (40,600,2008,3);


------------------------------------------------------------------------------------------------------------------
-- Load Catalog - Step 4:  Media Items used by products
-- ========================================================
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (101,'/cmsstatic/img/sauces/Sudden-Death-Sauce-Bottle.jpg','Sudden Death Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (102,'/cmsstatic/img/sauces/Sudden-Death-Sauce-Close.jpg','Sudden Death Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (201,'/cmsstatic/img/sauces/Sweet-Death-Sauce-Bottle.jpg','Sweet Death Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (202,'/cmsstatic/img/sauces/Sweet-Death-Sauce-Close.jpg','Sweet Death Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (203,'/cmsstatic/img/sauces/Sweet-Death-Sauce-Skull.jpg','Sweet Death Sauce Close-up','alt2');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (204,'/cmsstatic/img/sauces/Sweet-Death-Sauce-Tile.jpg','Sweet Death Sauce Close-up','alt3');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (205,'/cmsstatic/img/sauces/Sweet-Death-Sauce-Grass.jpg','Sweet Death Sauce Close-up','alt4');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (206,'/cmsstatic/img/sauces/Sweet-Death-Sauce-Logo.jpg','Sweet Death Sauce Close-up','alt5');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (301,'/cmsstatic/img/sauces/Hoppin-Hot-Sauce-Bottle.jpg','Hoppin Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (302,'/cmsstatic/img/sauces/Hoppin-Hot-Sauce-Close.jpg','Hoppin Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (401,'/cmsstatic/img/sauces/Day-of-the-Dead-Chipotle-Hot-Sauce-Bottle.jpg','Day of the Dead Chipotle Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (402,'/cmsstatic/img/sauces/Day-of-the-Dead-Chipotle-Hot-Sauce-Close.jpg','Day of the Dead Chipotle Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (501,'/cmsstatic/img/sauces/Day-of-the-Dead-Habanero-Hot-Sauce-Bottle.jpg','Day of the Dead Habanero Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (502,'/cmsstatic/img/sauces/Day-of-the-Dead-Habanero-Hot-Sauce-Close.jpg','Day of the Dead Habanero Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (601,'/cmsstatic/img/sauces/Day-of-the-Dead-Scotch-Bonnet-Hot-Sauce-Bottle.jpg','Day of the Dead Scotch Bonnet Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (602,'/cmsstatic/img/sauces/Day-of-the-Dead-Scotch-Bonnet-Hot-Sauce-Close.jpg','Day of the Dead Scotch Bonnet Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (701,'/cmsstatic/img/sauces/Green-Ghost-Bottle.jpg','Green Ghost Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (702,'/cmsstatic/img/sauces/Green-Ghost-Close.jpg','Green Ghost Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (801,'/cmsstatic/img/sauces/Blazin-Saddle-XXX-Hot-Habanero-Pepper-Sauce-Bottle.jpg','Blazin Saddle XXX Hot Habanero Pepper Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (802,'/cmsstatic/img/sauces/Blazin-Saddle-XXX-Hot-Habanero-Pepper-Sauce-Close.jpg','Blazin Saddle XXX Hot Habanero Pepper Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (901,'/cmsstatic/img/sauces/Armageddon-The-Hot-Sauce-To-End-All-Bottle.jpg','Armageddon The Hot Sauce To End All Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (902,'/cmsstatic/img/sauces/Armageddon-The-Hot-Sauce-To-End-All-Close.jpg','Armageddon The Hot Sauce To End All Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1001,'/cmsstatic/img/sauces/Dr.-Chilemeisters-Insane-Hot-Sauce-Bottle.jpg','Dr. Chilemeisters Insane Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1002,'/cmsstatic/img/sauces/Dr.-Chilemeisters-Insane-Hot-Sauce-Close.jpg','Dr. Chilemeisters Insane Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1101,'/cmsstatic/img/sauces/Bull-Snort-Cowboy-Cayenne-Pepper-Hot-Sauce-Bottle.jpg','Bull Snort Cowboy Cayenne Pepper Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1102,'/cmsstatic/img/sauces/Bull-Snort-Cowboy-Cayenne-Pepper-Hot-Sauce-Close.jpg','Bull Snort Cowboy Cayenne Pepper Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1201,'/cmsstatic/img/sauces/Cafe-Louisiane-Sweet-Cajun-Blackening-Sauce-Bottle.jpg','Cafe Louisiane Sweet Cajun Blackening Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1202,'/cmsstatic/img/sauces/Cafe-Louisiane-Sweet-Cajun-Blackening-Sauce-Close.jpg','Cafe Louisiane Sweet Cajun Blackening Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1301,'/cmsstatic/img/sauces/Bull-Snort-Smokin-Toncils-Hot-Sauce-Bottle.jpg','Bull Snort Smokin Toncils Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1302,'/cmsstatic/img/sauces/Bull-Snort-Smokin-Toncils-Hot-Sauce-Close.jpg','Bull Snort Smokin Toncils Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1401,'/cmsstatic/img/sauces/Cool-Cayenne-Pepper-Hot-Sauce-Bottle.jpg','Cool Cayenne Pepper Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1402,'/cmsstatic/img/sauces/Cool-Cayenne-Pepper-Hot-Sauce-Close.jpg','Cool Cayenne Pepper Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1501,'/cmsstatic/img/sauces/Roasted-Garlic-Hot-Sauce-Bottle.jpg','Roasted Garlic Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1502,'/cmsstatic/img/sauces/Roasted-Garlic-Hot-Sauce-Close.jpg','Roasted Garlic Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1601,'/cmsstatic/img/sauces/Scotch-Bonnet-Hot-Sauce-Bottle.jpg','Scotch Bonnet Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1602,'/cmsstatic/img/sauces/Scotch-Bonnet-Hot-Sauce-Close.jpg','Scotch Bonnet Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1701,'/cmsstatic/img/sauces/Insanity-Sauce-Bottle.jpg','Insanity Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1702,'/cmsstatic/img/sauces/Insanity-Sauce-Close.jpg','Insanity Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1801,'/cmsstatic/img/sauces/Hurtin-Jalepeno-Hot-Sauce-Bottle.jpg','Hurtin Jalepeno Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1802,'/cmsstatic/img/sauces/Hurtin-Jalepeno-Hot-Sauce-Close.jpg','Hurtin Jalepeno Hot Sauce Close-up','alt1');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1901,'/cmsstatic/img/sauces/Roasted-Red-Pepper-and-Chipotle-Hot-Sauce-Bottle.jpg','Roasted Red Pepper and Chipotle Hot Sauce Bottle','primary');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT) VALUES (1902,'/cmsstatic/img/sauces/Roasted-Red-Pepper-and-Chipotle-Hot-Sauce-Close.jpg','Roasted Red Pepper and Chipotle Hot Sauce Close-up','alt1');

INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (10001,'/cmsstatic/img/merch/habanero_mens_black.jpg','Hawt Like a Habanero Men''s Black','primary','Black');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (10002,'/cmsstatic/img/merch/habanero_mens_red.jpg','Hawt Like a Habanero Men''s Red','primary','Red');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (10003,'/cmsstatic/img/merch/habanero_mens_silver.jpg','Hawt Like a Habanero Men''s Silver','primary','Silver');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (20001,'/cmsstatic/img/merch/habanero_womens_black.jpg','Hawt Like a Habanero Women''s Black','primary','Black');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (20002,'/cmsstatic/img/merch/habanero_womens_red.jpg','Hawt Like a Habanero Women''s Red','primary','Red');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (20003,'/cmsstatic/img/merch/habanero_womens_silver.jpg','Hawt Like a Habanero Women''s Silver','primary','Silver');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (30001,'/cmsstatic/img/merch/heat_clinic_handdrawn_mens_black.jpg','Heat Clinic Hand-Drawn Men''s Black','primary', 'Black');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (30002,'/cmsstatic/img/merch/heat_clinic_handdrawn_mens_red.jpg','Heat Clinic Hand-Drawn Men''s Red','primary', 'Red');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (30003,'/cmsstatic/img/merch/heat_clinic_handdrawn_mens_silver.jpg','Heat Clinic Hand-Drawn Men''s Silver','primary', 'Silver');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (40001,'/cmsstatic/img/merch/heat_clinic_handdrawn_womens_black.jpg','Heat Clinic Hand-Drawn Women''s Black','primary', 'Black');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (40002,'/cmsstatic/img/merch/heat_clinic_handdrawn_womens_red.jpg','Heat Clinic Hand-Drawn Women''s Red','primary', 'Red');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (40003,'/cmsstatic/img/merch/heat_clinic_handdrawn_womens_silver.jpg','Heat Clinic Hand-Drawn Women''s Silver','primary', 'Silver');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (50001,'/cmsstatic/img/merch/heat_clinic_mascot_mens_black.jpg','Heat Clinic Mascot Men''s Black','primary', 'Black');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (50002,'/cmsstatic/img/merch/heat_clinic_mascot_mens_red.jpg','Heat Clinic Mascot Men''s Red','primary', 'Red');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (50003,'/cmsstatic/img/merch/heat_clinic_mascot_mens_silver.jpg','Heat Clinic Mascot Men''s Silver','primary', 'Silver');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (60001,'/cmsstatic/img/merch/heat_clinic_mascot_womens_black.jpg','Heat Clinic Mascot Women''s Black','primary', 'Black');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (60002,'/cmsstatic/img/merch/heat_clinic_mascot_womens_red.jpg','Heat Clinic Mascot Women''s Red','primary', 'Red');
INSERT INTO BLC_MEDIA (MEDIA_ID, URL, TITLE, ALT_TEXT, TAGS) VALUES (60003,'/cmsstatic/img/merch/heat_clinic_mascot_womens_silver.jpg','Heat Clinic Mascot Women''s Silver','primary', 'Silver');


------------------------------------------------------------------------------------------------------------------
-- Load Catalog - Step 5:  Mapping for product to media
-- ========================================================
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-100,1,101,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-99,2,201,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-98,3,301,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-97,4,401,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-96,5,501,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-95,6,601,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-94,7,701,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-93,8,801,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-92,9,901,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-91,10,1001,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-90,11,1101,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-89,12,1201,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-88,13,1301,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-87,14,1401,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-86,15,1501,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-85,16,1601,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-84,17,1701,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-83,18,1801,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-82,19,1901,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-81,1,102,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-80,2,202,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-79,3,302,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-78,4,402,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-77,5,502,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-76,6,602,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-75,7,702,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-74,8,802,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-73,9,902,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-72,10,1002,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-71,11,1102,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-70,12,1202,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-69,13,1302,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-68,14,1402,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-67,15,1502,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-66,16,1602,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-65,17,1702,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-64,18,1802,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-63,19,1902,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-62,2,203,'alt2');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-61,2,204,'alt3');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-60,2,205,'alt4');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-59,2,206,'alt5');

INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-58,100,10001,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-57,200,20002,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-56,300,30003,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-55,400,40002,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-54,500,50003,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-53,600,60001,'primary');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-52,100,10002,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-51,200,20001,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-50,300,30001,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-49,400,40001,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-48,500,50001,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-47,600,60002,'alt1');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-46,100,10003,'alt2');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-45,200,20003,'alt2');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-44,300,30002,'alt2');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-43,400,40003,'alt2');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-42,500,50002,'alt2');
INSERT INTO BLC_SKU_MEDIA_MAP (SKU_MEDIA_ID, BLC_SKU_SKU_ID, MEDIA_ID, MAP_KEY) VALUES (-41,600,60003,'alt2');


------------------------------------------------------------------------------------------------------------------
-- Load Catalog - Step 5: Asset Items (media)
-- ========================================================
------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (101,'image/jpg','FILESYSTEM','/img/sauces/Sudden-Death-Sauce-Bottle.jpg','Sudden Death Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (102,'image/jpg','FILESYSTEM','/img/sauces/Sudden-Death-Sauce-Close.jpg','Sudden Death Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (201,'image/jpg','FILESYSTEM','/img/sauces/Sweet-Death-Sauce-Bottle.jpg','Sweet Death Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (202,'image/jpg','FILESYSTEM','/img/sauces/Sweet-Death-Sauce-Close.jpg','Sweet Death Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (203,'image/jpg','FILESYSTEM','/img/sauces/Sweet-Death-Sauce-Skull.jpg','Sweet Death Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (204,'image/jpg','FILESYSTEM','/img/sauces/Sweet-Death-Sauce-Tile.jpg','Sweet Death Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (205,'image/jpg','FILESYSTEM','/img/sauces/Sweet-Death-Sauce-Grass.jpg','Sweet Death Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (206,'image/jpg','FILESYSTEM','/img/sauces/Sweet-Death-Sauce-Logo.jpg','Sweet Death Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (301,'image/jpg','FILESYSTEM','/img/sauces/Hoppin-Hot-Sauce-Bottle.jpg','Hoppin Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (302,'image/jpg','FILESYSTEM','/img/sauces/Hoppin-Hot-Sauce-Close.jpg','Hoppin Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (401,'image/jpg','FILESYSTEM','/img/sauces/Day-of-the-Dead-Chipotle-Hot-Sauce-Bottle.jpg','Day of the Dead Chipotle Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (402,'image/jpg','FILESYSTEM','/img/sauces/Day-of-the-Dead-Chipotle-Hot-Sauce-Close.jpg','Day of the Dead Chipotle Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (501,'image/jpg','FILESYSTEM','/img/sauces/Day-of-the-Dead-Habanero-Hot-Sauce-Bottle.jpg','Day of the Dead Habanero Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (502,'image/jpg','FILESYSTEM','/img/sauces/Day-of-the-Dead-Habanero-Hot-Sauce-Close.jpg','Day of the Dead Habanero Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (601,'image/jpg','FILESYSTEM','/img/sauces/Day-of-the-Dead-Scotch-Bonnet-Hot-Sauce-Bottle.jpg','Day of the Dead Scotch Bonnet Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (602,'image/jpg','FILESYSTEM','/img/sauces/Day-of-the-Dead-Scotch-Bonnet-Hot-Sauce-Close.jpg','Day of the Dead Scotch Bonnet Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (701,'image/jpg','FILESYSTEM','/img/sauces/Green-Ghost-Bottle.jpg','Green Ghost Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (702,'image/jpg','FILESYSTEM','/img/sauces/Green-Ghost-Close.jpg','Green Ghost Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (801,'image/jpg','FILESYSTEM','/img/sauces/Blazin-Saddle-XXX-Hot-Habanero-Pepper-Sauce-Bottle.jpg','Blazin Saddle XXX Hot Habanero Pepper Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (802,'image/jpg','FILESYSTEM','/img/sauces/Blazin-Saddle-XXX-Hot-Habanero-Pepper-Sauce-Close.jpg','Blazin Saddle XXX Hot Habanero Pepper Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (901,'image/jpg','FILESYSTEM','/img/sauces/Armageddon-The-Hot-Sauce-To-End-All-Bottle.jpg','Armageddon The Hot Sauce To End All Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (902,'image/jpg','FILESYSTEM','/img/sauces/Armageddon-The-Hot-Sauce-To-End-All-Close.jpg','Armageddon The Hot Sauce To End All Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1001,'image/jpg','FILESYSTEM','/img/sauces/Dr.-Chilemeisters-Insane-Hot-Sauce-Bottle.jpg','Dr. Chilemeisters Insane Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1002,'image/jpg','FILESYSTEM','/img/sauces/Dr.-Chilemeisters-Insane-Hot-Sauce-Close.jpg','Dr. Chilemeisters Insane Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1101,'image/jpg','FILESYSTEM','/img/sauces/Bull-Snort-Cowboy-Cayenne-Pepper-Hot-Sauce-Bottle.jpg','Bull Snort Cowboy Cayenne Pepper Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1102,'image/jpg','FILESYSTEM','/img/sauces/Bull-Snort-Cowboy-Cayenne-Pepper-Hot-Sauce-Close.jpg','Bull Snort Cowboy Cayenne Pepper Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1201,'image/jpg','FILESYSTEM','/img/sauces/Cafe-Louisiane-Sweet-Cajun-Blackening-Sauce-Bottle.jpg','Cafe Louisiane Sweet Cajun Blackening Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1202,'image/jpg','FILESYSTEM','/img/sauces/Cafe-Louisiane-Sweet-Cajun-Blackening-Sauce-Close.jpg','Cafe Louisiane Sweet Cajun Blackening Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1301,'image/jpg','FILESYSTEM','/img/sauces/Bull-Snort-Smokin-Toncils-Hot-Sauce-Bottle.jpg','Bull Snort Smokin Toncils Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1302,'image/jpg','FILESYSTEM','/img/sauces/Bull-Snort-Smokin-Toncils-Hot-Sauce-Close.jpg','Bull Snort Smokin Toncils Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1401,'image/jpg','FILESYSTEM','/img/sauces/Cool-Cayenne-Pepper-Hot-Sauce-Bottle.jpg','Cool Cayenne Pepper Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1402,'image/jpg','FILESYSTEM','/img/sauces/Cool-Cayenne-Pepper-Hot-Sauce-Close.jpg','Cool Cayenne Pepper Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1501,'image/jpg','FILESYSTEM','/img/sauces/Roasted-Garlic-Hot-Sauce-Bottle.jpg','Roasted Garlic Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1502,'image/jpg','FILESYSTEM','/img/sauces/Roasted-Garlic-Hot-Sauce-Close.jpg','Roasted Garlic Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1601,'image/jpg','FILESYSTEM','/img/sauces/Scotch-Bonnet-Hot-Sauce-Bottle.jpg','Scotch Bonnet Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1602,'image/jpg','FILESYSTEM','/img/sauces/Scotch-Bonnet-Hot-Sauce-Close.jpg','Scotch Bonnet Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1701,'image/jpg','FILESYSTEM','/img/sauces/Insanity-Sauce-Bottle.jpg','Insanity Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1702,'image/jpg','FILESYSTEM','/img/sauces/Insanity-Sauce-Close.jpg','Insanity Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1801,'image/jpg','FILESYSTEM','/img/sauces/Hurtin-Jalepeno-Hot-Sauce-Bottle.jpg','Hurtin Jalepeno Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1802,'image/jpg','FILESYSTEM','/img/sauces/Hurtin-Jalepeno-Hot-Sauce-Close.jpg','Hurtin Jalepeno Hot Sauce Close-up');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1901,'image/jpg','FILESYSTEM','/img/sauces/Roasted-Red-Pepper-and-Chipotle-Hot-Sauce-Bottle.jpg','Roasted Red Pepper and Chipotle Hot Sauce Bottle');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (1902,'image/jpg','FILESYSTEM','/img/sauces/Roasted-Red-Pepper-and-Chipotle-Hot-Sauce-Close.jpg','Roasted Red Pepper and Chipotle Hot Sauce Close-up');

INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (10001,'image/jpg','FILESYSTEM','/img/merch/habanero_mens_black.jpg','Hawt Like a Habanero Men''s Black');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (10002,'image/jpg','FILESYSTEM','/img/merch/habanero_mens_red.jpg','Hawt Like a Habanero Men''s Red');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (10003,'image/jpg','FILESYSTEM','/img/merch/habanero_mens_silver.jpg','Hawt Like a Habanero Men''s Silver');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (20001,'image/jpg','FILESYSTEM','/img/merch/habanero_womens_black.jpg','Hawt Like a Habanero Women''s Black');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (20002,'image/jpg','FILESYSTEM','/img/merch/habanero_womens_red.jpg','Hawt Like a Habanero Women''s Red');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (20003,'image/jpg','FILESYSTEM','/img/merch/habanero_womens_silver.jpg','Hawt Like a Habanero Women''s Silver');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (30001,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_handdrawn_mens_black.jpg','Heat Clinic Hand-Drawn Men''s Black');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (30002,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_handdrawn_mens_red.jpg','Heat Clinic Hand-Drawn Men''s Red');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (30003,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_handdrawn_mens_silver.jpg','Heat Clinic Hand-Drawn Men''s Silver');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (40001,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_handdrawn_womens_black.jpg','Heat Clinic Hand-Drawn Women''s Black');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (40002,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_handdrawn_womens_red.jpg','Heat Clinic Hand-Drawn Women''s Red');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (40003,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_handdrawn_womens_silver.jpg','Heat Clinic Hand-Drawn Women''s Silver');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (50001,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_mascot_mens_black.jpg','Heat Clinic Mascot Men''s Black');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (50002,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_mascot_mens_red.jpg','Heat Clinic Mascot Men''s Red');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (50003,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_mascot_mens_silver.jpg','Heat Clinic Mascot Men''s Silver');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (60001,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_mascot_womens_black.jpg','Heat Clinic Mascot Women''s Black');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (60002,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_mascot_womens_red.jpg','Heat Clinic Mascot Women''s Red');
INSERT INTO BLC_STATIC_ASSET (STATIC_ASSET_ID, MIME_TYPE, STORAGE_TYPE, FULL_URL, NAME) VALUES (60003,'image/jpg','FILESYSTEM','/img/merch/heat_clinic_mascot_womens_silver.jpg','Heat Clinic Mascot Women''s Silver');


------------------------------------------------------------------------------------------------------------------
-- End of Catalog load
-- ========================================================
------------------------------------------------------------------------------------------------------------------

INSERT INTO BLC_URL_HANDLER(URL_HANDLER_ID, INCOMING_URL, NEW_URL, URL_REDIRECT_TYPE) VALUES (1, '/googlePerm', 'http://www.google.com', 'REDIRECT_PERM');
INSERT INTO BLC_URL_HANDLER(URL_HANDLER_ID, INCOMING_URL, NEW_URL, URL_REDIRECT_TYPE) VALUES (2, '/googleTemp', 'http://www.google.com', 'REDIRECT_TEMP');
INSERT INTO BLC_URL_HANDLER(URL_HANDLER_ID, INCOMING_URL, NEW_URL, URL_REDIRECT_TYPE) VALUES (3, '/insanity', '/hot-sauces/insanity_sauce', 'FORWARD');
INSERT INTO BLC_URL_HANDLER(URL_HANDLER_ID, INCOMING_URL, NEW_URL, URL_REDIRECT_TYPE) VALUES (4, '/jalepeno', '/hot-sauces/hurtin_jalepeno_hot_sauce', 'REDIRECT_TEMP');

INSERT INTO BLC_SEARCH_INTERCEPT(SEARCH_REDIRECT_ID, PRIORITY,SEARCH_TERM, URL, ACTIVE_START_DATE) VALUES (-1,1, 'insanity', '/hot-sauces/insanity_sauce', '1992-10-15 14:28:36');
INSERT INTO BLC_SEARCH_INTERCEPT(SEARCH_REDIRECT_ID, PRIORITY, SEARCH_TERM, URL, ACTIVE_START_DATE) VALUES (-2,-10, 'sale', '/clearance', '1992-10-15 14:28:36');

-----------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------
-- RELATED PRODUCT - DATA (featured products for right-hand side display)
-----------------------------------------------------------------------------------------------------------------------------------
-- Adding to home category
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (1, 1, 2001, 18);
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (2, 2, 2001, 15);
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (3, 3, 2001, 200);
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (4, 4, 2001, 100);

-- Adding to merchandise category
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (5, 1, 2003, 500);

-- Adding to hot-sauces category
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (8, 1, 2002, 4);
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (9, 2, 2002, 5);
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (10, 3, 2002, 3);
INSERT INTO BLC_PRODUCT_FEATURED(FEATURED_PRODUCT_ID, SEQUENCE, CATEGORY_ID, PRODUCT_ID)  VALUES (11, 4, 2002, 12);

-- Adding a 20% off sale to Merchandise category to fit the Shirts Special Homepage Banner
INSERT INTO BLC_OFFER (OFFER_ID, OFFER_NAME, START_DATE, END_DATE, OFFER_TYPE, OFFER_DISCOUNT_TYPE, OFFER_VALUE, COMBINABLE_WITH_OTHER_OFFERS, APPLY_TO_SALE_PRICE, MAX_USES, OFFER_ITEM_TARGET_RULE, AUTOMATICALLY_ADDED, APPLY_TO_CHILD_ITEMS) VALUES (1, 'Shirts Special',CURRENT_DATE,'2020-01-01 00:00:00','ORDER_ITEM','PERCENT_OFF',20,TRUE,FALSE,0, 'NONE', FALSE, FALSE);

INSERT INTO BLC_OFFER_ITEM_CRITERIA (OFFER_ITEM_CRITERIA_ID, ORDER_ITEM_MATCH_RULE, QUANTITY) VALUES (1, 'MvelHelper.toUpperCase(orderItem.?category.?name)==MvelHelper.toUpperCase("merchandise")', 1);

INSERT INTO BLC_TAR_CRIT_OFFER_XREF (OFFER_TAR_CRIT_ID, OFFER_ITEM_CRITERIA_ID, OFFER_ID) VALUES (-100, 1, 1);

-- Sample fulfillment option
INSERT INTO BLC_FULFILLMENT_OPTION (FULFILLMENT_OPTION_ID, NAME, LONG_DESCRIPTION, USE_FLAT_RATES, FULFILLMENT_TYPE) VALUES (1, 'Standard', '5 - 7 Days', FALSE, 'PHYSICAL_SHIP');
INSERT INTO BLC_FULFILLMENT_OPTION (FULFILLMENT_OPTION_ID, NAME, LONG_DESCRIPTION, USE_FLAT_RATES, FULFILLMENT_TYPE) VALUES (2, 'Priority', '3 - 5 Days', FALSE, 'PHYSICAL_SHIP');
INSERT INTO BLC_FULFILLMENT_OPTION (FULFILLMENT_OPTION_ID, NAME, LONG_DESCRIPTION, USE_FLAT_RATES, FULFILLMENT_TYPE) VALUES (3, 'Express', '1 - 2 Days', FALSE, 'PHYSICAL_SHIP');

INSERT INTO BLC_FULFILLMENT_OPTION_FIXED (FULFILLMENT_OPTION_ID, PRICE) VALUES (1, 5.00);
INSERT INTO BLC_FULFILLMENT_OPTION_FIXED (FULFILLMENT_OPTION_ID, PRICE) VALUES (2, 10.00);
INSERT INTO BLC_FULFILLMENT_OPTION_FIXED (FULFILLMENT_OPTION_ID, PRICE) VALUES (3, 20.00);

-- No out of box sandboxes
-- INSERT INTO BLC_SANDBOX (SANDBOX_ID, SANDBOX_NAME, SANDBOX_TYPE, COLOR) VALUES (-10, 'Fall Sale', 'APPROVAL', '#E67D4C');
-- INSERT INTO BLC_SANDBOX (SANDBOX_ID, SANDBOX_NAME, SANDBOX_TYPE, COLOR) VALUES (-20, 'Winter Sale', 'APPROVAL', '#2F5FAF');
-- Note, the code below adds sandboxes with null site_id meaning they are for all sites
-- INSERT INTO BLC_SANDBOX_MGMT (SANDBOX_MGMT_ID, SANDBOX_ID) VALUES (-10, -10);
-- INSERT INTO BLC_SANDBOX_MGMT (SANDBOX_MGMT_ID, SANDBOX_ID) VALUES (-20, -20);
