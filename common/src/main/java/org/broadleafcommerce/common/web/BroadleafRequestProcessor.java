/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.RequestDTOImpl;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.Theme;
import org.broadleafcommerce.common.web.exception.HaltFilterChainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Phillip Verheyden
 * @see {@link BroadleafRequestFilter}
 */
@Component("blRequestProcessor")
public class BroadleafRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    protected final Log LOG = LogFactory.getLog(getClass());

    private static String REQUEST_DTO_PARAM_NAME = BroadleafRequestFilter.REQUEST_DTO_PARAM_NAME;
    public static String REPROCESS_PARAM_NAME = "REPROCESS_BLC_REQUEST";

    @Resource(name = "blSiteResolver")
    protected BroadleafSiteResolver siteResolver;

    @Resource(name = "blLocaleResolver")
    protected BroadleafLocaleResolver localeResolver;

    @Resource(name = "blCurrencyResolver")
    protected BroadleafCurrencyResolver currencyResolver;

    @Resource(name = "blSandBoxResolver")
    protected BroadleafSandBoxResolver sandboxResolver;

    @Resource(name = "blThemeResolver")
    protected BroadleafThemeResolver themeResolver;

    @Resource(name = "messageSource")
    protected MessageSource messageSource;

    @Resource(name = "blTimeZoneResolver")
    protected BroadleafTimeZoneResolver broadleafTimeZoneResolver;
    
    @Value("${thymeleaf.threadLocalCleanup.enabled}")
    protected boolean thymeleafThreadLocalCleanupEnabled = true;

    @Resource(name="blEntityExtensionManagers")
    protected Map<String, ExtensionManager> entityExtensionManagers;
    
    @Override
    public void process(WebRequest request) {
        Site site = siteResolver.resolveSite(request);

        BroadleafRequestContext brc = new BroadleafRequestContext();
        
        brc.setSite(site);
        brc.setWebRequest(request);
        if (site == null) {
            brc.setIgnoreSite(true);
        }
        brc.setAdmin(false);

        BroadleafRequestContext.setBroadleafRequestContext(brc);

        Locale locale = localeResolver.resolveLocale(request);
        TimeZone timeZone = broadleafTimeZoneResolver.resolveTimeZone(request);
        BroadleafCurrency currency = currencyResolver.resolveCurrency(request);
        // Assumes BroadleafProcess
        RequestDTO requestDTO = (RequestDTO) request.getAttribute(REQUEST_DTO_PARAM_NAME, WebRequest.SCOPE_REQUEST);
        if (requestDTO == null) {
            requestDTO = new RequestDTOImpl(request);
        }

        SandBox currentSandbox = sandboxResolver.resolveSandBox(request, site);
        
        // When a user elects to switch his sandbox, we want to invalidate the current session. We'll then redirect the 
        // user to the current URL so that the configured filters trigger again appropriately.
        Boolean reprocessRequest = (Boolean) request.getAttribute(BroadleafRequestProcessor.REPROCESS_PARAM_NAME, WebRequest.SCOPE_REQUEST);
        if (reprocessRequest != null && reprocessRequest) {
            LOG.debug("Reprocessing request");
            if (request instanceof ServletWebRequest) {
                HttpServletRequest hsr = ((ServletWebRequest) request).getRequest();
                
                clearBroadleafSessionAttrs(request);
                
                StringBuffer url = hsr.getRequestURL();
                if (hsr.getQueryString() != null) {
                    url.append('?').append(hsr.getQueryString());
                }
                try {
                    ((ServletWebRequest) request).getResponse().sendRedirect(url.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                throw new HaltFilterChainException("Reprocess required, redirecting user");
            }
        }
        
        
        if (currentSandbox != null) {
            SandBoxContext previewSandBoxContext = new SandBoxContext();
            previewSandBoxContext.setSandBoxId(currentSandbox.getId());
            previewSandBoxContext.setPreviewMode(true);
            SandBoxContext.setSandBoxContext(previewSandBoxContext);
        }
        // Note that this must happen after the request context is set up as resolving a theme is dependent on site
        Theme theme = themeResolver.resolveTheme(request);
        brc.setLocale(locale);
        brc.setBroadleafCurrency(currency);
        brc.setSandBox(currentSandbox);
        brc.setTheme(theme);
        brc.setMessageSource(messageSource);
        brc.setTimeZone(timeZone);
        brc.setRequestDTO(requestDTO);
        Map<String, Object> ruleMap = (Map<String, Object>) request.getAttribute("blRuleMap", WebRequest.SCOPE_REQUEST);
        if (ruleMap == null) {
            LOG.trace("Creating ruleMap and adding in Locale.");
            ruleMap = new HashMap<String, Object>();
            request.setAttribute("blRuleMap", ruleMap, WebRequest.SCOPE_REQUEST);
        } else {
            LOG.trace("Using pre-existing ruleMap - added by non standard BLC process.");
        }
        ruleMap.put("locale", locale);

        String adminUserId = request.getParameter(BroadleafRequestFilter.ADMIN_USER_ID_PARAM_NAME);
        if (StringUtils.isNotBlank(adminUserId)) {
            //TODO: Add token logic to secure the admin user id
            brc.setAdminUserId(Long.parseLong(adminUserId));
        }

        brc.getAdditionalProperties().putAll(entityExtensionManagers);
    }

    @Override
    public void postProcess(WebRequest request) {
        ThreadLocalManager.remove();
    }
    
    protected void clearBroadleafSessionAttrs(WebRequest request) {
        request.removeAttribute(BroadleafLocaleResolverImpl.LOCALE_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
        request.removeAttribute(BroadleafCurrencyResolverImpl.CURRENCY_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
        request.removeAttribute(BroadleafTimeZoneResolverImpl.TIMEZONE_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
        request.removeAttribute(BroadleafSandBoxResolver.SANDBOX_ID_VAR, WebRequest.SCOPE_GLOBAL_SESSION);

        // From CustomerStateRequestProcessorImpl, using explicit String because it's out of module
        request.removeAttribute("_blc_anonymousCustomer", WebRequest.SCOPE_GLOBAL_SESSION);
        request.removeAttribute("_blc_anonymousCustomerId", WebRequest.SCOPE_GLOBAL_SESSION);
    }
}
