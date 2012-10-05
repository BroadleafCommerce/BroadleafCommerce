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
package org.broadleafcommerce.core.inventory.service;

import org.apache.commons.lang.BooleanUtils;
import org.broadleafcommerce.core.inventory.dao.FulfillmentLocationDao;
import org.broadleafcommerce.core.inventory.domain.FulfillmentLocation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("blFulfillmentLocationService")
@Transactional(value="blTransactionManager")
public class FulfillmentLocationServiceImpl implements FulfillmentLocationService {

    @Resource(name = "blFulfillmentLocationDao")
    protected FulfillmentLocationDao fulfillmentLocationDao;

    @Override
    public List<FulfillmentLocation> readAll() {
        return fulfillmentLocationDao.readAll();
    }

    @Override
    public FulfillmentLocation readById(Long fulfillmentLocationId) {
        return fulfillmentLocationDao.readById(fulfillmentLocationId);
    }

    @Override
    public FulfillmentLocation save(FulfillmentLocation fulfillmentLocation) {
        return fulfillmentLocationDao.save(fulfillmentLocation);
    }

    @Override
    public void delete(FulfillmentLocation fulfillmentLocation) {
        fulfillmentLocationDao.delete(fulfillmentLocation);
    }

    @Override
    public void updateOtherDefaultLocationToFalse(FulfillmentLocation fulfillmentLocation) {
        fulfillmentLocationDao.updateOtherDefaultLocationToFalse(fulfillmentLocation);
    }
}
