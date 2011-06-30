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
package org.broadleafcommerce.core.catalog.domain.sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuAttribute;
import org.broadleafcommerce.core.catalog.domain.SkuAttributeImpl;
import org.broadleafcommerce.core.catalog.domain.common.EmbeddedSandBoxItem;
import org.broadleafcommerce.core.catalog.domain.common.SandBoxItem;
import org.broadleafcommerce.core.catalog.domain.common.SkuMappedSuperclass;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.media.domain.sandbox.SandBoxMediaImpl;
import org.compass.annotations.Searchable;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.Table;

/**
 * @author jfischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_SKU_SNDBX", indexes={
		@Index(name="SKU_SNDBX_VER_INDX", columnNames={"VERSION"}),
		@Index(name="SKU_SNDBX_NAME_INDX", columnNames={"NAME"}),
		@Index(name="SKU_SNDBX_TXBL_INDX", columnNames={"TAXABLE_FLAG"}),
		@Index(name="SKU_SNDBX_DSCNTBL_INDX", columnNames={"DISCOUNTABLE_FLAG"}),
		@Index(name="SKU_SNDBX_AVAIL_INDX", columnNames={"AVAILABLE_FLAG"}),
		@Index(name="SKU_SNDBX_ACTIVE_INDX", columnNames={"ACTIVE_START_DATE","ACTIVE_END_DATE"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@Searchable
public class SandBoxSkuImpl extends SkuMappedSuperclass implements Sku, SandBoxItem {
	
	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(SandBoxSkuImpl.class);
	
    /** The sku images. */
    @CollectionOfElements
    @JoinTable(name = "BLC_SKU_SNDBX_IMAGE", joinColumns = @JoinColumn(name = "SKU_ID"))
    @org.hibernate.annotations.MapKey(columns = { @Column(name = "NAME", length = 5, nullable = false) })
    @Column(name = "URL")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @Deprecated
    protected Map<String, String> skuImages = new HashMap<String, String>();

    /** The sku media. */
    @ManyToMany(targetEntity = SandBoxMediaImpl.class)
    @JoinTable(name = "BLC_SKU_MEDIA_SNDBX_MAP", inverseJoinColumns = @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID"))
    @MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Map<String, Media> skuMedia = new HashMap<String , Media>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = SandBoxProductImpl.class)
    @JoinTable(name = "BLC_PRDCT_SKU_SNDBX_XREF", joinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID", nullable = true), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID", nullable = true))
    protected List<Product> allParentProducts = new ArrayList<Product>();

    @OneToMany(mappedBy = "sku", targetEntity = SkuAttributeImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<SkuAttribute> skuAttributes  = new ArrayList<SkuAttribute>();
    
    @Embedded
    protected SandBoxItem sandBoxItem = new EmbeddedSandBoxItem();
    
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

    public List<Product> getAllParentProducts() {
        return allParentProducts;
    }

    public void setAllParentProducts(List<Product> allParentProducts) {
        this.allParentProducts = allParentProducts;
    }
    
    /**
	 * @return the skuAttributes
	 */
	public List<SkuAttribute> getSkuAttributes() {
		return skuAttributes;
	}

	/**
	 * @param skuAttributes the skuAttributes to set
	 */
	public void setSkuAttributes(List<SkuAttribute> skuAttributes) {
		this.skuAttributes = skuAttributes;
	}

    /**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#getVersion()
	 */
	public long getVersion() {
		return sandBoxItem.getVersion();
	}

	/**
	 * @param version
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#setVersion(long)
	 */
	public void setVersion(long version) {
		sandBoxItem.setVersion(version);
	}

	/**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#isDirty()
	 */
	public boolean isDirty() {
		return sandBoxItem.isDirty();
	}

	/**
	 * @param dirty
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {
		sandBoxItem.setDirty(dirty);
	}

	/**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#getCommaDelimitedDirtyFields()
	 */
	public String getCommaDelimitedDirtyFields() {
		return sandBoxItem.getCommaDelimitedDirtyFields();
	}

	/**
	 * @param commaDelimitedDirtyFields
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#setCommaDelimitedDirtyFields(java.lang.String)
	 */
	public void setCommaDelimitedDirtyFields(String commaDelimitedDirtyFields) {
		sandBoxItem.setCommaDelimitedDirtyFields(commaDelimitedDirtyFields);
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SandBoxSkuImpl other = (SandBoxSkuImpl) obj;

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
