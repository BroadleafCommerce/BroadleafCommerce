/*
 * Copyright 2008-2009 the original author or authors.
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.springframework.stereotype.Component;

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

    @Resource(name = "blLocaleService")
    private LocaleService localeService;  
	
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = null;

        // First check for request attribute
        locale = (Locale) request.getAttribute(LOCALE_VAR);

        // Second, check for a request parameter
        if (locale == null && request.getParameter(LOCALE_CODE_PARAM) != null) {
            String localeCode = request.getParameter(LOCALE_CODE_PARAM);
            locale = localeService.findLocaleByCode(localeCode);
            request.getSession().removeAttribute(BroadleafCurrencyResolverImpl.CURRENCY_VAR);
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
            request.getSession().removeAttribute(BroadleafCurrencyResolverImpl.CURRENCY_VAR);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Locale set to default locale " + locale);
            }
        }

        request.getSession().setAttribute(LOCALE_VAR, locale);
        return locale;
    }
}
