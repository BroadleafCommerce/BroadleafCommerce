package org.broadleafcommerce.order.domain;

public class BroadleafPaymentInfo implements PaymentInfo {

	private double amount;
	private Object payment;
	
	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public Object getPayment() {
		return payment;
	}

	@Override
	public void setPayment(Object payment) {
		this.payment = payment;
	}
	
	
}
