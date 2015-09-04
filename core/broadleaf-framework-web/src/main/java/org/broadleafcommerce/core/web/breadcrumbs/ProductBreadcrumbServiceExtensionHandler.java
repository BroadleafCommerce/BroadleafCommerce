/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.breadcrumbs;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTOType;
import org.broadleafcommerce.common.breadcrumbs.service.AbstractBreadcrumbServiceExtensionHandler;
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
