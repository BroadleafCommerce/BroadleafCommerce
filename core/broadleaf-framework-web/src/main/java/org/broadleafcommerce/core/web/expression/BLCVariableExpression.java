/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.broadleafcommerce.core.web.expression;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogURLService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * Exposes "blc" to expressions to the Thymeleaf expression context.
 * 
 * This class is intended to be augmented using load time weaving by other modules
 * within Broadleaf.
 * 
 * It provides one function (getDate()) primarily just for testing purposes.   This can
 * be accessed with Thymeleaf as ${#blc.date()}
 * 
 * @author bpolster
 */
public class BLCVariableExpression implements BroadleafVariableExpression {
    
    @Override
    public String getName() {
        return "blc";
    }
    
    @Resource(name = "blCatalogURLService")
    protected CatalogURLService catalogURLService;

    public String relativeURL(Category category) {
        return catalogURLService.buildRelativeCategoryURL(getCurrentUrl(), category);
    }

    public String relativeURL(Product product) {
        return catalogURLService.buildRelativeProductURL(getCurrentUrl(), product);
    }

    public String relativeURL(String baseUrl, Category category) {
        return catalogURLService.buildRelativeCategoryURL(baseUrl, category);
    }

    public String relativeURL(String baseUrl, Product product) {
        return catalogURLService.buildRelativeProductURL(baseUrl, product);
    }

    protected String getCurrentUrl() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        String currentUrl = "";
        if (brc != null && brc.getRequest() != null) {
            currentUrl = brc.getRequest().getRequestURI();

            if (!StringUtils.isEmpty(brc.getRequest().getQueryString())) {
                currentUrl = currentUrl + "?" + brc.getRequest().getQueryString();
            }
        }
        return currentUrl;
    }
}
