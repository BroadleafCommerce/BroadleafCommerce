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
 * @author Elbert Bautista (elbertbautista)
 *
 * The purpose of this class, is to provide an API that will create
 * any gateway specific parameters needed for a Transparent Redirect/Silent Order Post etc...
 *
 * Some payment gateways provide this ability and will generate either a Secure Token
 * or some hashed parameters that will be placed as hidden fields on your Credit Card form.
 * These parameters (along with the Credit Card information) will be placed on the ResponseDTO
 * and your HTML should include these fields to be POSTed directly to the
 * implementing gateway for processing.
 *
 */
public interface PaymentGatewayTransparentRedirectService {

    public PaymentResponseDTO createAuthorizeForm(PaymentRequestDTO requestDTO) throws PaymentException;

    public PaymentResponseDTO createAuthorizeAndCaptureForm(PaymentRequestDTO requestDTO) throws PaymentException;

}
