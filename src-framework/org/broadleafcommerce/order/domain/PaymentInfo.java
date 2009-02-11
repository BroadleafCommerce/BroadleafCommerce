package org.broadleafcommerce.order.domain;

public interface PaymentInfo {

	public Object getPayment();
	
	public void setPayment(Object payment);
	
	public double getAmount();
	
	public void setAmount(double amount);
}
