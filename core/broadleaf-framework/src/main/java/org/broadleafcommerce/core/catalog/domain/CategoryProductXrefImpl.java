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

import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.common.vendor.service.type.ContainerSizeType;
import org.broadleafcommerce.core.media.domain.Media;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class CategoryProductXrefImpl is the default implmentation of {@link Category}.
 * This entity is only used for executing a named query.
 *
 * If you want to add fields specific to your implementation of BroadLeafCommerce you should extend
 * this class and add your fields.  If you need to make significant changes to the class then you
 * should implement your own version of {@link Category}.
 * <br>
 * <br>
 * This implementation uses a Hibernate implementation of JPA configured through annotations.
 * The Entity references the following tables:
 * BLC_CATEGORY_PRODUCT_XREF,
 *
 * @see {@link Category}, {@link ProductImpl}
 * @author btaylor
 */
@Entity
@Polymorphism(type = PolymorphismType.EXPLICIT)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_PRODUCT_XREF")
@AdminPresentationClass(excludeFromPolymorphism = true)
public class CategoryProductXrefImpl implements CategoryProductXref {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    CategoryProductXrefPK categoryProductXref = new CategoryProductXrefPK();

    public CategoryProductXrefPK getCategoryProductXref() {
        return categoryProductXref;
    }

    public void setCategoryProductXref(CategoryProductXrefPK categoryProductXref) {
        this.categoryProductXref = categoryProductXref;
    }

    /** The display order. */
    @Column(name = "DISPLAY_ORDER")
    protected Long displayOrder;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.CategoryProductXref#getDisplayOrder()
     */
    public Long getDisplayOrder() {
        return displayOrder;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.CategoryProductXref#setDisplayOrder(java.lang.Integer)
     */
    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * @return
     * @see org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl.CategoryProductXrefPK#getCategory()
     */
    public Category getCategory() {
        return categoryProductXref.getCategory();
    }

    /**
     * @param category
     * @see org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl.CategoryProductXrefPK#setCategory(org.broadleafcommerce.core.catalog.domain.Category)
     */
    public void setCategory(Category category) {
        categoryProductXref.setCategory(category);
    }

    /**
     * @return
     * @see org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl.CategoryProductXrefPK#getProduct()
     */
    public Product getProduct() {
        return categoryProductXref.getProduct();
    }

    /**
     * @param product
     * @see org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl.CategoryProductXrefPK#setProduct(org.broadleafcommerce.core.catalog.domain.Product)
     */
    public void setProduct(Product product) {
        categoryProductXref.setProduct(product);
    }

    //Product interface methods

    @Override
    public void clearDynamicPrices() {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public Long getId() {
        return categoryProductXref.getProduct().getId();
    }

    @Override
    public void setId(Long id) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getName() {
        return categoryProductXref.getProduct().getName();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getDescription() {
        return categoryProductXref.getProduct().getDescription();
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getLongDescription() {
        return categoryProductXref.getProduct().getLongDescription();
    }

    @Override
    public void setLongDescription(String longDescription) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public Date getActiveStartDate() {
        return categoryProductXref.getProduct().getActiveStartDate();
    }

    @Override
    public void setActiveStartDate(Date activeStartDate) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public Date getActiveEndDate() {
        return categoryProductXref.getProduct().getActiveEndDate();
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public boolean isActive() {
        return categoryProductXref.getProduct().isActive();
    }

    @Override
    public Sku getDefaultSku() {
        return categoryProductXref.getProduct().getDefaultSku();
    }

    @Override
    public void setDefaultSku(Sku defaultSku) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public Boolean getCanSellWithoutOptions() {
        return categoryProductXref.getProduct().getCanSellWithoutOptions();
    }

    @Override
    public void setCanSellWithoutOptions(Boolean canSellWithoutOptions) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<Sku> getSkus() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getSkus());
    }

    @Override
    public List<Sku> getAdditionalSkus() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getAdditionalSkus());
    }

    @Override
    public void setAdditionalSkus(List<Sku> skus) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<Sku> getAllSkus() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getAllSkus());
    }

    @Override
    public Map<String, Media> getMedia() {
        return Collections.unmodifiableMap(categoryProductXref.getProduct().getMedia());
    }

    @Override
    public void setMedia(Map<String, Media> media) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public Map<String, Media> getAllSkuMedia() {
        return Collections.unmodifiableMap(categoryProductXref.getProduct().getAllSkuMedia());
    }

    @Override
    public List<Category> getAllParentCategories() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getAllParentCategories());
    }

    @Override
    public void setAllParentCategories(List<Category> allParentCategories) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public Category getDefaultCategory() {
        return categoryProductXref.getProduct().getDefaultCategory();
    }

    @Override
    public void setDefaultCategory(Category defaultCategory) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getModel() {
        return categoryProductXref.getProduct().getModel();
    }

    @Override
    public void setModel(String model) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getManufacturer() {
        return categoryProductXref.getProduct().getManufacturer();
    }

    @Override
    public void setManufacturer(String manufacturer) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public Dimension getDimension() {
        return categoryProductXref.getProduct().getDimension();
    }

    @Override
    public void setDimension(Dimension dimension) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public BigDecimal getWidth() {
        return categoryProductXref.getProduct().getWidth();
    }

    @Override
    public void setWidth(BigDecimal width) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public BigDecimal getHeight() {
        return categoryProductXref.getProduct().getHeight();
    }

    @Override
    public void setHeight(BigDecimal height) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public BigDecimal getDepth() {
        return categoryProductXref.getProduct().getDepth();
    }

    @Override
    public void setDepth(BigDecimal depth) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public BigDecimal getGirth() {
        return categoryProductXref.getProduct().getGirth();
    }

    @Override
    public void setGirth(BigDecimal girth) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public ContainerSizeType getSize() {
        return categoryProductXref.getProduct().getSize();
    }

    @Override
    public void setSize(ContainerSizeType size) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public ContainerShapeType getContainer() {
        return categoryProductXref.getProduct().getContainer();
    }

    @Override
    public void setContainer(ContainerShapeType container) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getDimensionString() {
        return categoryProductXref.getProduct().getDimensionString();
    }

    @Override
    public Weight getWeight() {
        return categoryProductXref.getProduct().getWeight();
    }

    @Override
    public void setWeight(Weight weight) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<RelatedProduct> getCrossSaleProducts() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getCrossSaleProducts());
    }

    @Override
    public void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<RelatedProduct> getUpSaleProducts() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getUpSaleProducts());
    }

    @Override
    public void setUpSaleProducts(List<RelatedProduct> upSaleProducts) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public boolean isFeaturedProduct() {
        return categoryProductXref.getProduct().isFeaturedProduct();
    }

    @Override
    public void setFeaturedProduct(boolean isFeaturedProduct) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<ProductAttribute> getProductAttributes() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getProductAttributes());
    }

    @Override
    public void setProductAttributes(List<ProductAttribute> productAttributes) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getPromoMessage() {
        return categoryProductXref.getProduct().getPromoMessage();
    }

    @Override
    public void setPromoMessage(String promoMessage) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<ProductOption> getProductOptions() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getProductOptions());
    }

    @Override
    public void setProductOptions(List<ProductOption> productOptions) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getUrl() {
        return categoryProductXref.getProduct().getUrl();
    }

    @Override
    public void setUrl(String url) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getUrlKey() {
        return categoryProductXref.getProduct().getUrlKey();
    }

    @Override
    public void setUrlKey(String url) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getDisplayTemplate() {
        return categoryProductXref.getProduct().getDisplayTemplate();
    }

    @Override
    public void setDisplayTemplate(String displayTemplate) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public String getGeneratedUrl() {
        return categoryProductXref.getProduct().getGeneratedUrl();
    }

    @Override
    public ProductAttribute getProductAttributeByName(String name) {
        return categoryProductXref.getProduct().getProductAttributeByName(name);
    }

    @Override
    public Map<String, ProductAttribute> getMappedProductAttributes() {
        return Collections.unmodifiableMap(categoryProductXref.getProduct().getMappedProductAttributes());
    }

    @Override
    public List<RelatedProduct> getCumulativeCrossSaleProducts() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getCumulativeCrossSaleProducts());
    }

    @Override
    public List<RelatedProduct> getCumulativeUpSaleProducts() {
        return Collections.unmodifiableList(categoryProductXref.getProduct().getCumulativeUpSaleProducts());
    }
}
