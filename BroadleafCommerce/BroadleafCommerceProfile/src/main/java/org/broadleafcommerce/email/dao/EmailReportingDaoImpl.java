/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.email.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.domain.EmailTracking;
import org.broadleafcommerce.email.domain.EmailTrackingClicks;
import org.broadleafcommerce.email.domain.EmailTrackingOpens;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.time.SystemTime;
import org.springframework.stereotype.Repository;

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
        EmailTracking tracking = (EmailTracking) entityConfiguration.createEntityInstance("org.broadleafcommerce.email.domain.EmailTracking");
        tracking.setDateSent(SystemTime.asDate());
        tracking.setEmailAddress(emailAddress);
        tracking.setType(type);

        em.persist(tracking);

        return tracking.getId();
    }

    public EmailTarget createTarget() {
        EmailTarget target = (EmailTarget) entityConfiguration.createEntityInstance("org.broadleafcommerce.email.domain.EmailTarget");
        return target;
    }

    @SuppressWarnings("unchecked")
    public EmailTracking retrieveTracking(Long emailId) {
        return (EmailTracking) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.email.domain.EmailTracking"), emailId);
    }

    public void recordOpen(Long emailId, String userAgent) {
        EmailTrackingOpens opens = (EmailTrackingOpens) entityConfiguration.createEntityInstance("org.broadleafcommerce.email.domain.EmailTrackingOpens");
        opens.setEmailTracking(retrieveTracking(emailId));
        opens.setDateOpened(SystemTime.asDate());
        opens.setUserAgent(userAgent);

        em.persist(opens);
    }

    public void recordClick(Long emailId, Customer customer, String destinationUri, String queryString) {
        EmailTrackingClicks clicks = (EmailTrackingClicks) entityConfiguration.createEntityInstance("org.broadleafcommerce.email.domain.EmailTrackingClicks");
        clicks.setEmailTracking(retrieveTracking(emailId));
        clicks.setDateClicked(SystemTime.asDate());
        clicks.setDestinationUri(destinationUri);
        clicks.setQueryString(queryString);
        clicks.setCustomer(customer);

        em.persist(clicks);
    }
}
