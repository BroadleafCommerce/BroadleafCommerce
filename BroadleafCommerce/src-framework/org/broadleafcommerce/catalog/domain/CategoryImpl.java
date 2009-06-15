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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.broadleafcommerce.util.DateUtil;
import org.broadleafcommerce.util.UrlUtil;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.OrderBy;

/**
 * The Class CategoryImpl is the default implementation of {@link Category}. A
 * category is a group of products. <br>
 * <br>
 * If you want to add fields specific to your implementation of
 * BroadLeafCommerce you should extend this class and add your fields. If you
 * need to make significant changes to the CategoryImpl then you should implment
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
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CategoryImpl implements Category {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    private transient final Logger log = Logger.getLogger(getClass());

    /** The id. */
    @Id
    @GeneratedValue(generator = "CategoryId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CategoryId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CategoryImpl", allocationSize = 50)
    @Column(name = "CATEGORY_ID")
    protected Long id;

    /** The name. */
    @Column(name = "NAME", nullable=false)
    protected String name;

    /** The url. */
    @Column(name = "URL")
    protected String url;

    /** The url key. */
    @Column(name = "URL_KEY")
    protected String urlKey;

    /** The default parent category. */
    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_PARENT_CATEGORY_ID")
    protected Category defaultParentCategory;

    /** The description. */
    @Column(name = "DESCRIPTION")
    protected String description;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE")
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    protected Date activeEndDate;

    /** The display template. */
    @Column(name = "DISPLAY_TEMPLATE")
    protected String displayTemplate;

    /** The all child categories. */
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = CategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "SUB_CATEGORY_ID", referencedColumnName = "CATEGORY_ID"))
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @BatchSize(size = 50)
    protected List<Category> allChildCategories = new ArrayList<Category>();

    /** The all parent categories. */
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = CategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_XREF", joinColumns = @JoinColumn(name = "SUB_CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = true))
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @BatchSize(size = 50)
    protected List<Category> allParentCategories = new ArrayList<Category>();

    /** The category images. */
    @CollectionOfElements
    @JoinTable(name = "BLC_CATEGORY_IMAGE", joinColumns = @JoinColumn(name = "CATEGORY_ID"))
    @MapKey(columns = { @Column(name = "NAME", length = 5) })
    @Column(name = "URL")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    protected Map<String, String> categoryImages = new HashMap<String, String>();

    /** The long description. */
    @Column(name = "LONG_DESCRIPTION")
    protected String longDescription;

    //TODO remove the transient annotations once all SQL files have been updated.
    @OneToMany(mappedBy = "category", targetEntity = FeaturedProductImpl.class, cascade = {CascadeType.ALL})
    @Transient
    protected List<FeaturedProduct> featuredProducts;

    /** The child categories. */
    @Transient
    protected List<Category> childCategories = new ArrayList<Category>();

    /** The cached child category url map. */
    @Transient
    protected Map<String, List<Category>> cachedChildCategoryUrlMap = new HashMap<String, List<Category>>();

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getId()
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#getDefaultParentCategory()
     */
    public Category getDefaultParentCategory() {
        return defaultParentCategory;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setDefaultParentCategory
     * (org.broadleafcommerce.catalog.domain.Category)
     */
    public void setDefaultParentCategory(Category defaultParentCategory) {
        this.defaultParentCategory = defaultParentCategory;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getUrl()
     */
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setUrl(java.lang.String)
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getUrlKey()
     */
    public String getUrlKey() {
        if (GenericValidator.isBlankOrNull(urlKey) && getName() != null) {
            return UrlUtil.generateUrlKey(getName());
        }
        return urlKey;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getGeneratedUrl()
     */
    public String getGeneratedUrl() {
        return buildLink(null, this, false);
    }

    /**
     * Builds the link.
     * @param link the link
     * @param category the category
     * @param ignoreTopLevel the ignore top level
     * @return the string
     */
    private String buildLink(String link, Category category, boolean ignoreTopLevel) {
        if (category == null || (ignoreTopLevel && category.getDefaultParentCategory() == null)) {
            return link;
        } else {
            if (link == null) {
                link = category.getUrlKey();
            } else {
                link = category.getUrlKey() + "/" + link;
            }
        }
        return buildLink(link, category.getDefaultParentCategory(), ignoreTopLevel);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setUrlKey(java.lang.String)
     */
    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setDescription(java.lang
     * .String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getActiveStartDate()
     */
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setActiveStartDate(java
     * .util.Date)
     */
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getActiveEndDate()
     */
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setActiveEndDate(java.util
     * .Date)
     */
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#isActive()
     */
    public boolean isActive() {
        if (log.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false)) {
                log.debug("category, " + id + ", inactive due to date");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getDisplayTemplate()
     */
    public String getDisplayTemplate() {
        return displayTemplate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setDisplayTemplate(java
     * .lang.String)
     */
    public void setDisplayTemplate(String displayTemplate) {
        this.displayTemplate = displayTemplate;
    }

    /**
     * Gets the all child categories.
     * @return the all child categories
     */
    private List<Category> getAllChildCategories() {
        return allChildCategories;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getChildCategories()
     */
    public List<Category> getChildCategories() {
        if (childCategories.size() == 0) {
            List<Category> allChildCategories = getAllChildCategories();
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
     * @see org.broadleafcommerce.catalog.domain.Category#hasChildCategories()
     */
    public boolean hasChildCategories() {
        return getChildCategories().size() > 0;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setAllChildCategories(java
     * .util.List)
     */
    public void setAllChildCategories(List<Category> allChildCategories) {
        this.allChildCategories = allChildCategories;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getCategoryImages()
     */
    public Map<String, String> getCategoryImages() {
        return categoryImages;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#getCategoryImage(java.lang
     * .String)
     */
    public String getCategoryImage(String imageKey) {
        return categoryImages.get(imageKey);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setCategoryImages(java.
     * util.Map)
     */
    public void setCategoryImages(Map<String, String> categoryImages) {
        this.categoryImages = categoryImages;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.Category#getLongDescription()
     */
    public String getLongDescription() {
        return longDescription;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#setLongDescription(java
     * .lang.String)
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.catalog.domain.Category#getChildCategoryURLMap()
     */
    public Map<String, List<Category>> getChildCategoryURLMap() {
        // TODO: Add expiration logic to the Map
        if (cachedChildCategoryUrlMap.isEmpty()) {
            synchronized (cachedChildCategoryUrlMap) {
                if (cachedChildCategoryUrlMap.isEmpty()) {
                    Map<String, List<Category>> newMap = new HashMap<String, List<Category>>();
                    fillInURLMapForCategory(newMap, this, "", new ArrayList<Category>());
                    cachedChildCategoryUrlMap = newMap;
                }
            }
        }
        return cachedChildCategoryUrlMap;
    }

    /**
     * Fill in url map for category.
     * @param categoryUrlMap the category url map
     * @param category the category
     * @param startingPath the starting path
     * @param startingCategoryList the starting category list
     */
    private void fillInURLMapForCategory(Map<String, List<Category>> categoryUrlMap, Category category, String startingPath, List<Category> startingCategoryList) {
        String currentPath = startingPath + "/" + category.getUrlKey();
        List<Category> newCategoryList = new ArrayList<Category>(startingCategoryList);
        newCategoryList.add(category);
        categoryUrlMap.put(currentPath, newCategoryList);
        for (Category currentCategory : category.getChildCategories()) {
            fillInURLMapForCategory(categoryUrlMap, currentCategory, currentPath, newCategoryList);
        }
    }

    public List<Category> getAllParentCategories() {
        return allParentCategories;
    }

    public void setAllParentCategories(List<Category> allParentCategories) {
        this.allParentCategories = allParentCategories;
    }

    public List<FeaturedProduct> getFeaturedProducts() {
        return featuredProducts;
    }

    public void setFeaturedProducts(List<FeaturedProduct> featuredProducts) {
        this.featuredProducts = featuredProducts;
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoryImpl other = (CategoryImpl) obj;

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
