package org.broadleafcommerce.order.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.catalog.domain.Sku;

public interface OrderItem {

    public Long getId();

    public void setId(Long id);

    public Sku getSku();

    public void setSku(Sku sku);

    public Order getOrder();

    public void setOrder(Order order);

    public BigDecimal getFinalPrice();

    public void setFinalPrice(BigDecimal finalPrice);

    public int getQuantity();

    public void setQuantity(int quantity);
}
