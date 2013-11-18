/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.inventory.dao;

import org.broadleafcommerce.core.inventory.domain.SkuAvailability;

import java.util.List;

/**
 * 
 * @deprecated This is no longer required and is instead implemented as a third-party inventory module
 * 
 */
@Deprecated
public interface AvailabilityDao {

    /**
     * Returns a SKU Availability record for the passed in skuId.   Uses a cacheable query
     * unless the realTime flag is set to true.
     * @param skuId
     * @param realTime
     * @return
     */
    public List<SkuAvailability> readSKUAvailability(List<Long> skuIds, boolean realTime);

    /**
     * Returns a SKU Availability record for the passed in skuId and locationId.   Uses a cacheable query
     * unless the realTime flag is set to true.
     * @param skuId
     * @param realTime
     * @return
     */
    public List<SkuAvailability> readSKUAvailabilityForLocation(List<Long> skuIds, Long locationId, boolean realTime);

    public void save(SkuAvailability skuAvailability);
}
