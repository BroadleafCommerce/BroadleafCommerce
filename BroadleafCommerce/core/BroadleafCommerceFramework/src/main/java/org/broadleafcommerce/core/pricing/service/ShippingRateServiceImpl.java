package org.broadleafcommerce.core.pricing.service;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.broadleafcommerce.core.pricing.dao.ShippingRateDao;
import org.broadleafcommerce.core.pricing.domain.ShippingRate;
import org.springframework.stereotype.Service;

@Service("blShippingRateService")
public class ShippingRateServiceImpl implements ShippingRateService {
	
	@Resource(name="blShippingRatesDao")
	protected ShippingRateDao shippingRateDao;

	public ShippingRate readShippingRateByFeeTypesUnityQty(String feeType, String feeSubType, BigDecimal unitQuantity) {
		return shippingRateDao.readShippingRateByFeeTypesUnityQty(feeType, feeSubType, unitQuantity);
	}

	public ShippingRate readShippingRateById(Long id) {
		return shippingRateDao.readShippingRateById(id);
	}

	public ShippingRate save(ShippingRate shippingRate) {
		return shippingRateDao.save(shippingRate);
	}

}
