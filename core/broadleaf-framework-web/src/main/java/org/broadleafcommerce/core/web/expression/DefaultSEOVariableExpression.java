/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.expression;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;

public class DefaultSEOVariableExpression implements BroadleafVariableExpression {

    @Override
    public String getName() {
        return "seo";
    }

    /// Title methods

    public String getProductTitle(Product product) {
        return getProductTitle(product, "");
    }

    public String getProductTitle(Product product, String defaultTitle) {
        String title = product.getMetaTitle();
        if (StringUtils.isEmpty(title)) {
            if (BLCSystemProperty.resolveBooleanSystemProperty("seo.default.to.category", false)) {
                title = getCategoryTitle(product.getCategory(), defaultTitle);
            } else {
                title = StringUtils.isEmpty(defaultTitle) ? getDefaultSystemTitle() : defaultTitle;
            }
        }
        return title;
    }

    public String getCategoryTitle(Category category) {
        return getCategoryTitle(category, "");
    }

    public String getCategoryTitle(Category category, String defaultTitle) {
        String title = category.getMetaTitle();
        if (StringUtils.isEmpty(title)) {
            title = StringUtils.isEmpty(defaultTitle) ? getDefaultSystemTitle() : defaultTitle;
        }
        return title;
    }

    public String getDefaultSystemTitle() {
        return BLCSystemProperty.resolveSystemProperty("default.site.title", "");
    }

    /// Description methods

    public String getProductDescription(Product product) {
        return getProductDescription(product, "");
    }

    public String getProductDescription(Product product, String defaultDescription) {
        String description = product.getMetaDescription();
        if (StringUtils.isEmpty(description)) {
            if (BLCSystemProperty.resolveBooleanSystemProperty("seo.default.to.category", false)) {
                description = getCategoryDescription(product.getCategory(), defaultDescription);
            } else {
                description = StringUtils.isEmpty(defaultDescription) ? getDefaultSystemDescription() : defaultDescription;
            }
        }
        return description;
    }

    public String getCategoryDescription(Category category) {
        return getCategoryDescription(category, "");
    }

    public String getCategoryDescription(Category category, String defaultDescription) {
        String description = category.getMetaDescription();
        if (StringUtils.isEmpty(description)) {
            description = StringUtils.isEmpty(defaultDescription) ? getDefaultSystemDescription() : defaultDescription;
        }
        return description;
    }

    public String getDefaultSystemDescription() {
        return BLCSystemProperty.resolveSystemProperty("default.site.description", "");
    }
}
