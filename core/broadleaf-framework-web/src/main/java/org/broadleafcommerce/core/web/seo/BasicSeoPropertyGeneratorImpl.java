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
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The BasicSeoTagGeneratorImpl knows how to generate DOM Elements for the following basic SEO tags:
 * 
 * <ul>
 *     <li>Title</li>
 *     <li>Meta-Description</li>
 * </ul>
 * 
 * @author Chris Kittrell (ckittrell)
 * @author Andre Azzolini (apazzolini)
 */
@Component("blBasicSeoPropertyGenerator")
@ConditionalOnTemplating
public class BasicSeoPropertyGeneratorImpl extends AbstractSeoPropertyGenerator {

    @Override
    public Map<String, String> filterForSeoProperties(Map<String, String> properties) {
        Map<String, String> filteredProperties = new HashMap<>();

        for (String propertyKey : properties.keySet()) {
            if ("title".equals(propertyKey) || "metaDescription".equals(propertyKey)) {
                filteredProperties.put(propertyKey, properties.get(propertyKey));
            }
        }

        return filteredProperties;
    }

    @Override
    public Map<String, String> gatherSeoProperties(Category category) {
        Map<String, String> properties = getSimpleProperties(category);

        String defaultTitle = defaultPropertyService.getCategoryTitlePattern();
        addDefaultTitle(properties, defaultTitle);

        String defaultDescription = defaultPropertyService.getCategoryDescriptionPattern();
        addDefaultDescription(properties, defaultDescription);

        return properties;
    }

    @Override
    public Map<String, String> gatherSeoProperties(Product product) {
        Map<String, String> properties = getSimpleProperties(product);

        String defaultTitle = defaultPropertyService.getProductTitlePattern(product);
        addDefaultTitle(properties, defaultTitle);

        String defaultDescription = defaultPropertyService.getProductDescriptionPattern(product);
        addDefaultDescription(properties, defaultDescription);

        String canonicalUrl = defaultPropertyService.getCanonicalUrl(product);
        properties.put("canonicalUrl", canonicalUrl);

        return properties;
    }

    @Override
    public Map<String, String> gatherSeoProperties(PageDTO page) {
        Map<String, String> properties = getSimpleProperties(page);

        String defaultTitle = defaultPropertyService.getTitle(page);
        addDefaultTitle(properties, defaultTitle);

        String defaultDescription = defaultPropertyService.getDescription(page);
        addDefaultDescription(properties, defaultDescription);

        return properties;
    }

    protected void addDefaultTitle(Map<String, String> properties, String defaultTitle) {
        if (!properties.containsKey("title")) {
            properties.put("title", defaultTitle);
        }
    }

    protected void addDefaultDescription(Map<String, String> properties, String defaultDescription) {
        if (!properties.containsKey("metaDescription")) {
            properties.put("metaDescription", defaultDescription);
        }
    }
}
