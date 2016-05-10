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
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.workflow.BaseActivity
import org.broadleafcommerce.core.workflow.DefaultProcessContextImpl
import org.broadleafcommerce.core.workflow.ProcessContext
import org.broadleafcommerce.profile.core.domain.Customer
import org.broadleafcommerce.profile.core.domain.CustomerImpl

import spock.lang.Specification

/**
 * @author Elbert Bautista (elbertbautista)
 */
class BaseCheckoutActivitySpec extends Specification {

    BaseActivity<ProcessContext<CheckoutSeed>> activity;
    ProcessContext<CheckoutSeed> context;

    def setup() {
        Customer customer = new CustomerImpl()
        customer.id = 1
        Order order = new OrderImpl()
        order.id = 1
        order.customer = customer
        context = new DefaultProcessContextImpl<CheckoutSeed>().with{
			seedData = new CheckoutSeed(order, new HashMap<String, Object>())
			it
		} 
    }

}
