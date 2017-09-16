package org.broadleafcommerce.core.search.index;

/**
 * Simple interface to provide the ability to consume an item, especially from a Queue.
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public interface QueueConsumer<T> {

    public T consume();
    
}
