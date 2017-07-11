/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jacob Mitash
 */
@Component("blLinkedDataServiceFactory")
public class LinkedDataServiceFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Bean
    @Scope("prototype")
    public LinkedDataService categoryLinkedDataService(String url, List<Product> products) {
        CategoryLinkedDataServiceImpl service = new CategoryLinkedDataServiceImpl(url, products);
        initializeService(service);
        return service;
    }

    @Bean
    @Scope("prototype")
    public LinkedDataService defaultLinkedDataService(String url) {
        DefaultLinkedDataServiceImpl service = new DefaultLinkedDataServiceImpl(url);
        initializeService(service);
        return service;
    }

    @Bean
    @Scope("prototype")
    public LinkedDataService homepageLinkedDataService(String url) {
        HomepageLinkedDataServiceImpl service = new HomepageLinkedDataServiceImpl(url);
        initializeService(service);
        return service;
    }

    @Bean
    @Scope("prototype")
    public LinkedDataService productLinkedDataService(String url, Product product) {
        ProductLinkedDataServiceImpl service = new ProductLinkedDataServiceImpl(url, product);
        initializeService(service);
        return service;
    }

    protected void initializeService(LinkedDataService service) {
        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        factory.autowireBean(service);
        factory.initializeBean(service, service.getClass().getSimpleName());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LinkedDataServiceFactory.applicationContext = applicationContext;
    }
}
