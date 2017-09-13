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

public interface QueueProducer<T> extends Runnable {

    /**
     * Puts the object on a queue, typically for consumption by another thread.
     * 
     * @param payload
     * @return
     */
    public boolean put(T payload) throws InterruptedException;
    
    /**
     * Puts the object on a queue, typically for consumption by another thread.
     * 
     * @param payload
     * @param timeout
     * @param timeUnit
     * @return
     * @throws InterruptedException
     */
    public boolean put(T payload, long timeout, TimeUnit timeUnit) throws InterruptedException;
    
    /**
     * Indicates that there is nothing else that will be added to the queue during this process.
     * 
     * @return
     */
    public boolean isComplete();
    
    /**
     * Indicates if this queue producer supports distributed processing.
     * 
     * @return
     */
    public boolean isDistributed();
    
}
