package org.broadleafcommerce.order.service;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.springframework.stereotype.Service;

@Service("fulfillmentGroupService")
public class FulfillmentGroupServiceImpl implements FulfillmentGroupService {

    @Resource
    private FulfillmentGroupDao fulfillmentGroupDao;

    @Override
    public FulfillmentGroup save(FulfillmentGroup fulfillmentGroup) {
        return fulfillmentGroupDao.save(fulfillmentGroup);
    }

}
