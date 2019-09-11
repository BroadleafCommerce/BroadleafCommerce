package org.broadleafcommerce.common.extensibility.cache.ehcache;

import org.broadleafcommerce.common.extensibility.cache.TimedValueHolder;

/**
 * Convenience class providing a 1 minute expiry policy, along with the ability to override it on a per entry basis when 
 * using a {@link TimedValueHolder} as the cached value. This can also be used in the EhCache XML to specify an expiry policy:
 * 
 * <pre>
 * {@code
 * <cache alias="myCache">
 *        <expiry>
 *            <class>org.broadleafcommerce.common.extensibility.cache.ehcache.OneMinuteExpiryPolicy</class>
 *        </expiry>
 *        <heap>5000</heap>
 * </cache>
 * }
 * </pre>
 * 
 * @author Kelly Tisdell
 *
 */
public final class OneMinuteExpiryPolicy extends DefaultExpiryPolicy {

    public OneMinuteExpiryPolicy() {
        super(60);
    }
}
