package org.broadleafcommerce.inventory.dao;

import java.util.List;

import org.broadleafcommerce.inventory.domain.SkuAvailability;

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