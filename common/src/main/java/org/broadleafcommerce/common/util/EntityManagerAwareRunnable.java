/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.hibernate.LazyInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Semaphore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Provides a {@link Runnable} implementation that allows the execution of the run() method inside the context of a thread-bound {@link EntityManager}.
 * 
 * This MUST be run in a background thread (typically a {@link TaskExecutor}; 
 * not a HTTP request thread, a {@link EntityManagerFactoryAwareSystemEventConsumer}, 
 * or a thread to which an EntityManager has already been bound).
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class EntityManagerAwareRunnable implements Runnable {
	
	public static final String DEFAULT_ENTITY_MANAGER_NAME = "blPU";
	private static final Log LOG = LogFactory.getLog(EntityManagerAwareRunnable.class);
	
	private final Semaphore semaphore;
	private EntityManager em;
	
	/**
	 * Constructs an abstract {@link Runnable} implementation that can be run in another thread.  Guarantees that an {@link EntityManager} is bound 
     * to the current thread.
	 */
	public EntityManagerAwareRunnable() {
	    this.semaphore = null;
	}
	
	/**
	 * Constructs an abstract {@link Runnable} implementation that can be run in another thread.  Guarantees that an {@link EntityManager} is bound 
	 * to the current thread.
	 * 
	 * If a {@link Semaphore} is provided (not null), a single permit will be released prior to the completion of the run method.
	 * 
	 * @param sem
	 */
	public EntityManagerAwareRunnable(Semaphore sem) {
	    this.semaphore = sem;
	}
	
	/**
	 * This method attempts to create / bind an {@link EntityManager} prior to execution.  This will also create and bind a {@link BroadleafRequestContext}, which should 
	 * not already be bound to the background thread.  If the run method is executed in a foreground thread, any existing {@link BroadleafRequestContext} bound to the thread 
	 * will be returned to its previous state.
	 */
	@Override
	public final void run() {
	    //This will typically be null, especially if executing in a background thread.
	    final BroadleafRequestContext originalCtx = BroadleafRequestContext.getBroadleafRequestContext(false);
	    try {
	        BroadleafRequestContext.setBroadleafRequestContext(new BroadleafRequestContext());
    		final EntityManagerFactory emf = getEntityManagerFactory();
    		boolean participate = false;
    		
    		try {
    		    if (TransactionSynchronizationManager.hasResource(emf)) {
    	            // Do not modify the EntityManager. Just set the participate flag.
    	            participate = true;
    	            em = ((EntityManagerHolder)TransactionSynchronizationManager.getResource(emf)).getEntityManager();
    	        } else {
    	            em = emf.createEntityManager();
    	            EntityManagerHolder emHolder = new EntityManagerHolder(em);
    	            TransactionSynchronizationManager.bindResource(emf, emHolder);
    	        }
    		} catch (Exception e) {
    		    registerError(e);
    			LOG.error("Error occured opening an EntityManager in a EntityManagerAwareRunnable.", e);
    			return;
    		}
    		
    		try {
    		    executeInternal();
    		} catch (Exception e) {
    		    registerError(e);
    			LOG.error("An error occured executing in an EntityManagerAwareRunnable background thread.", e);
    		} finally {
    		    if (!participate) {
        			EntityManagerHolder emHolder = (EntityManagerHolder)
        					TransactionSynchronizationManager.unbindResource(emf);
        			EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    		    }
    		}
	    } finally {
	        em = null;
	        BroadleafRequestContext.setBroadleafRequestContext(originalCtx);
	        if (semaphore != null) {
                semaphore.release();
            }
	    }
	}
	
	/**
	 * Executes within the context of a thread-bound {@link EntityManager}.  
	 * Helps avoid {@link LazyInitializationException}s and exceptions indicating that the Hibernate Session is closed.
	 * 
	 * 
	 */
	protected abstract void executeInternal() throws Exception;
	
	/**
	 * The name of the persistence unit.
	 * 
	 * Defaults to "blPU", the main persistence unit in Broadleaf.
	 * 
	 * @return
	 */
	public String getEntityManagerName() {
		return DEFAULT_ENTITY_MANAGER_NAME;
	}
	
	protected EntityManagerFactory getEntityManagerFactory() {
		return EntityManagerFactoryUtils.findEntityManagerFactory(getApplicationContext(), getEntityManagerName());
	}
	
	protected ApplicationContext getApplicationContext() {
		return ApplicationContextHolder.getApplicationContext();
	}
	
	protected final EntityManager getEntityManager() {
	    return em;
	}
	
	protected void registerError(Exception e) {
	    //Default implementation does nothing.
	}
}
