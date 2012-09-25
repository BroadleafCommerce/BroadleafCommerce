/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.catalog.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.cache.Hydrated;
import org.broadleafcommerce.common.cache.HydratedSetup;
import org.broadleafcommerce.common.cache.engine.CacheFactoryException;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.util.UrlUtil;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.media.domain.MediaImpl;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.CategorySearchFacetImpl;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bTaylor
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_CATEGORY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "CategoryImpl_baseCategory")
@SQLDelete(sql="UPDATE BLC_CATEGORY SET ARCHIVED = 'Y' WHERE CATEGORY_ID = ?")
public class CategoryImpl implements Category, Status {

    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(CategoryImpl.class);

    private static String buildLink(Category category, boolean ignoreTopLevel) {
        Category myCategory = category;
        StringBuilder linkBuffer = new StringBuilder(50);
        while (myCategory != null) {
            if (!ignoreTopLevel || myCategory.getDefaultParentCategory() != null) {
                if (linkBuffer.length() == 0) {
                    linkBuffer.append(myCategory.getUrlKey());
                } else if(myCategory.getUrlKey() != null && !"/".equals(myCategory.getUrlKey())){
                    linkBuffer.insert(0, myCategory.getUrlKey() + '/');
                }
            }
            myCategory = myCategory.getDefaultParentCategory();
        }

        return linkBuffer.toString();
    }

    private static void fillInURLMapForCategory(Map<String, List<Long>> categoryUrlMap, Category category, String startingPath, List<Long> startingCategoryList) throws CacheFactoryException {
        String urlKey = category.getUrlKey();
        if (urlKey == null) {
        	throw new CacheFactoryException("Cannot create childCategoryURLMap - the urlKey for a category("+category.getId()+") was null");
        }

        String currentPath = "";
        if (! "/".equals(category.getUrlKey())) {
            currentPath = startingPath + "/" + category.getUrlKey();
        }

        List<Long> newCategoryList = new ArrayList<Long>(startingCategoryList);
        newCategoryList.add(category.getId());

        categoryUrlMap.put(currentPath, newCategoryList);
        for (Category currentCategory : category.getChildCategories()) {
            fillInURLMapForCategory(categoryUrlMap, currentCategory, currentPath, newCategoryList);
        }
    }

    @Id
    @GeneratedValue(generator= "CategoryId")
    @GenericGenerator(
        name="CategoryId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="CategoryImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.CategoryImpl")
        }
    )
    @Column(name = "CATEGORY_ID")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_ID", group = "CategoryImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME", nullable=false)
    @Index(name="CATEGORY_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Name", order=1, group = "CategoryImpl_Description", prominent=true)
    protected String name;

    @Column(name = "URL")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Url", order=2, group = "CategoryImpl_Description")
    protected String url;

    @Column(name = "URL_KEY")
    @Index(name="CATEGORY_URLKEY_INDEX", columnNames={"URL_KEY"})
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Url_Key", order=3, group = "CategoryImpl_Description")
    protected String urlKey;

    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Description", order=5, group = "CategoryImpl_Description", largeEntry=true)
    protected String description;

    @Column(name = "ACTIVE_START_DATE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Active_Start_Date", order=7, group = "CategoryImpl_Active_Date_Range")
    protected Date activeStartDate;

    @Column(name = "ACTIVE_END_DATE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Active_End_Date", order=8, group = "CategoryImpl_Active_Date_Range")
    protected Date activeEndDate;

    @Column(name = "DISPLAY_TEMPLATE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Display_Template", order=4, group = "CategoryImpl_Description")
    protected String displayTemplate;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "LONG_DESCRIPTION", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Long_Description", order=6, group = "CategoryImpl_Description", largeEntry=true,fieldType=SupportedFieldType.HTML_BASIC)
    protected String longDescription;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_PARENT_CATEGORY_ID")
    @Index(name="CATEGORY_PARENT_INDEX", columnNames={"DEFAULT_PARENT_CATEGORY_ID"})
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Default_Parent", order=7, group = "CategoryImpl_Description", excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Category defaultParentCategory;

    @ManyToMany(targetEntity = CategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "SUB_CATEGORY_ID", referencedColumnName = "CATEGORY_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Category> allChildCategories = new ArrayList<Category>(10);

    @ManyToMany(targetEntity = CategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_XREF", joinColumns = @JoinColumn(name = "SUB_CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Category> allParentCategories = new ArrayList<Category>(10);

    @ManyToMany(targetEntity = ProductImpl.class)
    @JoinTable(name = "BLC_CATEGORY_PRODUCT_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", nullable = true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(clause = "DISPLAY_ORDER")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Product> allProducts = new ArrayList<Product>(10);

    @CollectionOfElements
    @JoinTable(name = "BLC_CATEGORY_IMAGE", joinColumns = @JoinColumn(name = "CATEGORY_ID"))
    @MapKey(columns = { @Column(name = "NAME", length = 5, nullable = false) })
    @Column(name = "URL")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @Deprecated
    protected Map<String, String> categoryImages = new HashMap<String, String>(10);

    @ManyToMany(targetEntity = MediaImpl.class)
    @JoinTable(name = "BLC_CATEGORY_MEDIA_MAP", inverseJoinColumns = @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID"))
    @MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected Map<String, Media> categoryMedia = new HashMap<String , Media>(10);

    @OneToMany(mappedBy = "category", targetEntity = FeaturedProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})   
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<FeaturedProduct> featuredProducts = new ArrayList<FeaturedProduct>(10);
    
	@OneToMany(mappedBy = "category", targetEntity = CrossSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected List<RelatedProduct> crossSaleProducts = new ArrayList<RelatedProduct>();

    @OneToMany(mappedBy = "category", targetEntity = UpSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected List<RelatedProduct> upSaleProducts  = new ArrayList<RelatedProduct>();
    
    @OneToMany(mappedBy = "category", targetEntity = CategorySearchFacetImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected List<CategorySearchFacet> searchFacets  = new ArrayList<CategorySearchFacet>();
    
    @ManyToMany(targetEntity = SearchFacetImpl.class)
    @JoinTable(name = "BLC_CAT_SEARCH_FACET_EXCL_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "SEARCH_FACET_ID", nullable = true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<SearchFacet> excludedSearchFacets = new ArrayList<SearchFacet>(10);
    
    @OneToMany(mappedBy = "category", targetEntity = CategoryAttributeImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(addType = AddMethodType.PERSIST, friendlyName = "categoryAttributesTitle", dataSourceName = "categoryAttributeDS")
    protected List<CategoryAttribute> categoryAttributes  = new ArrayList<CategoryAttribute>();

    @Column(name = "INVENTORY_TYPE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_InventoryType", group = "CategoryImpl_Sku_Inventory", order=10, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.inventory.service.type.InventoryType")
    protected String inventoryType;
    
    @Column(name = "FULFILLMENT_TYPE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_FulfillmentType", group = "CategoryImpl_Sku_Inventory", order=11, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentType")
    protected String fulfillmentType;

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

    @Transient
    @Hydrated(factoryMethod = "createChildCategoryURLMap")
    protected Map<String, List<Long>> childCategoryURLMap;        

    @Transient
    protected List<Category> childCategories = new ArrayList<Category>(50);

    @Transient
    protected List<FeaturedProduct> filteredFeaturedProducts = null;
    
    @Transient
    protected List<RelatedProduct> filteredCrossSales = null;

    @Transient
    protected List<RelatedProduct> filteredUpSales = null;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUrl() {
        // TODO: if null return
        // if blank return
        // if startswith "/" return
        // if contains a ":" and no "?" or (contains a ":" before a "?") return
        // else "add a /" at the beginning
        if(url == null || url.equals("") || url.startsWith("/")) {
            return url;       
        } else if ((url.contains(":") && !url.contains("?")) || url.indexOf('?', url.indexOf(':')) != -1) {
            return url;
        } else {
            return "/" + url;
        }
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrlKey() {
        if ((urlKey == null || "".equals(urlKey.trim())) && name != null) {
            return UrlUtil.generateUrlKey(name);
        }
        return urlKey;
    }

    @Override
    public String getGeneratedUrl() {
        return buildLink(this, false);
    }

    @Override
    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getActiveStartDate() {
        if ('Y'==getArchived()) {
            return null;
        }
        return activeStartDate;
    }

    @Override
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = new Date(activeStartDate.getTime());
    }

    @Override
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = new Date(activeEndDate.getTime());
    }

    @Override
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(activeStartDate, activeEndDate, true)) {
                LOG.debug("category, " + id + ", inactive due to date");
            }
            if ('Y'==getArchived()) {
                LOG.debug("category, " + id + ", inactive due to archived status");
            }
        }
        return DateUtil.isActive(activeStartDate, activeEndDate, true) && 'Y'!=getArchived();
    }

    @Override
    public String getDisplayTemplate() {
        return displayTemplate;
    }

    @Override
    public void setDisplayTemplate(String displayTemplate) {
        this.displayTemplate = displayTemplate;
    }

    @Override
    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public Category getDefaultParentCategory() {
        return defaultParentCategory;
    }

    @Override
    public void setDefaultParentCategory(Category defaultParentCategory) {
        this.defaultParentCategory = defaultParentCategory;
    }

    @Override
    public List<Category> getAllChildCategories(){
    	return allChildCategories;
    }

    @Override
    public boolean hasAllChildCategories(){
    	return !allChildCategories.isEmpty();
    }

    @Override
    public void setAllChildCategories(List<Category> childCategories){
    	allChildCategories.clear();
    	for(Category category : childCategories){
    		allChildCategories.add(category);
    	}    	
    }

    @Override
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

    @Override
    public boolean hasChildCategories() {
        return !getChildCategories().isEmpty();
    }

    @Override
    public void setChildCategories(List<Category> childCategories) {
        this.childCategories.clear();
    	for(Category category : childCategories){
    		this.childCategories.add(category);
    	}
    }

    @Override
    @Deprecated
    public Map<String, String> getCategoryImages() {
        return categoryImages;
    }

    @Override
    @Deprecated
    public String getCategoryImage(String imageKey) {
        return categoryImages.get(imageKey);
    }

    @Override
    @Deprecated
    public void setCategoryImages(Map<String, String> categoryImages) {
    	this.categoryImages.clear();
    	for(Map.Entry<String, String> me : categoryImages.entrySet()) {
    		this.categoryImages.put(me.getKey(), me.getValue());
    	}
    }

    @Override
	public Map<String, List<Long>> getChildCategoryURLMap() {
        if (childCategoryURLMap == null) {
            HydratedSetup.populateFromCache(this);
        }
        return childCategoryURLMap;
    }

    public Map<String, List<Long>> createChildCategoryURLMap() {
    	try {
            Map<String, List<Long>> newMap = new HashMap<String, List<Long>>(50);
            fillInURLMapForCategory(newMap, this, "", new ArrayList<Long>(10));
            return newMap;
		} catch (CacheFactoryException e) {
			throw new RuntimeException(e);
		}
    }

    @Override
    public void setChildCategoryURLMap(Map<String, List<Long>> childCategoryURLMap) {
        this.childCategoryURLMap = childCategoryURLMap;
    }
    
    @Override
    public List<Category> buildFullCategoryHierarchy(List<Category> currentHierarchy) {
        if (currentHierarchy == null) { 
            currentHierarchy = new ArrayList<Category>();
            currentHierarchy.add(this);
        }
        
        List<Category> myParentCategories = new ArrayList<Category>();
        if (defaultParentCategory != null) {
            myParentCategories.add(defaultParentCategory);
        }
        if (allParentCategories != null && allParentCategories.size() > 0) {
            myParentCategories.addAll(allParentCategories);
        }
        
        for (Category category : myParentCategories) {
            if (!currentHierarchy.contains(category)) {
                currentHierarchy.add(category);
                category.buildFullCategoryHierarchy(currentHierarchy);
            }
        }
        
        return currentHierarchy;
    }
    
    @Override
    public List<Category> buildCategoryHierarchy(List<Category> currentHierarchy) {
    	if (currentHierarchy == null) {
    		currentHierarchy = new ArrayList<Category>();
    		currentHierarchy.add(this);
    	}
    	if (defaultParentCategory != null && ! currentHierarchy.contains(defaultParentCategory)) {
    		currentHierarchy.add(defaultParentCategory);
    		defaultParentCategory.buildCategoryHierarchy(currentHierarchy);
    	}
    	return currentHierarchy;
    }

    @Override
    public List<Category> getAllParentCategories() {
        return allParentCategories;
    }

    @Override
    public void setAllParentCategories(List<Category> allParentCategories) {
    	this.allParentCategories.clear();
    	for(Category category : allParentCategories){
    		this.allParentCategories.add(category);
    	}
    }

    @Override
    public List<FeaturedProduct> getFeaturedProducts() {
        if (filteredFeaturedProducts == null && featuredProducts != null) {
            filteredFeaturedProducts = new ArrayList<FeaturedProduct>(featuredProducts.size());
            filteredFeaturedProducts.addAll(featuredProducts);
            CollectionUtils.filter(featuredProducts, new Predicate() {
                @Override
                public boolean evaluate(Object arg) {
                    return 'Y' != ((Status) ((FeaturedProduct) arg).getProduct()).getArchived();
                }
            });
        }
        return filteredFeaturedProducts;
    }

    @Override
    public void setFeaturedProducts(List<FeaturedProduct> featuredProducts) {
    	this.featuredProducts.clear();
    	for(FeaturedProduct featuredProduct : featuredProducts){
    		this.featuredProducts.add(featuredProduct);
    	}
    }
    
    @Override
    public List<RelatedProduct> getCrossSaleProducts() {
        if (filteredCrossSales == null && crossSaleProducts != null) {
            filteredCrossSales = new ArrayList<RelatedProduct>(crossSaleProducts.size());
            filteredCrossSales.addAll(crossSaleProducts);
            CollectionUtils.filter(crossSaleProducts, new Predicate() {
                @Override
                public boolean evaluate(Object arg) {
                    return 'Y'!=((Status)((CrossSaleProductImpl) arg).getRelatedProduct()).getArchived();
                }
            });
        }
        return filteredCrossSales;
    }

    @Override
    public void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts) {
        this.crossSaleProducts.clear();
        for(RelatedProduct relatedProduct : crossSaleProducts){
        	this.crossSaleProducts.add(relatedProduct);
        }    	
    }

    @Override
    public List<RelatedProduct> getUpSaleProducts() {
        if (filteredUpSales == null && upSaleProducts != null) {
            filteredUpSales = new ArrayList<RelatedProduct>(upSaleProducts.size());
            filteredUpSales.addAll(upSaleProducts);
            CollectionUtils.filter(upSaleProducts, new Predicate() {
                @Override
                public boolean evaluate(Object arg) {
                    return 'Y'!=((Status)((UpSaleProductImpl) arg).getRelatedProduct()).getArchived();
                }
            });
        }
        return filteredUpSales;
    }
    
    @Override
    public List<RelatedProduct> getCumulativeCrossSaleProducts() {
    	Set<RelatedProduct> returnProductsSet = new LinkedHashSet<RelatedProduct>();
    	    	
    	List<Category> categoryHierarchy = buildCategoryHierarchy(null);
    	for (Category category : categoryHierarchy) {
    		returnProductsSet.addAll(category.getCrossSaleProducts());
    	}
    	return new ArrayList<RelatedProduct>(returnProductsSet);
    }
    
    @Override
    public List<RelatedProduct> getCumulativeUpSaleProducts() {
    	Set<RelatedProduct> returnProductsSet = new LinkedHashSet<RelatedProduct>();
    	
    	List<Category> categoryHierarchy = buildCategoryHierarchy(null);
    	for (Category category : categoryHierarchy) {
    		returnProductsSet.addAll(category.getUpSaleProducts());
    	}
    	return new ArrayList<RelatedProduct>(returnProductsSet);
    }

    @Override
    public List<FeaturedProduct> getCumulativeFeaturedProducts() {
    	Set<FeaturedProduct> returnProductsSet = new LinkedHashSet<FeaturedProduct>();
    	
    	List<Category> categoryHierarchy = buildCategoryHierarchy(null);
    	for (Category category : categoryHierarchy) {
    		returnProductsSet.addAll(category.getFeaturedProducts());
    	}
    	return new ArrayList<FeaturedProduct>(returnProductsSet);
    }
    
    @Override
    public void setUpSaleProducts(List<RelatedProduct> upSaleProducts) {
        this.upSaleProducts.clear();
        for(RelatedProduct relatedProduct : upSaleProducts){
        	this.upSaleProducts.add(relatedProduct);
        }
        this.upSaleProducts = upSaleProducts;
    }

    @Override
    public List<Product> getActiveProducts() {
        List<Product> result = new ArrayList<Product>();
        for (Product product : allProducts) {
            if (product.isActive()) {
                result.add(product);
            }
        }
        return result;
    }
    
	@Override
    public List<Product> getAllProducts() {
		return allProducts;
	}

    @Override
	public void setAllProducts(List<Product> allProducts) {
		this.allProducts.clear();
    	for(Product product : allProducts){
    		this.allProducts.add(product);
    	}
	}
    
    @Override
    public List<CategorySearchFacet> getSearchFacets() {
		return searchFacets;
	}

    @Override
	public void setSearchFacets(List<CategorySearchFacet> searchFacets) {
		this.searchFacets = searchFacets;
	}

	@Override
	public List<SearchFacet> getExcludedSearchFacets() {
		return excludedSearchFacets;
	}

    @Override
	public void setExcludedSearchFacets(List<SearchFacet> excludedSearchFacets) {
		this.excludedSearchFacets = excludedSearchFacets;
	}

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.getInstance(this.inventoryType);
    }

    @Override
    public void setInventoryType(InventoryType inventoryType) {
        this.inventoryType = inventoryType.getType();
    }
    
    @Override
    public FulfillmentType getFulfillmentType() {
    	return FulfillmentType.getInstance(this.fulfillmentType);
    }
    
    @Override
    public void setFulfillmentType(FulfillmentType fulfillmentType) {
    	this.fulfillmentType = fulfillmentType.getType();
    }
    
    @Override
    public List<CategorySearchFacet> getCumulativeSearchFacets() {
    	final List<CategorySearchFacet> returnFacets = new ArrayList<CategorySearchFacet>();
    	returnFacets.addAll(getSearchFacets());
    	Collections.sort(returnFacets, facetPositionComparator);
    	
    	// Add in parent facets unless they are excluded
    	List<CategorySearchFacet> parentFacets = null;
    	if (defaultParentCategory != null) {
    		parentFacets = defaultParentCategory.getCumulativeSearchFacets();   
        	CollectionUtils.filter(parentFacets, new Predicate() {
    			@Override
    			public boolean evaluate(Object arg) {
    				CategorySearchFacet csf = (CategorySearchFacet) arg;
    				return !getExcludedSearchFacets().contains(csf.getSearchFacet()) && !returnFacets.contains(csf.getSearchFacet());
    			}
        	});
    	}
    	if (parentFacets != null) {
    		returnFacets.addAll(parentFacets);
    	}
    	
    	return returnFacets;
    }

	@Override
    public Map<String, Media> getCategoryMedia() {
        return categoryMedia;
    }

    @Override
    public void setCategoryMedia(Map<String, Media> categoryMedia) {
    	this.categoryMedia.clear();
    	for(Map.Entry<String, Media> me : categoryMedia.entrySet()) {
    		this.categoryMedia.put(me.getKey(), me.getValue());
    	}
    }
    
    @Override
    public List<CategoryAttribute> getCategoryAttributes() {
        return categoryAttributes;
    }

    @Override
    public void setCategoryAttributes(List<CategoryAttribute> categoryAttributes) {
        this.categoryAttributes = categoryAttributes;
    }
    
    @Override
    public CategoryAttribute getCategoryAttributeByName(String name) {
        for (CategoryAttribute attribute : getCategoryAttributes()) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }

    @Override
    public Map<String, CategoryAttribute> getMappedCategoryAttributes() {
        Map<String, CategoryAttribute> map = new HashMap<String, CategoryAttribute>();
        for (CategoryAttribute attr : getCategoryAttributes()) {
            map.put(attr.getName(), attr);
        }
        return map;
    }

    @Override
    public Character getArchived() {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        return archiveStatus.getArchived();
    }

    @Override
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        archiveStatus.setArchived(archived);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (url == null ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CategoryImpl other = (CategoryImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }
    
    protected static Comparator<CategorySearchFacet> facetPositionComparator = new Comparator<CategorySearchFacet>() {
		@Override
		public int compare(CategorySearchFacet o1, CategorySearchFacet o2) {
			return o1.getSequence().compareTo(o2.getSequence());
		}
	};

}
