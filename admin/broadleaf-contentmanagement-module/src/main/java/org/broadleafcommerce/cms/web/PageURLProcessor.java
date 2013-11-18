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
package org.broadleafcommerce.cms.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @deprecated.   Should now use PageHandlerMapping
 * Based on the passed in request, this processor determines if a CMS managed page exists
 * that matches the passed in URL.
 *
 * The {@code ProcessURLFilter} will check it's internal cache to determine which URL processor
 * should be invoked for a passed in URL.  If it is unable to find a matching processor in cache,
 * then it will call each processor in turn to provide an attempt to process the URL.
 *
 * Created by bpolster.
 */
@Component("blPageURLProcessor")
public class PageURLProcessor implements URLProcessor {

    private static final Log LOG = LogFactory.getLog(PageURLProcessor.class);


    @Resource(name = "blPageService")
    private PageService pageService;

    @Resource(name = "blStaticAssetService")
    private StaticAssetService staticAssetService;

    private static final String PAGE_ATTRIBUTE_NAME = "BLC_PAGE";
    
    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    // The following attribute is set in BroadleafProcessURLFilter
    public static final String REQUEST_DTO = "blRequestDTO";

    /**
     * Implementors of this interface will return true if they are able to process the
     * current in request.
     *
     * Implementors of this method will need to rely on the BroadleafRequestContext class
     * which provides access to the current sandbox, locale, request, and response via a
     * threadlocal context
     *
     * @see BroadleafRequestContext
     *
     * @return true if this URLProcessor is able to process the passed in request
     */
    @Override
    public boolean canProcessURL(String key) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        PageDTO p = pageService.findPageByURI(context.getLocale(), key, buildMvelParameters(context.getRequest()), context.isSecure());
        context.getRequest().setAttribute(PAGE_ATTRIBUTE_NAME, p);
        return (p != null);
    }

    /**
     * Determines if the requestURI for the passed in request matches a custom content
     * managed page.   If so, the request is forwarded to the correct page template.
     *
     * The page object will be stored in the request attribute "BLC_PAGE".
     *
     * @param key The URI to process
     *
     * @return false if the url could not be processed
     *
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public boolean processURL(String key) throws IOException, ServletException {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        PageDTO p = (PageDTO) context.getRequest().getAttribute(PAGE_ATTRIBUTE_NAME);
        if (p == null) {
            p = pageService.findPageByURI(context.getLocale(), key, buildMvelParameters(context.getRequest()), context.isSecure());
        }

        if (p != null) {
            String templateJSPPath = p.getTemplatePath();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forwarding to page: " + templateJSPPath);
            }
            context.getRequest().setAttribute(PAGE_ATTRIBUTE_NAME, p);

            RequestDispatcher rd = context.getRequest().getRequestDispatcher(templateJSPPath);
            rd.forward(context.getRequest(), context.getResponse());
            return true;
        }
        return false;
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
