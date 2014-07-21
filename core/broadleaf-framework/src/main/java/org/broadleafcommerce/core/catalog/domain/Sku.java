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

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.order.service.workflow.CheckAvailabilityActivity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * Returns the sku specific portion of a full url used for a sku info page.
     */ 
    public String getUrlKey();

    /**
     * Sets the sku specific portion of a full url used for a sku info page.  
     * 
     * @param url
     */
    public void setUrlKey(String url);

    /**
     * Returns the name of a display template that is used to render this sku.  Most implementations have a default
     * template for all skus.  This allows for the user to define a specific template to be used by this sku.
     * 
     * @return
     */
    public String getDisplayTemplate();

    /**
     * Sets the name of a display template that is used to render this sku.  Most implementations have a default
     * template for all skus.  This allows for the user to define a specific template to be used by this sku.
     * 
     * @param displayTemplate
     */
    public void setDisplayTemplate(String displayTemplate);
    /**
     * This is the sum total of the priceAdjustments from the associated ProductOptionValues
     * 
     * @return <b>null</b> if there are no ProductOptionValues associated with this Sku or
     * all of their priceAdjustments are null. Otherwise this will be the sum total of those price
     * adjustments
     * 
     * @see {@link ProductOptionValue}
     */
    public Money getProductOptionValueAdjustments();
    
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
     * Determines if there is a sale price.  In other words, determines whether salePrice is null. Returns true if 
     * salePrice is not null.  Returns false otherwise.
     * @return
     */
    public boolean hasSalePrice();

    /**
     * Returns the Retail Price of the Sku.  The Retail Price is the MSRP of the sku. If {@link SkuPricingConsiderationContext}
     * is set, this uses the DynamicSkuPricingService to calculate what this should actually be rather than use the property
     * itself.
     * 
     * @throws IllegalStateException if retail price is null. 
     * 
     * @see SkuPricingConsiderationContext, DynamicSkuPricingService, Sku.hasRetailPrice()
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
     * Provides a way of determining if a Sku has a retail price without getting an IllegalStateException. Returns true if 
     * retailPrice is not null.  Returns false otherwise.
     * @see Sku.getRetailPrice()
     * @return
     */
    public boolean hasRetailPrice();

    /**
     * Resolves the price of the Sku. If the Sku is on sale (that is, isOnSale() returns true), the
     * return value will be the result of getSalePrice(). Otherwise, the return value will be the result of
     * getRetailPrice().
     * @return the price of the Sku
     */
    public Money getPrice();

    /**
     * Returns the List Price of the Sku.  The List Price is the MSRP of the sku.
     * @deprecated
     */
    @Deprecated
    public Money getListPrice();

    /**
     * Sets the the List Price of the Sku.  The List Price is the MSRP of the sku.
     * @deprecated
     */
    @Deprecated
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
     * <p>Availability is really a concern of inventory vs a concern of the Sku being active or not. A Sku could be marked as
     * unavailable but still be considered 'active' where you still want to show the Sku on the site but not actually sell
     * it. This defaults to true</p>
     * 
     * <p>This method only checks that this Sku is not marked as {@link InventoryType#UNAVAILABLE}. If {@link #getInventoryType()}
     * is set to {@link InventoryType#CHECK_QUANTITY} then this will return true.</p>
     * 
     * @deprecated use {@link #getInventoryType()} or {@link InventoryService#isAvailable(Sku, int)} instead.
     */
    @Deprecated
    public Boolean isAvailable();

    /**
     * Convenience that passes through to isAvailable
     * @see {@link #isAvailable()}
     * @deprecated use {@link #getInventoryType()} instead
     */
    @Deprecated
    public Boolean getAvailable();
    
    /**
     * Availability is really a concern of inventory vs a concern of the Sku being active or not. A Sku could be marked as
     * unavailable but still be considered 'active' where you still want to show the Sku on the site but not actually sell
     * it. This defaults to true
     * 
     * @deprecated use {@link #setInventoryType(InventoryType)} instead
     */
    @Deprecated
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
    public Map<String, SkuAttribute> getSkuAttributes();

    /**
     * Sets the denormalized set of key-value pairs on a Sku
     *
     * @param skuAttributes
     */
    public void setSkuAttributes(Map<String, SkuAttribute> skuAttributes);

    /**
     * Gets the ProductOptionValues used to map to this Sku. For instance, this Sku could hold specific
     * inventory, price and image information for a "Blue" "Extra-Large" shirt
     * 
     * @return the ProductOptionValues for this Sku
     * @see {@link ProductOptionValue}, {@link ProductOption}
     * @deprecated use {@link #getProductOptionValuesCollection()} instead
     */
    @Deprecated
    public List<ProductOptionValue> getProductOptionValues();

    /**
     * Sets the ProductOptionValues that should be mapped to this Sku
     * 
     * @param productOptionValues
     * @see {@link ProductOptionValue}, {@link ProductOption}
     * @deprecated use {@link #setProductOptionValuesCollection(java.util.Set)} instead
     */
    @Deprecated
    public void setProductOptionValues(List<ProductOptionValue> productOptionValues);

    /**
     * Gets the ProductOptionValues used to map to this Sku. For instance, this Sku could hold specific
     * inventory, price and image information for a "Blue" "Extra-Large" shirt
     *
     * @return the ProductOptionValues for this Sku
     * @see {@link ProductOptionValue}, {@link ProductOption}
     */
    Set<ProductOptionValue> getProductOptionValuesCollection();

    /**
     * Sets the ProductOptionValues that should be mapped to this Sku
     *
     * @param productOptionValues
     * @see {@link ProductOptionValue}, {@link ProductOption}
     */
    void setProductOptionValuesCollection(Set<ProductOptionValue> productOptionValues);

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
     * A product is on sale provided the sale price is not null, non-zero, and less than the retail price.
     * 
     * Note that this flag should always be checked before showing or using a sale price as it is possible 
     * for a sale price to be greater than the retail price from a purely data perspective.
     * 
     * @return whether or not the product is on sale
     */
    public boolean isOnSale();

    /**
     * Whether this Sku can be sorted by a machine
     * 
     * @return <b>true</b> if this Sku can be sorted by a machine
     * @deprecated use {@link #getIsMachineSortable()} instead since that is the correct bean notation
     */
    @Deprecated
    public Boolean isMachineSortable();

    /**
     * Whether this Sku can be sorted by a machine
     * 
     */
    public Boolean getIsMachineSortable();

    /**
     * Sets whether or not this Sku can be sorted by a machine
     * 
     * @param isMachineSortable
     * @deprecated use {@link #setIsMachineSortable(Boolean)} instead since that is the correct bean notation
     */
    @Deprecated
    public void setMachineSortable(Boolean isMachineSortable);
    
    /**
     * Sets whether or not this Sku can be sorted by a machine
     * @param isMachineSortable
     */
    public void setIsMachineSortable(Boolean isMachineSortable);

    /**
     * Gets all the extra fees for this particular Sku. If the fee type is FULFILLMENT, these are stored
     * on {@link FulfillmentGroup#getFulfillmentGroupFees()} for an Order
     * 
     * @return the {@link SkuFee}s for this Sku
     */
    public List<SkuFee> getFees();

    /**
     * Sets the extra fees for this particular Sku
     * 
     * @param fees
     */
    public void setFees(List<SkuFee> fees);

    /**
     * Gets the flat rate for fulfilling this {@link Sku} for a particular {@link FulfillmentOption}. Depending
     * on the result of {@link FulfillmentOption#getUseFlatRates()}, this flat rate will be used in calculating
     * the cost of fulfilling this {@link Sku}.
     * 
     * @return the flat rates for this {@link Sku}
     */
    public Map<FulfillmentOption, BigDecimal> getFulfillmentFlatRates();

    /**
     * Sets the flat rates for fulfilling this {@link Sku} for a particular {@link FulfillmentOption}. Depending
     * on the result of {@link FulfillmentOption#getUseFlatRates()}, this flat rate will be used in calculating
     * the cost of fulfilling this {@link Sku}.
     * 
     * @param fulfillmentFlatRates
     */
    public void setFulfillmentFlatRates(Map<FulfillmentOption, BigDecimal> fulfillmentFlatRates);

    /**
     * Gets the {@link FulfillmentOption}s that this {@link Sku} should be excluded from. For instance,
     * some {@link Sku}s might not be available to be fulfilled next-day
     * 
     * @return
     */
    public List<FulfillmentOption> getExcludedFulfillmentOptions();

    /**
     * Sets the {@link FulfillmentOption}s that this Sku should be excluded from being apart of
     * 
     * @param excludedFulfillmentOptions
     */
    public void setExcludedFulfillmentOptions(List<FulfillmentOption> excludedFulfillmentOptions);

    /**
     * Returns the type of inventory for this sku
     * @return the {@link org.broadleafcommerce.core.inventory.service.type.InventoryType} for this sku
     */
    public InventoryType getInventoryType();

    /**
     * Sets the type of inventory for this sku
     * @param inventoryType the {@link InventoryType} for this sku
     */
    public void setInventoryType(InventoryType inventoryType);
    
    /**
     * <p>Used in conjuction with {@link InventoryType#CHECK_QUANTITY} within the blAddItemWorkflow and blUpdateItemWorkflow.
     * This field is checked within the {@link CheckAvailabilityActivity} to determine if inventory is actually available
     * for this Sku.
     */
    public Integer getQuantityAvailable();
    
    /**
     * <p>Used in conjunction with {@link InventoryType#CHECK_QUANTITY} from {@link #getInventoryType()}. This sets how much
     * inventory is available for this Sku.</p>
     * 
     * 
     * @param quantityAvailable the quantity available for this sku 
     */
    public void setQuantityAvailable(Integer quantityAvailable);
    
    /**
     * Returns the fulfillment type for this sku. May be null.
     * @return
     */
    public FulfillmentType getFulfillmentType();
    
    /**
     * Sets the fulfillment type for this sku. May return null.
     * @param fulfillmentType
     */
    public void setFulfillmentType(FulfillmentType fulfillmentType);

    /**
     * Clears any currently stored dynamic pricing
     */
    public void clearDynamicPrices();

    /**
     * Sets the currency for this Sku
     * 
     * Note: Currency is ignored when using dynamic pricing
     * 
     * @param currency
     */
    public void setCurrency(BroadleafCurrency currency);

    /**
     * Returns the currency for this sku if there is one set. If there is not, it will return the currency for the
     * default sku if this is not the default sku. Note that it is possible for this method to return null.
     * 
     * <b>Note: When using dynamic pricing, this method is unreliable and should not be called outside of the 
     * Broadleaf admin</b>
     * 
     * @return the currency for this sku
     */
    public BroadleafCurrency getCurrency();

    /**
     * Returns the Tax Code for this particular Entity.
     * 
     *  If the current Tax Code on the Sku is null, the Product tax code will be returned.
     * @return taxCode
     */
    public String getTaxCode();

    /**
     * Sets the tax code for this SKU
     * 
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
