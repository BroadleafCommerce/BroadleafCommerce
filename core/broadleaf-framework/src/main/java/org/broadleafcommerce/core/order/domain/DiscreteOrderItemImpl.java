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

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.order.service.manipulation.OrderItemVisitor;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_DISCRETE_ORDER_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationClass(friendlyName = "discreteOrderItem")
public class DiscreteOrderItemImpl extends OrderItemImpl implements DiscreteOrderItem {

    private static final long serialVersionUID = 1L;
    
    @Column(name="BASE_RETAIL_PRICE")
    @AdminPresentation(friendlyName="Base Retail Price", order=2, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal baseRetailPrice;
    
    @Column(name="BASE_SALE_PRICE")
    @AdminPresentation(friendlyName="Base Sale Price", order=2, group="Pricing", fieldType= SupportedFieldType.MONEY)
    protected BigDecimal baseSalePrice;
    
    @ManyToOne(targetEntity = SkuImpl.class, optional=false)
    @JoinColumn(name = "SKU_ID", nullable = false)
    @Index(name="DISCRETE_SKU_INDEX", columnNames={"SKU_ID"})
    protected Sku sku;

    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name="DISCRETE_PRODUCT_INDEX", columnNames={"PRODUCT_ID"})
    @NotFound(action = NotFoundAction.IGNORE)
    protected Product product;

    @ManyToOne(targetEntity = BundleOrderItemImpl.class)
    @JoinColumn(name = "BUNDLE_ORDER_ITEM_ID")
    @Index(name="DISCRETE_BUNDLE_INDEX", columnNames={"BUNDLE_ORDER_ITEM_ID"})
    protected BundleOrderItem bundleOrderItem;
    
    @CollectionOfElements
    @JoinTable(name = "BLC_ORDER_ITEM_ADD_ATTR", joinColumns = @JoinColumn(name = "ORDER_ITEM_ID"))
    @MapKey(columns = { @Column(name = "NAME", nullable = false) })
    @Column(name = "VALUE")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOrderElements")
    @BatchSize(size = 50)
    protected Map<String, String> additionalAttributes = new HashMap<String, String>();
    
    @OneToMany(mappedBy = "discreteOrderItem", targetEntity = DiscreteOrderItemFeePriceImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<DiscreteOrderItemFeePrice> discreteOrderItemFeePrices = new ArrayList<DiscreteOrderItemFeePrice>();

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
        if (sku.getRetailPrice() != null) {
            this.baseRetailPrice = sku.getRetailPrice().getAmount();
        }
        if (sku.getSalePrice() != null) {
            this.baseSalePrice = sku.getSalePrice().getAmount();
        }
        setName(sku.getName());
    }

    public Money getTaxablePrice() {
        Money taxablePrice = new Money(0D);
        if (sku.isTaxable() == null || sku.isTaxable()) {
            taxablePrice = getPrice();
        }
        return taxablePrice;
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
    public String getName() {
        String name = super.getName();
        if (name == null) {
            return sku.getName();
        }
        return name;
    }

    @Override
    public boolean updatePrices() {
        boolean updated = false;
        //use the sku prices - the retail and sale prices could be null
        if (!getSku().getRetailPrice().equals(getRetailPrice())) {
            setBaseRetailPrice(getSku().getRetailPrice());
            setRetailPrice(getSku().getRetailPrice());
            updated = true;
        }
        if (getSku().getSalePrice() != null && !getSku().getSalePrice().equals(getSalePrice())) {
            setBaseSalePrice(getSku().getSalePrice());
            setSalePrice(getSku().getSalePrice());
            updated = true;
        }
        if (getDiscreteOrderItemFeePrices() != null) {
            for (DiscreteOrderItemFeePrice fee : getDiscreteOrderItemFeePrices()) {
                setSalePrice(getSalePrice().add(fee.getAmount()));
                setRetailPrice(getRetailPrice().add(fee.getAmount()));
            }
        }
        return updated;
    }

    public Map<String, String> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Map<String, String> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public Money getBaseRetailPrice() {
        return baseRetailPrice != null?new Money(baseRetailPrice):null;
    }

    public void setBaseRetailPrice(Money baseRetailPrice) {
        this.baseRetailPrice = baseRetailPrice.getAmount();
    }

    public Money getBaseSalePrice() {
        return baseSalePrice!=null?new Money(baseRetailPrice):null;
    }

    public void setBaseSalePrice(Money baseSalePrice) {
        this.baseSalePrice = baseSalePrice==null?null:baseSalePrice.getAmount();
    }

    public List<DiscreteOrderItemFeePrice> getDiscreteOrderItemFeePrices() {
        return discreteOrderItemFeePrices;
    }

    public void setDiscreteOrderItemFeePrices(List<DiscreteOrderItemFeePrice> discreteOrderItemFeePrices) {
        this.discreteOrderItemFeePrices = discreteOrderItemFeePrices;
    }
    
    @Override
    public OrderItem clone() {
        DiscreteOrderItem orderItem = (DiscreteOrderItem) super.clone();
        if (getDiscreteOrderItemFeePrices() != null) orderItem.getDiscreteOrderItemFeePrices().addAll(getDiscreteOrderItemFeePrices());
        if (getAdditionalAttributes() != null) orderItem.getAdditionalAttributes().putAll(getAdditionalAttributes());
        orderItem.setBaseRetailPrice(getBaseRetailPrice());
        orderItem.setBaseSalePrice(getBaseSalePrice());
        orderItem.setBundleOrderItem(getBundleOrderItem());
        orderItem.setProduct(getProduct());
        orderItem.setSku(getSku());
        
        return orderItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiscreteOrderItemImpl other = (DiscreteOrderItemImpl) obj;
        
        if (!super.equals(obj)) {
            return false;
        }

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (bundleOrderItem == null) {
            if (other.bundleOrderItem != null)
                return false;
        } else if (!bundleOrderItem.equals(other.bundleOrderItem))
            return false;
        if (sku == null) {
            if (other.sku != null)
                return false;
        } else if (!sku.equals(other.sku))
            return false;
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
    public void accept(OrderItemVisitor visitor) throws PricingException {
        visitor.visit(this);
    }

}
