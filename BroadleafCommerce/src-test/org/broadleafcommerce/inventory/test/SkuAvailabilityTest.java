package org.broadleafcommerce.inventory.test;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.inventory.dao.AvailabilityDao;
import org.broadleafcommerce.inventory.domain.SkuAvailability;
import org.broadleafcommerce.inventory.service.AvailabilityService;
import org.broadleafcommerce.inventory.test.dataprovider.SkuAvailabilityDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class SkuAvailabilityTest extends BaseTest {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
	protected final Long[] skuIDs = {1L, 2L, 3L, 4L, 5L};
	protected final List<Long> skuIdList = Arrays.asList(skuIDs);

    @Resource
    private AvailabilityService availabilityService;

    @Resource
    private AvailabilityDao availabilityDao;

    @Test(groups = { "createSkuAvailability" }, dataProvider = "setupSkuAvailability", dataProviderClass = SkuAvailabilityDataProvider.class)
    @Rollback(false)
    public void createSkuAvailability(SkuAvailability skuAvailability) {
    	availabilityDao.saveSKUAvailability(skuAvailability);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void readSKUAvailabilityEntries() {
        List<SkuAvailability> skuAvailabilityList = availabilityService.checkSKUAvailability(skuIdList, false);
        assert(skuAvailabilityList.size() == 5);

        int skuCount=0;
        for (SkuAvailability skuAvailability : skuAvailabilityList) {
			if (skuAvailability.getAvailabilityStatus() != null && skuAvailability.getAvailabilityStatus().isAvailable()) {
				skuCount++;
			}
		}
        assert(skuCount == 1);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void readAvailableSkusForUnknownLocation() {
        List<SkuAvailability> skuAvailabilityList = availabilityService.checkSKUAvailabilityForLocation(skuIdList, 100L, false);
        assert(skuAvailabilityList.size() == 0);
    }

    @Test(dependsOnGroups = { "createSkuAvailability" })
    public void readAvailableSkusForLocation() {
        List<SkuAvailability> skuAvailabilityList = availabilityService.checkSKUAvailabilityForLocation(skuIdList, 1L, false);
        assert(skuAvailabilityList.size() == 5);
    }
}
