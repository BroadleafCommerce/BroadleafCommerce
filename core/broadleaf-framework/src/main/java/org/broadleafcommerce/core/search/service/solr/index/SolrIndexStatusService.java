/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.search.service.solr.index;

import java.util.Date;

/**
 * Responsible for reading and writing the status using one or more {@link org.broadleafcommerce.core.search.service.solr.index.SolrIndexStatusProvider}
 * instances. {@link #getSeedStatusInstance()} can be used to provide a custom {@link org.broadleafcommerce.core.search.service.solr.index.IndexStatusInfo}
 * implementation.
 *
 * @author Jeff Fischer
 */
public interface SolrIndexStatusService {

    /**
     * Adds an IndexStatusInfo entry into the status providers
     * @param status
     */
    void setIndexStatus(IndexStatusInfo status);

    /**
     * Adds a new IndexStatusInfo given the eventId and the create date
     * @param status
     */
    void addIndexStatus(Long eventId, Date eventCreatedDate);

    /**
     * Returns a populated IndexStatusInfo instance from the provider(s)
     * @return the index status information 
     */
    IndexStatusInfo getIndexStatus();

    /**
     * Provide a custom IndexStatusInfo instance to be used by the system.
     *
     * @return
     */
    IndexStatusInfo getSeedStatusInstance();

    /**
     * Adds an error into the index status
     * @param eventId the Id of the event that has erred
     * @param retryCount the pre-set retry count defined in the event
     * @param eventCreatedDate the date that the event was created
     */
    void addIndexErrorStatus(Long eventId, Integer retryCount, Date eventCreatedDate);
}
