/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.core.inventory.service;

import org.broadleafcommerce.core.inventory.domain.SkuAvailability;
import org.broadleafcommerce.core.inventory.service.dataprovider.SkuAvailabilityDataProvider;
import org.broadleafcommerce.core.inventory.service.type.AvailabilityStatusType;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

public class SkuAvailabilityTest extends BaseTest {

    protected final Long[] skuIDs = {1L, 2L, 3L, 4L, 5L};
    protected final List<Long> skuIdList = Arrays.asList(skuIDs);

    @Resource
    private AvailabilityService availabilityService;

    @Test(groups = { "createSkuAvailability" }, dataProvider = "setupSkuAvailability", dataProviderClass = SkuAvailabilityDataProvider.class)
    @Rollback(false)
    public void createSkuAvailability(SkuAvailability skuAvailability) {
        availabilityService.save(skuAvailability);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void readSKUAvailabilityEntries() {
        List<SkuAvailability> skuAvailabilityList = availabilityService.lookupSKUAvailability(skuIdList, false);
        assert(skuAvailabilityList.size() == 5);

        int backorderCount=0;
        int availableCount=0;
        for (SkuAvailability skuAvailability : skuAvailabilityList) {
            if (skuAvailability.getAvailabilityStatus() != null && skuAvailability.getAvailabilityStatus().equals(AvailabilityStatusType.BACKORDERED)) {
                backorderCount++;
            }
            if (skuAvailability.getAvailabilityStatus() != null && skuAvailability.getAvailabilityStatus().equals(AvailabilityStatusType.AVAILABLE)) {
                availableCount++;
            }
        }
        assert(backorderCount == 1);
        assert(availableCount == 1);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void readAvailableSkusForUnknownLocation() {
        List<SkuAvailability> skuAvailabilityList = availabilityService.lookupSKUAvailabilityForLocation(skuIdList, 100L, false);
        assert(skuAvailabilityList.size() == 0);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void readAvailableSkusForLocation() {
        List<SkuAvailability> skuAvailabilityList = availabilityService.lookupSKUAvailabilityForLocation(skuIdList, 1L, false);
        assert(skuAvailabilityList.size() == 5);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void checkAvailableQuantityWithReserveAndQOH() {
        SkuAvailability skuAvailability = availabilityService.lookupSKUAvailabilityForLocation(2L, 1L, false);
        assert(skuAvailability.getReserveQuantity() == 1 && skuAvailability.getQuantityOnHand() == 5);
        assert(skuAvailability.getAvailableQuantity() == 4);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void checkAvailableQuantityWithNullReserveQty() {
        SkuAvailability skuAvailability = availabilityService.lookupSKUAvailabilityForLocation(5L, 1L, false);
        assert(skuAvailability.getReserveQuantity() == null && skuAvailability.getQuantityOnHand() == 5);
        assert(skuAvailability.getAvailableQuantity() == 5);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void checkAvailableQuantityWithNullQuantityOnHand() {
        SkuAvailability skuAvailability = availabilityService.lookupSKUAvailabilityForLocation(1L, 1L, false);
        assert(skuAvailability.getReserveQuantity() == 1 && skuAvailability.getQuantityOnHand() == null);
        assert(skuAvailability.getAvailableQuantity() == null);
    }
}
