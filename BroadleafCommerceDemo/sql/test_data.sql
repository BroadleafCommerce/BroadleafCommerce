-- MySQL test data
use broadleafcommerce;

DELETE FROM BLC_COUNTRY;
DELETE FROM blc_challenge_question;
DELETE FROM BLC_ID_GENERATION;
DELETE FROM blc_customer;
DELETE FROM blc_customer_phone;
DELETE FROM blc_phone;

------------------------
-- INSERT COUNTRIES
------------------------

INSERT INTO BLC_COUNTRY VALUES ( 'US', 'United States' );

------------------------
-- INSERT TEST CHALLENGE QUESTIONS
------------------------

INSERT INTO blc_challenge_question ( QUESTION_ID, QUESTION ) VALUES ( 1, 'What is your place of birth?' );
INSERT INTO blc_challenge_question ( QUESTION_ID, QUESTION ) VALUES ( 2, 'What is your Mother''s maiden name?' );
INSERT INTO blc_challenge_question ( QUESTION_ID, QUESTION ) VALUES ( 3, 'What is the name of your favorite pet?' );

------------------------
-- INSERT TEST ID GENERATION
------------------------

INSERT INTO BLC_ID_GENERATION ( ID_TYPE, BATCH_START, BATCH_SIZE ) VALUES ( 'org.broadleafcommerce.profile.domain.Customer', 1, 10 );


------------------------
-- INSERT Customer
------------------------

INSERT INTO `broadleafcommerce`.`blc_customer` 
(CUSTOMER_ID,CHALLENGE_ANSWER ,CHALLENGE_QUESTION_ID,EMAIL_ADDRESS         ,FIRST_NAME,LAST_NAME,PASSWORD  ,PASSWORD_CHANGE_REQUIRED,RECEIVE_EMAIL,IS_REGISTERED,USER_NAME) VALUES
(1          ,'Temple'         ,1                    ,'sconlon@credera.com' ,'Sean'    ,'Conlon' ,'asdf123' ,0                       ,1            ,1            ,'sconlon')
;

------------------------
-- INSERT Phone
------------------------

INSERT INTO `broadleafcommerce`.`blc_phone` 
(PHONE_ID, IS_ACTIVE,IS_DEFAULT,PHONE_NUMBER  ) VALUES
(1        ,1        ,1         ,'123-555-1111')
;
INSERT INTO `broadleafcommerce`.`blc_phone` 
(PHONE_ID, IS_ACTIVE,IS_DEFAULT,PHONE_NUMBER  ) VALUES
(2        ,1        ,0         ,'123-555-2222')
;
INSERT INTO `broadleafcommerce`.`blc_phone` 
(PHONE_ID, IS_ACTIVE,IS_DEFAULT,PHONE_NUMBER  ) VALUES
(3        ,1        ,0         ,'123-555-3333')
;
INSERT INTO `broadleafcommerce`.`blc_phone` 
(PHONE_ID, IS_ACTIVE,IS_DEFAULT,PHONE_NUMBER  ) VALUES
(4       ,1        ,0         ,'123-555-4444')
;
INSERT INTO `broadleafcommerce`.`blc_phone` 
(PHONE_ID, IS_ACTIVE,IS_DEFAULT,PHONE_NUMBER  ) VALUES
(5        ,0        ,0         ,'123-555-5555')
;

------------------------
-- INSERT Customer_Phone
------------------------

INSERT INTO `broadleafcommerce`.`blc_customer_phone` 
(CUSTOMER_ID,PHONE_NAME     ,PHONE_ID) VALUES
(1          ,'Home_Phone_1' ,1       )
;
INSERT INTO `broadleafcommerce`.`blc_customer_phone` 
(CUSTOMER_ID,PHONE_NAME     ,PHONE_ID) VALUES
(1          ,'Home_Phone_2' ,2       )
;
INSERT INTO `broadleafcommerce`.`blc_customer_phone` 
(CUSTOMER_ID,PHONE_NAME     ,PHONE_ID) VALUES
(1          ,'Work_Phone_1' ,3       )
;
INSERT INTO `broadleafcommerce`.`blc_customer_phone` 
(CUSTOMER_ID,PHONE_NAME     ,PHONE_ID) VALUES
(1          ,'Work_Phone_2' ,4       )
;
INSERT INTO `broadleafcommerce`.`blc_customer_phone` 
(CUSTOMER_ID,PHONE_NAME     ,PHONE_ID) VALUES
(1          ,'Cell_Phone_1' ,5       )
;




