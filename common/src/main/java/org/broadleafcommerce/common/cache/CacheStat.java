/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.common.cache;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import org.broadleafcommerce.common.time.SystemTime;

/**
 * @author Jeff Fischer
 */
public class CacheStat {

    protected AtomicLong requestCount = new AtomicLong(0L);
    protected AtomicLong cacheHitCount = new AtomicLong(0L);
    protected Long lastLogTime = SystemTime.asMillis(true);

    public Long getCacheHitCount() {
        return cacheHitCount.longValue();
    }

    public Long getLastLogTime() {
        return lastLogTime;
    }

    public synchronized void setLastLogTime(Long lastLogTime) {
        this.lastLogTime = lastLogTime;
    }

    public Long getRequestCount() {
        return requestCount.longValue();
    }

    public void incrementRequest() {
        requestCount.incrementAndGet();
    }

    public void incrementHit() {
        cacheHitCount.incrementAndGet();
    }

    public BigDecimal getHitRate() {
        if (getRequestCount() == 0) {
            return new BigDecimal(-1);
        }
        BigDecimal percentage = new BigDecimal(getCacheHitCount()).divide(new BigDecimal(getRequestCount
                ()), 2, BigDecimal.ROUND_HALF_UP);
        percentage = percentage.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        return percentage;
    }
}
