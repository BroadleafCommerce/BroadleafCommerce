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

    public BlockingQueue<SolrUpdateCommand> createOrRetrieveCommandQueue(String queueName);
    
    public Lock createOrRetrieveCommandLock(String lockName);
    
    public boolean isDistributed();
    
}
