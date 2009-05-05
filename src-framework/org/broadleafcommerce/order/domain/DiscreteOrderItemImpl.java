package org.broadleafcommerce.order.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_DISCRETE_ORDER_ITEM")
public class DiscreteOrderItemImpl extends OrderItemImpl implements DiscreteOrderItem {

    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = SkuImpl.class)
    @JoinColumn(name = "SKU_ID", nullable = false)
    private Sku sku;

    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne(targetEntity = BundleOrderItemImpl.class)
    @JoinColumn(name = "BUNDLE_ORDER_ITEM_ID")
    private BundleOrderItem bundleOrderItem;

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BundleOrderItem getBundleOrderItem() {
        return bundleOrderItem;
    }

    public void setBundleOrderItem(BundleOrderItem bundleOrderItem) {
        this.bundleOrderItem = bundleOrderItem;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof DiscreteOrderItemImpl)) return false;

        DiscreteOrderItemImpl item = (DiscreteOrderItemImpl) other;

        if (sku != null && item.sku != null ? !sku.equals(item.sku) : sku != item.sku) return false;
        if (bundleOrderItem != null && item.bundleOrderItem != null ? !bundleOrderItem.getId().equals(item.bundleOrderItem.getId()) : bundleOrderItem != item.bundleOrderItem) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sku != null ? sku.getId().hashCode() : 0;
        result = 31 * result + (bundleOrderItem != null ? bundleOrderItem.getId().hashCode() : 0);

        return result;
    }

}
