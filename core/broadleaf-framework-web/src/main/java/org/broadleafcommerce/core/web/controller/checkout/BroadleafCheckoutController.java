package org.broadleafcommerce.core.web.controller.checkout;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderMultishipOptionForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * In charge of performing the various checkout operations
 * 
 * @author Andre Azzolini (apazzolini)
 * @author Elbert Bautista (elbertbautista)
 */
public class BroadleafCheckoutController extends AbstractCheckoutController {

    /**
     * Renders the default checkout page.
     *
     * @param request
     * @param response
     * @param model
     * @return the return path
     */
    public String checkout(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Order cart = CartState.getCart();
		model.addAttribute("orderMultishipOptions", orderMultishipOptionService.findOrderMultishipOptions(cart.getId()));
        model.addAttribute("fulfillmentOptions", fulfillmentOptionService.readAllFulfillmentOptions());
        model.addAttribute("validShipping", hasValidShippingAddresses(cart));
    	model.addAttribute("states", stateService.findStates());
        model.addAttribute("countries", countryService.findCountries());
        return ajaxRender("checkout", request, model);
    }

    /**
     * Processes the request to save a single shipping address
     *
     * Note that the default Broadleaf implementation creates an order
     * with a single fulfillment group. In the case of shipping to mutiple addresses,
     * the multiship methods should be used.
     *
     * @param request
     * @param response
     * @param model
     * @param shippingForm
     * @return the return path
     */
	public String saveSingleShip(HttpServletRequest request, HttpServletResponse response, Model model,
			ShippingInfoForm shippingForm, BindingResult result) throws PricingException {
        Order cart = CartState.getCart();

        shippingInfoFormValidator.validate(shippingForm, result);
        if (result.hasErrors()) {
            checkout(request, response, model);
            return ajaxRender("checkout", request, model);
        }

        FulfillmentGroup fulfillmentGroup = cart.getFulfillmentGroups().get(0);
        fulfillmentGroup.setAddress(shippingForm.getAddress());
        FulfillmentOption fulfillmentOption = fulfillmentOptionService.readFulfillmentOptionById(shippingForm.getFulfillmentOptionId());
        fulfillmentGroup.setFulfillmentOption(fulfillmentOption);

        cart = orderService.save(cart, true);

        CartState.setCart(cart);

        return isAjaxRequest(request) ? "ajax/checkout" : "redirect:/checkout";
    }

    public String savePaymentForm(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
        return isAjaxRequest(request) ? "ajax/checkout" : "redirect:/checkout";
    }

	/**
	 * Renders the multiship page. This page is used by the user when shipping items
	 * to different locations (or with different FulfillmentOptions) is desired.
	 * 
	 * Note that the default Broadleaf implementation will require the user to input
	 * an Address and FulfillmentOption for each quantity of each DiscreteOrderItem.
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return the return path
	 */
	public String showMultiship(HttpServletRequest request, HttpServletResponse response, Model model) {
		Customer customer = CustomerState.getCustomer();
		Order cart = CartState.getCart();
		model.addAttribute("orderMultishipOptions", orderMultishipOptionService.getOrGenerateOrderMultishipOptions(cart));
    	model.addAttribute("customerAddresses", customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId()));
    	model.addAttribute("fulfillmentOptions", fulfillmentOptionService.readAllFulfillmentOptions());
		return ajaxRender("multiship", request, model);
	}
	
	/**
	 * Processes the given options for multiship
	 * 
	 * @see #showMultiship(HttpServletRequest, HttpServletResponse, Model)
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param orderMultishipOptionForm
	 * @return a redirect to the checkout page
	 * @throws PricingException 
	 */
    public String saveMultiship(HttpServletRequest request, HttpServletResponse response, Model model,
    		OrderMultishipOptionForm orderMultishipOptionForm) throws PricingException {
    	Order cart = CartState.getCart();
    	orderMultishipOptionService.saveOrderMultishipOptions(cart, orderMultishipOptionForm.getOptions());
    	cart = fulfillmentGroupService.splitIntoMultishipGroups(cart, true);
    	
    	if (isAjaxRequest(request)) {
    		return buildAjaxRedirect(request, "/checkout", model);
    	} else {
    		return "redirect:/checkout";
    	}
    }

    /**
     * Renders the add address form during the multiship process
     * 
     * @param request
     * @param response
     * @param model
     * @return the return path
     */
    public String showMultishipAddAddress(HttpServletRequest request, HttpServletResponse response, Model model) {
    	model.addAttribute("states", stateService.findStates());
        model.addAttribute("countries", countryService.findCountries());
        return ajaxRender("multishipAddAddress", request, model);
    }
    
    /**
     * Processes the requested add address from the multiship process.
     * This method will create a CustomerAddress based on the requested Address
     * and associate it with the current Customer in session.
     * 
     * @param request
     * @param response
     * @param model
     * @param addressForm
     * @return the return path to the multiship page
     */
    public String saveMultishipAddAddress(HttpServletRequest request, HttpServletResponse response, Model model,
    		 ShippingInfoForm addressForm) {
    	Address address = addressService.saveAddress(addressForm.getAddress());
    	
    	CustomerAddress customerAddress = customerAddressService.create();
    	customerAddress.setAddressName(addressForm.getAddressName());
    	customerAddress.setAddress(address);
    	customerAddress.setCustomer(CustomerState.getCustomer());
    	customerAddressService.saveCustomerAddress(customerAddress);
    	
    	return showMultiship(request, response, model);
    }

    /**
     * Processes the request to complete checkout
     *
     * This method allows a checkout and assumes that a credit card payment info
     * will be either sent to a third party gateway or saved in a secure schema.
     *
     * @param request
     * @param response
     * @param model
     * @param billingForm
     * @return the return path
     */
    public String completeSecureCreditCardCheckout(HttpServletRequest request, HttpServletResponse response, Model model,
            BillingInfoForm billingForm, BindingResult result) throws CheckoutException {

        Order cart = CartState.getCart();
        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();

        billingInfoFormValidator.validate(billingForm, result);
        if (result.hasErrors()) {
            checkout(request, response, model);
            return ajaxRender("checkout", request, model);
        }

        PaymentInfo ccInfo = creditCardPaymentInfoFactory.constructPaymentInfo(cart);
        ccInfo.setAddress(billingForm.getAddress());
        cart.getPaymentInfos().add(ccInfo);

        CreditCardPaymentInfo ccReference = (CreditCardPaymentInfo) securePaymentInfoService.create(PaymentInfoType.CREDIT_CARD);
        ccReference.setNameOnCard(billingForm.getCreditCardName());
        ccReference.setReferenceNumber(ccInfo.getReferenceNumber());
        ccReference.setPan(billingForm.getCreditCardNumber());
        ccReference.setCvvCode(billingForm.getCreditCardCvvCode());
        ccReference.setExpirationMonth(Integer.parseInt(billingForm.getCreditCardExpMonth()));
        ccReference.setExpirationYear(Integer.parseInt(billingForm.getCreditCardExpYear()));

        payments.put(ccInfo, ccReference);

        cart.setOrderNumber(new SimpleDateFormat("yyyyMMddHHmmssS").format(SystemTime.asDate()));
        cart.setStatus(OrderStatus.SUBMITTED);
        cart.setSubmitDate(Calendar.getInstance().getTime());

        CheckoutResponse checkoutResponse = checkoutService.performCheckout(cart, payments);
        if (!checkoutResponse.getPaymentResponse().getResponseItems().get(ccInfo).getTransactionSuccess()){
            checkout(request, response, model);
            model.addAttribute("paymentException", true);
            return ajaxRender("checkout", request, model);
        }

        if (isAjaxRequest(request)) {
            return buildAjaxRedirect(request, "/" + defaultOrderConfirmationViewName + "/" + cart.getOrderNumber(), model);
        } else {
            return "redirect:/" + defaultOrderConfirmationViewName + "/" + cart.getOrderNumber();
        }
    }

    /**
     * A helper method used to determine the validity of the fulfillment groups
     *
     * @param cart
     * @return boolean indicating whether or not the fulfillment groups on the cart have addresses.
     */
    public boolean hasValidShippingAddresses(Order cart) {
    	if (cart.getFulfillmentGroups() == null) {
    		return false;
    	}
    	
        for (FulfillmentGroup fulfillmentGroup : cart.getFulfillmentGroups()) {
            if (fulfillmentGroup.getAddress() == null) {
                return false;
            }
        }
        return true;
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
            	State state = stateService.findStateByAbbreviation(text);
                setValue(state);
            }
        });

        binder.registerCustomEditor(Country.class, "address.country", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Country country = countryService.findCountryByAbbreviation(text);
                setValue(country);
            }
        });

    }
}
