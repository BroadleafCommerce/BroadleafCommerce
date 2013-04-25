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
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.module.PaymentResponse;
import org.broadleafcommerce.core.payment.service.workflow.CompositePaymentResponse;

import java.util.Map;

public interface CompositePaymentService {

    public CompositePaymentResponse executePayment(Order order, Map<PaymentInfo, Referenced> payments, PaymentResponse response) throws PaymentException;

    public CompositePaymentResponse executePayment(Order order, Map<PaymentInfo, Referenced> payments) throws PaymentException;

    public CompositePaymentResponse executePayment(Order order) throws PaymentException;

    public CompositePaymentResponse executePaymentForGateway(Order order, PaymentInfoFactory paymentInfoFactory) throws PaymentException;

}
