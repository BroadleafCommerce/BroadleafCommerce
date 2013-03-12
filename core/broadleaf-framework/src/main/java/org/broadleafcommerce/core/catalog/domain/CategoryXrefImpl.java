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
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The Class CategoryXrefImpl is for testing purposes only.  It helps autogenerate the cross reference table
 * properly with the DISPLY_ORDER column

 * @author krosenberg
 *
 */
@Entity
@Polymorphism(type = PolymorphismType.EXPLICIT)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_XREF")
@AdminPresentationClass(excludeFromPolymorphism = true)
public class CategoryXrefImpl implements CategoryXref {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The category id. */
    @EmbeddedId
    CategoryXrefPK categoryXrefPK = new CategoryXrefPK();

    public CategoryXrefPK getCategoryXrefPK() {
        return categoryXrefPK;
    }

    public void setCategoryXrefPK(final CategoryXrefPK categoryXrefPK) {
        this.categoryXrefPK = categoryXrefPK;
    }

    @Column(name = "DISPLAY_ORDER")
    protected Long displayOrder;
    
    @Transient
    protected boolean isChildOriented = true;

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(final Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isChildOriented() {
        return isChildOriented;
    }

    public void setChildOriented(boolean childOriented) {
        isChildOriented = childOriented;
    }

    /**
     * @return
     * @see org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK#getCategory()
     */
    public Category getCategory() {
        return categoryXrefPK.getCategory();
    }

    /**
     * @param category
     * @see org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK#setCategory(org.broadleafcommerce.core.catalog.domain.Category)
     */
    public void setCategory(Category category) {
        categoryXrefPK.setCategory(category);
    }

    /**
     * @return
     * @see org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK#getSubCategory()
     */
    public Category getSubCategory() {
        return categoryXrefPK.getSubCategory();
    }

    /**
     * @param subCategory
     * @see org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK#setSubCategory(org.broadleafcommerce.core.catalog.domain.Category)
     */
    public void setSubCategory(Category subCategory) {
        categoryXrefPK.setSubCategory(subCategory);
    }

    //Category interface methods
    
    protected Category getCategoryBasedOnOrientation() {
        return isChildOriented?getSubCategory():getCategory();
    }

    @Override
    public List<Category> buildCategoryHierarchy(List<Category> currentHierarchy) {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().buildCategoryHierarchy(currentHierarchy));
    }

    @Nullable
    @Override
    public Long getId() {
        return getCategoryBasedOnOrientation().getId();
    }

    @Override
    public void setId(@Nullable Long id) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nonnull
    @Override
    public String getName() {
        return getCategoryBasedOnOrientation().getName();
    }

    @Override
    public void setName(@Nonnull String name) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nullable
    @Override
    public Category getDefaultParentCategory() {
        return getCategoryBasedOnOrientation().getDefaultParentCategory();
    }

    @Override
    public void setDefaultParentCategory(@Nullable Category defaultParentCategory) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nonnull
    @Override
    public List<CategoryXref> getAllParentCategories() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getAllParentCategories());
    }

    @Override
    public void setAllParentCategories(@Nonnull List<CategoryXref> allParentCategories) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nullable
    @Override
    public String getUrl() {
        return getCategoryBasedOnOrientation().getUrl();
    }

    @Override
    public void setUrl(@Nullable String url) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nullable
    @Override
    public String getUrlKey() {
        return getCategoryBasedOnOrientation().getUrlKey();
    }

    @Nullable
    @Override
    public String getGeneratedUrl() {
        return getCategoryBasedOnOrientation().getGeneratedUrl();
    }

    @Override
    public void setUrlKey(@Nullable String urlKey) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nullable
    @Override
    public String getDescription() {
        return getCategoryBasedOnOrientation().getDescription();
    }

    @Override
    public void setDescription(@Nullable String description) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nullable
    @Override
    public Date getActiveStartDate() {
        return getCategoryBasedOnOrientation().getActiveStartDate();
    }

    @Override
    public void setActiveStartDate(@Nullable Date activeStartDate) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nullable
    @Override
    public Date getActiveEndDate() {
        return getCategoryBasedOnOrientation().getActiveEndDate();
    }

    @Override
    public void setActiveEndDate(@Nullable Date activeEndDate) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public boolean isActive() {
        return getCategoryBasedOnOrientation().isActive();
    }

    @Nullable
    @Override
    public String getDisplayTemplate() {
        return getCategoryBasedOnOrientation().getDisplayTemplate();
    }

    @Override
    public void setDisplayTemplate(@Nullable String displayTemplate) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nonnull
    @Override
    public Map<String, List<Long>> getChildCategoryURLMap() {
        return Collections.unmodifiableMap(getCategoryBasedOnOrientation().getChildCategoryURLMap());
    }

    @Override
    public void setChildCategoryURLMap(@Nonnull Map<String, List<Long>> childCategoryURLMap) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nonnull
    @Override
    public List<CategoryXref> getAllChildCategories() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getAllChildCategories());
    }

    @Override
    public boolean hasAllChildCategories() {
        return getCategoryBasedOnOrientation().hasAllChildCategories();
    }

    @Override
    public void setAllChildCategories(@Nonnull List<CategoryXref> childCategories) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nonnull
    @Override
    public List<CategoryXref> getChildCategories() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getChildCategories());
    }

    @Override
    public boolean hasChildCategories() {
        return getCategoryBasedOnOrientation().hasChildCategories();
    }

    @Override
    public void setChildCategories(@Nonnull List<CategoryXref> childCategories) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nonnull
    @Override
    public Map<String, String> getCategoryImages() {
        return Collections.unmodifiableMap(getCategoryBasedOnOrientation().getCategoryImages());
    }

    @Nullable
    @Override
    public String getCategoryImage(@Nonnull String imageKey) {
        return getCategoryBasedOnOrientation().getCategoryImage(imageKey);
    }

    @Override
    public void setCategoryImages(@Nonnull Map<String, String> categoryImages) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nonnull
    @Override
    public Map<String, Media> getCategoryMedia() {
        return Collections.unmodifiableMap(getCategoryBasedOnOrientation().getCategoryMedia());
    }

    @Override
    public void setCategoryMedia(@Nonnull Map<String, Media> categoryMedia) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return getCategoryBasedOnOrientation().getLongDescription();
    }

    @Override
    public void setLongDescription(@Nullable String longDescription) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Nonnull
    @Override
    public List<FeaturedProduct> getFeaturedProducts() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getFeaturedProducts());
    }

    @Override
    public void setFeaturedProducts(@Nonnull List<FeaturedProduct> featuredProducts) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<CategoryProductXref> getActiveProducts() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getActiveProducts());
    }

    @Nonnull
    @Override
    public List<CategoryProductXref> getAllProducts() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getAllProducts());
    }

    @Override
    public void setAllProducts(@Nonnull List<CategoryProductXref> allProducts) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<RelatedProduct> getCrossSaleProducts() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getCrossSaleProducts());
    }

    @Override
    public void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<RelatedProduct> getUpSaleProducts() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getUpSaleProducts());
    }

    @Override
    public void setUpSaleProducts(List<RelatedProduct> upSaleProducts) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<RelatedProduct> getCumulativeCrossSaleProducts() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getCumulativeCrossSaleProducts());
    }

    @Override
    public List<RelatedProduct> getCumulativeUpSaleProducts() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getCumulativeUpSaleProducts());
    }

    @Override
    public List<FeaturedProduct> getCumulativeFeaturedProducts() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getCumulativeFeaturedProducts());
    }

    @Override
    public List<CategorySearchFacet> getSearchFacets() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getSearchFacets());
    }

    @Override
    public void setSearchFacets(List<CategorySearchFacet> searchFacets) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public void setExcludedSearchFacets(List<SearchFacet> excludedSearchFacets) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public List<SearchFacet> getExcludedSearchFacets() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getExcludedSearchFacets());
    }

    @Override
    public List<CategorySearchFacet> getCumulativeSearchFacets() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getCumulativeSearchFacets());
    }

    @Override
    public List<Category> buildFullCategoryHierarchy(List<Category> currentHierarchy) {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().buildFullCategoryHierarchy(currentHierarchy));
    }

    @Override
    public List<CategoryAttribute> getCategoryAttributes() {
        return Collections.unmodifiableList(getCategoryBasedOnOrientation().getCategoryAttributes());
    }

    @Override
    public void setCategoryAttributes(List<CategoryAttribute> categoryAttributes) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public CategoryAttribute getCategoryAttributeByName(String name) {
        return getCategoryBasedOnOrientation().getCategoryAttributeByName(name);
    }

    @Override
    public Map<String, CategoryAttribute> getMappedCategoryAttributes() {
        return Collections.unmodifiableMap(getCategoryBasedOnOrientation().getMappedCategoryAttributes());
    }

    @Override
    public InventoryType getInventoryType() {
        return getCategoryBasedOnOrientation().getInventoryType();
    }

    @Override
    public void setInventoryType(InventoryType inventoryType) {
        throw new UnsupportedOperationException("operation not supported");
    }

    @Override
    public FulfillmentType getFulfillmentType() {
        return getCategoryBasedOnOrientation().getFulfillmentType();
    }

    @Override
    public void setFulfillmentType(FulfillmentType fulfillmentType) {
        throw new UnsupportedOperationException("operation not supported");
    }
}
