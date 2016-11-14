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
            title = BLCSystemProperty.resolveBooleanSystemProperty("seo.default.to.category", false)
                    ? getCategoryTitle(product.getCategory(), defaultTitle) : getDefaultSystemTitle(defaultTitle);
        }
        return title;
    }

    public String getCategoryTitle(Category category) {
        return getCategoryTitle(category, "");
    }

    public String getCategoryTitle(Category category, String defaultTitle) {
        String title = category.getMetaTitle();
        return StringUtils.isEmpty(title) ? getDefaultSystemTitle(defaultTitle) : title;
    }

    public String getDefaultSystemTitle(String defaultTitle) {
        return BLCSystemProperty.resolveBooleanSystemProperty("apply.site.defaults", false)
                ? BLCSystemProperty.resolveSystemProperty("default.site.title", defaultTitle) : defaultTitle;
    }

    /// Description methods

    public String getProductDescription(Product product) {
        return getProductDescription(product, "");
    }

    public String getProductDescription(Product product, String defaultDescription) {
        String description = product.getMetaDescription();
        if (StringUtils.isEmpty(description)) {
            description = BLCSystemProperty.resolveBooleanSystemProperty("seo.default.to.category", false)
                    ? getCategoryDescription(product.getCategory(), defaultDescription) : getDefaultSystemDescription(defaultDescription);
        }
        return description;
    }

    public String getCategoryDescription(Category category) {
        return getCategoryDescription(category, "");
    }

    public String getCategoryDescription(Category category, String defaultDescription) {
        String description = category.getMetaDescription();
        return StringUtils.isEmpty(description) ? getDefaultSystemDescription(defaultDescription) : description;
    }

    public String getDefaultSystemDescription(String defaultDescription) {
        return BLCSystemProperty.resolveBooleanSystemProperty("apply.site.defaults", false)
                ? BLCSystemProperty.resolveSystemProperty("default.site.description", defaultDescription) : defaultDescription;
    }

    // This method is used by any page that does not have a custom description.
    public String getGenericDescription() {
        return BLCSystemProperty.resolveSystemProperty("default.site.description", "TESTING");
    }
}
