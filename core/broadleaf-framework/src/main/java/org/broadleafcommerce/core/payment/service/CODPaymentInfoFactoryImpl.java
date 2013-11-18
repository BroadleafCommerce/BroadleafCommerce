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
import org.broadleafcommerce.core.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Collect On Delivery(COD) PaymentInfo factory
 * 
 * @author Joshua Skorton (jskorton)
 */
@Service("blCODPaymentInfoFactory")
public class CODPaymentInfoFactoryImpl implements PaymentInfoFactory {

    /**
     * Constructs a COD PaymentInfo object based on the passed in order.
     * Sets the basic information necessary to complete an order.
     *
     * @param order
     * @return PaymentInfo - the COD Payment object that gets persisted in Broadleaf.
     */
    @Override
    public PaymentInfo constructPaymentInfo(Order order) {
        PaymentInfoImpl paymentInfo = new PaymentInfoImpl();
        paymentInfo.setOrder(order);
        paymentInfo.setType(PaymentInfoType.COD);
        paymentInfo.setReferenceNumber(UUID.randomUUID().toString());
        paymentInfo.setAmount(order.getRemainingTotal());

        return paymentInfo;
    }

}
