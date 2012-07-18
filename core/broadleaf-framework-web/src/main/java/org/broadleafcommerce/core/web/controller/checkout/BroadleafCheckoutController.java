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
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.beans.PropertyEditorSupport;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * In charge of performing the various checkout operations
 * 
 * @author Andre Azzolini (apazzolini)
 * @author Elbert Bautista (elbertbautista)
 */
public class BroadleafCheckoutController extends AbstractCheckoutController {
	
	protected String checkoutView = "checkout/checkout";
	protected String checkoutPageRedirect = "redirect:/checkout";
	protected String multishipView = "ajax:checkout/multiship";
    protected String multishipAddAddressView = "ajax:checkout/multishipAddAddressForm";
    protected String multishipAddAddressSuccessView = "redirect:/checkout/multiship";
	protected String multishipSuccessView = "redirect:/checkout";
	protected String baseConfirmationView = "ajaxredirect:/confirmation";

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
		model.addAttribute("orderMultishipOptions", orderMultishipOptionService.getOrGenerateOrderMultishipOptions(cart));
        model.addAttribute("fulfillmentOptions", fulfillmentOptionService.readAllFulfillmentOptions());
        model.addAttribute("validShipping", hasValidShippingAddresses(cart));
    	model.addAttribute("states", stateService.findStates());
        model.addAttribute("countries", countryService.findCountries());
        model.addAttribute("expirationMonths", populateExpirationMonths());
        model.addAttribute("expirationYears", populateExpirationYears());
        return getCheckoutView();
    }
    
    /**
     * Converts the order to singleship by collapsing all of the fulfillment groups into the 
     * default one
     * 
     * @param request
     * @param response
     * @param model
     * @return a redirect to /checkout
     * @throws PricingException 
     */
	public String convertToSingleship(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
    	Order cart = CartState.getCart();
		fulfillmentGroupService.collapseToOneFulfillmentGroup(cart, true);
		return getCheckoutPageRedirect();
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
            return checkout(request, response, model);
        }

        FulfillmentGroup fulfillmentGroup = cart.getFulfillmentGroups().get(0);
        fulfillmentGroup.setAddress(shippingForm.getAddress());
        FulfillmentOption fulfillmentOption = fulfillmentOptionService.readFulfillmentOptionById(shippingForm.getFulfillmentOptionId());
        fulfillmentGroup.setFulfillmentOption(fulfillmentOption);

        cart = orderService.save(cart, true);

        CartState.setCart(cart);

        return isAjaxRequest(request) ? getCheckoutView() : getCheckoutPageRedirect();
    }

    public String savePaymentForm(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
    	//TODO: Implement
        return isAjaxRequest(request) ? getCheckoutView() : getCheckoutPageRedirect();
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
        return getMultishipView();
	}
	
	/**
	 * Processes the given options for multiship. Validates that all options are
	 * selected before performing any actions.
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
    		OrderMultishipOptionForm orderMultishipOptionForm, BindingResult result) throws PricingException {
    	Order cart = CartState.getCart();
    	orderMultishipOptionService.saveOrderMultishipOptions(cart, orderMultishipOptionForm.getOptions());
    	cart = fulfillmentGroupService.matchFulfillmentGroupsToMultishipOptions(cart, true);
    	return getMultishipSuccessView();
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
        return getMultishipAddAddressView();
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
    		 ShippingInfoForm addressForm, BindingResult result) {
        multishipAddAddressFormValidator.validate(addressForm, result);
        if (result.hasErrors()) {
            return showMultishipAddAddress(request, response, model);
        }
    	
    	Address address = addressService.saveAddress(addressForm.getAddress());
    	
    	CustomerAddress customerAddress = customerAddressService.create();
    	customerAddress.setAddressName(addressForm.getAddressName());
    	customerAddress.setAddress(address);
    	customerAddress.setCustomer(CustomerState.getCustomer());
    	customerAddressService.saveCustomerAddress(customerAddress);
    	
    	return getMultishipAddAddressSuccessView();
    }

    /**
     * Processes the request to complete checkout using a Credit Card
     *
     * This method assumes that a credit card payment info
     * will be either sent to a third party gateway or saved in a secure schema.
     * If the transaction is successful, the order will be assigned an order number,
     * its status change to SUBMITTED, and given a submit date. The method then
     * returns the default confirmation path "/confirmation/{orderNumber}"
     *
     * If the transaction is unsuccessful, (e.g. the gateway declines payment)
     * processFailedOrderCheckout() is called and reverses the state of the order.
     *
     * Note: this method removes any existing payment infos of type CREDIT_CARD
     * and re-creates it with the information from the BillingInfoForm
     *
     * @param request
     * @param response
     * @param model
     * @param billingForm
     * @return the return path
     */
    public String completeSecureCreditCardCheckout(HttpServletRequest request, HttpServletResponse response, Model model,
            BillingInfoForm billingForm, BindingResult result) throws CheckoutException, PricingException {

        Order cart = CartState.getCart();
        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();

        orderService.removePaymentsFromOrder(cart, PaymentInfoType.CREDIT_CARD);

        billingInfoFormValidator.validate(billingForm, result);
        if (result.hasErrors()) {
            checkout(request, response, model);
            return getCheckoutView();
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
            processFailedOrderCheckout(cart);
            checkout(request, response, model);
            result.rejectValue("creditCardNumber", "payment.exception", null, null);
            return getCheckoutView();
        }

        return getConfirmationView(cart.getOrderNumber());
    }

    /**
     * This method dictates what actions need to be taken if there is a failure during the checkout process.
     * Normally called when either the transaction success is false (e.g. payment declined by gateway)
     * or an unknown error occurs during the Checkout Workflow (e.g. a CheckoutException is thrown)
     *
     * The default behavior is to reverse the status of the order and set the submit date and order number to null.
     *
     * @param order
     */
    public void processFailedOrderCheckout(Order order) throws PricingException {
        order.setOrderNumber(null);
        order.setStatus(OrderStatus.IN_PROCESS);
        order.setSubmitDate(null);
        orderService.save(order, false);
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
            if (fulfillmentGroup.getAddress() == null || fulfillmentGroup.getFulfillmentOption() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * A helper method used to construct a list of Credit Card Expiration Months
     * Useful for expiration dropdown menus.
     *
     * @return List containing expiration months of the form "01 - January"
     */
    public List<String> populateExpirationMonths() {
        List<String> expirationMonths = new ArrayList<String>();
        NumberFormat formatter = new DecimalFormat("00");
        String[] months = new DateFormatSymbols().getMonths();
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
    public List<String> populateExpirationYears() {
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

	public String getCheckoutView() {
		return checkoutView;
	}

	public void setCheckoutView(String checkoutView) {
		this.checkoutView = checkoutView;
	}

	public String getCheckoutPageRedirect() {
		return checkoutPageRedirect;
	}

	public void setCheckoutPageRedirect(String checkoutPageRedirect) {
		this.checkoutPageRedirect = checkoutPageRedirect;
	}
	
	public String getMultishipView() {
		return multishipView;
	}

	public void setMultishipView(String multishipView) {
		this.multishipView = multishipView;
	}
	
	public String getMultishipAddAddressView() {
		return multishipAddAddressView;
	}

	public void setMultishipAddAddressView(String multishipAddAddressView) {
		this.multishipAddAddressView = multishipAddAddressView;
	}

	public String getMultishipSuccessView() {
		return multishipSuccessView;
	}
	
	public String getMultishipAddAddressSuccessView() {
		return multishipAddAddressSuccessView;
	}

	public void setMultishipAddAddressSuccessView(String multishipAddAddressSuccessView) {
		this.multishipAddAddressSuccessView = multishipAddAddressSuccessView;
	}

	public void setMultishipSuccessView(String multishipSuccessView) {
		this.multishipSuccessView = multishipSuccessView;
	}

	public String getBaseConfirmationView() {
		return baseConfirmationView;
	}

	public void setBaseConfirmationView(String baseConfirmationView) {
		this.baseConfirmationView = baseConfirmationView;
	}
	
	protected String getConfirmationView(String orderNumber) {
		return getBaseConfirmationView() + "/" + orderNumber;
	}

}
