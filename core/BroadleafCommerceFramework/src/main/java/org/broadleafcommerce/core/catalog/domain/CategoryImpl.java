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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.core.catalog.domain.common.CategoryMappedSuperclass;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.media.domain.MediaImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.profile.cache.CacheFactoryException;
import org.broadleafcommerce.profile.cache.HydratedCacheManager;
import org.broadleafcommerce.profile.cache.HydratedCacheManagerImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.OrderBy;

/**
 * The Class CategoryImpl is the default implementation of {@link Category}. A
 * category is a group of products. <br>
 * <br>
 * If you want to add fields specific to your implementation of
 * BroadLeafCommerce you should extend this class and add your fields. If you
 * need to make significant changes to the CategoryImpl then you should implement
 * your own version of {@link Category}. <BR>
 * <BR>
 * This implementation uses a Hibernate implementation of JPA configured through
 * annotations. The Entity references the following tables: BLC_CATEGORY,
 * BLC_CATEGORY_XREF, BLC_CATEGORY_IMAGE
 * @see {@link Category}
 * @author btaylor
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class CategoryImpl extends CategoryMappedSuperclass implements Category {

	private static final long serialVersionUID = 1L;
	
    /** The default parent category. */
    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_PARENT_CATEGORY_ID")
    @Index(name="CATEGORY_PARENT_INDEX", columnNames={"DEFAULT_PARENT_CATEGORY_ID"})
    @AdminPresentation(friendlyName="Category Default Parent", order=7, group="Description")
    protected Category defaultParentCategory;

    /** The all child categories. */
    @ManyToMany(targetEntity = CategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "SUB_CATEGORY_ID", referencedColumnName = "CATEGORY_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Category> allChildCategories = new ArrayList<Category>();

    /** The all parent categories. */	
    @ManyToMany(targetEntity = CategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_XREF", joinColumns = @JoinColumn(name = "SUB_CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Category> allParentCategories = new ArrayList<Category>();
    
    /** The all parent categories. */	
    @ManyToMany(targetEntity = ProductImpl.class)
    @JoinTable(name = "BLC_CATEGORY_PRODUCT_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", nullable = true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Product> allProducts = new ArrayList<Product>();

    /** The category images. */
    @CollectionOfElements
    @JoinTable(name = "BLC_CATEGORY_IMAGE", joinColumns = @JoinColumn(name = "CATEGORY_ID"))
    @MapKey(columns = { @Column(name = "NAME", length = 5, nullable = false) })
    @Column(name = "URL")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @Deprecated
    protected Map<String, String> categoryImages = new HashMap<String, String>();

    @ManyToMany(targetEntity = MediaImpl.class)
    @JoinTable(name = "BLC_CATEGORY_MEDIA_MAP", inverseJoinColumns = @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID"))
    @MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected Map<String, Media> categoryMedia = new HashMap<String , Media>();

    @OneToMany(mappedBy = "category", targetEntity = FeaturedProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})   
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<FeaturedProduct> featuredProducts = new ArrayList<FeaturedProduct>();

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#getDefaultParentCategory()
     */
    public Category getDefaultParentCategory() {
        return defaultParentCategory;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setDefaultParentCategory
     * (org.broadleafcommerce.core.catalog.domain.Category)
     */
    public void setDefaultParentCategory(final Category defaultParentCategory) {
        this.defaultParentCategory = defaultParentCategory;
    }

    /**
     * Gets the child categories.
     * 
     * @return the child categories
     */
    public List<Category> getAllChildCategories(){
    	return allChildCategories;
    }

    /**
     * Checks for child categories.
     * 
     * @return true, if successful
     */
    public boolean hasAllChildCategories(){
    	return !allChildCategories.isEmpty();
    }

    /**
     * Sets the all child categories.
     * 
     * @param allChildCategories the new all child categories
     */
    public void setAllChildCategories(final List<Category> childCategories){
    	this.allChildCategories.clear();
    	for(Category category : childCategories){
    		this.allChildCategories.add(category);
    	}    	
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getChildCategories()
     */
    public List<Category> getChildCategories() {
    	if (childCategories.isEmpty()) {
            for (Category category : allChildCategories) {
                if (category.isActive()) {
                    childCategories.add(category);
                }
            }
        }
        return childCategories;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#hasChildCategories()
     */
    public boolean hasChildCategories() {
        return getChildCategories().size() > 0;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setAllChildCategories(java
     * .util.List)
     */
    public void setChildCategories(final List<Category> childCategories) {
        this.childCategories.clear();
    	for(Category category : childCategories){
    		this.childCategories.add(category);
    	}
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getCategoryImages()
     */
    @Deprecated
    public Map<String, String> getCategoryImages() {
        return categoryImages;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#getCategoryImage(java.lang
     * .String)
     */
    @Deprecated
    public String getCategoryImage(final String imageKey) {
        return categoryImages.get(imageKey);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setCategoryImages(java.
     * util.Map)
     */
    @Deprecated
    public void setCategoryImages(final Map<String, String> categoryImages) {
    	this.categoryImages.clear();
//    	for(String key : categoryImages.keySet()){
//    		this.categoryImages.put(key, categoryImages.get(key));
//    	}
    	for(Map.Entry<String, String> me : categoryImages.entrySet()) {
    		this.categoryImages.put(me.getKey(), me.getValue());
    	}
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#getChildCategoryURLMap()
     */
    @SuppressWarnings("unchecked")
	public Map<String, List<Category>> getChildCategoryURLMap() {
    	HydratedCacheManagerImpl manager = HydratedCacheManagerImpl.getInstance();
    	Object hydratedItem = ((HydratedCacheManager) manager).getHydratedCacheElementItem("blStandardElements", CategoryImpl.class.getName(), getId(), "childCategoryURLMap");
    	if (hydratedItem != null) {
    		return (Map<String, List<Category>>) hydratedItem;
    	}
    	childCategoryURLMap = createChildCategoryURLMap();
    	((HydratedCacheManager) manager).addHydratedCacheElementItem("blStandardElements", CategoryImpl.class.getName(), getId(), "childCategoryURLMap", childCategoryURLMap);
        return childCategoryURLMap;
    }
    
    public void setChildCategoryURLMap(final Map<String, List<Category>> cachedChildCategoryUrlMap) {
    	this.childCategoryURLMap = cachedChildCategoryUrlMap;
    }
    
    public Map<String, List<Category>> createChildCategoryURLMap() {
    	try {
    	childCategoryURLMap = new HashMap<String, List<Category>>();
        final Map<String, List<Category>> newMap = new HashMap<String, List<Category>>();
        fillInURLMapForCategory(newMap, this, "", new ArrayList<Category>());
        childCategoryURLMap = newMap;
        return childCategoryURLMap;
		} catch (CacheFactoryException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Fill in url map for category.
     * @param categoryUrlMap the category url map
     * @param category the category
     * @param startingPath the starting path
     * @param startingCategoryList the starting category list
     */
    private void fillInURLMapForCategory(final Map<String, List<Category>> categoryUrlMap, final Category category, final String startingPath, final List<Category> startingCategoryList) throws CacheFactoryException {
        final String urlKey = category.getUrlKey();
        if (urlKey == null) {
        	throw new CacheFactoryException("Cannot create childCategoryURLMap - the urlKey for a category("+category.getId()+") was null");
        }
    	final String currentPath = startingPath + "/" + category.getUrlKey();
        final List<Category> newCategoryList = new ArrayList<Category>(startingCategoryList);
        newCategoryList.add(category);

        /*
         * TODO create a simplified, non-persistent version of category to place in this map instead
         * of the actual persistent entity, since we do not intend to fully eager populate every
         * lazy collection in the category hierarchy of elements.
         */
        //populate some lazy items for our cached map
		category.getCategoryImages().size();
		category.getCategoryMedia().size();
		category.getAllParentCategories().size();
		category.getAllChildCategories().size();
		category.getFeaturedProducts().size();

        categoryUrlMap.put(currentPath, newCategoryList);
        for (Category currentCategory : category.getChildCategories()) {
            fillInURLMapForCategory(categoryUrlMap, currentCategory, currentPath, newCategoryList);
        }
    }

    public List<Category> getAllParentCategories() {
        return allParentCategories;
    }

    public void setAllParentCategories(final List<Category> allParentCategories) {
    	this.allParentCategories.clear();
    	for(Category category : allParentCategories){
    		this.allParentCategories.add(category);
    	}
    }

    public List<FeaturedProduct> getFeaturedProducts() {
        return featuredProducts;
    }

    public void setFeaturedProducts(final List<FeaturedProduct> featuredProducts) {
    	this.featuredProducts.clear();
    	for(FeaturedProduct featuredProduct : featuredProducts){
    		this.featuredProducts.add(featuredProduct);
    	}
    }

    public List<Product> getAllProducts() {
		return allProducts;
	}

	public void setAllProducts(List<Product> allProducts) {
		this.allProducts.clear();
    	for(Product product : allProducts){
    		this.allProducts.add(product);
    	}
	}

	/*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getCategoryMedia()
     */
    public Map<String, Media> getCategoryMedia() {
        return categoryMedia;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#setCategoryMedia(java.util.Map)
     */
    public void setCategoryMedia(final Map<String, Media> categoryMedia) {
    	this.categoryMedia.clear();
    	for(Map.Entry<String, Media> me : categoryMedia.entrySet()) {
    		this.categoryMedia.put(me.getKey(), me.getValue());
    	}
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CategoryImpl other = (CategoryImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
