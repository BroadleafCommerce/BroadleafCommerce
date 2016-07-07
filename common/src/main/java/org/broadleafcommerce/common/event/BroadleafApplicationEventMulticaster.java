/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;

import java.util.concurrent.Executor;

/**
 * This class is a simple extension to Spring's SimpleApplicationEventMulticaster.  The difference is 
 * that this allows the EventListener to indicate whether it should be asynchronous or not, assuming that a 
 * TaskExecutor has been configured.
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
	
    @Autowired(required = false)
    @Qualifier("blApplicationEventMulticastTaskExecutor")
    private Executor taskExecutor;

	protected ApplicationContext ctx;

    /**
     * Take care when specifying that event or application listener should be executed asynchronously.  
     * If there is no TaskExecutor configured, this 
     * will execute synchronously, regardless.  If there is a TaskExecutor configured, then if the 
     * listener is a BroadleafApplicationListener and its 
     * <code>isAsynchronous()</code> method returns true then the event will fire asynchronously. 
     * Be aware that the events are not durable in this case.  Events that are executed asynchronously  
     * should be used with caution, where a loss of event due to error or shutdown of the VM is not a major 
     * concern.
     */
	@Override
	public void multicastEvent(final ApplicationEvent event) {
        Executor executor = getTaskExecutor();
        for (final ApplicationListener<?> listener : getApplicationListeners(event, ResolvableType.forInstance(event))) {
			boolean isAsynchronous = false;
			if (executor != null) {
                if ((BroadleafApplicationListener.class.isAssignableFrom(listener.getClass())
                            && ((BroadleafApplicationListener<? extends ApplicationEvent>)listener).isAsynchronous())) {
                    isAsynchronous = true;
			    }
			}
			
            if (isAsynchronous) {
				executor.execute(new Runnable() {
					public void run() {
                        invokeListener(listener, event);
					}
				});
			} else {
				invokeListener(listener, event);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
	}
	
    public Executor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

}
