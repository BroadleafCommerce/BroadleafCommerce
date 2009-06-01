package org.broadleafcommerce.order.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.PaymentInfoDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.springframework.stereotype.Service;

@Service("paymentInfoService")
public class PaymentInfoServiceImpl implements PaymentInfoService {

    @Resource
    private PaymentInfoDao paymentInfoDao;

    @Override
    public PaymentInfo save(PaymentInfo paymentInfo) {
        return paymentInfoDao.save(paymentInfo);
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
    public void delete(PaymentInfo paymentInfo) {
        paymentInfoDao.delete(paymentInfo);
    }

}
