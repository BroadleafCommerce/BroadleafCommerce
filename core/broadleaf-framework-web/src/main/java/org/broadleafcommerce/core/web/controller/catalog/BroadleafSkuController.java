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
package org.broadleafcommerce.core.web.controller.catalog;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.template.TemplateType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.TemplateTypeAware;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.common.web.deeplink.DeepLinkService;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.web.catalog.SkuHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class works in combination with the SkuHandlerMapping which finds a category based upon
 * the passed in URL.
 *
 * @author Joshua Skorton (jskorton)
 */
public class BroadleafSkuController extends BroadleafAbstractController implements Controller, TemplateTypeAware {
    
    protected String defaultSkuView = "catalog/sku";
    protected static String MODEL_ATTRIBUTE_NAME = "sku";
    protected static String ALL_SKUS_ATTRIBUTE_NAME = "blcAllDisplayedSkus";
    
    @Autowired(required = false)
    @Qualifier("blSkuDeepLinkService")
    protected DeepLinkService<Sku> deepLinkService;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView model = new ModelAndView();
        Sku sku = (Sku) request.getAttribute(SkuHandlerMapping.CURRENT_SKU_ATTRIBUTE_NAME);
        assert(sku != null);
        
        model.addObject(MODEL_ATTRIBUTE_NAME, sku);
        Set<Sku> allSkusSet = new HashSet<Sku>();
        allSkusSet.add(sku);
        model.addObject(ALL_SKUS_ATTRIBUTE_NAME, new HashSet<Sku>(allSkusSet));

        addDeepLink(model, deepLinkService, sku);

        if (StringUtils.isNotEmpty(sku.getDisplayTemplate())) {
            model.setViewName(sku.getDisplayTemplate());    
        } else {
            model.setViewName(getDefaultSkuView());
        }
        return model;
    }

    public String getDefaultSkuView() {
        return defaultSkuView;
    }

    public void setDefaultSkuView(String defaultSkuView) {
        this.defaultSkuView = defaultSkuView;
    }
    
    @Override
    public String getExpectedTemplateName(HttpServletRequest request) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null) {
            Sku sku = (Sku) context.getRequest().getAttribute(SkuHandlerMapping.CURRENT_SKU_ATTRIBUTE_NAME);
            if (sku != null && sku.getDisplayTemplate() != null) {
                return sku.getDisplayTemplate();
            }
        }
        return getDefaultSkuView();
    }

    @Override
    public TemplateType getTemplateType(HttpServletRequest request) {
        return TemplateType.SKU;
    }

}
