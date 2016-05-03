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

import javax.annotation.Resource;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.Processor;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.springframework.stereotype.Service;

@Service("blPricingService")
public class PricingServiceImpl implements PricingService {

    @Resource(name="blPricingWorkflow")
    protected Processor pricingWorkflow;

    public Order executePricing(Order order) throws PricingException {
        try {
            ProcessContext<Order> context = (ProcessContext<Order>) pricingWorkflow.doActivities(order);
            Order response = context.getSeedData();

            return response;
        } catch (WorkflowException e) {
            throw new PricingException("Unable to execute pricing for order -- id: " + order.getId(), e);
        }
    }

}
