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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.RequestDTOImpl;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.sandbox.service.SandBoxService;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.time.FixedTimeSource;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.util.StatusExposingServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * @deprecated In favor of org.broadleafcommerce.common.web.BroadleafRequestFilter.
 * formally component name "blProcessURLFilter"
 * 
 * This filter sets up the CMS system by setting the current sandbox, locale, time of day, and languageCode
 * that used by content items.
 * <p/>
 * After setting up content variables, it checks to see if a request can be processed by an instance of
 * URLProcessor and if so, delegates the request to that processor.
 *
 * This filter creates an internal cache to quickly determine if the request should be processed
 * by an instance of URLProcessor or be passed to the next filter in the filter chain.    The
 * cache settings (including expiration seconds, maximum elements, and concurrency) can be
 * configured via Spring at startup.   See {@code com.google.common.cache.CacheBuilder} for more information
 * on these parameters.
 *
 * @author bpolster
 */
public class BroadleafProcessURLFilter extends OncePerRequestFilter {
    private final Log LOG = LogFactory.getLog(BroadleafProcessURLFilter.class);

    // List of URLProcessors
    private List<URLProcessor> urlProcessorList = new ArrayList<URLProcessor>();

    // Cache-settings
    //   by default, expire cache every four hours (4 hours * 60 minutes * 60 seconds)
    private int cacheExpirationSeconds = 4 * 60 * 60;
    private int maxCacheElements = 10000;
    private int maxCacheConcurrency = 3;
    private Cache<String, URLProcessor> urlCache;


    @Resource(name = "blSandBoxService")
    private SandBoxService sandBoxService;

    @Resource(name = "blLocaleService")
    private LocaleService localeService;

    protected Boolean sandBoxPreviewEnabled = true;

    /**
     * Parameter/Attribute name for the current language
     */
    public static String LOCALE_VAR = "blLocale";

    /**
     * Parameter/Attribute name for the current language
     */
    public static String LOCALE_CODE_PARAM = "blLocaleCode";

    /**
     * Parameter/Attribute name for the current language
     */
    public static String REQUEST_DTO = "blRequestDTO";

    /**
     * Request attribute to store the current sandbox
     */
    public static String SANDBOX_VAR = "blSandbox";

    // Properties to manage URLs that will not be processed by this filter.
    private static final String BLC_ADMIN_GWT = "org.broadleafcommerce.admin";
    private static final String BLC_ADMIN_PREFIX = "blcadmin";
    private static final String BLC_ADMIN_SERVICE = ".service";
    private HashSet<String> ignoreSuffixes;

    // Request Parameters and Attributes for Sandbox Mode properties - mostly date values.
    private static String SANDBOX_ID_VAR = "blSandboxId";
    private static String SANDBOX_DATE_TIME_VAR = "blSandboxDateTime";
    private static final SimpleDateFormat CONTENT_DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmm");
    private static final SimpleDateFormat CONTENT_DATE_DISPLAY_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat CONTENT_DATE_DISPLAY_HOURS_FORMATTER = new SimpleDateFormat("h");
    private static final SimpleDateFormat CONTENT_DATE_DISPLAY_MINUTES_FORMATTER = new SimpleDateFormat("mm");
    private static final SimpleDateFormat CONTENT_DATE_PARSE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
    private static String SANDBOX_DATE_TIME_RIBBON_OVERRIDE_PARAM = "blSandboxDateTimeRibbonOverride";
    private static final String SANDBOX_DISPLAY_DATE_TIME_DATE_PARAM = "blSandboxDisplayDateTimeDate";
    private static final String SANDBOX_DISPLAY_DATE_TIME_HOURS_PARAM = "blSandboxDisplayDateTimeHours";
    private static final String SANDBOX_DISPLAY_DATE_TIME_MINUTES_PARAM = "blSandboxDisplayDateTimeMinutes";
    private static final String SANDBOX_DISPLAY_DATE_TIME_AMPM_PARAM = "blSandboxDisplayDateTimeAMPM";


    /**
     * (non-Javadoc)
     *
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (!shouldProcessURL(request, request.getRequestURI())) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Process URL not processing URL " + request.getRequestURI());
            }
            filterChain.doFilter(request, response);
            return;
        }

        final String requestURIWithoutContext;

        if (request.getContextPath() != null) {
            requestURIWithoutContext = request.getRequestURI().substring(request.getContextPath().length());
        } else {
            requestURIWithoutContext = request.getRequestURI();
        }


        if (LOG.isTraceEnabled()) {
            LOG.trace("Process URL Filter Begin " + requestURIWithoutContext);
        }

        if (request.getAttribute(REQUEST_DTO) == null) {
            request.setAttribute(REQUEST_DTO, new RequestDTOImpl(request));
        }

        Site site = determineSite(request);
        SandBox currentSandbox = determineSandbox(request, site);

        BroadleafRequestContext brc = new BroadleafRequestContext();
        brc.setLocale(determineLocale(request, site));
        brc.setSandBox(currentSandbox);
        brc.setRequest(request);
        brc.setResponse(response);
        BroadleafRequestContext.setBroadleafRequestContext(brc);

        try {
            URLProcessor urlProcessor = null;

            if (isProduction(currentSandbox)) {
                try {
                    urlProcessor = lookupProcessorFromCache(requestURIWithoutContext);
                } catch (ExecutionException e) {
                    LOG.error(e);
                }
            }

            if (urlProcessor == null) {
                urlProcessor = determineURLProcessor(requestURIWithoutContext);
            }

            if (urlProcessor instanceof NullURLProcessor) {
                // Pass request down the filter chain
                if (LOG.isTraceEnabled()) {
                    LOG.trace("URL not being processed by a Broadleaf URLProcessor " + requestURIWithoutContext);
                }
                StatusExposingServletResponse sesResponse = new StatusExposingServletResponse(response);
                filterChain.doFilter(request, sesResponse);
                if (sesResponse.getStatus() == sesResponse.SC_NOT_FOUND) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Page not found.  Unable to render " + requestURIWithoutContext);
                    }
                    urlCache.invalidate(requestURIWithoutContext);
                }
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("URL about to be processed by a Broadleaf URLProcessor " + requestURIWithoutContext);
                }                
                urlProcessor.processURL(requestURIWithoutContext);
            }
        } finally {
            // If the system-time was overridden, set it back to normal
            SystemTime.resetLocalTimeSource();
        }

    }


    /**
     * Returns true if the passed in sandbox is null or is of type SandBoxType.PRODUCTION.
     *
     * @param sandbox
     * @return
     */
    private boolean isProduction(SandBox sandbox) {
        return (sandbox == null) || (SandBoxType.PRODUCTION.equals(sandbox));
    }


    /**
     * Builds a cache for each URL that determines how it should be processed.
     *
     * @return
     */
    private URLProcessor lookupProcessorFromCache(String requestURIWithoutContextPath) throws ExecutionException {
        if (urlCache == null) {
            urlCache = CacheBuilder.newBuilder()
                   .maximumSize(maxCacheElements)
                   .concurrencyLevel(maxCacheConcurrency)
                   .expireAfterWrite(cacheExpirationSeconds, TimeUnit.SECONDS)
                   .build(new CacheLoader<String,URLProcessor>() {
                        public URLProcessor load(String key) throws IOException, ServletException {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Loading URL processor into Cache");
                            }
                            return determineURLProcessor(key);
                        }
                   });
        }
        return urlCache.getIfPresent(requestURIWithoutContextPath);
    }

    private URLProcessor determineURLProcessor(String requestURI) {
         for (URLProcessor processor: getUrlProcessorList()) {
            if (processor.canProcessURL(requestURI)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("URLProcessor found for URI " + requestURI + " - " + processor.getClass().getName());
                }
                return processor;
            }
        }
        // Indicates that this URL is not handled by a URLProcessor
        return NullURLProcessor.getInstance();
    }

    /**
     * Determines if the passed in URL should be processed by the content management system.
     * <p/>
     * By default, this method returns false for any BLC-Admin URLs and service calls and for all
     * common image/digital mime-types (as determined by an internal call to {@code getIgnoreSuffixes}.
     * <p/>
     * This check is called with the {@code doFilterInternal} method to short-circuit the content
     * processing which can be expensive for requests that do not require it.
     *
     * @param requestURI - the HttpServletRequest.getRequestURI
     * @return true if the {@code HttpServletRequest} should be processed
     */
    protected boolean shouldProcessURL(HttpServletRequest request, String requestURI) {
        if (requestURI.contains(BLC_ADMIN_GWT) || 
            requestURI.endsWith(BLC_ADMIN_SERVICE) ||
            requestURI.contains(BLC_ADMIN_PREFIX)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("BroadleafProcessURLFilter ignoring admin request URI " + requestURI);
            }
            return false;
        } else {
            int pos = requestURI.lastIndexOf(".");
            if (pos > 0) {
                String suffix = requestURI.substring(pos);
                if (getIgnoreSuffixes().contains(suffix.toLowerCase())) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("BroadleafProcessURLFilter ignoring request due to suffix " + requestURI);
                    }
                    return false;
                }
            }
        }
        return true;
    }


    private SandBox determineSandbox(HttpServletRequest request, Site site) {
        SandBox currentSandbox = null;
        if (!sandBoxPreviewEnabled) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Sandbox preview disabled. Setting sandbox to production");
            }
            request.setAttribute(SANDBOX_VAR, currentSandbox);
        } else {
            Long sandboxId = null;
            if (request.getParameter("blSandboxDateTimeRibbonProduction") == null) {
                sandboxId = lookupSandboxId(request);
            } else {
                request.getSession().removeAttribute(SANDBOX_DATE_TIME_VAR);
                request.getSession().removeAttribute(SANDBOX_ID_VAR);
            }
            if (sandboxId != null) {
                currentSandbox = sandBoxService.retrieveSandBoxById(sandboxId);
                request.setAttribute(SANDBOX_VAR, currentSandbox);
                if (currentSandbox != null && !SandBoxType.PRODUCTION.equals(currentSandbox.getSandBoxType())) {
                    setContentTime(request);
                }
            }

//            if (currentSandbox == null && site != null) {
//                currentSandbox = site.getProductionSandbox();
//            }
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Serving request using sandbox: " + currentSandbox);
        }

        Date currentSystemDateTime = SystemTime.asDate(true);
        Calendar sandboxDateTimeCalendar = Calendar.getInstance();
        sandboxDateTimeCalendar.setTime(currentSystemDateTime);
        request.setAttribute(SANDBOX_DISPLAY_DATE_TIME_DATE_PARAM, CONTENT_DATE_DISPLAY_FORMATTER.format(currentSystemDateTime));
        request.setAttribute(SANDBOX_DISPLAY_DATE_TIME_HOURS_PARAM, CONTENT_DATE_DISPLAY_HOURS_FORMATTER.format(currentSystemDateTime));
        request.setAttribute(SANDBOX_DISPLAY_DATE_TIME_MINUTES_PARAM, CONTENT_DATE_DISPLAY_MINUTES_FORMATTER.format(currentSystemDateTime));
        request.setAttribute(SANDBOX_DISPLAY_DATE_TIME_AMPM_PARAM, sandboxDateTimeCalendar.get(Calendar.AM_PM));
        return currentSandbox;
    }

    /**
     * If another filter has already set the language as a request attribute, that will be honored.
     * Otherwise, the request parameter is checked followed by the session attribute.
     *
     * @param request
     * @param site
     * @return
     */
    private Locale determineLocale(HttpServletRequest request, Site site) {

        Locale locale = null;

        // First check for request attribute
        locale = (Locale) request.getAttribute(LOCALE_VAR);

        // Second, check for a request parameter
        if (locale == null && request.getParameter(LOCALE_CODE_PARAM) != null) {
            String localeCode = request.getParameter(LOCALE_CODE_PARAM);
            locale = localeService.findLocaleByCode(localeCode);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find locale by param " + localeCode + " resulted in " + locale);
            }
        }

        // Third, check the session
        if (locale == null) {
            HttpSession session = request.getSession(true);
            if (session != null) {
                locale = (Locale) session.getAttribute(LOCALE_VAR);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find locale from session resulted in " + locale);
            }
        }

        // Finally, use the default
        if (locale == null) {
            locale = localeService.findDefaultLocale();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Locale set to default locale " + locale);
            }
        }

        request.setAttribute(LOCALE_VAR, locale);
        request.getSession().setAttribute(LOCALE_VAR, locale);

        Map<String, Object> ruleMap = (Map<String, Object>) request.getAttribute("blRuleMap");
        if (ruleMap == null) {
            ruleMap = new HashMap<String, Object>();
            request.setAttribute("blRuleMap", ruleMap);
        }
        ruleMap.put("locale", locale);
        return locale;
    }


    private Long lookupSandboxId(HttpServletRequest request) {
        String sandboxIdStr = request.getParameter(SANDBOX_ID_VAR);
        Long sandboxId = null;

        if (sandboxIdStr != null) {
            try {
                sandboxId = Long.valueOf(sandboxIdStr);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("SandboxId found on request " + sandboxId);
                }
            } catch (NumberFormatException nfe) {
                LOG.warn("blcSandboxId parameter could not be converted into a Long", nfe);
            }
        }

        if (sandboxId == null) {
            // check the session
            HttpSession session = request.getSession(false);
            if (session != null) {
                sandboxId = (Long) session.getAttribute(SANDBOX_ID_VAR);
                if (LOG.isTraceEnabled()) {
                    if (sandboxId != null) {
                        LOG.trace("SandboxId found in session " + sandboxId);
                    }
                }
            }
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(SANDBOX_ID_VAR, sandboxId);
        }
        return sandboxId;
    }

    private void setContentTime(HttpServletRequest request) {
        String sandboxDateTimeParam = request.getParameter(SANDBOX_DATE_TIME_VAR);
        if (sandBoxPreviewEnabled) {
            sandboxDateTimeParam = null;
        }
        Date overrideTime = null;

        try {
            if (request.getParameter(SANDBOX_DATE_TIME_RIBBON_OVERRIDE_PARAM) != null) {
                overrideTime = readDateFromRequest(request);
            } else if (sandboxDateTimeParam != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting date/time using " + sandboxDateTimeParam);
                }
                overrideTime = CONTENT_DATE_FORMATTER.parse(sandboxDateTimeParam);
            }
        } catch (ParseException e) {
            LOG.debug(e);
        }

        if (overrideTime == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                overrideTime = (Date) session.getAttribute(SANDBOX_DATE_TIME_VAR);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting date-time for sandbox mode to " + overrideTime + " for sandboxDateTimeParam = " + sandboxDateTimeParam);
            }
            HttpSession session = request.getSession();
            session.setAttribute(SANDBOX_DATE_TIME_VAR, overrideTime);
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

        String dateString = date + " " + hours + ":" + minutes + " " + ampm;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting date/time using " + dateString);
        }

        Date parsedDate = CONTENT_DATE_PARSE_FORMAT.parse(dateString);
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

    /**
     * Returns a set of suffixes that can be ignored by content processing.   The following
     * are returned:
     * <p/>
     * <B>List of suffixes ignored:</B>
     *
     * ".aif", ".aiff", ".asf", ".avi", ".bin", ".bmp", ".doc", ".eps", ".gif", ".hqx", ".jpg", ".jpeg", ".mid", ".midi", ".mov", ".mp3", ".mpg", ".mpeg", ".p65", ".pdf", ".pic", ".pict", ".png", ".ppt", ".psd", ".qxd", ".ram", ".ra", ".rm", ".sea", ".sit", ".stk", ".swf", ".tif", ".tiff", ".txt", ".rtf", ".vob", ".wav", ".wmf", ".xls", ".zip";
     *
     * @return set of suffixes to ignore.
     */
    protected Set getIgnoreSuffixes() {
        if (ignoreSuffixes == null || ignoreSuffixes.isEmpty()) {
            String[] ignoreSuffixList = {".aif", ".aiff", ".asf", ".avi", ".bin", ".bmp", ".css", ".doc", ".eps", ".gif", ".hqx", ".js", ".jpg", ".jpeg", ".mid", ".midi", ".mov", ".mp3", ".mpg", ".mpeg", ".p65", ".pdf", ".pic", ".pict", ".png", ".ppt", ".psd", ".qxd", ".ram", ".ra", ".rm", ".sea", ".sit", ".stk", ".swf", ".tif", ".tiff", ".txt", ".rtf", ".vob", ".wav", ".wmf", ".xls", ".zip"};
            ignoreSuffixes = new HashSet<String>(Arrays.asList(ignoreSuffixList));
        }
        return ignoreSuffixes;
    }

    public int getMaxCacheElements() {
        return maxCacheElements;
    }

    public void setMaxCacheElements(int maxCacheElements) {
        this.maxCacheElements = maxCacheElements;
    }

    public int getCacheExpirationSeconds() {
        return cacheExpirationSeconds;
    }

    public void setCacheExpirationSeconds(int cacheExpirationSeconds) {
        this.cacheExpirationSeconds = cacheExpirationSeconds;
    }

    public int getMaxCacheConcurrency() {
        return maxCacheConcurrency;
    }

    public void setMaxCacheConcurrency(int maxCacheConcurrency) {
        this.maxCacheConcurrency = maxCacheConcurrency;
    }

    public List<URLProcessor> getUrlProcessorList() {
        return urlProcessorList;
    }

    public void setUrlProcessorList(List<URLProcessor> urlProcessorList) {
        this.urlProcessorList = urlProcessorList;
    }

    public Boolean getSandBoxPreviewEnabled() {
        return sandBoxPreviewEnabled;
    }

    public void setSandBoxPreviewEnabled(Boolean sandBoxPreviewEnabled) {
        this.sandBoxPreviewEnabled = sandBoxPreviewEnabled;
    }
}
