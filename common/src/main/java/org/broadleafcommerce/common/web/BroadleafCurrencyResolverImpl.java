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
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafRequestedCurrencyDto;
import org.broadleafcommerce.common.currency.service.BroadleafCurrencyService;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    public BroadleafRequestedCurrencyDto resolveCurrency(HttpServletRequest request) {
        return resolveCurrency(new ServletWebRequest(request));
    }

    @Override
    public BroadleafRequestedCurrencyDto resolveCurrency(WebRequest request) {
        BroadleafCurrency desiredCurrency = null;

        // 1) Check request for currency
        desiredCurrency = (BroadleafCurrency) request.getAttribute(CURRENCY_VAR, WebRequest.SCOPE_REQUEST);

        // 2) Check for a request parameter
        if (desiredCurrency == null && BLCRequestUtils.getURLorHeaderParameter(request, CURRENCY_CODE_PARAM) != null) {
            String currencyCode = BLCRequestUtils.getURLorHeaderParameter(request, CURRENCY_CODE_PARAM);
            desiredCurrency = broadleafCurrencyService.findCurrencyByCode(currencyCode);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find currency by param " + currencyCode + " resulted in " + desiredCurrency);
            }
        }

        // 3) Check session for currency
        if (desiredCurrency == null && BLCRequestUtils.isOKtoUseSession(request)) {
            desiredCurrency = (BroadleafCurrency) request.getAttribute(CURRENCY_VAR, WebRequest.SCOPE_SESSION);
        }

        // 4) Check locale for currency
        if (desiredCurrency == null) {
            Locale locale = (Locale) request.getAttribute(BroadleafLocaleResolverImpl.LOCALE_VAR, WebRequest.SCOPE_REQUEST);
            if (locale != null) {
                desiredCurrency = locale.getDefaultCurrency();
            }
        }

        // 5) Lookup default currency from DB
        BroadleafCurrency defaultCurrency = broadleafCurrencyService.findDefaultBroadleafCurrency();
        if (desiredCurrency == null) {
            desiredCurrency = defaultCurrency;
        }

        // For an out-of-box installation, only one currency is supported, so even though we have a 
        // desired currency, we may not have any prices that support it. 
        BroadleafCurrency currencyToUse = defaultCurrency;

        if (BLCRequestUtils.isOKtoUseSession(request)) {
            request.setAttribute(CURRENCY_VAR, currencyToUse, WebRequest.SCOPE_SESSION);
        }

        BroadleafRequestedCurrencyDto dto = new BroadleafRequestedCurrencyDto(currencyToUse, desiredCurrency);
        return dto;
    }



}
