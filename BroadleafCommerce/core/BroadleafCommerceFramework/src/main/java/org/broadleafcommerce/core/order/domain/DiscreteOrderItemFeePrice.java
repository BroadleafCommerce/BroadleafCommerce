package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.money.Money;

public interface DiscreteOrderItemFeePrice {

	public abstract Long getId();

	public abstract void setId(Long id);

	public DiscreteOrderItem getDiscreteOrderItem();

	public void setDiscreteOrderItem(DiscreteOrderItem discreteOrderItem);

	public abstract Money getAmount();

	public abstract void setAmount(Money amount);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getReportingCode();

	public abstract void setReportingCode(String reportingCode);

}