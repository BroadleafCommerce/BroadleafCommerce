package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;

public interface CategoryXref extends Serializable {

	public Long getDisplayOrder();

    public void setDisplayOrder(final Long displayOrder);
    
    public Category getCategory();

	public void setCategory(final Category category);

	public Category getSubCategory();

	public void setSubCategory(final Category subCategory);
	
}