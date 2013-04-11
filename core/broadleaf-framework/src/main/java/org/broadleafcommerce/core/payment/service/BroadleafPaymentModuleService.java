/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.core.payment.service.workflow.PaymentSeed;

/**
 * Service implemented by Broadleaf Payment modules used to provide general functionality.
 *
 * @author Jerry Ocanas (jocanas)
 */
public interface BroadleafPaymentModuleService {

    /**
     * Validates the response received from the payment module.
     *
     * When implemented it should throw an error with the message received from the payment module. This message
     * will be bubbled up and displayed in the admin to the user.
     *
     * @param paymentSeed
     * @return boolean
     */
    public void validateResponse(PaymentSeed paymentSeed) throws Exception;

    /**
     * Used by the payment module to implement setting the transaction id into the database where approriate
     * the payment module.
     *
     * @param transactionID
     */
    public void manualPayment(PaymentSeed paymentSeed, String transactionID);
}
