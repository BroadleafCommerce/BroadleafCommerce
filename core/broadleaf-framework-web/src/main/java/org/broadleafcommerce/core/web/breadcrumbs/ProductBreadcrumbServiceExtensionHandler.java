/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.web.breadcrumbs;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTOType;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbHandlerDefaultPriorities;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbServiceExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * Adds a product breadcrumb using the product on the BroadleafRequestContext.
 * 
 * @author bpolster
 */
@Service("blProductBreadcrumbServiceExtensionHandler")
public class ProductBreadcrumbServiceExtensionHandler extends AbstractBreadcrumbServiceExtensionHandler {

    @Resource(name = "blBreadcrumbServiceExtensionManager")
    protected BreadcrumbServiceExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    public ExtensionResultStatusType modifyBreadcrumbList(String url, Map<String, String[]> params,
            ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        Product product = determineProduct(url, params, holder);

        if (product != null) {
            BreadcrumbDTO productDto = new BreadcrumbDTO();
            productDto.setText(getNameForProductLink(product));
            productDto.setLink(buildLink(url, params));
            productDto.setType(BreadcrumbDTOType.PRODUCT);
            holder.getResult().add(0, productDto);
        }

        updateContextMap(url, params, holder);

        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    protected Product determineProduct(String url, Map<String, String[]> params,
            ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            return (Product) brc.getRequestAttribute("currentProduct"); // see ProductHandlerMapping
        }
        return null;
    }

    protected String getNameForProductLink(Product product) {
        return product.getName();
    }

    /**
     * Remove the productId and the last fragment of the URL     
     * 
     * @param params
     * @param holder
     */
    protected void updateContextMap(String origUrl, Map<String, String[]> params, ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        Map<String, Object> contextMap = holder.getContextMap();

        if (params != null && params.containsKey(getProductIdParam())) {
            params.remove(getProductIdParam());
            contextMap.put(BreadcrumbServiceExtensionManager.CONTEXT_PARAM_STRIPPED_PARAMS, params);
        }

        int pos = origUrl.lastIndexOf("/");
        if (pos > 0) {
            String newUrl = origUrl.substring(0, pos);
            contextMap.put(BreadcrumbServiceExtensionManager.CONTEXT_PARAM_STRIPPED_URL, newUrl);
        }
    }

    protected String getProductIdParam() {
        return "productId";
    }

    @Override
    public int getDefaultPriority() {
        return BreadcrumbHandlerDefaultPriorities.PRODUCT_CRUMB;
    }
}
