/*
 * #%L
 * broadleaf-enterprise
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
package org.broadleafcommerce.core.web.seo;

import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryAttribute;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * An {@link AbstractSeoPropertyGenerator} understands how to gather a specific set of SEO properties for the given domain object
 * 
 * @author Chris Kittrell (ckittrell)
 */
public abstract class AbstractSeoPropertyGenerator implements SeoPropertyGenerator {

    @Autowired
    protected Environment env;

    @Resource(name = "blSeoDefaultPropertyService")
    protected SeoDefaultPropertyService defaultPropertyService;


    @Override
    public Map<String, String> gatherSeoProperties(Category category) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public Map<String, String> gatherSeoProperties(Product product) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public Map<String, String> gatherSeoProperties(PageDTO page) {
        throw new UnsupportedOperationException("Not Implemented");
    }


    protected Map<String, String> getSimpleProperties(Category category) {
        Map<String, String> properties = new HashMap<>();

        for (Map.Entry<String, CategoryAttribute> entry : category.getCategoryAttributesMap().entrySet()) {
            properties.put(entry.getKey(), entry.getValue().getValue());
        }

        return filterForSeoProperties(properties);
    }

    protected Map<String, String> getSimpleProperties(Product product) {
        Map<String, String> properties = new HashMap<>();

        for (Map.Entry<String, ProductAttribute> entry : product.getProductAttributes().entrySet()) {
            properties.put(entry.getKey(), entry.getValue().getValue());
        }

        return filterForSeoProperties(properties);
    }

    protected Map<String, String> getSimpleProperties(PageDTO page) {
        Map<String, String> properties = new HashMap<>();

        for (Map.Entry<String, String> entry : page.getPageAttributes().entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        return filterForSeoProperties(properties);
    }

}
