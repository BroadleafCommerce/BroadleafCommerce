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

        em.persist(clicks);
    }
}
