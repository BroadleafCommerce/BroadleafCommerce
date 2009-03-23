package org.broadleafcommerce.email.dao;

import java.util.Date;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.email.domain.EmailTracking;
import org.broadleafcommerce.email.domain.EmailTrackingClicks;
import org.broadleafcommerce.email.domain.EmailTrackingClicksImpl;
import org.broadleafcommerce.email.domain.EmailTrackingImpl;
import org.broadleafcommerce.email.domain.EmailTrackingOpens;
import org.broadleafcommerce.email.domain.EmailTrackingOpensImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

/**
 * @author jfischer
 *
 */
@Repository("emailReportingDao")
public class EmailReportingDaoJpa implements EmailReportingDao {

	@PersistenceContext(unitName="blPU")
    private EntityManager em;
	
	@Resource
    private EntityConfiguration entityConfiguration;

    /* (non-Javadoc)
	 * @see WebReportingDao#createTracking(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Long createTracking(String emailAddress, String type, String extraValue) {
		EmailTracking tracking = new EmailTrackingImpl();
		tracking.setDateSent(new Date());
		tracking.setEmailAddress(emailAddress);
		tracking.setType(type);
		//tracking.setExtraValue(extraValue);
		
		em.persist(tracking);
		
		return tracking.getId();
	}
	
	@SuppressWarnings("unchecked")
	public EmailTracking retrieveTracking(Long emailId) {
		return (EmailTracking) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.email.domain.EmailTracking"), emailId);
	}

	public void recordOpen(Long emailId, String userAgent) {
		EmailTrackingOpens opens = new EmailTrackingOpensImpl();
		opens.setEmailTracking(retrieveTracking(emailId));
		opens.setDateOpened(new Date());
		opens.setUserAgent(userAgent);
		
		em.persist(opens);
	}
	
	public void recordClick(Long emailId, Customer customer, String destinationUri, String queryString) {
		EmailTrackingClicks clicks = new EmailTrackingClicksImpl();
		clicks.setEmailTracking(retrieveTracking(emailId));
		clicks.setDateClicked(new Date());
		clicks.setDestinationUri(destinationUri);
		clicks.setQueryString(queryString);
		clicks.setCustomer(customer);
		//clicks.setSessionId(sessionId);
		
		em.persist(clicks);
	}
}
