/**
 * 
 */
package org.broadleafcommerce.core.web.expression;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;

import javax.annotation.Resource;


/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class OrderVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Override
    public String getName() {
        return "orders";
    }

    public Order getNamedOrderForCurrentCustomer(String orderName) {
        return getNamedOrderForCustomer(orderName, CustomerState.getCustomer());
    }
    
    public Order getNamedOrderForCustomer(String orderName, Customer customer) {
        return orderService.findNamedOrderForCustomer(orderName, customer);
    }
}
