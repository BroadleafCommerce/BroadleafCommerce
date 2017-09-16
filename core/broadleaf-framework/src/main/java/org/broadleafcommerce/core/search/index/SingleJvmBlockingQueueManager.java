package org.broadleafcommerce.core.search.index;

import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public class SingleJvmBlockingQueueManager<T> extends AbstractQueueManager<T> implements QueueConsumer<T> {
    
    protected static final long DEFAULT_POLL_TIME = 1000L;
    protected static final int DEFAULT_MAX_QUEUE_CAPACITY = 10000;
    private static final Set<String> QUEUE_NAMES_IN_USE = new HashSet<>();
    
    protected final int maxCapacity;
    
    protected final boolean fair;
    
    protected final String queueName;
    
    protected final BatchReader<T> batchReader;
    
    protected String processId;
    
    protected QueueLoader<T> queueLoader;
    
    protected ArrayBlockingQueue<T> queue;
    
    public SingleJvmBlockingQueueManager(String queueName, BatchReader<T> batchReader) {
        this(queueName, DEFAULT_MAX_QUEUE_CAPACITY, batchReader);
    }
    
    public SingleJvmBlockingQueueManager(String queueName, int maxCapacity, BatchReader<T> batchReader) {
        this(queueName, maxCapacity, true, batchReader);
    }
    
    public SingleJvmBlockingQueueManager(String queueName, int maxCapacity, boolean fair, BatchReader<T> batchReader) {
        this.queueName = queueName;
        this.maxCapacity = maxCapacity;
        this.fair = fair;
        this.batchReader = batchReader;
        Assert.notNull(queueName, "The queueName cannot be null");
        Assert.notNull(batchReader, "The batchReader cannot be null.");
        Assert.isTrue(maxCapacity > 0, "The maxCapacity must be greater than 0. Default is " + DEFAULT_MAX_QUEUE_CAPACITY);
    }

    @Override
    public QueueLoader<T> getQueueLoader() {
        return queueLoader;
    }

    @Override
    public final boolean isDistributed() {
        //By definition this is in-JVM.  This is not distributed, and so it is final.
        return false;
    }

    @Override
    public synchronized boolean isQueueEmpty() {
        if (queue == null) {
            return true;
        }
        return queue.isEmpty();
    }

    @Override
    protected synchronized void initializeInternal(String processId) {
        if (QUEUE_NAMES_IN_USE.contains(getQueueName())) {
            throw new IllegalStateException("A QueueManager with the queueName of " + getQueueName() 
                + " was already in use. Ensure it is stopped first.");
        }
        queue = createQueue();
        batchReader.reset();
        Assert.notNull(processId, "The processId cannot be null.");
        
    }
    
    @Override
    protected synchronized void closeInternal() {
        queue.clear();
        QUEUE_NAMES_IN_USE.remove(getQueueName());
        batchReader.reset();
        queue = null;
        queueLoader = null;
    }

    @Override
    public final String getQueueName() {
        return queueName;
    }
    
    protected ArrayBlockingQueue<T> createQueue() {
        return new ArrayBlockingQueue<>(maxCapacity, fair);
    }
    
    protected QueueLoader<T> createQueueLoader() {
        return new SingleJvmBlockingQueueLoader<>(processId, queue, batchReader);
    }

    @Override
    public boolean isComplete() {
        //TODO:
        return false;
    }

    @Override
    public T consume() {
        //TODO:
        return null;
    }
    
}
