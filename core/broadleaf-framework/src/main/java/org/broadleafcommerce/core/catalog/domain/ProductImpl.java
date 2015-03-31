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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeService;
import org.broadleafcommerce.common.service.ParentCategoryLegacyModeServiceImpl;
import org.broadleafcommerce.common.template.TemplatePathContainer;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.common.vendor.service.type.ContainerSizeType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafRequestProcessor;
import org.broadleafcommerce.common.web.Locatable;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

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
@javax.persistence.Table(name="BLC_PRODUCT")
//multi-column indexes don't appear to get exported correctly when declared at the field level, so declaring here as a workaround
@org.hibernate.annotations.Table(appliesTo = "BLC_PRODUCT", indexes = {
    @Index(name = "PRODUCT_URL_INDEX",
            columnNames = {"URL","URL_KEY"}
    )
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "defaultSku.displayTemplate", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "defaultSku.urlKey", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "defaultSku.retailPrice", mergeEntries = 
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE, overrideValue = "REQUIRED")),
        @AdminPresentationMergeOverride(name = "defaultSku.name", mergeEntries = 
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE, overrideValue = "REQUIRED"))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "baseProduct")
@SQLDelete(sql="UPDATE BLC_PRODUCT SET ARCHIVED = 'Y' WHERE PRODUCT_ID = ?")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class ProductImpl implements Product, Status, AdminMainEntity, Locatable, TemplatePathContainer {

    private static final Log LOG = LogFactory.getLog(ProductImpl.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator= "ProductId")
    @GenericGenerator(
        name="ProductId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ProductImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductImpl")
        }
    )
    @Column(name = "PRODUCT_ID")
    @AdminPresentation(friendlyName = "ProductImpl_Product_ID", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "URL")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Url", order = Presentation.FieldOrder.URL,
        group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General, 
        prominent = true, gridOrder = 3, columnWidth = "200px",
            requiredOverride = RequiredOverride.REQUIRED,
            validationConfigurations = { @ValidationConfiguration(validationImplementation = "blUriPropertyValidator") })
    protected String url;

    @Column(name = "OVERRIDE_GENERATED_URL")
    @AdminPresentation(friendlyName = "ProductImpl_Override_Generated_Url", group = Presentation.Group.Name.General,
            order = Presentation.FieldOrder.URL + 10)
    protected Boolean overrideGeneratedUrl = false;

    @Column(name = "URL_KEY")
    @AdminPresentation(friendlyName = "ProductImpl_Product_UrlKey",
        tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
        group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General, 
        excluded = true)
    protected String urlKey;

    @Column(name = "DISPLAY_TEMPLATE")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Display_Template", 
        tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
        group = Presentation.Group.Name.Advanced, groupOrder = Presentation.Group.Order.Advanced)
    protected String displayTemplate;

    @Column(name = "MODEL")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Model",
        tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
        group = Presentation.Group.Name.Advanced, groupOrder = Presentation.Group.Order.Advanced)
    protected String model;

    @Column(name = "MANUFACTURE")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Manufacturer", order = Presentation.FieldOrder.MANUFACTURER,
        group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General, 
        prominent = true, gridOrder = 4)
    protected String manufacturer;
    
    @Column(name = "IS_FEATURED_PRODUCT", nullable=false)
    @AdminPresentation(friendlyName = "ProductImpl_Is_Featured_Product", requiredOverride = RequiredOverride.NOT_REQUIRED,
        tab = Presentation.Tab.Name.Marketing, tabOrder = Presentation.Tab.Order.Marketing,
        group = Presentation.Group.Name.Badges, groupOrder = Presentation.Group.Order.Badges)
    protected Boolean isFeaturedProduct = false;
    
    @OneToOne(targetEntity = SkuImpl.class, cascade={CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name = "DEFAULT_SKU_ID")
    @ClonePolicy(toOneProperty = "defaultProduct")
    protected Sku defaultSku;
    
    @Column(name = "CAN_SELL_WITHOUT_OPTIONS")
    @AdminPresentation(friendlyName = "ProductImpl_Can_Sell_Without_Options",
        tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
        group = Presentation.Group.Name.Advanced, groupOrder = Presentation.Group.Order.Advanced)
    protected Boolean canSellWithoutOptions = false;
    
    @Transient
    protected List<Sku> skus = new ArrayList<Sku>();
    
    @Transient
    protected String promoMessage;

    @OneToMany(mappedBy = "product", targetEntity = CrossSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
    @OrderBy(value="sequence")
    @AdminPresentationAdornedTargetCollection(friendlyName = "crossSaleProductsTitle", order = 1000,
        tab = Presentation.Tab.Name.Marketing, tabOrder = Presentation.Tab.Order.Marketing,
        targetObjectProperty = "relatedSaleProduct", 
        sortProperty = "sequence", 
        maintainedAdornedTargetFields = { "promotionMessage" }, 
        gridVisibleFields = { "defaultSku.name", "promotionMessage" })
    protected List<RelatedProduct> crossSaleProducts = new ArrayList<RelatedProduct>();

    @OneToMany(mappedBy = "product", targetEntity = UpSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
    @OrderBy(value="sequence")
    @AdminPresentationAdornedTargetCollection(friendlyName = "upsaleProductsTitle", order = 2000,
        tab = Presentation.Tab.Name.Marketing, tabOrder = Presentation.Tab.Order.Marketing,
        targetObjectProperty = "relatedSaleProduct", 
        sortProperty = "sequence",
        maintainedAdornedTargetFields = { "promotionMessage" }, 
        gridVisibleFields = { "defaultSku.name", "promotionMessage" })
    protected List<RelatedProduct> upSaleProducts  = new ArrayList<RelatedProduct>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = SkuImpl.class, mappedBy = "product", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "ProductImpl_Additional_Skus", order = 1000,
        tab = Presentation.Tab.Name.ProductOptions, tabOrder = Presentation.Tab.Order.ProductOptions)
    protected List<Sku> additionalSkus = new ArrayList<Sku>();

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_CATEGORY_ID")
    @Index(name="PRODUCT_CATEGORY_INDEX", columnNames={"DEFAULT_CATEGORY_ID"})
    @AdminPresentation(friendlyName = "ProductImpl_Product_Default_Category", order = Presentation.FieldOrder.DEFAULT_CATEGORY,
        group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General, 
        prominent = true, gridOrder = 2, 
        requiredOverride = RequiredOverride.REQUIRED)
    @AdminPresentationToOneLookup()
    @Deprecated
    protected Category defaultCategory;

    @OneToMany(targetEntity = CategoryProductXrefImpl.class, mappedBy = "product", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @OrderBy(value="displayOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(friendlyName = "allParentCategoriesTitle", order = 3000,
        tab = Presentation.Tab.Name.Marketing, tabOrder = Presentation.Tab.Order.Marketing,
        targetObjectProperty = "category",
        parentObjectProperty = "product",
        gridVisibleFields = { "name" })
    protected List<CategoryProductXref> allParentCategoryXrefs = new ArrayList<CategoryProductXref>();

    @OneToMany(mappedBy = "product", targetEntity = ProductAttributeImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
    @MapKey(name="name")
    @BatchSize(size = 50)
    @AdminPresentationMap(friendlyName = "productAttributesTitle",
        tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
        deleteEntityUponRemove = true, forceFreeFormKeys = true, keyPropertyFriendlyName = "ProductAttributeImpl_Attribute_Name"
    )
    protected Map<String, ProductAttribute> productAttributes = new HashMap<String, ProductAttribute>();

    @OneToMany(targetEntity = ProductOptionXrefImpl.class, mappedBy = "product",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(friendlyName = "productOptionsTitle",
        tab = Presentation.Tab.Name.ProductOptions, tabOrder = Presentation.Tab.Order.ProductOptions,
        joinEntityClass = "org.broadleafcommerce.core.catalog.domain.ProductOptionXrefImpl",
        targetObjectProperty = "productOption",
        parentObjectProperty = "product",
        gridVisibleFields = {"label", "required"})
    protected List<ProductOptionXref> productOptions = new ArrayList<ProductOptionXref>();

    @Transient
    protected Map<String, Set<String>> productOptionMap;

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
        return  getDefaultSku().getActiveStartDate();
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
            if ('Y'==getArchived()) {
                LOG.debug("product, " + id + ", inactive due to archived status");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), true) && 'Y'!=getArchived();
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

    @Override
    public boolean isFeaturedProduct() {
        return isFeaturedProduct;
    }

    @Override
    public void setFeaturedProduct(boolean isFeaturedProduct) {
        this.isFeaturedProduct = isFeaturedProduct;
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
        List<Sku> allSkus = new ArrayList<Sku>();
        allSkus.add(getDefaultSku());
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
        for(Sku sku : skus){
            this.additionalSkus.add(sku);
        }
    }

    @Override
    @Deprecated
    public Category getDefaultCategory() {
        Category response = null;
        if (defaultCategory != null) {
            response = defaultCategory;
            //TODO add code to look for a category via getCategory(), otherwise fall into the next block
        } else {
            List<CategoryProductXref> xrefs = getAllParentCategoryXrefs();
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
    @Deprecated
    public void setDefaultCategory(Category defaultCategory) {
        if (isDefaultCategoryLegacyMode()) {
            this.defaultCategory = defaultCategory;
        } else {
            setCategory(defaultCategory);
        }
    }

    @Override
    public Category getCategory() {
        if (!CollectionUtils.isEmpty(allParentCategoryXrefs)){
            //TODO Use a isDefault field on the xref instead to check
            return allParentCategoryXrefs.get(0).getCategory();
        }
        return null;
    }

    @Override
    public void setCategory(Category category) {
        //TODO Use a isDefault field on the xref
        if (!CollectionUtils.isEmpty(allParentCategoryXrefs)){
            allParentCategoryXrefs.get(0).setCategory(category);
        } else {
            CategoryProductXref xref = new CategoryProductXrefImpl();
            xref.setProduct(this);
            xref.setCategory(category);
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
    @Deprecated
    public List<CategoryProductXref> getAllParentCategoryXrefs() {
        return allParentCategoryXrefs;
    }

    @Override
    @Deprecated
    public void setAllParentCategoryXrefs(List<CategoryProductXref> allParentCategories) {
        this.allParentCategoryXrefs.clear();
        allParentCategoryXrefs.addAll(allParentCategories);
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
        for(RelatedProduct relatedProduct : crossSaleProducts){
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
        for(RelatedProduct relatedProduct : upSaleProducts){
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
        while(itr.hasNext()) {
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
        while(itr.hasNext()) {
            RelatedProduct relatedProduct = itr.next();
            if (relatedProduct.getRelatedProduct().equals(this)) {
                itr.remove();
            }
        }
        return returnProducts;
    }

    @Override
    public Map<String, ProductAttribute> getProductAttributes() {
        return productAttributes;
    }

    @Override
    public void setProductAttributes(Map<String, ProductAttribute> productAttributes) {
        this.productAttributes = productAttributes;
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
                
                returnKey = returnKey.replaceAll(" ","-");
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
        for(Sku entry : additionalSkus){
            Sku clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getAdditionalSkus().add(clonedEntry);
        }
        for(ProductOptionXref entry : productOptions){
            ProductOptionXref clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getProductOptionXrefs().add(clonedEntry);

        }
        for(Map.Entry<String, ProductAttribute> entry : productAttributes.entrySet()){
            ProductAttribute clonedEntry = entry.getValue().createOrRetrieveCopyInstance(context).getClone();
            cloned.getProductAttributes().put(entry.getKey(),clonedEntry);
        }

        //Don't clone references to other Product and Category collections - those will be handled by another MultiTenantCopier call

        return createResponse;
    }

    public static class Presentation {
        public static class Tab {
            public static class Name {
                public static final String Marketing = "ProductImpl_Marketing_Tab";
                public static final String Media = "SkuImpl_Media_Tab";
                public static final String ProductOptions = "ProductImpl_Product_Options_Tab";
                public static final String Inventory = "ProductImpl_Inventory_Tab";
                public static final String Shipping = "ProductImpl_Shipping_Tab";
                public static final String Advanced = "ProductImpl_Advanced_Tab";

            }
            
            public static class Order {
                public static final int Marketing = 2000;
                public static final int Media = 3000;
                public static final int ProductOptions = 4000;
                public static final int Inventory = 5000;
                public static final int Shipping = 6000;
                public static final int Advanced = 7000;
            }
        }
            
        public static class Group {
            public static class Name {
                public static final String General = "ProductImpl_Product_Description";
                public static final String Price = "SkuImpl_Price";
                public static final String ActiveDateRange = "ProductImpl_Product_Active_Date_Range";
                public static final String Advanced = "ProductImpl_Advanced";
                public static final String Inventory = "SkuImpl_Sku_Inventory";
                public static final String Badges = "ProductImpl_Badges";
                public static final String Shipping = "ProductWeight_Shipping";
                public static final String Financial = "ProductImpl_Financial";
            }
            
            public static class Order {
                public static final int General = 1000;
                public static final int Price = 2000;
                public static final int ActiveDateRange = 3000;
                public static final int Advanced = 1000;
                public static final int Inventory = 1000;
                public static final int Badges = 1000;
                public static final int Shipping = 1000;
            }

        }

        public static class FieldOrder {

            public static final int NAME = 1000;
            public static final int SHORT_DESCRIPTION = 2000;
            public static final int PRIMARY_MEDIA = 3000;
            public static final int LONG_DESCRIPTION = 4000;
            public static final int DEFAULT_CATEGORY = 5000;
            public static final int MANUFACTURER = 6000;
            public static final int URL = 7000;
        }
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

    protected Boolean isDefaultCategoryLegacyMode() {
        ParentCategoryLegacyModeService legacyModeService = ParentCategoryLegacyModeServiceImpl.getLegacyModeService();
        if (legacyModeService != null) {
            return legacyModeService.isLegacyMode();
        }
        return false;
    }
}
