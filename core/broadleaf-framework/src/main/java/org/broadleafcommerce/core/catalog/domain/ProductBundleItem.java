package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;

import org.broadleafcommerce.common.money.Money;

/**
 * Represents the {@link Product} being sold in a bundle along with metadata
 * about the relationship itself like how many items should be included in the
 * bundle
 * 
 * @author Phillip Verheyden
 * @see ProductBundle, Product
 */
public interface ProductBundleItem extends Serializable {

    public Integer getQuantity();

    public void setQuantity(Integer quantity);

    /**
     * Allows for overriding the related Product's sale price. This is only used
     * if the pricing model for the bundle is a composition of its parts
     * getProduct().getDefaultSku().getSalePrice()
     * 
     * @param salePrice The sale price for this bundle item
     */
    public void setSalePrice(Money salePrice);

    /**
     * @return this salePrice if it is set,
     *         getProduct().getDefaultSku().getSalePrice() if this item's salePrice is
     *         null
     */
    public Money getSalePrice();

    public ProductBundle getBundle();

    public void setBundle(ProductBundle bundle);

    public Product getProduct();

    public void setProduct(Product product);

}
