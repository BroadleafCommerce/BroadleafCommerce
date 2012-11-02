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

package org.broadleafcommerce.common.pricelist.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.pricelist.dao.PriceListDao;
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

@Service("blPriceListService")
public class PriceListServiceImpl implements PriceListService {
    private static final Log LOG = LogFactory.getLog(PriceListServiceImpl.class);

    @Resource(name="blPriceListDao")
    protected PriceListDao priceListDao;

    /**
     * Returns a pricelist that matches the passed in key
     *
     * @return The pricelist for the passed in key
     */
    @Override
    public PriceList findPriceListByKey(String key) {
        PriceList priceList = priceListDao.findPriceListByKey(key);
        if (priceList != null) {
                return priceList;
        } else {
                return null;
        }
    }

    /**
     * Returns a pricelist that matches the passed in currency
     *
     * @param currency
     * @return pricelist
     */
    @Override
    public PriceList findPriceListByCurrency(BroadleafCurrency currency){
        PriceList priceList = priceListDao.findPriceListByCurrency(currency);
        if (priceList != null) {
            return priceList;
        } else {
            return null;
        }
    }

    /**
     * Returns the default pricelist
     *
     * @return the default pricelist
     */
    @Override
    public PriceList findDefaultPricelist() {
        return priceListDao.findDefaultPricelist();
    }
    
    @Override
    public List<PriceList> findAllPriceLists() {
        return priceListDao.readAllPriceLists();
    }

}
