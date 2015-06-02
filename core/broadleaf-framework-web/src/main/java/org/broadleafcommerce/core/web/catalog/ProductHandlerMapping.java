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

import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * This handler mapping works with the Product entity to determine if a product has been configured for
 * the passed in URL.   
 * 
 * If the URL matches a valid Product then this mapping returns the handler configured via the 
 * controllerName property or blProductController by default. 
 *
 * @author bpolster
 * @since 2.0
 * @see org.broadleafcommerce.core.catalog.domain.Product
 * @see CatalogService
 */
public class ProductHandlerMapping extends BLCAbstractHandlerMapping {

    private static final Log LOG = LogFactory.getLog(ProductHandlerMapping.class);

    private final String controllerName = "blProductController";

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Resource(name = "blCatalogService")
    private CatalogService catalogService;

    protected String defaultTemplateName = "catalog/product";

    public static final String CURRENT_PRODUCT_ATTRIBUTE_NAME = "currentProduct";

    @Value("${request.uri.encoding}")
    public String charEncoding;

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        if (useSku) {
            return null;
        }

        boolean usesProductId = BLCSystemProperty.resolveBooleanSystemProperty("product.url.use.id");
        //if usesProductId exists, it is useful to bypass the normal product retrieval mechanism (by URI),
        //beause of URL caching

        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null) {
            Product product = null;
            Long productId = ServletRequestUtils.getLongParameter(context.getRequest(), "productId");
            if (usesProductId && productId!=null) {
                product = catalogService.findProductById(productId);
                LOG.info("obtained the product based on ID=" + productId);
            } else {
                if (context.getRequestURIWithoutContext() != null) {
                    String requestUri = URLDecoder.decode(context.getRequestURIWithoutContext(), charEncoding);
                    product = catalogService.findProductByURI(requestUri);
                    LOG.info("obtained the product based on URI=" + requestUri);
                }
            }
            if (product != null) {
                context.getRequest().setAttribute(CURRENT_PRODUCT_ATTRIBUTE_NAME, product);
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

}
