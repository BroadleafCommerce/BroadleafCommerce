/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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

/**
 * The SeoDefaultPropertyService is responsible for providing default values for a Product, Category, or Page's SEO properties.
 *
 * @author Chris Kittrell (ckittrell)
 */
public interface SeoDefaultPropertyService {

    /**
     * Returns the default SEO title for the given product, fully resolved to plain text.
     * <p>
     * This replaces {@link #getProductTitlePattern(Product)}: the value is built in Java rather than
     * returned as an expression for the template to evaluate, so no admin-entered content is ever
     * evaluated as a Thymeleaf/SpEL expression.
     */
    String getProductTitle(Product product);

    /**
     * Returns the default SEO meta description for the given product, fully resolved to plain text.
     * @see #getProductTitle(Product)
     */
    String getProductDescription(Product product);

    /**
     * Returns the default SEO title for the given category, fully resolved to plain text.
     * @see #getProductTitle(Product)
     */
    String getCategoryTitle(Category category);

    /**
     * Returns the default SEO meta description for the given category, fully resolved to plain text.
     * @see #getProductTitle(Product)
     */
    String getCategoryDescription(Category category);

    /**
     * @deprecated returns an expression pattern that was historically evaluated in the storefront
     * template, which allowed admin-entered SEO values to be evaluated as expressions. Use
     * {@link #getProductTitle(Product)} instead, which resolves the value in Java.
     */
    @Deprecated
    String getProductTitlePattern(Product product);

    /**
     * @deprecated see {@link #getProductTitlePattern(Product)}; use {@link #getCategoryTitle(Category)}.
     */
    @Deprecated
    String getCategoryTitlePattern();

    String getTitle(PageDTO page);

    String getType(Product product);

    String getType(Category category);

    String getType(PageDTO page);

    /**
     * @deprecated see {@link #getProductTitlePattern(Product)}; use {@link #getProductDescription(Product)}.
     */
    @Deprecated
    String getProductDescriptionPattern(Product product);

    /**
     * @deprecated see {@link #getProductTitlePattern(Product)}; use {@link #getCategoryDescription(Category)}.
     */
    @Deprecated
    String getCategoryDescriptionPattern();

    String getDescription(PageDTO page);

    String getUrl(Product product);

    String getUrl(Category category);

    String getUrl(PageDTO page);

    String getImage(Product product);

    String getImage(Category category);

    String getImage(PageDTO page);

    String getCanonicalUrl(Product product);

    String getPaginationPrevUrl(Product product);

    String getPaginationNextUrl(Product product);

    String getCanonicalUrl(Category category);

    String getPaginationPrevUrl(Category category);

    String getPaginationNextUrl(Category category);

}
