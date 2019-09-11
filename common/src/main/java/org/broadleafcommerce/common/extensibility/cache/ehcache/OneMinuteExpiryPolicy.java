package org.broadleafcommerce.common.extensibility.cache.ehcache;

import org.broadleafcommerce.common.extensibility.cache.TimedValueHolder;

/**
 * Convenience class providing a 1 minute expiry policy, along with the ability to override it on a per entry basis when 
 * using a {@link TimedValueHolder} as the cached value.
 * 
 * @author Kelly Tisdell
 *
 */
public final class OneMinuteExpiryPolicy extends DefaultExpiryPolicy {

    public OneMinuteExpiryPolicy() {
        super(60);
    }
}
