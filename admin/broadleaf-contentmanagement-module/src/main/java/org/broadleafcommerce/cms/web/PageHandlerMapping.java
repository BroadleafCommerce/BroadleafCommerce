/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.cms.web.controller.BroadleafPageController;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.page.dto.NullPageDTO;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This handler mapping works with the Page entity to determine if a page has been configured for
 * the passed in URL.   
 * 
 * If the URL represents a valid PageUrl, then this mapping returns 
 * 
 * Allows configuration of the controller name to use if a Page was found.
 *
 * @author bpolster
 * @since 2.0
 * @see org.broadleafcommerce.cms.page.domain.Page
 * @see BroadleafPageController
 */
public class PageHandlerMapping extends BLCAbstractHandlerMapping {
    
    private final String controllerName="blPageController";
    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    // The following attribute is set in BroadleafProcessURLFilter
    public static final String REQUEST_DTO = "blRequestDTO";
    
    @Resource(name = "blPageService")
    private PageService pageService;
    
    public static final String PAGE_ATTRIBUTE_NAME = "BLC_PAGE";

    @Value("${request.uri.encoding}")
    public String charEncoding;

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null && context.getRequestURIWithoutContext() != null) {
            String requestUri = URLDecoder.decode(context.getRequestURIWithoutContext(), charEncoding);
            
            Boolean internalValidateFindPreviouslySet = false;
            PageDTO page;
            
            try {
                if (!BroadleafRequestContext.getBroadleafRequestContext().getInternalValidateFind()) {
                    BroadleafRequestContext.getBroadleafRequestContext().setInternalValidateFind(true);
                    internalValidateFindPreviouslySet = true;
                }
                page = pageService.findPageByURI(context.getLocale(), requestUri, buildMvelParameters(request), context.isSecure());

            } finally {
                if (internalValidateFindPreviouslySet) {
                    BroadleafRequestContext.getBroadleafRequestContext().setInternalValidateFind(false);
                }
            }

            if (page != null && ! (page instanceof NullPageDTO)) {
                context.getRequest().setAttribute(PAGE_ATTRIBUTE_NAME, page);
                return controllerName;
            }
        }
        return null;
    }
    
     /**
     * MVEL is used to process the content targeting rules.
     *
     *
     * @param request
     * @return
     */
    private Map<String,Object> buildMvelParameters(HttpServletRequest request) {
        TimeDTO timeDto = new TimeDTO(SystemTime.asCalendar());
        RequestDTO requestDto = (RequestDTO) request.getAttribute(REQUEST_DTO);

        Map<String, Object> mvelParameters = new HashMap<String, Object>();
        mvelParameters.put("time", timeDto);
        mvelParameters.put("request", requestDto);

        Map<String,Object> blcRuleMap = (Map<String,Object>) request.getAttribute(BLC_RULE_MAP_PARAM);
        if (blcRuleMap != null) {
            for (String mapKey : blcRuleMap.keySet()) {
                mvelParameters.put(mapKey, blcRuleMap.get(mapKey));
            }
        }

        return mvelParameters;
    }
}
