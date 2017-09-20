package org.broadleafcommerce.core.search.index;

import org.broadleafcommerce.common.exception.ServiceException;

/**
 * Interface to define components that can handle entries from a Queue associated with an Index process.  Typically 
 * implementations of this will take an entry (which could be a single item or a list), converts it to indexable items, 
 * and then writes them to the index.
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public interface QueueEntryProcessor<T> {
    
    public void process(String processId, T entry) throws ServiceException;

}
