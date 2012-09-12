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
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.broadleafcommerce.common.pricelist.service.PriceListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Responsible for returning the price list to use for the current request based on Currency.
 *
 * Author: jerryocanas
 * Date: 9/7/12
 */
@Component("blPriceListResovler")
public class CurrencyPriceListResolver implements BroadleafPricelistResolver {
    private final Log LOG = LogFactory.getLog(CurrencyPriceListResolver.class);

    /**
     * Parameter/Attribute name for the current pricelist
     */
    public static String PRICELIST_VAR = "blPricelist";

    /**
     * Parameter/Attribute name for the current currency
     */
    public static String CURRENCY_VAR = "blCurrency";

    /**
     * Parameter/Attribute name for the current pricelist key
     */
    public static String PRICELIST_KEY_PARAM = "blPricelistKey";

    @Resource(name = "blPriceListService")
    PriceListService priceListService;

    @Override
    public PriceList resolvePricelist(HttpServletRequest request) {
        PriceList priceList = null;
        HttpSession session = request.getSession(true);

        // 1) Check request for a pricelist
        priceList = (PriceList) request.getAttribute(PRICELIST_VAR);

        // 2) Check for a request parameter
        if (priceList == null && request.getParameter(PRICELIST_KEY_PARAM) != null) {
            String key = request.getParameter(PRICELIST_KEY_PARAM);
            priceList = priceListService.findPriceListByKey(key);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find pricelist by param " + key + " resulted in " + priceList);
            }
        }
        
        // 3) Check session for pricelist
        if (priceList == null){
            priceList = (PriceList) session.getAttribute(PRICELIST_VAR);
        }

        // 4) Check pricelist based on currency
        if (priceList == null){
            BroadleafCurrency currency = (BroadleafCurrency) session.getAttribute(CURRENCY_VAR);
            if (currency != null){
                priceList = priceListService.findPriceListByCurrency(currency);
            }
        }

        // 5) Check default pricelist from DB
        if(priceList == null){
            priceList = priceListService.findDefaultPricelist();
        }

        session.setAttribute(PRICELIST_VAR, priceList);
        return priceList;
    }
}
