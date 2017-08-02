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

import java.util.Map;

/**
 * The SeoPropertyService is responsible for generating the appropriate properties representing the known metadata about that page.
 * 
 * @author Chris Kittrell (ckittrell)
 */
public interface SeoPropertyService {

    /**
     * Returns a Map<String, String> that contains the known additional attributes for the current context. This method
     * understands how to extract additional attributes for products.
     *
     * Note that this method will extract the values of the {@link ProductAttribute}s from their respective entities.
     * It does not filter these attributes, and it is quite likely that some of these attributes will not be SEO related.
     *
     * Individual resolvers for these attributes will skip ones that do not apply.
     *
     * @param product
     * @return the known attributes
     */
    Map<String, String> getSeoProperties(Product product);

    /**
     * Returns a Map<String, String> that contains the known additional attributes for the current context. This method
     * understands how to extract additional attributes for products, categories, and pages.
     *
     * Note that this method will extract the values of the {@link CategoryAttribute}s from their respective entities.
     * It does not filter these attributes, and it is quite likely that some of these attributes will not be SEO related.
     *
     * Individual resolvers for these attributes will skip ones that do not apply.
     *
     * @param category
     * @return the known attributes
     */
    Map<String, String> getSeoProperties(Category category);

    /**
     * Returns a Map<String, String> that contains the known additional attributes for the current context. This method
     * understands how to extract additional attributes for products, categories, and pages.
     *
     * Note that this method will extract the values of the {@link PageAttribute}s from their respective entities.
     * It does not filter these attributes, and it is quite likely that some of these attributes will not be SEO related.
     *
     * Individual resolvers for these attributes will skip ones that do not apply.
     *
     * @param page
     * @return the known attributes
     */
    Map<String, String> getSeoProperties(PageDTO page);

}
