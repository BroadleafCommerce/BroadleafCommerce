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
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

/**
 * Default Resolver implementation. Extensions and modules can override this to provide
 * more exotic scenarios on which PaymentGateway should be used.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blPaymentGatewayResolver")
public class PaymentGatewayResolverImpl implements PaymentGatewayResolver {

    @Override
    public boolean isHandlerCompatible(PaymentGatewayType handlerType) {
        return true;
    }

    @Override
    public PaymentGatewayType resolvePaymentGateway(WebRequest request) {
        return null;
    }

}
