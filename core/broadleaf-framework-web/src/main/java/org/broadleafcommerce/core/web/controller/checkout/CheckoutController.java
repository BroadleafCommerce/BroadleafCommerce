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

package org.broadleafcommerce.core.web.controller.checkout;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.AmountItem;
import org.broadleafcommerce.core.payment.domain.AmountItemImpl;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.EmptyReferenced;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.domain.TotalledPaymentInfoImpl;
import org.broadleafcommerce.core.payment.service.CompositePaymentService;
import org.broadleafcommerce.core.payment.service.PaymentInfoService;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.payment.service.workflow.CompositePaymentResponse;
import org.broadleafcommerce.core.web.checkout.model.CheckoutForm;
import org.broadleafcommerce.core.web.checkout.validator.CheckoutFormValidator;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPhone;
import org.broadleafcommerce.profile.core.domain.CustomerPhoneImpl;
import org.broadleafcommerce.profile.core.service.*;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("blCheckoutController")
@RequestMapping("/checkout")
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
    protected SecurePaymentInfoService securePaymentInfoService;
    @Resource(name="blCheckoutFormValidator")
    protected CheckoutFormValidator checkoutFormValidator;
    @Resource(name="blCustomerService")
    protected CustomerService customerService;
    
    @Resource(name="blCompositePaymentService")
    protected CompositePaymentService compositePaymentService;

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

    @RequestMapping(value = "/checkout.htm", method = {RequestMethod.POST})
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
        if(groups.size() < 1){
        	return "redirect:/basket/currentCart.htm";
        }
        FulfillmentGroup group = groups.get(0);
        group.setOrder(order);
        group.setAddress(checkoutForm.getShippingAddress());
        group.setShippingPrice(order.getTotalShipping());

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

        //Fill out a few customer values for anonymous customers
        Customer customer = order.getCustomer();
        if (StringUtils.isEmpty(customer.getFirstName())) {
            customer.setFirstName(checkoutForm.getBillingAddress().getFirstName());
        }
        if (StringUtils.isEmpty(customer.getLastName())) {
            customer.setLastName(checkoutForm.getBillingAddress().getLastName());
        }
        if (StringUtils.isEmpty(customer.getEmailAddress())) {
            customer.setEmailAddress(order.getEmailAddress());
        }
        customerService.saveCustomer(customer, false);

        return receiptView != null ? "redirect:" + receiptView : "redirect:/orders/viewOrderConfirmation.htm?orderNumber=" + order.getOrderNumber();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/checkout.htm", method = {RequestMethod.GET})
    public String checkout(@ModelAttribute CheckoutForm checkoutForm,
            BindingResult errors,
            ModelMap model,
            HttpServletRequest request) {

        model.addAttribute("stateList", stateService.findStates());
        List<Country> countries = countryService.findCountries();
        Collections.sort(countries, new ReverseComparator(new BeanComparator("abbreviation")));
        model.addAttribute("countryList", countries);

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

    //TODO move this paypal specific controller into the demo project and leave the old-school version of the checkout controller here
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/paypalCheckout.htm", method = {RequestMethod.GET})
    public String paypalCheckout(@ModelAttribute CheckoutForm checkoutForm,
                           BindingResult errors,
                           ModelMap model,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        try {
            final Order order = retrieveCartOrder(request, model);
            Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();

            TotalledPaymentInfoImpl paymentInfo = new TotalledPaymentInfoImpl();
            paymentInfo.setOrder(order);
            paymentInfo.setType(PaymentInfoType.PAYPAL);
            paymentInfo.getAdditionalFields().put("PAYPALMETHODTYPE", "CHECKOUT");
            paymentInfo.setReferenceNumber(String.valueOf(order.getId()));
            paymentInfo.setAmount(order.getTotal());
            paymentInfo.setSubTotal(order.getSubTotal());
            paymentInfo.setTotalShipping(order.getTotalShipping());
            paymentInfo.setTotalTax(order.getTotalTax());
            paymentInfo.setShippingDiscount(order.getFulfillmentGroupAdjustmentsValue());
            for (OrderItem orderItem : order.getOrderItems()) {
                AmountItem amountItem = new AmountItemImpl();
                if (DiscreteOrderItem.class.isAssignableFrom(orderItem.getClass())) {
                    amountItem.setDescription(((DiscreteOrderItem)orderItem).getSku().getDescription());
                    amountItem.setSystemId(String.valueOf(((DiscreteOrderItem) orderItem).getSku().getId()));
                }
                amountItem.setShortDescription(orderItem.getName());
                amountItem.setPaymentInfo(paymentInfo);
                amountItem.setQuantity((long) orderItem.getQuantity());
                amountItem.setUnitPrice(orderItem.getPrice().getAmount());
                paymentInfo.getAmountItems().add(amountItem);
            }
            payments.put(paymentInfo, paymentInfo.createEmptyReferenced());
            List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();
            paymentInfos.add(paymentInfo);
            order.setPaymentInfos(paymentInfos);
            
            CompositePaymentResponse compositePaymentResponse = compositePaymentService.executePayment(order, payments);
            PaymentResponseItem responseItem = compositePaymentResponse.getPaymentResponse().getResponseItems().get(paymentInfo);

            if(responseItem.getTransactionSuccess()) {
                order.setOrderNumber(new SimpleDateFormat("yyyyMMddHHmmssS").format(SystemTime.asDate()));
                model.addAttribute("order", retrieveCartOrder(request, model));
                return "redirect:" + responseItem.getAdditionalFields().get("REDIRECTURL");
            } else {
                //TODO this needs some work
                //return resp.substring(resp.indexOf("ERRORCODE"));
                return "";
            }
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    //TODO this code is no longer needed with the paypal module
    private String setHttpClient(Order order, String method, List<NameValuePair> nvps) throws IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod("https://api-3t.sandbox.paypal.com/nvp");
        if(method.equals("checkout")) {
            setNvpsForCheckout(nvps, order);
        } else if(method.equals("details")) {
            setNvpsForDetails(nvps);
        } else {
            setNvpsForProcess(nvps, order);
        }
        postMethod.setRequestBody(nvps.toArray(new NameValuePair[]{}));
        httpClient.executeMethod(postMethod);
        return postMethod.getResponseBodyAsString();
    }

    //TODO this code is no longer needed with the paypal module
    private String getResponseValue(String resp, String valueName) {
        int tokenBegin = resp.indexOf(valueName) + valueName.length() + 1;
        int tokenEnd = resp.indexOf('&', tokenBegin);
        return resp.substring(tokenBegin, tokenEnd);
    }

    //TODO this code is no longer needed with the paypal module
    private void setNvpsForCheckout(List<NameValuePair> nvps, Order order) {

        nvps.add(new NameValuePair("USER", "mercha_1328542046_biz_api1.gmail.com"));
        nvps.add(new NameValuePair("PWD", "1328542070"));
        nvps.add(new NameValuePair("SIGNATURE", "A7LScFN9bhUhM6p4ti.OLcaBFzmBAlwph13OCo.JwdJt3Kfi9fw1J3v5"));
        nvps.add(new NameValuePair("VERSION", "78.0"));
        nvps.add(new NameValuePair("PAYMENTREQUEST_0_PAYMENTACTION", "Sale"));

        //setCallbackNvps(nvps, order);
        setCostNvps(nvps, order);

        nvps.add(new NameValuePair("RETURNURL", "http://localhost:8080/broadleafdemo/checkout/paypalDetails.htm"));
        nvps.add(new NameValuePair("CANCELURL", "http://localhost:8080/broadleafdemo/basket/viewCart.htm"));
        nvps.add(new NameValuePair("HDRIMG", "http://localhost:8080/broadleafdemo/images/havalettaLogo.png"));
        nvps.add(new NameValuePair("HDRBORDERCOLOR", "333333"));
        nvps.add(new NameValuePair("HDRBACKCOLOR", "669933"));
        nvps.add(new NameValuePair("PAYFLOWCOLOR", "B58253"));
        nvps.add(new NameValuePair("METHOD", "SetExpressCheckout"));
    }

    //TODO this code is no longer needed with the paypal module
    /*private void setCallbackNvps(List<NameValuePair> nvps, Order order) {
        nvps.add(new NameValuePair("CALLBACK", "http://localhost:8080/broadleafdemo/basket/viewCart.htm"));
        nvps.add(new NameValuePair("CALLBACKTIMEOUT", "4"));
        nvps.add(new NameValuePair("L_SHIPPINGOPTIONISDEFAULT0", "true"));
        nvps.add(new NameValuePair("L_SHIPPINGOPTIONNAME0", "Ground"));
        nvps.add(new NameValuePair("L_SHIPPINGOPTIONAMOUNT0", "5.00"));
        nvps.add(new NameValuePair("L_SHIPPINGOPTIONISDEFAULT1", "false"));
        nvps.add(new NameValuePair("L_SHIPPINGOPTIONNAME1", "Air"));
        nvps.add(new NameValuePair("L_SHIPPINGOPTIONAMOUNT1", "8.00"));
        nvps.add(new NameValuePair("MAXAMT", "" + (order.getTotal().doubleValue() + 10)));
    }*/

    //TODO this code is no longer needed with the paypal module
    private void setCostNvps(List<NameValuePair> nvps, Order order) {
        List<OrderItem> items = order.getOrderItems();
        for(int i = 0; i < items.size(); i++) {
            OrderItem thisItem = items.get(i);
            nvps.add(new NameValuePair("L_PAYMENTREQUEST_0_NAME" + i, ((DiscreteOrderItem) thisItem).getProduct().getManufacturer() + " " + thisItem.getName()));

            nvps.add(new NameValuePair("L_PAYMENTREQUEST_0_NUMBER" + i, ((DiscreteOrderItem) thisItem).getSku().getId().toString()));
            nvps.add(new NameValuePair("L_PAYMENTREQUEST_0_DESC" + i, ((DiscreteOrderItem) thisItem).getSku().getDescription()));
            nvps.add(new NameValuePair("L_PAYMENTREQUEST_0_AMT" + i, thisItem.getPrice().toString()));
            nvps.add(new NameValuePair("L_PAYMENTREQUEST_0_QTY" + i, "" + thisItem.getQuantity()));

        }
        nvps.add(new NameValuePair("PAYMENTREQUEST_0_ITEMAMT", order.getSubTotal().toString()));
        nvps.add(new NameValuePair("PAYMENTREQUEST_0_TAXAMT", order.getTotalTax().toString()));
        nvps.add(new NameValuePair("PAYMENTREQUEST_0_SHIPPINGAMT", order.getTotalShipping().toString()));
        nvps.add(new NameValuePair("PAYMENTREQUEST_0_SHIPDISCAMT", "-" + order.getFulfillmentGroupAdjustmentsValue().add(order.getOrderAdjustmentsValue()).toString()));
        nvps.add(new NameValuePair("PAYMENTREQUEST_0_AMT", order.getTotal().toString()));
    }

    //TODO migrate to the paypal module
    @RequestMapping(value="/paypalDetails.htm", method = {RequestMethod.GET})
    public String paypalDetails(ModelMap model,
                                @RequestParam String token,
                                @RequestParam("PayerID") String payerID,
                                CheckoutForm checkoutForm,
                                HttpServletRequest request) {

        try {

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Order order = retrieveCartOrder(request, model);
            nvps.add(new NameValuePair("TOKEN", token));
            String resp = setHttpClient(order, "details", nvps);


            if(resp.contains("ACK=Success") || resp.contains("ACK=SuccessWithWarning")) {

                String firstName = getResponseValue(resp, "FIRSTNAME");
                String lastName = getResponseValue(resp, "LASTNAME");
                String email = getResponseValue(resp, "EMAIL");
                fillCheckoutForm(checkoutForm, resp);
                checkoutForm.getShippingAddress().setFirstName(firstName);
                checkoutForm.getShippingAddress().setLastName(lastName);

                order.getCustomer().setFirstName(firstName);
                order.getCustomer().setLastName(lastName);
                order.getCustomer().setEmailAddress(email.replaceAll("%40", "@").replaceAll("%2e", "."));

                List<FulfillmentGroup> groups = order.getFulfillmentGroups();
                if(groups.size() < 1){
                    return "redirect:/basket/currentCart.htm";
                }
                FulfillmentGroup group = groups.get(0);
                group.setOrder(order);
                group.setAddress(checkoutForm.getShippingAddress());
                group.setShippingPrice(order.getTotalShipping());

                model.addAttribute("order", retrieveCartOrder(request, model));
                model.addAttribute("token", token);
                model.addAttribute("payerID", payerID);
                return "checkout/checkoutReview";//paypalProcess(model, token, payerID, checkoutForm, request);
            } else {
                return resp.substring(resp.indexOf("ERRORCODE"));
            }
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    //TODO migrate to the paypal module
    private void setNvpsForDetails(List<NameValuePair> nvps) {

        nvps.add(new NameValuePair("USER", "mercha_1328542046_biz_api1.gmail.com"));
        nvps.add(new NameValuePair("PWD", "1328542070"));
        nvps.add(new NameValuePair("SIGNATURE", "A7LScFN9bhUhM6p4ti.OLcaBFzmBAlwph13OCo.JwdJt3Kfi9fw1J3v5"));
        nvps.add(new NameValuePair("VERSION", "78.0"));

        nvps.add(new NameValuePair("METHOD", "GetExpressCheckoutDetails"));
    }

    //TODO migrate to the paypal module
    private void fillCheckoutForm(CheckoutForm checkoutForm, String resp) {
        String shipToStreet = getResponseValue(resp, "PAYMENTREQUEST_0_SHIPTOSTREET").replaceAll("%20", " ");
        String shipToCity = getResponseValue(resp, "PAYMENTREQUEST_0_SHIPTOCITY");
        String shipToState = getResponseValue(resp, "PAYMENTREQUEST_0_SHIPTOSTATE");
        String shipToCountryCode = getResponseValue(resp, "PAYMENTREQUEST_0_SHIPTOCOUNTRYCODE");
        String shipToZip = getResponseValue(resp, "PAYMENTREQUEST_0_SHIPTOZIP");

        checkoutForm.getShippingAddress().setCountry(countryService.findCountryByAbbreviation(shipToCountryCode));
        checkoutForm.getShippingAddress().setState(stateService.findStateByAbbreviation(shipToState));
        checkoutForm.getShippingAddress().setAddressLine1(shipToStreet);
        checkoutForm.getShippingAddress().setCity(shipToCity);
        checkoutForm.getShippingAddress().setPostalCode(shipToZip);
    }

    //TODO migrate to the paypal module
    @RequestMapping(value="/paypalProcess.htm", method = {RequestMethod.POST})
    public String paypalProcess(ModelMap model,
                                @RequestParam String token,
                                @RequestParam("PayerID") String payerID,
                                CheckoutForm checkoutForm,
                                HttpServletRequest request) {

        try {
            List <NameValuePair> nvps = new ArrayList<NameValuePair>();
            Order order = retrieveCartOrder(request, model);
            nvps.add(new NameValuePair("PAYERID", payerID));
            nvps.add(new NameValuePair("TOKEN", token));

            String resp = setHttpClient(order, "process", nvps);

            if(resp.contains("ACK=Success") || resp.contains("ACK=SuccessWithWarning")) {

                Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();
                paypalPayment(checkoutForm, order, payments);

                try {
                    checkoutService.performCheckout(order, payments);
                } catch (CheckoutException e) {
                    LOG.error("Cannot perform checkout", e);
                }

                //Fill out a few customer values for anonymous customers
                Customer customer = order.getCustomer();
                if (StringUtils.isEmpty(customer.getFirstName())) {
                    customer.setFirstName(checkoutForm.getBillingAddress().getFirstName());
                }
                if (StringUtils.isEmpty(customer.getLastName())) {
                    customer.setLastName(checkoutForm.getBillingAddress().getLastName());
                }
                if (StringUtils.isEmpty(customer.getEmailAddress())) {
                    customer.setEmailAddress(order.getEmailAddress());
                }
                customerService.saveCustomer(customer, false);
                return receiptView != null ? "redirect:" + receiptView : "redirect:/orders/viewOrderConfirmation.htm?orderNumber=" + order.getOrderNumber();

            } else  {
                return resp.substring(resp.indexOf("ERRORCODE"));
            }
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    //TODO see how this is done in the checkout method
    private void paypalPayment(CheckoutForm checkoutForm, Order order, Map<PaymentInfo, Referenced> payments) {
        PaymentInfo paymentInfo = paymentInfoService.create();
        paymentInfo.setAddress(checkoutForm.getShippingAddress());
        paymentInfo.setOrder(order);
        paymentInfo.setType(PaymentInfoType.PAYPAL);

        paymentInfo.setAmount(order.getTotal());

        List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();
        paymentInfos.add(paymentInfo);
        order.setPaymentInfos(paymentInfos);

        order.setStatus(OrderStatus.SUBMITTED);
        order.setSubmitDate(Calendar.getInstance().getTime());
    }

    //TODO migrate to the paypal module
    private void setNvpsForProcess(List<NameValuePair> nvps, Order order) {

        nvps.add(new NameValuePair("USER", "mercha_1328542046_biz_api1.gmail.com"));
        nvps.add(new NameValuePair("PWD", "1328542070"));
        nvps.add(new NameValuePair("SIGNATURE", "A7LScFN9bhUhM6p4ti.OLcaBFzmBAlwph13OCo.JwdJt3Kfi9fw1J3v5"));
        nvps.add(new NameValuePair("VERSION", "78.0"));
        nvps.add(new NameValuePair("PAYMENTREQUEST_0_PAYMENTACTION", "Sale"));

        nvps.add(new NameValuePair("PAYMENTREQUEST_0_AMT", order.getTotal().toString()));
        nvps.add(new NameValuePair("METHOD", "DoExpressCheckoutPayment"));
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