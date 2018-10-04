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
package org.broadleafcommerce.catalog.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.util.DateUtil;
import org.broadleafcommerce.util.money.Money;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

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
@Table(name = "BLC_SKU")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Searchable
public class SkuImpl implements Sku {

    private static final Log LOG = LogFactory.getLog(SkuImpl.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "SkuId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SkuId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SkuImpl", allocationSize = 50)
    @Column(name = "SKU_ID")
    @SearchableId
    protected Long id;

    /** The sale price. */
    @Column(name = "SALE_PRICE")
    protected BigDecimal salePrice;

    /** The retail price. */
    @Column(name = "RETAIL_PRICE", nullable=false)
    protected BigDecimal retailPrice;

    /** The name. */
    @Column(name = "NAME", nullable=false)
    @SearchableProperty
    protected String name;

    /** The description. */
    @Column(name = "DESCRIPTION")
    protected String description;

    /** The long description. */
    @Column(name = "LONG_DESCRIPTION")
    protected String longDescription;

    /** The taxable. */
    @Column(name = "TAXABLE_FLAG")
    protected Character taxable;

    /** The discountable. */
    @Column(name = "DISCOUNTABLE_FLAG")
    protected Character discountable;

    /** The available. */
    @Column(name = "AVAILABLE_FLAG")
    protected Character available;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE")
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    protected Date activeEndDate;

    /** The sku images. */
    @ElementCollection
    @MapKeyColumn(name="NAME", length = 5)
    @Column(name="URL")
    @CollectionTable(name="BLC_SKU_IMAGE", joinColumns=@JoinColumn(name="SKU_ID"))
    @BatchSize(size = 50)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    protected Map<String, String> skuImages = new HashMap<String, String>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = ProductImpl.class)
    @JoinTable(name = "BLC_PRODUCT_SKU_XREF", joinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID", nullable = true), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID", nullable = true))
    protected List<Product> allParentProducts = new ArrayList<Product>();

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getId()
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getSalePrice()
     */
    public Money getSalePrice() {
        return salePrice == null ? null : new Money(salePrice);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#setSalePrice(org.broadleafcommerce
     * .util.money.Money)
     */
    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getRetailPrice()
     */
    public Money getRetailPrice() {
        return retailPrice == null ? null : new Money(retailPrice);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#setRetailPrice(org.broadleafcommerce
     * .util.money.Money)
     */
    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = Money.toAmount(retailPrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getListPrice()
     */
    public Money getListPrice() {
        return retailPrice == null ? null : new Money(retailPrice);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#setListPrice(org.broadleafcommerce
     * .util.money.Money)
     */
    public void setListPrice(Money listPrice) {
        this.retailPrice = Money.toAmount(listPrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getLongDescription()
     */
    public String getLongDescription() {
        return longDescription;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#setLongDescription(java.lang
     * .String)
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#isTaxable()
     */
    public Boolean isTaxable() {
        if (taxable == null)
            return null;
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
     * org.broadleafcommerce.catalog.domain.Sku#setTaxable(java.lang.Boolean)
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
     * @see org.broadleafcommerce.catalog.domain.Sku#isDiscountable()
     */
    public Boolean isDiscountable() {
        if (discountable == null)
            return null;
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
     * org.broadleafcommerce.catalog.domain.Sku#setDiscountable(java.lang.Boolean)
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
     * @see org.broadleafcommerce.catalog.domain.Sku#isAvailable()
     */
    public Boolean isAvailable() {
        if (available == null)
            return null;
        return available == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#setAvailable(java.lang.Boolean)
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
     * @see org.broadleafcommerce.catalog.domain.Sku#getActiveStartDate()
     */
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#setActiveStartDate(java.util
     * .Date)
     */
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getActiveEndDate()
     */
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#setActiveEndDate(java.util.Date)
     */
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#isActive()
     */
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false)) {
                LOG.debug("sku, " + id + ", inactive due to date");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false);
    }

    public boolean isActive(Product product, Category category) {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false)) {
                LOG.debug("sku, " + id + ", inactive due to date");
            } else if (!product.isActive()) {
                LOG.debug("sku, " + id + ", inactive due to product being inactive");
            } else if (!category.isActive()) {
                LOG.debug("sku, " + id + ", inactive due to category being inactive");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false) || !product.isActive() || !category.isActive();
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#getSkuImages()
     */
    public Map<String, String> getSkuImages() {
        return skuImages;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Sku#getSkuImage(java.lang.String)
     */
    public String getSkuImage(String imageKey) {
        return skuImages.get(imageKey);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Sku#setSkuImages(java.util.Map)
     */
    public void setSkuImages(Map<String, String> skuImages) {
        this.skuImages = skuImages;
    }

    public List<Product> getAllParentProducts() {
        return allParentProducts;
    }

    public void setAllParentProducts(List<Product> allParentProducts) {
        this.allParentProducts = allParentProducts;
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

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
}
