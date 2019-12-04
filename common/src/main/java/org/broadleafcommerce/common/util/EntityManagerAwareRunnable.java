package org.broadleafcommerce.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	public EntityManagerAwareRunnable() {
	    this(null);
	}
	
	/**
	 * If a {@link Semaphore} is provided (not null), a single permit will be released prior to the completion of the run method.
	 * 
	 * @param sem
	 */
	public EntityManagerAwareRunnable(Semaphore sem) {
	    this.semaphore = sem;
	}
	
	@Override
	public final void run() {
	    try {
    		EntityManagerFactory emf = getEntityManagerFactory();
    		boolean participate = false;
    		
    		try {
    		    if (TransactionSynchronizationManager.hasResource(emf)) {
    	            // Do not modify the EntityManager. Just set the participate flag.
    	            participate = true;
    	        } else {
    	            EntityManager em = emf.createEntityManager();
    	            EntityManagerHolder emHolder = new EntityManagerHolder(em);
    	            TransactionSynchronizationManager.bindResource(emf, emHolder);
    	        }
    		} catch (Exception e) {
    		    registerError(e);
    			LOG.error("Error occured opening an EntityManager in a background thread.", e);
    			return;
    		}
    		
    		try {
    		    executeInternal();
    		} catch (Exception e) {
    		    registerError(e);
    			LOG.error("An error occured executing in an EntityManager Aware background thread.", e);
    		} finally {
    		    
    		    if (!participate) {
        			EntityManagerHolder emHolder = (EntityManagerHolder)
        					TransactionSynchronizationManager.unbindResource(emf);
        			EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    		    }
    		}
	    } finally {
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
	
	protected void registerError(Exception e) {
	    //Default implementation does nothing.
	}
}
