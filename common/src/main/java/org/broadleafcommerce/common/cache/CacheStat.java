/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
