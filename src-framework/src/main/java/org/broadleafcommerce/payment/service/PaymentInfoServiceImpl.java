/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.payment.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.payment.dao.PaymentInfoDao;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentLog;
import org.broadleafcommerce.payment.domain.PaymentResponseItem;
import org.springframework.stereotype.Service;

@Service("blPaymentInfoService")
public class PaymentInfoServiceImpl implements PaymentInfoService {

    @Resource(name = "blPaymentInfoDao")
    protected PaymentInfoDao paymentInfoDao;

    public PaymentInfo save(PaymentInfo paymentInfo) {
        return paymentInfoDao.save(paymentInfo);
    }

    public PaymentResponseItem save(PaymentResponseItem paymentResponseItem) {
        return paymentInfoDao.save(paymentResponseItem);
    }

    public PaymentLog save(PaymentLog log) {
        return paymentInfoDao.save(log);
    }

    public PaymentInfo readPaymentInfoById(Long paymentId) {
        return paymentInfoDao.readPaymentInfoById(paymentId);
    }

    public List<PaymentInfo> readPaymentInfosForOrder(Order order) {
        return paymentInfoDao.readPaymentInfosForOrder(order);
    }

    public PaymentInfo create() {
        return paymentInfoDao.create();
    }

    public void delete(PaymentInfo paymentInfo) {
        paymentInfoDao.delete(paymentInfo);
    }

    public PaymentLog createLog() {
        return paymentInfoDao.createLog();
    }

    public PaymentResponseItem createResponseItem() {
        return paymentInfoDao.createResponseItem();
    }

}
