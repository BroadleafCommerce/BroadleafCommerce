/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;

/**
 * <p>This API provides the ability to get the status of a Transaction after it has been submitted to the Gateway.
 * Gateways have different ways to provide this information.
 * For example, Cybersource can provide a nightly feed or FTP file that contain details of
 * what was SETTLED, CHARGEBACK, etc... to be reconciled in your system.
 * Braintree and Paypal have API hooks to either do a date based query or an individual
 * inquiry on a particular transaction.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayReportingService {

    public PaymentResponseDTO findDetailsByTransaction(PaymentRequestDTO paymentRequestDTO) throws PaymentException;

}
