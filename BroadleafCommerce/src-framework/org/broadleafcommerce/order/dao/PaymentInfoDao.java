package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;

public interface PaymentInfoDao {

	public PaymentInfo readPaymentInfoById(Long paymentId);
	
	public PaymentInfo save(PaymentInfo paymentInfo);
	
	public List<PaymentInfo> readPaymentInfosForOrder(Order order);
	
	public PaymentInfo create();
}
