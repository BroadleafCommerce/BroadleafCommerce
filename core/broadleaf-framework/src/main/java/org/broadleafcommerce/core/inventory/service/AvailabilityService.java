/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.inventory.service;

import org.broadleafcommerce.core.inventory.domain.SkuAvailability;

import java.util.List;

/**
 * 
 * @deprecated This is no longer required and is instead implemented as a third-party inventory module
 *
 */
@Deprecated
public interface AvailabilityService {

    /**
     * Returns the availability status for this passed in skuId.   Implementations may choose
     * to cache the status based upon the passed in realTime indicator.
     *
     * @param skuId
     * @param realTime
     * @return String indicating the availabilityStatus (statuses are implementation specific)
     */
    public SkuAvailability lookupSKUAvailability(Long skuId, boolean realTime);

    /**
     * Returns the availability status for a specific skuId and location.   Implementations may choose
     * to cache the status based upon the passed in realTime indicator.
     *
     * @param skuId
     * @param locationId
     * @param realTime
     * @return String indicating the availabilityStatus (statuses are implementation specific)
     */
    public SkuAvailability lookupSKUAvailabilityForLocation(Long skuId, Long locationId, boolean realTime);

    /**
     * Returns the availability status for this passed in skuId.   Implementations may choose
     * to cache the status based upon the passed in realTime indicator.
     *
     * @param skuId
     * @param realTime
     * @return String indicating the availabilityStatus (statuses are implementation specific)
     */
    public List<SkuAvailability> lookupSKUAvailability(List<Long> skuIds, boolean realTime);

    /**
     * Returns the availability status for a specific skuId and location.   Implementations may choose
     * to cache the status based upon the passed in realTime indicator.
     *
     * @param skuId
     * @param locationId
     * @param realTime
     * @return String indicating the availabilityStatus (statuses are implementation specific)
     */
    public List<SkuAvailability> lookupSKUAvailabilityForLocation(List<Long> skuIds, Long locationId, boolean realTime);

    public void save(SkuAvailability skuAvailability);
    
}
