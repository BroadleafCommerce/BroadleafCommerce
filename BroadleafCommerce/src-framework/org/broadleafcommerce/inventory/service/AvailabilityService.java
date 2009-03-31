package org.broadleafcommerce.inventory.service;

import java.util.List;

import org.broadleafcommerce.inventory.domain.SkuAvailability;

public interface AvailabilityService {

    public List<SkuAvailability> checkSKUAvailability(List<Long> skuIds, boolean realTime);

    public List<SkuAvailability> checkSKUAvailabilityForLocation(List<Long> skuIds, long locationId, boolean realTime);
}
