/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.pricing.service;

import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.pricing.dao.ShippingRateDao;
import org.broadleafcommerce.core.pricing.domain.ShippingRate;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.BandedFulfillmentPricingProvider;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

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
    public ShippingRate save(ShippingRate shippingRate) {
        return shippingRateDao.save(shippingRate);
    }

}
