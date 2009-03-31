package org.broadleafcommerce.inventory.test.dataprovider;

import java.util.Date;

import org.broadleafcommerce.inventory.domain.SkuAvailability;
import org.broadleafcommerce.inventory.domain.SkuAvailabilityImpl;
import org.broadleafcommerce.inventory.service.AvailabilityStatusEnum;
import org.testng.annotations.DataProvider;

public class SkuAvailabilityDataProvider {

    @DataProvider(name = "setupSkuAvailability")
    public static Object[][] createSkuAvailabilityRecords() {
    	Object[][] paramArray = new Object[10][1];
    	Date AVAILABLE_TODAY = new Date();
    	Date AVAILABLE_NULL = null;
    	Long LOCATION_NULL = null;
    	Long LOCATION_ONE = 1L;
    	Long SKU_ID_1 = 1L;
    	Long SKU_ID_2 = 2L;
    	Long SKU_ID_3 = 3L;
    	Long SKU_ID_4 = 4L;
    	Long SKU_ID_5 = 5L;

    	long recordCount = 0;

   		paramArray[0][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_1, LOCATION_NULL, AVAILABLE_TODAY, AvailabilityStatusEnum.AVAILABLE, null);
   		paramArray[1][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_1, LOCATION_ONE, AVAILABLE_TODAY, AvailabilityStatusEnum.AVAILABLE, null);
   		paramArray[2][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_2, LOCATION_NULL, AVAILABLE_TODAY, null, new Long(5));
   		paramArray[3][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_2, LOCATION_ONE, AVAILABLE_TODAY, null, new Long(5));
   		paramArray[4][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_3, LOCATION_NULL, AVAILABLE_TODAY, null, new Long(0));
   		paramArray[5][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_3, LOCATION_ONE, AVAILABLE_TODAY, null, new Long(0));
   		paramArray[6][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_4, LOCATION_NULL, AVAILABLE_NULL, AvailabilityStatusEnum.BACKORDERED, new Long(0));
   		paramArray[7][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_4, LOCATION_ONE, AVAILABLE_NULL, AvailabilityStatusEnum.BACKORDERED, new Long(0));
   		paramArray[8][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_5, LOCATION_NULL, AVAILABLE_NULL, null, new Long(5));
   		paramArray[9][0] = createSkuForSkuIdAndLocation(recordCount++, SKU_ID_5, LOCATION_ONE, AVAILABLE_NULL, null, new Long(5));

        return paramArray;
    }

    public static SkuAvailability createSkuForSkuIdAndLocation(Long id, Long skuId, Long locationId, Date availabilityDate, AvailabilityStatusEnum availStatus, Long qoh) {
        SkuAvailability skuAvailability = new SkuAvailabilityImpl();
        skuAvailability.setId(null);
        skuAvailability.setSkuId(skuId);
        skuAvailability.setLocationId(locationId);
        skuAvailability.setAvailabilityDate(availabilityDate);
        skuAvailability.setAvailabilityStatus(availStatus);
        skuAvailability.setQuantityOnHand(qoh);
        return skuAvailability;
    }
}
