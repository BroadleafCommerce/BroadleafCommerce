/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.cms.page.dto.NullPageDTO;
import org.broadleafcommerce.cms.page.dto.PageDTO;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.cms.web.controller.BroadleafPageController;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BLCAbstractHandlerMapping;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
    
    private String controllerName="blPageController";
    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    // The following attribute is set in BroadleafProcessURLFilter
    public static final String REQUEST_DTO = "blRequestDTO";
    
    @Resource(name = "blPageService")
    private PageService pageService;
    
    public static final String PAGE_ATTRIBUTE_NAME = "BLC_PAGE";        

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null && context.getRequestURIWithoutContext() != null) {
            PageDTO page = pageService.findPageByURI(context.getSandbox(), context.getLocale(), context.getRequestURIWithoutContext(), buildMvelParameters(request), context.isSecure());

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
