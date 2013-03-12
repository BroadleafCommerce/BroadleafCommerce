package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

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
        if (obj == null) return false;
        else if (!(obj instanceof CategoryProductXrefPK)) return false;

        return category.getId().equals(((CategoryProductXrefPK) obj).category.getId())
        && product.getId().equals(((CategoryProductXrefPK) obj).product.getId());
    }
}
