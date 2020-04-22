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
package org.broadleafcommerce.core.util.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.broadleafcommerce.common.util.GenericOperation;
import org.broadleafcommerce.common.util.GenericOperationUtil;
import org.broadleafcommerce.core.util.ZookeeperUtil;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Represents a {@link Queue} that is distributed (used by multiple JVMs or nodes) and managed by Zookeeper.  This queue uses distributed locks, also backed by Zookeeper.
 * 
 * Please note that while this works quite well in certain circumstances, it is not recommended for high volume or high capacity queues, 
 * nor for large queue messages.  It's a relatively slow queue.  Zookeeper allows you to create queues that can be used in a distributed way, but large queues can cause performance problems 
 * in Zookeeper, and Zookeeper has a 1MB transport limit, so messages have to be smaller than that.  Incidentally, initial performance tests showed queue operations (put / take) taking 
 * approximately 25-30 milliseconds, or about 30-40 queue operations per second with a small payload (about 15 bytes).
 * 
 * This Queue works quite well for smaller, lower capacity / throughput queues where you need to read/write in a distributed way. 
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
    public static final String QUEUE_ENTRY_FOLDER = "/elements";
    public static final String QUEUE_LOCKS_FOLDER = "/locks";
    public static final String QUEUE_CONFIGS_FOLDER = "/configs";
    
    public static final int DEFAULT_MAX_QUEUE_SIZE = 500;
    
    private static final String QUEUE_ENTRY_NAME = "dz-queue-entry";
    
    protected final Object QUEUE_MONITOR = new Object();
    private final String queueFolderPath;
    private final ZooKeeper zk;
    private final List<ACL> acls;
    private final int requestedMaxQueueCapacity;
    private final DistributedLock queueAccessLock;
    private final DistributedLock configLock;
    private int capacity;
    
    /**
     * Constructs a folder structure in Zookeeper for managing a queue and queue state..  The argument, queuePath, should start with a forward slash ('/') and should not 
     * end with a slash.  This argument should not contain whitespaces or other special characters.
     * 
     * The default max queue size will be 500.
     * 
     * @param queuePath
     * @param zk
     */
    public ZookeeperDistributedQueue(String queuePath, ZooKeeper zk) {
        this(queuePath, zk, DEFAULT_MAX_QUEUE_SIZE, true, null);
    }
    
    /**
     * Constructs a folder structure in Zookeeper for managing a queue and queue state..  The argument, queuePath, should start with a forward slash ('/') and should not 
     * end with a slash.  This argument should not contain whitespaces or other special characters.
     * 
     * The default max queue size will be 500.
     * 
     * @param queuePath
     * @param zk
     * @param maxQueueSize
     */
    public ZookeeperDistributedQueue(String queuePath, ZooKeeper zk, int maxQueueSize) {
        this(queuePath, zk, maxQueueSize, true, null);
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
     * @param acls
     */
    public ZookeeperDistributedQueue(String queuePath, ZooKeeper zk, int maxQueueSize, boolean useDefaultBasePath, List<ACL> acls) {
        Assert.notNull(zk, "The SolrZkClient cannot be null.");
        Assert.notNull(queuePath, "The queuePath cannot be null and must be a Unix-style path (e.g. '/solr-index/command-queue').");
        Assert.hasText(queuePath.trim(), "The queuePath must not be empty and should not contain white spaces.");
        Assert.isTrue(maxQueueSize > 0, "maxQueueSize must be greater than 0.");
        
        this.zk = zk;
        if (acls == null || acls.isEmpty()) {
            this.acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        } else {
            this.acls = acls;
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
        
        this.requestedMaxQueueCapacity = maxQueueSize;
        seMaxCapacity(this.requestedMaxQueueCapacity);
        
        if (this.requestedMaxQueueCapacity > DEFAULT_MAX_QUEUE_SIZE) {
            LOG.error("Zookeeper queues can cause performance problems, especially when their maximum queue size is greater than 500. "
                    + "Anything over 1000 is considered unsupported. Please consider reducing the maximum capacity of this queue.");
        }
        
        determineMaxCapacity();
    }
    
    @Override
    public T remove() {
        try {
            Map<String, T> entries = readQueueInternal(1, true, 0L);
            Iterator<Map.Entry<String, T>> itr = entries.entrySet().iterator();
            if (itr.hasNext()) {
                return itr.next().getValue();
            }
            
            throw new DistributedQueueException("The queue was empty.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DistributedQueueException("Thread was interrupdated removing an entry from the Zookeeper queue: " + getQueueFolderPath(), e);
        }
    }

    @Override
    public T poll() {
        try {
            Map<String, T> entries = readQueueInternal(1, true, 0L);
            Iterator<Map.Entry<String, T>> itr = entries.entrySet().iterator();
            if (itr.hasNext()) {
                return itr.next().getValue();
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public T element() {
        T element = peek();
        if (element == null) {
            throw new DistributedQueueException("The Zookeeper queue was empty." + getQueueFolderPath());
        }
        return element;
    }

    @Override
    public T peek() {
        try {
            Map<String, T> elements = readQueueInternal(1, false, 0L);
            Iterator<Map.Entry<String, T>> entries = elements.entrySet().iterator();
            if (entries.hasNext()) {
                return entries.next().getValue();
            }
            
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public int size() {
        DistributedLock lock = getQueueAccessLock();
        try {
            lock.lockInterruptibly();
            try {
                return executeOperation(new GenericOperation<Integer>() {
                    @Override
                    public Integer execute() throws Exception {
                        final Stat stat = new Stat();
                        getZookeeperClient().getData(getQueueEntryFolder(), null, stat);
                        return stat.getNumChildren();
                    }
                });
                
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
                Map<String, T> elements = readQueueInternal(geMaxCapacity(), false, 0L);
                if (!elements.isEmpty()) {
                    return elements.values().containsAll(c);
                }
                
                return false;
                
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
                        List<String> entryNames = getZookeeperClient().getChildren(getQueueEntryFolder(), null);
                        if (entryNames != null) {
                            for (String entry : entryNames) {
                                getZookeeperClient().delete(getQueueEntryFolder() + '/' + entry, 0);
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
            final ArrayList<T> lst = new ArrayList<>();
            lst.add(e);
            int count = writeToQueue(lst, 0L);
            if (count != 1) {
                throw new IllegalStateException("The Zookeeper queue was full.");
            } else {
                return true;
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
        Map<String, T> elements = readQueueInternal(1, true, -1L);
        Iterator<Map.Entry<String,T>> itr = elements.entrySet().iterator();
        if (itr.hasNext()) {
            return itr.next().getValue();
        }
        
        return null;
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        Map<String, T> elements = readQueueInternal(1, true, unit.toMillis(timeout));
        Iterator<Map.Entry<String,T>> itr = elements.entrySet().iterator();
        if (itr.hasNext()) {
            return itr.next().getValue();
        }
        
        return null;
    }

    @Override
    public int remainingCapacity() {
        synchronized (QUEUE_MONITOR) {
            final int cap = geMaxCapacity() - size();
            if (cap < 0) {
                return 0;
            }
            return cap;
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            DistributedLock lock = getQueueAccessLock();
            lock.lockInterruptibly();
            try {
                Map<String, T> entries = readQueueInternal(geMaxCapacity(), false, 0L);
                if (!entries.isEmpty()) {
                    Iterator<Map.Entry<String, T>> itr = entries.entrySet().iterator();
                    while (itr.hasNext()) {
                        final Map.Entry<String, T> entry = itr.next();
                        if (entry.getValue().equals(o)) {
                            executeOperation(new GenericOperation<Void>() {
                                @Override
                                public Void execute() throws Exception {
                                    getZookeeperClient().delete(getQueueEntryFolder() + '/' + entry.getKey(), 0);
                                    return null;
                                }
                            });
                            
                            return true;
                        }
                        
                    }
                }
                return false;
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean contains(Object o) {
        return containsAll(Collections.singletonList(o));
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        try {
            Map<String, T> entries = readQueueInternal(geMaxCapacity(), true, 0L);
            c.addAll(entries.values());
            return entries.size();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        try {
            Map<String,T> entries = readQueueInternal(maxElements, true, 0L);
            c.addAll(entries.values());
            return entries.size();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
                boolean locked = false;
                DistributedLock lock = getQueueAccessLock();
                if (timeout < 0L) {
                    lock.lockInterruptibly();
                    locked = true;
                } else if (timeout > 0L && waitTime > 0L) {
                    long start = System.currentTimeMillis();
                    locked = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
                    long end = System.currentTimeMillis();
                    waitTime -= (end - start);
                } else {
                    locked = lock.tryLock();
                    if (! locked) {
                        return entryCount;
                    }
                }
                
                if (locked) {
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
                                            ZookeeperUtil.makePath(getQueueEntryFolder() + '/' + getQueueEntryName(), data, getZookeeperClient(), CreateMode.PERSISTENT_SEQUENTIAL, getAcls());
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
                            locked = false;
                            
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
                        if (locked) {
                            lock.unlock();
                        }
                    }
                } else if (timeout >= 0L && waitTime <= 0L) {
                    return entryCount;  
                }
            }
        }
    }
    
    protected Map<String, T> readQueueInternal(final int qty, final boolean remove, final long timeout) throws InterruptedException {
        final Map<String, T> out = new LinkedHashMap<>();
        long waitTime = timeout;
        synchronized (QUEUE_MONITOR) {
            while (true) {
                boolean locked;
                DistributedLock lock = getQueueAccessLock();
                if (timeout < 0L) {
                    lock.lockInterruptibly();
                    locked = true;
                } else if (timeout > 0L && waitTime > 0L) {
                    long start = System.currentTimeMillis();
                    locked = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
                    long end = System.currentTimeMillis();
                    waitTime -= (end - start);
                } else {
                    locked = lock.tryLock();
                    if (!locked) {
                        return out;
                    }
                }
                
                if (locked) {
                    try {
                        List<String> entryNames = executeOperation(new GenericOperation<List<String>>() {
                            @Override
                            public List<String> execute() throws Exception {
                                return getZookeeperClient().getChildren(getQueueEntryFolder(), new Watcher() {
                                    @Override
                                    public void process(WatchedEvent event) {
                                        synchronized (QUEUE_MONITOR) {
                                            QUEUE_MONITOR.notifyAll();
                                        }
                                    }
                                    
                                });
                            }
                        });
                        
                        if (entryNames != null && ! entryNames.isEmpty()) {
                            Collections.sort(entryNames);
                            
                            int count = 0;
                            for (final String entryName : entryNames) {
                                try {
                                    T entry = executeOperation(new GenericOperation<T>() {
                                        @Override
                                        public T execute() throws Exception {
                                            byte[] data = getZookeeperClient().getData(getQueueEntryFolder() + '/' + entryName, null, null);
                                            
                                            @SuppressWarnings("unchecked")
                                            T deserialized = (T)deserialize(data);
                                            
                                            if (remove) {
                                                getZookeeperClient().delete(getQueueEntryFolder() + '/' + entryName, 0);
                                            }
                                            
                                            return deserialized;
                                        }
                                    });
                                    
                                    if (entry != null) {
                                        count++;
                                        out.put(entryName, entry);
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
                            
                            return out;
                        } else {
                            //Unlock here so that we're not holding the lock while we wait...
                            lock.unlock();
                            locked = false;
                            
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
                        if (locked) {
                            lock.unlock();
                        }
                    }
                } else if (timeout >= 0L && waitTime <= 0L) {
                    return out;  
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
                    ZookeeperUtil.makePath(getQueueFolderPath(), null, getZookeeperClient(), CreateMode.PERSISTENT, getAcls());
                    return null;
                }
            });
            
            executeOperation(new GenericOperation<Void>() {
                @Override
                public Void execute() throws Exception {
                    //Folder to hold the queue elements...
                    ZookeeperUtil.makePath(getQueueEntryFolder(), null, getZookeeperClient(), CreateMode.PERSISTENT, getAcls());
                    return null;
                }
            });
            
            executeOperation(new GenericOperation<Void>() {
                @Override
                public Void execute() throws Exception {
                    //Folder to hold the locks...
                    ZookeeperUtil.makePath(getLocksFolder(), null, getZookeeperClient(), CreateMode.PERSISTENT, getAcls());
                    return null;
                }
            });
            
            executeOperation(new GenericOperation<Void>() {
                @Override
                public Void execute() throws Exception {
                    //Folder to hold the queue state...
                    ZookeeperUtil.makePath(getConfigsFolder(), null, getZookeeperClient(), CreateMode.PERSISTENT, getAcls());
                    return null;
                }
            });
        } catch (InterruptedException e) {
            throw new DistributedLockException("SolrZkClient encountered an error trying to create the persistent path, " 
                    + getQueueFolderPath() + " in Zookeeper.", e);
        }
    }
    
    protected synchronized void determineMaxCapacity() {
        final DistributedLock lock = getConfigLock();
        try {
            lock.lockInterruptibly();
            try {
                executeOperation(new GenericOperation<Void>() {
                    @Override
                    public Void execute() throws Exception {
                        if (getZookeeperClient().exists(getConfigsFolder() + "/maxCapacity", false) == null) {
                            final int size = getRequestedMaxQueueSize();
                            ZookeeperUtil.makePath(getConfigsFolder() + "/maxCapacity", serialize(size), getZookeeperClient(), CreateMode.EPHEMERAL, getAcls());
                            seMaxCapacity(size);
                        } else {
                            final Integer size = (Integer)deserialize(getZookeeperClient().getData(getConfigsFolder() + "/maxCapacity", new Watcher() {
                                @Override
                                public void process(WatchedEvent event) {
                                    //This happens in a callback on another thread, allowing us to avoid an infinite recursive loop.
                                    try {
                                        determineMaxCapacity();
                                    } catch (Exception e) {
                                        LOG.error("An error occured in a callback to determine the max queue size.", e);
                                        if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                                
                            }, null));
                            seMaxCapacity(size);
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
        return new ReentrantDistributedZookeeperLock(getZookeeperClient(), getLocksFolder() + "/access", "queueAccessLock", false, getAcls());
    }
    
    protected DistributedLock initializeConfigLock() {
        return new ReentrantDistributedZookeeperLock(getZookeeperClient(), getLocksFolder() + "/config", "configLock", false, getAcls());
    }
    
    protected int getRequestedMaxQueueSize() {
        return requestedMaxQueueCapacity;
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
    
    protected int geMaxCapacity() {
        synchronized (QUEUE_MONITOR) {
            return capacity;
        }
    }
    
    protected void seMaxCapacity(int size) {
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
    
    protected ZooKeeper getZookeeperClient() {
        return zk;
    }
    
    protected List<ACL> getAcls() {
        return acls;
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
            return GenericOperationUtil.executeRetryableOperation(operation, 5, 100L, true, null);
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
