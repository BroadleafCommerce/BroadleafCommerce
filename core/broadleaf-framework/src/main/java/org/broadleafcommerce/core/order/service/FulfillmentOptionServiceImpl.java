/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.dao.FulfillmentOptionDao;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * @author Phillip Verheyden
 */
@Service("blFulfillmentOptionService")
@Transactional("blTransactionManager")
public class FulfillmentOptionServiceImpl implements FulfillmentOptionService {

    @Resource(name = "blFulfillmentOptionDao")
    FulfillmentOptionDao fulfillmentOptionDao;

    @Override
    public FulfillmentOption readFulfillmentOptionById(Long fulfillmentOptionId) {
        return fulfillmentOptionDao.readFulfillmentOptionById(fulfillmentOptionId);
    }

    @Override
    public FulfillmentOption save(FulfillmentOption option) {
        return fulfillmentOptionDao.save(option);
    }

    @Override
    public List<FulfillmentOption> readAllFulfillmentOptions() {
        return fulfillmentOptionDao.readAllFulfillmentOptions();
    }

    @Override
    public List<FulfillmentOption> readAllFulfillmentOptionsByFulfillmentType(FulfillmentType type) {
        return fulfillmentOptionDao.readAllFulfillmentOptionsByFulfillmentType(type);
    }
}
