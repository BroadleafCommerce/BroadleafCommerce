/*-
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.web.expression.OrderVariableExpression;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Component;

import java.util.Map;

import jakarta.annotation.Resource;

/**
 * <p>
 * A Thymeleaf processor that will add the desired named order to the model
 *
 * <p>
 * Example:
 *
 * <pre>
 *  &lt;blc:named_order orderVar="wishlist" orderName="wishlist" /&gt;
 *  &lt;span th:text="${wishlist.customer.name}" /&gt;
 * </pre>
 *
 * @param orderVar  the value that the order will be assigned to
 * @param orderName the name of the order, {@link Order#getName()}
 * @author elbertbautista
 * @see {@link Order#getName()}
 * @deprecated use {@link OrderVariableExpression#getNamedOrderForCurrentCustomer(String)} instead
 */
@Deprecated
@Component("blNamedOrderProcessor")
@ConditionalOnTemplating
public class NamedOrderProcessor implements NamedOrderExpression {

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Override
    public String getName() {
        return "named_order";
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    public Order getWishlist() {
        Customer customer = CustomerState.getCustomer();
        String orderName = "wishlist";

        Map<String, Object> additionalProperties = BroadleafRequestContext.getBroadleafRequestContext()
                .getAdditionalProperties();
        Object wishlist = additionalProperties.get("NAMED_ORDER_WISHLIST");
        if (wishlist != null) {
            return (Order) wishlist;
        }

        Order order = orderService.findNamedOrderForCustomer(orderName, customer);
        if (order != null) {
            additionalProperties.put("NAMED_ORDER_WISHLIST", order);
            return order;
        }
        NullOrderImpl nullOrder = new NullOrderImpl();
        additionalProperties.put("NAMED_ORDER_WISHLIST", nullOrder);
        return nullOrder;
    }

}
