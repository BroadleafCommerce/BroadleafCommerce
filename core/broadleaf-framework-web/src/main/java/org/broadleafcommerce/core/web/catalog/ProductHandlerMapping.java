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

package org.broadleafcommerce.core.web.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null) {
            if (shouldSkipExecution(context)) {
                return null;
            }
            Product product = null;

            if (allowProductResolutionUsingIdParam()) {
                product = findProductUsingIdParam(context);
            }

            if (product == null) {
                product = findProductUsingUrl(context);
            }

            if (product != null) {
                context.getRequest().setAttribute(CURRENT_PRODUCT_ATTRIBUTE_NAME, product);
                return controllerName;
            }
        }
        return null;
    }

    protected Product findProductUsingIdParam(BroadleafRequestContext context) throws ServletRequestBindingException {
        if (context.getRequest() != null) {
            Long productId = ServletRequestUtils.getLongParameter(context.getRequest(), "productId");
            if (productId != null) {
                Product product = catalogService.findProductById(productId);
                if (product != null && LOG.isDebugEnabled()) {
                    LOG.debug("Obtained the product using id=" + productId);
                }
                return product;
            }
        }
        return null;
    }

    protected Product findProductUsingUrl(BroadleafRequestContext context)
            throws ServletRequestBindingException, UnsupportedEncodingException {
        if (context.getRequestURIWithoutContext() == null) {
            return null;
        }
        String requestUri = URLDecoder.decode(context.getRequestURIWithoutContext(), charEncoding);
        Product product = catalogService.findProductByURI(requestUri);
        if (product != null && LOG.isDebugEnabled()) {
            LOG.debug("Obtained the product using URI=" + requestUri);
        }
        return product;
    }

    public String getDefaultTemplateName() {
        return defaultTemplateName;
    }

    public void setDefaultTemplateName(String defaultTemplateName) {
        this.defaultTemplateName = defaultTemplateName;
    }

    public boolean allowProductResolutionUsingIdParam() {
        return BLCSystemProperty.resolveBooleanSystemProperty("allowProductResolutionUsingIdParam");
    }

    public boolean allowCategoryResolutionUsingIdParam() {
        return BLCSystemProperty.resolveBooleanSystemProperty("allowCategoryResolutionUsingIdParam");
    }

    public boolean shouldSkipExecution(BroadleafRequestContext context) throws ServletRequestBindingException {
        if (useSku) {
            return true;
        }

        if (allowCategoryResolutionUsingIdParam() &&
                ServletRequestUtils.getLongParameter(context.getRequest(), "categoryId") != null) {
            return true;
        }

        return false;
    }

}
