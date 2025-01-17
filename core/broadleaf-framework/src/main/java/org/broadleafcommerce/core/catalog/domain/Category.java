/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.search.domain.CategoryExcludedSearchFacet;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Implementations of this interface are used to hold data about a Category.  A category is a group of products.
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to how the
 * Category is persisted.  If you just want to add additional fields then you should extend {@link CategoryImpl}.
 *
 * @author btaylor
 * @author Jeff Fischer
 * @see {@link CategoryImpl}
 */
public interface Category extends Serializable, MultiTenantCloneable<Category> {

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    @Nullable
    Long getId();

    /**
     * Sets the primary key.
     *
     * @param id the new primary key
     */
    void setId(@Nullable Long id);

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Nonnull
    String getName();

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    void setName(@Nonnull String name);

    String getProductTitlePatternOverride();

    void setProductTitlePatternOverride(String productTitlePatternOverride);

    String getProductDescriptionPatternOverride();

    void setProductDescriptionPatternOverride(String productDescriptionPatternOverride);

    /**
     * Gets the default parent category. This method will delegate to
     * {@link #getParentCategory()} by default, unless the "use.legacy.default.category.mode" property is set to
     * true in the implementation's property file. If set to true, this method will use legacy behavior,
     * which is to return the deprecated defaultParentCategory field.
     *
     * @return the default parent category
     * @deprecated use {@link #getParentCategory()} instead
     */
    @Deprecated
    @Nullable
    Category getDefaultParentCategory();

    /**
     * Sets the default parent category. This method will delegate to
     * {@link #setParentCategory(Category)} by default, unless the "use.legacy.default.category.mode" property is set to
     * true in the implementation's property file. If set to true, this method will use legacy behavior,
     * which is to set the deprecated defaultParentCategory field.
     *
     * @param defaultParentCategory the new default parent category
     * @deprecated use {@link #setParentCategory(Category)} instead
     */
    @Deprecated
    void setDefaultParentCategory(@Nullable Category defaultParentCategory);

    /**
     * Return the category that is the parent of this category - if applicable
     *
     * @return
     */
    Category getParentCategory();

    /**
     * Set the parent category of this category
     *
     * @param category
     */
    void setParentCategory(Category category);

    /**
     * Return the parent category xref of the default parent or first applicable parent
     *
     * @return
     */
    CategoryXref getParentCategoryXref();

    /**
     * Gets the url. The url represents the presentation layer destination for
     * this category. For example, if using Spring MVC, you could send the user
     * to this destination by returning {@code "redirect:"+currentCategory.getUrl();}
     * from a controller.
     *
     * @return the url for the presentation layer component for this category
     */
    @Nullable
    String getUrl();

    /**
     * Sets the url. The url represents the presentation layer destination for
     * this category. For example, if using Spring MVC, you could send the user
     * to this destination by returning {@code "redirect:"+currentCategory.getUrl();}
     * from a controller.
     *
     * @param url the new url for the presentation layer component for this category
     */
    void setUrl(@Nullable String url);

    /**
     * @return the flag for whether or not the URL should not be generated in the admin
     */
    Boolean getOverrideGeneratedUrl();

    /**
     * Sets the flag for whether or not the URL should not be generated in the admin
     *
     * @param overrideGeneratedUrl
     */
    void setOverrideGeneratedUrl(Boolean overrideGeneratedUrl);

    /**
     * Gets the url key. The url key is used as part of SEO url generation for this
     * category. Each segment of the url leading to a category is comprised of the url
     * keys of the various associated categories in a hierarchy leading to this one. If
     * the url key is null, the the name for the category formatted with dashes for spaces.
     *
     * @return the url key for this category to appear in the SEO url
     */
    @Nullable
    String getUrlKey();

    /**
     * Sets the url key. The url key is used as part of SEO url generation for this
     * category. Each segment of the url leading to a category is comprised of the url
     * keys of the various associated categories in a hierarchy leading to this one.
     *
     * @param urlKey the new url key for this category to appear in the SEO url
     */
    void setUrlKey(@Nullable String urlKey);

    /**
     * Creates the SEO url starting from this category and recursing up the
     * hierarchy of default parent categories until the topmost category is
     * reached. The url key for each category is used for each segment
     * of the SEO url.
     *
     * @return the generated SEO url for this category
     */
    @Nullable
    String getGeneratedUrl();

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Nullable
    String getDescription();

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    void setDescription(@Nullable String description);

    /**
     * Gets the active start date. If the current date is before activeStartDate,
     * then this category will not be visible on the site.
     *
     * @return the active start date
     */
    @Nullable
    Date getActiveStartDate();

    /**
     * Sets the active start date. If the current date is before activeStartDate,
     * then this category will not be visible on the site.
     *
     * @param activeStartDate the new active start date
     */
    void setActiveStartDate(@Nullable Date activeStartDate);

    /**
     * Gets the active end date. If the current date is after activeEndDate,
     * the this category will not be visible on the site.
     *
     * @return the active end date
     */
    @Nullable
    Date getActiveEndDate();

    /**
     * Sets the active end date. If the current date is after activeEndDate,
     * the this category will not be visible on the site.
     *
     * @param activeEndDate the new active end date
     */
    void setActiveEndDate(@Nullable Date activeEndDate);

    /**
     * Checks if is active. Returns true if the startDate is null or if the current
     * date is after the start date, or if the endDate is null or if the current date
     * is before the endDate.
     *
     * @return true, if is active
     */
    boolean isActive();

    /**
     * Gets the display template. The display template can be used to help create a unique key
     * that drives the presentation layer destination for this category. For example, if
     * using Spring MVC, you might derive the view destination in this way:
     * <p>
     * {@code view = categoryTemplatePrefix + currentCategory.getDisplayTemplate();}
     *
     * @return the display template
     */
    @Nullable
    String getDisplayTemplate();

    /**
     * Sets the display template. The display template can be used to help create a unique key
     * that drives the presentation layer destination for this category. For example, if
     * using Spring MVC, you might derive the view destination in this way:
     * <p>
     * {@code view = categoryTemplatePrefix + currentCategory.getDisplayTemplate();}
     *
     * @param displayTemplate the new display template
     */
    void setDisplayTemplate(@Nullable String displayTemplate);

    /**
     * Gets the child category url map. This map is keyed off of the {@link #getGeneratedUrl()} values
     * for this category and all of its child categories. By calling get on this map using the
     * generated url for a given category, you will receive the list of immediate child categories.
     * This is inefficient, so its use is highly discouraged.
     *
     * @return the child category url map
     * @deprecated This approach is inherently inefficient and should no longer be used
     */
    @Deprecated
    @Nonnull
    Map<String, List<Long>> getChildCategoryURLMap();

    /**
     * Set the child category url map. This approach is inefficient,
     * so its use is highly discouraged.
     *
     * @param childCategoryURLMap
     * @deprecated This approach is inherently inefficient and should no longer be used
     */
    @Deprecated
    void setChildCategoryURLMap(@Nonnull Map<String, List<Long>> childCategoryURLMap);

    /**
     * Gets the category media map. The key is of arbitrary meaning
     * and the {@code Media} instance stores information about the
     * media itself (image url, etc...)
     *
     * @return the category Media
     * @deprecated use {@link #getCategoryMediaXref()} instead
     */
    @Nonnull
    @Deprecated
    Map<String, Media> getCategoryMedia();

    /**
     * Sets the category media. The key is of arbitrary meaning
     * and the {@code Media} instance stores information about the
     * media itself (image url, etc...)
     *
     * @param categoryMedia the category media
     * @deprecated use {@link #setCategoryMediaXref(Map)} instead
     */
    @Deprecated
    void setCategoryMedia(@Nonnull Map<String, Media> categoryMedia);

    /**
     * Gets the category media map. The key is of arbitrary meaning
     * and the {@code Media} instance stores information about the
     * media itself (image url, etc...)
     *
     * @return the category Media
     */
    Map<String, CategoryMediaXref> getCategoryMediaXref();

    /**
     * Sets the category media. The key is of arbitrary meaning
     * and the {@code Media} instance stores information about the
     * media itself (image url, etc...)
     *
     * @param categoryMediaXref the category media
     */
    void setCategoryMediaXref(Map<String, CategoryMediaXref> categoryMediaXref);

    /**
     * Gets the long description.
     *
     * @return the long description
     */
    @Nullable
    String getLongDescription();

    /**
     * Sets the long description.
     *
     * @param longDescription the new long description
     */
    void setLongDescription(@Nullable String longDescription);

    /**
     * Gets the meta data title of the category
     *
     * @return
     */
    String getMetaTitle();

    /**
     * Sets the meta data title of the category
     *
     * @param metaTitle
     */
    void setMetaTitle(String metaTitle);

    /**
     * Gets the meta data description of the category
     *
     * @return
     */
    String getMetaDescription();

    /**
     * Sets the meta data description of the category
     *
     * @param metaDescription
     */
    void setMetaDescription(String metaDescription);

    /**
     * Gets the featured products. Featured products are a special list
     * of products you would like to showcase for this category.
     *
     * @return the featured products
     */
    @Nonnull
    List<FeaturedProduct> getFeaturedProducts();

    /**
     * Sets the featured products. Featured products are a special list
     * of products you would like to showcase for this category.
     *
     * @param featuredProducts the featured products
     */
    void setFeaturedProducts(@Nonnull List<FeaturedProduct> featuredProducts);

    /**
     * Returns a list of cross sale products that are related to this category.
     *
     * @return a list of cross sale products
     */
    List<RelatedProduct> getCrossSaleProducts();

    /**
     * Sets the cross sale products that are related to this category.
     *
     * @param crossSaleProducts
     * @see #getCrossSaleProducts()
     */
    void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts);

    /**
     * Returns a list of cross sale products that are related to this category.
     *
     * @return a list of cross sale products
     */
    List<RelatedProduct> getUpSaleProducts();

    /**
     * Sets the upsale products that are related to this category.
     *
     * @param upSaleProducts
     * @see #getUpSaleProducts()
     */
    void setUpSaleProducts(List<RelatedProduct> upSaleProducts);

    /**
     * Returns a list of the cross sale products in this category as well as
     * all cross sale products in all parent categories of this category.
     *
     * @return the cumulative cross sale products
     */
    List<RelatedProduct> getCumulativeCrossSaleProducts();

    /**
     * Returns a list of the upsale products in this category as well as
     * all upsale products in all parent categories of this category.
     *
     * @return the cumulative upsale products
     */
    List<RelatedProduct> getCumulativeUpSaleProducts();

    /**
     * Returns a list of the featured products in this category as well as
     * all featured products in all parent categories of this category.
     *
     * @return the cumulative featured products
     */
    List<FeaturedProduct> getCumulativeFeaturedProducts();

    /**
     * Returns all of the SearchFacets that are directly associated with this Category
     *
     * @return related SearchFacets
     */
    List<CategorySearchFacet> getSearchFacets();

    /**
     * Sets the SearchFacets that are directly associated with this Category
     *
     * @param searchFacets
     */
    void setSearchFacets(List<CategorySearchFacet> searchFacets);

    /**
     * Gets the excluded SearchFacets
     *
     * @return the excluded SearchFacets
     */
    List<CategoryExcludedSearchFacet> getExcludedSearchFacets();

    /**
     * Sets the SearchFacets that should not be rendered by this Category. Typically, this will include
     * facets from parent categories that do not apply to this category.
     *
     * @param excludedSearchFacets
     */
    void setExcludedSearchFacets(List<CategoryExcludedSearchFacet> excludedSearchFacets);

    /**
     * Returns a list of CategorySearchFacets that takes into consideration the search facets for this Category,
     * the search facets for all parent categories, and the search facets that should be excluded from this
     * Category. This method will order the resulting list based on the {@link CategorySearchFacet#getPosition()}
     * method for each category level. That is, the facets on this Category will be ordered by their position
     * relative to each other with the ordered parent facets after that, etc.
     *
     * @return the current active search facets for this category and all parent categories
     */
    List<CategorySearchFacet> getCumulativeSearchFacets();

    /**
     * Returns a list of CategorySearchFacets that takes into consideration the search facets for this Category,
     * the search facets for all parent categories, and the search facets that should be excluded from this
     * Category. This method will order the resulting list based on the {@link CategorySearchFacet#getPosition()}
     * method for each category level. That is, the facets on this Category will be ordered by their position
     * relative to each other with the ordered parent facets after that, etc.
     * <p>
     * Takes a Set of the categories that have been traversed in order to protect from circular dependencies.
     *
     * @param categoryHierarchy
     * @return the current active search facets for this category and all parent categories     *
     */
    List<CategorySearchFacet> getCumulativeSearchFacets(Set<Category> categoryHierarchy);

    /**
     * Build category hierarchy by walking the default category tree up to the root category.
     * If the passed in tree is null then create the initial list.
     *
     * @param currentHierarchy
     * @return
     */
    List<Category> buildDefaultParentCategoryPath(List<Category> currentHierarchy);

    /**
     * Build the full category hierarchy by walking up the default category tree and the all parent
     * category tree.
     *
     * @param currentHierarchy
     * @return the full hierarchy
     */
    List<Category> getParentCategoryHierarchy(List<Category> currentHierarchy);

    /**
     * Build the full category hierarchy by walking up the default category tree and the all parent
     * category tree.  Adds the option of only adding the first parent found.
     *
     * @param currentHierarchy
     * @param firstParent      determines if ONLY the first parent category should be returned per hierarchy tier
     * @return the full hierarchy
     */
    List<Category> getParentCategoryHierarchy(List<Category> currentHierarchy, Boolean firstParent);

    /**
     * Gets the attributes for this {@link Category}. In smaller sites, using these attributes might be preferred to
     * extending the domain object itself.
     *
     * @return
     * @see {@link #getMappedCategoryAttributes()}
     */
    Map<String, CategoryAttribute> getCategoryAttributesMap();

    void setCategoryAttributesMap(Map<String, CategoryAttribute> categoryAttributes);

    /**
     * Gets the attributes for this {@link Category}. In smaller sites, using these attributes might be preferred to
     * extending the domain object itself.
     *
     * @return
     * @see {@link #getMappedCategoryAttributes()}
     * @deprecated This will be replaced with {@link #getCategoryAttributesMap()} in 3.1.0.
     */
    @Deprecated
    List<CategoryAttribute> getCategoryAttributes();

    /**
     * Sets the attributes for this {@link Category}. In smaller sites, using these attributes might be preferred to
     * extending the domain object and creating a new table to store custom properties.
     *
     * @return
     * @deprecated This will be replaced with {@link #setCategoryAttributesMap()} in 3.1.0.
     */
    @Deprecated
    void setCategoryAttributes(List<CategoryAttribute> categoryAttributes);

    /**
     * Convenience method to get a {@link CategoryAttribute} by name
     *
     * @param name
     * @return
     * @see {@link #getCategoryAttributes()}, {@link #getMappedCategoryAttributes()}
     * @deprecated This will be removed in 3.1.0
     */
    @Deprecated
    CategoryAttribute getCategoryAttributeByName(String name);

    /**
     * Convenience method to return the {@link CategoryAttribute}s for the {@link Category} in an easily-consumable
     * form
     *
     * @return
     * @deprecated This will be removed in 3.1.0
     */
    @Deprecated
    Map<String, CategoryAttribute> getMappedCategoryAttributes();

    Map<String, CategoryAttribute> getMultiValueCategoryAttributes();

    /**
     * Used to determine availability for all of the products/skus in this category
     *
     * @return the {@link InventoryType} for this category
     */
    InventoryType getInventoryType();

    /**
     * Sets the type of inventory for this category
     *
     * @param inventoryType the {@link InventoryType} for this category
     */
    void setInventoryType(InventoryType inventoryType);

    /**
     * Returns the default fulfillment type for skus in this category. May be null.
     *
     * @return
     */
    FulfillmentType getFulfillmentType();

    /**
     * Sets the default fulfillment type for skus in this category. May return null.
     *
     * @param fulfillmentType
     */
    void setFulfillmentType(FulfillmentType fulfillmentType);

    /**
     * Gets the child categories. This list includes all categories, regardless
     * of whether or not they are active.
     *
     * @return the list of active and inactive child categories.
     * @deprecated use getAllChildCategoryXrefs() instead.
     */
    @Nonnull
    @Deprecated
    List<Category> getAllChildCategories();

    /**
     * Sets the list of child categories (active and inactive)
     *
     * @param childCategories the list of child categories
     * @deprecated Use setAllChildCategoryXrefs() instead.
     */
    @Deprecated
    void setAllChildCategories(@Nonnull List<Category> childCategories);

    /**
     * Checks for child categories.
     *
     * @return true, if this category has any children (active or not)
     */
    boolean hasAllChildCategories();

    /**
     * Gets the child categories. If child categories has not been previously
     * set, then the list of active only categories will be returned.
     *
     * @return the list of active child categories
     * @deprecated Use getChildCategoryXrefs() instead.
     */
    @Deprecated
    @Nonnull
    List<Category> getChildCategories();

    /**
     * Sets the all child categories. This should be a list
     * of active only child categories.
     *
     * @param childCategories the list of active child categories.
     * @deprecated Use setChildCategoryXrefs() instead.
     */
    @Deprecated
    void setChildCategories(@Nonnull List<Category> childCategories);

    /**
     * Gets the child category ids. If child categories has not been previously
     * set, then the list of active only categories will be returned. This method
     * is optimized with Hydrated cache, which means that the algorithm required
     * to harvest active child categories will not need to be rebuilt as long
     * as the parent category (this category) is not evicted from second level cache.
     *
     * @return the list of active child category ids
     */
    @Nonnull
    List<Long> getChildCategoryIds();

    /**
     * Sets the all child category ids. This should be a list
     * of active only child categories.
     *
     * @param childCategoryIds the list of active child category ids.
     */
    void setChildCategoryIds(@Nonnull List<Long> childCategoryIds);

    /**
     * Checks for child categories.
     *
     * @return true, if this category contains any active child categories.
     */
    boolean hasChildCategories();

    List<CategoryXref> getAllChildCategoryXrefs();

    void setAllChildCategoryXrefs(List<CategoryXref> childCategories);

    List<CategoryXref> getChildCategoryXrefs();

    void setChildCategoryXrefs(List<CategoryXref> childCategories);

    /**
     * Retrieve all the xref entities linking this category to parent categories
     */
    List<CategoryXref> getAllParentCategoryXrefs();

    /**
     * Set all the xref entities linking this product to parent categories
     */
    void setAllParentCategoryXrefs(List<CategoryXref> allParentCategories);

    /**
     * Retrieve the displayOrder that is used if the Category does not have any parents
     */
    BigDecimal getRootDisplayOrder();

    /**
     * Set the displayOrder that is used if the Category does not have any parents
     */
    void setRootDisplayOrder(BigDecimal rootDisplayOrder);

    /**
     * Retrieve all parent categories
     *
     * @return the list of parent categories
     * @deprecated Use getAllParentCategoryXrefs() instead.
     */
    @Deprecated
    @Nonnull
    List<Category> getAllParentCategories();

    /**
     * Sets the list of parent categories
     *
     * @param allParentCategories the list of parent categories
     * @deprecated Use setAllParentCategoryXrefs() instead.
     */
    @Deprecated
    void setAllParentCategories(@Nonnull List<Category> allParentCategories);

    List<CategoryProductXref> getActiveProductXrefs();

    List<CategoryProductXref> getAllProductXrefs();

    void setAllProductXrefs(List<CategoryProductXref> allProducts);

    /**
     * Convenience method to retrieve all of this {@link Category}'s {@link Product}s filtered by
     * active. If you want all of the {@link Product}s (whether inactive or not) consider using
     * {@link #getAllProducts()}.
     *
     * @return the list of active {@link Product}s for this {@link Category}
     * @see {@link Product#isActive()}
     * @deprecated Use getActiveProductXrefs() instead.
     */
    @Deprecated
    List<Product> getActiveProducts();

    /**
     * Retrieve all the {@code Product} instances associated with this
     * category.
     * <br />
     * <b>Note:</b> this method does not take into account whether or not the {@link Product}s are active or not. If
     * you need this functionality, use {@link #getActiveProducts()}
     *
     * @return the list of products associated with this category.
     * @deprecated Use getAllProductXrefs() instead.
     */
    @Deprecated
    @Nonnull
    List<Product> getAllProducts();

    /**
     * Set all the {@code Product} instances associated with this
     * category.
     *
     * @param allProducts the list of products to associate with this category
     * @deprecated Use setAllProductXrefs() instead.
     */
    @Deprecated
    void setAllProducts(@Nonnull List<Product> allProducts);

    /**
     * Returns the tax code of this category.
     *
     * @return taxCode
     */
    String getTaxCode();

    /**
     * Sets the tax code of this category.
     *
     * @param taxCode
     */
    void setTaxCode(String taxCode);

    /**
     * Intended to hold any unique identifier not tied to the Broadleaf Database Sequence Identifier.
     * For example, many implementations may integrate or import/export
     * data from other systems that manage their own unique identifiers.
     *
     * @return external ID
     */
    String getExternalId();

    /**
     * Sets a unique external ID
     *
     * @param externalId
     */
    void setExternalId(String externalId);

}
