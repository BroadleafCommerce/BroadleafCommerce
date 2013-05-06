/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.common.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.exception.SiteNotFoundException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

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
public class BroadleafRequestProcessor implements BroadleafWebRequestProcessor {

    private final Log LOG = LogFactory.getLog(getClass());

    @Resource(name = "blSiteResolver")
    private BroadleafSiteResolver siteResolver;

    @Resource(name = "blLocaleResolver")
    private BroadleafLocaleResolver localeResolver;

    @Resource(name = "blCurrencyResolver")
    private BroadleafCurrencyResolver currencyResolver;

    @Resource(name = "blSandBoxResolver")
    private BroadleafSandBoxResolver sandboxResolver;

    @Resource(name = "blThemeResolver")
    private BroadleafThemeResolver themeResolver;

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    @Resource(name = "blTimeZoneResolver")
    private BroadleafTimeZoneResolver broadleafTimeZoneResolver;

    @Override
    public void process(WebRequest request) throws SiteNotFoundException {
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

}
