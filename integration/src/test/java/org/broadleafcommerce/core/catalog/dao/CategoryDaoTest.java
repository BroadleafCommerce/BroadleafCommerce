/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.CategoryDaoDataProvider;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.core.catalog.domain.FeaturedProductImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

public class CategoryDaoTest extends TestNGSiteIntegrationSetup {

    @Resource
    private CategoryDao categoryDao;
    @Resource
    private CatalogService catalogService;

    @Test(groups =  {"testSetFeaturedProducts"}, dataProvider="basicCategory", dataProviderClass=CategoryDaoDataProvider.class)
    @Transactional
    public void testSetFeaturedProducts(Category category) {
        category = catalogService.saveCategory(category);

        Sku sku = new SkuImpl();
        sku.setDescription("This thing will change your life");
        sku.setName("Test Product");
        catalogService.saveSku(sku);
        
        Product product = new ProductImpl();
        product.setModel("KGX200");
        product.setDefaultSku(sku);
        product = catalogService.saveProduct(product);

        FeaturedProduct featuredProduct = new FeaturedProductImpl();
        featuredProduct.setCategory(category);
        featuredProduct.setProduct(product);
        featuredProduct.setPromotionMessage("BUY ME NOW!!!!");
        List<FeaturedProduct> featuredProducts = new ArrayList<>();
        featuredProducts.add(featuredProduct);
        category.setFeaturedProducts(featuredProducts);
        category = catalogService.saveCategory(category);

        Category categoryTest = categoryDao.readCategoryById(category.getId());
        FeaturedProduct featuredProductTest = categoryTest.getFeaturedProducts().get(0);

        assert (featuredProductTest.getPromotionMessage() == "BUY ME NOW!!!!");
        assert (featuredProductTest.getProduct().getModel().equals("KGX200"));
    }

}
