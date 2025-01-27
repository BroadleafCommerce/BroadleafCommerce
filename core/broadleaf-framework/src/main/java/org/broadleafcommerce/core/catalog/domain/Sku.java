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
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPrices;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.core.order.service.workflow.CheckAddAvailabilityActivity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
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
 * @author btaylor
 * @see {@link SkuImpl}, {@link Money}
 */
public interface Sku extends Serializable, MultiTenantCloneable<Sku>, Indexable {

    /**
     * Returns the id of this sku
     */
    Long getId();

    /**
     * Sets the id of this sku
     */
    void setId(Long id);

    /**
     * Returns the sku specific portion of a full url used for a sku info page.
     */
    String getUrlKey();

    /**
     * Sets the sku specific portion of a full url used for a sku info page.
     *
     * @param url
     */
    void setUrlKey(String url);

    /**
     * Returns the name of a display template that is used to render this sku.  Most implementations have a default
     * template for all skus.  This allows for the user to define a specific template to be used by this sku.
     *
     * @return
     */
    String getDisplayTemplate();

    /**
     * Sets the name of a display template that is used to render this sku.  Most implementations have a default
     * template for all skus.  This allows for the user to define a specific template to be used by this sku.
     *
     * @param displayTemplate
     */
    void setDisplayTemplate(String displayTemplate);

    /**
     * This is the sum total of the priceAdjustments from the associated ProductOptionValues
     *
     * @return <b>null</b> if there are no ProductOptionValues associated with this Sku or
     * all of their priceAdjustments are null. Otherwise this will be the sum total of those price
     * adjustments
     * @see {@link ProductOptionValue}
     */
    Money getProductOptionValueAdjustments();

    /**
     * Returns the Sale Price of the Sku.  The Sale Price is the standard price the vendor sells
     * this item for.  If {@link SkuPricingConsiderationContext} is set, this uses the DynamicSkuPricingService
     * to calculate what this should actually be rather than use the property itself
     *
     * @see SkuPricingConsiderationContext, DynamicSkuPricingService
     */
    Money getSalePrice();

    /**
     * Sets the the Sale Price of the Sku.  The Sale Price is the standard price the vendor sells
     * this item for. This price will automatically be overridden if your system is utilizing
     * the DynamicSkuPricingService.
     */
    void setSalePrice(Money salePrice);

    /**
     * Determines if there is a sale price.  In other words, determines whether salePrice is null. Returns true if
     * salePrice is not null.  Returns false otherwise.
     *
     * @return
     */
    boolean hasSalePrice();

    /**
     * Returns the Retail Price of the Sku.  The Retail Price is the MSRP of the sku. If {@link SkuPricingConsiderationContext}
     * is set, this uses the DynamicSkuPricingService to calculate what this should actually be rather than use the property
     * itself.
     *
     * @throws IllegalStateException if retail price is null.
     * @see SkuPricingConsiderationContext, DynamicSkuPricingService, Sku.hasRetailPrice()
     */
    Money getRetailPrice();

    /**
     * Sets the retail price for the Sku. This price will automatically be overridden if your system is utilizing
     * the DynamicSkuPricingService.
     *
     * @param retailPrice price for the Sku
     */
    void setRetailPrice(Money retailPrice);

    /**
     * Returns the basic retail price of the Sku. This price does not include any dynamic pricing, including PriceList
     * modification.
     *
     * @return
     */
    Money getBaseRetailPrice();

    /**
     * Returns the basic sale price of the Sku. This price does not include any dynamic pricing, including PriceList
     * modification.
     *
     * @return
     */
    Money getBaseSalePrice();

    /**
     * Provides a way of determining if a Sku has a retail price without getting an IllegalStateException. Returns true if
     * retailPrice is not null.  Returns false otherwise.
     *
     * @return
     * @see #getRetailPrice()
     */
    boolean hasRetailPrice();

    /**
     * Resolves the price of the Sku. If the Sku is on sale (that is, isOnSale() returns true), the
     * return value will be the result of getSalePrice(). Otherwise, the return value will be the result of
     * getRetailPrice().
     *
     * @return the price of the Sku
     */
    Money getPrice();

    /**
     * Returns the List Price of the Sku.  The List Price is the MSRP of the sku.
     *
     * @deprecated
     */
    @Deprecated
    Money getListPrice();

    /**
     * Sets the the List Price of the Sku.  The List Price is the MSRP of the sku.
     *
     * @deprecated
     */
    @Deprecated
    void setListPrice(Money listPrice);

    /**
     * Returns the purchase cost of this Sku.
     *
     * @return the purchase cost of this Sku
     */
    Money getCost();

    /**
     * Sets the purchase cost for this Sku.
     *
     * @param cost
     */
    void setCost(Money cost);

    /**
     * Returns the margin of the Sku. This is the value of getPrice() - getCost()
     *
     * @return the margin of this Sku
     */
    Money getMargin();

    /**
     * Returns the name of the Sku.  The name is a label used to show when displaying the sku.
     */
    String getName();

    /**
     * Sets the the name of the Sku.  The name is a label used to show when displaying the sku.
     */
    void setName(String name);

    /**
     * Returns the brief description of the Sku.
     */
    String getDescription();

    /**
     * Sets the brief description of the Sku.
     */
    void setDescription(String description);

    /**
     * Returns the long description of the sku.
     */
    String getLongDescription();

    /**
     * Sets the long description of the sku.
     */
    void setLongDescription(String longDescription);

    /**
     * Returns whether the Sku qualifies for taxes or not.  This field is used by the pricing engine
     * to calculate taxes.
     */
    Boolean isTaxable();

    /**
     * Convenience that passes through to isTaxable
     */
    Boolean getTaxable();

    /**
     * Sets the whether the Sku qualifies for taxes or not.  This field is used by the pricing engine
     * to calculate taxes.
     */
    void setTaxable(Boolean taxable);

    /**
     * Returns whether the Sku qualifies for discounts or not.  This field is used by the pricing engine
     * to apply offers.
     */
    Boolean isDiscountable();

    /**
     * Sets the whether the Sku qualifies for discounts or not.  This field is used by the pricing engine
     * to apply offers.
     */
    void setDiscountable(Boolean discountable);

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
    Boolean isAvailable();

    /**
     * Convenience that passes through to isAvailable
     *
     * @see {@link #isAvailable()}
     * @deprecated use {@link #getInventoryType()} instead
     */
    @Deprecated
    Boolean getAvailable();

    /**
     * Availability is really a concern of inventory vs a concern of the Sku being active or not. A Sku could be marked as
     * unavailable but still be considered 'active' where you still want to show the Sku on the site but not actually sell
     * it. This defaults to true
     *
     * @deprecated use {@link #setInventoryType(InventoryType)} instead
     */
    @Deprecated
    void setAvailable(Boolean available);

    /**
     * Returns the first date that the Sku should be available for sale.  This field is used to determine
     * whether a user can add the sku to their cart.
     */
    Date getActiveStartDate();

    /**
     * Sets the the first date that the Sku should be available for sale.  This field is used to determine
     * whether a user can add the sku to their cart.
     */
    void setActiveStartDate(Date activeStartDate);

    /**
     * Returns the the last date that the Sku should be available for sale.  This field is used to determine
     * whether a user can add the sku to their cart.
     */
    Date getActiveEndDate();

    /**
     * Sets the the last date that the Sku should be available for sale.  This field is used to determine
     * whether a user can add the sku to their cart.
     */
    void setActiveEndDate(Date activeEndDate);

    /**
     * Get the dimensions for this Sku
     *
     * @return this Sku's embedded Weight
     */
    Dimension getDimension();

    /**
     * Sets the embedded Dimension for this Sku
     *
     * @param dimension
     */
    void setDimension(Dimension dimension);

    /**
     * Gets the embedded Weight for this Sku
     *
     * @return this Sku's embedded Weight
     */
    Weight getWeight();

    /**
     * Sets the embedded Weight for this Sku
     *
     * @param weight
     */
    void setWeight(Weight weight);

    /**
     * Returns a boolean indicating whether this sku is active.  This is used to determine whether a user
     * the sku can add the sku to their cart.
     */
    boolean isActive();

    /**
     * Returns a map of key/value pairs where the key is a string for the name of a media object and the value
     * is a media object.
     *
     * @deprecated use {@link #getSkuMediaXref()} instead
     */
    @Deprecated
    Map<String, Media> getSkuMedia();

    /**
     * Sets a map of key/value pairs where the key is a string for the name of a media object and the value
     * is an object of type Media.
     *
     * @deprecated use {@link #setSkuMediaXref(java.util.Map)} instead
     */
    @Deprecated
    void setSkuMedia(Map<String, Media> skuMedia);

    /**
     * Returns a map of key/value pairs where the key is a string for the name of a media object and the value
     * is a cross-reference to a media object.
     */
    Map<String, SkuMediaXref> getSkuMediaXref();

    /**
     * Sets a map of key/value pairs where the key is a string for the name of a media object and the value
     * is a cross-reference object to type Media.
     */
    void setSkuMediaXref(Map<String, SkuMediaXref> skuMediaXref);

    /**
     * Returns a map of key/value pairs where the key is a string for the name of a media object and the value
     * is a cross-reference to a media object. This is intended to be used when cloning Skus and does not
     * check the defaultSku unlike {@link #getSkuMediaXref()}.
     */
    Map<String, SkuMediaXref> getSkuMediaXrefIgnoreDefaultSku();

    /**
     * Returns the primary {@link Media} for this Sku
     */
    Media getPrimarySkuMedia();

    /**
     * Returns whether or not this Sku, the given Product and the given Category are all active
     *
     * @param product  - Product that should be active
     * @param category - Category that should be active
     * @return <b>true</b> if this Sku, <code>product</code> and <code>category</code> are all active
     * <b>false</b> otherwise
     */
    boolean isActive(Product product, Category category);

    /**
     * Denormalized set of key-value pairs to attach to a Sku. If you are looking for setting up
     * a {@link ProductOption} scenario (like colors, sizes, etc) see {@link #getProductOptionValues()}
     * and {@link #setProductOptionValues(List)}
     *
     * @return the attributes for this Sku
     * @deprecated use {@link #getMultiValueSkuAttributes()} instead.
     */
    @Deprecated
    Map<String, SkuAttribute> getSkuAttributes();

    /**
     * Sets the denormalized set of key-value pairs on a Sku
     *
     * @param skuAttributes
     */
    void setSkuAttributes(Map<String, SkuAttribute> skuAttributes);

    /**
     * Returns multivalued SkuAttributes associated with this sku using {@link org.apache.commons.collections.map.MultiValueMap}.
     * For example, the key of Color could have SkuAttributes of Red and Blue values.
     *
     * @return the multivalued attributes for this Sku
     */
    Map<String, Collection<SkuAttribute>> getMultiValueSkuAttributes();

    /**
     * Gets the ProductOptionValues used to map to this Sku. For instance, this Sku could hold specific
     * inventory, price and image information for a "Blue" "Extra-Large" shirt
     *
     * @return the ProductOptionValues for this Sku
     * @see {@link ProductOptionValue}, {@link ProductOption}
     * @deprecated use {@link #getProductOptionValuesCollection()} instead
     */
    @Deprecated
    List<ProductOptionValue> getProductOptionValues();

    /**
     * Sets the ProductOptionValues that should be mapped to this Sku
     *
     * @param productOptionValues
     * @see {@link ProductOptionValue}, {@link ProductOption}
     * @deprecated use {@link #setProductOptionValuesCollection(java.util.Set)} instead
     */
    @Deprecated
    void setProductOptionValues(List<ProductOptionValue> productOptionValues);

    /**
     * Gets the ProductOptionValues used to map to this Sku. For instance, this Sku could hold specific
     * inventory, price and image information for a "Blue" "Extra-Large" shirt
     *
     * @return the ProductOptionValues for this Sku
     * @see {@link ProductOptionValue}, {@link ProductOption}
     * @deprecated use {@link #getProductOptionValueXrefs()} instead
     */
    @Deprecated
    Set<ProductOptionValue> getProductOptionValuesCollection();

    /**
     * Sets the ProductOptionValues that should be mapped to this Sku
     *
     * @param productOptionValues
     * @see {@link ProductOptionValue}, {@link ProductOption}
     * @deprecated use {@link #setProductOptionValueXrefs(Set)} instead
     */
    @Deprecated
    void setProductOptionValuesCollection(Set<ProductOptionValue> productOptionValues);

    /**
     * Returns the ProductOptionValues that should be mapped to this Sku using the middle
     * XREF entity, {@link SkuProductOptionValueXref}
     *
     * @return the Set of {@link SkuProductOptionValueXref}s
     */
    Set<SkuProductOptionValueXref> getProductOptionValueXrefs();

    /**
     * Sets the ProductOptionValues that should be mapped to this Sku using the middle
     * XREF entity, {@link SkuProductOptionValueXref}
     */
    void setProductOptionValueXrefs(Set<SkuProductOptionValueXref> productOptionValueXrefs);

    /**
     * This will be a value if and only if this Sku is the defaultSku of a Product (and thus has a @OneToOne relationship with a Product).
     * The mapping for this is actually done at the Product level with a foreign key to Sku; this exists for convenience to get the reverse relationship
     *
     * @return The associated Product if this Sku is a defaultSku, <b>null</b> otherwise
     * @see #getProduct()
     */
    Product getDefaultProduct();

    /**
     * The relationship for a Product's default Sku (and thus a Sku's default Product) is actually maintained
     * on the Product entity as a foreign key to Sku.  Because of this, there are probably very few circumstances
     * that you would actually want to change this from the Sku perspective instead of the Product perspective.
     * <br />
     * <br />
     * If you are looking for a way to simply associate a Sku to a Product, the correct way would be to call
     * {@link #setProduct(Product)} or {@link Product#setAdditionalSkus(List)} which would then cause this Sku to show up in the list of Skus for
     * the given Product
     *
     * @param product
     */
    void setDefaultProduct(Product product);

    /**
     * This will return the correct Product association that is being used on the Sku. If this Sku is a default Sku
     * for a Product (as in, {@link #getDefaultProduct()} != null) than this will return {@link #getDefaultProduct()}. If this is not
     * a default Sku for a Product, this will return the @ManyToOne Product relationship created by adding this Sku to a Product's
     * list of Skus, or using {@link #setProduct(Product)}.
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
    Product getProduct();

    /**
     * Associates a Sku to a given Product. This will then show up in the list returned by {@link Product#getSkus()}
     *
     * @param product - Product to associate this Sku to
     * @see Product#getSkus()
     */
    void setProduct(Product product);

    /**
     * A product is on sale provided the sale price is not null, non-zero, and less than the retail price.
     * <p>
     * Note that this flag should always be checked before showing or using a sale price as it is possible
     * for a sale price to be greater than the retail price from a purely data perspective.
     *
     * @return whether or not the product is on sale
     */
    boolean isOnSale();

    /**
     * Whether this Sku can be sorted by a machine
     *
     * @return <b>true</b> if this Sku can be sorted by a machine
     * @deprecated use {@link #getIsMachineSortable()} instead since that is the correct bean notation
     */
    @Deprecated
    Boolean isMachineSortable();

    /**
     * Whether this Sku can be sorted by a machine
     */
    Boolean getIsMachineSortable();

    /**
     * Sets whether or not this Sku can be sorted by a machine
     *
     * @param isMachineSortable
     */
    void setIsMachineSortable(Boolean isMachineSortable);

    /**
     * Sets whether or not this Sku can be sorted by a machine
     *
     * @param isMachineSortable
     * @deprecated use {@link #setIsMachineSortable(Boolean)} instead since that is the correct bean notation
     */
    @Deprecated
    void setMachineSortable(Boolean isMachineSortable);

    /**
     * Gets all the extra fees for this particular Sku. If the fee type is FULFILLMENT, these are stored
     * on {@link FulfillmentGroup#getFulfillmentGroupFees()} for an Order
     *
     * @return the {@link SkuFee}s for this Sku
     */
    List<SkuFee> getFees();

    /**
     * Sets the extra fees for this particular Sku
     *
     * @param fees
     */
    void setFees(List<SkuFee> fees);

    /**
     * Gets the flat rate for fulfilling this {@link Sku} for a particular {@link FulfillmentOption}. Depending
     * on the result of {@link FulfillmentOption#getUseFlatRates()}, this flat rate will be used in calculating
     * the cost of fulfilling this {@link Sku}.
     *
     * @return the flat rates for this {@link Sku}
     */
    Map<FulfillmentOption, BigDecimal> getFulfillmentFlatRates();

    /**
     * Sets the flat rates for fulfilling this {@link Sku} for a particular {@link FulfillmentOption}. Depending
     * on the result of {@link FulfillmentOption#getUseFlatRates()}, this flat rate will be used in calculating
     * the cost of fulfilling this {@link Sku}.
     *
     * @param fulfillmentFlatRates
     */
    void setFulfillmentFlatRates(Map<FulfillmentOption, BigDecimal> fulfillmentFlatRates);

    /**
     * Gets the {@link FulfillmentOption}s that this {@link Sku} should be excluded from. For instance,
     * some {@link Sku}s might not be available to be fulfilled next-day
     *
     * @return
     */
    List<FulfillmentOption> getExcludedFulfillmentOptions();

    /**
     * Sets the {@link FulfillmentOption}s that this Sku should be excluded from being apart of
     *
     * @param excludedFulfillmentOptions
     */
    void setExcludedFulfillmentOptions(List<FulfillmentOption> excludedFulfillmentOptions);

    /**
     * Returns the type of inventory for this sku
     *
     * @return the {@link org.broadleafcommerce.core.inventory.service.type.InventoryType} for this sku
     */
    InventoryType getInventoryType();

    /**
     * Sets the type of inventory for this sku
     *
     * @param inventoryType the {@link InventoryType} for this sku
     */
    void setInventoryType(InventoryType inventoryType);

    /**
     * <p>Used in conjuction with {@link InventoryType#CHECK_QUANTITY} within the blAddItemWorkflow and blUpdateItemWorkflow.
     * This field is checked within the {@link CheckAddAvailabilityActivity} to determine if inventory is actually available
     * for this Sku.
     */
    Integer getQuantityAvailable();

    /**
     * <p>Used in conjunction with {@link InventoryType#CHECK_QUANTITY} from {@link #getInventoryType()}. This sets how much
     * inventory is available for this Sku.</p>
     *
     * @param quantityAvailable the quantity available for this sku
     */
    void setQuantityAvailable(Integer quantityAvailable);

    /**
     * Returns the fulfillment type for this sku. May be null.
     *
     * @return
     */
    FulfillmentType getFulfillmentType();

    /**
     * Sets the fulfillment type for this sku. May return null.
     *
     * @param fulfillmentType
     */
    void setFulfillmentType(FulfillmentType fulfillmentType);

    /**
     * Clears any currently stored dynamic pricing
     */
    void clearDynamicPrices();

    /**
     * <b>Note: When using dynamic pricing, this method is unreliable and should not be called outside of the
     * Broadleaf admin</b>  Instead, you should rely on the {@link BroadleafRequestContext#getBroadleafCurrency()}
     * instead of storing at the SKU level.
     * <p>
     * As such, for supported, enterprise installations, this method should always return null.
     * <p>
     * This method was not deprecated as it may have some use in non-standard Broadleaf installations but
     * using its use is not suggested for most implementations.
     *
     * @return the currency for this sku
     */
    BroadleafCurrency getCurrency();

    /**
     * Sets the currency for this Sku
     * <p>
     * Note: Currency is ignored when using dynamic pricing
     *
     * @param currency
     */
    void setCurrency(BroadleafCurrency currency);

    /**
     * Returns the Tax Code for this particular Entity.
     * <p>
     * If the current Tax Code on the Sku is null, the Product tax code will be returned.
     *
     * @return taxCode
     */
    String getTaxCode();

    /**
     * Sets the tax code for this SKU
     *
     * @param taxCode
     */
    void setTaxCode(String taxCode);

    /**
     * Gets the Universal Product Code (UPC)
     *
     * @return
     */
    String getUpc();

    /**
     * Sets the Universal Product Code (UPC)
     *
     * @param upc
     */
    void setUpc(String upc);

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

    /**
     * If a DynamicPricingService is being used, this method will return the dynamic Sku prices.
     * Otherwise, it will return an instance of DynamicSkuPrices with the retail and sale price
     * from the underlying record.
     *
     * @return
     */
    DynamicSkuPrices getPriceData();

}
