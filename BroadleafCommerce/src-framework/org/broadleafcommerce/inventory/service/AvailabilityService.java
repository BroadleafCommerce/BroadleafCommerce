package org.broadleafcommerce.inventory.service;

import java.util.List;

import org.broadleafcommerce.inventory.domain.SkuAvailability;

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


}
