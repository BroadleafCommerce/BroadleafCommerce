
package org.broadleafcommerce.core.util.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
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
 * @author Kelly Tisdell
 *
 */
public class ReentrantDistributedZookeeperLock implements DistributedLock {
    
    private static final Log LOG = LogFactory.getLog(ReentrantDistributedZookeeperLock.class);
    
    public static final String GLOBAL_ENV_CAN_OBTAIN_LOCK_PROPERTY = ReentrantDistributedZookeeperLock.class.getName() + ".canParticipate";
    
    /**
     * This is the base folder that all locks will be written to in Zookeeper.  The constructors require a lock path, which will be appended 
     * to this path.
     */
    public static final String DEFAULT_BASE_FOLDER = "/broadleaf/app/distributed-locks";
    
    private final ThreadLocal<AtomicInteger> THREAD_LOCK_PERMITS = new ThreadLocal<>();
    
    private final Object NON_PARTICIPANT_LOCK_MONITOR = new Object();
    private final Object LOCK_MONITOR = new Object();
    private final SolrZkClient zk;
    private final Environment env;
    
    private final String lockName;
    private final String lockFolderPath;
    private final String lockAccessPropertyName;
    
    private String currentlockPath;
    
    /**
     * This constructor takes in the {@link SolrZkClient} (non-nullable), 
     * the lock path (non-nullable and in the format of '/path/to/this/lock/folder`), 
     * and the lock name (non-nullable and non-empty string with no whitespaces, leading or trailing slashes, or special characters except '-' or '_').
     * 
     * This {@link Lock} will, by default, participate in or be allowed to acquire a lock.
     * 
     * @param zk
     * @param lockPath
     * @param lockName
     */
    public ReentrantDistributedZookeeperLock(SolrZkClient zk, String lockPath, String lockName) {
        this(zk, lockPath, lockName, null);
    }
    
    /**
     * This constructor takes in the {@link SolrZkClient} (non-nullable), 
     * the lock path (non-nullable and in the format of '/path/to/this/lock/folder`), 
     * the lock name (non-nullable and non-empty string with no whitespaces, leading or trailing slashes, or special characters except '-' or '_'), and 
     * an {@link Environment} object, which can be null.
     * 
     * This {@link Lock} will, by default, participate in or be allowed to acquire a lock if the {@link Environment} argument is null or 
     * if the 'org.broadleafcommerce.core.util.lock.ReentrantDistributedZookeeperLock.${lockName}.canParticipate' property is not set or is set to false.
     * 
     * @param zk
     * @param lockPath
     * @param lockName
     * @param env
     */
    public ReentrantDistributedZookeeperLock(SolrZkClient zk, String lockPath, String lockName, Environment env) {
        Assert.notNull(zk, "SolrKzClient cannot be null.");
        Assert.isTrue(zk.isConnected(), "Please ensure that the SolrZkClient is connected.");
        Assert.notNull(lockName, "The lockName cannot be null.");
        Assert.notNull(lockPath, "The lockPath cannot be null and must be a Unix-style path (e.g. '/solr-index/command-lock').");
        
        this.lockName = lockName.trim();
        Assert.hasText(this.lockName, "The lockName must not be empty and should not contain white spaces.");
        
        Assert.hasText(lockPath.trim(), "The lockPath must not be empty and should not contain white spaces.");
        this.zk = zk;
        this.env = env;
        
        this.lockAccessPropertyName = ReentrantDistributedZookeeperLock.class.getName() + '.' + this.lockName + ".canParticipate";
        
        if (lockPath.trim().startsWith("/")) {
            this.lockFolderPath = DEFAULT_BASE_FOLDER + lockPath.trim();
        } else {
            this.lockFolderPath = DEFAULT_BASE_FOLDER + '/' + lockPath.trim();
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
        if (THREAD_LOCK_PERMITS.get() == null || THREAD_LOCK_PERMITS.get().get() < 1) {
            throw new DistributedLockException("The current thread did not contain this lock and thereforer cannot unlock it.");
        }
        
        if (THREAD_LOCK_PERMITS.get().get() > 1) {
            //Decrement the lock access but don't eliminate the lock from Zookeeper.
            //This allows it to be reentrant.
            THREAD_LOCK_PERMITS.get().decrementAndGet();
            return;
        }
        
        try {
            synchronized (LOCK_MONITOR) {
                zk.delete(currentlockPath, -1, true);
                
                if (THREAD_LOCK_PERMITS.get().decrementAndGet() < 1) {
                    THREAD_LOCK_PERMITS.remove();
                }
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ZK lock was released by " + Thread.currentThread().getName() + ". The lock name was " + currentlockPath);
                }
                currentlockPath = null;
            }
        } catch (KeeperException | InterruptedException e) {
            LOG.error("An error occured trying to unlock a distributed lock stored in Zookeeper.  "
                    + "The lock has not been released and manual intervention may be required.  Lock path is: " + currentlockPath, e);
            if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                Thread.currentThread().interrupt();
            }
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
                    wait(); 
                }
            } else if (waitTime > 0L) {
                //Simulate normal lock semantics,where the lock is unavailable, but we've been asked to wait interruptably for it for a period of time.
                Thread.sleep(waitTime);
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
            final String localLockPath = zk.create(getLockFolderPath() + '/' + getLockName(), null, CreateMode.EPHEMERAL_SEQUENTIAL, true);
            synchronized (LOCK_MONITOR) {
                boolean waitCompleted = false; //Allows us to avoid waiting indefinitely if the client specified a wait time.
                while(true) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    List<String> nodes = zk.getChildren(getLockFolderPath(), new Watcher() {
                        @Override
                        public void process(WatchedEvent event) {
                            synchronized (LOCK_MONITOR) {
                                //This will be executed in a callback on another thread, managed by the ZK client.
                                LOCK_MONITOR.notifyAll();
                            }
                        }
                    }, true);
                    
                   // ZooKeeper node names can be sorted.
                    Collections.sort(nodes); 
                    if (localLockPath.endsWith(nodes.get(0))) {
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
                            zk.delete(localLockPath, 0, true);
                            return false;
                        } else if (waitTime < 0L) {
                            //Wait indefinitely.  If this notified (typically by the Watcher, above), then it will try to obtain the lock.
                            LOCK_MONITOR.wait();
                        } else {
                            if (waitCompleted) {
                                //We already waited the specified time, so don't wait again.  
                                //The caller specified a wait time, and we already waited so we'll just return.
                                zk.delete(localLockPath, 0, true);
                                return false;
                            }
                            //Wait for a specified period of time.
                            LOCK_MONITOR.wait(waitTime);
                            //Indicate that we've already waited for the specified time so that we don't wait again.
                            waitCompleted = true; 
                        }
                    }
                }
            }
        } catch (KeeperException e) {
            LOG.error("Error occured trying to obtain a distributed lock from Zookeeper.", e);
            return false;
        }
    }
    
    /**
     * Creates the appropriate folder(s) in Zookeeper if they don't already exist.
     */
    protected synchronized void initialize() {
        try {
            zk.makePath(getLockFolderPath(), false, true);
        } catch (InterruptedException | KeeperException e) {
            if (InterruptedException.class.isAssignableFrom(e.getClass())) {
                Thread.currentThread().interrupt();
            }
            throw new DistributedLockException("SolrZkClient encountered an error trying to create the persistent path, " 
                    + getLockFolderPath() + " in Zookeeper.", e);
        }
    }
    
    /**
     * Allows one to disable this locking mechansim via the 'org.broadleafcommerce.core.util.lock.ReentrantDistributedZookeeperLock.${lockName}.canParticipate' property, which is true, by default. 
     * If this property is set to false, then a lock will never be obtained.  This allows only certain environments or certain nodes (JVMs) to obtain the lock.
     * 
     * If this method returns false, then the locking mechanisms continue to work.  However, the lock will always be locked and will never allow the acquisition of a lock.
     * 
     * @return
     */
    @Override
    public boolean canParticipate() {
        if (getEnvironment() != null) {
            Boolean result = getEnvironment().getProperty(GLOBAL_ENV_CAN_OBTAIN_LOCK_PROPERTY, Boolean.class);
            if (result == null || result == true) {
                result = getEnvironment().getProperty(lockAccessPropertyName, Boolean.class, true);
            }
            return result;
        }
        
        return true;
    }
    
    protected Environment getEnvironment() {
        return env;
    }
    
    protected String getLockName() {
        return lockName;
    }
    
    protected String getLockFolderPath() {
        return lockFolderPath;
    }
}