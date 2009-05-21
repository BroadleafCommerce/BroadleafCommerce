package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class CategoryProductImpl is the default implmentation of {@link Category}.
 * This entity is only used for executing a named query.
 * 
 * If you want to add fields specific to your implementation of BroadLeafCommerce you should extend
 * this class and add your fields.  If you need to make significant changes to the class then you
 * should implement your own version of {@link Category}.
 * <br>
 * <br>
 * This implementation uses a Hibernate implementation of JPA configured through annotations.
 * The Entity references the following tables:
 * BLC_CATEGORY_PRODUCT_XREF,
 * 
 * @see {@link Category}, {@link ProductImpl}
 * @author btaylor
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_PRODUCT_XREF")
public class CategoryProductImpl implements CategoryProduct, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    /** The category. */
    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    /** The product. */
    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    /** The display order. */
    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#getCategory()
     */
    public Category getCategory() {
        return category;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#setCategory(org.broadleafcommerce.catalog.domain.Category)
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#getProduct()
     */
    public Product getProduct() {
        return product;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#setProduct(org.broadleafcommerce.catalog.domain.Product)
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#getDisplayOrder()
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#setDisplayOrder(java.lang.Integer)
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
