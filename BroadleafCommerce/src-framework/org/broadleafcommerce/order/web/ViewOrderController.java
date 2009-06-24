/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.order.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.FulfillmentGroupService;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.order.web.model.FindOrderForm;
import org.broadleafcommerce.payment.service.PaymentInfoService;
import org.broadleafcommerce.pricing.dao.ShippingRateDao;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.dao.CustomerDao;
import org.broadleafcommerce.profile.dao.StateDao;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("viewOrderController")
public class ViewOrderController {

    //  @Resource
    //  private CustomerState customerState;
    @Resource
    protected OrderService orderService;
    @Resource
    protected CustomerService customerService;
    @Resource
    protected OrderDao orderDao;
    @Resource
    protected ShippingRateDao shippingRateDao;
    @Resource
    protected FulfillmentGroupService fulfillmentGroupService;
    @Resource
    protected PaymentInfoService paymentInfoService;
    @Resource
    protected CatalogService catalogService;
    @Resource
    protected AddressDao addressDao;
    @Resource
    protected StateDao stateDao;
    @Resource
    protected CustomerDao customerDao;
    @Resource
    protected FulfillmentGroupItemDao fulfillmentGroupItemDao;
    @Resource
    protected OrderItemDao orderItemDao;

    @RequestMapping(method =  {RequestMethod.GET})
    public String viewOrders (ModelMap model, HttpServletRequest request) throws PricingException {
        Customer customer = customerService.readCustomerById(1L);
        // List<Order> orders = orderService.findOrdersForCustomer(customerState.getCustomer(request), OrderStatus.SUBMITTED);
        List<Order> orders = orderService.findOrdersForCustomer(customer, OrderStatus.SUBMITTED);
        model.addAttribute("orderList", orders);
        return "listOrders";
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String viewOrderDetails (ModelMap model, HttpServletRequest request, @RequestParam(required = true) String orderNumber) {
        Order order = orderService.findOrderByOrderNumber(orderNumber);

        if (order == null)
        {
            return "findOrderError";
        }

        model.addAttribute("order", order);
        return "viewOrderDetails";
    }

    @RequestMapping(method =  {RequestMethod.GET})
    public String showFindOrder (ModelMap model, HttpServletRequest request) {
        model.addAttribute("findOrderForm", new FindOrderForm());
        return "findOrder";
    }

    @RequestMapping(method =  {RequestMethod.POST})
    public String processFindOrder (@ModelAttribute FindOrderForm findOrderForm, ModelMap model, HttpServletRequest request) {
        boolean zipFound = false;
        Order order = orderService.findOrderByOrderNumber(findOrderForm.getOrderNumber());

        if (order == null)
        {
            return "findOrderError";
        }

        List<FulfillmentGroup> orderFulfillmentGroups = order.getFulfillmentGroups();
        if (orderFulfillmentGroups != null ) {
            orderLoop: for (FulfillmentGroup fulfillmentGroup : orderFulfillmentGroups)
            {
                if (fulfillmentGroup.getAddress().getPostalCode().equals(findOrderForm.getPostalCode())) {
                    zipFound = true;
                    break orderLoop;
                }
            }
        }

        if (zipFound) {
            return viewOrderDetails(model, request, order.getOrderNumber());
        }

        return "findOrderError";
    }

    //    public void setCustomerState(CustomerState customerState) {
    //        this.customerState = customerState;
    //    }
}
