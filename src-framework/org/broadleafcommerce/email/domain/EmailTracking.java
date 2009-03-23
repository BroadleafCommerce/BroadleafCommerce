package org.broadleafcommerce.email.domain;

import java.util.Date;

/**
 * @author jfischer
 *
 */
public interface EmailTracking {

	public abstract Long getId();

	public abstract void setId(Long id);

	/**
	 * @return the emailAddress
	 */
	public abstract String getEmailAddress();

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public abstract void setEmailAddress(String emailAddress);

	/**
	 * @return the dateSent
	 */
	public abstract Date getDateSent();

	/**
	 * @param dateSent the dateSent to set
	 */
	public abstract void setDateSent(Date dateSent);

	/**
	 * @return the type
	 */
	public abstract String getType();

	/**
	 * @param type the type to set
	 */
	public abstract void setType(String type);

}