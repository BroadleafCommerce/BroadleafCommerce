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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.common.event.OrderSubmittedEvent;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component("blCompleteOrderActivity")
public class CompleteOrderActivity extends BaseActivity<ProcessContext<CheckoutSeed>> implements ApplicationContextAware {

    public static final int ORDER = 7000;
    
    protected ApplicationContext applicationContext;

    @Autowired
    public CompleteOrderActivity(@Qualifier("blCompleteOrderRollbackHandler") CompleteOrderRollbackHandler rollbackHandler) {
        //no specific state to set here for the rollback handler; it's always safe for it to run
        setAutomaticallyRegisterRollbackHandler(true);
        setRollbackHandler(rollbackHandler);
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        CheckoutSeed seed = context.getSeedData();

        seed.getOrder().setStatus(getCompletedStatus());
        seed.getOrder().setOrderNumber(determineOrderNumber(seed.getOrder()));
        seed.getOrder().setSubmitDate(determineSubmitDate(seed.getOrder()));

        OrderSubmittedEvent event = new OrderSubmittedEvent(seed.getOrder().getId(), seed.getOrder().getOrderNumber());
        applicationContext.publishEvent(event);

        return context;
    }

    protected Date determineSubmitDate(Order order) {
        return Calendar.getInstance().getTime();
    }

    protected String determineOrderNumber(Order order) {
        return new SimpleDateFormat("yyyyMMddHHmmssS").format(SystemTime.asDate()) + order.getId();
    }

    protected OrderStatus getCompletedStatus() {
        return OrderStatus.SUBMITTED;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
