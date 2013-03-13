package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
@Embeddable
public class CategoryXrefPK implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = CategoryImpl.class, optional=false)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category = new CategoryImpl();

    @ManyToOne(targetEntity = CategoryImpl.class, optional=false)
    @JoinColumn(name = "SUB_CATEGORY_ID")
    protected Category subCategory = new CategoryImpl();

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    public Category getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(final Category subCategory) {
        this.subCategory = subCategory;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof CategoryXrefPK)) return false;

        return category.getId().equals(((CategoryXrefPK) obj).getCategory().getId())
        && subCategory.getId().equals(((CategoryXrefPK) obj).getSubCategory().getId());
    }


    @Override
    public int hashCode() {
        return category.hashCode() + subCategory.hashCode();
    }
}
