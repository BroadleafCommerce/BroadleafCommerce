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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.broadleafcommerce.core.catalog.domain.common.ProductMappedSuperclass;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.media.domain.MediaImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.compass.annotations.Searchable;
import org.compass.annotations.SupportUnmarshall;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.Table;

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
 * @see {@link Product}, {@link SandBoxSkuImpl}, {@link CategoryImpl}
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_PRODUCT", indexes={
		@Index(name="PRODUCT_NAME_INDEX", columnNames={"NAME"}),
		@Index(name="PRODUCT_CATEGORY_INDEX", columnNames={"DEFAULT_CATEGORY_ID"})
})
//@Table(name = "BLC_PRODUCT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@Searchable(alias="product", supportUnmarshall=SupportUnmarshall.FALSE)
public class ProductImpl extends ProductMappedSuperclass implements Product {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "product", targetEntity = CrossSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})    
    protected List<RelatedProduct> crossSaleProducts = new ArrayList<RelatedProduct>();

    @OneToMany(mappedBy = "product", targetEntity = UpSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})    
    @OrderBy(value="sequence")
    protected List<RelatedProduct> upSaleProducts  = new ArrayList<RelatedProduct>();

    /** The all skus. */
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = SkuImpl.class)
    @JoinTable(name = "BLC_PRODUCT_SKU_XREF", joinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Sku> allSkus = new ArrayList<Sku>();

    /** The product images. */
    @CollectionOfElements
    @JoinTable(name = "BLC_PRODUCT_IMAGE", joinColumns = @JoinColumn(name = "PRODUCT_ID"))
    @org.hibernate.annotations.MapKey(columns = { @Column(name = "NAME", length = 5, nullable = false) })
    @Column(name = "URL")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @Deprecated
    protected Map<String, String> productImages = new HashMap<String, String>();

    /** The product media. */
    @ManyToMany(targetEntity = MediaImpl.class)
    @JoinTable(name = "BLC_PRODUCT_MEDIA_MAP", inverseJoinColumns = @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID"))
    @MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected Map<String, Media> productMedia = new HashMap<String , Media>();

    /** The default category. */
    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_CATEGORY_ID")
    @AdminPresentation(friendlyName="Product Default Category", order=6, group="Product Description")
    protected Category defaultCategory;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = CategoryImpl.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "BLC_CATEGORY_PRODUCT_XREF", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable=true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Category> allParentCategories = new ArrayList<Category>();
    
    @OneToMany(mappedBy = "product", targetEntity = ProductAttributeImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<ProductAttribute> productAttributes  = new ArrayList<ProductAttribute>();

    /**
     * Gets the all skus.
     * @return the all skus
     */
    public List<Sku> getAllSkus() {
        return allSkus;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getSkus()
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
     * org.broadleafcommerce.core.catalog.domain.Product#setAllSkus(java.util.List)
     */
    public void setAllSkus(List<Sku> skus) {
        this.allSkus.clear();
        for(Sku sku : skus){
        	this.allSkus.add(sku);
        }
        //this.skus.clear();
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getProductImages()
     */
    @Deprecated
    public Map<String, String> getProductImages() {
        return productImages;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Product#getProductImage(java.lang
     * .String)
     */
    @Deprecated
    public String getProductImage(String imageKey) {
        return productImages.get(imageKey);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Product#setProductImages(java.util
     * .Map)
     */
    @Deprecated
    public void setProductImages(Map<String, String> productImages) {
        this.productImages.clear();
//        for(String key : productImages.keySet()){
//        	this.productImages.put(key, productImages.get(key));
//        }
    	for(Map.Entry<String, String> me : productImages.entrySet()) {
    		this.productImages.put(me.getKey(), me.getValue());
    	}
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getDefaultCategory()
     */
    public Category getDefaultCategory() {
        return defaultCategory;
    }

    public Map<String, Media> getProductMedia() {
        return productMedia;
    }

    public void setProductMedia(Map<String, Media> productMedia) {
        this.productMedia.clear();
    	for(Map.Entry<String, Media> me : productMedia.entrySet()) {
    		this.productMedia.put(me.getKey(), me.getValue());
    	}
    }

    /*
     * (non-Javadoc)
     * @seeorg.broadleafcommerce.core.catalog.domain.Product#setDefaultCategory(org.
     * broadleafcommerce.catalog.domain.Category)
     */
    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public List<Category> getAllParentCategories() {
        return allParentCategories;
    }

    public void setAllParentCategories(List<Category> allParentCategories) {    	
        this.allParentCategories.clear();
        for(Category category : allParentCategories){
        	this.allParentCategories.add(category);
        }
    }

    public List<RelatedProduct> getCrossSaleProducts() {
        return crossSaleProducts;
    }

    public void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts) {
        this.crossSaleProducts.clear();
        for(RelatedProduct relatedProduct : crossSaleProducts){
        	this.crossSaleProducts.add(relatedProduct);
        }    	
    }

    public List<RelatedProduct> getUpSaleProducts() {
        return upSaleProducts;
    }

    public void setUpSaleProducts(List<RelatedProduct> upSaleProducts) {
        this.upSaleProducts.clear();
        for(RelatedProduct relatedProduct : upSaleProducts){
        	this.upSaleProducts.add(relatedProduct);
        }
        this.upSaleProducts = upSaleProducts;
    }

    public List<ProductAttribute> getProductAttributes() {
		return productAttributes;
	}

	public void setProductAttributes(List<ProductAttribute> productAttributes) {
		this.productAttributes = productAttributes;
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
