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
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Sale_Price", order=9, group = "SkuImpl_Price", prominent=true, fieldType=SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal salePrice;

    /** The retail price. */
    @Column(name = "RETAIL_PRICE", nullable=false, precision=19, scale=5)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Retail_Price", order=10, group = "SkuImpl_Price", prominent=true, fieldType= SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal retailPrice;

    /** The name. */
    @Column(name = "NAME", nullable=false)
    @SearchableProperty
    @Index(name="SKU_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Name", order=1, group = "SkuImpl_Sku_Description", prominent=true, columnWidth="25%", groupOrder=4)
    protected String name;

    /** The description. */
    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Description", order=2, group = "SkuImpl_Sku_Description", largeEntry=true, groupOrder=4)
    protected String description;

    /** The long description. */
    @Lob
    @Column(name = "LONG_DESCRIPTION", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Large_Description", order=3, group = "SkuImpl_Sku_Description", largeEntry=true, groupOrder=4)
    protected String longDescription;

    /** The taxable. */
    @Column(name = "TAXABLE_FLAG")
    @Index(name="SKU_TAXABLE_INDEX", columnNames={"TAXABLE_FLAG"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Taxable", order=4, group = "SkuImpl_Sku_Description", groupOrder=4)
    protected Character taxable;

    /** The discountable. */
    @Column(name = "DISCOUNTABLE_FLAG")
    @Index(name="SKU_DISCOUNTABLE_INDEX", columnNames={"DISCOUNTABLE_FLAG"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Discountable", order=5, group = "SkuImpl_Sku_Description", groupOrder=4)
    protected Character discountable;

    /** The available. */
    @Column(name = "AVAILABLE_FLAG")
    @Index(name="SKU_AVAILABLE_INDEX", columnNames={"AVAILABLE_FLAG"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Available", order=6, group = "SkuImpl_Sku_Description", groupOrder=4)
    protected Character available;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE")
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Start_Date", order=7, group = "SkuImpl_Sku_Description", groupOrder=4)
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    @Index(name="SKU_ACTIVE_INDEX", columnNames={"ACTIVE_START_DATE","ACTIVE_END_DATE"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_End_Date", order=8, group = "SkuImpl_Sku_Description", groupOrder=4)
    protected Date activeEndDate;
    
    /** The product dimensions **/
    @Embedded
    protected ProductDimension dimension = new ProductDimension();

    /** The product weight **/
    @Embedded
    protected ProductWeight weight = new ProductWeight();
    
    @Transient
    protected DynamicSkuPrices dynamicPrices = null;
	
    /** The sku images. */
    @CollectionOfElements
    @JoinTable(name = "BLC_SKU_IMAGE", joinColumns = @JoinColumn(name = "SKU_ID"))
    @org.hibernate.annotations.MapKey(columns = { @Column(name = "NAME", length = 5, nullable = false) })
    @Column(name = "URL")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @Deprecated
    protected Map<String, String> skuImages = new HashMap<String, String>();

    /** The sku media. */
    @ManyToMany(targetEntity = MediaImpl.class)
    @JoinTable(name = "BLC_SKU_MEDIA_MAP", inverseJoinColumns = @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID"))
    @MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Map<String, Media> skuMedia = new HashMap<String , Media>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = ProductImpl.class)
    @JoinTable(name = "BLC_PRODUCT_SKU_XREF", joinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID", nullable = true), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID", nullable = true))
    @Deprecated
    protected List<Product> allParentProducts = new ArrayList<Product>();

    @ManyToOne(optional = true, targetEntity = ProductImpl.class)
    @JoinColumn(name = "DEFAULT_PRODUCT_ID")
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
    List<ProductOptionValue> productOptionValues;

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getId()
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
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

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getSalePrice()
     */
    public Money getSalePrice() {
        if (salePrice == null && hasDefaultSku()) {
            return lookupDefaultSku().getSalePrice();
        }

    	if (dynamicPrices != null) {
    		dynamicPrices.getSalePrice();
    	}
    	if (
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext() != null && 
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext().size() > 0 &&
    			SkuPricingConsiderationContext.getSkuPricingService() != null
    	) {
    		DefaultDynamicSkuPricingInvocationHandler handler = new DefaultDynamicSkuPricingInvocationHandler(this);
    		Sku proxy = (Sku) Proxy.newProxyInstance(getClass().getClassLoader(), getClass().getInterfaces(), handler);
    		dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getSkuPrices(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
    		handler.reset();
    		return dynamicPrices.getSalePrice();
    	} else {
            return salePrice == null ? null : new Money(salePrice);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setSalePrice(org.broadleafcommerce.util.money.Money)
     */
    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getRetailPrice()
     */
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
    		DefaultDynamicSkuPricingInvocationHandler handler = new DefaultDynamicSkuPricingInvocationHandler(this);
    		Sku proxy = (Sku) Proxy.newProxyInstance(getClass().getClassLoader(), getClass().getInterfaces(), handler);
    		dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getSkuPrices(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
    		handler.reset();
    		return dynamicPrices.getRetailPrice();
    	}
        return retailPrice == null ? null : new Money(retailPrice);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setRetailPrice(org.broadleafcommerce
     * .util.money.Money)
     */
    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = Money.toAmount(retailPrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getListPrice()
     */
    public Money getListPrice() {
        return getRetailPrice();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setListPrice(org.broadleafcommerce
     * .util.money.Money)
     */
    public void setListPrice(Money listPrice) {
        this.retailPrice = Money.toAmount(listPrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getName()
     */
    public String getName() {
        if (name == null && hasDefaultSku()) {
            return lookupDefaultSku().getName();
        }
        return name;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getDescription()
     */
    public String getDescription() {
        if (description == null && hasDefaultSku()) {
            return lookupDefaultSku().getDescription();
        }
        return description;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getLongDescription()
     */
    public String getLongDescription() {
        if (longDescription == null && hasDefaultSku()) {
            return lookupDefaultSku().getLongDescription();
        }
        return longDescription;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setLongDescription(java.lang
     * .String)
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#isTaxable()
     */
    public Boolean isTaxable() {
        if (taxable == null) {
            if (hasDefaultSku()) {
                return lookupDefaultSku().isTaxable();
            }
            return null;
        }
        return taxable == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    /*
     * This is to facilitate serialization to non-Java clients
     */
    public Boolean getTaxable() {
        return isTaxable();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setTaxable(java.lang.Boolean)
     */
    public void setTaxable(Boolean taxable) {
        if (taxable == null) {
            this.taxable = null;
        } else {
            this.taxable = taxable ? 'Y' : 'N';
        }
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#isDiscountable()
     */
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

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setDiscountable(java.lang.Boolean)
     */
    public void setDiscountable(Boolean discountable) {
        if (discountable == null) {
            this.discountable = null;
        } else {
            this.discountable = discountable ? 'Y' : 'N';
        }
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#isAvailable()
     */
    public Boolean isAvailable() {
        if (available == null) {
            if (hasDefaultSku()) {
                return lookupDefaultSku().isAvailable();
            }
            return null;
        }
        return available == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean getAvailable() {
    	return isAvailable();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setAvailable(java.lang.Boolean)
     */
    public void setAvailable(Boolean available) {
        if (available == null) {
            this.available = null;
        } else {
            this.available = available ? 'Y' : 'N';
        }
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getActiveStartDate()
     */
    public Date getActiveStartDate() {
        if (activeStartDate == null && hasDefaultSku()) {
            return lookupDefaultSku().getActiveStartDate();
        } else {
            return activeStartDate;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setActiveStartDate(java.util
     * .Date)
     */
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getActiveEndDate()
     */
    public Date getActiveEndDate() {
        if (activeEndDate == null && hasDefaultSku()) {
            return lookupDefaultSku().getActiveEndDate();
        } else {
            return activeEndDate;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setActiveEndDate(java.util.Date)
     */
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    public ProductDimension getDimension() {
        if (dimension == null && hasDefaultSku()) {
            return lookupDefaultSku().getDimension();
        } else {
            return dimension;
        }
    }

    public void setDimension(ProductDimension dimension) {
        this.dimension = dimension;
    }

    public ProductWeight getWeight() {
        if (weight == null && hasDefaultSku()) {
            return lookupDefaultSku().getWeight();
        } else {
            return weight;
        }
    }

    public void setWeight(ProductWeight weight) {
        this.weight = weight;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#isActive()
     */
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), true)) {
                LOG.debug("sku, " + id + ", inactive due to date");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), true);
    }

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
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getSkuImages()
     */
    @Deprecated
    public Map<String, String> getSkuImages() {
        return skuImages;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#getSkuImage(java.lang.String)
     */
    @Deprecated
    public String getSkuImage(String imageKey) {
        return skuImages.get(imageKey);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#setSkuImages(java.util.Map)
     */
    @Deprecated
    public void setSkuImages(Map<String, String> skuImages) {
        this.skuImages = skuImages;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#getSkuMedia()
     */
    public Map<String, Media> getSkuMedia() {
        if (skuMedia == null || skuMedia.isEmpty()) {
            if (hasDefaultSku()) {
                return lookupDefaultSku().getSkuMedia();
            }
        }
        return skuMedia;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#getSkuImage(java.util.Map)
     */
    public void setSkuMedia(Map<String, Media> skuMedia) {
        this.skuMedia = skuMedia;
    }

    public Product getDefaultProduct() {
        return product;
    }

    public void setDefaultProduct(Product product) {
        this.product = product;
    }

    @Deprecated
    public List<Product> getAllParentProducts() {
        return allParentProducts;
    }

    @Deprecated
    public void setAllParentProducts(List<Product> allParentProducts) {
        this.allParentProducts = allParentProducts;
    }

    /**
	 * @return the skuAttributes
	 */
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

	/**
	 * @param skuAttributes the skuAttributes to set
	 */
	public void setSkuAttributes(List<SkuAttribute> skuAttributes) {
		this.skuAttributes = skuAttributes;
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
