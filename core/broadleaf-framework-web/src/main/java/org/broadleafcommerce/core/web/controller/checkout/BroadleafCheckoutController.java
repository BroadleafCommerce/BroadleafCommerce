package org.broadleafcommerce.core.web.controller.checkout;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.web.checkout.model.OrderMultishipOptionForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingAddressForm;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.beans.PropertyEditorSupport;

/**
 * In charge of performing the various checkout operations
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BroadleafCheckoutController extends AbstractCheckoutController {
	
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
	
	/**
	 * Renders the default checkout page.
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return the return path
	 */
	public String checkout(HttpServletRequest request, HttpServletResponse response, Model model) {
    	model.addAttribute("states", stateService.findStates());
        model.addAttribute("countries", countryService.findCountries());
		return "checkout";
	}
	
	public String attachShippingAddress(HttpServletRequest request, HttpServletResponse response, Model model,
			ShippingAddressForm addressForm) {
	    return null;
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
		model.addAttribute("orderMultishipOptions", orderMultishipOptionService.generateMultishipOptions(cart));
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
	 */
    public String saveMultiship(HttpServletRequest request, HttpServletResponse response, Model model,
    		OrderMultishipOptionForm orderMultishipOptionForm) {
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
        return ajaxRender("multiship-add-address", request, model);
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
    		 ShippingAddressForm addressForm) {
    	Address address = addressService.saveAddress(addressForm.getAddress());
    	
    	CustomerAddress customerAddress = customerAddressService.create();
    	customerAddress.setAddressName(addressForm.getAddressName());
    	customerAddress.setAddress(address);
    	customerAddress.setCustomer(CustomerState.getCustomer());
    	customerAddressService.saveCustomerAddress(customerAddress);
    	
    	return showMultiship(request, response, model);
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
