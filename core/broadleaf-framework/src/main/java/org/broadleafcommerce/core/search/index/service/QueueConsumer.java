/*-
 * #%L
 * BroadleafCommerce Core Solr Components Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */

package org.broadleafcommerce.core.search.index.service;

import java.util.concurrent.TimeUnit;

public interface QueueConsumer<T> {
    
    public T consume() throws InterruptedException;
    
    public T consume(long timeout, TimeUnit timeUnit) throws InterruptedException;
    
    /**
     * Indicates if the queue is empty and there is nothing else that will be added to the queue.
     * Essentially indicates that nothing more will be added to the queue, and nothing else is left in the queue.
     * @return
     */
    public boolean isQueueExpired();
    
    /**
     * Indicates if this queue consumer supports distributed processing.
     * 
     * @return
     */
    public boolean isDistributed();
    
}
