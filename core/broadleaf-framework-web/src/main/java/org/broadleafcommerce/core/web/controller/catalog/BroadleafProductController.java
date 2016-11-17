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
package org.broadleafcommerce.core.web.controller.catalog;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.cms.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.template.TemplateOverrideExtensionManager;
import org.broadleafcommerce.common.template.TemplateType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.TemplateTypeAware;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.common.web.deeplink.DeepLinkService;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class works in combination with the ProductHandlerMapping which finds a product based upon
 * the passed in URL.
 *
 * @author bpolster
 */
public class BroadleafProductController extends BroadleafAbstractController implements Controller, TemplateTypeAware {
    
    protected String defaultProductView = "catalog/product";
    protected static String MODEL_ATTRIBUTE_NAME = "product";
    protected static String ALL_PRODUCTS_ATTRIBUTE_NAME = "blcAllDisplayedProducts";
    
    @Autowired(required = false)
    @Qualifier("blProductDeepLinkService")
    protected DeepLinkService<Product> deepLinkService;

    @Resource(name="blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;


    @Resource(name = "blTemplateOverrideExtensionManager")
    protected TemplateOverrideExtensionManager templateOverrideManager;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView model = new ModelAndView();
        Product product = (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
        assert(product != null);
        model.addObject(MODEL_ATTRIBUTE_NAME, product);
        Set<Product> allProductsSet = new HashSet<Product>();
        allProductsSet.add(product);
        model.addObject(ALL_PRODUCTS_ATTRIBUTE_NAME, new HashSet<Product>(allProductsSet));
        model.addObject("BLC_PAGE_TYPE", "product");

        addDeepLink(model, deepLinkService, product);
        
        String templatePath = null;

        // Use the products custom template if available
        if (StringUtils.isNotBlank(product.getDisplayTemplate())) {
            templatePath = product.getDisplayTemplate();
        } else {
            // Otherwise, use the controller default.
            templatePath = getDefaultProductView();
        }

        // Allow extension managers to override.
        ExtensionResultHolder<String> erh = new ExtensionResultHolder<String>();
        ExtensionResultStatusType extResult = templateOverrideManager.getProxy().getOverrideTemplate(erh, product);
        if (extResult != ExtensionResultStatusType.NOT_HANDLED) {
            templatePath = erh.getResult();
        }
        
        model.setViewName(templatePath);
        return model;
    }

    public String getDefaultProductView() {
        return defaultProductView;
    }

    public void setDefaultProductView(String defaultProductView) {
        this.defaultProductView = defaultProductView;
    }
    
    @Override
    public String getExpectedTemplateName(HttpServletRequest request) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null) {
            Product product = (Product) context.getRequest().getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
            if (product != null && product.getDisplayTemplate() != null) {
                return product.getDisplayTemplate();
            }
        }
        return getDefaultProductView();
    }

    @Override
    public TemplateType getTemplateType(HttpServletRequest request) {
        return TemplateType.PRODUCT;
    }

}
