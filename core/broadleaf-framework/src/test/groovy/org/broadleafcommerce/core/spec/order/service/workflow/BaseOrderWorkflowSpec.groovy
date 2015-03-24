package org.broadleafcommerce.core.spec.order.service.workflow

import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest
import org.broadleafcommerce.core.workflow.BaseActivity
import org.broadleafcommerce.core.workflow.DefaultProcessContextImpl
import org.broadleafcommerce.core.workflow.ProcessContext
import org.broadleafcommerce.profile.core.domain.Customer
import org.broadleafcommerce.profile.core.domain.CustomerImpl

import spock.lang.Specification


class BaseOrderWorkflowSpec extends Specification{

    BaseActivity<ProcessContext<CartOperationRequest>> activity
    ProcessContext<CartOperationRequest> context
    
    def setup() {
        context = new DefaultProcessContextImpl<CartOperationRequest>().with() {
            Customer customer = new CustomerImpl()
            customer.id = 1
            Order order = new OrderImpl()
            order.id = 1
            order.customer = customer
            OrderItemRequestDTO itemRequest = Spy(OrderItemRequestDTO)
            seedData = new CartOperationRequest(order,itemRequest,true)
            it
        }
    }
}
