/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.test.translation.sitejvm;

import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Locale;

import javax.annotation.Resource;

/**
 * Test basic core translation use cases. Confirms core is configured correctly and operational.
 *
 * @author Jeff Fischer
 */
public class TranslationServiceTest extends BaseTest {

    @Resource
    private TranslationService translationService;

    @Resource
    private CatalogService catalogService;

    @Test(groups = {"testTranslation"})
    @Transactional
    public void testTranslation() throws Exception {
        Category category = new CategoryImpl();
        category.setName("Translation");
        category = catalogService.saveCategory(category);

        translationService.save(TranslatedEntity.CATEGORY.getType(), String.valueOf(category.getId()), "name", "es_MX", "es_MX");
        translationService.save(TranslatedEntity.CATEGORY.getType(), String.valueOf(category.getId()), "name", "es", "es");

        String specificTranslation = translationService.getTranslatedValue(category, "name", new Locale("es", "MX"));
        Assert.assertEquals(specificTranslation, "es_MX");

        String generalTranslation = translationService.getTranslatedValue(category, "name", Locale.forLanguageTag("es"));
        Assert.assertEquals(generalTranslation, "es");

        //test a second time to go through cache

        specificTranslation = translationService.getTranslatedValue(category, "name", new Locale("es", "MX"));
        Assert.assertEquals(specificTranslation, "es_MX");

        generalTranslation = translationService.getTranslatedValue(category, "name", Locale.forLanguageTag("es"));
        Assert.assertEquals(generalTranslation, "es");
    }
}
