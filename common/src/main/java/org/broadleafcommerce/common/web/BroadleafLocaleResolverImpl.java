/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for returning the Locale to use for the current request.
 *
 * @author bpolster
 */
@Component("blLocaleResolver")
public class BroadleafLocaleResolverImpl implements BroadleafLocaleResolver {
    private final Log LOG = LogFactory.getLog(BroadleafLocaleResolverImpl.class);
    
    /**
     * Parameter/Attribute name for the current language
     */
    public static String LOCALE_VAR = "blLocale";

    /**
     * Parameter/Attribute name for the current language
     */
    public static String LOCALE_CODE_PARAM = "blLocaleCode";

    /**
     * Attribute indicating that the LOCALE was pulled from session.   Other filters may want to 
     * behave differently if this is the case.
     */
    public static String LOCALE_PULLED_FROM_SESSION = "blLocalePulledFromSession";

    @Resource(name = "blLocaleService")
    private LocaleService localeService;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return resolveLocale(new ServletWebRequest(request));
    }

    @Override
    public Locale resolveLocale(WebRequest request) {
        Locale locale = null;

        // First check for request attribute
        locale = (Locale) request.getAttribute(LOCALE_VAR, WebRequest.SCOPE_REQUEST);

        // Second, check for a request parameter
        if (locale == null && BLCRequestUtils.getURLorHeaderParameter(request, LOCALE_CODE_PARAM) != null) {
            String localeCode = BLCRequestUtils.getURLorHeaderParameter(request, LOCALE_CODE_PARAM);
            locale = localeService.findLocaleByCode(localeCode);
            if (BLCRequestUtils.isOKtoUseSession(request)) {
                request.removeAttribute(BroadleafCurrencyResolverImpl.CURRENCY_VAR, WebRequest.SCOPE_SESSION);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find locale by param " + localeCode + " resulted in " + locale);
            }
        }

        // Third, check the session
        if (locale == null && BLCRequestUtils.isOKtoUseSession(request)) {
            locale = (Locale) request.getAttribute(LOCALE_VAR, WebRequest.SCOPE_SESSION);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find locale from session resulted in " + locale);
            }

            if (locale != null) {
                request.setAttribute(LOCALE_PULLED_FROM_SESSION, Boolean.TRUE, WebRequest.SCOPE_REQUEST);
            }

        }

        // Finally, use the default
        if (locale == null) {
            locale = localeService.findDefaultLocale();
            if (BLCRequestUtils.isOKtoUseSession(request)) {
                request.removeAttribute(BroadleafCurrencyResolverImpl.CURRENCY_VAR, WebRequest.SCOPE_SESSION);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Locale set to default locale " + locale);
            }
        }
        
        // Set the default locale to override Spring's cookie resolver
        request.setAttribute(LOCALE_VAR, locale, WebRequest.SCOPE_REQUEST);
        java.util.Locale javaLocale = BroadleafRequestContext.convertLocaleToJavaLocale(locale);
        request.setAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME, javaLocale, WebRequest.SCOPE_REQUEST);

        if (BLCRequestUtils.isOKtoUseSession(request)) {
            request.setAttribute(LOCALE_VAR, locale, WebRequest.SCOPE_SESSION);
        }
        return locale;
    }
}
