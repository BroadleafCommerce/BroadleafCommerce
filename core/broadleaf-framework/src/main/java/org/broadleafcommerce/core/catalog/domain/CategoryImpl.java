/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.catalog.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.cache.Hydrated;
import org.broadleafcommerce.common.cache.HydratedSetup;
import org.broadleafcommerce.common.cache.engine.CacheFactoryException;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminatable;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminatableType;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.media.domain.MediaImpl;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationDataDrivenEnumeration;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationMapKey;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.OptionFilterParam;
import org.broadleafcommerce.common.presentation.OptionFilterParamType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.util.UrlUtil;
import org.broadleafcommerce.common.web.Locatable;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicyAdornedTargetCollection;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicyMap;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.CategorySearchFacetImpl;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;

/**
 * @author bTaylor
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_CATEGORY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
@AdminPresentationClass(friendlyName = "CategoryImpl_baseCategory")
@SQLDelete(sql="UPDATE BLC_CATEGORY SET ARCHIVED = 'Y' WHERE CATEGORY_ID = ?")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX_CATEGORY_INVOKE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class CategoryImpl implements Category, Status, AdminMainEntity, Locatable {

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
        for (CategoryXref currentCategory : category.getChildCategoryXrefs()) {
            fillInURLMapForCategory(categoryUrlMap, currentCategory.getSubCategory(), currentPath, newCategoryList);
        }
    }

    @Id
    @GeneratedValue(generator= "CategoryId")
    @GenericGenerator(
        name="CategoryId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="CategoryImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.CategoryImpl")
        }
    )
    @Column(name = "CATEGORY_ID")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_ID", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME", nullable=false)
    @Index(name="CATEGORY_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Name", order = 1000,
            group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General,
            prominent = true, gridOrder = 1, columnWidth = "300px",
            translatable = true)
    protected String name;

    @Column(name = "URL")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Url", order = 2000,
            group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General,
            prominent = true, gridOrder = 2, columnWidth = "300px")
    @Index(name="CATEGORY_URL_INDEX", columnNames={"URL"})
    protected String url;

    @Column(name = "URL_KEY")
    @Index(name="CATEGORY_URLKEY_INDEX", columnNames={"URL_KEY"})
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Url_Key",
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
            group = Presentation.Group.Name.Advanced, groupOrder = Presentation.Group.Order.Advanced,
            excluded = true)
    protected String urlKey;

    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Description",
            group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General,
            largeEntry = true,
            excluded = true,
            translatable = true)
    protected String description;

    @Column(name = "TAX_CODE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_TaxCode", order = 4000,
            group = Presentation.Group.Name.Advanced)
    @AdminPresentationDataDrivenEnumeration(optionCanEditValues = true, optionFilterParams = { @OptionFilterParam(
            param = "type.key", value = "TAX_CODE", paramType = OptionFilterParamType.STRING) })
    protected String taxCode;

    @Column(name = "ACTIVE_START_DATE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Active_Start_Date", order = 1000,
            group = Presentation.Group.Name.ActiveDateRange, groupOrder = Presentation.Group.Order.ActiveDateRange)
    protected Date activeStartDate;

    @Column(name = "ACTIVE_END_DATE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Active_End_Date", order = 2000,
            group = Presentation.Group.Name.ActiveDateRange, groupOrder = Presentation.Group.Order.ActiveDateRange)
    protected Date activeEndDate;

    @Column(name = "DISPLAY_TEMPLATE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Display_Template", order = 1000,
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
            group = Presentation.Group.Name.Advanced, groupOrder = Presentation.Group.Order.Advanced)
    protected String displayTemplate;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "LONG_DESCRIPTION", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Long_Description", order = 3000,
            group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General,
            largeEntry = true,
            fieldType = SupportedFieldType.HTML_BASIC,
            translatable = true)
    protected String longDescription;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_PARENT_CATEGORY_ID")
    @Index(name="CATEGORY_PARENT_INDEX", columnNames={"DEFAULT_PARENT_CATEGORY_ID"})
    @AdminPresentation(friendlyName = "CategoryImpl_defaultParentCategory", order = 4000,
            group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General)
    @AdminPresentationToOneLookup()
    protected Category defaultParentCategory;

    @OneToMany(targetEntity = CategoryXrefImpl.class, mappedBy = "category", orphanRemoval = true)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(value="displayOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(
            targetObjectProperty = "subCategory",
            parentObjectProperty = "category",
            friendlyName = "allChildCategoriesTitle",
            sortProperty = "displayOrder",
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
            gridVisibleFields = { "name" })
    @ClonePolicyAdornedTargetCollection(unowned = true)
    @SiteDiscriminatable(type= SiteDiscriminatableType.CATALOG)
    protected List<CategoryXref> allChildCategoryXrefs = new ArrayList<CategoryXref>(10);

    @OneToMany(targetEntity = CategoryXrefImpl.class, mappedBy = "subCategory", orphanRemoval = true)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(value="displayOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(
            targetObjectProperty = "category",
            parentObjectProperty = "subCategory",
            friendlyName = "allParentCategoriesTitle",
            sortProperty = "displayOrder",
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
            gridVisibleFields = { "name" })
    @ClonePolicyAdornedTargetCollection(unowned = true)
    @SiteDiscriminatable(type= SiteDiscriminatableType.CATALOG)
    protected List<CategoryXref> allParentCategoryXrefs = new ArrayList<CategoryXref>(10);

    @OneToMany(targetEntity = CategoryProductXrefImpl.class, mappedBy = "category", orphanRemoval = true)
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(value="displayOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(
            targetObjectProperty = "product",
            parentObjectProperty = "category",
            friendlyName = "allProductsTitle",
            sortProperty = "displayOrder",
            tab = Presentation.Tab.Name.Products, tabOrder = Presentation.Tab.Order.Products,
            gridVisibleFields = { "defaultSku.name" })
    @ClonePolicyAdornedTargetCollection(unowned = true)
    @SiteDiscriminatable(type= SiteDiscriminatableType.CATALOG)
    protected List<CategoryProductXref> allProductXrefs = new ArrayList<CategoryProductXref>(10);

    @ElementCollection
    @MapKeyColumn(name="NAME")
    @Column(name="URL")
    @CollectionTable(name="BLC_CATEGORY_IMAGE", joinColumns=@JoinColumn(name="CATEGORY_ID"))
    @BatchSize(size = 50)
    @Deprecated
    protected Map<String, String> categoryImages = new HashMap<String, String>(10);

    @ManyToMany(targetEntity = MediaImpl.class)
    @JoinTable(name = "BLC_CATEGORY_MEDIA_MAP", inverseJoinColumns = @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID"))
    @MapKeyColumn(name = "MAP_KEY")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @BatchSize(size = 50)
    @AdminPresentationMap(
            friendlyName = "SkuImpl_Sku_Media",
            tab = Presentation.Tab.Name.Media, tabOrder = Presentation.Tab.Order.Media,
            keyPropertyFriendlyName = "SkuImpl_Sku_Media_Key",
            deleteEntityUponRemove = true,
            mediaField = "url",
            keys = {
                    @AdminPresentationMapKey(keyName = "primary", friendlyKeyName = "mediaPrimary"),
                    @AdminPresentationMapKey(keyName = "alt1", friendlyKeyName = "mediaAlternate1"),
                    @AdminPresentationMapKey(keyName = "alt2", friendlyKeyName = "mediaAlternate2"),
                    @AdminPresentationMapKey(keyName = "alt3", friendlyKeyName = "mediaAlternate3"),
                    @AdminPresentationMapKey(keyName = "alt4", friendlyKeyName = "mediaAlternate4"),
                    @AdminPresentationMapKey(keyName = "alt5", friendlyKeyName = "mediaAlternate5"),
                    @AdminPresentationMapKey(keyName = "alt6", friendlyKeyName = "mediaAlternate6")
            }
    )
    @ClonePolicyMap
    protected Map<String, Media> categoryMedia = new HashMap<String , Media>(10);

    @OneToMany(mappedBy = "category", targetEntity = FeaturedProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})   
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @OrderBy(value="sequence")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(friendlyName = "featuredProductsTitle", order = 1000,
            tab = Presentation.Tab.Name.Marketing, tabOrder = Presentation.Tab.Order.Marketing,
            targetObjectProperty = "product",
            sortProperty = "sequence",
            maintainedAdornedTargetFields = { "promotionMessage" },
            gridVisibleFields = { "defaultSku.name", "promotionMessage" })
    @ClonePolicyAdornedTargetCollection
    @SiteDiscriminatable(type= SiteDiscriminatableType.CATALOG)
    protected List<FeaturedProduct> featuredProducts = new ArrayList<FeaturedProduct>(10);
    
    @OneToMany(mappedBy = "category", targetEntity = CrossSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @OrderBy(value="sequence")
    @AdminPresentationAdornedTargetCollection(friendlyName = "crossSaleProductsTitle", order = 2000,
            tab = Presentation.Tab.Name.Marketing, tabOrder = Presentation.Tab.Order.Marketing,
            targetObjectProperty = "relatedSaleProduct",
            sortProperty = "sequence",
            maintainedAdornedTargetFields = { "promotionMessage" },
            gridVisibleFields = { "defaultSku.name", "promotionMessage" })
    @ClonePolicyAdornedTargetCollection
    @SiteDiscriminatable(type= SiteDiscriminatableType.CATALOG)
    protected List<RelatedProduct> crossSaleProducts = new ArrayList<RelatedProduct>();

    @OneToMany(mappedBy = "category", targetEntity = UpSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @OrderBy(value="sequence")
    @AdminPresentationAdornedTargetCollection(friendlyName = "upsaleProductsTitle", order = 3000,
            tab = Presentation.Tab.Name.Marketing, tabOrder = Presentation.Tab.Order.Marketing,
            targetObjectProperty = "relatedSaleProduct",
            sortProperty = "sequence",
            maintainedAdornedTargetFields = { "promotionMessage" },
            gridVisibleFields = { "defaultSku.name", "promotionMessage" })
    @ClonePolicyAdornedTargetCollection
    @SiteDiscriminatable(type= SiteDiscriminatableType.CATALOG)
    protected List<RelatedProduct> upSaleProducts  = new ArrayList<RelatedProduct>();
    
    @OneToMany(mappedBy = "category", targetEntity = CategorySearchFacetImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @OrderBy(value="sequence")
    @AdminPresentationAdornedTargetCollection(friendlyName = "categoryFacetsTitle", order = 1000,
            tab = Presentation.Tab.Name.SearchFacets, tabOrder = Presentation.Tab.Order.SearchFacets,
            targetObjectProperty = "searchFacet",
            sortProperty = "sequence",
            gridVisibleFields = { "field", "label", "searchDisplayPriority" })
    @ClonePolicyAdornedTargetCollection
    protected List<CategorySearchFacet> searchFacets  = new ArrayList<CategorySearchFacet>();
    
    @ManyToMany(targetEntity = SearchFacetImpl.class)
    @JoinTable(name = "BLC_CAT_SEARCH_FACET_EXCL_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "SEARCH_FACET_ID", nullable = true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(
            order = 2000,
            joinEntityClass = "org.broadleafcommerce.core.search.domain.CategoryExcludedSearchFacetImpl",
            targetObjectProperty = "searchFacet",
            parentObjectProperty = "category",
            friendlyName = "excludedFacetsTitle",
            tab = Presentation.Tab.Name.SearchFacets, tabOrder = Presentation.Tab.Order.SearchFacets,
            gridVisibleFields = {"field", "label", "searchDisplayPriority"})
    @ClonePolicyAdornedTargetCollection
    protected List<SearchFacet> excludedSearchFacets = new ArrayList<SearchFacet>(10);
    
    @OneToMany(mappedBy = "category", targetEntity = CategoryAttributeImpl.class, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
    @MapKey(name="name")
    @BatchSize(size = 50)
    @AdminPresentationMap(friendlyName = "categoryAttributesTitle",
        tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
        deleteEntityUponRemove = true, forceFreeFormKeys = true, keyPropertyFriendlyName = "ProductAttributeImpl_Attribute_Name"
    )
    @ClonePolicyMap
    protected Map<String, CategoryAttribute> categoryAttributes = new HashMap<String, CategoryAttribute>();

    @Column(name = "INVENTORY_TYPE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_InventoryType", order = 2000,
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
            group = Presentation.Group.Name.Advanced, groupOrder = Presentation.Group.Order.Advanced,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.inventory.service.type.InventoryType")
    protected String inventoryType;
    
    @Column(name = "FULFILLMENT_TYPE")
    @AdminPresentation(friendlyName = "CategoryImpl_Category_FulfillmentType", order = 3000,
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
            group = Presentation.Group.Name.Advanced, groupOrder = Presentation.Group.Order.Advanced,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.order.service.type.FulfillmentType")
    protected String fulfillmentType;

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

    @Transient
    @Hydrated(factoryMethod = "createChildCategoryURLMap")
    @Deprecated
    protected Map<String, List<Long>> childCategoryURLMap;

    @Transient
    @Hydrated(factoryMethod = "createChildCategoryIds")
    protected List<Long> childCategoryIds;

    @Transient
    protected List<CategoryXref> childCategoryXrefs = new ArrayList<CategoryXref>(50);

    @Transient
    protected List<Category> legacyChildCategories = new ArrayList<Category>(50);

    @Transient
    protected List<Category> allLegacyChildCategories = new ArrayList<Category>(50);

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
        return DynamicTranslationProvider.getValue(this, "name", name);
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
        return DynamicTranslationProvider.getValue(this, "description", description);
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
        this.activeStartDate = (activeStartDate == null) ? null : new Date(activeStartDate.getTime());
    }

    @Override
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = (activeEndDate == null) ? null : new Date(activeEndDate.getTime());
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
        return DynamicTranslationProvider.getValue(this, "longDescription", longDescription);
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
    public List<CategoryXref> getAllChildCategoryXrefs(){
        return getAllChildCategoryXrefsInternal();
    }

    protected List<CategoryXref> getAllChildCategoryXrefsInternal() {
        return allChildCategoryXrefs;
    }

    @Override
    public List<CategoryXref> getChildCategoryXrefs() {
        return getChildCategoryXrefsInternal();
    }

    protected List<CategoryXref> getChildCategoryXrefsInternal() {
        if (childCategoryXrefs.isEmpty()) {
            for (CategoryXref category : allChildCategoryXrefs) {
                if (category.getSubCategory().isActive()) {
                    childCategoryXrefs.add(category);
                }
            }
        }
        return Collections.unmodifiableList(childCategoryXrefs);
    }

    @Override
    public void setChildCategoryXrefs(List<CategoryXref> childCategories) {
        this.childCategoryXrefs.clear();
        for(CategoryXref category : childCategories){
            this.childCategoryXrefs.add(category);
        }
    }

    @Override
    public void setAllChildCategoryXrefs(List<CategoryXref> childCategories){
        allChildCategoryXrefs.clear();
        for(CategoryXref category : childCategories){
            allChildCategoryXrefs.add(category);
        }
    }

    @Override
    @Deprecated
    public List<Category> getAllChildCategories(){
        if (allLegacyChildCategories.isEmpty()) {
            for (CategoryXref category : allChildCategoryXrefs) {
                allLegacyChildCategories.add(category.getSubCategory());
            }
        }
        return Collections.unmodifiableList(allLegacyChildCategories);
    }

    @Override
    public boolean hasAllChildCategories(){
        return hasAllChildCategoriesInternal();
    }

    protected boolean hasAllChildCategoriesInternal() {
        return !allChildCategoryXrefs.isEmpty();
    }

    @Override
    @Deprecated
    public void setAllChildCategories(List<Category> childCategories){
        throw new UnsupportedOperationException("Not Supported - Use setAllChildCategoryXrefs()");
    }

    @Override
    @Deprecated
    public List<Category> getChildCategories() {
        if (legacyChildCategories.isEmpty()) {
            for (CategoryXref category : allChildCategoryXrefs) {
                if (category.getSubCategory().isActive()) {
                    legacyChildCategories.add(category.getSubCategory());
                }
            }
        }
        return Collections.unmodifiableList(legacyChildCategories);
    }

    @Override
    public boolean hasChildCategories() {
        return hasChildCategoriesInternal();
    }

    protected boolean hasChildCategoriesInternal() {
        return !getChildCategoryXrefs().isEmpty();
    }

    @Override
    @Deprecated
    public void setChildCategories(List<Category> childCategories) {
        throw new UnsupportedOperationException("Not Supported - Use setChildCategoryXrefs()");
    }

    @Override
    public List<Long> getChildCategoryIds() {
        if (childCategoryIds == null) {
            HydratedSetup.populateFromCache(this, "childCategoryIds");
        }
        return childCategoryIds;
    }

    @Override
    public void setChildCategoryIds(List<Long> childCategoryIds) {
        this.childCategoryIds = childCategoryIds;
    }

    public List<Long> createChildCategoryIds() {
        childCategoryIds = new ArrayList<Long>();
        for (CategoryXref category : allChildCategoryXrefs) {
            if (category.getSubCategory().isActive()) {
                childCategoryIds.add(category.getSubCategory().getId());
            }
        }
        return childCategoryIds;
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
    @Deprecated
    public Map<String, List<Long>> getChildCategoryURLMap() {
        if (childCategoryURLMap == null) {
            HydratedSetup.populateFromCache(this, "childCategoryURLMap");
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
    @Deprecated
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
        if (allParentCategoryXrefs != null && allParentCategoryXrefs.size() > 0) {
            for (CategoryXref parent : allParentCategoryXrefs) {
                myParentCategories.add(parent.getCategory());
            }
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
    public List<CategoryXref> getAllParentCategoryXrefs() {
        return getAllParentCategoryXrefsInternal();
    }

    protected List<CategoryXref> getAllParentCategoryXrefsInternal() {
        return allParentCategoryXrefs;
    }

    @Override
    public void setAllParentCategoryXrefs(List<CategoryXref> allParentCategories) {
        this.allParentCategoryXrefs.clear();
        allParentCategoryXrefs.addAll(allParentCategories);
    }

    @Override
    @Deprecated
    public List<Category> getAllParentCategories() {
        List<Category> parents = new ArrayList<Category>(allParentCategoryXrefs.size());
        for (CategoryXref xref : allParentCategoryXrefs) {
            parents.add(xref.getCategory());
        }
        return Collections.unmodifiableList(parents);
    }

    @Override
    @Deprecated
    public void setAllParentCategories(List<Category> allParentCategories) {
        throw new UnsupportedOperationException("Not Supported - Use setAllParentCategoryXrefs()");
    }

    @Override
    public List<FeaturedProduct> getFeaturedProducts() {
        return featuredProducts;
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
        return crossSaleProducts;
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
        return upSaleProducts;
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
    public List<CategoryProductXref> getActiveProductXrefs() {
        return getActiveProductXrefsInternal();
    }

    protected List<CategoryProductXref> getActiveProductXrefsInternal() {
        List<CategoryProductXref> result = new ArrayList<CategoryProductXref>();
        for (CategoryProductXref product : allProductXrefs) {
            if (product.getProduct().isActive()) {
                result.add(product);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<CategoryProductXref> getAllProductXrefs() {
        return getAllProductXrefsInternal();
    }

    public List<CategoryProductXref> getAllProductXrefsInternal() {
        return allProductXrefs;
    }

    @Override
    public void setAllProductXrefs(List<CategoryProductXref> allProducts) {
        this.allProductXrefs.clear();
        allProductXrefs.addAll(allProducts);
    }

    @Override
    @Deprecated
    public List<Product> getActiveProducts() {
        return getActiveProductsInternal();
    }

    protected List<Product> getActiveProductsInternal() {
        List<Product> result = new ArrayList<Product>();
        for (CategoryProductXref product : allProductXrefs) {
            if (product.getProduct().isActive()) {
                result.add(product.getProduct());
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    @Override
    @Deprecated
    public List<Product> getAllProducts() {
        return getAllProductsInternal();
    }

    protected List<Product> getAllProductsInternal() {
        List<Product> result = new ArrayList<Product>();
        for (CategoryProductXref product : allProductXrefs) {
            result.add(product.getProduct());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    @Deprecated
    public void setAllProducts(List<Product> allProducts) {
        throw new UnsupportedOperationException("Not Supported - Use setAllProductXrefs()");
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
    public Map<String, CategoryAttribute> getCategoryAttributesMap() {
        return categoryAttributes;
    }
    
    @Override
    public void setCategoryAttributesMap(Map<String, CategoryAttribute> categoryAttributes) {
        this.categoryAttributes = categoryAttributes;
    }

    @Override
    public List<CategoryAttribute> getCategoryAttributes() {
        List<CategoryAttribute> ca = new ArrayList<CategoryAttribute>(categoryAttributes.values());
        return Collections.unmodifiableList(ca);
    }

    @Override
    public void setCategoryAttributes(List<CategoryAttribute> categoryAttributes) {
        this.categoryAttributes = new HashMap<String, CategoryAttribute>();
        for (CategoryAttribute categoryAttribute : categoryAttributes) {
            this.categoryAttributes.put(categoryAttribute.getName(), categoryAttribute);
        }
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

    public static class Presentation {

        public static class Tab {

            public static class Name {

                public static final String Marketing = "CategoryImpl_Marketing_Tab";
                public static final String Media = "CategoryImpl_Media_Tab";
                public static final String Advanced = "CategoryImpl_Advanced_Tab";
                public static final String Products = "CategoryImpl_Products_Tab";
                public static final String SearchFacets = "CategoryImpl_categoryFacetsTab";
            }

            public static class Order {

                public static final int Marketing = 2000;
                public static final int Media = 3000;
                public static final int Advanced = 4000;
                public static final int Products = 5000;
                public static final int SearchFacets = 3500;
            }
        }

        public static class Group {

            public static class Name {

                public static final String General = "CategoryImpl_Category_Description";
                public static final String ActiveDateRange = "CategoryImpl_Active_Date_Range";
                public static final String Advanced = "CategoryImpl_Advanced";
            }

            public static class Order {

                public static final int General = 1000;
                public static final int ActiveDateRange = 2000;
                public static final int Advanced = 1000;
            }
        }
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }

    @Override
    public String getTaxCode() {
        return this.taxCode;
    }

    @Override
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    @Override
    public String getLocation() {
        return getUrl();
    }

}
