/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.pricing.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.pricing.domain.ShippingRate;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blShippingRatesDao")
public class ShippingRateDaoImpl implements ShippingRateDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public Address save(Address address) {
        if (address.getId() == null) {
            em.persist(address);
        } else {
            address = em.merge(address);
        }
        return address;
    }

    public ShippingRate save(ShippingRate shippingRate) {
        if (shippingRate.getId() == null) {
            em.persist(shippingRate);
        } else {
            shippingRate = em.merge(shippingRate);
        }
        return shippingRate;
    }

    @SuppressWarnings("unchecked")
    public ShippingRate readShippingRateById(Long id) {
        return (ShippingRate) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.pricing.domain.ShippingRate"), id);
    }

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

    public ShippingRate create() {
        return (ShippingRate) entityConfiguration.createEntityInstance(ShippingRate.class.getName());
    }
}
