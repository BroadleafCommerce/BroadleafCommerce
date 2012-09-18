/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.core.catalog.domain;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.pricelist.domain.PriceListImpl;
import org.broadleafcommerce.core.catalog.service.dynamic.DefaultDynamicSkuPricingInvocationHandler;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPrices;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.pricing.domain.PriceDataImpl;
import org.broadleafcommerce.core.pricing.domain.SkuBundleItemPriceData;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.Parameter;

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
    @AdminPresentation(friendlyName = "SkuBundleItemImpl_ID", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "bundleItemQuantity", requiredOverride=RequiredOverride.REQUIRED)
    protected Integer quantity;

    @Column(name = "ITEM_SALE_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "bundleItemSalePrice", tooltip="bundleItemSalePriceTooltip", fieldType = SupportedFieldType.MONEY)
    protected BigDecimal itemSalePrice;

    @ManyToOne(targetEntity = ProductBundleImpl.class, optional = false)
    @JoinColumn(name = "PRODUCT_BUNDLE_ID", referencedColumnName = "PRODUCT_ID")
    protected ProductBundle bundle;

    @ManyToOne(targetEntity = SkuImpl.class, optional = false)
    @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID")
    private Sku sku;
    
    /** The pricelist/pricedata. */
    @ManyToMany(targetEntity = PriceDataImpl.class)
    @JoinTable(name = "BLC_SKU_BUNDLE_PRICE_DATA", joinColumns = @JoinColumn(name = "SKU_BUNDLE_ITEM_ID", referencedColumnName = "SKU_BUNDLE_ITEM_ID"), inverseJoinColumns = @JoinColumn(name = "PRICE_DATA_ID", referencedColumnName = "PRICE_DATA_ID"))
    @MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 20)
    @AdminPresentationMap(
            friendlyName = "SkuImpl_PriceData",
           // targetUIElementId = "productSkuMediaLayout",
            dataSourceName = "skuPriceDataMapDS",
            keyPropertyFriendlyName = "PriceListImpl_Key",
            deleteEntityUponRemove = true,
            mapKeyOptionEntityClass = PriceListImpl.class,
            mapKeyOptionEntityDisplayField = "friendlyName",
            mapKeyOptionEntityValueField = "priceKey"
      
        )
    protected Map<String, SkuBundleItemPriceData> priceDataMap = new HashMap<String , SkuBundleItemPriceData>();


    @Transient
    protected DynamicSkuPrices dynamicPrices = null;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
   
    public Money getDynamicSalePrice(Sku sku, BigDecimal salePrice) {   
        Money returnPrice = null;
        
        if (SkuPricingConsiderationContext.hasDynamicPricing()) {
            if (dynamicPrices != null) {
                returnPrice = dynamicPrices.getSalePrice();
            } else {
                DefaultDynamicSkuPricingInvocationHandler handler = new DefaultDynamicSkuPricingInvocationHandler(sku);
                Sku proxy = (Sku) Proxy.newProxyInstance(getClass().getClassLoader(), getClass().getInterfaces(), handler);
                
                dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getSkuPrices(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
                returnPrice = dynamicPrices.getSalePrice();
            }
        } else {
            if (salePrice != null) {
                returnPrice = new Money(salePrice,Money.defaultCurrency());
            }
        }
        
        return returnPrice;   
    }
    @Override
    public void setSalePrice(Money salePrice) {
        if (salePrice != null) {
            this.itemSalePrice = salePrice.getAmount();
        } else {
            this.itemSalePrice = null;
        }
    }


    @Override
    public Money getSalePrice() {
        if (itemSalePrice == null) {
            return sku.getSalePrice();
        } else {
            return getDynamicSalePrice(sku, itemSalePrice);
        }
    }

    @Override
    public Money getRetailPrice() {
         return sku.getRetailPrice();
     }

    @Override
    public ProductBundle getBundle() {
        return bundle;
    }

    @Override
    public void setBundle(ProductBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public Sku getSku() {
        return sku;
    }

    @Override
    public void setSku(Sku sku) {
        this.sku = sku;
    }
    @Override
    public Map<String, SkuBundleItemPriceData> getPriceDataMap() {
        return priceDataMap;
    }

    @Override
    public void setPriceDataMap(Map<String, SkuBundleItemPriceData> priceDataMap) {
        this.priceDataMap = priceDataMap;
    }
}
