package org.broadleafcommerce.core.web.controller.checkout;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.web.checkout.model.ShippingAddressForm;
import org.broadleafcommerce.profile.core.domain.Address;
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

public class BroadleafCheckoutController extends BroadleafAbstractController {
	
	@Resource(name = "blStateService")
	protected StateService stateService;
	
	@Resource(name = "blCountryService")
	protected CountryService countryService;
	
	@Resource(name = "blCustomerAddressService")
	protected CustomerAddressService customerAddressService;
	
	@Resource(name = "blAddressService")
	protected AddressService addressService;
	
	public String checkout(HttpServletRequest request, HttpServletResponse response, Model model) {
    	model.addAttribute("state", stateService.findStates());
		return "checkout";
	}
	
	public String attachShippingAddress(HttpServletRequest request, HttpServletResponse response, Model model,
			ShippingAddressForm addressForm) {
	    return null;
	}

	public String showMultiship(HttpServletRequest request, HttpServletResponse response, Model model) {
		Customer customer = CustomerState.getCustomer();
    	model.addAttribute("customerAddresses", customerAddressService.readActiveCustomerAddressesByCustomerId(customer.getId()));
		return ajaxRender("multiship", request, model);
	}
	
    public String saveMultiship(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return buildAjaxRedirect(request, "/cart", model);
    }

    public String showMultishipAddAddress(HttpServletRequest request, HttpServletResponse response, Model model) {
    	model.addAttribute("states", stateService.findStates());
    	return ajaxRender("multiship-add-address", request, model);
    }
    
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
    
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(State.class, "address.state", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
            	State state = stateService.findStateByAbbreviation(text);
                setValue(state);
            }
        });
    }
}
