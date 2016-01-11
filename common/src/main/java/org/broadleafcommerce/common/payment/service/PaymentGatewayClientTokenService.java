/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

/**
 * <p>The intention of this API is to generate the necessary parameters and fields
 * needed by the payment gateway's JavaScript library in order to create a tokenization request.
 * These parameters will be placed on the ResponseDTO for consumption by a client application or template.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayClientTokenService {

    public PaymentResponseDTO generateClientToken(PaymentRequestDTO requestDTO);

}
