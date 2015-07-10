/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;


/**
 * Simple interface for returning a {@link PaymentRequestDTO} based on the current order in the system (like something on
 * threadlocal).
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface CurrentOrderPaymentRequestService {

    /**
     * Returns a {@link PaymentRequestDTO} based on all the information from the current order in the system, like one
     * on threadlocal
     */
    public PaymentRequestDTO getPaymentRequestFromCurrentOrder();

}
