package org.broadleafcommerce.catalog.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.broadleafcommerce.changeset.ChangeSetImpl;
import org.hibernate.annotations.Index;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_CHANGE_SET")
public class CategoryChangeSetImpl extends ChangeSetImpl implements CategoryChangeSet {

	private static final long serialVersionUID = 1L;
	
	@OneToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="CATEGORY_CHANGE_SET_INDEX", columnNames={"CATEGORY_ID"})
    protected Category category;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}
