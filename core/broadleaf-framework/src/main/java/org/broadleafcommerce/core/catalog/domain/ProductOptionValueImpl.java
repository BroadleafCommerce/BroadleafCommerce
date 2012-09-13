/*
 * Copyright 2012 the original author or authors.
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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.pricing.domain.PriceAdjustment;
import org.broadleafcommerce.core.pricing.domain.PriceAdjustmentImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.Parameter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION_VALUE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Product Option Value")
public class ProductOptionValueImpl implements ProductOptionValue {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "ProductOptionValueId")
    @GenericGenerator(
        name="ProductOptionValueId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ProductOptionValueImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl")
        }
    )
    @Column(name = "PRODUCT_OPTION_VALUE_ID")
    protected Long id;

    @Column(name = "ATTRIBUTE_VALUE")
    @AdminPresentation(friendlyName = "Attribute Value")
    protected String attributeValue;

    @Column(name ="DISPLAY_ORDER")
    @AdminPresentation(friendlyName = "Display Order")
    protected Long displayOrder;
    
    @Column(name = "PRICE_ADJUSTMENT", precision=19, scale=5)
    @AdminPresentation(friendlyName="Adjustment", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal priceAdjustment;

    @ManyToOne(targetEntity = ProductOptionImpl.class)
    @JoinColumn(name = "PRODUCT_OPTION_ID")
    protected ProductOption productOption;
    
    /** The pricelist/pricedata. */
    @ManyToMany(targetEntity = PriceAdjustmentImpl.class)
    @JoinTable(name = "BLC_ADJ_PRICE_MAP", joinColumns = @JoinColumn(name = "PRODUCT_OPTION_VALUE_ID", referencedColumnName = "PRODUCT_OPTION_VALUE_ID"), inverseJoinColumns = @JoinColumn(name = "PRICE_ADJUSTMENT_ID", referencedColumnName = "PRICE_ADJUSTMENT_ID"))
    @MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 20)
    protected Map<String, PriceAdjustment> priceAdjustmentMap = new HashMap<String , PriceAdjustment>();
   
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAttributeValue() {
        return attributeValue;
    }

    @Override
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
    
    @Override
    public Long getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    @Override
    public Money getPriceAdjustment() {
        /*
         * if (
                 
                        SkuPricingConsiderationContext.getSkuPricingService() != null
        ) {
 
 // TODO:  handle productOptionValue
                dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getOptionValuePrice(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());                
                returnPrice = dynamicPrices.getSalePrice();
        } else {
            returnPrice = (salePrice == null ? null : new Money(salePrice));
        }
         */
        Money dynamicAdjustedPrice=null;
        if(SkuPricingConsiderationContext.hasDynamicPricing()) {
            dynamicAdjustedPrice=SkuPricingConsiderationContext.getSkuPricingService().getPriceAdjustmentForProductOptionValue(this,SkuPricingConsiderationContext.getSkuPricingConsiderationContext());                
           
         
             
        }
        if (dynamicAdjustedPrice == null) {
            dynamicAdjustedPrice = (priceAdjustment == null ? null : new Money(priceAdjustment));
        }
        return dynamicAdjustedPrice;
    }

    @Override
    public void setPriceAdjustment(Money priceAdjustment) {
        this.priceAdjustment = Money.toAmount(priceAdjustment);
    }

    @Override
    public ProductOption getProductOption() {
        return productOption;
    }

    @Override
    public void setProductOption(ProductOption productOption) {
        this.productOption = productOption;
    }
    @Override
    public Map<String, PriceAdjustment> getPriceAdjustmentMap() {
        return priceAdjustmentMap;
    }

    @Override
    public void setPriceAdjustmentMap(Map<String, PriceAdjustment> priceDataMap) {
        this.priceAdjustmentMap = priceDataMap;
    }

}
