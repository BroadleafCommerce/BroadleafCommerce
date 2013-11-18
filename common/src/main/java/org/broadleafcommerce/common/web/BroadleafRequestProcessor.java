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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.RequestDTOImpl;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.thymeleaf.TemplateEngine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;


/**
 * 
 * @author Phillip Verheyden
 * @see {@link BroadleafRequestFilter}
 */
@Component("blRequestProcessor")
public class BroadleafRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    protected final Log LOG = LogFactory.getLog(getClass());

    private static String REQUEST_DTO_PARAM_NAME = BroadleafRequestFilter.REQUEST_DTO_PARAM_NAME;

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
    
    @Override
    public void process(WebRequest request) {
        Site site = siteResolver.resolveSite(request);

        BroadleafRequestContext brc = new BroadleafRequestContext();
        
        brc.setSite(site);
        brc.setWebRequest(request);
        if (site == null) {
            brc.setIgnoreSite(true);
        }
        
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
        brc.setSandbox(currentSandbox);
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

    }

    @Override
    public void postProcess(WebRequest request) {
        ThreadLocalManager.remove();
        //temporary workaround for Thymeleaf issue #18 (resolved in version 2.1)
        //https://github.com/thymeleaf/thymeleaf-spring3/issues/18
        if (thymeleafThreadLocalCleanupEnabled) {
            try {
                Field currentProcessLocale = TemplateEngine.class.getDeclaredField("currentProcessLocale");
                currentProcessLocale.setAccessible(true);
                ((ThreadLocal) currentProcessLocale.get(null)).remove();
    
                Field currentProcessTemplateEngine = TemplateEngine.class.getDeclaredField("currentProcessTemplateEngine");
                currentProcessTemplateEngine.setAccessible(true);
                ((ThreadLocal) currentProcessTemplateEngine.get(null)).remove();
    
                Field currentProcessTemplateName = TemplateEngine.class.getDeclaredField("currentProcessTemplateName");
                currentProcessTemplateName.setAccessible(true);
                ((ThreadLocal) currentProcessTemplateName.get(null)).remove();
            } catch (Throwable e) {
                LOG.warn("Unable to remove Thymeleaf threadlocal variables from request thread", e);
            }
        }
    }
}
