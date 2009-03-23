package org.broadleafcommerce.email.dao;

import org.broadleafcommerce.email.domain.EmailTracking;
import org.broadleafcommerce.profile.domain.Customer;

/**
 * @author jfischer
 *
 */
public interface EmailReportingDao {
	
	public Long createTracking(String emailAddress, String type, String extraValue) ;
	public void recordOpen(Long emailId, String userAgent);
	public void recordClick(Long emailId, Customer customer, String destinationUri, String queryString);
	public EmailTracking retrieveTracking(Long emailId);
	
}
