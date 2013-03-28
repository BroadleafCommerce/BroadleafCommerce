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
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationOperationTypes;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.util.TypedClosure;
import org.broadleafcommerce.common.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.common.vendor.service.type.ContainerSizeType;
import org.broadleafcommerce.core.media.domain.Media;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
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
@Table(name="BLC_PRODUCT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "baseProduct")
@SQLDelete(sql="UPDATE BLC_PRODUCT SET ARCHIVED = 'Y' WHERE PRODUCT_ID = ?")
public class ProductImpl implements Product, Status {

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
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="ProductImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductImpl")
        }
    )
    @Column(name = "PRODUCT_ID")
    @AdminPresentation(friendlyName = "ProductImpl_Product_ID", group = "ProductImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "URL")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Url", gridOrder=3, order = 1, group = "Seo_Group", groupOrder = 2, prominent = true, requiredOverride = RequiredOverride.REQUIRED)
    protected String url;

    @Column(name = "URL_KEY")
    @AdminPresentation(friendlyName = "ProductImpl_Product_UrlKey", order = 2, group = "Seo_Group", groupOrder = 2)
    protected String urlKey;

    @Column(name = "DISPLAY_TEMPLATE")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Display_Template", order=9, group = "ProductImpl_Product_Description",groupOrder=1)
    protected String displayTemplate;

    /** The product model number */
    @Column(name = "MODEL")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Model", order = 7, group = "ProductImpl_Product_Description", groupOrder = 1)
    protected String model;

    /** The manufacture name */
    @Column(name = "MANUFACTURE")
    @AdminPresentation(friendlyName = "ProductImpl_Product_Manufacturer", gridOrder = 4, order=6, group = "ProductImpl_Product_Description", prominent=true, groupOrder=1)
    protected String manufacturer;
    
    @Column(name = "IS_FEATURED_PRODUCT", nullable=false)
    @AdminPresentation(friendlyName = "ProductImpl_Is_Featured_Product", order=5, group = "ProductImpl_Product_Description", prominent=false, groupOrder=1)
    protected Boolean isFeaturedProduct = false;
    
    @OneToOne(optional = false, targetEntity = SkuImpl.class, cascade={CascadeType.ALL}, mappedBy = "defaultProduct")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL})
    protected Sku defaultSku;
    
    @Column(name = "CAN_SELL_WITHOUT_OPTIONS")
    @AdminPresentation(friendlyName = "ProductImpl_Can_Sell_Without_Options", order=8, group = "ProductImpl_Product_Description", prominent=false, groupOrder=1)
    protected Boolean canSellWithoutOptions = false;
    
    /** The skus. */
    @Transient
    protected List<Sku> skus = new ArrayList<Sku>();
    
    @Transient
    protected String promoMessage;

    @OneToMany(mappedBy = "product", targetEntity = CrossSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @AdminPresentationAdornedTargetCollection(targetObjectProperty = "relatedSaleProduct", friendlyName = "crossSaleProductsTitle",
            tab = "productMarketingTab", tabOrder = 200, sortProperty = "sequence", dataSourceName = "crossSaleProductsDS",
            maintainedAdornedTargetFields = { "promotionMessage" }, gridVisibleFields = { "defaultSku.name", "promotionMessage" })
    protected List<RelatedProduct> crossSaleProducts = new ArrayList<RelatedProduct>();

    @OneToMany(mappedBy = "product", targetEntity = UpSaleProductImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @OrderBy(value="sequence")
    @AdminPresentationAdornedTargetCollection(targetObjectProperty = "relatedSaleProduct", friendlyName = "upsaleProductsTitle",
            tab = "productMarketingTab", tabOrder = 200, sortProperty = "sequence", dataSourceName = "upSaleProductsDS",
            maintainedAdornedTargetFields = { "promotionMessage" }, gridVisibleFields = { "defaultSku.name", "promotionMessage" })
    protected List<RelatedProduct> upSaleProducts  = new ArrayList<RelatedProduct>();

    /** The all skus. */
    @OneToMany(fetch = FetchType.LAZY, targetEntity = SkuImpl.class, mappedBy="product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName="Additional Skus")
    protected List<Sku> additionalSkus = new ArrayList<Sku>();

    /** The default category. */
    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_CATEGORY_ID")
    @Index(name="PRODUCT_CATEGORY_INDEX", columnNames={"DEFAULT_CATEGORY_ID"})
    @AdminPresentation(friendlyName = "ProductImpl_Product_Default_Category", gridOrder = 2, order=4, group = "ProductImpl_Product_Description", requiredOverride = RequiredOverride.REQUIRED, groupOrder=1, prominent=true)
    @AdminPresentationToOneLookup()
    protected Category defaultCategory;

    @OneToMany(targetEntity = CategoryProductXrefImpl.class, mappedBy = "categoryProductXref.product")
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @OrderBy(value="displayOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationAdornedTargetCollection(joinEntityClass = "org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl",
            targetObjectProperty = "categoryProductXref.category",
            parentObjectProperty = "categoryProductXref.product",
            friendlyName = "allParentCategoriesTitle",
            sortProperty = "displayOrder",
            gridVisibleFields = { "name" })
    protected List<CategoryProductXref> allParentCategoryXrefs = new ArrayList<CategoryProductXref>();
    
    @OneToMany(mappedBy = "product", targetEntity = ProductAttributeImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(addType = AddMethodType.PERSIST, friendlyName = "productAttributesTitle", dataSourceName = "productAttributeDS")
    protected List<ProductAttribute> productAttributes  = new ArrayList<ProductAttribute>();
    
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = ProductOptionImpl.class)
    @JoinTable(name = "BLC_PRODUCT_OPTION_XREF", joinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_OPTION_ID", referencedColumnName = "PRODUCT_OPTION_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(addType = AddMethodType.LOOKUP, friendlyName = "productOptionsTitle", manyToField = "products",
            operationTypes = @AdminPresentationOperationTypes(removeType = OperationType.NONDESTRUCTIVEREMOVE))
    protected List<ProductOption> productOptions = new ArrayList<ProductOption>();

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
        defaultSku.setDefaultProduct(this);
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
        return allSkus;
    }

    @Override
    public List<Sku> getSkus() {
        if (skus.size() == 0) {
            List<Sku> additionalSkus = getAdditionalSkus();
            for (Sku sku : additionalSkus) {
                if (sku.isActive()) {
                    skus.add(sku);
                }
            }
        }
        return skus;
    }

    @Override
    public List<Sku> getAdditionalSkus() {
        return additionalSkus;
    }

    @Override
    public void setAdditionalSkus(List<Sku> skus) {
        this.additionalSkus.clear();
        for(Sku sku : skus){
            this.additionalSkus.add(sku);
        }
        //this.skus.clear();
    }

    @Override
    public Category getDefaultCategory() {
        return defaultCategory;
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
    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory;
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
        List<RelatedProduct> returnProducts = new ArrayList<RelatedProduct>();
        if (crossSaleProducts != null) {
            returnProducts.addAll(crossSaleProducts);
             CollectionUtils.filter(returnProducts, new Predicate() {
                 @Override
                 public boolean evaluate(Object arg) {
                     return 'Y'!=((Status)((CrossSaleProductImpl) arg).getRelatedProduct()).getArchived();
                 }
             });            
        }
        return returnProducts;
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
        List<RelatedProduct> returnProducts = new ArrayList<RelatedProduct>();
        if (upSaleProducts != null) {
            returnProducts.addAll(upSaleProducts);
            CollectionUtils.filter(returnProducts, new Predicate() {
                 @Override
                 public boolean evaluate(Object arg) {
                     return 'Y'!=((Status)((UpSaleProductImpl) arg).getRelatedProduct()).getArchived();
                 }
             });            
        }
        return returnProducts;
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
    public List<ProductAttribute> getProductAttributes() {
        return productAttributes;
    }

    @Override
    public void setProductAttributes(List<ProductAttribute> productAttributes) {
        this.productAttributes = productAttributes;
    }
    
    @Override
    public List<ProductOption> getProductOptions() {
        return productOptions;
    }

    @Override
    public void setProductOptions(List<ProductOption> productOptions) {
        this.productOptions = productOptions;
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
    public ProductAttribute getProductAttributeByName(String name) {
        for (ProductAttribute attribute : getProductAttributes()) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }
    
    @Override
    public Map<String, ProductAttribute> getMappedProductAttributes() {
        return BLCMapUtils.keyedMap(getProductAttributes(), new TypedClosure<String, ProductAttribute>() {
            @Override
            public String getKey(ProductAttribute value) {
                return value.getName();
            }
        });
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
        if (getClass() != obj.getClass())
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
    
    public String getMainEntityName() {
        return getManufacturer() + " " + getName();
    }

}
