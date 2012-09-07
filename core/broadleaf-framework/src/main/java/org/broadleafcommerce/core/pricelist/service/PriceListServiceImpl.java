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

package org.broadleafcommerce.core.pricelist.service;

import javax.annotation.Resource;

import org.broadleafcommerce.core.pricing.domain.NullPriceList;
import org.broadleafcommerce.core.pricing.domain.PriceList;
import org.springframework.stereotype.Service;

@Service("blPriceListService")
public class PriceListServiceImpl implements PriceListService {
    private final NullPriceList NULL_PRICE_LIST = new NullPriceList();

    @Resource(name="blPriceListDao")
    protected PriceListDao priceListDao;

    @Override
    public PriceList getPriceList(String key) {
        PriceList priceList = priceListDao.findbyKey(key);
        if (priceList != null) {
                return priceList;
        } else {
                return NULL_PRICE_LIST;
        }
    }

}
