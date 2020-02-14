/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.indexer;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

/**
 * Provides a FIFO {@link Queue} to hold a series of commands to be executed in sequence.  This also provides a 
 * {@link Lock} so that only one thread can execute commands at any given time.
 * 
 * Note that it's safer to use a distributed Queue and a distributed {@link Lock} so that multiple nodes (JVMs) can't simultaneously 
 * execute commands, potentially overriding or corrupting each other.  A local {@link Queue} and a local {@link Lock} are satisfactory 
 * if users can guarantee that only a single node (JVM) will ever update Solr or receive events (e.g. from the admin) to update Solr.
 * 
 * @author Kelly Tisdell
 *
 */
public interface SolrIndexQueueProvider {
    
    public static final String COMMAND_LOCK_NAME = "_commandLock";
    public static final String COMMAND_QUEUE_NAME = "_commandQueue";

    public BlockingQueue<? super SolrUpdateCommand> createOrRetrieveCommandQueue(String queueName);
    
    public Lock createOrRetrieveCommandLock(String lockName);
    
    public boolean isDistributed();
    
}
