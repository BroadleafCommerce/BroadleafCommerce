/**
 * Copyright 2012 the original author or authors.
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
package org.broadleafcommerce.core.inventory.dao;

import org.apache.commons.lang.BooleanUtils;
import org.broadleafcommerce.core.inventory.domain.FulfillmentLocation;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository("blFulfillmentLocationDao")
public class FulfillmentLocationDaoImpl implements FulfillmentLocationDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @SuppressWarnings("unchecked")
    @Override
    public List<FulfillmentLocation> readAll() {
        return em.createNamedQuery("BC_READ_ALL_FULFILLMENT_LOCATIONS").getResultList();
    }

    @Override
    public FulfillmentLocation readById(Long fulfillmentLocationId) {
        return em.find(FulfillmentLocation.class, fulfillmentLocationId);
    }

    @Override
    public FulfillmentLocation save(FulfillmentLocation fulfillmentLocation) {
        if (BooleanUtils.isTrue(fulfillmentLocation.getDefaultLocation())) {
            em.createNamedQuery("BC_UPDATE_ALL_FULFILLMENT_LOCATIONS_TO_NOT_DEFAULT").executeUpdate();
        }
        return em.merge(fulfillmentLocation);
    }

    @Override
    public void delete(FulfillmentLocation fulfillmentLocation) {
        em.remove(fulfillmentLocation);
    }

}
