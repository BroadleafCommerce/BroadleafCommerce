/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.pricing.service;

import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.pricing.dao.ShippingRateDao;
import org.broadleafcommerce.core.pricing.domain.ShippingRate;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.BandedFulfillmentPricingProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import javax.annotation.Resource;

/**
 * @deprecated Superceded in functionality by {@link BandedPriceFulfillmentOption} and {@link BandedFulfillmentPricingProvider}
 * @see {@link FulfillmentOption}, {@link FulfillmentPricingService}
 */
@Service("blShippingRateService")
@Deprecated
public class ShippingRateServiceImpl implements ShippingRateService {
    
    @Resource(name="blShippingRatesDao")
    protected ShippingRateDao shippingRateDao;

    @Override
    public ShippingRate readShippingRateByFeeTypesUnityQty(String feeType, String feeSubType, BigDecimal unitQuantity) {
        return shippingRateDao.readShippingRateByFeeTypesUnityQty(feeType, feeSubType, unitQuantity);
    }

    @Override
    public ShippingRate readShippingRateById(Long id) {
        return shippingRateDao.readShippingRateById(id);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public ShippingRate save(ShippingRate shippingRate) {
        return shippingRateDao.save(shippingRate);
    }

}
