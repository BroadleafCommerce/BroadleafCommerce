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

package org.broadleafcommerce.core.payment.dao;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;

import java.util.List;

public interface PaymentInfoDao {

    public PaymentInfo readPaymentInfoById(Long paymentId);

    public PaymentInfo save(PaymentInfo paymentInfo);

    public PaymentResponseItem save(PaymentResponseItem paymentResponseItem);

    public PaymentLog save(PaymentLog log);

    public List<PaymentInfo> readPaymentInfosForOrder(Order order);

    public PaymentInfo create();

    public void delete(PaymentInfo paymentInfo);

    public PaymentResponseItem createResponseItem();

    public PaymentLog createLog();

}
