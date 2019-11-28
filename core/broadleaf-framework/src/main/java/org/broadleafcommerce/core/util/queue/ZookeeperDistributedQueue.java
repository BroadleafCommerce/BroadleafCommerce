package org.broadleafcommerce.core.util.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.broadleafcommerce.common.util.GenericOperation;
import org.broadleafcommerce.common.util.GenericOperationUtil;
import org.broadleafcommerce.core.util.lock.DistributedLock;
import org.broadleafcommerce.core.util.lock.DistributedLock.DistributedLockException;
import org.broadleafcommerce.core.util.lock.ReentrantDistributedZookeeperLock;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Represents a {@link Queue} that is distributed (used by multiple JVMs or nodes) and managed by Zookeeper.  This queue uses distributed locks, also backed by Zookeeper.
 * 
 * Please note that while this works quite well in certain circumstances, it is not recommended for high volume or high capacity queues, 
 * nor for large queue messages.  It's a relatively slow queue.  Zookeeper allows you to create queues that can be used in a distributed way, but large queues can cause performance problems 
 * in Zookeeper, and Zookeeper has a 1MB transport limit, so messages have to be smaller than that.  Incidentally, initial performance tests showed queue operations (put / take) taking 
 * approximately 25-30 milliseconds, or about 40 queue operations per second with a small payload (about 15 bytes).
 * 
 * This Queue works quite well for smaller, lower capacity queues where you need to read/write in a distributed way. 
 * Try to limit the size of this queue to around 500 elements or fewer. Otherwise, consider a different queue implementation.
 * 
 * @author Kelly Tisdell
 *
 */
public class ZookeeperDistributedQueue<T extends Serializable> implements DistributedBlockingQueue<T> {
    
    private static final Log LOG = LogFactory.getLog(ZookeeperDistributedQueue.class);
    
    /**
     * This is the base folder that all queues will be written to in Solr.  The constructors require a lock path, which will be appended 
     * to this path.
     */
    public static final String DEFAULT_BASE_FOLDER = "/broadleaf/app/distributed-queues";
    
    protected static final String QUEUE_ENTRY_FOLDER = "/elements";
    protected static final String QUEUE_LOCKS_FOLDER = "/locks";
    protected static final String QUEUE_CONFIGS_FOLDER = "/configs";
    
    /*
     * List of Exception classes for which to ignore a retry in the case of an error, when interacting with Zookeeper.
     * In this case, InterruptedException is the only exceptions that we do not retry.
     */
    @SuppressWarnings({ "unchecked" })
    protected static final Class<Exception>[] IGNORABLE_EXCEPTIONS_FOR_RETRY = (Class<Exception>[])new Class<?>[]{InterruptedException.class};
    
    public static final int DEFAULT_MAX_QUEUE_SIZE = 100;
    
    private static final String QUEUE_ENTRY_NAME = "dz-queue-entry";
    
    protected final Object QUEUE_MONITOR = new Object();
    private final String queueFolderPath;
    private final SolrZkClient zk;
    private final int requestedMaxQueueSize;
    private final DistributedLock queueAccessLock;
    private final DistributedLock configLock;
    private int capacity;
    
    /**
     * Constructs a folder structure in Zookeeper for managing a queue and queue state..  The argument, queuePath, should start with a forward slash ('/') and should not 
     * end with a slash.  This argument should not contain whitespaces or other special characters.
     * 
     * The default max queue size will be 100.
     * 
     * @param queuePath
     * @param zk
     */
    public ZookeeperDistributedQueue(String queuePath, SolrZkClient zk) {
        this(queuePath, zk, DEFAULT_MAX_QUEUE_SIZE, true);
    }
    
    /**
     * Constructs a folder structure in Zookeeper for managing a queue and queue state..  The argument, queuePath, should start with a forward slash ('/') and should not 
     * end with a slash.  This argument should not contain whitespaces or other special characters.
     * 
     * The default max queue size will be 100.
     * 
     * @param queuePath
     * @param zk
     * @param maxQueueSize
     */
    public ZookeeperDistributedQueue(String queuePath, SolrZkClient zk, int maxQueueSize) {
        this(queuePath, zk, maxQueueSize, true);
    }
    
    /**
     * Constructs a folder structure in Zookeeper for managing a queue and queue state..  The argument, queuePath, should start with a forward slash ('/') and should not 
     * end with a slash.  This argument should be alpha-numeric, not contain whitespaces or other special characters, and can contain forward slashes ('/') to delineate folders. 
     * If useDefaultBasePath is true, then /broadleaf/app/distributed-queues will be prepended to the queuePath.  Otherwise, the queuePath will be used as it is provided.
     * 
     * The argument, maxQueueSize, will be a hint.  If another thread creates the queue structure in Zookeeper, then it will persist the maxQueueSize.
     * 
     * 
     * @param queuePath
     * @param zk
     * @param maxQueueSize
     * @param useDefaultBasePath
     */
    public ZookeeperDistributedQueue(String queuePath, SolrZkClient zk, int maxQueueSize, boolean useDefaultBasePath) {
        Assert.notNull(zk, "The SolrZkClient cannot be null.");
        Assert.isTrue(zk.isConnected(), "Please ensure that the SolrZkClient is connected.");
        Assert.notNull(queuePath, "The queuePath cannot be null and must be a Unix-style path (e.g. '/solr-index/command-queue').");
        Assert.hasText(queuePath.trim(), "The queuePath must not be empty and should not contain white spaces.");
        Assert.isTrue(maxQueueSize > 0, "maxQueueSize must be greater than 0.");
        
        this.requestedMaxQueueSize = maxQueueSize;
        setMaxQueueSize(requestedMaxQueueSize);
        this.zk = zk;
        
        if (this.requestedMaxQueueSize > 500) {
            LOG.error("Zookeeper queues can cause performance problems, especially when their maximum queue size is greater than 500. Anything over 1000 is considered unsupported. Please consider reducing the size of this queue.");
        }
        
        if (useDefaultBasePath) {
            if (queuePath.trim().startsWith("/")) {
                this.queueFolderPath = DEFAULT_BASE_FOLDER + queuePath.trim();
            } else {
                this.queueFolderPath = DEFAULT_BASE_FOLDER + '/' + queuePath.trim();
            }
        } else {
            if (queuePath.trim().startsWith("/")) {
                this.queueFolderPath = queuePath.trim();
            } else {
                this.queueFolderPath = '/' + queuePath.trim();
            }
        }
        
        intializeQueueFolders();
        
        this.queueAccessLock = initializeQueueAccessLock();
        this.configLock = initializeConfigLock();
        
        Assert.notNull(this.queueAccessLock, "The queue access lock cannot be null.");
        Assert.notNull(this.configLock, "The config lock cannot be null.");
        
        determineMaxQueueSize();
    }
    
    @Override
    public T remove() {
        try {
            List<T> entries = readQueueInternal(1, true, 0L);
            if (entries != null && !entries.isEmpty()) {
                return entries.get(0);
            } else {
                throw new DistributedQueueException("The queue was empty.");
            }
        } catch (InterruptedException e) {
            throw new DistributedQueueException("Thread was interrupdated removing an entry from the Zookeeper queue: " + getQueueFolderPath(), e);
        }
    }

    @Override
    public T poll() {
        try {
            List<T> entries = readQueueInternal(1, true, 0L);
            if (entries != null && ! entries.isEmpty()) {
                return entries.get(0);
            } else {
                return null;
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public T element() {
        T element = peek();
        if (element == null) {
            throw new IllegalStateException("The Zookeeper queue was empty." + getQueueFolderPath());
        }
        return element;
    }

    @Override
    public T peek() {
        try {
            List<T> elements = readQueueInternal(1, false, 0L);
            if (elements != null && ! elements.isEmpty()) {
                return elements.get(0);
            } else {
                return null;
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public int size() {
        DistributedLock lock = getQueueAccessLock();
        try {
            lock.lockInterruptibly();
            try {
                List<String> elementNames = executeOperation(new GenericOperation<List<String>>() {
                    @Override
                    public List<String> execute() throws Exception {
                        return getSolrZkClient().getChildren(getQueueEntryFolder(), null, true);
                    }
                });
                
                if (elementNames == null) {
                    return 0;
                }
                
                return elementNames.size();
                
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            throw new DistributedQueueException("Thread was interrupted while trying to determine queue size for distributed Zookeeper queue, " + getQueueFolderPath(), e);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (QUEUE_MONITOR) {
            return size() == 0;
        }
    }
    
    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("This method is not supported by default.");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("This method is not supported by default.");
    }

    @SuppressWarnings("hiding")
    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("This method is not supported by default.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        DistributedLock lock = getQueueAccessLock();
        try {
            lock.lockInterruptibly();
            try {
                List<T> elements = readQueueInternal(getMaxQueueSize(), false, 0L);
                if (elements != null && !elements.isEmpty()) {
                    return elements.containsAll(c);
                }
                
                return false;
                
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            throw new DistributedQueueException("The thread was interrupted while trying to determine if elements are contained in the Zookeeper queue, " + getQueueFolderPath(), e);
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c == null || c.isEmpty()) {
            return false;
        }
        try {
            int count = writeToQueue(new ArrayList<>(c), -1L);
            return count == c.size();
        } catch (InterruptedException e) {
            return false;
        }
        
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("This method is not supported by default.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("This method is not supported by default.");
    }

    @Override
    public void clear() {
        DistributedLock lock = getQueueAccessLock();
        try {
            lock.lockInterruptibly();
            try {
                executeOperation(new GenericOperation<Void>() {
                    @Override
                    public Void execute() throws Exception {
                        List<String> entryNames = getSolrZkClient().getChildren(getQueueEntryFolder(), null, true);
                        if (entryNames != null) {
                            for (String entry : entryNames) {
                                getSolrZkClient().delete(getQueueEntryFolder() + '/' + entry, 0, true);
                            }
                        }
                        return null;
                    }
                });
                
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DistributedQueueException("Thread was interrupted while clearing the queue.", e);
        }
    }

    @Override
    public boolean add(T e) {
        try {
            int count = writeToQueue(Collections.singletonList(e), 0L);
            if (count != 1) {
                throw new IllegalStateException("The Zookeeper queue was full.");
            } else {
                return true;
            }
        } catch (InterruptedException ex) {
            return false;
        }
    }

    @Override
    public boolean offer(T e) {
        try {
            final ArrayList<T> elementsToAdd = new ArrayList<>();
            elementsToAdd.add(e);
            int count = writeToQueue(elementsToAdd, 0L);
            if (count != 1) {
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException ex) {
            return false;
        }
    }

    @Override
    public void put(T e) throws InterruptedException {
        final ArrayList<T> elementsToAdd = new ArrayList<>();
        elementsToAdd.add(e);
        writeToQueue(elementsToAdd, -1L);
    }

    @Override
    public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
        final ArrayList<T> elementsToAdd = new ArrayList<>();
        elementsToAdd.add(e);
        return (writeToQueue(elementsToAdd, unit.toMillis(timeout)) == 1);
    }

    @Override
    public T take() throws InterruptedException { 
        return readQueueInternal(1, true, -1L).get(0);
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        List<T> elements = readQueueInternal(1, true, unit.toMillis(timeout));
        if (elements != null && ! elements.isEmpty()) {
            return elements.get(0);
        } else {
            return null;
        }
    }

    @Override
    public int remainingCapacity() {
        synchronized (QUEUE_MONITOR) {
            return getMaxQueueSize() - size();
        }
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return containsAll(Collections.singletonList(o));
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        try {
            List<T> entries = readQueueInternal(getMaxQueueSize(), true, 0L);
            c.addAll(entries);
            return entries.size();
        } catch (InterruptedException e) {
            return 0;
        }
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        try {
            List<T> entries = readQueueInternal(maxElements, true, 0L);
            c.addAll(entries);
            return entries.size();
        } catch (InterruptedException e) {
            return 0;
        }
    }
    
    protected int writeToQueue(List<? extends T> entries, final long timeout) throws InterruptedException {
        if (entries == null || entries.isEmpty()) {
            return 0;
        }
        
        int entryCount = 0;
        long waitTime = timeout;
        synchronized (QUEUE_MONITOR) {
            while (true) {
                DistributedLock lock = getQueueAccessLock();
                if (timeout < 0L) {
                    lock.lockInterruptibly();
                } else if (timeout > 0L && waitTime > 0L) {
                    long start = System.currentTimeMillis();
                    lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
                    long end = System.currentTimeMillis();
                    waitTime -= (end - start);
                } else {
                    lock.tryLock();
                }
                
                if (lock.currentThreadHoldsLock()) {
                    try {
                        int remainingCapacity = remainingCapacity();
                        if (remainingCapacity > 0) {
                            ListIterator<? extends T> itr = entries.listIterator();
                            while (itr.hasNext()) {
                                final T entry = itr.next();
                                if (remainingCapacity > 0) {
                                    executeOperation(new GenericOperation<Void>() {
                                        @Override
                                        public Void execute() throws Exception {
                                            byte[] data = serialize(entry);
                                            getSolrZkClient().makePath(getQueueEntryFolder() + '/' + getQueueEntryName(), data, CreateMode.PERSISTENT_SEQUENTIAL, null, true);
                                            return null;
                                        }
                                    });
                                    remainingCapacity--;
                                    entryCount++;
                                    itr.remove();
                                    
                                } else {
                                    break;
                                }
                            }
                            
                            if (entries.isEmpty()) {
                                return entryCount;
                            } else {
                                //Reset the timeout and continue...
                                waitTime = timeout;
                                continue;
                            }
                            
                        } else {
                            lock.unlock();
                            
                            if (timeout < 0L) {
                                //Wait forever
                                QUEUE_MONITOR.wait();
                            } else if (timeout > 0L && waitTime > 0L) {
                                //Wait for a period of time
                                long start = System.currentTimeMillis();
                                QUEUE_MONITOR.wait(waitTime);
                                long end = System.currentTimeMillis();
                                waitTime -= (end - start);  //Keep track of how long we waited.
                            } else {
                                return entryCount;
                            }   
                        }
                    } finally {
                        if (lock.currentThreadHoldsLock()) {
                            lock.unlock();
                        }
                    }
                }
                
            }
        }
    }
    
    protected List<T> readQueueInternal(final int qty, final boolean remove, final long timeout) throws InterruptedException {
        final ArrayList<T> out = new ArrayList<>();
        long waitTime = timeout;
        synchronized (QUEUE_MONITOR) {
            while (true) {
                DistributedLock lock = getQueueAccessLock();
                if (timeout < 0L) {
                    lock.lockInterruptibly();
                } else if (timeout > 0L && waitTime > 0L) {
                    long start = System.currentTimeMillis();
                    lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
                    long end = System.currentTimeMillis();
                    waitTime -= (end - start);
                } else {
                    lock.tryLock();
                }
                
                if (lock.currentThreadHoldsLock()) {
                    try {
                        List<String> entryNames = executeOperation(new GenericOperation<List<String>>() {
                            @Override
                            public List<String> execute() throws Exception {
                                return getSolrZkClient().getChildren(getQueueEntryFolder(), new Watcher() {
                                    @Override
                                    public void process(WatchedEvent event) {
                                        synchronized (QUEUE_MONITOR) {
                                            QUEUE_MONITOR.notifyAll();
                                        }
                                    }
                                    
                                }, true);
                            }
                        });
                        
                        if (entryNames != null && ! entryNames.isEmpty()) {
                            Collections.sort(entryNames);
                            
                            if (lock.currentThreadHoldsLock()) {
                                int count = 0;
                                for (final String entryName : entryNames) {
                                    try {
                                        T entry = executeOperation(new GenericOperation<T>() {
                                            @Override
                                            public T execute() throws Exception {
                                                byte[] data = getSolrZkClient().getData(getQueueEntryFolder() + '/' + entryName, null, null, true);
                                                
                                                @SuppressWarnings("unchecked")
                                                T deserialized = (T)deserialize(data);
                                                
                                                if (remove) {
                                                    getSolrZkClient().delete(getQueueEntryFolder() + '/' + entryName, 0, true);
                                                }
                                                
                                                return deserialized;
                                            }
                                        });
                                        
                                        if (entry != null) {
                                            count++;
                                            out.add(entry);
                                        }
                                        
                                        if (count >= qty) {
                                            break;
                                        }
                                        
                                    } catch (Exception e) {
                                        //This may have been removed by another thread, so just continue.
                                        if (e.getCause() != null && KeeperException.NoNodeException.class.isAssignableFrom(e.getCause().getClass())) {
                                            continue;
                                        } else if (RuntimeException.class.isAssignableFrom(e.getClass())) {
                                            throw (RuntimeException)e;
                                        } else if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                                            Thread.currentThread().interrupt();
                                            throw (InterruptedException)e;
                                        } else {
                                            throw new DistributedQueueException("An unexpected error occured executing a retryable operation for distributed Zookeeper queue, " + getQueueFolderPath(), e);
                                        }
                                    }
                                }
                            }
                            
                            return out;
                        } else {
                            //Unlock here so that we're not holding the lock while we wait...
                            lock.unlock();
                            
                            if (timeout < 0L) {
                                //Wait forever
                                QUEUE_MONITOR.wait();
                            } else if (timeout > 0L && waitTime > 0L) {
                                //Wait for a period of time
                                long start = System.currentTimeMillis();
                                QUEUE_MONITOR.wait(waitTime);
                                long end = System.currentTimeMillis();
                                waitTime -= (end - start);  //Keep track of how long we waited.
                            } else {
                                return out;
                            }   
                        }
                    } finally {
                        if (lock.currentThreadHoldsLock()) {
                            lock.unlock();
                        }
                    }
                }
            }
            
        }
    }
    
    /**
     * Creates the appropriate folder(s) in Zookeeper if they don't already exist.
     */
    protected synchronized void intializeQueueFolders() {
        try {
            executeOperation(new GenericOperation<Void>() {
                @Override
                public Void execute() throws Exception {
                    //Folder to hold the queue structure...
                    getSolrZkClient().makePath(getQueueFolderPath(), false, true);
                    return null;
                }
            });
            
            executeOperation(new GenericOperation<Void>() {
                @Override
                public Void execute() throws Exception {
                    //Folder to hold the queue elements...
                    getSolrZkClient().makePath(getQueueEntryFolder(), false, true);
                    return null;
                }
            });
            
            executeOperation(new GenericOperation<Void>() {
                @Override
                public Void execute() throws Exception {
                    //Folder to hold the locks...
                    getSolrZkClient().makePath(getLocksFolder(), false, true);
                    return null;
                }
            });
            
            executeOperation(new GenericOperation<Void>() {
                @Override
                public Void execute() throws Exception {
                    //Folder to hold the queue state...
                    getSolrZkClient().makePath(getConfigsFolder(), false, true);
                    return null;
                }
            });
        } catch (InterruptedException e) {
            throw new DistributedLockException("SolrZkClient encountered an error trying to create the persistent path, " 
                    + getQueueFolderPath() + " in Zookeeper.", e);
        }
    }
    
    protected synchronized void determineMaxQueueSize() {
        final DistributedLock lock = getQueueAccessLock();
        try {
            lock.lockInterruptibly();
            try {
                executeOperation(new GenericOperation<Void>() {
                    @Override
                    public Void execute() throws Exception {
                        if (!getSolrZkClient().exists(getConfigsFolder() + "/queueSize", true)) {
                            final int size = getRequestedMaxQueueSize();
                            getSolrZkClient().create(getConfigsFolder() + "/queueSize", serialize(size), CreateMode.EPHEMERAL, true);
                            setMaxQueueSize(size);
                        } else {
                            final Integer size = (Integer)deserialize(getSolrZkClient().getData(getConfigsFolder() + "/queueSize", new Watcher() {
                                @Override
                                public void process(WatchedEvent event) {
                                    //This happens in a callback on another thread, allowing us to avoid an infinite recursive loop.
                                    try {
                                        determineMaxQueueSize();
                                    } catch (Exception e) {
                                        LOG.error("An error occured in a callback to determine the max queue size.", e);
                                        if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                                
                            }, null, true));
                            setMaxQueueSize(size);
                        }
                        return null;
                    }
                });
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DistributedQueueException("An error occured trying to initialize the queue size.", e);
        }
    }
    
    /**
     * Mechanism to convert a byte array to an object.  Default implementation uses {@link ObjectInputStream}.
     * @param bytes
     * @return
     */
    protected Object deserialize(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new DistributedQueueException("Unable to deserialze an element from the Zookeeper queue.", e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Error occured closing the ObjectInputStream.", e);
                    }
                }
            }
            
            try {
                bais.close();
            } catch (IOException e) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Error occured closing the ByteArrayInputStream.", e);
                }
            }
        }
    }
    
    /**
     * Mechanism to convert an object to a byte array.  Default implementation uses {@link ObjectOutputStream}.
     * @param obj
     * @return
     */
    protected byte[] serialize(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            final byte[] bytes = baos.toByteArray();
            if (bytes.length >= 1000000) {
                LOG.warn("Zookeeper only allows a 1MB default transfer size.  This entry is " + bytes.length + " bytes.");
            }
            return bytes;
        } catch (IOException e) {
            throw new DistributedQueueException("An error occured trying to serialize an object to go on a Zookeeper queue.", e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Error occured closing the ObjectOutputStream.", e);
                    }
                }
            }
            
            try {
                baos.close();
            } catch (IOException e) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Error occured closing the ByteArrayOutputStream.", e);
                }
            }
        }
    }
    
    protected DistributedLock initializeQueueAccessLock() {
        return new ReentrantDistributedZookeeperLock(getSolrZkClient(), getLocksFolder() + "/access", "queueAccessLock", false);
    }
    
    protected DistributedLock initializeConfigLock() {
        return new ReentrantDistributedZookeeperLock(getSolrZkClient(), getLocksFolder() + "/config", "configLock", false);
    }
    
    protected SolrZkClient getSolrZkClient() {
        return zk;
    }
    
    protected int getRequestedMaxQueueSize() {
        return requestedMaxQueueSize;
    }
    
    public String getQueueFolderPath() {
        return queueFolderPath;
    }
    
    protected String getLocksFolder() {
        return getQueueFolderPath() + QUEUE_LOCKS_FOLDER;
    }
    
    protected String getConfigsFolder() {
        return getQueueFolderPath() + QUEUE_CONFIGS_FOLDER;
    }
    
    protected String getQueueEntryFolder() {
        return getQueueFolderPath() + QUEUE_ENTRY_FOLDER;
    }
    
    protected int getMaxQueueSize() {
        synchronized (QUEUE_MONITOR) {
            return capacity;
        }
    }
    
    protected void setMaxQueueSize(int size) {
        synchronized (QUEUE_MONITOR) {
            this.capacity = size;
        }
    }
    
    protected DistributedLock getQueueAccessLock() {
        return queueAccessLock;
    }
    
    protected DistributedLock getConfigLock() {
        return configLock;
    }
    
    protected String getQueueEntryName() {
        return QUEUE_ENTRY_NAME;
    }
    
    /**
     * Allows us to execute retry-able operations.
     * 
     * @param operation
     * @return
     * @throws InterruptedException
     */
    protected <R> R executeOperation(GenericOperation<R> operation) throws InterruptedException {
        try {
            return GenericOperationUtil.executeRetryableOperation(operation, 5, 100L, true, IGNORABLE_EXCEPTIONS_FOR_RETRY);
        } catch (Exception e) {
            if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                Thread.currentThread().interrupt();
                throw (InterruptedException)e;
            } else if (RuntimeException.class.isAssignableFrom(e.getClass())) {
                throw (RuntimeException)e;
            } else {
                throw new DistributedQueueException("An unexpected error occured executing a retryable operation for distributed Zookeeper queue, " + getQueueFolderPath(), e);
            }
        }
    }
}
