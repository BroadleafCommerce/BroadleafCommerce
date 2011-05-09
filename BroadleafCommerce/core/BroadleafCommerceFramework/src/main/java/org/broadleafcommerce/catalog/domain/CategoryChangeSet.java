package org.broadleafcommerce.catalog.domain;

import org.broadleafcommerce.changeset.ChangeSet;

public interface CategoryChangeSet extends ChangeSet {

	public Category getCategory();

	public void setCategory(Category category);
	
}
