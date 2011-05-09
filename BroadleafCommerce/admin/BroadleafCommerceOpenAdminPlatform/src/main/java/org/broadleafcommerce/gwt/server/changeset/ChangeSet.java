package org.broadleafcommerce.gwt.server.changeset;

import java.util.Date;

public interface ChangeSet {

	public ChangeSetStatusType getStatusType();
	public void setStatusType(ChangeSetStatusType status);
	public ChangeSet getReplacedBy();
	public void setReplacedBy(ChangeSet changeSet);
	public Date getDateCreated();
    public Long getCreatedBy();
    public Date getDateUpdated();
    public Long getUpdatedBy();
    public void setDateCreated(Date dateCreated);
    public void setCreatedBy(Long createdBy);
    public void setDateUpdated(Date dateUpdated);
    public void setUpdatedBy(Long updatedBy);
    public Date getActiveDate();
    public void setActiveDate(Date activeDate);
    
}
