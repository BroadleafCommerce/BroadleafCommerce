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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.service.BroadleafCurrencyService;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Author: jerryocanas
 * Date: 9/6/12
 */

/**
 * Responsible for returning the currency to use for the current request.
 */
@Component("blCurrencyResolver")
public class BroadleafCurrencyResolverImpl implements BroadleafCurrencyResolver {
    private final Log LOG = LogFactory.getLog(BroadleafCurrencyResolverImpl.class);

    /**
     * Parameter/Attribute name for the current currency code
     */
    public static String CURRENCY_CODE_PARAM = "blCurrencyCode";

    /**
     * Parameter/Attribute name for the current currency
     */
    public static String CURRENCY_VAR = "blCurrency";

    @Resource(name = "blCurrencyService")
    private BroadleafCurrencyService broadleafCurrencyService;

    /**
     * Responsible for returning the currency to use for the current request.
     */
    @Override
    public BroadleafCurrency resolveCurrency(HttpServletRequest request) {
        BroadleafCurrency currency = null;
        HttpSession session = request.getSession(true);

        // 1) Check request for currency
        currency = (BroadleafCurrency) request.getAttribute(CURRENCY_VAR);

        // 2) Check for a request parameter
        if (currency == null && request.getParameter(CURRENCY_CODE_PARAM) != null) {
            String currencyCode = request.getParameter(CURRENCY_CODE_PARAM);
            currency = broadleafCurrencyService.findCurrencyByCode(currencyCode);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find currency by param " + currencyCode + " resulted in " + currency);
            }
        }

        // 3) Check session for currency
        if (currency == null){
            currency = (BroadleafCurrency) session.getAttribute(CURRENCY_VAR);
        }

        // 4) Check locale for currency
        if(currency == null){
            Locale locale = (Locale) session.getAttribute(BroadleafLocaleResolverImpl.LOCALE_VAR);
            if (locale != null){
                currency = locale.getDefaultCurrency();
            }
        }

        // 5) Check default currency from DB
        if(currency == null){
            currency = broadleafCurrencyService.findDefaultBroadleafCurrency();
        }

        session.setAttribute(CURRENCY_VAR, currency);
        return currency;
    }
}
