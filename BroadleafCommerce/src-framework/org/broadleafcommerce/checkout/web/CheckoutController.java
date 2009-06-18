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
package org.broadleafcommerce.checkout.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.checkout.service.CheckoutService;
import org.broadleafcommerce.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.checkout.web.model.CheckoutForm;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.FulfillmentGroupService;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.PaymentInfoService;
import org.broadleafcommerce.payment.service.PaymentService;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.domain.CustomerPhoneImpl;
import org.broadleafcommerce.profile.service.CountryService;
import org.broadleafcommerce.profile.service.CustomerAddressService;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.broadleafcommerce.profile.service.StateService;
import org.broadleafcommerce.profile.web.CustomerState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("blCheckoutController")
public class CheckoutController {
    @Resource
    protected CartService cartService;
    @Resource
    protected CustomerState customerState;
    @Resource
    CustomerAddressService customerAddressService;
    @Resource
    protected CustomerPhoneService customerPhoneService;
    @Resource(name="blCreditCardService")
    protected PaymentService paymentService;

    @Resource
    protected OrderService orderService;
    @Resource
    protected CheckoutService checkoutService;
    @Resource
    protected CatalogService catalogService;
    @Resource
    protected StateService stateService;
    @Resource
    protected CountryService countryService;
    @Resource
    protected FulfillmentGroupService fulfillmentGroupService;
    @Resource
    protected PaymentInfoService paymentInfoService;
    protected String checkoutView;
    protected String receiptView;

    public void setReceiptView(String receiptView) {
        this.receiptView = receiptView;
    }

    public void setCheckoutView(String checkoutView) {
        this.checkoutView = checkoutView;
    }

    @RequestMapping(value = "checkout.htm", method = {RequestMethod.POST})
    public String processCheckout(@ModelAttribute CheckoutForm checkoutForm,
            BindingResult errors,
            ModelMap model,
            HttpServletRequest request) {

        Address addr = new AddressImpl();
        addr.setAddressLine1("12700 Merit Drive Way");
        addr.setCity("Dallas");
        addr.setState(stateService.findStateByAbbreviation("TX"));
        addr.setPostalCode("75251");
        addr.setCountry(countryService.findCountryByAbbreviation("US"));

        Order order = retrieveCartOrder(request, model);
        order.setOrderNumber(new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date()));

        FulfillmentGroup group = fulfillmentGroupService.createEmptyFulfillmentGroup();
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        group.setMethod("standard");
        group.setOrder(order);
        group.setAddress(addr);
        group.setShippingPrice(order.getTotalShipping());
        groups.add(group);
        order.setFulfillmentGroups(groups);

        for (OrderItem item:order.getOrderItems()) {
            FulfillmentGroupItem fulfillmentGroupItem = new FulfillmentGroupItemImpl();
            fulfillmentGroupItem.setOrderItem(item);
            fulfillmentGroupItem.setFulfillmentGroup(group);
            group.getFulfillmentGroupItems().add(fulfillmentGroupItem);
        }

        PaymentInfo paymentInfo = paymentInfoService.create();
        paymentInfo.setAddress(addr);
        paymentInfo.setOrder(order);
        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();
        order.setStatus(OrderStatus.SUBMITTED.toString());
        order.setSubmitDate(new Date());

        try {
            checkoutService.performCheckout(order, payments);
        } catch (CheckoutException e) {
            e.printStackTrace();
        }

        return receiptView != null ? "redirect:" + receiptView : "redirect:/orders/viewOrderDetails?orderNumber=" + order.getOrderNumber();
    }

    @RequestMapping(value = "checkout.htm", method = {RequestMethod.GET})
    public String checkout(@ModelAttribute CheckoutForm checkoutForm,
            BindingResult errors,
            ModelMap model,
            HttpServletRequest request) {

        Customer currentCustomer = customerState.getCustomer(request);
        model.addAttribute("customer", currentCustomer);

        List<CustomerPhone> customerPhones = customerPhoneService.readAllCustomerPhonesByCustomerId(currentCustomer.getId());
        while(customerPhones.size() < 2) {
            customerPhones.add(new CustomerPhoneImpl());
        }

        customerAddressService.readActiveCustomerAddressesByCustomerId(currentCustomer.getId());

        model.addAttribute("order", retrieveCartOrder(request, model));

        return checkoutView;
    }


    protected Order retrieveCartOrder(HttpServletRequest request, ModelMap model) {
        Customer currentCustomer = customerState.getCustomer(request);
        Order currentCartOrder = null;
        if (currentCustomer != null) {
            currentCartOrder = cartService.findCartForCustomer(currentCustomer);
            if (currentCartOrder == null) {
                currentCartOrder = cartService.createNewCartForCustomer(currentCustomer);
            }
        }

        return currentCartOrder;
    }

}
