package org.broadleafcommerce.common.util;

/**
 * Allows for a generic operation that can be executed in a particular context.
 * 
 * @author Kelly Tisdell
 *
 */
public interface GenericOperation<R, T extends Throwable> {

    /**
     * Returns R, the return value and throws T, the Throwable.  Use {@link Void} as the return type 
     * and return null if void is the expected return type.
     * @return
     * @throws T
     */
    public R execute() throws T;
    
}
