package org.broadleafcommerce.inventory.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.inventory.dao.AvailabilityDao;
import org.broadleafcommerce.inventory.domain.SkuAvailability;
import org.springframework.stereotype.Service;

@Service("availabilityService")
public class AvailabilityServiceImpl implements AvailabilityService {

    @Resource
    private AvailabilityDao availabilityDao;

	@Override
	public List<SkuAvailability> checkSKUAvailability(List<Long> skuIds, boolean realTime) {
		return availabilityDao.readSKUAvailability(skuIds, false);
	}

	@Override
	public List<SkuAvailability> checkSKUAvailabilityForLocation(List<Long> skuIds,
			long locationId, boolean realTime) {
		return availabilityDao.readSKUAvailabilityForLocation(skuIds, locationId, realTime);
	}


}
