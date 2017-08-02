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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service("blSeoElementVariableExpression")
@ConditionalOnTemplating
public class SeoElementVariableExpression implements BroadleafVariableExpression {

    private static final Log LOG = LogFactory.getLog(SeoElementVariableExpression.class);

    @Override
    public String getName() {
        return "seoElement";
    }


    public String getSiteSimpleURL() {
        return BLCMessageUtils.getMessage("seo.site.simple.url");
    }

    public String getTitle(Category category) {
        String title = category.getMetaTitle();

        if (StringUtils.isEmpty(title)) {
            title = category.getName();
        }

        return title;
    }

    public String getDescription(Category category) {
        String result = "";

        String metaDescription = category.getMetaDescription();
        String description = category.getLongDescription();

        if (StringUtils.isNotEmpty(metaDescription)) {
            result = metaDescription;
        } else if (StringUtils.isNotEmpty(description)) {
            result = description;
        }

        return result;
    }

    public String getTitle(Product product) {
        String title = product.getMetaTitle();

        if (StringUtils.isEmpty(title)) {
            title = product.getName();
        }

        return title;
    }

    public String getDescription(Product product) {
        String result = "";

        String metaDescription = product.getMetaDescription();
        String description = product.getLongDescription();

        if (StringUtils.isNotEmpty(metaDescription)) {
            result = metaDescription;
        } else if (StringUtils.isNotEmpty(description)) {
            result = description;
        }

        return result;
    }

    public String buildTitleString(List<String> titleElements, String elementDelimiter) {
        titleElements.removeAll(Arrays.asList(null, ""));
        String result = StringUtils.join(titleElements, elementDelimiter);
        return StringUtils.isNotEmpty(result) ? result : getSiteSimpleURL();
    }
}
