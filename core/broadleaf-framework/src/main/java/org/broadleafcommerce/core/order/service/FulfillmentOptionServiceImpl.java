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
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.dao.FulfillmentOptionDao;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

/**
 * 
 * @author Phillip Verheyden
 */
@Service("blFulfillmentOptionService")
public class FulfillmentOptionServiceImpl implements FulfillmentOptionService {

    @Resource(name = "blFulfillmentOptionDao")
    protected FulfillmentOptionDao fulfillmentOptionDao;

    @Override
    public FulfillmentOption readFulfillmentOptionById(Long fulfillmentOptionId) {
        return fulfillmentOptionDao.readFulfillmentOptionById(fulfillmentOptionId);
    }

    @Override
    @Transactional("blTransactionManager")
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
