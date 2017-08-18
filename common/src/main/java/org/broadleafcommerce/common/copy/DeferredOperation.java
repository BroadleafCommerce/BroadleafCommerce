package org.broadleafcommerce.common.copy;

/**
 * Encapsulates a block of code that should be run at a later time, usually in order to guarantee the proper state
 * or conditions for its execution.
 *
 * @author Jeff Fischer
 */
public interface DeferredOperation {

    void run();

}
