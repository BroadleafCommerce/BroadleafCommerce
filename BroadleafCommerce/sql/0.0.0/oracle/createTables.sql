DROP TABLE blc_customer;
DROP TABLE blc_challenge_question;

CREATE TABLE blc_challenge_question
(
  QUESTION_ID NUMBER(19,0)  PRIMARY KEY NOT NULL,
  QUESTION VARCHAR2(255)
);

CREATE TABLE blc_customer
(
  CUSTOMER_ID NUMBER(19,0)  PRIMARY KEY NOT NULL,
  CHALLENGE_ANSWER VARCHAR2(255) ,
  CHALLENGE_QUESTION_ID NUMBER(19,0) ,
  EMAIL_ADDRESS VARCHAR2(255) ,
  FIRST_NAME VARCHAR2(255) ,
  LAST_NAME VARCHAR2(255) ,
  PASSWORD VARCHAR2(255) ,
  PASSWORD_CHANGE_REQUIRED NUMBER(1,0) ,
  USER_NAME VARCHAR2(255) ,
  RECEIVE_EMAIL NUMBER(1,0) ,
  CONSTRAINT cust_ques_fk FOREIGN KEY (CHALLENGE_QUESTION_ID) REFERENCES blc_challenge_question(QUESTION_ID)
);

CREATE UNIQUE INDEX USER_NAME
   ON blc_customer ( USER_NAME);

------------------------
-- INSERT TEST CHALLENGE QUESTIONS
------------------------
INSERT INTO blc_challenge_question ( QUESTION_ID, QUESTION ) VALUES ( 1, 'What is your place of birth?' );;
INSERT INTO blc_challenge_question ( QUESTION_ID, QUESTION ) VALUES ( 2, 'What is your Mother''s maiden name?' );
INSERT INTO blc_challenge_question ( QUESTION_ID, QUESTION ) VALUES ( 3, 'What is the name of your favorite pet?' );

------------------------
-- INSERT TEST CUSTOMER
------------------------
INSERT INTO blc_customer
  ( CUSTOMER_ID, USER_NAME, PASSWORD, PASSWORD_CHANGE_REQUIRED, RECEIVE_EMAIL )
  VALUES ( 1, 'rod', '16d7a4fca7442dda3ad93c9a726597e4', 1, 1 );
