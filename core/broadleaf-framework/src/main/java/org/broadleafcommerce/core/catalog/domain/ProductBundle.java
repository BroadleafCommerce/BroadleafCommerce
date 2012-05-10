package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Default implementation for representing a bundle that can be sold
 * individually. Product bundles are composed of multiple
 * {@link ProductBundleItem}. <br>
 * <p>
 * Bundle prices are determined 1 of 2 ways, depending on the pricing model:
 * <ol>
 * <li>The sum of the prices of its {@link ProductBundleItem}</li>
 * <li>Uses the pricing information on the bundle itself</li>
 * </ol>
 * </p>
 * 
 * @author Phillip Verheyden
 * 
 * @see ProductBundleItem
 */
public interface ProductBundle extends Product, Serializable {

    public String getPricingModel();

    public void setPricingModel(String pricingModel);

    /**
     * Gets whether or not this should be bundled together if the individual
     * Products are added to the cart. For instance, if this Bundle is composed
     * of Item1 and Item2, and the user adds Item1 and Item2 to the cart
     * separately, if this is true then these items will be bundled into a
     * single BundleOrderItem instead of unique items in the cart
     * 
     * @return <b>true</b> if the items in this bundle should be automatically
     *         bundled together when added to the cart separately, <b>false</b>
     *         otherwise
     */
    public boolean isAutoBundle();

    /**
     * Sets whether or not this should be bundled together if the individual
     * Products are added to the cart. For instance, if this Bundle is composed
     * of Item1 and Item2, and the user adds Item1 and Item2 to the cart
     * separately, if this is true then these items will be bundled into a
     * single BundleOrderItem instead of unique items in the cart
     * 
     * @param autoBundle
     *            Whether or not the items in the bundle should be auto-bundled
     *            if added to the cart separately
     */
    public void setAutoBundle(boolean autoBundle);

    /**
     * Gets whether or not the items in this bundle should be considered for
     * promotions using the promotion engine
     * 
     * @return <b>true</b> if the items should be included in the promotion
     *         engine, <b>false</b> otherwise
     */
    public boolean isItemsPromotable();

    /**
     * Sets whether or not the items in this bundle should be considered for
     * promotions using the promotion engine
     * 
     * @param itemsPromotable
     *            Whether or not the items in the bundle should be considered
     *            for promotions
     */
    public void setItemsPromotable(boolean itemsPromotable);

    /**
     * Gets whether or not the bundle itself should be promotable. <br>
     * <b>Note:</b> this should only be used if the pricing model for the bundle
     * uses the pricing on the bundle itself and not on the sum of its bundle
     * items
     * 
     * @return <b>true</b> if the bundle itself should be available for
     *         promotion, <b>false</b> otherwise
     */
    public boolean isBundlePromotable();

    /**
     * Gets whether or not the bundle itself should be promotable. <br>
     * <b>Note:</b> this should only be used if the pricing model for the bundle
     * uses the pricing on the bundle itself and not on the sum of its bundle
     * items
     * 
     * @param bundlePromotable
     *            Whether or not the bundle itself should be available for
     *            promotion
     */
    public void setBundlePromotable(boolean bundlePromotable);

    public List<ProductBundleItem> getBundleItems();

    public void setBundleItems(List<ProductBundleItem> bundleItems);

}
