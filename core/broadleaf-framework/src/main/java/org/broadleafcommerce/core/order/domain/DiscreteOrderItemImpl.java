/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.DefaultPostLoaderDao;
import org.broadleafcommerce.common.persistence.PostLoaderDao;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.HibernateUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItemImpl;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPrices;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_DISCRETE_ORDER_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationClass(friendlyName = "DiscreteOrderItemImpl_discreteOrderItem")
public class DiscreteOrderItemImpl extends OrderItemImpl implements DiscreteOrderItem {

    private static final long serialVersionUID = 1L;
    
    @Column(name="BASE_RETAIL_PRICE", precision=19, scale=5)
    @AdminPresentation(excluded = true, friendlyName = "DiscreteOrderItemImpl_Base_Retail_Price", order=2,
            group = "DiscreteOrderItemImpl_Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal baseRetailPrice;
    
    @Column(name="BASE_SALE_PRICE", precision=19, scale=5)
    @AdminPresentation(excluded = true, friendlyName = "DiscreteOrderItemImpl_Base_Sale_Price", order=2,
            group = "DiscreteOrderItemImpl_Pricing", fieldType= SupportedFieldType.MONEY)
    protected BigDecimal baseSalePrice;
    
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = SkuImpl.class, optional=false)
    @JoinColumn(name = "SKU_ID", nullable = false)
    @Index(name="DISCRETE_SKU_INDEX", columnNames={"SKU_ID"})
    @AdminPresentation(friendlyName = "DiscreteOrderItemImpl_Sku", order=Presentation.FieldOrder.SKU,
            group = OrderItemImpl.Presentation.Group.Name.Catalog, groupOrder = OrderItemImpl.Presentation.Group.Order.Catalog)
    @AdminPresentationToOneLookup()
    protected Sku sku;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name="DISCRETE_PRODUCT_INDEX", columnNames={"PRODUCT_ID"})
    @NotFound(action = NotFoundAction.IGNORE)
    @AdminPresentation(friendlyName = "DiscreteOrderItemImpl_Product", order=Presentation.FieldOrder.PRODUCT,
            group = OrderItemImpl.Presentation.Group.Name.Catalog, groupOrder = OrderItemImpl.Presentation.Group.Order.Catalog)
    @AdminPresentationToOneLookup()
    protected Product product;

    @ManyToOne(targetEntity = BundleOrderItemImpl.class)
    @JoinColumn(name = "BUNDLE_ORDER_ITEM_ID")
    @AdminPresentation(excluded = true)
    protected BundleOrderItem bundleOrderItem;

    @ManyToOne(targetEntity = SkuBundleItemImpl.class)
    @JoinColumn(name = "SKU_BUNDLE_ITEM_ID")
    @AdminPresentation(excluded = true)
    protected SkuBundleItem skuBundleItem;

    @ElementCollection
    @MapKeyColumn(name="NAME")
    @Column(name="VALUE")
    @CollectionTable(name="BLC_ORDER_ITEM_ADD_ATTR", joinColumns=@JoinColumn(name="ORDER_ITEM_ID"))
    @BatchSize(size = 50)
    @Deprecated
    protected Map<String, String> additionalAttributes = new HashMap<String, String>();
    
    @OneToMany(mappedBy = "discreteOrderItem", targetEntity = DiscreteOrderItemFeePriceImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<DiscreteOrderItemFeePrice> discreteOrderItemFeePrices = new ArrayList<DiscreteOrderItemFeePrice>();

    @Transient
    protected Sku deproxiedSku;

    @Transient
    protected Product deproxiedProduct;

    @Override
    public Sku getSku() {
        if (deproxiedSku == null) {
            PostLoaderDao postLoaderDao = DefaultPostLoaderDao.getPostLoaderDao();

            if (postLoaderDao != null && sku.getId() != null) {
                Long id = sku.getId();
                deproxiedSku = postLoaderDao.find(SkuImpl.class, id);
            } else if (sku instanceof HibernateProxy) {
                deproxiedSku = HibernateUtils.deproxy(sku);
            } else {
                deproxiedSku = sku;
            }
        }

        return deproxiedSku;
    }

    @Override
    public void setSku(Sku sku) {
        this.sku = sku;
        if (sku.hasRetailPrice()) {
            this.baseRetailPrice = sku.getRetailPrice().getAmount();
        }
        if (sku.hasSalePrice()) {
            this.baseSalePrice = sku.getSalePrice().getAmount();
        }
        this.itemTaxable = sku.isTaxable();
        setName(sku.getName());
    }

    @Override
    public Boolean isTaxable() {
        return (sku == null || sku.isTaxable() == null || sku.isTaxable());
    }

    @Override
    public Product getProduct() {
        if (deproxiedProduct == null) {
            PostLoaderDao postLoaderDao = DefaultPostLoaderDao.getPostLoaderDao();

            if (product != null && postLoaderDao != null && product.getId() != null) {
                Long id = product.getId();
                deproxiedProduct = postLoaderDao.find(ProductImpl.class, id);
            } else if (product instanceof HibernateProxy) {
                deproxiedProduct = HibernateUtils.deproxy(product);
            } else {
                deproxiedProduct = product;
            }
        }

        return deproxiedProduct;
    }

    @Override
    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public BundleOrderItem getBundleOrderItem() {
        return bundleOrderItem;
    }

    @Override
    public void setBundleOrderItem(BundleOrderItem bundleOrderItem) {
        if (this.order != null && bundleOrderItem != null) {
            throw new IllegalStateException("Cannot set a BundleOrderItem on a DiscreteOrderItem that is already associated with an Order");
        }
        this.bundleOrderItem = bundleOrderItem;
    }

    @Override
    public void setOrder(Order order) {
        if (order != null && bundleOrderItem != null) {
            throw new IllegalStateException("Cannot set an Order on a DiscreteOrderItem that is already associated with a BundleOrderItem");
        }
        this.order = order;
    }

    /**
     * If this item is part of a bundle that was created via a ProductBundle, then this
     * method returns a reference to the corresponding SkuBundleItem.
     * <p/>
     * For manually created
     * <p/>
     * For all others, this method returns null.
     *
     * @return
     */
    @Override
    public SkuBundleItem getSkuBundleItem() {
        return skuBundleItem;
    }

    /**
     * Sets the associated SkuBundleItem.
     *
     * @param SkuBundleItem
     */
    @Override
    public void setSkuBundleItem(SkuBundleItem SkuBundleItem) {
        this.skuBundleItem =SkuBundleItem;
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (name == null) {
            return sku.getName();
        }
        return name;
    }
    
    @Override
    public Order getOrder() {
        if (order == null) {
            if (getBundleOrderItem() != null) {
                return getBundleOrderItem().getOrder();
            }
        }
        return order;
    }

    protected boolean updateSalePrice() {
        if (isSalePriceOverride()) {
            return false;
        }

        Money skuSalePrice = null;

        DynamicSkuPrices priceData = getSku().getPriceData();
        if (priceData != null) {
            skuSalePrice = priceData.getPriceForQuantity(quantity);
        }
        if (skuSalePrice == null) {
            skuSalePrice = getSku().getSalePrice();
        }

        // Override retail/sale prices from skuBundle.
        if (skuBundleItem != null) {
            if (skuBundleItem.getSalePrice() != null) {
                skuSalePrice = skuBundleItem.getSalePrice();
            }
        }

        boolean updated = false;
        //use the sku prices - the retail and sale prices could be null
        if (skuSalePrice != null && !skuSalePrice.getAmount().equals(salePrice)) {
            baseSalePrice = skuSalePrice.getAmount();
            salePrice = skuSalePrice.getAmount();
            updated = true;
        }
        
        // If there is no more sale price (because it got removed) then detect that case as well
        if (skuSalePrice == null && salePrice != null) {
            baseSalePrice = null;
            salePrice = null;
            updated = true;
        }

        // Adjust prices by adding in fees if they are attached.
        if (getDiscreteOrderItemFeePrices() != null) {
            for (DiscreteOrderItemFeePrice fee : getDiscreteOrderItemFeePrices()) {
                Money returnPrice = convertToMoney(salePrice);
                if (returnPrice != null) {
                    salePrice = returnPrice.add(fee.getAmount()).getAmount();
                }
            }
        }
        return updated;
    }

    protected boolean updateRetailPrice() {
        if (isRetailPriceOverride()) {
            return false;
        }
        Money skuRetailPrice = getSku().getRetailPrice();

        // Override retail/sale prices from skuBundle.
        if (skuBundleItem != null) {
            if (skuBundleItem.getRetailPrice() != null) {
                skuRetailPrice = skuBundleItem.getRetailPrice();
            }
        }

        boolean updated = false;
        //use the sku prices - the retail and sale prices could be null
        if (skuRetailPrice != null && !skuRetailPrice.getAmount().equals(retailPrice)) {
            baseRetailPrice = skuRetailPrice.getAmount();
            retailPrice = skuRetailPrice.getAmount();
            updated = true;
        }

        // Adjust prices by adding in fees if they are attached.
        if (getDiscreteOrderItemFeePrices() != null) {
            for (DiscreteOrderItemFeePrice fee : getDiscreteOrderItemFeePrices()) {
                Money returnPrice = convertToMoney(retailPrice);
                retailPrice = returnPrice.add(fee.getAmount()).getAmount();
            }
        }
        return updated;
    }

    @Override
    public boolean updateSaleAndRetailPrices() {
        boolean salePriceUpdated = updateSalePrice();
        boolean retailPriceUpdated = updateRetailPrice();
        if (!isRetailPriceOverride() && !isSalePriceOverride()) {
            if (salePrice != null && salePrice.compareTo(retailPrice) <= 0) {
                price = salePrice;
            } else {
                price = retailPrice;
            }
        }
        return salePriceUpdated || retailPriceUpdated;
    }

    @Override
    public Map<String, String> getAdditionalAttributes() {
        return additionalAttributes;
    }

    @Override
    public void setAdditionalAttributes(Map<String, String> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    @Override
    public Money getBaseRetailPrice() {
        return convertToMoney(baseRetailPrice);
    }

    @Override
    public void setBaseRetailPrice(Money baseRetailPrice) {
        this.baseRetailPrice = baseRetailPrice==null?null:baseRetailPrice.getAmount();
    }

    @Override
    public Money getBaseSalePrice() {
        return convertToMoney(baseSalePrice);
    }

    @Override
    public void setBaseSalePrice(Money baseSalePrice) {
        this.baseSalePrice = baseSalePrice==null?null:baseSalePrice.getAmount();
    }

    @Override
    public List<DiscreteOrderItemFeePrice> getDiscreteOrderItemFeePrices() {
        return discreteOrderItemFeePrices;
    }

    @Override
    public void setDiscreteOrderItemFeePrices(List<DiscreteOrderItemFeePrice> discreteOrderItemFeePrices) {
        this.discreteOrderItemFeePrices = discreteOrderItemFeePrices;
    }

    @Override
    protected Money convertToMoney(BigDecimal amount) {
        return amount == null ? null : BroadleafCurrencyUtils.getMoney(amount, getOrder().getCurrency());
    }
    
    @Override
    public OrderItem clone() {
        DiscreteOrderItem orderItem = (DiscreteOrderItem) super.clone();
        if (discreteOrderItemFeePrices != null) {
            for (DiscreteOrderItemFeePrice feePrice : discreteOrderItemFeePrices) {
                DiscreteOrderItemFeePrice cloneFeePrice = feePrice.clone();
                cloneFeePrice.setDiscreteOrderItem(orderItem);
                orderItem.getDiscreteOrderItemFeePrices().add(cloneFeePrice);
            }
        }
        if (additionalAttributes != null) {
            orderItem.getAdditionalAttributes().putAll(additionalAttributes);
        }
        orderItem.setBaseRetailPrice(convertToMoney(baseRetailPrice));
        orderItem.setBaseSalePrice(convertToMoney(baseSalePrice));
        orderItem.setBundleOrderItem(bundleOrderItem);
        orderItem.setProduct(product);
        orderItem.setSku(sku);

        if (orderItem.getOrder() == null) {
            throw new IllegalStateException("Either an Order or a BundleOrderItem must be set on the DiscreteOrderItem");
        }
        
        return orderItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        DiscreteOrderItemImpl other = (DiscreteOrderItemImpl) obj;
        
        if (!super.equals(obj)) {
            return false;
        }

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (bundleOrderItem == null) {
            if (other.bundleOrderItem != null) {
                return false;
            }
        } else if (!bundleOrderItem.equals(other.bundleOrderItem)) {
            return false;
        }
        if (sku == null) {
            if (other.sku != null) {
                return false;
            }
        } else if (!sku.equals(other.sku)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = super.hashCode();
        int result = 1;
        result = prime * result + ((bundleOrderItem == null) ? 0 : bundleOrderItem.hashCode());
        result = prime * result + ((sku == null) ? 0 : sku.hashCode());
        return result;
    }

    @Override
    public boolean isDiscountingAllowed() {
        if (discountsAllowed == null) {
            return sku.isDiscountable();
        } else {
            return discountsAllowed.booleanValue();
        }
    }

    @Override
    public BundleOrderItem findParentItem() {
        for (OrderItem orderItem : getOrder().getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleItem = (BundleOrderItem) orderItem;
                for (OrderItem containedItem : bundleItem.getOrderItems()) {
                    if (containedItem.equals(this)) {
                        return bundleItem;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public CreateResponse<DiscreteOrderItem> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<DiscreteOrderItem> createResponse = super.createOrRetrieveCopyInstance(context);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        DiscreteOrderItem cloned = createResponse.getClone();
        cloned.setBaseRetailPrice(getBaseRetailPrice());
        cloned.setBaseSalePrice(getBaseSalePrice());
        cloned.setProduct(product);
        cloned.setSku(sku);
        cloned.setCategory(category);
        ((DiscreteOrderItemImpl)cloned).discountsAllowed = discountsAllowed;
        cloned.setName(name);
        // dont clone
        cloned.setOrder(order);
        return  createResponse;
    }

    public static class Presentation {
        public static class Tab {
            public static class Name {
                public static final String OrderItems = "OrderImpl_Order_Items_Tab";
            }

            public static class Order {
                public static final int OrderItems = 2000;
            }
        }

        public static class Group {
            public static class Name {
            }

            public static class Order {
            }
        }

        public static class FieldOrder {
            public static final int PRODUCT = 2000;
            public static final int SKU = 3000;
        }
    }

    @Override
    public boolean isSkuActive() {
        return sku.isActive();
    }
}
