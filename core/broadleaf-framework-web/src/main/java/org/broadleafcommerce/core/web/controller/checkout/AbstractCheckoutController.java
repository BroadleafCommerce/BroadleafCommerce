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
package org.broadleafcommerce.core.web.controller.checkout;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.vendor.service.exception.FulfillmentPriceException;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.FulfillmentOptionService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.pricing.service.FulfillmentPricingService;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.FulfillmentEstimationResponse;
import org.broadleafcommerce.core.web.checkout.validator.BillingInfoFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.MultishipAddAddressFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.OrderInfoFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.ShippingInfoFormValidator;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * An abstract controller that provides convenience methods and resource declarations for its
 * children. Operations that are shared between controllers that deal with checkout belong here.
 *
 * @author Andre Azzolini (apazzolini)
 * @author Elbert Bautista (elbertbautista)
 * @author Joshua Skorton (jskorton)
 */
public abstract class AbstractCheckoutController extends BroadleafAbstractController {

    private static final Log LOG = LogFactory.getLog(AbstractCheckoutController.class);

    protected static String cartPageRedirect = "redirect:/cart";
    protected static String checkoutView = "checkout/checkout";
    protected static String checkoutPageRedirect = "redirect:/checkout";
    protected static String baseConfirmationView = "ajaxredirect:/confirmation";

    /* Services */
    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService dtoTranslationService;

    @Resource(name = "blFulfillmentOptionService")
    protected FulfillmentOptionService fulfillmentOptionService;
    
    @Resource(name = "blFulfillmentPricingService")
    protected FulfillmentPricingService fulfillmentPricingService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blCheckoutService")
    protected CheckoutService checkoutService;
    
    @Resource(name = "blCustomerService")
    protected CustomerService customerService;

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blAddressService")
    protected AddressService addressService;

    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    /* Validators */
    @Resource(name = "blShippingInfoFormValidator")
    protected ShippingInfoFormValidator shippingInfoFormValidator;

    @Resource(name = "blBillingInfoFormValidator")
    protected BillingInfoFormValidator billingInfoFormValidator;

    @Resource(name = "blMultishipAddAddressFormValidator")
    protected MultishipAddAddressFormValidator multishipAddAddressFormValidator;

    @Resource(name = "blOrderInfoFormValidator")
    protected OrderInfoFormValidator orderInfoFormValidator;

    /* Extension Managers */
    @Resource(name = "blOrderPaymentServiceExtensionManager")
    protected OrderPaymentServiceExtensionManager paymentInfoServiceExtensionManager;

    @Resource(name = "blCheckoutControllerExtensionManager")
    protected BroadleafCheckoutControllerExtensionManager checkoutControllerExtensionManager;

    /* Views and Redirects */
    public String getCartPageRedirect() {
        return cartPageRedirect;
    }

    public String getCheckoutView() {
        return checkoutView;
    }

    public String getCheckoutPageRedirect() {
        return checkoutPageRedirect;
    }

    public String getBaseConfirmationView() {
        return baseConfirmationView;
    }

    protected String getConfirmationView(String orderNumber) {
        return getBaseConfirmationView() + "/" + orderNumber;
    }

    /**
     * A helper method used to determine the validity of order info
     *
     * @param cart
     * @return boolean indicating whether or not the order has valid info
     */
    protected boolean hasValidOrderInfo(Order cart) {
        return StringUtils.isNotBlank(cart.getEmailAddress());
    }

    /**
     * A helper method used to determine the validity of the fulfillment groups
     *
     * @param cart
     * @return boolean indicating whether or not the fulfillment groups on the cart have addresses.
     */
    protected boolean hasValidShippingAddresses(Order cart) {
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
     * A helper method to retrieve all fulfillment options for the cart and estimate the cost of applying
     * fulfillment options on the first shippable fulfillment group.
     *
     */
    protected void putFulfillmentOptionsAndEstimationOnModel(Model model) {
        List<FulfillmentOption> fulfillmentOptions = fulfillmentOptionService.readAllFulfillmentOptions();
        Order cart = CartState.getCart();

        if (!(cart instanceof NullOrderImpl) && cart.getFulfillmentGroups().size() > 0 && hasValidShippingAddresses(cart)) {
            Set<FulfillmentOption> options = new HashSet<FulfillmentOption>();
            options.addAll(fulfillmentOptions);
            FulfillmentEstimationResponse estimateResponse = null;
            try {
                estimateResponse = fulfillmentPricingService.estimateCostForFulfillmentGroup(fulfillmentGroupService.getFirstShippableFulfillmentGroup(cart), options);
            } catch (FulfillmentPriceException e) {

            }
            model.addAttribute("estimateResponse", estimateResponse);
        }
        model.addAttribute("fulfillmentOptions", fulfillmentOptions);
    }

    protected void populateModelWithReferenceData(HttpServletRequest request, Model model) {
        String editOrderInfo = request.getParameter("edit-order-info");
        boolean hasValidOrderInfo;
        if (BooleanUtils.toBoolean(editOrderInfo)) {
            hasValidOrderInfo = false;
        } else {
            hasValidOrderInfo = hasValidOrderInfo(CartState.getCart());
        }
        model.addAttribute("validOrderInfo", hasValidOrderInfo);

        boolean hasValidShipping = false;

        String editShipping = request.getParameter("edit-shipping");

        if (BooleanUtils.toBoolean(editShipping)) {
            hasValidShipping = false;
        } else {
            hasValidShipping = hasValidShippingAddresses(CartState.getCart());
        }

        int numShippableFulfillmentGroups = 0;
        List<FulfillmentGroup> fulfillmentGroups = CartState.getCart().getFulfillmentGroups();
        if (fulfillmentGroups != null) {
            for (FulfillmentGroup fulfillmentGroup : fulfillmentGroups) {
                if (fulfillmentGroupService.isShippable(fulfillmentGroup.getType())) {
                    numShippableFulfillmentGroups++;
                }
            }
        }
        if (numShippableFulfillmentGroups == 0) {
            hasValidShipping = true;
        }
        model.addAttribute("numShippableFulfillmentGroups", numShippableFulfillmentGroups);
        model.addAttribute("validShipping", hasValidShipping);

        putFulfillmentOptionsAndEstimationOnModel(model);

        model.addAttribute("states", stateService.findStates());
        model.addAttribute("countries", countryService.findCountries());
        model.addAttribute("expirationMonths", populateExpirationMonths());
        model.addAttribute("expirationYears", populateExpirationYears());

        //Add module specific model variables
        checkoutControllerExtensionManager.getProxy().addAdditionalModelVariables(model);
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

    /**
     * Initializes some custom binding operations for the checkout flow.
     * More specifically, this method will attempt to bind state and country
     * abbreviations to actual State and Country objects when the String
     * representation of the abbreviation is submitted.
     *
     * @param request
     * @param binder
     * @throws Exception
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(State.class, "address.state", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (StringUtils.isNotEmpty(text)) {
                    State state = stateService.findStateByAbbreviation(text);
                    setValue(state);
                } else {
                    setValue(null);
                }
            }
        });

        binder.registerCustomEditor(Country.class, "address.country", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Country country = countryService.findCountryByAbbreviation(text);
                setValue(country);
            }
        });
        binder.registerCustomEditor(Phone.class, "address.phonePrimary", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                if (!StringUtils.isBlank(text)) {
                    Phone phone = new PhoneImpl();
                    phone.setPhoneNumber(text);
                    setValue(phone);
                } else {
                    setValue(null);
                }
            }

        });
    }

}
