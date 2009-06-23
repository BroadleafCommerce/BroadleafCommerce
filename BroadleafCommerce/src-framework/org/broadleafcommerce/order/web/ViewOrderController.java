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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.FulfillmentGroupService;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.order.service.type.OrderItemType;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.order.web.model.FindOrderForm;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.service.PaymentInfoService;
import org.broadleafcommerce.pricing.dao.ShippingRateDao;
import org.broadleafcommerce.pricing.domain.ShippingRate;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.dao.CustomerDao;
import org.broadleafcommerce.profile.dao.StateDao;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.util.money.Money;
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
    public String createNewOrder (ModelMap model, HttpServletRequest request) throws PricingException {
        ShippingRate sr = shippingRateDao.create();
        sr.setFeeType("SHIPPING");
        sr.setFeeSubType("ALL");
        sr.setFeeBand(1);
        sr.setBandUnitQuantity(BigDecimal.valueOf(29.99));
        sr.setBandResultQuantity(BigDecimal.valueOf(8.5));
        sr.setBandResultPercent(0);
        ShippingRate sr2 = shippingRateDao.create();
        sr2.setFeeType("SHIPPING");
        sr2.setFeeSubType("ALL");
        sr2.setFeeBand(2);
        sr2.setBandUnitQuantity(BigDecimal.valueOf(999999.99));
        sr2.setBandResultQuantity(BigDecimal.valueOf(8.5));
        sr2.setBandResultPercent(0);

        shippingRateDao.save(sr);
        shippingRateDao.save(sr2);

        Address addr = addressDao.create();
        addr.setCity("Dallas");
        State state = stateDao.create();
        state.setAbbreviation("TX");
        state.setName("Texas");
        addr.setState(state);
        addr.setAddressLine1("5657 Amesbury Drive");
        addr.setPostalCode("75206");

        Order order = orderDao.create();

        Customer customer = customerDao.create();
        customer.setFirstName("AJ");
        customer.setLastName("Angus");
        customer.setEmailAddress("jay@aj.com");
        customer.setId(new Long(1));
        customerService.saveCustomer(customer);
        order.setCustomer(customer);

        FulfillmentGroup group = fulfillmentGroupService.createEmptyFulfillmentGroup();
        FulfillmentGroup group2 = fulfillmentGroupService.createEmptyFulfillmentGroup();

        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();

        group.setMethod("standard");
        group.setOrder(order);
        group.setAddress(addr);
        group.setShippingPrice(new Money(5D));

        group2.setMethod("standard");
        group2.setOrder(order);
        group2.setAddress(addr);
        group2.setShippingPrice(new Money(10D));

        FulfillmentGroupItem fulfillmentGroupItem = fulfillmentGroupItemDao.create();
        FulfillmentGroupItem fulfillmentGroupItem2 = fulfillmentGroupItemDao.create();
        FulfillmentGroupItem fulfillmentGroupItem3 = fulfillmentGroupItemDao.create();

        DiscreteOrderItem item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        Sku sku = catalogService.findSkuById(1L);
        item.setSku(sku);
        item.setQuantity(2);
        order.addOrderItem(item);
        fulfillmentGroupItem.setPrice(sku.getSalePrice());
        fulfillmentGroupItem.setOrderItem(item);

        item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        sku = catalogService.findSkuById(2L);
        item.setSku(sku);
        item.setQuantity(1);
        order.addOrderItem(item);
        fulfillmentGroupItem.setPrice(sku.getSalePrice());
        fulfillmentGroupItem2.setOrderItem(item);

        item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        sku = catalogService.findSkuById(130L);
        item.setSku(sku);
        item.setQuantity(1);
        order.addOrderItem(item);
        fulfillmentGroupItem.setPrice(sku.getSalePrice());
        fulfillmentGroupItem3.setOrderItem(item);

        fulfillmentGroupItem.setFulfillmentGroup(group);
        fulfillmentGroupItem2.setFulfillmentGroup(group);
        fulfillmentGroupItem3.setFulfillmentGroup(group2);

        group.getFulfillmentGroupItems().add(fulfillmentGroupItem);
        group.getFulfillmentGroupItems().add(fulfillmentGroupItem2);
        group2.getFulfillmentGroupItems().add(fulfillmentGroupItem3);

        groups.add(group);
        groups.add(group2);
        order.setFulfillmentGroups(groups);

        PaymentInfo paymentInfo = paymentInfoService.create();
        paymentInfo.setAddress(addr);
        paymentInfo.setOrder(order);
        List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();
        paymentInfos.add(paymentInfo);
        order.setPaymentInfos(paymentInfos);

        order.setTotalShipping(new Money(0D));
        order.setStatus(OrderStatus.SUBMITTED);
        order.setOrderNumber("1234");
        order.setSubmitDate(new Date());
        orderService.save(order, true);

        return viewOrders(model, request);
    }

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
