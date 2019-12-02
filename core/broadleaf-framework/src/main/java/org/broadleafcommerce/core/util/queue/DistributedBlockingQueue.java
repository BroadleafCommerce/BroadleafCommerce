package org.broadleafcommerce.core.util.queue;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/**
 * Interface that defines a distributed {@link BlockingQueue}, which means that this queue can be created, distributed, and operated on by multiple 
 * @author Kelly Tisdell
 *
 * @param <E>
 */
public interface DistributedBlockingQueue<E extends Serializable> extends BlockingQueue<E> {

    /**
     * {@link RuntimeException} indicating that there was an error operating on the queue, or changing queue state.
     * 
     * @author Kelly Tisdell
     *
     */
    public class DistributedQueueException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public DistributedQueueException() {
            super();
        }

        public DistributedQueueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        public DistributedQueueException(String message, Throwable cause) {
            super(message, cause);
        }

        public DistributedQueueException(String message) {
            super(message);
        }

        public DistributedQueueException(Throwable cause) {
            super(cause);
        }
        
        
    }
}
