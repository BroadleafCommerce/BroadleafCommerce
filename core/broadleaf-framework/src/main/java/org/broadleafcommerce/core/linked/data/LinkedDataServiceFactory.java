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

import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jacob Mitash
 */
@Component("blLinkedDataServiceFactory")
public class LinkedDataServiceFactory {

    @Autowired
    protected Environment environment;

    @Autowired
    protected BreadcrumbService breadcrumbService;

    @Resource(name = "blRatingService")
    protected RatingService ratingService;

    public LinkedDataService categoryLinkedDataService(String url, List<Product> products) {

        return new CategoryLinkedDataServiceImpl(environment, breadcrumbService, url, products);
    }

    public LinkedDataService defaultLinkedDataService(String url) {
        return new DefaultLinkedDataServiceImpl(environment, breadcrumbService, url);
    }

    public LinkedDataService homepageLinkedDataService(String url) {
        return new HomepageLinkedDataServiceImpl(environment, breadcrumbService, url);
    }

    public LinkedDataService productLinkedDataService(String url, Product product) {
        return new ProductLinkedDataServiceImpl(environment, ratingService, url, product);
    }
}
