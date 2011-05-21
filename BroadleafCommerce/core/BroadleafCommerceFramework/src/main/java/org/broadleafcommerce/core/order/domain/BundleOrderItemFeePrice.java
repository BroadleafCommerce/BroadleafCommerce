package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.money.Money;

public interface BundleOrderItemFeePrice {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract BundleOrderItem getBundleOrderItem();

	public abstract void setBundleOrderItem(BundleOrderItem bundleOrderItem);

	public abstract Money getAmount();

	public abstract void setAmount(Money amount);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Boolean isTaxable();

	public abstract void setTaxable(Boolean isTaxable);

	public abstract String getReportingCode();

	public abstract void setReportingCode(String reportingCode);

}