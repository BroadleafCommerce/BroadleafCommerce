package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;

public interface ProductBundleItem extends Serializable {

    public int getQuantity();

    public void setQuantity(int quantity);

    public boolean isOverridePrice();

    public void setOverridePrice(boolean overridePrice);

    public boolean isOverrideUnitPrice();

    public void setOverrideUnitPrice(boolean overrideUnitPrice);

    public ProductBundle getBundle();

    public void setBundle(ProductBundle bundle);

    public Product getProduct();

    public void setProduct(Product product);

}
