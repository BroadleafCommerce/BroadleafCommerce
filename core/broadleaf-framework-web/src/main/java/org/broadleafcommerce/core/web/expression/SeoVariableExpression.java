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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.web.seo.SeoPropertyService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.Resource;

@Service("blSeoVariableExpression")
@ConditionalOnTemplating
public class SeoVariableExpression implements BroadleafVariableExpression {

    private static final Log LOG = LogFactory.getLog(SeoVariableExpression.class);

    @Resource(name = "blSeoPropertyService")
    protected SeoPropertyService seoPropertyService;

    @Override
    public String getName() {
        return "seo";
    }


    public Map<String, String> getMetaProperties(Category category) {
        return seoPropertyService.getSeoProperties(category);
    }

    public Map<String, String> getMetaProperties(Product product) {
        return seoPropertyService.getSeoProperties(product);
    }

    public Map<String, String> getMetaProperties(PageDTO page) {
        return seoPropertyService.getSeoProperties(page);
    }

}
