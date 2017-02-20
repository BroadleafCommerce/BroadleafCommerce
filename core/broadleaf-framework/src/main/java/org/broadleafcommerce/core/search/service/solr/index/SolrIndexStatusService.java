/*
 * #%L
 * BroadleafCommerce Framework
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
