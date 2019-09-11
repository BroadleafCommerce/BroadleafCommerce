package org.broadleafcommerce.common.extensibility.cache.ehcache;

import org.broadleafcommerce.common.extensibility.cache.TimedValueHolder;
import org.ehcache.expiry.ExpiryPolicy;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Defines a default ExpiryPolicy for EhCache.  In particular, this takes a TTL in seconds and 
 * converts it to a Cache duration or TTL.  This provides both a default TTL for caches created with it programmatically, 
 * as well as a way of mapping per-entry TTL values in EhCache when the cached value is an instance of {@link TimedValueHolder}.
 * 
 * @author Kelly Tisdell
 *
 */
public class DefaultExpiryPolicy implements ExpiryPolicy<Object, Object> {
    
    private final Duration defaultDuration;
    
    /**
     * Creates a new {@link ExpiryPolicy} with an infinite default duration.
     */
    public DefaultExpiryPolicy() {
        defaultDuration = ExpiryPolicy.INFINITE;
    }
    
    /**
     * creates a new {@link ExpiryPolicy} with a duration equal to the number of seconds passed in as a argument.
     * @param defaultTTLSeconds
     */
    public DefaultExpiryPolicy(int defaultTTLSeconds) {
        if (defaultTTLSeconds < 0) {
            defaultDuration = ExpiryPolicy.INFINITE;
        } else {
            defaultDuration = Duration.ofSeconds((long)defaultTTLSeconds);
        }
    }

    /*
     * If the value is an instance of {@link TimedValueHolder} and if the {@link TimedValueHolder} contains a non-null 
     * time to live, then this will attempt to override the default cache time to live on a per-entry basis.
     * 
     * (non-Javadoc)
     * @see org.ehcache.expiry.ExpiryPolicy#getExpiryForCreation(java.lang.Object, java.lang.Object)
     */
    @Override
    public Duration getExpiryForCreation(Object key, Object value) {
        if (value instanceof TimedValueHolder) {
            TimedValueHolder holder = (TimedValueHolder) value;
            if (holder.getTimeToLiveSeconds() != null) {
                if (holder.getTimeToLiveSeconds() < 0) {
                    return ExpiryPolicy.INFINITE;
                } else {
                    return Duration.ofSeconds((long)holder.getTimeToLiveSeconds());
                }
            }
        }
        return getDefaultDuration();
    }

    @Override
    public Duration getExpiryForAccess(Object key, Supplier<?> value) {
        // Keeping the existing expiry
        return null;
    }

    @Override
    public Duration getExpiryForUpdate(Object key, Supplier<?> oldValue, Object newValue) {
        // Keeping the existing expiry
        return null;
    }

    protected Duration getDefaultDuration() {
        return defaultDuration;
    }
}
