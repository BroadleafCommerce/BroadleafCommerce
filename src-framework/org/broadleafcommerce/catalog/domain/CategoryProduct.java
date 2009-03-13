package org.broadleafcommerce.catalog.domain;

// TODO: Auto-generated Javadoc
/**
 * Implementations of this interface are used to hold data about the many-to-many relationship between
 * the Category table and the Product table.  This entity is only used for executing a named
 * query.
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to the
 * relationship between Category and Product.  If you just want to add additional fields 
 * then you should extend {@link CategoryProductImpl}.
 * 
 *  @see {@link CategoryProductImpl},{@link Category}, {@link Product}
 *  @author btaylor
 *  
 */
public interface CategoryProduct {

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
     * Gets the category.
     * 
     * @return the category
     */
    public Category getCategory();

    /**
     * Sets the category.
     * 
     * @param category the new category
     */
    public void setCategory(Category category);

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
     * Gets the display order.
     * 
     * @return the display order
     */
    public Integer getDisplayOrder();

    /**
     * Sets the display order.
     * 
     * @param displayOrder the new display order
     */
    public void setDisplayOrder(Integer displayOrder);
}
