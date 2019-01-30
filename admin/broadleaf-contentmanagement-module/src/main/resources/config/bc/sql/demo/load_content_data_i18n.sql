--
-- The Archetype is configured with "hibernate.hbm2ddl.auto" value="create-drop" in "persistence.xml".
--
-- This will cause hibernate to populate the database when the application is started by processing the files that
-- were configured in the hibernate.hbm2ddl.import_files property.
--
-- This file loads some sample content pages and structured content data.
--

-----------------------------------------------------------------------------------------------------------------------------------
-- SAMPLE PAGE DATA - would typically be entered via the admin
-----------------------------------------------------------------------------------------------------------------------------------

-- Create structured content for locale="es"
-- -- Create an about-us page with "prueba de contenido" as the body of the page.
-- INSERT INTO BLC_PAGE (PAGE_ID, DESCRIPTION, PAGE_TMPLT_ID, FULL_URL) VALUES (10, 'Prueba de Contenido', 1, '/about_us');

INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-1000, 1, 'es', 'Page', 'pageTemplate|title', 'Espa&ntilde;ol G&eacute;nerico');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-1001, 1, 'es', 'Page', 'pageTemplate|body', 'prueba de contenido');

INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-1002, 2, 'es', 'Page', 'pageTemplate|body', '<h2 style="text-align:center;">Este es un ejemplo de una p&aacute;gina de contenido-manejado.</h2><h4 style="text-align:center;">Haga <a href="http://www.broadleafcommerce.com/features/content">click aqu&iacute;</a> para mas informaci&oacute;n.</h4>');

INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-1003, 3, 'es', 'Page', 'pageTemplate|body', '<h2 style="text-align:center;">Este es un ejemplo de una p&aacute;gina de contenido-manejado.</h2>');


-----------------------------------------------------------------------------------------------------------------------------------
-- SAMPLE STRUCTURED CONTENT DATA  - would typically be entered via the admin
-----------------------------------------------------------------------------------------------------------------------------------

---------------------------------------------------
-- HOME PAGE BANNER
---------------------------------------------------
-- Content Item
-- -- Spanish Banner (locale = "es")
INSERT INTO BLC_SC (SC_ID, CONTENT_NAME, OFFLINE_FLAG, PRIORITY, LOCALE_CODE, SC_TYPE_ID) VALUES (151, 'Promocion - 20% de descuento en todas las camisas', FALSE, 1, 'es', 1);

-- Fields
-- -- Spanish Banner (locale = "es")
INSERT INTO BLC_SC_FLD (SC_FLD_ID, FLD_KEY, VALUE) VALUES (51, 'imageUrl', '/cmsstatic/img/banners/promocion-camisas.jpg');
INSERT INTO BLC_SC_FLD (SC_FLD_ID, FLD_KEY, VALUE) VALUES (52, 'targetUrl', '/merchandise');

-- Field XREF
-- -- Spanish Banner (locale = "es")
INSERT INTO BLC_SC_FLD_MAP (BLC_SC_SC_FIELD_ID,SC_ID, SC_FLD_ID, MAP_KEY) VALUES (-150, 151, 51, 'imageUrl');
INSERT INTO BLC_SC_FLD_MAP (BLC_SC_SC_FIELD_ID,SC_ID, SC_FLD_ID, MAP_KEY) VALUES (-151, 151, 52, 'targetUrl');


-- Content Item
-- -- Spanish Banner (locale = "fr")
INSERT INTO BLC_SC (SC_ID, CONTENT_NAME, OFFLINE_FLAG, PRIORITY, LOCALE_CODE, SC_TYPE_ID) VALUES (152, 'Promocion - 20% de descuento en todas las camisas', FALSE, 1, 'fr', 1);

-- Fields
-- -- Spanish Banner (locale = "fr")
INSERT INTO BLC_SC_FLD (SC_FLD_ID, FLD_KEY, VALUE) VALUES (53, 'imageUrl', '/cmsstatic/img/banners/shirts-speciale.jpg');
INSERT INTO BLC_SC_FLD (SC_FLD_ID, FLD_KEY, VALUE) VALUES (54, 'targetUrl', '/merchandise');

-- Field XREF
-- -- Spanish Banner (locale = "fr")
INSERT INTO BLC_SC_FLD_MAP (BLC_SC_SC_FIELD_ID, SC_ID, SC_FLD_ID, MAP_KEY) VALUES (-152, 152, 53, 'imageUrl');
INSERT INTO BLC_SC_FLD_MAP (BLC_SC_SC_FIELD_ID, SC_ID, SC_FLD_ID, MAP_KEY) VALUES (-153, 152, 54, 'targetUrl');


---------------------------------------------------
-- HOME PAGE SNIPIT
---------------------------------------------------
-- -- Create a Home Page Snippet for loclale="es"
-- Content Item
INSERT INTO BLC_SC (SC_ID, CONTENT_NAME, OFFLINE_FLAG, PRIORITY, LOCALE_CODE, SC_TYPE_ID) VALUES (153, 'Home Page Snippet (es) - Aficionado', FALSE, 5, 'es', 2);

-- Fields
INSERT INTO BLC_SC_FLD (SC_FLD_ID, FLD_KEY, VALUE) VALUES (55, 'htmlContent', '<h2>AFICIONADO DE SALSAS PICANTES?</h2> Haga click para unirse a nuerto programa de Cuidades Intensivos de Heat Clinic. El lugar para conseguir las mejores ofertas.');

-- Field XREF
INSERT INTO BLC_SC_FLD_MAP (BLC_SC_SC_FIELD_ID, SC_ID, SC_FLD_ID, MAP_KEY) VALUES (-154, 153, 55, 'htmlContent');


-- -- Create a Home Page Snippet for loclale="fr"
-- Content Item
INSERT INTO BLC_SC (SC_ID, CONTENT_NAME, OFFLINE_FLAG, PRIORITY, LOCALE_CODE, SC_TYPE_ID) VALUES (154, 'Home Page Snippet (es) - Aficionado', FALSE, 5, 'fr', 2);

-- Fields
INSERT INTO BLC_SC_FLD (SC_FLD_ID, FLD_KEY, VALUE) VALUES (56, 'htmlContent', '<h2>AFICIONADO SAUCE PIQUANTE?</h2> Cliquez ici pour vous joindre &agrave; notre clinique de chaleur du Programme de soins fr&eacute;quents. L&#39;endroit pour obtenir toutes les offres sur le traitement des br&ucirc;lures.');

-- Field XREF
INSERT INTO BLC_SC_FLD_MAP (BLC_SC_SC_FIELD_ID, SC_ID, SC_FLD_ID, MAP_KEY) VALUES (-155, 154, 56, 'htmlContent');

---------------------------------------------------
-- HOME PAGE FEATURED PRODUCTS MESSAGE
---------------------------------------------------
-- -- Create a Home Page Snippet for loclale="es"
-- Content Item
INSERT INTO BLC_SC (SC_ID, CONTENT_NAME, OFFLINE_FLAG, PRIORITY, LOCALE_CODE, SC_TYPE_ID) VALUES (155, 'Home Page Featured Products Title', FALSE, 5, 'es', 3);

-- Fields
INSERT INTO BLC_SC_FLD (SC_FLD_ID, FLD_KEY, VALUE) VALUES (57, 'messageText', 'Las Salsas M&aacute;s vendidas de Heat Clinic');

-- Field XREF
INSERT INTO BLC_SC_FLD_MAP (BLC_SC_SC_FIELD_ID, SC_ID, SC_FLD_ID, MAP_KEY) VALUES (-156, 155, 57, 'messageText');


-- -- Create a Home Page Snippet for loclale="fr"
-- Content Item
INSERT INTO BLC_SC (SC_ID, CONTENT_NAME, OFFLINE_FLAG, PRIORITY, LOCALE_CODE, SC_TYPE_ID) VALUES (156, 'Home Page Featured Products Title', FALSE, 5, 'fr', 3);

-- Fields
INSERT INTO BLC_SC_FLD (SC_FLD_ID, FLD_KEY, VALUE) VALUES (58, 'messageText', 'La Clinique Heat Sauces Meilleures Ventes');

-- Field XREF
INSERT INTO BLC_SC_FLD_MAP (BLC_SC_SC_FIELD_ID, SC_ID, SC_FLD_ID, MAP_KEY) VALUES (-157, 156, 58, 'messageText');



---------------------------------------------------
-- RIGHT HAND SIDE - AD
---------------------------------------------------
-- Content Item

-- Fields

-- Field XREF
