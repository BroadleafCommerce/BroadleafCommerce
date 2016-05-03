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
