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
package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.util.money.Money;
/**
 * Implementations of this interface are used to hold data about a SKU.  A SKU is
 * a specific item that can be sold including any specific attributes of the item such as
 * color or size.
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to how the
 * class is persisted.  If you just want to add additional fields then you should extend {@link SkuImpl}.
 *
 * @see {@link SkuImpl}, {@link Money}
 * @author btaylor
 *
 */
public interface Sku extends Serializable {

    /**
     * Returns the id of this sku
     */
    public Long getId();

    /**
     * Sets the id of this sku
     */
    public void setId(Long id);

    /**
     * Returns the Sale Price of the Sku.  The Sale Price is the standard price the vendor sells
     * this item for.
     */
    public Money getSalePrice();

    /**
     * Sets the the Sale Price of the Sku.  The Sale Price is the standard price the vendor sells
     * this item for.
     */
    public void setSalePrice(Money salePrice);

    /**
     * Returns the Retail Price of the Sku.  The Retail Price is the MSRP of the sku.
     */
    public Money getRetailPrice();

    public void setRetailPrice(Money retailPrice);

    /**
     * Returns the List Price of the Sku.  The List Price is the MSRP of the sku.
     * @deprecated
     */
    public Money getListPrice();

    /**
     * Sets the the List Price of the Sku.  The List Price is the MSRP of the sku.
     * @deprecated
     */
    public void setListPrice(Money listPrice);

    /**
     * Returns the name of the Sku.  The name is a label used to show when displaying the sku.
     */
    public String getName();

    /**
     * Sets the the name of the Sku.  The name is a label used to show when displaying the sku.
     */
    public void setName(String name);

    /**
     * Returns the brief description of the Sku.
     */
    public String getDescription();

    /**
     * Sets the brief description of the Sku.
     */
    public void setDescription(String description);

    /**
     * Returns the long description of the sku.
     */
    public String getLongDescription();

    /**
     * Sets the long description of the sku.
     */
    public void setLongDescription(String longDescription);

    /**
     * Returns whether the Sku qualifies for taxes or not.  This field is used by the pricing engine
     * to calculate taxes.
     */
    public Boolean isTaxable();

    /**
     * Sets the whether the Sku qualifies for taxes or not.  This field is used by the pricing engine
     * to calculate taxes.
     */
    public void setTaxable(Boolean taxable);

    /**
     * Returns whether the Sku qualifies for discounts or not.  This field is used by the pricing engine
     * to apply offers.
     */
    public Boolean isDiscountable();

    /**
     * Sets the whether the Sku qualifies for discounts or not.  This field is used by the pricing engine
     * to apply offers.
     */
    public void setDiscountable(Boolean discountable);

    /**
     * Returns whether the Sku is available.
     */
    public Boolean isAvailable();

    /**
     * Sets the whether the Sku is available.
     */
    public void setAvailable(Boolean available);

    /**
     * Returns the first date that the Sku should be available for sale.  This field is used to determine
     * whether a user can add the sku to their cart.
     */
    public Date getActiveStartDate();

    /**
     * Sets the the first date that the Sku should be available for sale.  This field is used to determine
     * whether a user can add the sku to their cart.
     */
    public void setActiveStartDate(Date activeStartDate);

    /**
     * Returns the the last date that the Sku should be available for sale.  This field is used to determine
     * whether a user can add the sku to their cart.
     */
    public Date getActiveEndDate();

    /**
     * Sets the the last date that the Sku should be available for sale.  This field is used to determine
     * whether a user can add the sku to their cart.
     */
    public void setActiveEndDate(Date activeEndDate);

    /**
     * Returns a boolean indicating whether this sku is active.  This is used to determine whether a user
     * the sku can add the sku to their cart.
     */
    public boolean isActive();

    /**
     * Returns a map of key/value pairs where the key is a string for the name of an image and the value
     * is a string to the URL of the image.  This is used to display images while browsing the sku.
     */
    public Map<String, String> getSkuImages();

    /**
     * Returns the default image used for the Sku.
     */
    public String getSkuImage(String imageKey);

    /**
     * Sets a map of key/value pairs where the key is a string for the name of an image and the value
     * is a string to the URL of the image.  This is used to display images while browsing the sku.
     */
    public void setSkuImages(Map<String, String> skuImages);

    /**
     * Get all the parent products since a sku can exist in multiple
     */
    public List<Product> getAllParentProducts();

    /**
     * Set all the parent products since a sku can exist in multiple
     */
    public void setAllParentProducts(List<Product> allParentProducts);

    public boolean isActive(Product product, Category category);
}
