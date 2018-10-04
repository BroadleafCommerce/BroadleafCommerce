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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.checkout.service.CheckoutService;
import org.broadleafcommerce.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.checkout.web.model.CheckoutForm;
import org.broadleafcommerce.checkout.web.validator.CheckoutFormValidator;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.PaymentInfoService;
import org.broadleafcommerce.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.domain.CustomerPhoneImpl;
import org.broadleafcommerce.profile.service.CountryService;
import org.broadleafcommerce.profile.service.CustomerAddressService;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.broadleafcommerce.profile.service.StateService;
import org.broadleafcommerce.profile.web.CustomerState;
import org.broadleafcommerce.time.SystemTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("blCheckoutController")
public class CheckoutController {

    private static final Log LOG = LogFactory.getLog(CheckoutController.class);

    @Resource(name="blCartService")
    protected CartService cartService;
    @Resource(name="blCustomerState")
    protected CustomerState customerState;
    @Resource(name="blCustomerAddressService")
    protected CustomerAddressService customerAddressService;
    @Resource(name="blCustomerPhoneService")
    protected CustomerPhoneService customerPhoneService;
    @Resource(name="blCheckoutService")
    protected CheckoutService checkoutService;
    @Resource(name="blStateService")
    protected StateService stateService;
    @Resource(name="blCountryService")
    protected CountryService countryService;
    @Resource(name="blPaymentInfoService")
    protected PaymentInfoService paymentInfoService;
    @Resource(name="blSecurePaymentInfoService")
    private SecurePaymentInfoService securePaymentInfoService;
    @Resource(name="blCheckoutFormValidator")
    private CheckoutFormValidator checkoutFormValidator;

    protected String checkoutView;
    protected String receiptView;

    public void setReceiptView(String receiptView) {
        this.receiptView = receiptView;
    }

    public void setCheckoutView(String checkoutView) {
        this.checkoutView = checkoutView;
    }

    private CheckoutForm copyAddress (CheckoutForm checkoutForm) {
        checkoutForm.getShippingAddress().setFirstName(checkoutForm.getBillingAddress().getFirstName());
        checkoutForm.getShippingAddress().setLastName(checkoutForm.getBillingAddress().getLastName());
        checkoutForm.getShippingAddress().setAddressLine1(checkoutForm.getBillingAddress().getAddressLine1());
        checkoutForm.getShippingAddress().setAddressLine2(checkoutForm.getBillingAddress().getAddressLine2());
        checkoutForm.getShippingAddress().setCity(checkoutForm.getBillingAddress().getCity());
        checkoutForm.getShippingAddress().setState(checkoutForm.getBillingAddress().getState());
        checkoutForm.getShippingAddress().setPostalCode(checkoutForm.getBillingAddress().getPostalCode());
        checkoutForm.getShippingAddress().setCountry(checkoutForm.getBillingAddress().getCountry());
        checkoutForm.getShippingAddress().setPrimaryPhone(checkoutForm.getBillingAddress().getPrimaryPhone());

        return checkoutForm;
    }

    @RequestMapping(value = "checkout.htm", method = {RequestMethod.POST})
    public String processCheckout(@ModelAttribute CheckoutForm checkoutForm,
            BindingResult errors,
            ModelMap model,
            HttpServletRequest request) {

        if (checkoutForm.getIsSameAddress()) {
            copyAddress(checkoutForm);
        }

        checkoutFormValidator.validate(checkoutForm, errors);

        if (errors.hasErrors()) {
            return checkout(checkoutForm, errors, model, request);
        }

        checkoutForm.getBillingAddress().setCountry(countryService.findCountryByAbbreviation(checkoutForm.getBillingAddress().getCountry().getAbbreviation()));
        checkoutForm.getBillingAddress().setState(stateService.findStateByAbbreviation(checkoutForm.getBillingAddress().getState().getAbbreviation()));
        checkoutForm.getShippingAddress().setCountry(countryService.findCountryByAbbreviation(checkoutForm.getShippingAddress().getCountry().getAbbreviation()));
        checkoutForm.getShippingAddress().setState(stateService.findStateByAbbreviation(checkoutForm.getShippingAddress().getState().getAbbreviation()));

        Order order = retrieveCartOrder(request, model);
        order.setOrderNumber(new SimpleDateFormat("yyyyMMddHHmmssS").format(SystemTime.asDate()));

        List<FulfillmentGroup> groups = order.getFulfillmentGroups();
        FulfillmentGroup group = groups.get(0);
        group.setOrder(order);
        group.setAddress(checkoutForm.getShippingAddress());
        group.setShippingPrice(order.getTotalShipping());

        for (OrderItem item:order.getOrderItems()) {
            FulfillmentGroupItem fulfillmentGroupItem = new FulfillmentGroupItemImpl();
            fulfillmentGroupItem.setOrderItem(item);
            fulfillmentGroupItem.setFulfillmentGroup(group);
            group.getFulfillmentGroupItems().add(fulfillmentGroupItem);
        }

        //TODO this controller needs to handle the other payment types as well, not just credit card.
        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();
        CreditCardPaymentInfo creditCardPaymentInfo = ((CreditCardPaymentInfo) securePaymentInfoService.create(PaymentInfoType.CREDIT_CARD));

        creditCardPaymentInfo.setCvvCode(checkoutForm.getCreditCardCvvCode());
        creditCardPaymentInfo.setExpirationMonth(Integer.parseInt(checkoutForm.getCreditCardExpMonth()));
        creditCardPaymentInfo.setExpirationYear(Integer.parseInt(checkoutForm.getCreditCardExpYear()));
        creditCardPaymentInfo.setPan(checkoutForm.getCreditCardNumber());
        creditCardPaymentInfo.setReferenceNumber(checkoutForm.getCreditCardNumber());

        PaymentInfo paymentInfo = paymentInfoService.create();
        paymentInfo.setAddress(checkoutForm.getBillingAddress());
        paymentInfo.setOrder(order);
        paymentInfo.setType(PaymentInfoType.CREDIT_CARD);
        paymentInfo.setReferenceNumber(checkoutForm.getCreditCardNumber());
        paymentInfo.setAmount(order.getTotal());
        payments.put(paymentInfo, creditCardPaymentInfo);
        List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();
        paymentInfos.add(paymentInfo);
        order.setPaymentInfos(paymentInfos);

        order.setStatus(OrderStatus.SUBMITTED);
        order.setSubmitDate(Calendar.getInstance().getTime());

        try {
            checkoutService.performCheckout(order, payments);
        } catch (CheckoutException e) {
            LOG.error("Cannot perform checkout", e);
        }

        return receiptView != null ? "redirect:" + receiptView : "redirect:/orders/viewOrderConfirmation.htm?orderNumber=" + order.getOrderNumber();
    }

    @RequestMapping(value = "checkout.htm", method = {RequestMethod.GET})
    public String checkout(@ModelAttribute CheckoutForm checkoutForm,
            BindingResult errors,
            ModelMap model,
            HttpServletRequest request) {

        model.addAttribute("stateList", stateService.findStates());
        model.addAttribute("countryList", countryService.findCountries());

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
