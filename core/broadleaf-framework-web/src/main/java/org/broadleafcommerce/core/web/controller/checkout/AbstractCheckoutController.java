/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.controller.checkout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCheckoutService;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.FulfillmentOptionService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.web.checkout.validator.BillingInfoFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.CheckoutPaymentInfoFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.GiftCardInfoFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.MultishipAddAddressFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.OrderInfoFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.ShippingInfoFormValidator;
import org.broadleafcommerce.core.web.order.service.CartStateService;
import org.broadleafcommerce.core.web.service.InitBinderService;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CountrySubdivisionService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.core.service.PhoneService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    protected static String ACTIVE_STAGE = "activeStage";

    protected static String cartPageRedirect = "redirect:/cart";
    protected static String checkoutView = "checkout/checkout";
    protected static String checkoutStagesPartial = "checkout/partials/checkoutStages";
    protected static String checkoutPageRedirect = "redirect:/checkout";
    protected static String baseConfirmationView = "ajaxredirect:/confirmation";

    /* Optional Service */
    @Autowired(required=false)
    @Qualifier("blPaymentGatewayCheckoutService")
    protected PaymentGatewayCheckoutService paymentGatewayCheckoutService;

    /* Services */
    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService dtoTranslationService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blFulfillmentOptionService")
    protected FulfillmentOptionService fulfillmentOptionService;

    @Resource(name = "blCheckoutService")
    protected CheckoutService checkoutService;
    
    @Resource(name = "blCustomerService")
    protected CustomerService customerService;

    @Resource(name = "blCustomerPaymentService")
    protected CustomerPaymentService customerPaymentService;

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Resource(name = "blCountrySubdivisionService")
    protected CountrySubdivisionService countrySubdivisionService;

    @Resource(name = "blISOService")
    protected ISOService isoService;

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blAddressService")
    protected AddressService addressService;

    @Resource(name = "blPhoneService")
    protected PhoneService phoneService;

    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    /* Validators */
    @Resource(name = "blShippingInfoFormValidator")
    protected ShippingInfoFormValidator shippingInfoFormValidator;

    @Resource(name = "blBillingInfoFormValidator")
    protected BillingInfoFormValidator billingInfoFormValidator;

    @Resource(name = "blCheckoutPaymentInfoFormValidator")
    protected CheckoutPaymentInfoFormValidator paymentInfoFormValidator;

    @Resource(name = "blGiftCardInfoFormValidator")
    protected GiftCardInfoFormValidator giftCardInfoFormValidator;

    @Resource(name = "blMultishipAddAddressFormValidator")
    protected MultishipAddAddressFormValidator multishipAddAddressFormValidator;

    @Resource(name = "blOrderInfoFormValidator")
    protected OrderInfoFormValidator orderInfoFormValidator;

    @Resource(name = "blCartStateService")
    protected CartStateService cartStateService;

    @Resource(name = "blInitBinderService")
    protected InitBinderService initBinderService;

    /* Extension Managers */
    @Resource(name = "blCheckoutControllerExtensionManager")
    protected BroadleafCheckoutControllerExtensionManager checkoutControllerExtensionManager;

    /* Views and Redirects */
    public String getCartPageRedirect() {
        return cartPageRedirect;
    }

    public String getCheckoutView() {
        return checkoutView;
    }

    public String getCheckoutStagesPartial() {
        return checkoutStagesPartial;
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

    protected void populateModelWithReferenceData(HttpServletRequest request, Model model) {
        //Add module specific model variables
        checkoutControllerExtensionManager.getProxy().addAdditionalModelVariables(model);
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
        initBinderService.configAddressInitBinder(binder);
    }

}
