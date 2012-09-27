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

package org.broadleafcommerce.core.web.service;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.call.RepriceOrderResponse;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

/**
 * Provides methods to facilitate order repricing.
 *
 * Author: jerryocanas
 * Date: 9/26/12
 */
public interface RepriceOrderService {

    /**
     * Sets the currency that was set as active on last pass through.
     *
     * @param savedCurrency
     */
    public void setSavedCurrency(BroadleafCurrency savedCurrency);

    /**
     * Gets the currency that was set as active on last pass through.
     *
     * @return
     */
    public BroadleafCurrency getSavedCurrency();

    /**
     *  Compares the currency set in the BroadleafRequestContext with the savedCurrency.
     *  If different, returns TRUE
     *
     * @return
     */
    public boolean needsRepricing();

    /**
     * Reprices the order by removing all items and recreating the cart calling for a reprice on the new cart.
     *
     * @return
     */
    public RepriceOrderResponse repriceOrder(Order order)
            throws PricingException, RemoveFromCartException, AddToCartException;
}
