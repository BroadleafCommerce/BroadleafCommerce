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
import org.broadleafcommerce.core.order.service.type.OrderStatus;

public interface CheckoutService {

    /**
     * <p>Checks out an order by executing the blCheckoutWorkflow. The <b>order</b> is saved both before and after the workflow
     * is executed so that activities can modify the various entities on and related to the <b>order</b>.</p>
     * 
     * <p>This method is also thread-safe; 2 requests cannot attempt to check out the same <b>order</b></p>
     * 
     * @param order the order to be checked out
     * @return
     * @throws CheckoutException if there are any exceptions while executing any of the activities in the workflow (assuming
     * that the workflow does not already have a preconfigured error handler) or if the given <b>order</b> has already been
     * checked out (in Broadleaf terms this means the <b>order</b> has already been changed to {@link OrderStatus#SUBMITTED})
     */
    public CheckoutResponse performCheckout(Order order) throws CheckoutException;
    
}
