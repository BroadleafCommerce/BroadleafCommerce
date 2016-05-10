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
package org.broadleafcommerce.cms.web.controller;

import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.file.service.BroadleafFileUtils;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BaseUrlResolver;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.resource.BroadleafContextUtil;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class serves up the Robots.txt file.    The default contents can be overridden by 
 * adding a Page named "/robots.txt" in the BLC admin or DB. 
 *
 * @author bpolster
 */
public class BroadleafRobotsController {

    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    // The following attribute is set in BroadleafProcessURLFilter
    public static final String REQUEST_DTO = "blRequestDTO";

    @Resource(name = "blBaseUrlResolver")
    private BaseUrlResolver baseUrlResolver;

    @Resource(name = "blPageService")
    private PageService pageService;
    
    @Resource(name = "blBroadleafContextUtil")
    protected BroadleafContextUtil blcContextUtil;

    public String getRobotsFile(HttpServletRequest request, HttpServletResponse response) {
    	blcContextUtil.establishThinRequestContext();
    	
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PageDTO page = pageService.findPageByURI(null,
                "/robots.txt", buildMvelParameters(request), isSecure(request));

        if (page != null && page.getPageFields().containsKey("body")) {
            String body = (String) page.getPageFields().get("body");
            body = body.replace("${siteBaseUrl}", baseUrlResolver.getSiteBaseUrl());
            return body;
        } else {
            return getDefaultRobotsTxt();
        }
    }
    
    public boolean isSecure(HttpServletRequest request) {
        boolean secure = false;
        if (request != null) {
             secure = ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
        }
        return secure;
    }

    /**
     * Used to produce a working but simple robots.txt.    Can be overridden in code or by defining a page
     * managed in the Broadleaf CMS named  "/robots.txt"
     * 
     * @return
     */
    protected String getDefaultRobotsTxt() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Using default Broadleaf Commerce robots.txt file").append("\n");
        sb.append("User-agent: *").append("\n");
        sb.append("Disallow:").append("\n");
        String fileLoc = BroadleafFileUtils.appendUnixPaths(baseUrlResolver.getSiteBaseUrl(), "/sitemap.xml.gz");

        sb.append("Sitemap:").append(fileLoc);
        return sb.toString();
    }

    /**
    *
    * @param request
    * @return
    */
    private Map<String, Object> buildMvelParameters(HttpServletRequest request) {
        TimeDTO timeDto = new TimeDTO(SystemTime.asCalendar());
        RequestDTO requestDto = (RequestDTO) request.getAttribute(REQUEST_DTO);

        Map<String, Object> mvelParameters = new HashMap<String, Object>();
        mvelParameters.put("time", timeDto);
        mvelParameters.put("request", requestDto);

        Map<String, Object> blcRuleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM);
        if (blcRuleMap != null) {
            for (String mapKey : blcRuleMap.keySet()) {
                mvelParameters.put(mapKey, blcRuleMap.get(mapKey));
            }
        }

        return mvelParameters;
    }
}
