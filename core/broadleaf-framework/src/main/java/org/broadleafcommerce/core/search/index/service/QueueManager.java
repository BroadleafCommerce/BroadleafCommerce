package org.broadleafcommerce.core.search.index.service;

/**
 * Marker interface combining QueueProducer and QueueConsumer.
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public interface QueueManager<T> extends QueueProducer<T>, QueueConsumer<T> {

}
