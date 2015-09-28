/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This handler mapping works with the Category entity to determine if a category has been configured for
 * the passed in URL.   
 * 
 * If the URL matches a valid Category then this mapping returns the handler configured via the 
 * controllerName property or blCategoryController by default. 
 *
 * @author bpolster
 * @since 2.0
 * @see org.broadleafcommerce.core.catalog.domain.Category
 * @see CataService
 */
public class CategoryHandlerMapping extends BLCAbstractHandlerMapping {

    private static final Log LOG = LogFactory.getLog(CategoryHandlerMapping.class);

    private String controllerName = "blCategoryController";

    protected String defaultTemplateName = "catalog/category";

    @Resource(name = "blCatalogService")
    private CatalogService catalogService;

    public static final String CURRENT_CATEGORY_ATTRIBUTE_NAME = "category";

    @Value("${request.uri.encoding}")
    public String charEncoding;

    @Override
    protected Object getHandlerInternal(HttpServletRequest request)
            throws Exception {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();

        if (context != null) {
            Category category = null;
            if (allowCategoryResolutionUsingIdParam()) {
                category = findCategoryUsingIdParam(context);
            }

            if (category == null) {
                category = findCategoryUsingUrl(context);
            }

            if (category != null) {
                context.getRequest().setAttribute(CURRENT_CATEGORY_ATTRIBUTE_NAME, category);
                return controllerName;
            }

        }
        return null;
    }

    public String getDefaultTemplateName() {
        return defaultTemplateName;
    }

    public void setDefaultTemplateName(String defaultTemplateName) {
        this.defaultTemplateName = defaultTemplateName;
    }

    protected Category findCategoryUsingIdParam(BroadleafRequestContext context) throws ServletRequestBindingException {
        Long categoryId = ServletRequestUtils.getLongParameter(context.getRequest(), "categoryId");
        if (categoryId != null) {
            Category category = catalogService.findCategoryById(categoryId);
            if (category != null && LOG.isDebugEnabled()) {
                LOG.debug("Obtained the category using ID=" + categoryId);
            }
            return category;
        }
        return null;
    }

    protected Category findCategoryUsingUrl(BroadleafRequestContext context)
            throws ServletRequestBindingException, UnsupportedEncodingException {
        String requestUri = URLDecoder.decode(context.getRequestURIWithoutContext(), charEncoding);
        Category category = catalogService.findCategoryByURI(requestUri);
        if (category != null && LOG.isDebugEnabled()) {
            LOG.debug("Obtained the category using URI=" + requestUri);
        }
        return category;
    }

    public boolean allowCategoryResolutionUsingIdParam() {
        return BLCSystemProperty.resolveBooleanSystemProperty("allowCategoryResolutionUsingIdParam");
    }
}
