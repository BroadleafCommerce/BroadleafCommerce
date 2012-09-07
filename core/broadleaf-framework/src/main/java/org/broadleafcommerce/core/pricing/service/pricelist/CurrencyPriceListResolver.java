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

package org.broadleafcommerce.core.pricing.service.pricelist;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.core.pricing.domain.PriceList;
import org.broadleafcommerce.core.pricing.service.PriceListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for returning the price list to use for the current request based on Currency.
 *
 * Author: jerryocanas
 * Date: 9/7/12
 */
@Component("blPriceListResovler")
public class CurrencyPriceListResolver implements BroadleafPricelistResolver {

    /**
     * Parameter/Attribute name for the current language
     */
    public static String CURRENCY_VAR = "blCurrency";

    @Resource(name = "blPriceListService")
    PriceListService priceListService;

    @Override
    public PriceList resolvePricelist(HttpServletRequest request) {
        PriceList priceList = null;

        BroadleafCurrency currency = (BroadleafCurrency) request.getSession().getAttribute(CURRENCY_VAR);
        if (currency != null){
            String key = currency.getCurrencyCode();
            if (key != null){
                priceList = priceListService.getPriceList(key);
            }
        }

        return priceList;
    }
}
