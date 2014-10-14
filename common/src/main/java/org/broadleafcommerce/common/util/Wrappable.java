package org.broadleafcommerce.common.util;

/**
 * Utility interface for items that wrap an internal, delegate item
 *
 * @author Jeff Fischer
 */
public interface Wrappable {

    /**
     * Can this wrapped item be unwrapped as the indicated type?
     *
     * @param unwrapType The type to check.
     * @return True/false.
     */
    public boolean isUnwrappableAs(Class unwrapType);

    /**
     * Get the wrapped delegate item
     *
     * @param unwrapType The java type as which to unwrap this instance.
     * @return The unwrapped reference
     */
    public <T> T unwrap(Class<T> unwrapType);

}
