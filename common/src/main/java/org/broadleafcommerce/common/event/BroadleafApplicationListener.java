package org.broadleafcommerce.common.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Works in conjection with <code>org.broadleafcommerce.common.event.BroadleafApplicationEventMulticaster</code> except 
 * the event listener can indicate if the event should be run in a background thread.  If no TaskExecutor is 
 * configured on the BroadleafApplicationEventMulticaster then it will be executed synchronously, regardless of 
 * of whether an event listener is configured.
 * 
 * @author Kelly Tisdell
 *
 * @param <E>
 */
public interface BroadleafApplicationListener<E extends ApplicationEvent> extends ApplicationListener<E> {

    /**
     * Indicates if this application listener should be run in a background thread if a TasExecutor is configured.
     * @return
     */
    public boolean isAsynchronous();

}
