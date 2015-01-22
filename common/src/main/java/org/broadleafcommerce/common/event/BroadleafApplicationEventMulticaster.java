/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.Executor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * This class is a simple extension to Spring's SimpleApplicationEventMulticaster.  The difference is 
 * that this allows the event to indicate whether it should be asynchronous or not, assuming that a 
 * TaskExecutor has been configured.  This can optionally indicate whether the background threads 
 * should open an EntityManager and bind it to the execution of the thread.
 * 
 * Asynchronous execution should be used with care.  Events are not durable with this implementation. 
 * In addition, this implementation does not broadcast or multicast events to systems outside of the 
 * running JVM, although an event listener could be configured to do just that.
 * 
 * @author Kelly Tisdell
 *
 */
public class BroadleafApplicationEventMulticaster extends
		SimpleApplicationEventMulticaster implements ApplicationContextAware {
	
	private static Log LOG = LogFactory.getLog(BroadleafApplicationEventMulticaster.class);
	
	protected ApplicationContext ctx;

    /**
     * Take care when specifying that event should be asynchronous.  If there is no TaskExecutor configured, this 
     * will execute synchronously.  If there is a TaskExecutor configured, then if the event is a BroadleafApplicationEvent 
     * and is set to execute asynchronously, then be aware that the events are not durable.  These kinds of event 
     * situations should be used with caution, where a loss of event due to error or shutdown of the VM is not a major 
     * concern.
     */
	@Override
	public void multicastEvent(final ApplicationEvent event) {
		for (final ApplicationListener<?> listener : getApplicationListeners(event)) {
			Executor executor = getTaskExecutor();
			if (executor != null 
					&& (BroadleafApplicationEvent.class.isAssignableFrom(event.getClass())) 
					&& ((BroadleafApplicationEvent)event).isAsynchronous()) {
				
				final boolean openEm = isOpenEntityManagerForExecutor((BroadleafApplicationEvent)event);
				executor.execute(new Runnable() {
					public void run() {
						EntityManagerHolder emHolder = null;
						EntityManagerFactory emf = null;
						if (openEm) {
							//Since we're running in a background thread, we will open the entity manager and bind it to the thread
							emf = EntityManagerFactoryUtils.findEntityManagerFactory(ctx, "entityManagerFactory");
							if (!TransactionSynchronizationManager.hasResource(emf)) {
								EntityManager em = emf.createEntityManager();
								emHolder = new EntityManagerHolder(em);
								TransactionSynchronizationManager.bindResource(emf, emHolder);
							}
						}
						try {
							invokeListener(listener, event);
						} finally {
							if (openEm) {
								try {
									emHolder = (EntityManagerHolder)
											TransactionSynchronizationManager.unbindResource(emf);
									EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
								} catch (Exception e) {
									LOG.error("An error occured trying to unbind the EntityManager for an Executor", e);
								}
							}
						}
					}
				});
			} else {
				invokeListener(listener, event);
			}
		}
	}
	
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void invokeListener(ApplicationListener listener, ApplicationEvent event) {
		ErrorHandler errorHandler = null;
		if (BroadleafApplicationEvent.class.isAssignableFrom(event.getClass())) {
			errorHandler = ((BroadleafApplicationEvent)event).getErrorHandler();
		}
		
		if (errorHandler == null) {
			errorHandler = getErrorHandler();
		}
		
		if (errorHandler != null) {
			try {
				listener.onApplicationEvent(event);
			} catch (Throwable err) {
				errorHandler.handleError(err);
			}
		} else {
			listener.onApplicationEvent(event);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
	}
	
	/**
	 * Subclasses can override this method to determine, based on the event itself, whether a PersistenceManager 
	 * should be opened and bound to the background thread for processing. This can help reduce or eliminate 
	 * the risk of LazyInitializationExceptions when accessing lazy loaded entities in background threads.
	 * 
	 * Default implementation returns true
	 * 
	 * @param event
	 * @return
	 */
	protected boolean isOpenEntityManagerForExecutor(BroadleafApplicationEvent event) {
		return true;
	}
}
