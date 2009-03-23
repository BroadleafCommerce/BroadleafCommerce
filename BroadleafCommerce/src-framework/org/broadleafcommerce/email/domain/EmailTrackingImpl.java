package org.broadleafcommerce.email.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author jfischer
 *
 */
@Entity
@Table(name = "BLC_EMAIL_TRACKING")
public class EmailTrackingImpl implements EmailTracking {

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue
	@Column(name = "EMAIL_TRACKING_ID")
	private Long id;
	
	@Column(name = "EMAIL_ADDRESS")
	private String emailAddress;
	
	@Column(name = "DATE_SENT")
	private Date dateSent;
	
	@Column(name = "TYPE")
	private String type;
	
	@OneToMany(mappedBy = "emailTracking", targetEntity = EmailTrackingClicksImpl.class)
	private Set<EmailTrackingClicks> emailTrackingClicks;
	
	@OneToMany(mappedBy = "emailTracking", targetEntity = EmailTrackingOpensImpl.class)
	private Set<EmailTrackingOpens> emailTrackingOpens;

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTracking#getId()
	 */
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTracking#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTracking#getEmailAddress()
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTracking#setEmailAddress(java.lang.String)
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTracking#getDateSent()
	 */
	public Date getDateSent() {
		return dateSent;
	}
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTracking#setDateSent(java.util.Date)
	 */
	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTracking#getType()
	 */
	public String getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTracking#setType(java.lang.String)
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the emailTrackingClicks
	 */
	public Set<EmailTrackingClicks> getEmailTrackingClicks() {
		return emailTrackingClicks;
	}

	/**
	 * @param emailTrackingClicks the emailTrackingClicks to set
	 */
	public void setEmailTrackingClicks(Set<EmailTrackingClicks> emailTrackingClicks) {
		this.emailTrackingClicks = emailTrackingClicks;
	}

	/**
	 * @return the emailTrackingOpens
	 */
	public Set<EmailTrackingOpens> getEmailTrackingOpens() {
		return emailTrackingOpens;
	}

	/**
	 * @param emailTrackingOpens the emailTrackingOpens to set
	 */
	public void setEmailTrackingOpens(Set<EmailTrackingOpens> emailTrackingOpens) {
		this.emailTrackingOpens = emailTrackingOpens;
	}
	
}
