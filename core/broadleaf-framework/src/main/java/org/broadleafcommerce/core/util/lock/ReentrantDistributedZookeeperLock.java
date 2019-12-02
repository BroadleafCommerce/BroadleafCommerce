
package org.broadleafcommerce.core.util.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.broadleafcommerce.common.util.GenericOperation;
import org.broadleafcommerce.common.util.GenericOperationUtil;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is similar to a {@link ReentrantLock}, except that it uses Zookeeper (and, specifically, {@link CloudSolrClient}'s {@link SolrZkClient}) to 
 * share the lock state across multiple nodes, allowing for a distributed lock.  The owning thread may acquire the lock multiple times, but must unlock for each time the lock is 
 * acquired.  It's important to get in the habit of unlocking immediately in a finally block to prevent orphaned locks:
 * 
 * <code>
 * Lock lock = new ReentrantDistributedZookeeperLock(zk, "/solr-update/locks", "solrUpdate_commandLock");
 * lock.lockInterruptibly();  //This will block until a lock is acquired or until a 
 * try {
 *     //Do something in a globally locked state
 * } finally {
 *     lock.unlock();
 * }
 * </code>
 * <p>
 * or 
 * <p>
 * <code>
 * Lock lock = new ReentrantDistributedZookeeperLock(zk, "/solr-update/locks", "solrUpdate_commandLock");
 * if (lock.tryLock()) {
 *     try {
 *         //Do something in a globally locked state
 *     } finally {
 *         lock.unlock();
 *     }
 * }
 * </code>
 * 
 * In terms of performance, the average lock time, on a single quad-core laptop running a single Zookeeper instance, takes about 7-8 milliseconds. 
 * And the average unlock time also takes around 7-8 milliseconds during a simple test on a quad core laptop that was also running a single Zookeeper instance.
 * This is with a single thread, and is assuming no reentrant locks and no contention or waiting for locks to become available.  
 * In other words, disregarding blocking and contention, it will likely take, on average, 14-16 milliseconds to create and release a lock 
 * (approximately 55-65 locks and releases / second for any given lock). These metrics will depend on a number of things, including contention for locks, 
 * Zookeeper cluster size and configuration, hardware, etc.
 * 
 * @author Kelly Tisdell
 *
 */
public class ReentrantDistributedZookeeperLock implements DistributedLock {
    
    private static final Log LOG = LogFactory.getLog(ReentrantDistributedZookeeperLock.class);
    
    /**
     * This is the base folder that all locks will be written to in Zookeeper.  The constructors require a lock path, which will be appended 
     * to this path.
     */
    public static final String DEFAULT_BASE_FOLDER = "/broadleaf/app/distributed-locks";
    
    /**
     * This is the prefix of any lock entry.  It is dzlck-.  A lock file (node) will be dzlck-mylock, for example.
     */
    public static final String LOCK_PREFIX = "dzlck-";
    
    /**
     * For performance reasons, any paths that have already been created will not be attempted to be created again.
     */
    private static final Set<String> INITIALIZED_LOCK_PATHS = Collections.synchronizedSet(new HashSet<String>());
    
    /*
     * Allows reentrant locking.  If the current thread already has a lock, then additional attempts at locking or unlocking will be incremented or decremented here.
     */
    private final ThreadLocal<AtomicInteger> THREAD_LOCK_PERMITS = new ThreadLocal<>();
    
    /*
     * This is a synchronization monitor for threads, lock names, or environments for which this lock cannot be obtained.
     */
    private final Object NON_PARTICIPANT_LOCK_MONITOR = new Object();
    
    /*
     * This is a synchronization monitor for normal lock and unlock operations.
     */
    private final Object LOCK_MONITOR = new Object();
    
    /*
     * This provides the interface to Zookeeper.
     */
    private final SolrZkClient zk;
    
    /*
     * Optional Sping Environment object to assist in determining if the lock can be obtained.  This is 
     * nullable, and if this is null then the default is to allow the lock to be obtained.
     */
    private final Environment env;
    
    /*
     * Basic lock name (e.g. 'myLock')
     */
    private final String lockName;
    
    /*
     * Full lock name, which includes the LOCK_PREFIX (e.g. 'dzlck-myLock')
     */
    private final String fullLockName;
    
    /*
     * Path to the lock folder.  Typically, /broadleaf/app/distributed-locks/path/to/my/lock-folder
     */
    private final String lockFolderPath;
    
    /*
     * Property name, whose value should be null or boolean, to determine if this lock can be obtained by this environment.
     * E.g. org.broadleafcommerce.core.util.lock.DistributedLock.${getLockName()}.canParticipate=true
     */
    private final String lockAccessPropertyName;
    
    /*
     * This is the full path to the ephemeral node in Zookeeper that represents the lock.
     * E.g. /broadleaf/app/distributed-locks/path/to/my/lock-folder/dzlck-myLock00000000001
     * 
     */
    private String currentlockPath;
    
    /**
     * This constructor takes in the {@link SolrZkClient} (non-nullable), 
     * the lock path (non-nullable and in the format of '/path/to/this/lock/folder`), 
     * and the lock name (non-nullable and non-empty string with no whitespaces, leading or trailing slashes, or special characters except '-' or '_').
     * 
     * This {@link Lock} will, by default, participate in or be allowed to acquire a lock.
     * 
     * The lockPath will be prepended with '/broadleaf/app/distributed-locks'.
     * 
     * @param zk
     * @param lockPath
     * @param lockName
     */
    public ReentrantDistributedZookeeperLock(SolrZkClient zk, String lockPath, String lockName) {
        this(zk, lockPath, lockName, null, true);
    }
    
    /**
     * This constructor takes in the {@link SolrZkClient} (non-nullable), 
     * the lock path (non-nullable and in the format of '/path/to/this/lock/folder`), 
     * and the lock name (non-nullable and non-empty string with no whitespaces, leading or trailing slashes, or special characters except '-' or '_').
     * 
     * This {@link Lock} will, by default, participate in or be allowed to acquire a lock.
     * 
     * If use defaultBasePath is true, then the lockPath will be prepended with '/broadleaf/app/distributed-locks'.
     * 
     * @param zk
     * @param lockPath
     * @param lockName
     */
    public ReentrantDistributedZookeeperLock(SolrZkClient zk, String lockPath, String lockName, boolean useDefaultBasePath) {
        this(zk, lockPath, lockName, null, useDefaultBasePath);
    }
    
    /**
     * This constructor takes in the {@link SolrZkClient} (non-nullable), 
     * the lock path (non-nullable and in the format of '/path/to/this/lock/folder`), 
     * the lock name (non-nullable and non-empty string with no whitespaces, leading or trailing slashes, or special characters except '-' or '_'), and 
     * an {@link Environment} object, which can be null.
     * 
     * This {@link Lock} will, by default, participate in or be allowed to acquire a lock if the {@link Environment} argument is null or 
     * if the 'org.broadleafcommerce.core.util.lock.DistributedLock.${lockName}.canParticipate' property is not set or is set to false.
     * 
     * The lockPath will be prepended with '/broadleaf/app/distributed-locks'.
     * 
     * @param zk
     * @param lockPath
     * @param lockName
     * @param env
     */
    public ReentrantDistributedZookeeperLock(SolrZkClient zk, String lockPath, String lockName, Environment env) {
        this(zk, lockPath, lockName, env, true);
    }
    
    /**
     * This constructor takes in the {@link SolrZkClient} (non-nullable), 
     * the lock path (non-nullable and in the format of '/path/to/this/lock/folder`), 
     * the lock name (non-nullable and non-empty string with no whitespaces, leading or trailing slashes, or special characters except '-' or '_'), and 
     * an {@link Environment} object, which can be null.
     * 
     * This {@link Lock} will, by default, participate in or be allowed to acquire a lock if the {@link Environment} argument is null or 
     * if the 'org.broadleafcommerce.core.util.lock.DistributedLock.${lockName}.canParticipate' property is not set or is set to false.
     * 
     * If use defaultBasePath is true, then the lockPath will be prepended with '/broadleaf/app/distributed-locks'.
     * 
     * @param zk
     * @param lockPath
     * @param lockName
     * @param env
     * @param useDefaultBasePath
     */
    public ReentrantDistributedZookeeperLock(SolrZkClient zk, String lockPath, String lockName, Environment env, boolean useDefaultBasePath) {
        Assert.notNull(zk, "SolrKzClient cannot be null.");
        Assert.isTrue(zk.isConnected(), "Please ensure that the SolrZkClient is connected.");
        Assert.notNull(lockName, "The lockName cannot be null.");
        Assert.notNull(lockPath, "The lockPath cannot be null and must be a Unix-style path (e.g. '/solr-index/command-lock').");
        
        lockName = lockName.trim();
        Assert.hasText(lockName, "The lockName must not be empty and should not contain white spaces.");
        this.lockName = lockName;
        
        this.fullLockName = LOCK_PREFIX + this.lockName;
        
        Assert.hasText(lockPath.trim(), "The lockPath must not be empty and should not contain white spaces.");
        this.zk = zk;
        this.env = env;
        
        this.lockAccessPropertyName = DistributedLock.class.getName() + '.' + this.lockName + ".canParticipate";
        
        if (useDefaultBasePath) {
            if (lockPath.trim().startsWith("/")) {
                this.lockFolderPath = DEFAULT_BASE_FOLDER + lockPath.trim();
            } else {
                this.lockFolderPath = DEFAULT_BASE_FOLDER + '/' + lockPath.trim();
            }
        } else {
            this.lockFolderPath = lockPath.trim();
        }
        
        initialize();
    }

    @Override
    public void lock() {
        try {
            lockInternally(-1L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DistributedLockException("Thread was interruptted trying to obtain distributed lock from Zookeeper.", e);
        }
    }

    public void unlock() {
        try {
            synchronized (LOCK_MONITOR) {
                if (!currentThreadHoldsLock()) {
                    throw new DistributedLockException("The current thread did not obtain this lock and thereforer cannot unlock it.");
                }
                
                if (THREAD_LOCK_PERMITS.get().get() > 1) {
                    //Decrement the lock access but don't eliminate the lock from Zookeeper.
                    //This allows it to be reentrant.
                    THREAD_LOCK_PERMITS.get().decrementAndGet();
                    return;
                }
                
                GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {
                    @Override
                    public Void execute() throws Exception {
                        getSolrZkClient().delete(currentlockPath, -1, true);
                        return null;
                    }
                }, getFailureRetries(), getRetryWaitTime(), isAdditiveWaitTtimes(), null); 
                
                
                if (THREAD_LOCK_PERMITS.get().decrementAndGet() < 1) {
                    THREAD_LOCK_PERMITS.remove();
                }
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ZK lock was released by " + Thread.currentThread().getName() + ". The lock name was " + currentlockPath);
                }
                
                currentlockPath = null;
            }
        } catch (Exception e) {
            LOG.error("An error occured trying to unlock a distributed lock stored in Zookeeper.  "
                    + "The lock has not been released and manual intervention may be required.  Lock path is: " + currentlockPath, e);
            if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                Thread.currentThread().interrupt();
            }
            throw new DistributedLockException("An error occured trying to unlock a distributed lock stored in Zookeeper. " + 
                    "The lock has not been released and manual intervention may be required.  Lock path is: " + currentlockPath, e);
        } 
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("Thread was interrupted prior to trying to acquire the lock.");
        }
        
        lockInternally(-1L);
    }

    @Override
    public boolean tryLock() {
        try {
            return lockInternally(0L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (time < 0L) {
            throw new IllegalArgumentException("Wait time must be positive.");
        }
        
        try {
            return lockInternally(TimeUnit.MILLISECONDS.convert(time, unit));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("By default, conditions are not supported with this type of lock.");
    }
    
    /**
     * Negative number means wait indefinitely (typically until an {@link UnsupportedOperationException} is thrown.
     * Zero (0) means don't wait.
     * Positive number means wait for that number of millis.
     * @param waitTime
     * @throws InterruptedException
     */
    protected boolean lockInternally(long waitTime) throws InterruptedException {
        if (!canParticipate()) {
            //No lock will be provided in this case, but we want to simulate the normal lock semantics.
            if (waitTime < 0L) {
                //Simulate normal lock semantics,where the lock is unavailable, but we've been asked to wait interruptably for it indefinitely.
                synchronized (NON_PARTICIPANT_LOCK_MONITOR) {
                    //This basically will cause this thread to block forever until the thread is interrupted, which is what we want.
                    NON_PARTICIPANT_LOCK_MONITOR.wait(); 
                }
            } else if (waitTime > 0L) {
                //Simulate normal lock semantics, where the lock is unavailable, but we've been asked to wait interruptably for it for a period of time.
                synchronized (NON_PARTICIPANT_LOCK_MONITOR) {
                    NON_PARTICIPANT_LOCK_MONITOR.wait(waitTime);
                }
            }
            
            return false;
        }
        
        //See if this thread already has a lock permit.  If so, just increment the count and return it.
        //No need to interact with Zookeeper.
        if (THREAD_LOCK_PERMITS.get() != null) {
            THREAD_LOCK_PERMITS.get().incrementAndGet();
            return true;
        }
        
        try {
            //Create a lock reference in Zookeeper.  It looks something like /broadleaf/app/distributed-locks/path/to/my/locks/myLock000000000015
            //The sequential part of this guaranteed by Zookeeper to be unique.  Creating this file does not guarantee that a lock has been acquired.
            final String localLockPath = GenericOperationUtil.executeRetryableOperation(new GenericOperation<String>() {

                @Override
                public String execute() throws Exception {
                    return getSolrZkClient().create(getLockFolderPath() + '/' + getFullLockName(), null, CreateMode.EPHEMERAL_SEQUENTIAL, true);
                }
                
            }, getFailureRetries(), getRetryWaitTime(), isAdditiveWaitTtimes(), null);
            
            synchronized (LOCK_MONITOR) {
                boolean waitCompleted = false; //Allows us to avoid waiting indefinitely if the client specified a wait time.
                while(true) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    
                    final List<String> nodes = GenericOperationUtil.executeRetryableOperation(new GenericOperation<List<String>>() {
                        
                        @Override
                        public List<String> execute() throws Exception {
                            return getSolrZkClient().getChildren(getLockFolderPath(), new Watcher() {
                                @Override
                                public void process(WatchedEvent event) {
                                    synchronized (LOCK_MONITOR) {
                                        //This will be executed in a callback on another thread, managed by the ZK client.
                                        LOCK_MONITOR.notifyAll();
                                    }
                                }
                            }, true);
                        }
                        
                    }, getFailureRetries(), getRetryWaitTime(), isAdditiveWaitTtimes(), null);
                    
                     // ZooKeeper node names can be sorted.
                    Collections.sort(nodes);
                    String firstLocked = null;
                    for (String node : nodes) {
                        if (node.startsWith(getFullLockName())) {
                            firstLocked = node;
                            break;
                        }
                    }
                    
                    if (firstLocked != null && localLockPath.endsWith(firstLocked)) {
                        //We got the lock so increment the fact that this thread has 1 permit.
                        AtomicInteger counter = THREAD_LOCK_PERMITS.get();
                        if (counter == null) {
                            counter = new AtomicInteger();
                            THREAD_LOCK_PERMITS.set(counter);
                        }
                        
                        counter.incrementAndGet();
                        
                        //Set the currentLockPath with the one that we just obtained.
                        currentlockPath = localLockPath;
                        
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ZK lock was acquired by " + Thread.currentThread().getName() + ". The lock name is " + currentlockPath);
                        }
                        
                        return true;
                    } else {
                        if (waitTime == 0L) {
                            //No need to try again, the caller does not want to wait.
                            try {
                                //Do some cleanup
                                return GenericOperationUtil.executeRetryableOperation(new GenericOperation<Boolean>() {
    
                                    @Override
                                    public Boolean execute() throws Exception {
                                        getSolrZkClient().delete(localLockPath, 0, true);
                                        return false;
                                    }
                                }, getFailureRetries(), getRetryWaitTime(), isAdditiveWaitTtimes(), null);
                            } catch (Exception e) {
                                LOG.warn("Error occured trying to delete a temporary distributed lock file in Zookeeper", e);
                                return false;
                            }
                        } else if (waitTime < 0L) {
                            //Wait indefinitely.  If this notified (typically by the Watcher, above), then it will try to obtain the lock.
                            LOCK_MONITOR.wait();
                        } else {
                            if (waitCompleted) {
                                //We already waited the specified time, so don't wait again.  
                                //The caller specified a wait time, and we already waited so we'll just return false since we did not obtain the lock.
                                try {
                                    //Do some cleanup
                                    return GenericOperationUtil.executeRetryableOperation(new GenericOperation<Boolean>() {
    
                                        @Override
                                        public Boolean execute() throws Exception {
                                            getSolrZkClient().delete(localLockPath, 0, true);
                                            return false;
                                        }
                                    }, getFailureRetries(), getRetryWaitTime(), isAdditiveWaitTtimes(), null);
                                } catch (Exception e) {
                                    LOG.warn("Error occured trying to delete a temporary distributed lock file in Zookeeper", e);
                                    return false;
                                }
                            }
                            //Indicate that we've already waited for the specified time so that we don't wait again.
                            waitCompleted = true; 
                            //Wait for a specified period of time.
                            LOCK_MONITOR.wait(waitTime);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error occured trying to obtain a distributed lock from Zookeeper.", e);
            if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                throw (InterruptedException)e;
            }
            
            return false;
        }
    }
    
    /**
     * Creates the appropriate folder(s) in Zookeeper if they don't already exist.
     */
    protected synchronized void initialize() {
        try {
            if (INITIALIZED_LOCK_PATHS.contains(getLockFolderPath())) {
                return;
            }
            
            GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {

                @Override
                public Void execute() throws Exception {
                    getSolrZkClient().makePath(getLockFolderPath(), false, true);
                    return null;
                }
            }, getFailureRetries(), getRetryWaitTime(), isAdditiveWaitTtimes(), null);
            
            INITIALIZED_LOCK_PATHS.add(getLockFolderPath());
        } catch (Exception e) {
            if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                Thread.currentThread().interrupt();
            }
            throw new DistributedLockException("SolrZkClient encountered an error trying to create the persistent path, " 
                    + getLockFolderPath() + " in Zookeeper.", e);
        }
    }
    
    /**
     * Allows one to disable this locking mechanism via the 'org.broadleafcommerce.core.util.lock.DistributedLock.canParticipate' (globally) or the 
     * 'org.broadleafcommerce.core.util.lock.DistributedLock.${lockName}.canParticipate' property.  Both are true by default. 
     * If either of these properties are set to false, then this lock will never be obtained.  This allows only certain environments or certain nodes (JVMs) to obtain the lock 
     * while preventing others.  Assuming the {@link Environment} is not null, this first checks the more specific 
     * 'org.broadleafcommerce.core.util.lock.DistributedLock.${lockName}.canParticipate' property.  If that is true or 
     * null, then it checks the more global 'org.broadleafcommerce.core.util.lock.DistributedLock.canParticipate'.  If that is true or null then it returns true.
     * 
     * By default, this method returns true if the {@link Environment} is null or if both properties are null.
     * 
     * If this method returns false, then the locking mechanisms and semantics will continue to work.  
     * However, the lock will always be locked and will never allow the acquisition of a lock.
     * 
     * @return
     */
    @Override
    public boolean canParticipate() {
        if (getEnvironment() != null) {
            //Check the specific lock name first to see if this particular lock is allowed here.
            boolean lockNameParticipation = getEnvironment().getProperty(getLockAccessPropertyName(), Boolean.class, true);
            if (lockNameParticipation) {
                //Now, check the global access in this environment.
                return getEnvironment().getProperty(DistributedLock.GLOBAL_ENV_CAN_OBTAIN_LOCK_PROPERTY_NAME, Boolean.class, true);
            } else {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean currentThreadHoldsLock() {
        synchronized (LOCK_MONITOR) {
            return THREAD_LOCK_PERMITS.get() != null && THREAD_LOCK_PERMITS.get().get() > 0;
        }
    }

    protected SolrZkClient getSolrZkClient() {
        return zk;
    }
    
    protected Environment getEnvironment() {
        return env;
    }
    
    /**
     * The lock name provided in the constructor.
     * @return
     */
    protected String getLockName() {
        return lockName;
    }
    
    /**
     * Returns LOCK_PREFIX + getLockName()
     * 
     * E.g. dzlck-myLock
     * 
     * @return
     */
    protected String getFullLockName() {
        return fullLockName;
    }
    
    protected String getLockFolderPath() {
        return lockFolderPath;
    }
    
    /**
     * This will retry any interactions with Zookeeper this many times.  Must be a positive number.  Zero (0) is OK.
     * @return
     */
    protected int getFailureRetries() {
        return 5;
    }
    
    /**
     * If an interaction with Zookeeper fails, it will retry and this will be the pause time, in millis, between retries.
     * @return
     */
    protected long getRetryWaitTime() {
        return 100L;
    }
    
    /**
     * If this is true, the wait time in millis will be incremented by itself each time a retry occurs, up to the retry count.
     * 
     * So, if the retries is 5 and the retryWaitTime is 100, then the max amount of time that the system will wait is 1500:
     * 100 + 200 + 300 + 400 + 500, not including any wait time associated with I/O or execution of the Operation.
     * 
     * @return
     */
    protected boolean isAdditiveWaitTtimes() {
        return true;
    }
    
    protected String getLockAccessPropertyName() {
        return lockAccessPropertyName;
    }
}