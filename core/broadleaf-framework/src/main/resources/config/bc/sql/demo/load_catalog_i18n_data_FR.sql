-----------------------------------------------------------------------------------------------------------------------------------
-- FRENCH TRANSLATION DATA
-- Translates the catalog and content for the demo application to french.
-- Uses translation ids ranging from -200 to -399
-----------------------------------------------------------------------------------------------------------------------------------


-----------------------------------------------------------------------------------------------------------------------------------
-- CATEGORY DATA TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
-- Category names
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-201, 2001, 'fr', 'Category', 'name', 'Accueil');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-202, 2002, 'fr', 'Category', 'name', 'Sauces piquantes');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-203, 2003, 'fr', 'Category', 'name', 'Goodies');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-204, 2004, 'fr', 'Category', 'name', 'Soldes');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-205, 2005, 'fr', 'Category', 'name', 'Cartes Cadeaux');

-- Category descriptions
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-211, 2001, 'fr', 'Category', 'description', 'Accueil');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-212, 2002, 'fr', 'Category', 'description', 'Sauces piquantes');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-213, 2003, 'fr', 'Category', 'description', 'Goodies');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-214, 2004, 'fr', 'Category', 'description', 'Soldes');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-215, 2005, 'fr', 'Category', 'description', 'Cartes Cadeaux');

-----------------------------------------------------------------------------------------------------------------------------------
-- PRODUCT OPTIONS TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-231, 1, 'fr', 'ProdOption', 'label', 'Shirt Couleur');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-232, 2, 'fr', 'ProdOption', 'label', 'Shirt Taille');

-----------------------------------------------------------------------------------------------------------------------------------
-- PRODUCT OPTION VALUES TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-241, 1, 'fr', 'ProdOptionVal', 'attributeValue', 'Noir');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-242, 2, 'fr', 'ProdOptionVal', 'attributeValue', 'Rouge');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-243, 3, 'fr', 'ProdOptionVal', 'attributeValue', 'Argent');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-244, 11, 'fr', 'ProdOptionVal', 'attributeValue', 'P');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-245, 12, 'fr', 'ProdOptionVal', 'attributeValue', 'M');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-246, 13, 'fr', 'ProdOptionVal', 'attributeValue', 'G');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-247, 14, 'fr', 'ProdOptionVal', 'attributeValue', 'XG');


-----------------------------------------------------------------------------------------------------------------------------------
-- PRODUCT SKU TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-251, 1, 'fr', 'Sku', 'name', 'Sauce mort subite');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-252, 1, 'fr', 'Sku', 'longDescription', 'Comme mes Chilipals sais, je suis pas du genre &agrave; &ecirc;tre satisfaite. Par cons&eacute;quent, la cr&eacute;ation de la mort subite. Lorsque vous avez besoin d''aller au-del&agrave; ... Mort subite livrera!');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-253, 2, 'fr', 'Sku', 'name', 'Sauce Sweet Death');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-254, 2, 'fr', 'Sku', 'longDescription', 'Le parfait topper pour le poulet, le poisson, des hamburgers ou une pizza. Un grand m&eacute;lange de Habanero, mangue, fruits de la passion et de plus faire cette sauce Mort d''un festin incroyable tropicale');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-255, 3, 'fr', 'Sku', 'name', 'Hot Sauce Hoppin');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-256, 3, 'fr', 'Sku', 'longDescription', 'Tangy, venu de Cayenne poivron flux avec l''ail, l''oignon p&acirc;te de tomate, et un soupçon de sucre de canne pour en faire une sauce onctueuse avec une morsure. Magnifique sur les œufs, la volaille, le porc ou le poisson, cette sauce marie pour faire des marinades et des soupes riches.');                                                                                                                                                                  
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-257, 4, 'fr', 'Sku', 'name', 'Jour de la sauce chaude Morte Chipotle');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-258, 4, 'fr', 'Sku', 'longDescription', 'Lorsque tout le poivre est s&eacute;ch&eacute; et fum&eacute;, il est consid&eacute;r&eacute; comme un Chipotle. Normalement, avec un aspect froiss&eacute;, drak brun, le chipotle fum&eacute; offre une saveur douce qui est g&eacute;n&eacute;ralement utilis&eacute; pour ajouter un smokey, saveur rôtie aux salsas, les ragoûts et marinades.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-259, 5, 'fr', 'Sku', 'name', 'Jour de la sauce Habanero Hot Morte');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-260, 5, 'fr', 'Sku', 'longDescription', 'Si vous voulez chaud, c''est le piment de choisir. Originaire de la Caraïbe, du Yucatan et du Nord Côte de l''Am&eacute;rique du Sud, le Habanero se pr&eacute;sente dans une vari&eacute;t&eacute; de couleurs allant du vert p&acirc;le au rouge vif. La chaleur gras Habanero, la saveur et l''arôme unique, en a fait le favori des amateurs de chili.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-261, 6, 'fr', 'Sku', 'name', 'Jour de la sauce Scotch Bonnet Hot Morte');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-262, 6, 'fr', 'Sku', 'longDescription', 'Souvent confondu avec le Habanero, le Scotch Bonnet a une pointe profond&eacute;ment invers&eacute;e par rapport &agrave; l''extr&eacute;mit&eacute; pointue de l''Habanero. Allant dans de nombreuses couleurs allant du vert au jaune-orange, le Scotch Bonnet est un aliment de base dans les Antilles et sauces au poivre de style Barbade.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-263, 7, 'fr', 'Sku', 'name', 'Green Ghost');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-264, 7, 'fr', 'Sku', 'longDescription', 'Fabriqu&eacute; avec Naga Bhut Jolokia, plus chaud poivre dans le monde.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-265, 8, 'fr', 'Sku', 'name', 'Blazin ''Selle XXX Hot Habanero sauce au poivre');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-266, 8, 'fr', 'Sku', 'longDescription', 'Vous misez vos bottes, cette sauce chaude valu son nom de gens qui appr&eacute;cient une sauce chaude exceptionnel. Ce que vous trouverez ici est une saveur piquante vraiment original, pas un piquant irr&eacute;sistible que l''on retrouve dans les sauces au poivre Tabasco ordinaires - m&ecirc;me si le piment utilis&eacute; dans ce produit a &eacute;t&eacute; test&eacute; &agrave; 285.000 unit&eacute;s Scoville. Alors, en selle pour une balade inoubliable. Pour vous assurer que nous vous avons apport&eacute; la plus belle sauce au poivre de Habanero, nous sommes all&eacute;s aux contreforts des montagnes mayas au Belize, en Am&eacute;rique centrale. Ce produit est pr&eacute;par&eacute; enti&egrave;rement &agrave; la main en utilisant uniquement des l&eacute;gumes frais et de tous les ingr&eacute;dients naturels.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-267, 9, 'fr', 'Sku', 'name', 'Armageddon Le Hot Sauce To End All');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-268, 9, 'fr', 'Sku', 'longDescription', 'Tout l''enfer se d&eacute;chaîne, le feu et le soufre pleuvoir? se pr&eacute;parer &agrave; rencontrer votre machine?');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-269, 10, 'fr', 'Sku', 'name', 'Dr Chilemeister Sauce Hot Insane');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-270, 10, 'fr', 'Sku', 'longDescription', 'Voici la prescription pour ceux qui aiment la chaleur intol&eacute;rable. Dr Chilemeister potion de malades et mal mortel doit &ecirc;tre utilis&eacute; avec prudence. La douleur peut devenir une d&eacute;pendance!');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-271, 11, 'fr', 'Sku', 'name', 'Bull Snort Cowboy poivre de Cayenne Hot Sauce');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-272, 11, 'fr', 'Sku', 'longDescription', 'Been there, encord&eacute;s cela. Hotter than jument buckin ''en chaleur! Saupoudrez de plats de viande, de fruits de mer et l&eacute;gumes. Utilisation comme additif dans une sauce barbecue ou tout aliment qui a besoin d''une saveur &eacute;pic&eacute;e. Commencez avec quelques gouttes et travailler jusqu''&agrave; la saveur d&eacute;sir&eacute;e.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-273, 12, 'fr', 'Sku', 'name', 'Caf&eacute; Cajun Louisiane Douce Sauce Blackening');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-274, 12, 'fr', 'Sku', 'longDescription', 'L''une des sauces les plus insolites que nous vendons. L''original &eacute;tait un vieux style sauce cajun et c''est ça le noircissement &agrave; jour de version. C''est gentil, mais vous obtenez un grand succ&egrave;s de cannelle et de clou de girofle avec un coup de chaleur agr&eacute;able de Cayenne. Utilisez-le sur tous les aliments &agrave; donner cette ambiance cajun.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-275, 13, 'fr', 'Sku', 'name', 'Bull Snort Smokin ''Hot Sauce Toncils');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-276, 13, 'fr', 'Sku', 'longDescription', 'Todo es m&aacute;s grande en Texas, incluso lo picante de la Salsa de Snortin Bull! Tout est plus grand au Texas, m&ecirc;me la brûlure de Hot Sauce une Snortin Bull! douche sur le Texas Steak taille qu''ils appellent le 96er Ole ou vos l&eacute;gumes Jane avion. Si vous &ecirc;tes un fan sur faire de la sauce barbecue &agrave; partir de z&eacute;ro comme je suis, vous pouvez utiliser la sauce Bull amygdales Snort Smokin ''Hot tant qu''additif. Red hot habaneros et piments donner &agrave; cette tingler amygdales sa saveur c&eacute;l&egrave;bre et rouge de chaleur chaud. Bull Snort Smokin ''Hot amygdales Sauce''ll avoir vos entrailles buckin »avec une goutte d''eau.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-277, 14, 'fr', 'Sku', 'name', 'Frais Poivre de Cayenne Hot Sauce');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-278, 14, 'fr', 'Sku', 'longDescription', 'Cette sauce tire sa saveur des poivrons grand &acirc;ge et le vinaigre de canne. Il permettra d''am&eacute;liorer la saveur de la plupart de n''importe quel repas.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-279, 15, 'fr', 'Sku', 'name', 'Sauce &agrave; l''ail rôti chaud');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-280, 15, 'fr', 'Sku', 'longDescription', 'Cette sauce tire sa saveur des poivrons grand &acirc;ge et le vinaigre de canne. Il permettra d''am&eacute;liorer la saveur de la plupart de n''importe quel repas.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-281, 16, 'fr', 'Sku', 'name', 'Sauce Scotch Bonnet chaud');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-282, 16, 'fr', 'Sku', 'longDescription', 'Cette sauce tire sa saveur des poivrons grand &acirc;ge et le vinaigre de canne. Il permettra d''am&eacute;liorer la saveur de la plupart de n''importe quel repas.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-283, 17, 'fr', 'Sku', 'name', 'Sauce Scotch Bonnet chaud');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-284, 17, 'fr', 'Sku', 'longDescription', 'Cette sauce tire sa saveur des poivrons grand &acirc;ge et le vinaigre de canne. Il permettra d''am&eacute;liorer la saveur de la plupart de n''importe quel repas.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-285, 18, 'fr', 'Sku', 'name', 'Sauces chaudes Jalapeno');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-286, 18, 'fr', 'Sku', 'longDescription', 'Cette sauce tire sa saveur des poivrons grand &acirc;ge et le vinaigre de canne. Il permettra d''am&eacute;liorer la saveur de la plupart de n''importe quel repas.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-287, 19, 'fr', 'Sku', 'name', 'Sauce chaudes Chipotle');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-288, 19, 'fr', 'Sku', 'longDescription', 'Cette sauce tire sa saveur des poivrons grand &acirc;ge et le vinaigre de canne. Il permettra d''am&eacute;liorer la saveur de la plupart de n''importe quel repas.');

INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-289, 100, 'fr', 'Sku', 'name', 'Hawt comme une chemise Habanero (Hommes)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-290, 100, 'fr', 'Sku', 'longDescription', 'Collecte Hommes Habanero standards chemise &agrave; manches courtes t s&eacute;rigraphi&eacute;es en 30 coton doux singles en coupe regular.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-291, 200, 'fr', 'Sku', 'name', 'Hawt comme une chemise Habanero (Femmes)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-292, 200, 'fr', 'Sku', 'longDescription', 'Collecte de femmes Habanero standards chemise &agrave; manches courtes shirt s&eacute;rigraphi&eacute; &agrave; 30 coton doux singles en coupe regular.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-293, 300, 'fr', 'Sku', 'name', 'Clinique de chaleur tir&eacute; par la main (Hommes)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-294, 300, 'fr', 'Sku', 'longDescription', 'Ce t-shirt logo dessin&eacute; &agrave; la main pour les hommes dispose d''une coupe r&eacute;guli&egrave;re en trois couleurs diff&eacute;rentes.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-295, 400, 'fr', 'Sku', 'name', 'Clinique de chaleur tir&eacute; par la main (Femmes)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-296, 400, 'fr', 'Sku', 'longDescription', 'Ce t-shirt logo dessin&eacute; &agrave; la main pour les femmes dispose d''une coupe r&eacute;guli&egrave;re en trois couleurs diff&eacute;rentes.');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-297, 500, 'fr', 'Sku', 'name', 'Mascot Clinique chaleur (Hommes)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-298, 500, 'fr', 'Sku', 'longDescription', 'Avez-vous pas juste notre mascotte? Obtenez votre chemise propre aujourd''hui!');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-299, 600, 'fr', 'Sku', 'name', 'Mascot Clinique chaleur (Femmes)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-300, 600, 'fr', 'Sku', 'longDescription', 'Avez-vous pas juste notre mascotte? Obtenez votre chemise propre aujourd''hui!');

-----------------------------------------------------------------------------------------------------------------------------------
-- SEARCH FACET TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-380, 1, 'fr', 'SearchFacet', 'label', 'Fabricant');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-381, 2, 'fr', 'SearchFacet', 'label', 'Degr&eacute; de chaleur');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-382, 3, 'fr', 'SearchFacet', 'label', 'Prix');


-----------------------------------------------------------------------------------------------------------------------------------
-- FULFILLMENT OPTIONS TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-391, 1, 'fr', 'FulfillmentOption', 'name', 'Norme');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-392, 1, 'fr', 'FulfillmentOption', 'longDescription', '5 - 7 Journ&eacute;es');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-393, 2, 'fr', 'FulfillmentOption', 'name', 'Priorit&eacute;');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-394, 2, 'fr', 'FulfillmentOption', 'longDescription', '3 - 5 Journ&eacute;es');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-395, 3, 'fr', 'FulfillmentOption', 'name', 'Express');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-396, 3, 'fr', 'FulfillmentOption', 'longDescription', '1 - 2 Journ&eacute;es');

-----------------------------------------------------------------------------------------------------------------------------------
-- MENU ITEM TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME, TRANSLATED_VALUE) VALUES (-397, 5, 'fr', 'MenuItem', 'label', 'Nouveau Hot Sauce?');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME, TRANSLATED_VALUE) VALUES (-398, 6, 'fr', 'MenuItem', 'label', 'FAQ');