package org.broadleafcommerce.core.catalog.domain.sandbox;

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

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.core.catalog.domain.FeaturedProductImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_SNDBX")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SandBoxCategoryImpl extends CategoryMappedSuperclass implements SandBoxCategory {

	private static final long serialVersionUID = 1L;
    
    @Column(name = "VERSION", nullable=false)
    @Index(name="CAT_SNDBX_VER_INDX", columnNames={"VERSION"})
    protected String version;
	
    /** The default parent category. */
    @ManyToOne(targetEntity = SandBoxCategoryImpl.class)
    @JoinColumn(name = "DEFAULT_PARENT_CATEGORY_ID")
    @Index(name="CAT_PARENT_SNDBX_INDEX", columnNames={"DEFAULT_PARENT_CATEGORY_ID"})
    @AdminPresentation(friendlyName="Category Default Parent", order=7, group="Description")
    protected Category defaultParentCategory;

    /** The all child categories. */
    @ManyToMany(targetEntity = SandBoxCategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_SNDBX_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "SUB_CATEGORY_ID", referencedColumnName = "CATEGORY_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Category> allChildCategories = new ArrayList<Category>();

    /** The all parent categories. */	
    @ManyToMany(targetEntity = SandBoxCategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_SNDBX_XREF", joinColumns = @JoinColumn(name = "SUB_CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Category> allParentCategories = new ArrayList<Category>();

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
    
    /**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
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
        final SandBoxCategoryImpl other = (SandBoxCategoryImpl) obj;

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

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#getCategoryImages()
	 */
	@Override
	public Map<String, String> getCategoryImages() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#getCategoryImage(java.lang.String)
	 */
	@Override
	public String getCategoryImage(String imageKey) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#setCategoryImages(java.util.Map)
	 */
	@Override
	public void setCategoryImages(Map<String, String> categoryImages) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#getCategoryMedia()
	 */
	@Override
	public Map<String, Media> getCategoryMedia() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#setCategoryMedia(java.util.Map)
	 */
	@Override
	public void setCategoryMedia(Map<String, Media> categoryMedia) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#getFeaturedProducts()
	 */
	@Override
	public List<FeaturedProduct> getFeaturedProducts() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#setFeaturedProducts(java.util.List)
	 */
	@Override
	public void setFeaturedProducts(List<FeaturedProduct> featuredProducts) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#getAllProducts()
	 */
	@Override
	public List<Product> getAllProducts() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.Category#setAllProducts(java.util.List)
	 */
	@Override
	public void setAllProducts(List<Product> allProducts) {
		// TODO Auto-generated method stub
		
	}
    
}
