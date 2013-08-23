/*
 * Copyright 2008-2013 the original author or authors.
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
import org.broadleafcommerce.core.catalog.service.type.ProductBundlePricingModelType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Default implementation for representing a bundle that can be sold
 * individually. Product bundles are composed of multiple
 * {@link SkuBundleItem}. <br>
 * <p>
 * Bundle prices are determined 1 of 2 ways, depending on the pricing model:
 * <ol>
 * <li><b>ITEM_SUM</b>: The sum of the prices of its {@link SkuBundleItem}</li>
 * <li><b>BUNDLE</b>: Uses the pricing information on the bundle itself</li>
 * </ol>
 * </p>
 * 
 * @author Phillip Verheyden
 * 
 * @see SkuBundleItem
 */
public interface ProductBundle extends Product, Serializable {

    /**
     * @return The pricing model for this bundle
     *
     * ITEM_SUM indicates that the bundle is priced by the sum of the contained items.
     * BUNDLE indicates that the bundle is priced by the price on the bundle itself.
     */
    public ProductBundlePricingModelType getPricingModel();
    
    /**
     * 
     * @param pricingModel
     *            <b>ITEM_SUM</b> if the retailPrice and salePrice of this
     *            bundle should be the composition of its items, <b>BUNDLE</b>
     *            if this retailPrice and salePrice should come from the default
     *            Sku
     */
    public void setPricingModel(ProductBundlePricingModelType pricingModel);

    /**
     * Returns the retail price for this bundle
     * @return
     */
    public Money getRetailPrice();

    /**
     * Returns the sale price for this bundle
     * @return
     */
    public Money getSalePrice();

    /**
     * @return the sum of the retail prices of the bundle items
     */
    public Money getBundleItemsRetailPrice();

    /**
     * @return the sum of the sale prices of the bundle items
     */
    public Money getBundleItemsSalePrice();

    /**
     * Gets whether or not this should be bundled together if the individual
     * Products are added to the cart. For instance, if this Bundle is composed
     * of Item1 and Item2, and the user adds Item1 and Item2 to the cart
     * separately, if this is true then these items will be bundled into a
     * single BundleOrderItem instead of unique items in the cart
     * 
     * <b>NOTE: THIS IS NOT YET SUPPORTED BY BROADLEAF</b>
     * 
     * @return <b>true</b> if the items in this bundle should be automatically
     *         bundled together when added to the cart separately, <b>false</b>
     *         otherwise
     */
    public Boolean getAutoBundle();

    /**
     * Sets whether or not this should be bundled together if the individual
     * Products are added to the cart. For instance, if this Bundle is composed
     * of Item1 and Item2, and the user adds Item1 and Item2 to the cart
     * separately, if this is true then these items will be bundled into a
     * single BundleOrderItem instead of unique items in the cart
     * 
     * <b>NOTE: THIS IS NOT YET SUPPORTED BY BROADLEAF</b>
     * 
     * @param autoBundle
     *            Whether or not the items in the bundle should be auto-bundled
     *            if added to the cart separately
     */
    public void setAutoBundle(Boolean autoBundle);

    /**
     * Gets whether or not the items in this bundle should be considered for
     * promotions using the promotion engine <br />
     * <br />
     * Note: this is only applicable when the pricing model is the sum of the
     * bundle items
     * 
     * <b>NOTE: THIS IS NOT YET SUPPORTED BY BROADLEAF</b>
     * 
     * @return <b>true</b> if the items should be included in the promotion
     *         engine, <b>false</b> otherwise
     */
    public Boolean getItemsPromotable();

    /**
     * Sets whether or not the items in this bundle should be considered for
     * promotions using the promotion engine
     * 
     * <b>NOTE: THIS IS NOT YET SUPPORTED BY BROADLEAF</b>
     * 
     * @param itemsPromotable
     *            Whether or not the items in the bundle should be considered
     *            for promotions
     */
    public void setItemsPromotable(Boolean itemsPromotable);

    /**
     * Gets whether or not the bundle itself should be promotable. <br>
     * <b>Note:</b> this should only be used if the pricing model for the bundle
     * uses the pricing on the bundle itself and not on the sum of its bundle
     * items
     * 
     * <b>NOTE: THIS IS NOT YET SUPPORTED BY BROADLEAF</b>
     * 
     * @return <b>true</b> if the bundle itself should be available for
     *         promotion, <b>false</b> otherwise
     */
    public Boolean getBundlePromotable();

    /**
     * Gets whether or not the bundle itself should be promotable. <br>
     * <b>Note:</b> this should only be used if the pricing model for the bundle
     * uses the pricing on the bundle itself and not on the sum of its bundle
     * items
     * 
     * <b>NOTE: THIS IS NOT YET SUPPORTED BY BROADLEAF</b>
     *
     * @param bundlePromotable
     *            Whether or not the bundle itself should be available for
     *            promotion
     */
    public void setBundlePromotable(Boolean bundlePromotable);

    public List<SkuBundleItem> getSkuBundleItems();

    public void setSkuBundleItems(List<SkuBundleItem> bundleItems);

    /**
     * Used to determine the order for automatic bundling.
     * @return
     */
    public Integer getPriority();

    public void setPriority(Integer priority);

    /**
     * Calculates the potential savings by summing up the retail prices of the
     * contained items and comparing to the actual bundle prices.
     *
     * Used to determine the order for automatic bundling in case items might
     * qualify for multiple bundles.
     *
     * @return
     */
    public BigDecimal getPotentialSavings();

    /**
     * @return whether or not the product bundle is on sale
     */
    public boolean isOnSale();

}
