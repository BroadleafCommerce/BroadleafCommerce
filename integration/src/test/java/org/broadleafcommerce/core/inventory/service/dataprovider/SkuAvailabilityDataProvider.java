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
package org.broadleafcommerce.core.inventory.service.dataprovider;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.inventory.domain.SkuAvailability;
import org.broadleafcommerce.core.inventory.domain.SkuAvailabilityImpl;
import org.broadleafcommerce.core.inventory.service.type.AvailabilityStatusType;
import org.testng.annotations.DataProvider;

import java.util.Date;

public class SkuAvailabilityDataProvider {

    @DataProvider(name = "setupSkuAvailability")
    public static Object[][] createSkuAvailabilityRecords() {
        Object[][] paramArray = new Object[10][1];
        Date AVAILABLE_TODAY = SystemTime.asDate();
        Date AVAILABLE_NULL = null;
        Long LOCATION_NULL = null;
        Long LOCATION_ONE = 1L;
        Long SKU_ID_1 = 1L;
        Long SKU_ID_2 = 2L;
        Long SKU_ID_3 = 3L;
        Long SKU_ID_4 = 4L;
        Long SKU_ID_5 = 5L;

        long recordCount = 0;

        paramArray[0][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_1, LOCATION_NULL, AVAILABLE_TODAY, "AVAILABLE", null, new Integer(1));
        paramArray[1][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_1, LOCATION_ONE, AVAILABLE_TODAY, "AVAILABLE", null, new Integer(1));
        paramArray[2][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_2, LOCATION_NULL, AVAILABLE_TODAY, null, new Integer(5), new Integer(1));
        paramArray[3][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_2, LOCATION_ONE, AVAILABLE_TODAY, null, new Integer(5), new Integer(1));
        paramArray[4][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_3, LOCATION_NULL, AVAILABLE_TODAY, null, new Integer(0), new Integer(1));
        paramArray[5][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_3, LOCATION_ONE, AVAILABLE_TODAY, null, new Integer(0), new Integer(1));
        paramArray[6][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_4, LOCATION_NULL, AVAILABLE_NULL, "BACKORDERED", new Integer(0), new Integer(1));
        paramArray[7][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_4, LOCATION_ONE, AVAILABLE_NULL, "BACKORDERED", new Integer(0), new Integer(1));
        paramArray[8][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_5, LOCATION_NULL, AVAILABLE_NULL, null, new Integer(5), null);
        paramArray[9][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_5, LOCATION_ONE, AVAILABLE_NULL, null, new Integer(5), null);

        return paramArray;
    }

    public static SkuAvailability createSkuForSkuIdAndLocation(Long id, Long skuId, Long locationId, Date availabilityDate, String availStatus, Integer qoh, Integer reserveQuantity) {
        SkuAvailability skuAvailability = new SkuAvailabilityImpl();
        skuAvailability.setId(null);
        skuAvailability.setSkuId(skuId);
        skuAvailability.setLocationId(locationId);
        skuAvailability.setAvailabilityDate(availabilityDate);
        skuAvailability.setAvailabilityStatus(availStatus==null?null:AvailabilityStatusType.getInstance(availStatus));
        skuAvailability.setQuantityOnHand(qoh);
        skuAvailability.setReserveQuantity(reserveQuantity);
        return skuAvailability;
    }
}
