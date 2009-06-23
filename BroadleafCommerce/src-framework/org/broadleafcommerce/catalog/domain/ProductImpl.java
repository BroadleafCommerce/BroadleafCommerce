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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.broadleafcommerce.util.DateUtil;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;

/**
 * The Class ProductImpl is the default implementation of {@link Product}. A
 * product is a general description of an item that can be sold (for example: a
 * hat). Products are not sold or added to a cart. {@link Sku}s which are
 * specific items (for example: a XL Blue Hat) are sold or added to a cart. <br>
 * <br>
 * If you want to add fields specific to your implementation of
 * BroadLeafCommerce you should extend this class and add your fields. If you
 * need to make significant changes to the ProductImpl then you should implement
 * your own version of {@link Product}. <br>
 * <br>
 * This implementation uses a Hibernate implementation of JPA configured through
 * annotations. The Entity references the following tables: BLC_PRODUCT,
 * BLC_PRODUCT_SKU_XREF, BLC_PRODUCT_IMAGE
 * @author btaylor
 * @see {@link Product}, {@link SkuImpl}, {@link CategoryImpl}
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ProductImpl implements Product {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    private transient final Logger log = Logger.getLogger(getClass());

    /** The id. */
    @Id
    @GeneratedValue(generator = "ProductId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ProductId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ProductImpl", allocationSize = 50)
    @Column(name = "PRODUCT_ID")
    protected Long id;

    /** The name. */
    @Column(name = "NAME", nullable=false)
    protected String name;

    /** The description. */
    @Column(name = "DESCRIPTION")
    protected String description;

    /** The long description. */
    @Column(name = "LONG_DESCRIPTION")
    protected String longDescription;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE")
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    protected Date activeEndDate;

    /** The product model number */
    @Column(name = "MODEL")
    protected String model;

    /** The manufacture name */
    @Column(name = "MANUFACTURE")
    protected String manufacturer;

    /** The product dimensions **/
    @Embedded
    protected ProductDimension dimension = new ProductDimensionImpl();

    //TODO: may want to create a ProductWeight object with a weight amount and weight type (POUNDS or KILOGRAMS)
    /** The weight of the product */
    @Column(name = "WEIGHT")
    protected BigDecimal weight;

    @OneToMany(mappedBy = "product", targetEntity = CrossSaleProductImpl.class, cascade = {CascadeType.ALL})
    protected List<RelatedProduct> crossSaleProducts = new ArrayList<RelatedProduct>();

    /*    //TODO remove the transient annotations once all SQL files have been updated.*/
    @Transient
    @OneToMany(mappedBy = "product", targetEntity = UpSaleProductImpl.class, cascade = {CascadeType.ALL})
    @OrderBy(value="sequence")
    protected List<RelatedProduct> upSaleProducts  = new ArrayList<RelatedProduct>();

    /** The all skus. */
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = SkuImpl.class)
    @JoinTable(name = "BLC_PRODUCT_SKU_XREF", joinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @BatchSize(size = 50)
    protected List<Sku> allSkus = new ArrayList<Sku>();

    /** The product images. */
    @CollectionOfElements
    @JoinTable(name = "BLC_PRODUCT_IMAGE", joinColumns = @JoinColumn(name = "PRODUCT_ID"))
    @org.hibernate.annotations.MapKey(columns = { @Column(name = "NAME", length = 5) })
    @Column(name = "URL")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    protected Map<String, String> productImages = new HashMap<String, String>();

    /** The default category. */
    @OneToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_CATEGORY_ID")
    protected Category defaultCategory;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = CategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_PRODUCT_XREF", joinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = true))
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @BatchSize(size = 50)
    protected List<Category> allParentCategories = new ArrayList<Category>();

    @Column(name = "IS_FEATURED_PRODUCT", nullable=false)
    protected boolean isFeaturedProduct = false;

    /** The skus. */
    @Transient
    protected List<Sku> skus = new ArrayList<Sku>();

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getId()
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Product#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Product#setDescription(java.lang
     * .String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getLongDescription()
     */
    public String getLongDescription() {
        return longDescription;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Product#setLongDescription(java.
     * lang.String)
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getActiveStartDate()
     */
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Product#setActiveStartDate(java.
     * util.Date)
     */
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getActiveEndDate()
     */
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Product#setActiveEndDate(java.util
     * .Date)
     */
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#isActive()
     */
    public boolean isActive() {
        if (log.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false)) {
                log.debug("product, " + id + ", inactive due to date");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false);
    }

    /**
     * Gets the all skus.
     * @return the all skus
     */
    private List<Sku> getAllSkus() {
        return allSkus;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getSkus()
     */
    public List<Sku> getSkus() {
        if (skus.size() == 0) {
            List<Sku> allSkus = getAllSkus();
            for (Sku sku : allSkus) {
                if (sku.isActive()) {
                    skus.add(sku);
                }
            }
        }
        return skus;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Product#setAllSkus(java.util.List)
     */
    public void setAllSkus(List<Sku> skus) {
        this.allSkus = skus;
        this.skus.clear();
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getProductImages()
     */
    public Map<String, String> getProductImages() {
        return productImages;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Product#getProductImage(java.lang
     * .String)
     */
    public String getProductImage(String imageKey) {
        return productImages.get(imageKey);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Product#setProductImages(java.util
     * .Map)
     */
    public void setProductImages(Map<String, String> productImages) {
        this.productImages = productImages;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Product#getDefaultCategory()
     */
    public Category getDefaultCategory() {
        return defaultCategory;
    }

    /*
     * (non-Javadoc)
     * @seeorg.broadleafcommerce.catalog.domain.Product#setDefaultCategory(org.
     * broadleafcommerce.catalog.domain.Category)
     */
    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public List<Category> getAllParentCategories() {
        return allParentCategories;
    }

    public void setAllParentCategories(List<Category> allParentCategories) {
        this.allParentCategories = allParentCategories;
    }

    public List<RelatedProduct> getCrossSaleProducts() {
        return crossSaleProducts;
    }

    public void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts) {
        this.crossSaleProducts = crossSaleProducts;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public ProductDimension getDimension() {
        return dimension;
    }

    public void setDimension(ProductDimension dimension) {
        this.dimension = dimension;
    }

    public BigDecimal getWidth() {
        return dimension.getWidth();
    }

    public void setWidth(BigDecimal width) {
        dimension.setWidth(width);
    }

    public BigDecimal getHeight() {
        return dimension.getHeight();
    }

    public void setHeight(BigDecimal height) {
        dimension.setHeight(height);
    }

    public BigDecimal getDepth() {
        return dimension.getDepth();
    }

    public void setDepth(BigDecimal depth) {
        dimension.setDepth(depth);
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    /**
     * Returns the product dimensions as a String (assumes measurements are in inches)
     * @return a String value of the product dimensions
     */
    public String getDimensionString() {
        return dimension.getDimensionString();
    }

    public List<RelatedProduct> getUpSaleProducts() {
        return upSaleProducts;
    }

    public void setUpSaleProducts(List<RelatedProduct> upSaleProducts) {
        this.upSaleProducts = upSaleProducts;
    }

    public boolean getIsFeaturedProduct() {
        return isFeaturedProduct;
    }

    public void setFeaturedProduct(boolean isFeaturedProduct) {
        this.isFeaturedProduct = isFeaturedProduct;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((skus == null) ? 0 : skus.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductImpl other = (ProductImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (skus == null) {
            if (other.skus != null)
                return false;
        } else if (!skus.equals(other.skus))
            return false;
        return true;
    }

}
