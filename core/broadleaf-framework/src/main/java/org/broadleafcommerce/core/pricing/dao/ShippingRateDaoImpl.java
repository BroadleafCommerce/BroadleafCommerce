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

package org.broadleafcommerce.core.pricing.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.pricing.domain.ShippingRate;
import org.broadleafcommerce.core.pricing.domain.ShippingRateImpl;
import org.broadleafcommerce.core.pricing.service.FulfillmentPricingService;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.BandedFulfillmentPricingProvider;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated Superceded in functionality by {@link BandedPriceFulfillmentOption} and {@link BandedFulfillmentPricingProvider}
 * @see {@link FulfillmentOption}, {@link FulfillmentPricingService}
 */
@Repository("blShippingRatesDao")
@Deprecated
public class ShippingRateDaoImpl implements ShippingRateDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public ShippingRate save(ShippingRate shippingRate) {
        return em.merge(shippingRate);
    }

    @Override
    public ShippingRate readShippingRateById(Long id) {
        return em.find(ShippingRateImpl.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ShippingRate readShippingRateByFeeTypesUnityQty(String feeType, String feeSubType, BigDecimal unitQuantity) {
        Query query = em.createNamedQuery("BC_READ_FIRST_SHIPPING_RATE_BY_FEE_TYPES");
        query.setParameter("feeType", feeType);
        query.setParameter("feeSubType", feeSubType);
        query.setParameter("bandUnitQuantity", unitQuantity);
        List<ShippingRate> returnedRates = query.getResultList();
        if (returnedRates.size() > 0) {
            return returnedRates.get(0);
        } else {
            return null;
        }
    }

    @Override
    public ShippingRate create() {
        return (ShippingRate) entityConfiguration.createEntityInstance(ShippingRate.class.getName());
    }
}
