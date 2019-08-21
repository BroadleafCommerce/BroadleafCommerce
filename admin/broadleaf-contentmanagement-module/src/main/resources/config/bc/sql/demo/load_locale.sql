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
