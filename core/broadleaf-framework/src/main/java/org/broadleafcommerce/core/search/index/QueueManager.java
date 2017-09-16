package org.broadleafcommerce.core.search.index;

import org.springframework.core.task.TaskExecutor;

/**
 * There are 2 sides to a queue: an entry and an exit.  As a result, for every Queue concept there typically needs to 
 * be something adding to the queue and something removing from it.  We're not trying to reinvent JMS or AMQP here.  Rather, 
 * we are simply trying to provide some simple abstractions that can be used for the purpose of reindexing.  The underlying 
 * Queuing system may be backed by JMS, AMQP, Apache Kafka, a java.util.concurrent.ArrayBlockingQueue, etc.  For efficiency 
 * reasons, though, the there needs to be something that can read batch data, add to a queue independently of the control 
 * thread or the consumers.
 * 
 * In particular, we need a single-threaded data reader that fills a Queue until there is nothing left to put in the Queue, 
 * and then potentially multiple Queue consumer threads that read and process the data.  Implementors of this interface 
 * provide access to both ends of that contract - the QueueConsumer and the QueueLoader.
 * 
 * Components implementing this interface are NOT meant to be singletons.  There should be one instance per queue name 
 * or a new instance for every queue.  Each QueueManager can only be used by a single index process at a time.
 * 
 * @author Kelly Tisdell
 *
 */
public interface QueueManager<T> {

    /**
     * Returns the QueueLoader associated with this QueueManager
     * @return
     */
    public QueueLoader<T> getQueueLoader();
    
    /**
     * Returns the Queue name associated with both the QueueLoader and QueueConsumer.
     * 
     * @return
     */
    public String getQueueName();
    
    /**
     * Lifecycle method to initialize this QueueManager.  This should initialize connections to the underlying queue, 
     * if needed, and should make instances of the QueueLoader and QueueConsumer available. This method MUST NOT cause 
     * the queue to be loaded.  However, after a call to this method, the QueueConsumer should be full initialized and 
     * ready to begin consuming data from the Queue.  A call to startQueueProducer() will be required to begin loading 
     * the Queue with data.
     * 
     * @param processId
     */
    public void initialize(String processId);
    
    /**
     * This starts the QueueProducer in a new Thread.  Only the 
     * process manager or thread that has the key and reference can start the QueueProducer.
     * @param lockService
     * @param key
     * @param reference
     * @throws LockException
     */
    public void startQueueProducer();
    
    /**
     * Delegates the execution of the QueueProducer to the provided TaskExecutor.
     * 
     * @param t
     * 
     */
    public void startQueueProducer(TaskExecutor t);
    
    /**
     * Lifecycle method to allow the closing and cleanup of resources.
     * @throws LockException
     */
    public void close();
    
    /**
     * Indicates if this QueueManager is intended for distributed use.  Examples of distributed Queues include JMS, 
     * AMQP, Apache Kafka, etc.  An example of a non-distributed Queue would be java.util.concurrent.ArrayBlockingQueue.
     * @return
     */
    public boolean isDistributed();
    
    /**
     * Method to indicate that this component was successfully initialized by a call to initialize().
     * @return
     */
    public boolean isInitialized();
    
    /**
     * A convenience method to indicate whether the queue has any items in it at a given moment.
     * @return
     */
    public boolean isQueueEmpty();
    
    /**
     * Indicates if there is nothing left to take from the queue.
     */
    public boolean isComplete();
    
}
