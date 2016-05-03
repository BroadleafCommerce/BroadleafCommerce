/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.email.dao;

import org.broadleafcommerce.common.email.domain.EmailTarget;
import org.broadleafcommerce.common.email.domain.EmailTracking;
import org.broadleafcommerce.common.email.domain.EmailTrackingClicks;
import org.broadleafcommerce.common.email.domain.EmailTrackingOpens;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.time.SystemTime;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author jfischer
 *
 */
@Repository("blEmailReportingDao")
public class EmailReportingDaoImpl implements EmailReportingDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    /* (non-Javadoc)
     * @see WebReportingDao#createTracking(java.lang.String, java.lang.String, java.lang.String)
     */
    public Long createTracking(String emailAddress, String type, String extraValue) {
        EmailTracking tracking = (EmailTracking) entityConfiguration.createEntityInstance("org.broadleafcommerce.common.email.domain.EmailTracking");
        tracking.setDateSent(SystemTime.asDate());
        tracking.setEmailAddress(emailAddress);
        tracking.setType(type);

        em.persist(tracking);

        return tracking.getId();
    }

    public EmailTarget createTarget() {
        EmailTarget target = (EmailTarget) entityConfiguration.createEntityInstance("org.broadleafcommerce.common.email.domain.EmailTarget");
        return target;
    }

    @SuppressWarnings("unchecked")
    public EmailTracking retrieveTracking(Long emailId) {
        return (EmailTracking) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.common.email.domain.EmailTracking"), emailId);
    }

    public void recordOpen(Long emailId, String userAgent) {
        EmailTrackingOpens opens = (EmailTrackingOpens) entityConfiguration.createEntityInstance("org.broadleafcommerce.common.email.domain.EmailTrackingOpens");
        opens.setEmailTracking(retrieveTracking(emailId));
        opens.setDateOpened(SystemTime.asDate());
        opens.setUserAgent(userAgent);

        em.persist(opens);
    }

    public void recordClick(Long emailId, String customerId, String destinationUri, String queryString) {
        EmailTrackingClicks clicks = (EmailTrackingClicks) entityConfiguration.createEntityInstance("org.broadleafcommerce.common.email.domain.EmailTrackingClicks");
        clicks.setEmailTracking(retrieveTracking(emailId));
        clicks.setDateClicked(SystemTime.asDate());
        clicks.setDestinationUri(destinationUri);
        clicks.setQueryString(queryString);
        clicks.setCustomerId(customerId);

        em.persist(clicks);
    }
}
