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

import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
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
    
    private String controllerName="blSkuController";

    @Value("${solr.index.use.sku}")
    protected boolean useSku;
    
    @Resource(name = "blCatalogService")
    private CatalogService catalogService;

    protected String defaultTemplateName = "catalog/sku";

    public static final String CURRENT_SKU_ATTRIBUTE_NAME = "currentSku";

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        if(!useSku) {
            return null;
        }
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null && context.getRequestURIWithoutContext() != null) {
            Sku sku = catalogService.findSkuByURI(context.getRequestURIWithoutContext());
            if (sku != null) {
                context.getRequest().setAttribute(CURRENT_SKU_ATTRIBUTE_NAME, sku);
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
