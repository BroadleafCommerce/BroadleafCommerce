package org.broadleafcommerce.gwt.server.changeset.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.broadleafcommerce.gwt.server.changeset.ChangeSetInfoListener;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@RevisionEntity(ChangeSetInfoListener.class)
@Table(name = "BLC_CHANGESET_INFO")
public class ChangeSetInfo {

    @Id
    @GeneratedValue
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    private long timestamp;
    
    private long user;
    
    private long changeset;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}

	public long getChangeset() {
		return changeset;
	}

	public void setChangeset(long changeset) {
		this.changeset = changeset;
	}
    
}
