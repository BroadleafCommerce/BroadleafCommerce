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
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;

import java.util.Map;

/**
 * @author Jerry Ocanas (jocanas)
 */
public interface BroadleafPaymentInfoTypeService {

    /**
     * Constructs a default entry in the payments map for each payment found on the order that matches
     * a PaymentInfoTypes.
     *
     * @param order
     * @return Map<PaymentInfo, Referenced>
     */
    public Map<PaymentInfo, Referenced> getPaymentsMap(Order order);

}
