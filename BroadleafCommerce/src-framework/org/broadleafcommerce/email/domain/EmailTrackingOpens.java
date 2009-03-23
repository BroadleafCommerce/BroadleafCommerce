package org.broadleafcommerce.email.domain;

import java.util.Date;

/**
 * @author jfischer
 *
 */
public interface EmailTrackingOpens {

	/**
	 * @return the id
	 */
	public abstract Long getId();

	/**
	 * @param id the id to set
	 */
	public abstract void setId(Long id);

	/**
	 * @return the dateOpened
	 */
	public abstract Date getDateOpened();

	/**
	 * @param dateOpened the dateOpened to set
	 */
	public abstract void setDateOpened(Date dateOpened);

	/**
	 * @return the userAgent
	 */
	public abstract String getUserAgent();

	/**
	 * @param userAgent the userAgent to set
	 */
	public abstract void setUserAgent(String userAgent);

	/**
	 * @return the emailTracking
	 */
	public abstract EmailTracking getEmailTracking();

	/**
	 * @param emailTracking the emailTracking to set
	 */
	public abstract void setEmailTracking(EmailTracking emailTracking);

}