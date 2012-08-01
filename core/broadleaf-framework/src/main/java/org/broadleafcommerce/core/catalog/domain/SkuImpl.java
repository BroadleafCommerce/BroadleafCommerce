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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.core.catalog.service.dynamic.DefaultDynamicSkuPricingInvocationHandler;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPrices;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.media.domain.MediaImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.FulfillmentOptionImpl;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class SkuImpl is the default implementation of {@link Sku}. A SKU is a
 * specific item that can be sold including any specific attributes of the item
 * such as color or size. <br>
 * <br>
 * If you want to add fields specific to your implementation of
 * BroadLeafCommerce you should extend this class and add your fields. If you
 * need to make significant changes to the SkuImpl then you should implement
 * your own version of {@link Sku}. <br>
 * <br>
 * This implementation uses a Hibernate implementation of JPA configured through
 * annotations. The Entity references the following tables: BLC_SKU,
 * BLC_SKU_IMAGE
 * @see {@link Sku}
 * @author btaylor
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SKU")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@Searchable
public class SkuImpl implements Sku {
	
	private static final Log LOG = LogFactory.getLog(SkuImpl.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator= "SkuId")
    @GenericGenerator(
        name="SkuId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="SkuImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.SkuImpl")
        }
    )
    @Column(name = "SKU_ID")
    @SearchableId
    @AdminPresentation(friendlyName = "SkuImpl_Sku_ID", group = "SkuImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    /** The sale price. */
    @Column(name = "SALE_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Sale_Price", order=2, group = "SkuImpl_Price", prominent=true, fieldType=SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal salePrice;

    /** The retail price. */
    @Column(name = "RETAIL_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Retail_Price", order=1, group = "SkuImpl_Price", prominent=true, fieldType= SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal retailPrice;

    /** The name. */
    @Column(name = "NAME")
    @SearchableProperty
    @Index(name="SKU_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Name", order=1, group = "ProductImpl_Product_Description", prominent=true, columnWidth="25%", groupOrder=1)
    protected String name;

    /** The description. */
    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Description", order=2, group = "ProductImpl_Product_Description", largeEntry=true, groupOrder=1)
    protected String description;

    /** The long description. */
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "LONG_DESCRIPTION", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Large_Description", order=5, group = "ProductImpl_Product_Description", largeEntry=true, groupOrder=1,fieldType=SupportedFieldType.HTML_BASIC)
    protected String longDescription;

    /** The taxable. */
    @Column(name = "TAXABLE_FLAG")
    @Index(name="SKU_TAXABLE_INDEX", columnNames={"TAXABLE_FLAG"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Taxable", order=3, group = "SkuImpl_Price", groupOrder=3)
    protected Character taxable;

    /** The discountable. */
    @Column(name = "DISCOUNTABLE_FLAG")
    @Index(name="SKU_DISCOUNTABLE_INDEX", columnNames={"DISCOUNTABLE_FLAG"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Discountable", order=4, group = "SkuImpl_Price", groupOrder=3)
    protected Character discountable;

    /** The available. */
    @Column(name = "AVAILABLE_FLAG")
    @Index(name="SKU_AVAILABLE_INDEX", columnNames={"AVAILABLE_FLAG"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Available", order=6, group = "SkuImpl_Sku_Inventory", groupOrder=5)
    protected Character available;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE")
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Start_Date", order=7, group = "ProductImpl_Product_Description", tooltip="skuStartDateTooltip", groupOrder=1)
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    @Index(name="SKU_ACTIVE_INDEX", columnNames={"ACTIVE_START_DATE","ACTIVE_END_DATE"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_End_Date", order=8, group = "ProductImpl_Product_Description", tooltip="skuEndDateTooltip", groupOrder=1)
    protected Date activeEndDate;
    
    /** The product dimensions **/
    @Embedded
    protected Dimension dimension = new Dimension();

    /** The product weight **/
    @Embedded
    protected Weight weight = new Weight();
    
    @Transient
    protected DynamicSkuPrices dynamicPrices = null;

    @Column(name = "IS_MACHINE_SORTABLE")
    @AdminPresentation(friendlyName = "ProductImpl_Is_Product_Machine_Sortable", order=15, group = "ProductImpl_Product_Description", prominent=false)
    protected Boolean isMachineSortable = true;

    /** The sku media. */
    @ManyToMany(targetEntity = MediaImpl.class)
    @JoinTable(name = "BLC_SKU_MEDIA_MAP", inverseJoinColumns = @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID"))
    @MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Map<String, Media> skuMedia = new HashMap<String , Media>();
    
    /**
     * This will be non-null if and only if this Sku is the default Sku for a Product
     */
    @OneToOne(optional=true, targetEntity=ProductImpl.class, mappedBy="defaultSku")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Product defaultProduct;

    /**
     * This relationship will be non-null if and only if this Sku is contained in the list of
     * additional Skus for a Product (for Skus based on ProductOptions)
     */
    @ManyToOne(optional = true, targetEntity = ProductImpl.class)
    @JoinTable(name = "BLC_PRODUCT_SKU_XREF", joinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Product product;
    
    @OneToMany(mappedBy = "sku", targetEntity = SkuAttributeImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<SkuAttribute> skuAttributes  = new ArrayList<SkuAttribute>();
    
    @ManyToMany(targetEntity = ProductOptionValueImpl.class)
    @JoinTable(name = "BLC_SKU_OPTION_VALUE_XREF", joinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_OPTION_VALUE_ID", referencedColumnName = "PRODUCT_OPTION_VALUE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<ProductOptionValue> productOptionValues;
    
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = SkuFeeImpl.class)
    @JoinTable(name = "BLC_SKU_FEE_XREF",
                   joinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "SKU_FEE_ID", referencedColumnName = "SKU_FEE_ID", nullable = true))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected List<SkuFee> fees;

    @ElementCollection
    @CollectionTable(name = "BLC_SKU_FULFILLMENT_FLAT_RATES", 
                   joinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID", nullable = true))
    @MapKeyJoinColumn(name = "FULFILLMENT_OPTION_ID", referencedColumnName = "FULFILLMENT_OPTION_ID")
    @MapKeyClass(FulfillmentOptionImpl.class)
    @Column(name = "RATE", precision=19, scale=5)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Map<FulfillmentOption, BigDecimal> fulfillmentFlatRates = new HashMap<FulfillmentOption, BigDecimal>();
    
    @ManyToMany(targetEntity = FulfillmentOptionImpl.class)
    @JoinTable(name = "BLC_SKU_FULFILLMENT_EXCLUDED", 
                joinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID"), 
         inverseJoinColumns = @JoinColumn(name = "FULFILLMENT_OPTION_ID", referencedColumnName = "FULFILLMENT_OPTION_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<FulfillmentOption> excludedFulfillmentOptions;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isOnSale() {
    	Money retailPrice = getRetailPrice();
    	Money salePrice = getSalePrice();
    	return (salePrice != null && !salePrice.isZero() && salePrice.lessThan(retailPrice));
    }

    private boolean hasDefaultSku() {
        return (product != null && product.getDefaultSku() != null && ! getId().equals(product.getDefaultSku().getId()));
    }

    private Sku lookupDefaultSku() {
        if (product != null && product.getDefaultSku() != null) {
            return product.getDefaultSku();
        } else {
            return null;
        }
    }
    
    @Override
    public Money getProductOptionValueAdjustments() {
        Money optionValuePriceAdjustments = null;
        if (getProductOptionValues() != null) {
            for (ProductOptionValue value : getProductOptionValues()) {
                if (value.getPriceAdjustment() != null) {
                    if (optionValuePriceAdjustments == null) {
                        optionValuePriceAdjustments = Money.ZERO;
                    }
                    optionValuePriceAdjustments = optionValuePriceAdjustments.add(value.getPriceAdjustment());
                }
            }
        }
        return optionValuePriceAdjustments;
    }

    @Override
    public Money getSalePrice() {
        if (salePrice == null && hasDefaultSku()) {
            return lookupDefaultSku().getSalePrice();
        }

    	if (dynamicPrices != null) {
    		return dynamicPrices.getSalePrice();
    	}
    	if (
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext() != null && 
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext().size() > 0 &&
    			SkuPricingConsiderationContext.getSkuPricingService() != null
    	) {
    		DefaultDynamicSkuPricingInvocationHandler handler = new DefaultDynamicSkuPricingInvocationHandler(this, getProductOptionValueAdjustments());
    		Sku proxy = (Sku) Proxy.newProxyInstance(getClass().getClassLoader(), getClass().getInterfaces(), handler);
    		
    		dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getSkuPrices(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
    		handler.reset();
    		return dynamicPrices.getSalePrice();
    	} else {
    	    if (getProductOptionValueAdjustments() == null) {
    	        return salePrice == null ? null : new Money(salePrice);
    	    } else {
    	        return salePrice == null ? getProductOptionValueAdjustments() : getProductOptionValueAdjustments().add(new Money(salePrice));
    	    }
        }
    }

    @Override
    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }

    @Override
    public Money getRetailPrice() {
        if (retailPrice == null && hasDefaultSku()) {
            return lookupDefaultSku().getRetailPrice();
        }

    	if (dynamicPrices != null) {
    		return dynamicPrices.getRetailPrice();
    	}
    	if (
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext() != null && 
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext().size() > 0 &&
    			SkuPricingConsiderationContext.getSkuPricingService() != null
    	) {
    		DefaultDynamicSkuPricingInvocationHandler handler = new DefaultDynamicSkuPricingInvocationHandler(this, getProductOptionValueAdjustments());
    		Sku proxy = (Sku) Proxy.newProxyInstance(getClass().getClassLoader(), getClass().getInterfaces(), handler);
    		
    		dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getSkuPrices(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
    		handler.reset();
    		return dynamicPrices.getRetailPrice();
    	}
    	if (getProductOptionValueAdjustments() == null) {
            return retailPrice == null ? null : new Money(retailPrice);
        } else {
            return retailPrice == null ? getProductOptionValueAdjustments() : getProductOptionValueAdjustments().add(new Money(retailPrice));
        }
    }

    @Override
    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = Money.toAmount(retailPrice);
    }

    @Override
    public Money getListPrice() {
        return getRetailPrice();
    }

    @Override
    public void setListPrice(Money listPrice) {
        this.retailPrice = Money.toAmount(listPrice);
    }

    @Override
    public String getName() {
        if (name == null && hasDefaultSku()) {
            return lookupDefaultSku().getName();
        }
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        if (description == null && hasDefaultSku()) {
            return lookupDefaultSku().getDescription();
        }
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLongDescription() {
        if (longDescription == null && hasDefaultSku()) {
            return lookupDefaultSku().getLongDescription();
        }
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public Boolean isTaxable() {
        if (taxable == null) {
            if (hasDefaultSku()) {
                return lookupDefaultSku().isTaxable();
            }
            return null;
        }
        return taxable == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Boolean getTaxable() {
        return isTaxable();
    }

    @Override
    public void setTaxable(Boolean taxable) {
        if (taxable == null) {
            this.taxable = null;
        } else {
            this.taxable = taxable ? 'Y' : 'N';
        }
    }

    @Override
    public Boolean isDiscountable() {
        if (discountable == null) {
            if (hasDefaultSku()) {
                return lookupDefaultSku().isDiscountable();
            }
            return null;
        }
        return discountable == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    /*
     * This is to facilitate serialization to non-Java clients
     */
    public Boolean getDiscountable() {
        return isDiscountable();
    }

    @Override
    public void setDiscountable(Boolean discountable) {
        if (discountable == null) {
            this.discountable = null;
        } else {
            this.discountable = discountable ? 'Y' : 'N';
        }
    }

    @Override
    public Boolean isAvailable() {
        if (available == null) {
            if (hasDefaultSku()) {
                return lookupDefaultSku().isAvailable();
            }
            return null;
        }
        return available == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Boolean getAvailable() {
    	return isAvailable();
    }

    @Override
    public void setAvailable(Boolean available) {
        if (available == null) {
            this.available = null;
        } else {
            this.available = available ? 'Y' : 'N';
        }
    }

    @Override
    public Date getActiveStartDate() {
        if (activeStartDate == null && hasDefaultSku()) {
            return lookupDefaultSku().getActiveStartDate();
        } else {
            return activeStartDate;
        }
    }

    @Override
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    @Override
    public Date getActiveEndDate() {
        if (activeEndDate == null && hasDefaultSku()) {
            return lookupDefaultSku().getActiveEndDate();
        } else {
            return activeEndDate;
        }
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    @Override
    public Dimension getDimension() {
        if (dimension == null && hasDefaultSku()) {
            return lookupDefaultSku().getDimension();
        } else {
            return dimension;
        }
    }

    @Override
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    @Override
    public Weight getWeight() {
        if (weight == null && hasDefaultSku()) {
            return lookupDefaultSku().getWeight();
        } else {
            return weight;
        }
    }

    @Override
    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    @Override
    public boolean isActive() {
    	if (activeStartDate == null && activeEndDate == null && hasDefaultSku()) {
    		return lookupDefaultSku().isActive();
    	}
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), true)) {
                LOG.debug("sku, " + id + ", inactive due to date");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), true);
    }

    @Override
    public boolean isActive(Product product, Category category) {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), true)) {
                LOG.debug("sku, " + id + ", inactive due to date");
            } else if (!product.isActive()) {
                LOG.debug("sku, " + id + ", inactive due to product being inactive");
            } else if (!category.isActive()) {
                LOG.debug("sku, " + id + ", inactive due to category being inactive");
            }
        }
        return this.isActive() && product.isActive() && category.isActive();
    }

    @Override
    public Map<String, Media> getSkuMedia() {
        if (skuMedia == null || skuMedia.isEmpty()) {
            if (hasDefaultSku()) {
                return lookupDefaultSku().getSkuMedia();
            }
        }
        return skuMedia;
    }

    @Override
    public void setSkuMedia(Map<String, Media> skuMedia) {
        this.skuMedia = skuMedia;
    }

    @Override
    public Product getDefaultProduct() {
        return defaultProduct;
    }

    @Override
    public void setDefaultProduct(Product defaultProduct) {
        this.defaultProduct = defaultProduct;
    }
    
    @Override
    public Product getProduct() {
        return (getDefaultProduct() != null) ? getDefaultProduct() : this.product;
    }

    @Override
    public void setProduct(Product product) {
        this.product = product;
    }
    
    @Override
	public List<SkuAttribute> getSkuAttributes() {
		return skuAttributes;
	}

    @Override
    public List<ProductOptionValue> getProductOptionValues() {
        return productOptionValues;
    }

    @Override
    public void setProductOptionValues(List<ProductOptionValue> productOptionValues) {
        this.productOptionValues = productOptionValues;
    }

    @Override
	public void setSkuAttributes(List<SkuAttribute> skuAttributes) {
		this.skuAttributes = skuAttributes;
	}

    @Override
    public Boolean isMachineSortable() {
    	 if (isMachineSortable == null && hasDefaultSku()) {
             return lookupDefaultSku().isMachineSortable();
         }
        return isMachineSortable;
    }

    @Override
    public void setMachineSortable(Boolean isMachineSortable) {
        this.isMachineSortable = isMachineSortable;
    }

    @Override
    public List<SkuFee> getFees() {
        return fees;
    }

    @Override
    public void setFees(List<SkuFee> fees) {
        this.fees = fees;
    }
    
    @Override
    public Map<FulfillmentOption, BigDecimal> getFulfillmentFlatRates() {
        return fulfillmentFlatRates;
    }
    
    @Override
    public void setFulfillmentFlatRates(Map<FulfillmentOption, BigDecimal> fulfillmentFlatRates) {
        this.fulfillmentFlatRates = fulfillmentFlatRates;
    }
    
    @Override
    public List<FulfillmentOption> getExcludedFulfillmentOptions() {
        return excludedFulfillmentOptions;
    }

    @Override
    public void setExcludedFulfillmentOptions(List<FulfillmentOption> excludedFulfillmentOptions) {
        this.excludedFulfillmentOptions = excludedFulfillmentOptions;
    }
    
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkuImpl other = (SkuImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }
    
}
