/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.pricing.service.workflow;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.TaxService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Utilized within the blPricingWorkflow to calculate tax for an {@link Order}
 */
@Component("blTaxActivity")
public class TaxActivity extends BaseActivity<ProcessContext<Order>> {

    public static final int ORDER = 7000;

    @Resource(name = "blTaxService")
    protected TaxService taxService;

    public TaxActivity() {
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();

        if (taxService != null) {
            order = taxService.calculateTaxForOrder(order);
        } else {
            throw new RuntimeException("Tax service not found and old deprecated Tax Module was deleted!, set TaxService");
        }

        context.setSeedData(order);
        return context;
    }

    public void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }

}
