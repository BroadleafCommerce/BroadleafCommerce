package org.broadleafcommerce.email.service;

import javax.servlet.http.HttpServletRequest;


/**
 * @author jfischer
 *
 */
public interface EmailTrackingManager {

	public Long createTrackedEmail(String emailAddress, String type, String extraValue);
	public void recordOpen (Long emailId , HttpServletRequest request);
	public void recordClick(Long emailId , HttpServletRequest request);
	
}
