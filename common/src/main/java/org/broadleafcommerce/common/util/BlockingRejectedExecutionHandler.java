package org.broadleafcommerce.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This is a component to optionally configure a ThreadPoolTaskExecutor.  The purpose of this is that, when the task queue 
 * full, rather than rejecting another task, it delegates to the calling thread to execute.
 * 
 * Care should be taken as this can cause thread starvation, but the tradeoff is that when you have a task executor with a 
 * max queue size, it allows you to throttle so that you don't either run out of memory filling an unbounded queue, 
 * or get an exception because the queue was full.
 * 
 * @author Kelly Tisdell
 *
 */
public class BlockingRejectedExecutionHandler implements RejectedExecutionHandler {

    private static final Log LOG = LogFactory.getLog(BlockingRejectedExecutionHandler.class);
    
    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(runnable);
        } catch (InterruptedException exception) {
            LOG.error(exception);
            return;
        }
    }

}
