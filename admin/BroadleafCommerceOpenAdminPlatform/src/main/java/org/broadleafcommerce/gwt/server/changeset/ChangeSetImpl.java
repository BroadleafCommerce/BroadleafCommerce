package org.broadleafcommerce.gwt.server.changeset;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.broadleafcommerce.common.domain.Auditable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CHANGESET")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ChangeSetImpl implements ChangeSet {
	
	@Column(name = "STATUS_TYPE", nullable=false)
    @Index(name="CHANGESET_STATUS_INDEX", columnNames={"STATUS_TYPE"})
	protected String statusType;
	
	@OneToOne(targetEntity = ChangeSetImpl.class)
    @JoinColumn(name = "REPLACED_BY_ID")
    @Index(name="CHANGESET_REPLACED_BY_INDEX", columnNames={"REPLACED_BY_ID"})
	protected ChangeSet replacedBy;
	
	@Embedded
    protected Auditable auditable = new Auditable();
	
	@Column(name = "ACTIVE_DATE")
    @Index(name="CHANGESET_ACTIVE_DATE_INDEX", columnNames={"ACTIVE_DATE"})
	protected Date activeDate;

	public ChangeSetStatusType getStatusType() {
		return ChangeSetStatusType.getInstance(statusType);
	}

	public void setStatusType(ChangeSetStatusType statusType) {
		this.statusType = statusType.getType();
	}

	public ChangeSet getReplacedBy() {
		return replacedBy;
	}

	public void setReplacedBy(ChangeSet replacedBy) {
		this.replacedBy = replacedBy;
	}

	public Date getDateCreated() {
		return auditable.getDateCreated();
	}

	public Long getCreatedBy() {
		return auditable.getCreatedBy();
	}

	public Date getDateUpdated() {
		return auditable.getDateUpdated();
	}

	public Long getUpdatedBy() {
		return auditable.getUpdatedBy();
	}

	public void setDateCreated(Date dateCreated) {
		auditable.setDateCreated(dateCreated);
	}

	public void setCreatedBy(Long createdBy) {
		auditable.setCreatedBy(createdBy);
	}

	public void setDateUpdated(Date dateUpdated) {
		auditable.setDateUpdated(dateUpdated);
	}

	public void setUpdatedBy(Long updatedBy) {
		auditable.setUpdatedBy(updatedBy);
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	
}
