/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.core.config;

import org.broadleafcommerce.common.config.FrameworkCommonClasspathPropertySource;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.dao.CategoryDaoImpl;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.dao.ProductDaoImpl;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.dao.SkuDaoImpl;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.dao.OfferDaoImpl;
import org.broadleafcommerce.core.search.redirect.dao.SearchRedirectDao;
import org.broadleafcommerce.core.search.redirect.dao.SearchRedirectDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author Jeff Fischer
 */
@Configuration
public class FrameworkConfig {

    @Order(FrameworkCommonClasspathPropertySource.FRAMEWORK_ORDER)
    public static class FrameworkPropertySource implements FrameworkCommonClasspathPropertySource {

        @Override
        public String getClasspathFolder() {
            return "config/bc/fw/";
        }
        
    }

    @Bean
    public CategoryDao blCategoryDao() {
        CategoryDaoImpl categoryDao = new CategoryDaoImpl();
        categoryDao.setCurrentDateResolution(10000L);
        return categoryDao;
    }

    @Bean
    public ProductDao blProductDao() {
        ProductDaoImpl productDao = new ProductDaoImpl();
        productDao.setCurrentDateResolution(10000L);
        return productDao;
    }

    @Bean
    public SkuDao blSkuDao() {
        SkuDaoImpl skuDao = new SkuDaoImpl();
        skuDao.setCurrentDateResolution(10000L);
        return skuDao;
    }

    @Bean
    public OfferDao blOfferDao() {
        OfferDaoImpl offerDao = new OfferDaoImpl();
        offerDao.setCurrentDateResolution(10000L);
        return offerDao;
    }


    @Bean
    public SearchRedirectDao blSearchRedirectDao() {
        SearchRedirectDaoImpl searchRedirectDao = new SearchRedirectDaoImpl();
        searchRedirectDao.setCurrentDateResolution(10000L);
        return searchRedirectDao;
    }
}
