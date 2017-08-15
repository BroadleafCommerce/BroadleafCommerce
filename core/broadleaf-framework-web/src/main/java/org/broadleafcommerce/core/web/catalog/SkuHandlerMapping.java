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

import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This handler mapping works with the Sku entity to determine if a sku has been configured for
 * the passed in URL.   
 * 
 * If the URL matches a valid Sku then this mapping returns the handler configured via the 
 * controllerName property or blSkuController by default. 
 *
 * @author Joshua Skorton (jskorton)
 * @since 3.2
 * @see org.broadleafcommerce.core.catalog.domain.Sku
 * @see CatalogService
 */
public class SkuHandlerMapping extends BLCAbstractHandlerMapping {

    public static final String CURRENT_SKU_ATTRIBUTE_NAME = "currentSku";

    protected String defaultTemplateName = "catalog/sku";

    private String controllerName="blSkuController";

    @Resource(name = "blCatalogService")
    private CatalogService catalogService;

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        if (!useSku) {
            return null;
        }

        String requestURIWithoutContext = BLCRequestUtils.getRequestURIWithoutContext(request);
        if (requestURIWithoutContext != null) {
            Sku sku = catalogService.findSkuByURI(requestURIWithoutContext);

            if (sku != null) {
                request.setAttribute(CURRENT_SKU_ATTRIBUTE_NAME, sku);
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
