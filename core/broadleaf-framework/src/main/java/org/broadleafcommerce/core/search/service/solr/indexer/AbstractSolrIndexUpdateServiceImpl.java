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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;

/**
 * This component is an abstract component that will be extended by more concrete implementations for updating 
 * or reindexing Solr.  This makes use of a single background Thread, per command type (or command identifier - i.e. "catalog") that monitors a command queue, ensuring that all 
 * commands to update or reindex Solr happen serially for a given command identifier, but without blocking the calling thread that is issuing the command.
 * 
 * This component makes use of a {@link Lock} and a {@link BlockingQueue}.  The providers for these components, {@link SolrIndexQueueProvider} and 
 * {@link SolrIndexLockProvider} provide an <code>isDistributed()</code> method.  They must both return the same value.  Note that if 
 * <code>isDistributed()</code> returns false, then care must be taken to ensure that 2 or more nodes (i.e. JVMs) cannot execute at the same time. 
 * This is typically done by ensuring that only a single node will receive calls/events to update a Solr index.
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class AbstractSolrIndexUpdateServiceImpl implements SolrIndexUpdateService, DisposableBean {
    
    private static final Log LOG = LogFactory.getLog(AbstractSolrIndexUpdateServiceImpl.class);
    private static final Map<String, AtomicReferenceArray<Runnable>> commandThreadRegistry = Collections.synchronizedMap(new HashMap<String, AtomicReferenceArray<Runnable>>());
    
    private final String commandGroup;
    private BlockingQueue<? super SolrUpdateCommand> commandQueue;
    private final SolrIndexUpdateCommandHandler commandHandler;
    
    public AbstractSolrIndexUpdateServiceImpl(final String commandGroup, final SolrIndexQueueProvider queueProvider, final SolrIndexUpdateCommandHandler commandHandler) {
        Assert.notNull(commandGroup, "The command group must not be null. Consider using a simple domain identifier such as 'catalog', 'customer', or 'order', for example.");
        Assert.notNull(queueProvider, SolrIndexQueueProvider.class.getName() + " cannot be null.");
        Assert.notNull(commandHandler, SolrIndexUpdateCommandHandler.class.getName() + " cannot be null.");
        
        this.commandGroup = commandGroup.trim();
        Assert.hasLength(getCommandGroup(), "The command group must not be empty. Consider using a simple domain identifier such as 'catalog', 'customer', or 'order', for example.");
        
        Assert.notNull(commandHandler.getCommandGroup().equals(getCommandGroup()), "Command identifiers must match to avoid misconfiguration.");
        this.commandHandler = commandHandler;
        
        this.commandQueue = queueProvider.createOrRetrieveCommandQueue(getCommandGroup() + SolrIndexQueueProvider.COMMAND_QUEUE_NAME);
        Assert.notNull(this.commandQueue, "The commandQueue cannot be null.  Check the " + queueProvider.getClass().getName() + ".");
        
        synchronized (AbstractSolrIndexUpdateServiceImpl.class) {
            if (!commandThreadRegistry.containsKey(getCommandGroup())) {
                final Lock lock = queueProvider.createOrRetrieveCommandLock(getCommandGroup() + SolrIndexQueueProvider.COMMAND_LOCK_NAME);
                Assert.notNull(lock, "The lock cannot be null. Check the " + queueProvider.getClass().getName() + ".");
                
                final CommandCoordinator commandRunnable = new CommandCoordinator(this.commandQueue, lock, commandHandler);
                final Thread commandThread = new Thread(commandRunnable, getCommandGroup() + "-Solr-Index-Update-Command-Master");
                commandThread.start();
                
                AtomicReferenceArray<Runnable> ref = new AtomicReferenceArray<>(2);
                ref.set(0, commandRunnable);
                ref.set(1, commandThread);
                
                commandThreadRegistry.put(getCommandGroup(), ref);
            } else {
                LOG.warn("A command thread has already been registered for the following command group: " + getCommandGroup());
            }
        }
    }
    
    @Override
    public void destroy() throws Exception {
        synchronized (AbstractSolrIndexUpdateServiceImpl.class) {
            AtomicReferenceArray<Runnable> runnables = commandThreadRegistry.remove(getCommandGroup());
            if (runnables != null) {
                ((CommandCoordinator)runnables.get(0)).stopRunning();
                ((Thread)runnables.get(1)).interrupt();
            }
        }
    }
    
    /**
     * Stops all threads that are listening to various command queues.
     */
    public static void shutdownAll() {
        synchronized (AbstractSolrIndexUpdateServiceImpl.class) {
            Set<Entry<String, AtomicReferenceArray<Runnable>>> entries = commandThreadRegistry.entrySet();
            for (Entry<String, AtomicReferenceArray<Runnable>> entry : entries) {
                ((CommandCoordinator)entry.getValue().get(0)).stopRunning();
                ((Thread)entry.getValue().get(1)).interrupt();
            }
            
            commandThreadRegistry.clear();
        }
    }
    
    /**
     * This is any arbitrary name to identify or group commands, typically based on the Solr index (or indexes) being updated.  
     * For example, "catalog" will likely be a command identifier.  It could also be "product", but in less common cases where you want to 
     * index categories or other things with products and you want to serialize those commands, then this can assist.
     * 
     * @return
     */
    public final String getCommandGroup() {
        return commandGroup;
    }
    
    protected final <C extends SolrUpdateCommand> void scheduleCommand(C command) {
        if (!isRunning(getCommandGroup())) {
            throw new IllegalStateException("Attempted to queue a SolrUpdateCommand but " + getClass().getName() + " is not initialized or has been shut down.");
        }
        if (command != null) {
            try {
                for (int i = 0; i < 5; i++) {
                    // If there is not already an equal command in the queue,
                    // then add the command for processing in a background thread.
                    if (!commandQueue.contains(command)) {
                        if (commandQueue.offer(command, getQueueOfferTime(), TimeUnit.MILLISECONDS)) {
                            return;
                        } else if (!isRunning(getCommandGroup())) {
                            throw new IllegalStateException("Attempted to queue a SolrUpdateCommand but " + getClass().getName() + " is not initialized or has been shut down.");
                        }
                    }
                }
                throw new IllegalStateException("Unable to add a Solr index update command to the queue within 5 seconds.");
            } catch (InterruptedException e) {
                throw new RuntimeException("Unexpected error occurred attempting to add a command to the queue.", e);
            }
        }
    }
    
    /**
     * This provides a {@link Runnable} implementation that simply polls the command queue, but only if it can acquire a lock. It continuously attempts to obtain the lock, and 
     * then when it does it continuously pulls commands from the command queue.  This ensures that exactly one thread at a time can access the commands to update Solr.
     * @author Kelly Tisdell
     *
     */
    private class CommandCoordinator implements Runnable {
        
        private final BlockingQueue<? super SolrUpdateCommand> queue;
        private final Lock lock;
        private final SolrIndexUpdateCommandHandler commandHandler;
        private volatile boolean running = false;
        
        CommandCoordinator(BlockingQueue<? super SolrUpdateCommand> queue, Lock lock, SolrIndexUpdateCommandHandler commandHandler) {
            this.queue = queue;
            this.lock = lock;
            this.commandHandler = commandHandler;
        }
        
        @Override
        public void run() {
            try {
                startRunning();
                
                while (isRunning()) {
                    if (Thread.interrupted()) {
                        stopRunning();
                        return;
                    }
                    lock.lockInterruptibly();
                    try {
                        SolrUpdateCommand command;
                        while (isRunning()) {
                            if (Thread.interrupted()) {
                                stopRunning();
                                return;
                            }
                            
                            command = (SolrUpdateCommand)queue.poll(getQueuePollTime(), TimeUnit.MILLISECONDS);
                            
                            if (command != null) {
                                try {
                                    //We're running in a background thread, so let's just set up a new BroadleafRequestContext.
                                    BroadleafRequestContext.setBroadleafRequestContext(new BroadleafRequestContext());
                                    commandHandler.executeCommand(command);
                                } catch (Exception e) {
                                    LOG.error("Unexpected error occured attempting to update a Solr index.", e);
                                } finally {
                                    BroadleafRequestContext.setBroadleafRequestContext(null);
                                }
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                    
                }
                
            } catch (InterruptedException e) {
                stopRunning();
                return;
            }
            
        }
        
        public synchronized boolean isRunning() {
            return running;
        }
        
        public synchronized void stopRunning() {
            this.running = false;
        }
        
        private synchronized void startRunning() {
            this.running = true;
        }
    }
    
    public static boolean isRunning(String commandIdentifier) {
        synchronized (AbstractSolrIndexUpdateServiceImpl.class) {
            return commandThreadRegistry.containsKey(commandIdentifier) && ((CommandCoordinator)commandThreadRegistry.get(commandIdentifier).get(0)).isRunning();
            
        }
    }
    
    @Override
    public void rebuildIndex() throws ServiceException {
        scheduleCommand(FullReindexCommand.DEFAULT_INSTANCE);
    }
    
    @Override
    public void updateIndex(List<SolrInputDocument> documents) {
        updateIndex(documents, null);
    }

    @Override
    public void updateIndex(List<SolrInputDocument> documents, List<String> deleteQueries) {
        IncrementalUpdateCommand cmd = new IncrementalUpdateCommand(documents, deleteQueries);
        scheduleCommand(cmd);
    }
    
    @Override
    public SolrInputDocument buildDocument(Indexable indexable) {
        return commandHandler.buildDocument(indexable);
    }
    
    @Override
    public SolrInputDocument buildDocument(Indexable indexable, List<IndexField> fields, List<Locale> locales) {
        return commandHandler.buildDocument(indexable, fields, locales);
    }
    
    /**
     * Amount of time in millis that the queue will be polled before returning an item or null.  Default is 1 minute (60000 ms).  If you override this method, it must return 
     * a positive long value, preferrably greater than 60000 to reduce the polling cycles.
     * 
     * @return
     */
    protected long getQueuePollTime() {
        return 60000L;
    }
    
    /**
     * Amount of time that will be waited, assuming the queue is full, for space to become available in the queue.  Default is 1 second (1000 ms).
     * 
     * @return
     */
    protected long getQueueOfferTime() {
        return 1000L;
    }
}
