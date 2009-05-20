package org.broadleafcommerce.email.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author jfischer
 *
 */
@Entity
@Table(name = "BLC_EMAIL_TRACKING_OPENS")
public class EmailTrackingOpensImpl implements EmailTrackingOpens {

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name = "OPEN_ID")
	private Long id;
	
	@Column(name = "DATE_OPENED")
	private Date dateOpened;
	
	@Column(name = "USER_AGENT")
	private String userAgent;
	
	@ManyToOne(targetEntity = EmailTrackingImpl.class)
    @JoinColumn(name = "EMAIL_TRACKING_ID")
	private EmailTracking emailTracking;

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTrackingOpens#getId()
	 */
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTrackingOpens#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTrackingOpens#getDateOpened()
	 */
	public Date getDateOpened() {
		return dateOpened;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTrackingOpens#setDateOpened(java.util.Date)
	 */
	public void setDateOpened(Date dateOpened) {
		this.dateOpened = dateOpened;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTrackingOpens#getUserAgent()
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTrackingOpens#setUserAgent(java.lang.String)
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTrackingOpens#getEmailTracking()
	 */
	public EmailTracking getEmailTracking() {
		return emailTracking;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.email.domain.EmailTrackingOpens#setEmailTracking(org.broadleafcommerce.email.domain.EmailTrackingImpl)
	 */
	public void setEmailTracking(EmailTracking emailTracking) {
		this.emailTracking = emailTracking;
	}
	
}
