/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.cms.web.PageHandlerMapping;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.template.TemplateOverrideExtensionManager;
import org.broadleafcommerce.common.template.TemplateType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.TemplateTypeAware;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.common.web.deeplink.DeepLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class works in combination with the PageHandlerMapping which finds a page based upon
 * the request URL.
 *
 * @author bpolster
 */
public class BroadleafPageController extends BroadleafAbstractController implements Controller, TemplateTypeAware {

    protected static String MODEL_ATTRIBUTE_NAME="page";    
    
    @Autowired(required = false)
    @Qualifier("blPageDeepLinkService")
    protected DeepLinkService<PageDTO> deepLinkService;

    @Resource(name = "blTemplateOverrideExtensionManager")
    protected TemplateOverrideExtensionManager templateOverrideManager;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView model = new ModelAndView();
        PageDTO page = (PageDTO) request.getAttribute(PageHandlerMapping.PAGE_ATTRIBUTE_NAME);
        assert page != null;

        model.addObject(MODEL_ATTRIBUTE_NAME, page);
        model.addObject("pageFields", page.getPageFields()); // For convenience
        model.addObject("BLC_PAGE_TYPE", "page");

        String plainTextStr = (String) page.getPageFields().get("plainText");

        if (!StringUtils.isEmpty(plainTextStr)) {
            if (Boolean.valueOf(plainTextStr)) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain");
            }
        }
        
        String templatePath = page.getTemplatePath();
        
        // Allow extension managers to override the path.
        ExtensionResultHolder<String> erh = new ExtensionResultHolder<String>();
        ExtensionResultStatusType extResult = templateOverrideManager.getProxy().getOverrideTemplate(erh, page);
        if (extResult != ExtensionResultStatusType.NOT_HANDLED) {
            templatePath = erh.getResult();
        }

        model.setViewName(templatePath);
        addDeepLink(model, deepLinkService, page);
        return model;
    }

    @Override
    public String getExpectedTemplateName(HttpServletRequest request) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null) {
            PageDTO page = (PageDTO) request.getAttribute(PageHandlerMapping.PAGE_ATTRIBUTE_NAME);
            return page.getTemplatePath();
        }
        return "";
    }

    @Override
    public TemplateType getTemplateType(HttpServletRequest request) {
        return TemplateType.PAGE;
    }

}
