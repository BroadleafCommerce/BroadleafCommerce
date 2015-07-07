/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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

import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.springframework.web.context.request.WebRequest;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayResolver {

    /**
     * Used by Transparent Redirect Solutions that utilize Thymeleaf Processors and Expressions.
     * This method should determine whether or not an extension handler should run for a particular gateway.
     * @param handlerType
     * @return
     */
    public boolean isHandlerCompatible(PaymentGatewayType handlerType);

    /**
     * Resolves a {@link org.broadleafcommerce.common.payment.PaymentGatewayType}
     * based on a {@link org.springframework.web.context.request.WebRequest}
     * @param request
     * @return
     */
    public PaymentGatewayType resolvePaymentGateway(WebRequest request);

}
