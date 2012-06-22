package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;

/**
 * Represents the {@link org.broadleafcommerce.core.catalog.domain.Sku} being sold in a bundle along with metadata
 * about the relationship itself like how many items should be included in the
 * bundle
 *
 * @author Phillip Verheyden
 * @see ProductBundle, Product
 */
public interface SkuBundleItem extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Integer getQuantity();

    public void setQuantity(Integer quantity);

    /**
    * Allows for overriding the related Product's sale price. This is only used
    * if the pricing model for the bundle is a composition of its parts
    * getProduct().getDefaultSku().getSalePrice()
    *
    * @param itemSalePrice The sale price for this bundle item
    */
    public void setSalePrice(Money salePrice);

    /**
    * @return this itemSalePrice if it is set,
    *         getProduct().getDefaultSku().getSalePrice() if this item's itemSalePrice is
    *         null
    */
    public Money getSalePrice();

    public ProductBundle getBundle();

    public void setBundle(ProductBundle bundle);

    public Money getRetailPrice();

    public Sku getSku();

    public void setSku(Sku sku);
}
