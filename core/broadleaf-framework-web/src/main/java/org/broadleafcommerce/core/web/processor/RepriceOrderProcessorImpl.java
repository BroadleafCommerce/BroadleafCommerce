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

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.RepriceOrderResponse;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.service.RepriceOrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Processor to reprice the current cart if the currency is different from the last added item.
 * Will notify of any removed items via a RepriceOrderResponse added to the Session.
 *
 * Author: jerryocanas
 * Date: 9/27/12
 */
@Component("blRepriceOrderProcessor")
public class RepriceOrderProcessorImpl implements RepriceOrderProcessor{

    protected String repriceOrderResponseKey = "bl_reprice_cart_response";

    @Resource(name = "blCurrencyRepriceOrderService")
    protected RepriceOrderService repriceOrderService;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {

        Order cart = CartState.getCart();

        // Checks if the currency has been modified; if so, the order is repriced
        RepriceOrderResponse repriceOrderResponse = new RepriceOrderResponse();
        if (repriceOrderService.needsRepricing()){
            try {
                repriceOrderResponse = repriceOrderService.repriceOrder(cart);
            } catch (RemoveFromCartException removeFromCartException) {
                removeFromCartException.printStackTrace();
            } catch (PricingException e) {
                e.printStackTrace();
            } catch (AddToCartException e) {
                e.printStackTrace();
            }
        }

        request.getSession().setAttribute(repriceOrderResponseKey, repriceOrderResponse);
    }
}
