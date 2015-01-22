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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.common.event.OrderSubmittedEvent;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CompleteOrderActivity extends BaseActivity<ProcessContext<CheckoutSeed>> implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    public CompleteOrderActivity() {
        //no specific state to set here for the rollback handler; it's always safe for it to run
        setAutomaticallyRegisterRollbackHandler(true);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        CheckoutSeed seed = context.getSeedData();

        seed.getOrder().setStatus(getCompletedStatus());
        seed.getOrder().setOrderNumber(new SimpleDateFormat("yyyyMMddHHmmssS").format(SystemTime.asDate()) + seed.getOrder().getId());
        seed.getOrder().setSubmitDate(Calendar.getInstance().getTime());

        OrderSubmittedEvent event = new OrderSubmittedEvent(seed.getOrder().getId(), seed.getOrder().getOrderNumber());
        applicationContext.publishEvent(event);

        return context;
    }

    protected OrderStatus getCompletedStatus() {
        return OrderStatus.SUBMITTED;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
