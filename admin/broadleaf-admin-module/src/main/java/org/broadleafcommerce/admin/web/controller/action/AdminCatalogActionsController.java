/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.controller.action;

import org.broadleafcommerce.admin.server.service.AdminCatalogService;
import org.broadleafcommerce.admin.web.controller.entity.AdminProductController;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller that responds to custom catalog actions. These would normally be hooked up in customized controllers like 
 * {@link AdminProductController}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link AdminProductController}
 * 
 */
@Controller("blAdminCatalogActionsController")
public class AdminCatalogActionsController extends AdminAbstractController {
    
    @Resource(name = "blAdminCatalogService")
    protected AdminCatalogService adminCatalogService;
    
    @Autowired
    protected MessageSource messageSource;

    public static String NO_SKUS_GENERATED_KEY = "noSkusGenerated";
    public static String NO_PRODUCT_OPTIONS_GENERATED_KEY = "noProductOptionsConfigured";
    public static String FAILED_SKU_GENERATION_KEY = "errorNeedAllowedValue";
    public static String NUMBER_SKUS_GENERATED_KEY = "numberSkusGenerated";

    /**
     * Invokes a separate service to generate a list of Skus for a particular {@link Product} and that {@link Product}'s
     * Product Options
     */
    @RequestMapping(value = "product/{productId}/{skusFieldName}/generate-skus",
                    method = RequestMethod.GET,
                    produces = "application/json")
    public @ResponseBody Map<String, Object> generateSkus(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable(value = "productId") Long productId,
            @PathVariable(value = "skusFieldName") String skusFieldName) {
        HashMap<String, Object> result = new HashMap<>();
        Integer skusGenerated = adminCatalogService.generateSkusFromProduct(productId);
        
        //TODO: Modify the message "Failed to generate Skus...." to include which Product Option is the offender
        if (skusGenerated == 0) {
            result.put("message", BLCMessageUtils.getMessage(NO_SKUS_GENERATED_KEY));
        } else if (skusGenerated == -1) {
            result.put("message", BLCMessageUtils.getMessage(NO_PRODUCT_OPTIONS_GENERATED_KEY));
        } else if (skusGenerated == -2) {
            result.put("message", BLCMessageUtils.getMessage(FAILED_SKU_GENERATION_KEY));
            result.put("error", "no-allowed-value-error");
        } else {
            result.put("message", skusGenerated + " " + BLCMessageUtils.getMessage(NUMBER_SKUS_GENERATED_KEY));
        }
        
        String url = request.getRequestURL().toString();
        url = url.substring(0, url.indexOf("/generate-skus"));
        
        result.put("skusGenerated", skusGenerated);
        result.put("listGridUrl", url);
        return result;
    }
}
