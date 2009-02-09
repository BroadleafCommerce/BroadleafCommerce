package org.broadleafcommerce.common.domain;

import java.io.Serializable;
import java.util.Date;

//@Embeddable
public class Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Column(name = "DATE_CREATED", updatable = false)
//    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

//    @Column(name = "CREATED_BY", updatable = false)
    private Long createdBy;

//    @Column(name = "DATE_UPDATED")
//    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;

//    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    public Date getDateCreated() {
        return dateCreated;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

    
    
}
