package org.broadleafcommerce.order.domain;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;

public interface DiscreteOrderItem extends OrderItem {

    public Sku getSku();

    public void setSku(Sku sku);

    public Product getProduct();

    public void setProduct(Product product);

    public BundleOrderItem getBundleOrderItem();

    public void setBundleOrderItem(BundleOrderItem bundleOrderItem);

}
