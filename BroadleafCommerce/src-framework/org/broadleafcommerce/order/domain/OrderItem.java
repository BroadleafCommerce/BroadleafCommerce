package org.broadleafcommerce.order.domain;

import org.broadleafcommerce.catalog.domain.Sku;

public interface OrderItem {
	public Long getId();

	public void setId(Long id);

	public Sku getSku();

	public void setSku(Sku sku);

	public Order getOrder();

	public void setOrder(Order order);

	public double getFinalPrice();

	public void setFinalPrice(double finalPrice);

	public int getQuantity();

	public void setQuantity(int quantity);

}
