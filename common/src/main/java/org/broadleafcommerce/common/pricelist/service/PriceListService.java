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

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.pricelist.domain.PriceList;

import java.util.List;

public interface PriceListService {

    /**
     * Returns a pricelist that matches the passed in key
     *
     * @return The pricelist for the passed in key
     */
    public PriceList findPriceListByKey(String key);

    /**
     * Returns a pricelist that matches the passed in currency
     *
     * @param currency
     * @return pricelist
     */
    public PriceList findPriceListByCurrency(BroadleafCurrency currency);

    /**
     * Returns the default pricelist
     *
     * @return the default pricelist
     */
    public PriceList findDefaultPricelist();

    /**
     * @return a list of all currently configured price lists
     */
    public List<PriceList> findAllPriceLists();

}
