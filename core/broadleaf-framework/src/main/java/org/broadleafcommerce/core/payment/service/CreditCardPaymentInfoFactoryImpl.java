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

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("blCreditCardPaymentInfoFactory")
public class CreditCardPaymentInfoFactoryImpl implements PaymentInfoFactory {

    /**
     * Constructs a default Credit Card PaymentInfo object based on the passed in order.
     * Sets the basic information necessary to complete an order.
     *
     * @param order
     * @return PaymentInfo - the Credit Card Payment object that gets persisted in Broadleaf.
     */
    @Override
    public PaymentInfo constructPaymentInfo(Order order) {
        PaymentInfoImpl paymentInfo = new PaymentInfoImpl();
        paymentInfo.setOrder(order);
        paymentInfo.setType(PaymentInfoType.CREDIT_CARD);
        paymentInfo.setReferenceNumber(UUID.randomUUID().toString());
        paymentInfo.setAmount(order.getRemainingTotal());

        return paymentInfo;
    }

}
