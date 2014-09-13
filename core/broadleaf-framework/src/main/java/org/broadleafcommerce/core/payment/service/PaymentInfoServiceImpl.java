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

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.dao.PaymentInfoDao;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

@Service("blPaymentInfoService")
public class PaymentInfoServiceImpl implements PaymentInfoService {

    @Resource(name = "blPaymentInfoDao")
    protected PaymentInfoDao paymentInfoDao;

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public PaymentInfo save(PaymentInfo paymentInfo) {
        return paymentInfoDao.save(paymentInfo);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public PaymentResponseItem save(PaymentResponseItem paymentResponseItem) {
        return paymentInfoDao.save(paymentResponseItem);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public PaymentLog save(PaymentLog log) {
        return paymentInfoDao.save(log);
    }

    @Override
    public PaymentInfo readPaymentInfoById(Long paymentId) {
        return paymentInfoDao.readPaymentInfoById(paymentId);
    }

    @Override
    public List<PaymentInfo> readPaymentInfosForOrder(Order order) {
        return paymentInfoDao.readPaymentInfosForOrder(order);
    }

    @Override
    public PaymentInfo create() {
        return paymentInfoDao.create();
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void delete(PaymentInfo paymentInfo) {
        paymentInfoDao.delete(paymentInfo);
    }

    @Override
    public PaymentLog createLog() {
        return paymentInfoDao.createLog();
    }

    @Override
    public PaymentResponseItem createResponseItem() {
        PaymentResponseItem returnItem = paymentInfoDao.createResponseItem();
        returnItem.setTransactionTimestamp(SystemTime.asDate());
        return returnItem;
    }

}
