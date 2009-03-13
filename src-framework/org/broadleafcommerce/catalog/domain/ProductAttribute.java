package org.broadleafcommerce.catalog.domain;

// TODO: Auto-generated Javadoc
/**
 * The Interface ProductAttribute.
 */
public interface ProductAttribute {

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
     * Gets the value.
     * 
     * @return the value
     */
    public String getValue();

    /**
     * Sets the value.
     * 
     * @param value the new value
     */
    public void setValue(String value);

    /**
     * Gets the searchable.
     * 
     * @return the searchable
     */
    public Boolean getSearchable();

    /**
     * Sets the searchable.
     * 
     * @param searchable the new searchable
     */
    public void setSearchable(Boolean searchable);

    /**
     * Gets the product.
     * 
     * @return the product
     */
    public Product getProduct();

    /**
     * Sets the product.
     * 
     * @param product the new product
     */
    public void setProduct(Product product);

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
}
