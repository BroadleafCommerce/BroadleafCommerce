#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.PaymentInfoService;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.web.checkout.model.CheckoutForm;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This is a simple demonstration controller that populates the order with
 * some default, hard-coded shipping destination data and then engages
 * the checkout workflow.
 * 
 * @author jfischer
 *
 */
@Controller("mycompanyCheckoutController")
@RequestMapping("/checkout")
public class CheckoutController {

    private static final Log LOG = LogFactory.getLog(CheckoutController.class);

    @Resource(name="blCartService")
    protected CartService cartService;
    @Resource(name="blCustomerState")
    protected CustomerState customerState;
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

    @RequestMapping(value = "/checkout.htm", method = {RequestMethod.GET})
    public String checkout(@ModelAttribute CheckoutForm checkoutForm,
            BindingResult errors,
            ModelMap model,
            HttpServletRequest request) {

        Order order = retrieveCartOrder(request, model);
        order.setOrderNumber(new SimpleDateFormat("yyyyMMddHHmmssS").format(SystemTime.asDate()));

        List<FulfillmentGroup> groups = order.getFulfillmentGroups();
        if(groups.size() < 1){
        	return "redirect:/basket/currentCart.htm";
        }
        FulfillmentGroup group = groups.get(0);
        group.setOrder(order);
        
        AddressImpl address = new AddressImpl();
        address.setActive(true);
        address.setAddressLine1("123 Test Rd");
        address.setBusiness(false);
        address.setCity("Dallas");
        address.setCountry(countryService.findCountryByAbbreviation("US"));
        address.setFirstName("Test");
        address.setLastName("Tester");
        address.setPostalCode("75222");
        address.setPrimaryPhone("214-214-1234");
        address.setState(stateService.findStateByAbbreviation("TX"));

        group.setAddress(address);
        group.setShippingPrice(new Money(8D));

        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();
        CreditCardPaymentInfo creditCardPaymentInfo = ((CreditCardPaymentInfo) securePaymentInfoService.create(PaymentInfoType.CREDIT_CARD));

        creditCardPaymentInfo.setCvvCode("123");
        creditCardPaymentInfo.setExpirationMonth(12);
        creditCardPaymentInfo.setExpirationYear(2013);
        creditCardPaymentInfo.setPan("1111111111111111");
        creditCardPaymentInfo.setReferenceNumber("1234");

        PaymentInfo paymentInfo = paymentInfoService.create();
        paymentInfo.setAddress(address);
        paymentInfo.setOrder(order);
        paymentInfo.setType(PaymentInfoType.CREDIT_CARD);
        paymentInfo.setReferenceNumber("1234");
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

        model.addAttribute("order", order);
        
        return "checkout/checkoutConfirmation";
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