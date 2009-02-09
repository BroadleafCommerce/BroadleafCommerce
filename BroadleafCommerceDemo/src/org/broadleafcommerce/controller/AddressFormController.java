package org.broadleafcommerce.controller;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.BroadleafCustomerAddress;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.AddressService;
import org.broadleafcommerce.profile.service.AddressStandardizationService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.service.addressValidation.AddressStandarizationResponse;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class AddressFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private AddressService addressService;
    private AddressStandardizationService addressStandardizationService;
    private CustomerService customerService;

    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    public void setAddressStandardizationService(AddressStandardizationService addressStandardizationService) {
        this.addressStandardizationService = addressStandardizationService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        Address address = new BroadleafCustomerAddress();

        if (request.getParameter("addressId") != null) {
            address = addressService.readAddressById(Long.valueOf(request.getParameter("addressId")));

            // TODO: Need to have a discussion whether we want to access the entity directly
            // or do we need an object at this level
            // createAddress.setAddress(address);
        }

        return address;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Address addressFromDB = new BroadleafCustomerAddress();
        Address address = (Address) command;
        Customer customer = customerService.readCustomerByUsername(auth.getName());

        if (request.getParameter("addressId") != null) {
            addressFromDB = addressService.readAddressById(Long.valueOf(request.getParameter("addressId")));
        } else {
            List<Address> addressList = addressService.readAddressByUserId(customer.getId());

            for (Iterator<Address> itr = addressList.iterator(); itr.hasNext();) {
                Address addressItr = itr.next();

                if (address.getAddressName().equalsIgnoreCase(addressItr.getAddressName())) {
                    errors.rejectValue("addressName", "addressName.duplicate", new Object[] { new String(address.getAddressName()) }, null);
                }
            }
        }

        // For USPS test server, only certain addresses would work. Rest will throw an error. Please make sure you check addressVerification.txt file
        // TODO: try standardizeAndTokenize instead of standardizeAddress
        if (!errors.hasErrors()) {
            AddressStandarizationResponse standardizedResponse = addressStandardizationService.standardizeAddress(address);
            if (standardizedResponse.isErrorDetected()) {
                logger.debug("Address verification Failed. Please check the address and try again");
                address.setStandardized(false);
                errors.rejectValue("zipCode", "addressVerification.failed", null, null);
            } else {
                address.setStandardized(true);
                standardizedResponse.getAddress().setAddressName(address.getAddressName());
                if (addressFromDB.getId() != null) {
                    standardizedResponse.getAddress().setId(addressFromDB.getId());
                }
                addressFromDB = standardizedResponse.getAddress();
                addressFromDB.setCustomer(customer);
            }
        }
        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }

        addressService.saveAddress(addressFromDB);
        mav.addObject("saved", true);

        return mav;
    }
}
