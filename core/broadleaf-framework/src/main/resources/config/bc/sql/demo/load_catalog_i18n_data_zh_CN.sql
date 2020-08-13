-----------------------------------------------------------------------------------------------------------------------------------
-- FRENCH TRANSLATION DATA
-- Translates the catalog and content for the demo application to french.
-- Uses translation ids ranging from -200 to -399
-----------------------------------------------------------------------------------------------------------------------------------


-----------------------------------------------------------------------------------------------------------------------------------
-- CATEGORY DATA TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
-- Category names
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-501, 2001, 'zh_CN', 'Category', 'name', '首页');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-502, 2002, 'zh_CN', 'Category', 'name', '辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-503, 2003, 'zh_CN', 'Category', 'name', '商品');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-504, 2004, 'zh_CN', 'Category', 'name', '清仓');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-505, 2005, 'zh_CN', 'Category', 'name', '礼品卡');

-- Category descriptions
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-511, 2001, 'zh_CN', 'Category', 'description', '首页');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-512, 2002, 'zh_CN', 'Category', 'description', '礼品卡');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-513, 2003, 'zh_CN', 'Category', 'description', '商品');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-514, 2004, 'zh_CN', 'Category', 'description', '清仓');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-515, 2005, 'zh_CN', 'Category', 'description', '礼品卡');

-----------------------------------------------------------------------------------------------------------------------------------
-- PRODUCT OPTIONS TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-531, 1, 'zh_CN', 'ProdOption', 'label', '衬衫颜色');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-532, 2, 'zh_CN', 'ProdOption', 'label', '衬衫大小');

-----------------------------------------------------------------------------------------------------------------------------------
-- PRODUCT OPTION VALUES TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-541, 1, 'zh_CN', 'ProdOptionVal', 'attributeValue', '黑色');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-542, 2, 'zh_CN', 'ProdOptionVal', 'attributeValue', '红色');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-543, 3, 'zh_CN', 'ProdOptionVal', 'attributeValue', '灰色');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-544, 11, 'zh_CN', 'ProdOptionVal', 'attributeValue', 'P');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-545, 12, 'zh_CN', 'ProdOptionVal', 'attributeValue', 'M');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-546, 13, 'zh_CN', 'ProdOptionVal', 'attributeValue', 'G');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-547, 14, 'zh_CN', 'ProdOptionVal', 'attributeValue', 'XG');


-----------------------------------------------------------------------------------------------------------------------------------
-- PRODUCT SKU TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-551, 1, 'zh_CN', 'Sku', 'name', '酱油');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-552, 1, 'zh_CN', 'Sku', 'longDescription', '我的智利人知道，我不是那种ê非常满意。因此，突然死亡的创造。当你需要超越...利夫雷拉突然死亡!');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-553, 2, 'zh_CN', 'Sku', 'name', '甜死');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-554, 2, 'zh_CN', 'Sku', 'longDescription', '鸡肉、鱼、汉堡包或披萨的最佳搭配。一个伟大的混合哈瓦那，芒果，西番莲果和更多的死亡酱在一个难以置信的热带盛宴');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-555, 3, 'zh_CN', 'Sku', 'name', 'Hoppin辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-556, 3, 'zh_CN', 'Sku', 'longDescription', '香味扑鼻、成熟的辣椒酱与大蒜、洋葱、番茄酱和少许蔗糖混合在一起，咬一口就变成了光滑的酱汁。用在鸡蛋、家禽、猪肉或鱼上非常棒，混合这种酱汁可以做成丰富的卤汁和汤。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-557, 4, 'zh_CN', 'Sku', 'name', '亡灵节辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-558, 4, 'zh_CN', 'Sku', 'longDescription', '当所有的胡椒都干了和吸烟，它被认为是像个告密者。正常情况下，带着一种皱巴巴的、棕色的、烟熏的chipotle提供一种通常使用的甜味加入烟熏酱、炖菜和腌料。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-559, 5, 'zh_CN', 'Sku', 'name', '亡灵节哈瓦那辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-560, 5, 'zh_CN', 'Sku', 'longDescription', '如果你想要热，那就选择辣椒吧。哈巴内罗原产于加勒比海、尤卡坦半岛和南美北部海岸颜色从浅绿色到鲜红色。哈瓦那的脂肪热，独特的味道和香味，使它成为智利爱好者的最爱。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-561, 6, 'zh_CN', 'Sku', 'name', '亡灵节苏格兰辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-562, 6, 'zh_CN', 'Sku', 'longDescription', '苏格兰帽通常与哈瓦那帽混淆，它的尖端与哈瓦那帽完全相反。l’extré mité哈瓦那角。苏格兰威士忌帽有多种颜色，从绿色到橙色，是西印度群岛和巴巴多斯胡椒酱的主食。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-563, 7, 'zh_CN', 'Sku', 'name', '绿幽灵');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-564, 7, 'zh_CN', 'Sku', 'longDescription', '用世界上最辣的Naga Bhut Jolokia辣椒制成。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-565, 8, 'zh_CN', 'Sku', 'name', '辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-566, 8, 'zh_CN', 'Sku', 'longDescription', '你穿上靴子，这种热酱就赢得了那些喜欢特别热酱的人的名字。你会发现这是一种真正独特的辛辣味道，而不是普通塔巴斯科胡椒酱中不可抗拒的辛辣在这个产品中testéà28.5万斯科维尔单位。然后，在马鞍上进行一次难忘的旅行。为了向你保证我们给你带来了最漂亮的哈瓦那胡椒酱，我们去了中美洲伯利兹的玛雅山麓。本品为entiè种à只使用新鲜蔬菜和所有天然成分。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-567, 9, 'zh_CN', 'Sku', 'name', '世界末日结束了');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-568, 9, 'zh_CN', 'Sku', 'longDescription', '地狱在爆炸，火和硫磺在下雨?准备遇到你的机器?');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-569, 10, 'zh_CN', 'Sku', 'name', 'Chilemeister博士的疯狂辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-570, 10, 'zh_CN', 'Sku', 'longDescription', '这是给那些喜欢高温的人的处方。奇勒梅斯特博士小心谨慎。疼痛会变成依赖性!');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-571, 11, 'zh_CN', 'Sku', 'name', '牛鼻子牛仔辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-572, 11, 'zh_CN', 'Sku', 'longDescription', '在那里，把它捆起来。比白马还热!撒上肉、海鲜和蔬菜。用作烧烤酱或任何需要辛辣味道的食物的添加剂。从几滴开始，一直工作到所需的味道。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-573, 12, 'zh_CN', 'Sku', 'name', '路易斯安纳咖啡黑酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-574, 12, 'zh_CN', 'Sku', 'longDescription', '我们卖的最不寻常的酱汁之一。原来是一种古老的卡津酱，这是黑的版本。这很好，但是你得到了肉桂和丁香的巨大成功，还有卡宴的温暖。用它来做所有的食物给这种卡津氛围。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-575, 13, 'zh_CN', 'Sku', 'name', '牛鼻冒烟辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-576, 13, 'zh_CN', 'Sku', 'longDescription', '德州什么都比这儿大，就连喷着辣酱的公牛也不例外!沐浴在他们称之为Ole 96er的德州牛排上，或者你的飞机上的简蔬菜上。如果你像我一样喜欢从头开始做烧烤酱，你可以用牛鼻冒烟辣椒酱作为补充。红辣椒和辣椒使扁桃体刺激它的著名的味道和红热的热度。牛鼻冒烟辣椒酱只要一滴就能让你的肠子爆开!');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-577, 14, 'zh_CN', 'Sku', 'name', '辣椒酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-578, 14, 'zh_CN', 'Sku', 'longDescription', '这种酱汁的味道来自陈年的辣椒和甘蔗醋。它可以提升任何一餐的味道。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-579, 15, 'zh_CN', 'Sku', 'name', '烤蒜辣酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-580, 15, 'zh_CN', 'Sku', 'longDescription', '这种酱汁的味道来自陈年的辣椒和甘蔗醋。它可以提升任何一餐的味道。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-581, 16, 'zh_CN', 'Sku', 'name', '苏格兰帽辣酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-582, 16, 'zh_CN', 'Sku', 'longDescription', '这种酱汁的味道来自陈年的辣椒和甘蔗醋。它可以提升任何一餐的味道。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-583, 17, 'zh_CN', 'Sku', 'name', '苏格兰帽辣酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-584, 17, 'zh_CN', 'Sku', 'longDescription', '这种酱汁的味道来自陈年的辣椒和甘蔗醋。它可以提升任何一餐的味道。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-585, 18, 'zh_CN', 'Sku', 'name', '苏格兰帽辣酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-586, 18, 'zh_CN', 'Sku', 'longDescription', '这种酱汁的味道来自古老的辣椒和甘蔗醋。它将改善大多数食物的味道。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-587, 19, 'zh_CN', 'Sku', 'name', '苏格兰帽辣酱');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-588, 19, 'zh_CN', 'Sku', 'longDescription', '这种酱汁的味道来自陈年的辣椒和甘蔗醋。它可以提升任何一餐的味道。');

INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-589, 100, 'zh_CN', 'Sku', 'name', '像哈瓦那衬衫一样(男人)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-590, 100, 'zh_CN', 'Sku', 'longDescription', '收集男人哈巴内罗标准衬衫短袖子，丝印30纯棉，单根，裁切。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-591, 200, 'zh_CN', 'Sku', 'name', '像哈瓦那衬衫一样挥舞(女人)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-592, 200, 'zh_CN', 'Sku', 'longDescription', '收集哈瓦那妇女标准衬衫短袖丝印衬衫à普通单根棉布。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-593, 300, 'zh_CN', 'Sku', 'name', '热病诊所手(男性)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-594, 300, 'zh_CN', 'Sku', 'longDescription', '这件t恤上有图案à男性的手有三种不同的颜色');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-595, 400, 'zh_CN', 'Sku', 'name', '热病诊所手拉手(女性)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-596, 400, 'zh_CN', 'Sku', 'longDescription', '这件t恤上有图案à女性的手有三种不同的颜色。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-597, 500, 'zh_CN', 'Sku', 'name', '热(男性)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-598, 500, 'zh_CN', 'Sku', 'longDescription', '你不只是有我们的吉祥物吗?今天把衬衫弄干净。');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-599, 600, 'zh_CN', 'Sku', 'name', '热(女性)');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-600, 600, 'zh_CN', 'Sku', 'longDescription', '你不只是有我们的吉祥物吗?今天把衬衫弄干净。');

-----------------------------------------------------------------------------------------------------------------------------------
-- SEARCH FACET TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-680, 1, 'zh_CN', 'SearchFacet', 'label', '制造商');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-681, 2, 'zh_CN', 'SearchFacet', 'label', '热度');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-682, 3, 'zh_CN', 'SearchFacet', 'label', '价格');


-----------------------------------------------------------------------------------------------------------------------------------
-- FULFILLMENT OPTIONS TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-691, 1, 'zh_CN', 'FulfillmentOption', 'name', '标准');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-692, 1, 'zh_CN', 'FulfillmentOption', 'longDescription', '5 - 7 Journ&eacute;es');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-693, 2, 'zh_CN', 'FulfillmentOption', 'name', 'Priorit&eacute;');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-694, 2, 'zh_CN', 'FulfillmentOption', 'longDescription', '3 - 5 Journ&eacute;es');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-695, 3, 'zh_CN', 'FulfillmentOption', 'name', '快车');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME,  TRANSLATED_VALUE) VALUES (-696, 3, 'zh_CN', 'FulfillmentOption', 'longDescription', '1 - 2 Journ&eacute;es');

-----------------------------------------------------------------------------------------------------------------------------------
-- MENU ITEM TRANSLATION
-----------------------------------------------------------------------------------------------------------------------------------
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME, TRANSLATED_VALUE) VALUES (-697, 5, 'zh_CN', 'MenuItem', 'label', '新的辣酱?');
INSERT INTO BLC_TRANSLATION (TRANSLATION_ID, ENTITY_ID, LOCALE_CODE, ENTITY_TYPE, FIELD_NAME, TRANSLATED_VALUE) VALUES (-698, 6, 'zh_CN', 'MenuItem', 'label', 'FAQ');