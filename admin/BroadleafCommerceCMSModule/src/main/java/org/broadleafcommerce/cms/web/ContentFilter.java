/*
 * Copyright 2008-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.cms.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxType;
import org.broadleafcommerce.openadmin.server.domain.Site;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.broadleafcommerce.openadmin.time.FixedTimeSource;
import org.broadleafcommerce.openadmin.time.SystemTime;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author bpolster
 *
 */
@Component("blContentFilter")
public class ContentFilter extends OncePerRequestFilter {

    private static final Log logger = LogFactory.getLog(ContentFilter.class);
    private static final SimpleDateFormat CONTENT_DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmm");
    private static final SimpleDateFormat CONTENT_DATE_DISPLAY_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat CONTENT_DATE_DISPLAY_HOURS_FORMATTER = new SimpleDateFormat("h");
    private static final SimpleDateFormat CONTENT_DATE_DISPLAY_MINUTES_FORMATTER = new SimpleDateFormat("mm");
	private static final SimpleDateFormat CONTENT_DATE_PARSE_FORMAT = new SimpleDateFormat("MM/dd/yyyyhmm");
	   

    public static String SANDBOX_ID_PARAM = "blSandboxId";
    public static String SANDBOX_DATE_TIME_PARAM = "blSandboxDateTime";
    public static String SANDBOX_DATE_TIME_RIBBON_OVERRIDE_PARAM = "blSandboxDateTimeRibbonOverride";
    public static final String SANDBOX_DISPLAY_DATE_TIME_DATE_PARAM = "blSandboxDisplayDateTimeDate";
    public static final String SANDBOX_DISPLAY_DATE_TIME_HOURS_PARAM = "blSandboxDisplayDateTimeHours";
    public static final String SANDBOX_DISPLAY_DATE_TIME_MINUTES_PARAM = "blSandboxDisplayDateTimeMinutes";
    public static final String SANDBOX_DISPLAY_DATE_TIME_AMPM_PARAM = "blSandboxDisplayDateTimeAMPM";

    public static String SESSION_SANDBOX_VAR = "BLC_SANDBOX_ID";
    public static String SESSION_SANDBOX_TIME_VAR = "BLC_SANDBOX_TIME";

    private static final String BLC_PAGE = "BLC_PAGE";

    @Resource(name="blSandBoxService")
    private SandBoxService sandBoxService;

    @Resource(name="blPageService")
    private PageService pageService;

    private String blcPageTemplateDirectory ="/WEB-INF/jsp/templates";


	/** (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Site site = determineSite(request);
        SandBox currentSandbox = determineSandbox(request, site);

        try {
            if (! checkForContentManagedPage(currentSandbox, request, response)) {
		        filterChain.doFilter(request, response);
            }
        } finally {
            // If the system-time was overridden, set it back to normal
            SystemTime.resetLocalTimeSource();
        }
	}

    private boolean checkForContentManagedPage(SandBox currentSandbox, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String uriWithoutContextPath = request.getRequestURI().substring(request.getContextPath().length());
    	Page p = pageService.findPageByURI(currentSandbox, uriWithoutContextPath);
        if (p != null) {
        	String templateJSPPath = blcPageTemplateDirectory + p.getPageTemplate().getTemplatePath() + ".jsp";
            logger.debug("Forwarding to page: " + templateJSPPath);
            request.setAttribute(BLC_PAGE, p);
            RequestDispatcher rd = request.getRequestDispatcher(templateJSPPath);
            rd.forward(request, response);
            return true;
        }
        return false;
    }

    private SandBox determineSandbox(HttpServletRequest request, Site site) {
        SandBox currentSandbox = null;
        Long sandboxId = lookupSandboxId(request);
        if (sandboxId != null) {
            currentSandbox =  sandBoxService.retrieveSandboxById(sandboxId);
            if (currentSandbox != null && ! SandBoxType.PRODUCTION.equals(currentSandbox.getSandBoxType())) {
                // For non-production sandboxes, the system time can be modified to enable preview
                // functionality
                setContentTime(request);
            } else {
                logger.debug("Using production sandbox.");
            }
        }

        if (currentSandbox == null) {
            currentSandbox = sandBoxService.retrieveProductionSandBox(site);
        }

        logger.debug("Serving request using sandbox: " + currentSandbox);
        
        Date currentSystemDateTime = SystemTime.asDate(true);
        Calendar sandboxDateTimeCalendar = Calendar.getInstance();
        sandboxDateTimeCalendar.setTime(currentSystemDateTime); 
        request.setAttribute(SANDBOX_DISPLAY_DATE_TIME_DATE_PARAM,  CONTENT_DATE_DISPLAY_FORMATTER.format(currentSystemDateTime));
        request.setAttribute(SANDBOX_DISPLAY_DATE_TIME_HOURS_PARAM, CONTENT_DATE_DISPLAY_HOURS_FORMATTER.format(currentSystemDateTime));
        request.setAttribute(SANDBOX_DISPLAY_DATE_TIME_MINUTES_PARAM, CONTENT_DATE_DISPLAY_MINUTES_FORMATTER.format(currentSystemDateTime));
        request.setAttribute(SANDBOX_DISPLAY_DATE_TIME_AMPM_PARAM, sandboxDateTimeCalendar.get(Calendar.AM_PM));
        return currentSandbox;
    }


    private Long lookupSandboxId(HttpServletRequest request) {
        String sandboxIdStr = request.getParameter(SANDBOX_ID_PARAM);
        Long sandboxId = null;

        if (sandboxIdStr != null) {
            try {
                sandboxId = Long.valueOf(sandboxIdStr);
            } catch(NumberFormatException nfe) {
                logger.debug("blcSandboxId parameter could not be converted into a Long", nfe);
            }
        }

        if (sandboxId == null) {
            // check the session
            HttpSession session = request.getSession(false);
            if (session != null) {
                sandboxId = (Long) session.getAttribute(SESSION_SANDBOX_VAR);
            }
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_SANDBOX_VAR, sandboxId);
        }
        return sandboxId;
    }

    private void setContentTime(HttpServletRequest request) {
        String sandboxDateTimeParam = request.getParameter(SANDBOX_DATE_TIME_PARAM);
        Date overrideTime = null;

        try {
        	if (request.getParameter(SANDBOX_DATE_TIME_RIBBON_OVERRIDE_PARAM) != null) {
        		overrideTime = readDateFromRequest(request);
        	} else if (sandboxDateTimeParam != null) {
	            overrideTime = CONTENT_DATE_FORMATTER.parse(sandboxDateTimeParam);
        	}
        } catch (ParseException e) {
        	logger.debug(e);
        }

        if (overrideTime == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                overrideTime = (Date) session.getAttribute(SESSION_SANDBOX_TIME_VAR);
            }
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_SANDBOX_TIME_VAR, overrideTime);
        }

  
        if (overrideTime != null) {
            FixedTimeSource ft = new FixedTimeSource(overrideTime.getTime());
            SystemTime.setLocalTimeSource(ft);
        } else {
            SystemTime.resetLocalTimeSource();
        }


    }

    private Date readDateFromRequest(HttpServletRequest request) throws ParseException {
    	String date = request.getParameter(SANDBOX_DISPLAY_DATE_TIME_DATE_PARAM);
    	String minutes = request.getParameter(SANDBOX_DISPLAY_DATE_TIME_MINUTES_PARAM);
    	String hours = request.getParameter(SANDBOX_DISPLAY_DATE_TIME_HOURS_PARAM);
    	String ampm = request.getParameter(SANDBOX_DISPLAY_DATE_TIME_AMPM_PARAM);
    	
    	if (StringUtils.isEmpty(minutes)) {
    		minutes = Integer.toString(SystemTime.asCalendar().get(Calendar.MINUTE));
    	}
    	
    	if (StringUtils.isEmpty(hours)) {
    		hours = Integer.toString(SystemTime.asCalendar().get(Calendar.HOUR_OF_DAY));
    	}
    	
		Date parsedDate = CONTENT_DATE_PARSE_FORMAT.parse(date + hours + minutes + ampm);
		return parsedDate;
    }

    private Site determineSite(ServletRequest request) {
        /* TODO:  Multi-tennant:  Need to add code that determines the site to support
         SiteService.retrieveAllSites();
         For each site, check the identifier type (e.g. hostname, url, param)
         to determine the current site.
         */
        return null;
    }

    public String getBlcPageTemplateDirectory() {
        return blcPageTemplateDirectory;
    }

    public void setBlcPageTemplateDirectory(String blcPageTemplateDirectory) {
        this.blcPageTemplateDirectory = blcPageTemplateDirectory;
    }

}
