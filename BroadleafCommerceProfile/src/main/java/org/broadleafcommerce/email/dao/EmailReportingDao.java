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

import org.broadleafcommerce.email.domain.EmailTarget;
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
    public EmailTarget createTarget();

}
