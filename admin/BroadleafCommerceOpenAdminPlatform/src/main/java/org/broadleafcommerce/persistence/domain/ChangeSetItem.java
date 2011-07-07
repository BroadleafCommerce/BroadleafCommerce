package org.broadleafcommerce.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_CHANGESET_ITEM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class ChangeSetItem {

	@Id
    @GeneratedValue(generator = "ChangeSetItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ChangeSetItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ChangeSetItemImpl", allocationSize = 50)
    @Column(name = "CHANGESET_ITEM_ID")
    protected Long id;
	
	protected Long sandBoxId;
	
	protected Long entityId;
	
	protected String entityImplementationClass;
	
	
}
