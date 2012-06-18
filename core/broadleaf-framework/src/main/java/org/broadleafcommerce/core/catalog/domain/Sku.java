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

package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.media.domain.Media;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
     * this item for.  If {@link SkuPricingConsiderationContext} is set, this uses the DynamicSkuPricingService
     * to calculate what this should actually be rather than use the property itself
     * 
     * @see SkuPricingConsiderationContext, DynamicSkuPricingService
     */
    public Money getSalePrice();

    /**
     * Sets the the Sale Price of the Sku.  The Sale Price is the standard price the vendor sells
     * this item for. This price will automatically be overridden if your system is utilizing
     * the DynamicSkuPricingService.
     */
    public void setSalePrice(Money salePrice);

    /**
     * Returns the Retail Price of the Sku.  The Retail Price is the MSRP of the sku. If {@link SkuPricingConsiderationContext}
     * is set, this uses the DynamicSkuPricingService to calculate what this should actually be rather than use the property
     * itself
     * 
     * @see SkuPricingConsiderationContext, DynamicSkuPricingService
     */
    public Money getRetailPrice();

    /**
     * Sets the retail price for the Sku. This price will automatically be overridden if your system is utilizing
     * the DynamicSkuPricingService.
     * 
     * @param retail price for the Sku
     */
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
     * Convenience that passes through to isTaxable
     */
    public Boolean getTaxable();

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
     * Convenience that passes through to isAvailable
     */
    public Boolean getAvailable();
    
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
     * Get the dimensions for this Sku
     * 
     * @return this Sku's embedded Weight
     */
    public Dimension getDimension();

    /**
     * Sets the embedded Dimension for this Sku
     * 
     * @param dimension
     */
    public void setDimension(Dimension dimension);

    /**
     * Gets the embedded Weight for this Sku
     * 
     * @return this Sku's embedded Weight
     */
    public Weight getWeight();

    /**
     * Sets the embedded Weight for this Sku
     * 
     * @param weight
     */
    public void setWeight(Weight weight);
    
    /**
     * Returns a boolean indicating whether this sku is active.  This is used to determine whether a user
     * the sku can add the sku to their cart.
     */
    public boolean isActive();

    /**
     * Returns a map of key/value pairs where the key is a string for the name of an image and the value
     * is a string to the URL of the image.  This is used to display images while browsing the sku.
     * This method is deprecated. Use getSkuMedia instead.
     */
    @Deprecated
    public Map<String, String> getSkuImages();

    /**
     * Returns the default image used for the Sku.
     */
    @Deprecated
    public String getSkuImage(String imageKey);

    /**
     * Sets a map of key/value pairs where the key is a string for the name of an image and the value
     * is a string to the URL of the image.  This is used to display images while browsing the sku.
     * This method is deprecated. Use setSkuMedia instead.
     */
    @Deprecated
    public void setSkuImages(Map<String, String> skuImages);

    /**
     * Returns a map of key/value pairs where the key is a string for the name of a media object and the value
     * is a media object.
     */
    public Map<String, Media> getSkuMedia();

    /**
     * Sets a map of key/value pairs where the key is a string for the name of a media object and the value
     * is an object of type Media.
     */
    public void setSkuMedia(Map<String, Media> skuMedia);

    /**
     * Returns whether or not this Sku, the given Product and the given Category are all active
     * 
     * @param product - Product that should be active
     * @param category - Category that should be active
     * @return <b>true</b> if this Sku, <code>product</code> and <code>category</code> are all active
     * <b>false</b> otherwise
     */
    public boolean isActive(Product product, Category category);

    /**
     * Denormalized set of key-value pairs to attach to a Sku. If you are looking for setting up
     * a {@link ProductOption} scenario (like colors, sizes, etc) see {@link getProductOptionValues()}
     * and {@link setProductOptionValues()}
     * 
     * @return the attributes for this Sku
     */
    public List<SkuAttribute> getSkuAttributes();

    /**
     * Sets the denormalized set of key-value pairs on a Sku
     * 
     * @param skuAttributes
     */
    public void setSkuAttributes(List<SkuAttribute> skuAttributes);

    /**
     * Gets the ProductOptionValues used to map to this Sku. For instance, this Sku could hold specific
     * inventory, price and image information for a "Blue" "Extra-Large" shirt
     * 
     * @return the ProductOptionValues for this Sku
     * @see {@link ProductOptionValue}, {@link ProductOption}
     */
	public List<ProductOptionValue> getProductOptionValues();

	/**
	 * Sets the ProductOptionValues that should be mapped to this Sku
	 * 
	 * @param productOptionValues
	 * @see {@link ProductOptionValue}, {@link ProductOption}
	 */
    public void setProductOptionValues(List<ProductOptionValue> productOptionValues);

    /**
     * This will be a value if and only if this Sku is the defaultSku of a Product (and thus has a @OneToOne relationship with a Product).
     * The mapping for this is actually done at the Product level with a foreign key to Sku; this exists for convenience to get the reverse relationship
     * 
     * @return The associated Product if this Sku is a defaultSku, <b>null</b> otherwise
     * @see #getProduct()
     */
    public Product getDefaultProduct();

    /**
     * The relationship for a Product's default Sku (and thus a Sku's default Product) is actually maintained
     * on the Product entity as a foreign key to Sku.  Because of this, there are probably very few circumstances
     * that you would actually want to change this from the Sku perspective instead of the Product perspective.
     * <br />
     * <br />
     * If you are looking for a way to simply associate a Sku to a Product, the correct way would be to call
     * {@link #setProduct(Product)} or {@link Product#setSkus(List<Sku>)} which would then cause this Sku to show up in the list of Skus for
     * the given Product
     * 
     * @param product
     */
    public void setDefaultProduct(Product product);

    /**
     * This will return the correct Product association that is being used on the Sku. If this Sku is a default Sku
     * for a Product (as in, {@link #getDefaultProduct()} != null) than this will return {@link #getDefaultProduct()}. If this is not
     * a default Sku for a Product, this will return the @ManyToOne Product relationship created by adding this Sku to a Product's
     * list of Skus, or using {@link setProduct(Product)}.
     * <br />
     * <br />
     * In some implementations, it might make sense to have both the @OneToOne association set ({@link Product#setDefaultSku(Sku)})
     * as well as the @ManyToOne association set ({@link #setProduct(Product)}). In this case, This method would only return
     * the result of {@link #getDefaultProduct()}.  However, the @OneToOne and @ManyToOne association should never actually
     * refer to different Products, and would represent an error state. If you require this, consider subclassing and using
     * your own @ManyToMany relationship between Product and Sku. If you are trying to model bundles, consider using a {@link ProductBundle}
     * and subsequent {@link SkuBundleItem}s.
     * 
     * @return {@link #getDefaultProduct()} if {@link #getDefaultProduct()} is non-null, the @ManyToOne Product association otherwise. If no
     * relationship is set, returns null
     */
    public Product getProduct();

    /**
     * Associates a Sku to a given Product. This will then show up in the list returned by {@link Product#getSkus()}
     * 
     * @param product - Product to associate this Sku to
     * @see Product#getSkus()
     */
    public void setProduct(Product product);

    /**
     * A product is on sale provided the sale price is not null, non-zero, and less than the retail price
     * 
     * @return whether or not the product is on sale
     */
	public boolean isOnSale();

	/**
	 * Whether this Sku can be sorted by a machine
	 * 
	 * @return <b>true</b> if this Sku can be sorted by a machine
	 */
    public Boolean isMachineSortable();

    /**
     * Sets whether or not this Sku can be sorted by a machine
     * 
     * @param isMachineSortable
     */
    public void setMachineSortable(Boolean isMachineSortable);

}
