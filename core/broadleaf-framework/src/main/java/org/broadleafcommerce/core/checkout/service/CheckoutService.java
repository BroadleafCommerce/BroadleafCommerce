/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.checkout.service;

import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;

import java.util.Map;

public interface CheckoutService {

    public CheckoutResponse performCheckout(Order order) throws CheckoutException;

    /**
     * This method should not be used, and only really made sense if you were storing credit card numbers in your own system
     * which is not something that Broadleaf recommends. The normal case is 
     * @deprecated Use {@link #performCheckout(Order)} instead
     */
    @Deprecated
    public CheckoutResponse performCheckout(Order order, Map<OrderPayment, Referenced> payments) throws CheckoutException;

}
