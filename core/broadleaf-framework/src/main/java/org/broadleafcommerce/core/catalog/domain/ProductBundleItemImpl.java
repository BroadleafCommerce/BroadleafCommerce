package org.broadleafcommerce.core.catalog.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
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
    @AdminPresentation(friendlyName = "Quantity")
    protected Integer quantity;

    @Column(name = "SALE_PRICE")
    @AdminPresentation(friendlyName = "Sale Price", fieldType = SupportedFieldType.MONEY)
    protected BigDecimal salePrice;
    
    //TODO: add flag for allowing the price to be modified by the ProductOptions

    @ManyToOne(targetEntity = ProductBundleImpl.class, optional = false)
    @JoinColumn(name = "BUNDLE_PRODUCT_ID")
    @Index(name = "BUNDLE_PRODUCT_INDEX", columnNames = { "BUNDLE_PRODUCT_ID" })
    protected ProductBundle bundle;

    @ManyToOne(targetEntity = ProductImpl.class, optional = false)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name = "BUNDLE_ITEM_PRODUCT_INDEX", columnNames = { "PRODUCT_ID" })
    private Product product = new ProductImpl();

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }
    
    public Money getSalePrice() {
        if (salePrice == null) {
            return getProduct().getDefaultSku().getSalePrice();
        } else {
            return new Money(salePrice);
        }
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
