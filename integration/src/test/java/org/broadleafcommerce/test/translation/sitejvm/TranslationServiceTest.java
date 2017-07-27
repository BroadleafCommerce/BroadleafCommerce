/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.test.translation.sitejvm;

import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
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
public class TranslationServiceTest extends TestNGSiteIntegrationSetup {

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
