/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.core.catalog.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicy;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.*;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.template.TemplatePathContainer;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.common.vendor.service.type.ContainerSizeType;
import org.broadleafcommerce.common.web.Locatable;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import java.math.BigDecimal;
import java.util.*;

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
 *
 * @author btaylor
 * @see {@link Product}, {@link SkuImpl}, {@link CategoryImpl}
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@javax.persistence.Table(name = "BLC_PRODUCT")
//multi-column indexes don't appear to get exported correctly when declared at the field level, so declaring here as a workaround
@org.hibernate.annotations.Table(appliesTo = "BLC_PRODUCT", indexes = {
        @Index(name = "PRODUCT_URL_INDEX",
                columnNames = {"URL", "URL_KEY"}
        )
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
@AdminPresentationMergeOverrides(
        {
                @AdminPresentationMergeOverride(name = "defaultSku.displayTemplate", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
                @AdminPresentationMergeOverride(name = "defaultSku.urlKey", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
                @AdminPresentationMergeOverride(name = "defaultSku.retailPrice", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE, overrideValue = "REQUIRED")),
                @AdminPresentationMergeOverride(name = "defaultSku.name", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE, overrideValue = "REQUIRED")),
                @AdminPresentationMergeOverride(name = "defaultSku.activeEndDate", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VALIDATIONCONFIGURATIONS, validationConfigurations = {
                        @ValidationConfiguration(
                                validationImplementation = "blAfterStartDateValidator",
                                configurationItems = {
                                        @ConfigurationItem(itemName = "otherField", itemValue = "defaultSku.activeStartDate")
                                })
                })),
                @AdminPresentationMergeOverride(name = "defaultSku.auditable.createdBy", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
                @AdminPresentationMergeOverride(name = "defaultSku.auditable.dateCreated", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
                @AdminPresentationMergeOverride(name = "defaultSku.auditable.dateUpdated", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
                @AdminPresentationMergeOverride(name = "defaultSku.auditable.updatedBy", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true))
        })
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class ProductImpl implements Product, ProductAdminPresentation, Status, AdminMainEntity, Locatable, TemplatePathContainer {

    private static final Log LOG = LogFactory.getLog(ProductImpl.class);
    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The id.
     */
    @Id
    @GeneratedValue(generator = "ProductId")
    @GenericGenerator(
            name = "ProductId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "ProductImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.catalog.domain.ProductImpl")
            })
    @Column(name = "PRODUCT_ID")
    @AdminPresentation(friendlyName = "ProductImpl_Product_ID", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "URL")
    @AdminPresentation(
            friendlyName = "ProductImpl_Product_Url",
            group = GroupName.General,
            order = FieldOrder.URL,
            prominent = true,
            gridOrder = 3,
            columnWidth = "260px",
            requiredOverride = RequiredOverride.REQUIRED,
            validationConfigurations = {
                    @ValidationConfiguration(validationImplementation = "blUriPropertyValidator"),
                    @ValidationConfiguration(
                            validationImplementation = "blUniqueValueValidator",
                            configurationItems = {
                                    @ConfigurationItem(
                                            itemName = ConfigurationItem.ERROR_MESSAGE,
                                            itemValue = "This URL is already in use. Please provide a unique URL."
                                    )
                            }
                    )
            }
    )
    protected String url;

    @Column(name = "OVERRIDE_GENERATED_URL")
    @AdminPresentation(friendlyName = "ProductImpl_Override_Generated_Url",
            group = GroupName.General, order = FieldOrder.URL + 10, defaultValue = "false")
    protected Boolean overrideGeneratedUrl = false;

    @Column(name = "URL_KEY")
    @AdminPresentation(friendlyName = "ProductImpl_Product_UrlKey",
            group = GroupName.Advanced,
            excluded = true)
    protected String urlKey;

    @Column(name = "DISPLAY_TEMPLATE")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Display_Template",
            group = GroupName.Advanced)
    protected String displayTemplate;

    @Column(name = "MODEL")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Model",
            excluded = true)
    protected String model;

    @Column(name = "MANUFACTURE")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Manufacturer",
            group = GroupName.General, order = FieldOrder.MANUFACTURER,
            prominent = true, gridOrder = 4)
    protected String manufacturer;

    @Deprecated
    @Column(name = "IS_FEATURED_PRODUCT", nullable = false)
    protected Boolean isFeaturedProduct = false;

    @OneToOne(targetEntity = SkuImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name = "DEFAULT_SKU_ID")
    @ClonePolicy(toOneProperty = "defaultProduct")
    protected Sku defaultSku;

    @Column(name = "CAN_SELL_WITHOUT_OPTIONS")
    @AdminPresentation(friendlyName = "ProductImpl_Can_Sell_Without_Options",
            group = GroupName.ProductOptionsAdvanced,
            tooltip = "ProductImpl_Can_Sell_Without_Options_Tooltip",
            defaultValue = "false")
    protected Boolean canSellWithoutOptions = false;

    @Column(name = "META_TITLE")
    @AdminPresentation(friendlyName = "ProductImpl_MetaTitle",
            group = GroupName.Miscellaneous, order = 4000,
            tooltip = "ProductImpl_MetaTitle_Tooltip")
    protected String metaTitle;

    @Column(name = "META_DESC")
    @AdminPresentation(friendlyName = "ProductImpl_MetaDescription",
            group = GroupName.Miscellaneous, order = 5000,
            tooltip = "ProductImpl_MetaDescription_Tooltip")
    protected String metaDescription;

    @Column(name = "CANONICAL_URL")
    @AdminPresentation(friendlyName = "ProductImpl_Canonical_Url",
            group = GroupName.Miscellaneous, order = 6000,
            tooltip = "ProductImpl_Canonical_Url_Tooltip")
    protected String canonicalUrl;

    @Transient
    protected List<Sku> skus = new ArrayList<Sku>();

    @Transient
    protected String promoMessage;

    @OneToMany(mappedBy = "product", targetEntity = CrossSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
    @OrderBy(value = "sequence")
    @AdminPresentationAdornedTargetCollection(friendlyName = "crossSaleProductsTitle",
            tab = TabName.Marketing, order = 1000,
            targetObjectProperty = "relatedSaleProduct",
            sortProperty = "sequence",
            maintainedAdornedTargetFields = {"promotionMessage"},
            gridVisibleFields = {"defaultSku.name", "promotionMessage"})
    protected List<RelatedProduct> crossSaleProducts = new ArrayList<RelatedProduct>();

    @OneToMany(mappedBy = "product", targetEntity = UpSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
    @OrderBy(value = "sequence")
    @AdminPresentationAdornedTargetCollection(friendlyName = "upsaleProductsTitle",
            tab = TabName.Marketing, order = 2000,
            targetObjectProperty = "relatedSaleProduct",
            sortProperty = "sequence",
            maintainedAdornedTargetFields = {"promotionMessage"},
            gridVisibleFields = {"defaultSku.name", "promotionMessage"})
    protected List<RelatedProduct> upSaleProducts = new ArrayList<RelatedProduct>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = SkuImpl.class, mappedBy = "product", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "ProductImpl_Additional_Skus",
            tab = TabName.ProductOptions, order = 1000)
    protected List<Sku> additionalSkus = new ArrayList<Sku>();

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_CATEGORY_ID")
    @Index(name = "PRODUCT_CATEGORY_INDEX", columnNames = {"DEFAULT_CATEGORY_ID"})
    @AdminPresentation(friendlyName = "ProductImpl_Product_Default_Category", order = FieldOrder.DEFAULT_CATEGORY,
            group = GroupName.General,
            prominent = true, gridOrder = 2,
            requiredOverride = RequiredOverride.REQUIRED)
    @AdminPresentationToOneLookup()
    @Deprecated
    protected Category defaultCategory;

    @OneToMany(targetEntity = CategoryProductXrefImpl.class, mappedBy = "product", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @OrderBy(value = "displayOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(friendlyName = "allParentCategoriesTitle",
            tab = TabName.Marketing, order = 3000,
            targetObjectProperty = "category",
            parentObjectProperty = "product",
            gridVisibleFields = {"name"})
    protected List<CategoryProductXref> allParentCategoryXrefs = new ArrayList<CategoryProductXref>();

    @OneToMany(mappedBy = "product", targetEntity = ProductAttributeImpl.class, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "productAttributesTitle",
            tab = TabName.General, order = 6000)
    protected List<ProductAttribute> productAttributes = new ArrayList<ProductAttribute>();

    @OneToMany(targetEntity = ProductOptionXrefImpl.class, mappedBy = "product",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(friendlyName = "productOptionsTitle",
            tab = TabName.ProductOptions,
            joinEntityClass = "org.broadleafcommerce.core.catalog.domain.ProductOptionXrefImpl",
            targetObjectProperty = "productOption",
            parentObjectProperty = "product",
            gridVisibleFields = {"name", "type", "required"})
    protected List<ProductOptionXref> productOptions = new ArrayList<ProductOptionXref>();

    @Transient
    protected Map<String, Set<String>> productOptionMap;

    @Transient
    protected List<String> allParentCategoryIds;

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

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
        return getDefaultSku().getName();
    }

    @Override
    public void setName(String name) {
        getDefaultSku().setName(name);
    }

    @Override
    public String getDescription() {
        return getDefaultSku().getDescription();
    }

    @Override
    public void setDescription(String description) {
        getDefaultSku().setDescription(description);
    }

    @Override
    public String getLongDescription() {
        return getDefaultSku().getLongDescription();
    }

    @Override
    public void setLongDescription(String longDescription) {
        getDefaultSku().setLongDescription(longDescription);
    }

    @Override
    public Date getActiveStartDate() {
        return getDefaultSku().getActiveStartDate();
    }

    @Override
    public void setActiveStartDate(Date activeStartDate) {
        getDefaultSku().setActiveStartDate(activeStartDate);
    }

    @Override
    public Date getActiveEndDate() {
        return getDefaultSku().getActiveEndDate();
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        getDefaultSku().setActiveEndDate(activeEndDate);
    }

    @Override
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), true)) {
                LOG.debug("product, " + id + ", inactive due to date");
            }
            if ('Y' == getArchived()) {
                LOG.debug("product, " + id + ", inactive due to archived status");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), true) && 'Y' != getArchived();
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Deprecated
    @Override
    public boolean isFeaturedProduct() {
        return isFeaturedProduct;
    }

    @Deprecated
    @Override
    public void setFeaturedProduct(boolean isFeaturedProduct) {
        this.isFeaturedProduct = isFeaturedProduct;
    }

    @Override
    public boolean hasRetailPrice() {
        return getRetailPrice() != null;
    }

    @Override
    public Money getRetailPrice() {
        return getRetailPriceInternal();
    }

    @Override
    public Money getSalePrice() {
        return getSalePriceInternal();
    }

    protected Money getRetailPriceInternal() {
        if (defaultSku == null) {
            return null;
        } else {
            return defaultSku.getRetailPrice();
        }
    }

    protected Money getSalePriceInternal() {
        if (defaultSku == null) {
            return null;
        } else {
            return defaultSku.getSalePrice();
        }
    }

    @Override
    public boolean isOnSale() {
        return defaultSku.isOnSale();
    }

    @Override
    public Money getMargin() {
        return getDefaultSku().getMargin();
    }

    @Override
    public Sku getDefaultSku() {
        return defaultSku;
    }

    @Override
    public Boolean getCanSellWithoutOptions() {
        return canSellWithoutOptions == null ? false : canSellWithoutOptions;
    }

    @Override
    public void setCanSellWithoutOptions(Boolean canSellWithoutOptions) {
        this.canSellWithoutOptions = canSellWithoutOptions;
    }

    @Override
    public String getMetaTitle() {
        return metaTitle;
    }

    @Override
    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    @Override
    public String getMetaDescription() {
        return metaDescription;
    }

    @Override
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    @Override
    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    @Override
    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    @Override
    public void setDefaultSku(Sku defaultSku) {
        if (defaultSku != null) {
            defaultSku.setDefaultProduct(this);

        }
        this.defaultSku = defaultSku;
    }

    @Override
    public String getPromoMessage() {
        return promoMessage;
    }

    @Override
    public void setPromoMessage(String promoMessage) {
        this.promoMessage = promoMessage;
    }

    @Override
    public List<Sku> getAllSkus() {
        return getAllSkus(true);
    }

    @Override
    public List<Sku> getAllSellableSkus() {
        boolean includeDefaultSku = getCanSellWithoutOptions() || getAdditionalSkus().isEmpty();
        return getAllSkus(includeDefaultSku);
    }

    protected List<Sku> getAllSkus(boolean includeDefaultSku) {
        List<Sku> allSkus = new ArrayList<Sku>();
        if (includeDefaultSku) {
            allSkus.add(getDefaultSku());
        }
        for (Sku additionalSku : additionalSkus) {
            if (!additionalSku.getId().equals(getDefaultSku().getId())) {
                allSkus.add(additionalSku);
            }
        }
        return Collections.unmodifiableList(allSkus);
    }

    @Override
    @Deprecated
    public List<Sku> getSkus() {
        if (skus.size() == 0) {
            List<Sku> additionalSkus = getAdditionalSkus();
            for (Sku sku : additionalSkus) {
                if (sku.isActive()) {
                    skus.add(sku);
                }
            }
        }
        return Collections.unmodifiableList(skus);
    }

    @Override
    public List<Sku> getAdditionalSkus() {
        return additionalSkus;
    }

    @Override
    @Deprecated
    public void setAdditionalSkus(List<Sku> skus) {
        this.additionalSkus.clear();
        for (Sku sku : skus) {
            this.additionalSkus.add(sku);
        }
    }

    @Override
    @Deprecated
    public Category getDefaultCategory() {
        Category response;
        if (defaultCategory != null) {
            response = defaultCategory;
        } else {
            response = getCategory();
        }
        return response;
    }

    @Override
    @Deprecated
    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    @Override
    public Category getCategory() {
        Category response = null;
        List<CategoryProductXref> xrefs = getAllParentCategoryXrefs();
        if (!CollectionUtils.isEmpty(xrefs)) {
            for (CategoryProductXref xref : xrefs) {
                if (xref.getCategory().isActive() && xref.getDefaultReference() != null && xref.getDefaultReference()) {
                    response = xref.getCategory();
                    break;
                }
            }
        }
        if (response == null) {
            if (!CollectionUtils.isEmpty(xrefs)) {
                for (CategoryProductXref xref : xrefs) {
                    if (xref.getCategory().isActive()) {
                        response = xref.getCategory();
                        break;
                    }
                }
            }
        }
        return response;
    }

    @Override
    public void setCategory(Category category) {
        List<CategoryProductXref> xrefs = getAllParentCategoryXrefs();
        boolean found = false;
        for (CategoryProductXref xref : xrefs) {
            if (xref.getCategory().equals(category)) {
                xref.setDefaultReference(true);
                found = true;
            } else if (xref.getDefaultReference() != null && xref.getDefaultReference()) {
                xref.setDefaultReference(null);
            }
        }
        if (!found && category != null) {
            CategoryProductXref xref = new CategoryProductXrefImpl();
            xref.setProduct(this);
            xref.setCategory(category);
            xref.setDefaultReference(true);
            allParentCategoryXrefs.add(xref);
        }
    }

    @Override
    public Map<String, Media> getMedia() {
        return getDefaultSku().getSkuMedia();
    }

    @Override
    public void setMedia(Map<String, Media> media) {
        getDefaultSku().setSkuMedia(media);
    }

    @Override
    public Map<String, Media> getAllSkuMedia() {
        Map<String, Media> result = new HashMap<String, Media>();
        result.putAll(getMedia());
        for (Sku additionalSku : getAdditionalSkus()) {
            if (!additionalSku.getId().equals(getDefaultSku().getId())) {
                result.putAll(additionalSku.getSkuMedia());
            }
        }
        return result;
    }

    @Override
    public List<CategoryProductXref> getAllParentCategoryXrefs() {
        return allParentCategoryXrefs;
    }

    @Override
    public void setAllParentCategoryXrefs(List<CategoryProductXref> allParentCategories) {
        this.allParentCategoryXrefs.clear();
        allParentCategoryXrefs.addAll(allParentCategories);
    }

    @Override
    public List<Long> getParentCategoryHierarchyIds() {
        List<Long> parentCategoryHierarchyIds = new ArrayList<>();

        List<CategoryProductXref> parentCategoryXrefs = getAllParentCategoryXrefs();
        for (CategoryProductXref xref : parentCategoryXrefs) {
            Category xrefCategory = xref.getCategory();
            List<Category> parentCategoryHierarchy = xrefCategory.getParentCategoryHierarchy(null);
            for (Category hierarchyCategory : parentCategoryHierarchy) {
                parentCategoryHierarchyIds.add(hierarchyCategory.getId());
            }
        }

        return parentCategoryHierarchyIds;
    }

    @Override
    public List<String> getAllParentCategoryIds() {
        List<String> parentIds = new ArrayList<>();

        for (CategoryProductXref xref : allParentCategoryXrefs) {
            String parentId = String.valueOf(xref.getCategory().getId());
            parentIds.add(parentId);
        }

        allParentCategoryIds = parentIds;
        return parentIds;
    }

    @Override
    @Deprecated
    public List<Category> getAllParentCategories() {
        List<Category> parents = new ArrayList<Category>();
        for (CategoryProductXref xref : allParentCategoryXrefs) {
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
    public Dimension getDimension() {
        return getDefaultSku().getDimension();
    }

    @Override
    public void setDimension(Dimension dimension) {
        getDefaultSku().setDimension(dimension);
    }

    @Override
    public BigDecimal getWidth() {
        return getDefaultSku().getDimension().getWidth();
    }

    @Override
    public void setWidth(BigDecimal width) {
        getDefaultSku().getDimension().setWidth(width);
    }

    @Override
    public BigDecimal getHeight() {
        return getDefaultSku().getDimension().getHeight();
    }

    @Override
    public void setHeight(BigDecimal height) {
        getDefaultSku().getDimension().setHeight(height);
    }

    @Override
    public BigDecimal getDepth() {
        return getDefaultSku().getDimension().getDepth();
    }

    @Override
    public void setDepth(BigDecimal depth) {
        getDefaultSku().getDimension().setDepth(depth);
    }

    @Override
    public BigDecimal getGirth() {
        return getDefaultSku().getDimension().getGirth();
    }

    @Override
    public void setGirth(BigDecimal girth) {
        getDefaultSku().getDimension().setGirth(girth);
    }

    @Override
    public ContainerSizeType getSize() {
        return getDefaultSku().getDimension().getSize();
    }

    @Override
    public void setSize(ContainerSizeType size) {
        getDefaultSku().getDimension().setSize(size);
    }

    @Override
    public ContainerShapeType getContainer() {
        return getDefaultSku().getDimension().getContainer();
    }

    @Override
    public void setContainer(ContainerShapeType container) {
        getDefaultSku().getDimension().setContainer(container);
    }

    @Override
    public String getDimensionString() {
        return getDefaultSku().getDimension().getDimensionString();
    }

    @Override
    public Weight getWeight() {
        return getDefaultSku().getWeight();
    }

    @Override
    public void setWeight(Weight weight) {
        getDefaultSku().setWeight(weight);
    }

    @Override
    public List<RelatedProduct> getCrossSaleProducts() {
        return crossSaleProducts;
    }

    @Override
    public void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts) {
        this.crossSaleProducts.clear();
        for (RelatedProduct relatedProduct : crossSaleProducts) {
            this.crossSaleProducts.add(relatedProduct);
        }
    }

    @Override
    public List<RelatedProduct> getUpSaleProducts() {
        return upSaleProducts;
    }

    @Override
    public void setUpSaleProducts(List<RelatedProduct> upSaleProducts) {
        this.upSaleProducts.clear();
        for (RelatedProduct relatedProduct : upSaleProducts) {
            this.upSaleProducts.add(relatedProduct);
        }
        this.upSaleProducts = upSaleProducts;
    }

    @Override
    public List<RelatedProduct> getCumulativeCrossSaleProducts() {
        List<RelatedProduct> returnProducts = getCrossSaleProducts();
        if (defaultCategory != null) {
            List<RelatedProduct> categoryProducts = defaultCategory.getCumulativeCrossSaleProducts();
            if (categoryProducts != null) {
                returnProducts.addAll(categoryProducts);
            }
        }
        Iterator<RelatedProduct> itr = returnProducts.iterator();
        while (itr.hasNext()) {
            RelatedProduct relatedProduct = itr.next();
            if (relatedProduct.getRelatedProduct().equals(this)) {
                itr.remove();
            }
        }
        return returnProducts;
    }

    @Override
    public List<RelatedProduct> getCumulativeUpSaleProducts() {
        List<RelatedProduct> returnProducts = getUpSaleProducts();
        if (defaultCategory != null) {
            List<RelatedProduct> categoryProducts = defaultCategory.getCumulativeUpSaleProducts();
            if (categoryProducts != null) {
                returnProducts.addAll(categoryProducts);
            }
        }
        Iterator<RelatedProduct> itr = returnProducts.iterator();
        while (itr.hasNext()) {
            RelatedProduct relatedProduct = itr.next();
            if (relatedProduct.getRelatedProduct().equals(this)) {
                itr.remove();
            }
        }
        return returnProducts;
    }

    @Override
    @Deprecated
    public Map<String, ProductAttribute> getProductAttributes() {
        Map<String, ProductAttribute> attributeMap = new HashMap<String, ProductAttribute>();

        for (ProductAttribute productAttribute : productAttributes) {
            attributeMap.put(productAttribute.getName(), productAttribute);
        }

        return attributeMap;
    }

    @Override
    public Map<String, ProductAttribute> getMultiValueProductAttributes() {
        Map<String, ProductAttribute> multiValueMap = new MultiValueMap();

        for (ProductAttribute productAttribute : productAttributes) {
            multiValueMap.put(productAttribute.getName(), productAttribute);
        }

        return multiValueMap;
    }

    @Override
    public void setProductAttributes(Map<String, ProductAttribute> productAttributes) {
        List<ProductAttribute> productAttributeList = new ArrayList<ProductAttribute>();

        if (productAttributes instanceof MultiValueMap) {
            Iterator<String> it = productAttributes.keySet().iterator();

            while (it.hasNext()) {
                String theKey = it.next();
                productAttributeList.addAll((List) productAttributes.get(theKey));
            }
        } else {
            for (Map.Entry<String, ProductAttribute> entry : productAttributes.entrySet()) {
                productAttributeList.add(entry.getValue());
            }
        }

        this.productAttributes = productAttributeList;
    }

    @Override
    public List<ProductOptionXref> getProductOptionXrefs() {
        List<ProductOptionXref> sorted = new ArrayList<ProductOptionXref>(productOptions);
        Collections.sort(sorted, new Comparator<ProductOptionXref>() {

            @Override
            public int compare(ProductOptionXref o1, ProductOptionXref o2) {
                return ObjectUtils.compare(o1.getProductOption().getDisplayOrder(), o2.getProductOption().getDisplayOrder(), true);
            }

        });
        return sorted;
    }

    @Override
    public void setProductOptionXrefs(List<ProductOptionXref> productOptions) {
        this.productOptions = productOptions;
    }

    @Override
    public List<ProductOption> getProductOptions() {
        List<ProductOption> response = new ArrayList<ProductOption>();
        for (ProductOptionXref xref : getProductOptionXrefs()) {
            response.add(xref.getProductOption());
        }
        return Collections.unmodifiableList(response);
    }

    @Override
    public void setProductOptions(List<ProductOption> productOptions) {
        throw new UnsupportedOperationException("Use setProductOptionXrefs(..) instead");
    }

    @Override
    public String getUrl() {
        if (url == null) {
            return getGeneratedUrl();
        } else {
            return url;
        }
    }

    @Override
    public Boolean getOverrideGeneratedUrl() {
        return overrideGeneratedUrl == null ? false : overrideGeneratedUrl;
    }

    @Override
    public void setOverrideGeneratedUrl(Boolean overrideGeneratedUrl) {
        this.overrideGeneratedUrl = overrideGeneratedUrl == null ? false : overrideGeneratedUrl;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
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
    public Character getArchived() {
        ArchiveStatus temp;
        if (archiveStatus == null) {
            temp = new ArchiveStatus();
        } else {
            temp = archiveStatus;
        }
        return temp.getArchived();
    }

    @Override
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        archiveStatus.setArchived(archived);
    }

    @Override
    public Map<String, Set<String>> getProductOptionValuesMap() {
        if (productOptionMap == null) {
            productOptionMap = new HashMap<String, Set<String>>();
            List<ProductOptionXref> xrefs = getProductOptionXrefs();
            if (xrefs != null) {
                for (ProductOptionXref xref : xrefs) {
                    List<ProductOptionValue> productOptionValues = xref.getProductOption().getAllowedValues();
                    if (productOptionValues != null && !productOptionValues.isEmpty()) {
                        HashSet<String> values = new HashSet<String>();
                        for (ProductOptionValue value : productOptionValues) {
                            values.add(value.getAttributeValue());
                        }
                        productOptionMap.put(xref.getProductOption().getAttributeName(), values);
                    }
                }
            }
        }

        return productOptionMap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((skus == null) ? 0 : skus.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        ProductImpl other = (ProductImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (skus == null) {
            if (other.skus != null)
                return false;
        } else if (!skus.equals(other.skus))
            return false;
        return true;
    }

    @Override
    public String getUrlKey() {
        if (urlKey != null) {
            return urlKey;
        } else {
            if (getName() != null) {
                String returnKey = getName().toLowerCase();

                returnKey = returnKey.replaceAll(" ", "-");
                return returnKey.replaceAll("[^A-Za-z0-9/-]", "");
            }
        }
        return null;
    }

    @Override
    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    @Override
    public String getGeneratedUrl() {
        if (getDefaultCategory() != null && getDefaultCategory().getGeneratedUrl() != null) {
            String generatedUrl = getDefaultCategory().getGeneratedUrl();
            if (generatedUrl.endsWith("//")) {
                return generatedUrl + getUrlKey();
            } else {
                return generatedUrl + "//" + getUrlKey();
            }
        }
        return null;
    }

    @Override
    public void clearDynamicPrices() {
        for (Sku sku : getAllSkus()) {
            sku.clearDynamicPrices();
        }
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }

    @Override
    public <G extends Product> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        Product cloned = createResponse.getClone();
        cloned.setCanSellWithoutOptions(canSellWithoutOptions);
        cloned.setFeaturedProduct(isFeaturedProduct);
        cloned.setUrl(url);
        cloned.setUrlKey(urlKey);
        cloned.setManufacturer(manufacturer);
        cloned.setPromoMessage(promoMessage);
        if (defaultCategory != null) {
            cloned.setDefaultCategory(defaultCategory.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setModel(model);
        if (defaultSku != null) {
            cloned.setDefaultSku(defaultSku.createOrRetrieveCopyInstance(context).getClone());
        }
        for (Sku entry : additionalSkus) {
            Sku clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getAdditionalSkus().add(clonedEntry);
        }
        for (ProductOptionXref entry : productOptions) {
            ProductOptionXref clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getProductOptionXrefs().add(clonedEntry);

        }
        for (Map.Entry<String, ProductAttribute> entry : getProductAttributes().entrySet()) {
            ProductAttribute clonedEntry = entry.getValue().createOrRetrieveCopyInstance(context).getClone();
            cloned.getProductAttributes().put(entry.getKey(), clonedEntry);
        }

        //Don't clone references to other Product and Category collections - those will be handled by another MultiTenantCopier call

        return createResponse;
    }

    @Override
    public String getTaxCode() {
        return getDefaultSku().getTaxCode();
    }

    @Override
    public void setTaxCode(String taxCode) {
        getDefaultSku().setTaxCode(taxCode);
    }

    @Override
    public String getLocation() {
        return getUrl();
    }

    @Override
    public FieldEntity getFieldEntityType() {
        return FieldEntity.PRODUCT;
    }
}
