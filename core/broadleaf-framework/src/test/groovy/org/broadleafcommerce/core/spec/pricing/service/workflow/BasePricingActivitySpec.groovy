package org.broadleafcommerce.core.spec.pricing.service.workflow

import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.workflow.BaseActivity
import org.broadleafcommerce.core.workflow.DefaultProcessContextImpl
import org.broadleafcommerce.core.workflow.ProcessContext
import org.broadleafcommerce.profile.core.domain.Customer
import org.broadleafcommerce.profile.core.domain.CustomerImpl

import spock.lang.Specification

class BasePricingActivitySpec extends Specification{

	BaseActivity<ProcessContext<Order>> activity;
	ProcessContext<Order> context;
	
	def setup(){
		Customer customer = new CustomerImpl()
		customer.id = 1
		Order order = new OrderImpl()
		order.id = 1
		order.customer = customer
		context = new DefaultProcessContextImpl<Order>().with(){
			seedData = order
			it
		}
	}
}
