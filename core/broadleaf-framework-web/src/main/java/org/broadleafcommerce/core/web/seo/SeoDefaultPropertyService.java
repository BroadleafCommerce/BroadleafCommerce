/*
 * #%L
 * broadleaf-enterprise
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
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

    String getProductTitlePattern(Product product);

    String getCategoryTitlePattern();

    String getTitle(PageDTO page);

    String getType(Product product);

    String getType(Category category);

    String getType(PageDTO page);

    String getProductDescriptionPattern(Product product);

    String getCategoryDescriptionPattern();

    String getDescription(PageDTO page);

    String getUrl(Product product);

    String getUrl(Category category);

    String getUrl(PageDTO page);

    String getImage(Product product);

    String getImage(Category category);

    String getImage(PageDTO page);

    String getCanonicalUrl(Product product);

}
