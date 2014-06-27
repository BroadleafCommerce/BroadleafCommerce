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
/*
 * 
 */
package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementations of this interface are used to hold data about a Category.  A category is a group of products.
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to how the
 * Category is persisted.  If you just want to add additional fields then you should extend {@link CategoryImpl}.
 *
 * @see {@link CategoryImpl}
 * @author btaylor
 * 
 */

public interface Category extends Serializable {

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public Long getId();

    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    public void setId(Long id);

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName();

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name);

    /**
     * Gets the default parent category.
     * 
     * @return the default parent category
     */
    public Category getDefaultParentCategory();

    /**
     * Sets the default parent category.
     * 
     * @param defaultParentCategory the new default parent category
     */
    public void setDefaultParentCategory(Category defaultParentCategory);

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl();

    /**
     * Sets the url.
     * 
     * @param url the new url
     */
    public void setUrl(String url);

    /**
     * Gets the url key.
     * 
     * @return the url key
     */
    public String getUrlKey();

    /**
     * Gets the generated url.
     * 
     * @return the generated url
     */
    public String getGeneratedUrl();

    /**
     * Sets the url key.
     * 
     * @param urlKey the new url key
     */
    public void setUrlKey(String urlKey);

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription();

    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description);

    /**
     * Gets the active start date.
     * 
     * @return the active start date
     */
    public Date getActiveStartDate();

    /**
     * Sets the active start date.
     * 
     * @param activeStartDate the new active start date
     */
    public void setActiveStartDate(Date activeStartDate);

    /**
     * Gets the active end date.
     * 
     * @return the active end date
     */
    public Date getActiveEndDate();

    /**
     * Sets the active end date.
     * 
     * @param activeEndDate the new active end date
     */
    public void setActiveEndDate(Date activeEndDate);

    /**
     * Checks if is active.
     * 
     * @return true, if is active
     */
    public boolean isActive();

    /**
     * Gets the display template.
     * 
     * @return the display template
     */
    public String getDisplayTemplate();

    /**
     * Sets the display template.
     * 
     * @param displayTemplate the new display template
     */
    public void setDisplayTemplate(String displayTemplate);

    /**
     * Gets the child category url map.
     * 
     * @return the child category url map
     */
    public Map<String,List<Category>> getChildCategoryURLMap();

    /**
     * Gets the child categories.
     * 
     * @return the child categories
     */
    public List<Category> getChildCategories();

    List<Category> getAllChildCategories();

    /**
     * Checks for child categories.
     * 
     * @return true, if successful
     */
    public boolean hasChildCategories();

    /**
     * Sets the all child categories.
     * 
     * @param allChildCategories the new all child categories
     */
    public void setChildCategories(List<Category> childCategories);

    /**
     * Gets the category images.
     * 
     * @return the category images
     */
    public Map<String, String> getCategoryImages();

    /**
     * Gets the category image.
     * 
     * @param imageKey the image key
     * 
     * @return the category image
     */
    public String getCategoryImage(String imageKey);

    /**
     * Sets the category images.
     * 
     * @param categoryImages the category images
     */
    public void setCategoryImages(Map<String, String> categoryImages);

    /**
     * Gets the long description.
     * 
     * @return the long description
     */
    public String getLongDescription();

    /**
     * Sets the long description.
     * 
     * @param longDescription the new long description
     */
    public void setLongDescription(String longDescription);

    /**
     * Gets the featured products.
     * 
     * @return the featured products
     */
    public List<FeaturedProduct> getFeaturedProducts();

    /**
     * Sets the featured products.
     * 
     * @param featuredProducts the featured products
     */
    public void setFeaturedProducts(List<FeaturedProduct> featuredProducts);
}
