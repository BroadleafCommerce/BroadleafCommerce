SET foreign_key_checks = 0;
set sql_mode = '';

DELETE FROM broadleafcommerce.BLC_ADMIN_USER;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/admin_user.txt'
INTO TABLE broadleafcommerce.BLC_ADMIN_USER
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(ADMIN_USER_ID, EMAIL, LOGIN, NAME, PASSWORD);

DELETE FROM broadleafcommerce.BLC_ADMIN_ROLE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/admin_role.txt'
INTO TABLE broadleafcommerce.BLC_ADMIN_ROLE
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(ADMIN_ROLE_ID, DESCRIPTION, NAME);

DELETE FROM broadleafcommerce.BLC_ADMIN_PERMISSION;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/admin_permission.txt'
INTO TABLE broadleafcommerce.BLC_ADMIN_PERMISSION
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(ADMIN_PERMISSION_ID, DESCRIPTION, NAME);

DELETE FROM broadleafcommerce.BLC_ADMIN_ROLE_PERMISSION_XREF;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/admin_role_permission_xref.txt'
INTO TABLE broadleafcommerce.BLC_ADMIN_ROLE_PERMISSION_XREF
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(ADMIN_ROLE_ID, ADMIN_PERMISSION_ID);

DELETE FROM broadleafcommerce.BLC_ADMIN_USER_ROLE_XREF;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/admin_user_role_xref.txt'
INTO TABLE broadleafcommerce.BLC_ADMIN_USER_ROLE_XREF
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(ADMIN_ROLE_ID, ADMIN_USER_ID);

DELETE FROM broadleafcommerce.BLC_CODE_TYPES;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/code_types.txt'
INTO TABLE broadleafcommerce.BLC_CODE_TYPES
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(CODE_ID, CODE_TYPE, CODE_DESC, CODE_KEY, MODIFIABLE);

DELETE FROM broadleafcommerce.BLC_CATEGORY;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/category.txt' 
INTO TABLE broadleafcommerce.BLC_CATEGORY
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(CATEGORY_ID, DESCRIPTION, DISPLAY_TEMPLATE, LONG_DESCRIPTION, NAME, URL, URL_KEY, DEFAULT_PARENT_CATEGORY_ID)
  SET ACTIVE_END_DATE = DATE_ADD(CURDATE(), INTERVAL 31 DAY),
  ACTIVE_START_DATE = CURDATE();

UPDATE broadleafcommerce.BLC_CATEGORY
SET DEFAULT_PARENT_CATEGORY_ID = null
WHERE DEFAULT_PARENT_CATEGORY_ID = 0;

DELETE FROM broadleafcommerce.BLC_PRODUCT;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/product.txt' 
INTO TABLE broadleafcommerce.BLC_PRODUCT
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(PRODUCT_ID, DEFAULT_CATEGORY_ID, NAME, LONG_DESCRIPTION, @featured_product, IS_MACHINE_SORTABLE, WIDTH, HEIGHT, DEPTH, MANUFACTURE, MODEL, WEIGHT)
  SET ACTIVE_END_DATE = DATE_ADD(CURDATE(), INTERVAL 31 DAY),
	  ACTIVE_START_DATE = CURDATE(),      
	  IS_FEATURED_PRODUCT = CAST(@featured_product AS UNSIGNED);

DELETE FROM broadleafcommerce.BLC_CATEGORY_XREF;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/category_xref.txt' 
INTO TABLE broadleafcommerce.BLC_CATEGORY_XREF
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(SUB_CATEGORY_ID, CATEGORY_ID, DISPLAY_ORDER);

DELETE FROM broadleafcommerce.BLC_CATEGORY_PRODUCT_XREF;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/category_product_xref.txt' 
INTO TABLE broadleafcommerce.BLC_CATEGORY_PRODUCT_XREF
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(CATEGORY_PRODUCT_ID, PRODUCT_ID, CATEGORY_ID, DISPLAY_ORDER);

DELETE FROM broadleafcommerce.BLC_SKU;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/sku.txt' 
INTO TABLE broadleafcommerce.BLC_SKU
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(SKU_ID, NAME, DESCRIPTION, RETAIL_PRICE, SALE_PRICE, TAXABLE_FLAG, DISCOUNTABLE_FLAG)
  SET ACTIVE_END_DATE = DATE_ADD(CURDATE(), INTERVAL 31 DAY),
  ACTIVE_START_DATE = CURDATE();

DELETE FROM broadleafcommerce.BLC_PRODUCT_SKU_XREF;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/product_sku_xref.txt' 
INTO TABLE broadleafcommerce.BLC_PRODUCT_SKU_XREF
FIELDS TERMINATED BY '|' ENCLOSED BY ""; 

DELETE FROM broadleafcommerce.BLC_PRODUCT_ATTRIBUTE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/product_attribute.txt' 
INTO TABLE broadleafcommerce.BLC_PRODUCT_ATTRIBUTE
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(PRODUCT_ATTRIBUTE_ID, PRODUCT_ID, NAME, VALUE, SEARCHABLE);

DELETE FROM broadleafcommerce.BLC_PRODUCT_IMAGE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/product_image.txt' 
INTO TABLE broadleafcommerce.BLC_PRODUCT_IMAGE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(PRODUCT_ID, URL, NAME);

DELETE FROM broadleafcommerce.SEQUENCE_GENERATOR;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/sequence_generator.txt' 
INTO TABLE broadleafcommerce.SEQUENCE_GENERATOR
FIELDS TERMINATED BY '|' ENCLOSED BY "";

DELETE FROM broadleafcommerce.BLC_COUNTRY;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/country.txt' 
INTO TABLE broadleafcommerce.BLC_COUNTRY
FIELDS TERMINATED BY '|' ENCLOSED BY "";

DELETE FROM broadleafcommerce.BLC_STATE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/state.txt' 
INTO TABLE broadleafcommerce.BLC_STATE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(NAME, ABBREVIATION, COUNTRY);

DELETE FROM broadleafcommerce.BLC_SHIPPING_RATE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/shipping_rate.txt' 
INTO TABLE broadleafcommerce.BLC_SHIPPING_RATE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(ID, FEE_TYPE, FEE_SUB_TYPE, FEE_BAND, BAND_UNIT_QTY, BAND_RESULT_QTY, BAND_RESULT_PCT);

DELETE FROM broadleafcommerce.BLC_CUSTOMER;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/customer.txt' 
INTO TABLE broadleafcommerce.BLC_CUSTOMER
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(CUSTOMER_ID, FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, PASSWORD_CHANGE_REQUIRED, RECEIVE_EMAIL, IS_REGISTERED, USER_NAME, PASSWORD);

DELETE FROM broadleafcommerce.BLC_CUSTOMER_PHONE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/customer_phone.txt' 
INTO TABLE broadleafcommerce.BLC_CUSTOMER_PHONE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(CUSTOMER_PHONE_ID, PHONE_NAME, CUSTOMER_ID, PHONE_ID);

DELETE FROM broadleafcommerce.BLC_PHONE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/phone.txt' 
INTO TABLE broadleafcommerce.BLC_PHONE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(PHONE_ID, PHONE_NUMBER, IS_ACTIVE, IS_DEFAULT);

DELETE FROM broadleafcommerce.BLC_CHALLENGE_QUESTION;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/challenge_question.txt' 
INTO TABLE broadleafcommerce.BLC_CHALLENGE_QUESTION
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(QUESTION_ID, QUESTION);

DELETE FROM broadleafcommerce.BLC_PRODUCT_CROSS_SALE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/product_cross_sale.txt' 
INTO TABLE broadleafcommerce.BLC_PRODUCT_CROSS_SALE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(CROSS_SALE_PRODUCT_ID, PROMOTION_MESSAGE, SEQUENCE, PRODUCT_ID, RELATED_SALE_PRODUCT_ID);

DELETE FROM broadleafcommerce.BLC_PRODUCT_UP_SALE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/product_up_sale.txt' 
INTO TABLE broadleafcommerce.BLC_PRODUCT_UP_SALE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(UP_SALE_PRODUCT_ID, PROMOTION_MESSAGE, SEQUENCE, PRODUCT_ID, RELATED_SALE_PRODUCT_ID);

DELETE FROM broadleafcommerce.BLC_STORE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/store.txt' 
INTO TABLE broadleafcommerce.BLC_STORE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(STORE_ID, STORE_NAME, ADDRESS_1, ADDRESS_2, STORE_CITY, STORE_STATE, STORE_ZIP, LATITUDE, LONGITUDE);

DELETE FROM broadleafcommerce.BLC_ZIP_CODE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/zipCode.txt' 
INTO TABLE broadleafcommerce.BLC_ZIP_CODE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(ZIP_CODE_ID, ZIPCODE, ZIP_LATITUDE, ZIP_LONGITUDE);

DELETE FROM broadleafcommerce.BLC_PRODUCT_FEATURED;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/product_featured.txt' 
INTO TABLE broadleafcommerce.BLC_PRODUCT_FEATURED
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(FEATURED_PRODUCT_ID, PROMOTION_MESSAGE, SEQUENCE, CATEGORY_ID, PRODUCT_ID);

DELETE FROM broadleafcommerce.BLC_CATEGORY_IMAGE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/category_image.txt' 
INTO TABLE broadleafcommerce.BLC_CATEGORY_IMAGE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(CATEGORY_ID, URL, NAME);

DELETE FROM broadleafcommerce.BLC_OFFER;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/offer.txt' 
INTO TABLE broadleafcommerce.BLC_OFFER
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(OFFER_ID, APPLIES_TO_RULES, OFFER_NAME, START_DATE, END_DATE, OFFER_TYPE, OFFER_DISCOUNT_TYPE, OFFER_VALUE, 
OFFER_DELIVERY_TYPE, STACKABLE, @combinable, OFFER_PRIORITY, @apply_offer_to_marked, @apply_to_sale_price, USES, MAX_USES)

SET APPLY_OFFER_TO_MARKED_ITEMS = CAST(@apply_offer_to_marked AS UNSIGNED),
	APPLY_TO_SALE_PRICE = CAST(@apply_to_sale_price AS UNSIGNED),
	COMBINABLE_WITH_OTHER_OFFERS = CAST(@combinable AS UNSIGNED);

DELETE FROM broadleafcommerce.BLC_OFFER_CODE;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/offer_code.txt' 
INTO TABLE broadleafcommerce.BLC_OFFER_CODE
FIELDS TERMINATED BY '|' ENCLOSED BY ""
(OFFER_CODE_ID, OFFER_CODE, OFFER_ID, MAX_USES, USES);

DELETE FROM broadleafcommerce.BLC_RATING_SUMMARY;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/rating_summary.txt' 
INTO TABLE broadleafcommerce.BLC_RATING_SUMMARY
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(RATING_SUMMARY_ID, AVERAGE_RATING, ITEM_ID, RATING_TYPE);

DELETE FROM broadleafcommerce.BLC_REVIEW_DETAIL;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/review_detail.txt' 
INTO TABLE broadleafcommerce.BLC_REVIEW_DETAIL
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(REVIEW_DETAIL_ID, HELPFUL_COUNT, NOT_HELPFUL_COUNT, REVIEW_STATUS, REVIEW_TEXT, CUSTOMER_ID, RATING_SUMMARY_ID, RATING_ID)
  SET REVIEW_SUBMITTED_DATE = CURDATE();

DELETE FROM broadleafcommerce.BLC_RATING_DETAIL;

LOAD DATA INFILE 'C:\\dev\\workspaces\\credera\\BroadleafCommerce\\BroadleafCommerceDemo\\config\\mysql/rating_detail.txt' 
INTO TABLE broadleafcommerce.BLC_RATING_DETAIL
FIELDS TERMINATED BY '|' ENCLOSED BY "" 
(RATING_DETAIL_ID, RATING, CUSTOMER_ID, RATING_SUMMARY_ID)
  SET RATING_SUBMITTED_DATE = CURDATE();

DELETE FROM broadleafcommerce.BLC_CANDIDATE_ITEM_OFFER;

SET foreign_key_checks = 1;
