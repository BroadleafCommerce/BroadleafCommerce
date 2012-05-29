package org.broadleafcommerce.core.catalog.domain;

/**
 * Stores the values for a given product option.
 *
 * For example, a ProductOption of type "color" might have values of ("red","blue")
 *
 * Created by bpolster.
 */
public interface ProductOptionValue {
    /**
     * Returns unique identifier of the product option value.
     * @return
     */
    public Long getId();

    /**
     * Sets the unique identifier of the product option value.
     * @param id
     */
    public void setId(Long id);

    /**
     * Returns the associated ProductOption
     *
     * @return
     */
    public ProductOption getProductOption();

    /**
     * Sets the associated product option.
     * @param productOption
     */
    public void setProductOption(ProductOption productOption);

    /**
     * Gets the option value.  (e.g. "red")
     * @param
     */
    public String getAttributeValue();

    /**
     * Sets the option value.  (e.g. "red")
     * @param attributeValue
     */
    public void setAttributeValue(String attributeValue);

    /**
     * Returns the order that the option value should be displayed in.
     * @return
     */
    public Long getDisplayOrder();

    /**
     * Sets the display order.
     * @param order
     */
    public void setDisplayOrder(Long order);
}
