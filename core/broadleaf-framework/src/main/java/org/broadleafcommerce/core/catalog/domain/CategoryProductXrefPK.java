package org.broadleafcommerce.core.catalog.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Jeff Fischer
 */
@Embeddable
public class CategoryProductXrefPK implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = CategoryImpl.class, optional=false)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category = new CategoryImpl();

    /** The product. */
    @ManyToOne(targetEntity = ProductImpl.class, optional=false)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product = new ProductImpl();

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int hashCode() {
        return category.hashCode() + product.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CategoryProductXrefPK) {
            CategoryProductXrefPK that = (CategoryProductXrefPK) obj;
            return new EqualsBuilder()
                .append(category.getId(), that.category.getId())
                .append(product.getId(), that.product.getId())
                .build();
        }
        return false;
    }
    
}
