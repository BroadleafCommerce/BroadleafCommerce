package org.broadleafcommerce.core.util.queue;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

public interface DistributedBlockingQueue<E extends Serializable> extends BlockingQueue<E> {

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
