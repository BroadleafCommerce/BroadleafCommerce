package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_BUNDLE_ITEM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "ProductBundleItemImpl_baseProductBundleItem")
public class ProductBundleItemImpl implements ProductBundleItem {

    private static final long serialVersionUID = 1L;

    @Column(name = "QUANTITY")
    protected int quantity;

    @Column(name = "OVERRIDE_PRICE")
    protected boolean overridePrice;

    @Column(name = "OVERRIDE_UNIT_PRICE")
    protected boolean overrideUnitPrice;

    @ManyToOne(targetEntity = ProductBundleImpl.class, optional = false)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name = "PRODFEATURED_PRODUCT_INDEX", columnNames = { "PRODUCT_ID" })
    protected ProductBundle bundle;

    @ManyToOne(targetEntity = ProductImpl.class, optional = false)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name = "BUNDLE_ITEM_PRODUCT_INDEX", columnNames = { "PRODUCT_ID" })
    private Product product = new ProductImpl();

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isOverridePrice() {
        return overridePrice;
    }

    public void setOverridePrice(boolean overridePrice) {
        this.overridePrice = overridePrice;
    }

    public boolean isOverrideUnitPrice() {
        return overrideUnitPrice;
    }

    public void setOverrideUnitPrice(boolean overrideUnitPrice) {
        this.overrideUnitPrice = overrideUnitPrice;
    }

    public ProductBundle getBundle() {
        return bundle;
    }

    public void setBundle(ProductBundle bundle) {
        this.bundle = bundle;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
