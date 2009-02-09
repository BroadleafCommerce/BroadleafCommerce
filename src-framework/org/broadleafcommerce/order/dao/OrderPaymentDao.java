package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.OrderPayment;

public interface OrderPaymentDao {

	public OrderPayment readOrderPaymentById(Long paymentId);
	
	public OrderPayment maintainOrderPayment(OrderPayment orderPayment);
	
	public List<OrderPayment> readOrderPaymentsForOrder(BroadleafOrder order);
	
	
}
