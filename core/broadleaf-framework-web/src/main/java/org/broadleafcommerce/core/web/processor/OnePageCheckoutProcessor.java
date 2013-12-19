/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.processor;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.vendor.service.exception.FulfillmentPriceException;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.FulfillmentOptionService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.pricing.service.FulfillmentPricingService;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.FulfillmentEstimationResponse;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.joda.time.DateTime;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.element.AbstractLocalVariableDefinitionElementProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class OnePageCheckoutProcessor extends AbstractLocalVariableDefinitionElementProcessor {

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blFulfillmentOptionService")
    protected FulfillmentOptionService fulfillmentOptionService;

    @Resource(name = "blFulfillmentPricingService")
    protected FulfillmentPricingService fulfillmentPricingService;

    public OnePageCheckoutProcessor() {
        super("one_page_checkout");
    }

    @Override
    public int getPrecedence() {
        return 100;
    }

    @Override
    protected boolean removeHostElement(Arguments arguments, Element element) {
        return false;
    }

    @Override
    protected Map<String, Object> getNewLocalVariables(Arguments arguments, Element element) {

        //Pre-populate the command objects
        OrderInfoForm orderInfoForm = (OrderInfoForm) StandardExpressionProcessor.processExpression(arguments,
                element.getAttributeValue("orderInfoForm"));
        ShippingInfoForm shippingInfoForm = (ShippingInfoForm) StandardExpressionProcessor.processExpression(arguments,
                element.getAttributeValue("shippingInfoForm"));
        BillingInfoForm billingInfoForm = (BillingInfoForm) StandardExpressionProcessor.processExpression(arguments,
                element.getAttributeValue("billingInfoForm"));

        prepopulateCheckoutForms(CartState.getCart(), orderInfoForm, shippingInfoForm, billingInfoForm);

        Map<String, Object> localVars = new HashMap<String, Object>();

        //Initialize Fulfillment Group Vars
        int numShippableFulfillmentGroups = calculateNumShippableFulfillmentGroups();
        localVars.put("numShippableFulfillmentGroups", numShippableFulfillmentGroups);
        populateFulfillmentOptionsAndEstimationOnModel(localVars);

        //Initialize View States
        populateSectionViewStates(localVars);

        //Helpful lists to populate dropdowns on a checkout page
        localVars.put("states", stateService.findStates());
        localVars.put("countries", countryService.findCountries());
        localVars.put("expirationMonths", populateExpirationMonths());
        localVars.put("expirationYears", populateExpirationYears());

        return localVars;
    }

    /**
     * The Checkout page for Heat Clinic will have the shipping information pre-populated
     * with an address if the fulfillment group has an address and fulfillment option
     * associated with it. It also assumes that if there is only one order payment of type
     * credit card on the order, then the billing address will be pre-populated with that payment.
    */
    protected void prepopulateCheckoutForms(Order cart,
                                            OrderInfoForm orderInfoForm,
                                            ShippingInfoForm shippingForm,
                                            BillingInfoForm billingForm) {

        if (orderInfoForm != null) {
            orderInfoForm.setEmailAddress(cart.getEmailAddress());
        }

        FulfillmentGroup firstShippableFulfillmentGroup = fulfillmentGroupService.getFirstShippableFulfillmentGroup(cart);
        if (firstShippableFulfillmentGroup != null) {
            FulfillmentOption fulfillmentOption = firstShippableFulfillmentGroup.getFulfillmentOption();
            if (fulfillmentOption != null) {
                //if the cart has already has fulfillment information
                shippingForm.setAddress(firstShippableFulfillmentGroup.getAddress());
                shippingForm.setFulfillmentOption(fulfillmentOption);
                shippingForm.setFulfillmentOptionId(fulfillmentOption.getId());
            } else {
                //check for a default address for the customer
                CustomerAddress defaultAddress = customerAddressService.findDefaultCustomerAddress(CustomerState.getCustomer().getId());
                if (defaultAddress != null) {
                    shippingForm.setAddress(defaultAddress.getAddress());
                    shippingForm.setAddressName(defaultAddress.getAddressName());
                }
            }
        }

        if (cart.getPayments() != null) {
            for (OrderPayment payment : cart.getPayments()) {
                if (PaymentType.CREDIT_CARD.equals(payment.getType())) {
                    billingForm.setAddress(payment.getBillingAddress());
                }
            }
        }
    }

    protected int calculateNumShippableFulfillmentGroups() {
        int numShippableFulfillmentGroups = 0;
        List<FulfillmentGroup> fulfillmentGroups = CartState.getCart().getFulfillmentGroups();
        if (fulfillmentGroups != null) {
            for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
                if (fulfillmentGroupService.isShippable(fulfillmentGroup.getType())) {
                    numShippableFulfillmentGroups++;
                }
            }
        }
        return numShippableFulfillmentGroups;
    }

    public enum ViewState {
        SHOW_FORM_VIEW, SHOW_SAVED_VIEW, SHOW_HELP_VIEW
    }

    /**
     * This method is responsible of populating the variables necessary to draw the checkout page.
     * This logic is highly dependent on your layout. If your layout does not follow the same flow
     * as the HeatClinic demo, you will need to override with your own custom layout implementation
     *
     * @param localVars
     */
    protected void populateSectionViewStates(Map<String,Object> localVars) {
        boolean orderInfoPopulated = hasPopulatedOrderInfo(CartState.getCart());
        boolean billingPopulated = hasPopulatedBillingAddress(CartState.getCart());
        boolean shippingPopulated = hasPopulatedShippingAddress(CartState.getCart());

        localVars.put("orderInfoPopulated", orderInfoPopulated);
        localVars.put("billingPopulated", billingPopulated);
        localVars.put("shippingPopulated", shippingPopulated);

        //show all sections including header unless specifically hidden
        // (e.g. hide shipping if no shippable items in order or hide billing section if the order payment doesn't need
        // an address i.e. PayPal Express)
        boolean showOrderInfoSection = true;
        boolean showBillingInfoSection = true;
        boolean showShippingInfoSection = true;
        boolean showPaymentInfoSection = true;
        boolean showAllPaymentMethods = true;

        int numShippableFulfillmentGroups = calculateNumShippableFulfillmentGroups();
        if (numShippableFulfillmentGroups == 0) {
            showShippingInfoSection = false;
        }

        boolean orderContainsThirdPartyPayment = false;
        if (CartState.getCart().getPayments() != null) {
            for (OrderPayment payment : CartState.getCart().getPayments()) {
                if (PaymentType.THIRD_PARTY_ACCOUNT.equals(payment.getType())) {
                    orderContainsThirdPartyPayment = true;
                }
            }
        }

        if (orderContainsThirdPartyPayment) {
            showBillingInfoSection = false;
            showAllPaymentMethods = false;
        }

        localVars.put("showOrderInfoSection", showOrderInfoSection);
        localVars.put("showBillingInfoSection", showBillingInfoSection);
        localVars.put("showShippingInfoSection", showShippingInfoSection);
        localVars.put("showPaymentInfoSection", showPaymentInfoSection);
        localVars.put("showAllPaymentMethods", showAllPaymentMethods);

        //Logic to toggle state between form view, saved view, and help view
        //This is dependent on the layout of your checkout form. Override this if layout is different.

        //Initialize State
        ViewState orderInfoViewState = ViewState.SHOW_FORM_VIEW;
        ViewState billingInfoViewState = ViewState.SHOW_HELP_VIEW;
        ViewState shippingInfoViewState = ViewState.SHOW_HELP_VIEW;
        ViewState paymentInfoViewState = ViewState.SHOW_HELP_VIEW;

        if (orderInfoPopulated && billingPopulated && shippingPopulated) {
            paymentInfoViewState = ViewState.SHOW_FORM_VIEW;
        } else if (orderInfoPopulated && billingPopulated){
            shippingInfoViewState = ViewState.SHOW_FORM_VIEW;
        } else if (orderInfoPopulated) {
            billingInfoViewState = ViewState.SHOW_FORM_VIEW;
        }

        if (orderInfoPopulated) {
            orderInfoViewState = ViewState.SHOW_SAVED_VIEW;
        }

        if (billingPopulated) {
            billingInfoViewState = ViewState.SHOW_SAVED_VIEW;
        }

        if (shippingPopulated) {
            shippingInfoViewState = ViewState.SHOW_SAVED_VIEW;
        }

        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        HttpServletRequest request = blcContext.getRequest();

        String editOrderInfo = request.getParameter("edit-order-info");
        if (BooleanUtils.toBoolean(editOrderInfo)) {
            orderInfoViewState = ViewState.SHOW_FORM_VIEW;
        }

        String editBilling = request.getParameter("edit-billing");
        if (BooleanUtils.toBoolean(editBilling)) {
            billingInfoViewState = ViewState.SHOW_FORM_VIEW;
        }

        String editShipping = request.getParameter("edit-shipping");
        if (BooleanUtils.toBoolean(editShipping)) {
            shippingInfoViewState = ViewState.SHOW_FORM_VIEW;
        }

        localVars.put("orderInfoViewState", orderInfoViewState);
        localVars.put("billingInfoViewState", billingInfoViewState);
        localVars.put("shippingInfoViewState", shippingInfoViewState);
        localVars.put("paymentInfoViewState", paymentInfoViewState);
    }


    /**
     * A helper method to retrieve all fulfillment options for the cart and estimate the cost of applying
     * fulfillment options on the first shippable fulfillment group.
     *
     */
    protected void populateFulfillmentOptionsAndEstimationOnModel(Map<String, Object> localVars) {
        List<FulfillmentOption> fulfillmentOptions = fulfillmentOptionService.readAllFulfillmentOptions();
        Order cart = CartState.getCart();

        if (!(cart instanceof NullOrderImpl) && cart.getFulfillmentGroups().size() > 0
                && hasPopulatedShippingAddress(cart)) {
            Set<FulfillmentOption> options = new HashSet<FulfillmentOption>();
            options.addAll(fulfillmentOptions);
            FulfillmentEstimationResponse estimateResponse = null;
            try {
                estimateResponse = fulfillmentPricingService.estimateCostForFulfillmentGroup(fulfillmentGroupService.getFirstShippableFulfillmentGroup(cart), options);
            } catch (FulfillmentPriceException e) {

            }

            localVars.put( "estimateResponse", estimateResponse);
        }

        localVars.put("fulfillmentOptions", fulfillmentOptions);
    }

    /**
     * A helper method used to determine the validity of order info
     *
     * @param cart
     * @return boolean indicating whether or not the order has valid info
     */
    protected boolean hasPopulatedOrderInfo(Order cart) {
        return StringUtils.isNotBlank(cart.getEmailAddress());
    }

    /**
     * A helper method used to determine the validity of the payments on the Order
     *
     * @param cart
     * @return boolean indicating whether or not the CREDIT_CARD order payment on the order has an address
     */
    protected boolean hasPopulatedBillingAddress(Order cart) {
        if (cart.getPayments() == null) {
            return false;
        }

        for (OrderPayment payment : cart.getPayments()) {
            if (PaymentType.CREDIT_CARD.equals(payment.getType()) &&
                    payment.getBillingAddress() != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * A helper method used to determine the validity of the fulfillment groups
     *
     * @param cart
     * @return boolean indicating whether or not the fulfillment groups on the cart have addresses.
     */
    protected boolean hasPopulatedShippingAddress(Order cart) {
        if (cart.getFulfillmentGroups() == null) {
            return false;
        }
        for (FulfillmentGroup fulfillmentGroup : cart.getFulfillmentGroups()) {
            if (fulfillmentGroupService.isShippable(fulfillmentGroup.getType())) {
                if (fulfillmentGroup.getAddress() == null || fulfillmentGroup.getFulfillmentOption() == null) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * A helper method used to construct a list of Credit Card Expiration Months
     * Useful for expiration dropdown menus.
     * Will use locale to determine language if a locale is available.
     *
     * @return List containing expiration months of the form "01 - January"
     */
    protected List<String> populateExpirationMonths() {
        DateFormatSymbols dateFormatter;
        if(BroadleafRequestContext.hasLocale()){
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getJavaLocale();
            dateFormatter = new DateFormatSymbols(locale);
        } else {
            dateFormatter = new DateFormatSymbols();
        }
        List<String> expirationMonths = new ArrayList<String>();
        NumberFormat formatter = new DecimalFormat("00");
        String[] months = dateFormatter.getMonths();
        for (int i=1; i<months.length; i++) {
            expirationMonths.add(formatter.format(i) + " - " + months[i-1]);
        }
        return expirationMonths;
    }

    /**
     * A helper method used to construct a list of Credit Card Expiration Years
     * Useful for expiration dropdown menus.
     *
     * @return List of the next ten years starting with the current year.
     */
    protected List<String> populateExpirationYears() {
        List<String> expirationYears = new ArrayList<String>();
        DateTime dateTime = new DateTime();
        for (int i=0; i<10; i++){
            expirationYears.add(dateTime.plusYears(i).getYear()+"");
        }
        return expirationYears;
    }

}
