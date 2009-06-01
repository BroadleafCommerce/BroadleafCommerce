package org.broadleafcommerce.order.service;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;

public interface PaymentInfoService {

    public PaymentInfo save(PaymentInfo paymentInfo);

    public PaymentInfo readPaymentInfoById(Long paymentId);

    public List<PaymentInfo> readPaymentInfosForOrder(Order order);

    public PaymentInfo create();

    public void delete(PaymentInfo paymentInfo);

}
