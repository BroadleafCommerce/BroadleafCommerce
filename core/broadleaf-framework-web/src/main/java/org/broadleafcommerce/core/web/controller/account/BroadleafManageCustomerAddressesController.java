package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.web.controller.account.validator.CustomerAddressValidator;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.beans.PropertyEditorSupport;
import java.util.List;

public class BroadleafManageCustomerAddressesController extends BroadleafAbstractController {

	@Resource(name = "blCustomerAddressService")
    private CustomerAddressService customerAddressService;
	@Resource(name = "blAddressService")
	private AddressService addressService;
	@Resource(name = "blCountryService")
	private CountryService countryService;
	@Resource(name = "blCustomerAddressValidator")
	private CustomerAddressValidator customerAddressValidator;
	@Resource(name = "blStateService")
	private StateService stateService;
   
    protected String addressUpdatedMessage = "Address successfully updated";
    protected String addressAddedMessage = "Address successfully added";
    protected String addressRemovedMessage = "Address successfully removed";
	
    protected static String customerAddressesView = "account/manageCustomerAddresses";
    protected static String customerAddressesRedirect = "redirect:/account/addresses";
    
    /**
     * Initializes some custom binding operations for the managing an address. 
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
     
    protected List<State> populateStates() {
    	return stateService.findStates();
    }
    
    protected List<Country> populateCountries() {
    	return countryService.findCountries();
    }
    
    protected List<CustomerAddress> populateCustomerAddresses() {
    	return customerAddressService.readActiveCustomerAddressesByCustomerId(CustomerState.getCustomer().getId());
    }
    
    public String viewCustomerAddresses(HttpServletRequest request, Model model) {
    	model.addAttribute("customerAddressForm", new CustomerAddressForm());
    	return getCustomerAddressesView();
    }
    
    public String viewCustomerAddress(HttpServletRequest request, Model model, Long customerAddressId) {
    	CustomerAddress customerAddress = customerAddressService.readCustomerAddressById(customerAddressId);
    	if (customerAddress == null) {
    		throw new IllegalArgumentException("Customer Address not found with the specified customerAddressId");
    	}
    	CustomerAddressForm form = new CustomerAddressForm();
    	form.setAddress(customerAddress.getAddress());
    	form.setAddressName(customerAddress.getAddressName());
    	form.setCustomerAddressId(customerAddress.getId());
    	model.addAttribute("customerAddressForm", form);
    	return getCustomerAddressesView();
    }

    public String addCustomerAddress(HttpServletRequest request, Model model, CustomerAddressForm form, BindingResult result, RedirectAttributes redirectAttributes) throws ServiceException {
    	customerAddressValidator.validate(form, result);
    	if (result.hasErrors()) {
    		return getCustomerAddressesView();
    	}
        Address address = addressService.saveAddress(form.getAddress());
        CustomerAddress customerAddress = customerAddressService.create();
        customerAddress.setAddress(address);
        customerAddress.setAddressName(form.getAddressName());
        customerAddress.setCustomer(CustomerState.getCustomer());
        customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
    	if (form.getAddress().isDefault()) {
    		customerAddressService.makeCustomerAddressDefault(customerAddress.getId(), customerAddress.getCustomer().getId());
    	}
        if (!isAjaxRequest(request)) {
    		List<CustomerAddress> addresses = customerAddressService.readActiveCustomerAddressesByCustomerId(CustomerState.getCustomer().getId());
    		model.addAttribute("addresses", addresses);
    	}
        redirectAttributes.addFlashAttribute("successMessage", getAddressAddedMessage());
        return getCustomerAddressesRedirect();
    }
    
    public String updateCustomerAddress(HttpServletRequest request, Model model, Long customerAddressId, CustomerAddressForm form, BindingResult result, RedirectAttributes redirectAttributes) throws ServiceException {
    	customerAddressValidator.validate(form, result);
    	if (result.hasErrors()) {
    		return getCustomerAddressesView();
    	}
    	CustomerAddress customerAddress = customerAddressService.readCustomerAddressById(customerAddressId);
    	if (customerAddress == null) {
    		throw new IllegalArgumentException("Customer Address not found with the specified customerAddressId");
    	}
    	customerAddress.setAddress(form.getAddress());
    	customerAddress.setAddressName(form.getAddressName());
    	customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
    	if (form.getAddress().isDefault()) {
    		customerAddressService.makeCustomerAddressDefault(customerAddress.getId(), customerAddress.getCustomer().getId());
    	}
    	redirectAttributes.addFlashAttribute("successMessage", getAddressUpdatedMessage());
    	return getCustomerAddressesRedirect();
    }
    
    public String removeCustomerAddress(HttpServletRequest request, Model model, Long customerAddressId, RedirectAttributes redirectAttributes) {
    	customerAddressService.deleteCustomerAddressById(customerAddressId);
    	redirectAttributes.addFlashAttribute("successMessage", getAddressRemovedMessage());
    	return getCustomerAddressesRedirect();
    }

	public static String getCustomerAddressesView() {
		return customerAddressesView;
	}

	public static void setCustomerAddressesView(String customerAddressesView) {
		BroadleafManageCustomerAddressesController.customerAddressesView = customerAddressesView;
	}

	public static String getCustomerAddressesRedirect() {
		return customerAddressesRedirect;
	}

	public static void setCustomerAddressesRedirect(String customerAddressesRedirect) {
		BroadleafManageCustomerAddressesController.customerAddressesRedirect = customerAddressesRedirect;
	}

	public String getAddressUpdatedMessage() {
		return addressUpdatedMessage;
	}

	public String getAddressAddedMessage() {
		return addressAddedMessage;
	}

	public String getAddressRemovedMessage() {
		return addressRemovedMessage;
	}
	
}
