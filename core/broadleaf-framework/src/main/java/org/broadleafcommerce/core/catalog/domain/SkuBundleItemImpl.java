package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.catalog.service.dynamic.DefaultDynamicSkuPricingInvocationHandler;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPrices;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.compass.annotations.SearchableId;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SKU_BUNDLE_ITEM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
public class SkuBundleItemImpl implements SkuBundleItem {

    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "SkuBundleItemId")
    @GenericGenerator(name = "SkuBundleItemId", strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator", parameters = {
          @Parameter(name = "table_name", value = "SEQUENCE_GENERATOR"),
          @Parameter(name = "segment_column_name", value = "ID_NAME"),
          @Parameter(name = "value_column_name", value = "ID_VAL"),
          @Parameter(name = "segment_value", value = "SkuBundleItemImpl"),
          @Parameter(name = "increment_size", value = "50"),
          @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.catalog.domain.SkuBundleItemImpl") })
    @Column(name = "SKU_BUNDLE_ITEM_ID")
    @SearchableId
    @AdminPresentation(friendlyName = "SkuBundleItemImpl_ID", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "QUANTITY")
    @AdminPresentation(friendlyName = "bundleItemQuantity")
    protected Integer quantity;

    @Column(name = "ITEM_SALE_PRICE")
    @AdminPresentation(friendlyName = "bundleItemSalePrice", tooltip="bundleItemSalePriceTooltip", fieldType = SupportedFieldType.MONEY)
    protected BigDecimal itemSalePrice;

    @ManyToOne(targetEntity = ProductBundleImpl.class, optional = false)
    @JoinColumn(name = "PRODUCT_BUNDLE_ID", referencedColumnName = "PRODUCT_ID")
    protected ProductBundle bundle;

    @OneToOne(targetEntity = SkuImpl.class, optional = false)
    @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID")
    private Sku sku;

    @Transient
    protected DynamicSkuPrices dynamicPrices = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    protected Money getDynamicSalePrice(Sku sku, BigDecimal salePrice) {
        if (
            SkuPricingConsiderationContext.getSkuPricingConsiderationContext() != null &&
       		SkuPricingConsiderationContext.getSkuPricingConsiderationContext().size() > 0 &&
       		SkuPricingConsiderationContext.getSkuPricingService() != null
       	) {
            if (sku == null) {
                throw new IllegalArgumentException("Unable to price bundle item.   DynamicPricing is enabled but no default sku is associated with the ProductBundle.");
            }

       		DefaultDynamicSkuPricingInvocationHandler handler = new DefaultDynamicSkuPricingInvocationHandler(sku, salePrice);
       		Sku proxy = (Sku) Proxy.newProxyInstance(sku.getClass().getClassLoader(), sku.getClass().getInterfaces(), handler);
       		dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getSkuPrices(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
       		handler.reset();
       		return dynamicPrices.getSalePrice();
       	}
        return salePrice == null ? null : new Money(salePrice);
    }

    public void setSalePrice(Money salePrice) {
        if (salePrice != null) {
            this.itemSalePrice = salePrice.getAmount();
        } else {
            this.itemSalePrice = null;
        }
    }


    public Money getSalePrice() {
        if (itemSalePrice == null) {
            return sku.getSalePrice();
        } else {
            return getDynamicSalePrice(sku, itemSalePrice);
        }
    }

    public Money getRetailPrice() {
         return sku.getRetailPrice();
     }

    public ProductBundle getBundle() {
        return bundle;
    }

    public void setBundle(ProductBundle bundle) {
        this.bundle = bundle;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }
}
