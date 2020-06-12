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

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.search.domain.CategoryExcludedSearchFacet;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementations of this interface are used to hold data about a Category.  A category is a group of products.
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to how the
 * Category is persisted.  If you just want to add additional fields then you should extend {@link CategoryImpl}.
 *
 * @see {@link CategoryImpl}
 * @author btaylor
 * @author Jeff Fischer
 * 
 */
public interface Category extends Serializable, MultiTenantCloneable<Category> {

    /**
     * Gets the primary key.
     * 
     * @return the primary key
     */
    @Nullable
    public Long getId();

    /**
     * Sets the primary key.
     * 
     * @param id the new primary key
     */
    public void setId(@Nullable Long id);

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @Nonnull
    public String getName();

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(@Nonnull String name);

    /**
     * Gets the default parent category. This method will delegate to
     * {@link #getParentCategory()} by default, unless the "use.legacy.default.category.mode" property is set to
     * true in the implementation's property file. If set to true, this method will use legacy behavior,
     * which is to return the deprecated defaultParentCategory field.
     *
     * @deprecated use {@link #getParentCategory()} instead
     * @return the default parent category
     */
    @Deprecated
    @Nullable
    public Category getDefaultParentCategory();

    /**
     * Sets the default parent category. This method will delegate to
     * {@link #setParentCategory(Category)} by default, unless the "use.legacy.default.category.mode" property is set to
     * true in the implementation's property file. If set to true, this method will use legacy behavior,
     * which is to set the deprecated defaultParentCategory field.
     *
     * @deprecated use {@link #setParentCategory(Category)} instead
     * @param defaultParentCategory the new default parent category
     */
    @Deprecated
    public void setDefaultParentCategory(@Nullable Category defaultParentCategory);

    /**
     * Return the category that is the parent of this category - if applicable
     *
     * @return
     */
    Category getParentCategory();

    /**
     * Return the parent category xref of the default parent or first applicable parent
     *
     * @return
     */
    CategoryXref getParentCategoryXref();

    /**
     * Set the parent category of this category
     *
     * @param category
     */
    void setParentCategory(Category category);

    /**
     * Gets the url. The url represents the presentation layer destination for
     * this category. For example, if using Spring MVC, you could send the user
     * to this destination by returning {@code "redirect:"+currentCategory.getUrl();}
     * from a controller.
     * 
     * @return the url for the presentation layer component for this category
     */
    @Nullable
    public String getUrl();

    /**
     * Sets the url. The url represents the presentation layer destination for
     * this category. For example, if using Spring MVC, you could send the user
     * to this destination by returning {@code "redirect:"+currentCategory.getUrl();}
     * from a controller.
     * 
     * @param url the new url for the presentation layer component for this category
     */
    public void setUrl(@Nullable String url);

    /**
     * @return the flag for whether or not the URL should not be generated in the admin
     */
    public Boolean getOverrideGeneratedUrl();

    /**
     * Sets the flag for whether or not the URL should not be generated in the admin
     * 
     * @param overrideGeneratedUrl
     */
    public void setOverrideGeneratedUrl(Boolean overrideGeneratedUrl);

    /**
     * Gets the url key. The url key is used as part of SEO url generation for this
     * category. Each segment of the url leading to a category is comprised of the url
     * keys of the various associated categories in a hierarchy leading to this one. If
     * the url key is null, the the name for the category formatted with dashes for spaces.
     * 
     * @return the url key for this category to appear in the SEO url
     */
    @Nullable
    public String getUrlKey();

    /**
     * Creates the SEO url starting from this category and recursing up the
     * hierarchy of default parent categories until the topmost category is
     * reached. The url key for each category is used for each segment
     * of the SEO url.
     * 
     * @return the generated SEO url for this category
     */
    @Nullable
    public String getGeneratedUrl();

    /**
     * Sets the url key. The url key is used as part of SEO url generation for this
     * category. Each segment of the url leading to a category is comprised of the url
     * keys of the various associated categories in a hierarchy leading to this one.
     * 
     * @param urlKey the new url key for this category to appear in the SEO url
     */
    public void setUrlKey(@Nullable String urlKey);

    /**
     * Gets the description.
     * 
     * @return the description
     */
    @Nullable
    public String getDescription();

    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(@Nullable String description);

    /**
     * Gets the active start date. If the current date is before activeStartDate,
     * then this category will not be visible on the site.
     * 
     * @return the active start date
     */
    @Nullable
    public Date getActiveStartDate();

    /**
     * Sets the active start date. If the current date is before activeStartDate,
     * then this category will not be visible on the site.
     * 
     * @param activeStartDate the new active start date
     */
    public void setActiveStartDate(@Nullable Date activeStartDate);

    /**
     * Gets the active end date. If the current date is after activeEndDate,
     * the this category will not be visible on the site.
     * 
     * @return the active end date
     */
    @Nullable
    public Date getActiveEndDate();

    /**
     * Sets the active end date. If the current date is after activeEndDate,
     * the this category will not be visible on the site.
     * 
     * @param activeEndDate the new active end date
     */
    public void setActiveEndDate(@Nullable Date activeEndDate);

    /**
     * Checks if is active. Returns true if the startDate is null or if the current
     * date is after the start date, or if the endDate is null or if the current date
     * is before the endDate.
     * 
     * @return true, if is active
     */
    public boolean isActive();

    /**
     * Gets the display template. The display template can be used to help create a unique key
     * that drives the presentation layer destination for this category. For example, if
     * using Spring MVC, you might derive the view destination in this way:
     *
     * {@code view = categoryTemplatePrefix + currentCategory.getDisplayTemplate();}
     * 
     * @return the display template
     */
    @Nullable
    public String getDisplayTemplate();

    /**
     * Sets the display template. The display template can be used to help create a unique key
     * that drives the presentation layer destination for this category. For example, if
     * using Spring MVC, you might derive the view destination in this way:
     *
     * {@code view = categoryTemplatePrefix + currentCategory.getDisplayTemplate();}
     * 
     * @param displayTemplate the new display template
     */
    public void setDisplayTemplate(@Nullable String displayTemplate);

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
    public Map<String,List<Long>> getChildCategoryURLMap();

    /**
     * Set the child category url map. This approach is inefficient,
     * so its use is highly discouraged.
     *
     * @param childCategoryURLMap
     * @deprecated This approach is inherently inefficient and should no longer be used
     */
    @Deprecated
    public void setChildCategoryURLMap(@Nonnull Map<String, List<Long>> childCategoryURLMap);

    /**
     * Gets the category media map. The key is of arbitrary meaning
     * and the {@code Media} instance stores information about the
     * media itself (image url, etc...)
     * 
     * @deprecated use {@link #getCategoryMediaXref()} instead
     * @return the category Media
     */
    @Nonnull
    @Deprecated
    public Map<String, Media> getCategoryMedia() ;

    /**
     * Sets the category media. The key is of arbitrary meaning
     * and the {@code Media} instance stores information about the
     * media itself (image url, etc...)
     * 
     * @deprecated use {@link #setCategoryMediaXref(Map)} instead
     * @param categoryMedia the category media
     */
    @Deprecated
    public void setCategoryMedia(@Nonnull Map<String, Media> categoryMedia);

    /**
     * Gets the category media map. The key is of arbitrary meaning
     * and the {@code Media} instance stores information about the
     * media itself (image url, etc...)
     * 
     * @return the category Media
     */
    public Map<String, CategoryMediaXref> getCategoryMediaXref();

    /**
     * Sets the category media. The key is of arbitrary meaning
     * and the {@code Media} instance stores information about the
     * media itself (image url, etc...)
     * 
     * @param categoryMedia the category media
     */
    public void setCategoryMediaXref(Map<String, CategoryMediaXref> categoryMediaXref);

    /**
     * Gets the long description.
     * 
     * @return the long description
     */
    @Nullable
    public String getLongDescription();

    /**
     * Sets the long description.
     * 
     * @param longDescription the new long description
     */
    public void setLongDescription(@Nullable String longDescription);

    /**
     * Gets the featured products. Featured products are a special list
     * of products you would like to showcase for this category.
     * 
     * @return the featured products
     */
    @Nonnull
    public List<FeaturedProduct> getFeaturedProducts();

    /**
     * Sets the featured products. Featured products are a special list
     * of products you would like to showcase for this category.
     * 
     * @param featuredProducts the featured products
     */
    public void setFeaturedProducts(@Nonnull List<FeaturedProduct> featuredProducts);

    /**
     * Returns a list of cross sale products that are related to this category.
     * 
     * @return a list of cross sale products
     */
    public List<RelatedProduct> getCrossSaleProducts();

    /**
     * Sets the cross sale products that are related to this category.
     * 
     * @see #getCrossSaleProducts()
     * @param crossSaleProducts
     */
    public void setCrossSaleProducts(List<RelatedProduct> crossSaleProducts);

    /**
     * Returns a list of cross sale products that are related to this category.
     * 
     * @return a list of cross sale products
     */
    public List<RelatedProduct> getUpSaleProducts();

    /**
     * Sets the upsale products that are related to this category.
     * 
     * @see #getUpSaleProducts()
     * @param upSaleProducts
     */
    public void setUpSaleProducts(List<RelatedProduct> upSaleProducts);

    /** 
     * Returns a list of the cross sale products in this category as well as
     * all cross sale products in all parent categories of this category.
     * 
     * @return the cumulative cross sale products
     */
    public List<RelatedProduct> getCumulativeCrossSaleProducts();
    
    /** 
     * Returns a list of the upsale products in this category as well as
     * all upsale products in all parent categories of this category.
     * 
     * @return the cumulative upsale products
     */
    public List<RelatedProduct> getCumulativeUpSaleProducts();

    /**
     * Returns a list of the featured products in this category as well as
     * all featured products in all parent categories of this category.
     * 
     * @return the cumulative featured products
     */
    public List<FeaturedProduct> getCumulativeFeaturedProducts();

    /**
     * Returns all of the SearchFacets that are directly associated with this Category
     * 
     * @return related SearchFacets
     */
    public List<CategorySearchFacet> getSearchFacets();

    /**
     * Sets the SearchFacets that are directly associated with this Category
     * 
     * @param searchFacets
     */
    public void setSearchFacets(List<CategorySearchFacet> searchFacets);

    /**
     * Sets the SearchFacets that should not be rendered by this Category. Typically, this will include
     * facets from parent categories that do not apply to this category.
     * 
     * @param excludedSearchFacets
     */
    public void setExcludedSearchFacets(List<CategoryExcludedSearchFacet> excludedSearchFacets);

    /**
     * Gets the excluded SearchFacets
     * @return the excluded SearchFacets
     */
    public List<CategoryExcludedSearchFacet> getExcludedSearchFacets();

    /**
     * Returns a list of CategorySearchFacets that takes into consideration the search facets for this Category,
     * the search facets for all parent categories, and the search facets that should be excluded from this 
     * Category. This method will order the resulting list based on the {@link CategorySearchFacet#getPosition()}
     * method for each category level. That is, the facets on this Category will be ordered by their position
     * relative to each other with the ordered parent facets after that, etc.
     * 
     * @return the current active search facets for this category and all parent categories
     */
    public List<CategorySearchFacet> getCumulativeSearchFacets();

    /**
     * Returns a list of CategorySearchFacets that takes into consideration the search facets for this Category,
     * the search facets for all parent categories, and the search facets that should be excluded from this
     * Category. This method will order the resulting list based on the {@link CategorySearchFacet#getPosition()}
     * method for each category level. That is, the facets on this Category will be ordered by their position
     * relative to each other with the ordered parent facets after that, etc.
     *
     * Takes a Set of the categories that have been traversed in order to protect from circular dependencies.
     *
     * @param categoryHierarchy
     * @return the current active search facets for this category and all parent categories     *
     */
    public List<CategorySearchFacet> getCumulativeSearchFacets(Set<Category> categoryHierarchy);
    
    /**
     * Build category hierarchy by walking the default category tree up to the root category.
     * If the passed in tree is null then create the initial list.
     * 
     * @param currentHierarchy
     * @return
     */
    
    public List<Category> buildDefaultParentCategoryPath(List<Category> currentHierarchy);
    
    /**
     * Build the full category hierarchy by walking up the default category tree and the all parent
     * category tree.
     * 
     * @param currentHierarchy
     * @return the full hierarchy
     */
    public List<Category> buildParentCategoryPath(List<Category> currentHierarchy);

    /**
     * Build the full category hierarchy by walking up the default category tree and the all parent
     * category tree.  Adds the option of only adding the first parent found.
     *
     * @param currentHierarchy
     * @param firstParent determines if ONLY the first parent category should be returned per hierarchy tier
     * @return the full hierarchy
     */
    public List<Category> buildParentCategoryPath(List<Category> currentHierarchy, Boolean firstParent);

    /**
     * Gets the attributes for this {@link Category}. In smaller sites, using these attributes might be preferred to
     * extending the domain object itself.
     * 
     * @return
     * @see {@link #getMappedCategoryAttributes()}
     */
    public Map<String, CategoryAttribute> getCategoryAttributesMap();

    public void setCategoryAttributesMap(Map<String, CategoryAttribute> categoryAttributes);
    
    /**
     * Gets the attributes for this {@link Category}. In smaller sites, using these attributes might be preferred to
     * extending the domain object itself.
     * 
     * @return
     * @see {@link #getMappedCategoryAttributes()}
     * @deprecated This will be replaced with {@link #getCategoryAttributesMap()} in 3.1.0.
     */
    @Deprecated
    public List<CategoryAttribute> getCategoryAttributes();

    /**
     * Sets the attributes for this {@link Category}. In smaller sites, using these attributes might be preferred to
     * extending the domain object and creating a new table to store custom properties.
     * 
     * @return
     * @deprecated This will be replaced with {@link #setCategoryAttributesMap()} in 3.1.0.
     */
    @Deprecated
    public void setCategoryAttributes(List<CategoryAttribute> categoryAttributes);

    /**
     * Convenience method to get a {@link CategoryAttribute} by name
     * 
     * @param name
     * @return
     * @see {@link #getCategoryAttributes()}, {@link #getMappedCategoryAttributes()}
     * @deprecated This will be removed in 3.1.0
     */
    @Deprecated
    public CategoryAttribute getCategoryAttributeByName(String name);

    /**
     * Convenience method to return the {@link CategoryAttribute}s for the {@link Category} in an easily-consumable
     * form
     * 
     * @return
     * @deprecated This will be removed in 3.1.0
     */
    @Deprecated
    public Map<String, CategoryAttribute> getMappedCategoryAttributes();

    Map<String, CategoryAttribute> getMultiValueCategoryAttributes();

    /**
     * Used to determine availability for all of the products/skus in this category
     * @return the {@link InventoryType} for this category
     */
    public InventoryType getInventoryType();

    /**
     * Sets the type of inventory for this category
     * @param inventoryType the {@link InventoryType} for this category
     */
    public void setInventoryType(InventoryType inventoryType);
    
    /**
     * Returns the default fulfillment type for skus in this category. May be null.
     * @return
     */
    public FulfillmentType getFulfillmentType();
    
    /**
     * Sets the default fulfillment type for skus in this category. May return null.
     * @param fulfillmentType
     */
    public void setFulfillmentType(FulfillmentType fulfillmentType);

    /**
     * Gets the child categories. This list includes all categories, regardless
     * of whether or not they are active.
     *
     * @deprecated use getAllChildCategoryXrefs() instead.
     * @return the list of active and inactive child categories.
     */
    @Nonnull
    @Deprecated
    public List<Category> getAllChildCategories();

    /**
     * Checks for child categories.
     *
     * @return true, if this category has any children (active or not)
     */
    public boolean hasAllChildCategories();

    /**
     * Sets the list of child categories (active and inactive)
     *
     * @deprecated Use setAllChildCategoryXrefs() instead.
     * @param childCategories the list of child categories
     */
    @Deprecated
    public void setAllChildCategories(@Nonnull List<Category> childCategories);

    /**
     * Gets the child categories. If child categories has not been previously
     * set, then the list of active only categories will be returned.
     *
     * @deprecated Use getChildCategoryXrefs() instead.
     * @return the list of active child categories
     */
    @Deprecated
    @Nonnull
    public List<Category> getChildCategories();

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
    public List<Long> getChildCategoryIds();

    /**
     * Sets the all child category ids. This should be a list
     * of active only child categories.
     *
     * @param childCategoryIds the list of active child category ids.
     */
    public void setChildCategoryIds(@Nonnull List<Long> childCategoryIds);

    /**
     * Checks for child categories.
     *
     * @return true, if this category contains any active child categories.
     */
    public boolean hasChildCategories();

    /**
     * Sets the all child categories. This should be a list
     * of active only child categories.
     *
     * @deprecated Use setChildCategoryXrefs() instead.
     * @param childCategories the list of active child categories.
     */
    @Deprecated
    public void setChildCategories(@Nonnull List<Category> childCategories);

    public List<CategoryXref> getAllChildCategoryXrefs();

    public List<CategoryXref> getChildCategoryXrefs();

    public void setChildCategoryXrefs(List<CategoryXref> childCategories);

    public void setAllChildCategoryXrefs(List<CategoryXref> childCategories);

    /**
     * Retrieve all the xref entities linking this category to parent categories
     */
    public List<CategoryXref> getAllParentCategoryXrefs();

    /**
     * Set all the xref entities linking this product to parent categories
     */
    public void setAllParentCategoryXrefs(List<CategoryXref> allParentCategories);

    /**
     * Retrieve all parent categories
     *
     * @deprecated Use getAllParentCategoryXrefs() instead.
     * @return the list of parent categories
     */
    @Deprecated
    @Nonnull
    public List<Category> getAllParentCategories();

    /**
     * Sets the list of parent categories
     *
     * @deprecated Use setAllParentCategoryXrefs() instead.
     * @param allParentCategories the list of parent categories
     */
    @Deprecated
    public void setAllParentCategories(@Nonnull List<Category> allParentCategories);

    public List<CategoryProductXref> getActiveProductXrefs();

    public List<CategoryProductXref> getAllProductXrefs();

    public void setAllProductXrefs(List<CategoryProductXref> allProducts);

    /**
     * Convenience method to retrieve all of this {@link Category}'s {@link Product}s filtered by
     * active. If you want all of the {@link Product}s (whether inactive or not) consider using
     * {@link #getAllProducts()}.
     *
     * @deprecated Use getActiveProductXrefs() instead.
     * @return the list of active {@link Product}s for this {@link Category}
     * @see {@link Product#isActive()}
     */
    @Deprecated
    public List<Product> getActiveProducts();

    /**
     * Retrieve all the {@code Product} instances associated with this
     * category.
     * <br />
     * <b>Note:</b> this method does not take into account whether or not the {@link Product}s are active or not. If
     * you need this functionality, use {@link #getActiveProducts()}
     * @deprecated Use getAllProductXrefs() instead.
     * @return the list of products associated with this category.
     */
    @Deprecated
    @Nonnull
    public List<Product> getAllProducts();

    /**
     * Set all the {@code Product} instances associated with this
     * category.
     *
     * @deprecated Use setAllProductXrefs() instead.
     * @param allProducts the list of products to associate with this category
     */
    @Deprecated
    public void setAllProducts(@Nonnull List<Product> allProducts);

    /**
     * Returns the tax code of this category.
     * @return taxCode
     */
    public String getTaxCode();

    /**
     * Sets the tax code of this category.
     * @param taxCode
     */
    public void setTaxCode(String taxCode);

    /**
     * Intended to hold any unique identifier not tied to the Broadleaf Database Sequence Identifier.
     * For example, many implementations may integrate or import/export
     * data from other systems that manage their own unique identifiers.
     *
     * @return external ID
     */
    public String getExternalId();

    /**
     * Sets a unique external ID
     * @param externalId
     */
    public void setExternalId(String externalId);
}
