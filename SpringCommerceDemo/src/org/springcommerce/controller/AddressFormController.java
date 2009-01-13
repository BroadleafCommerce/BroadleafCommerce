package org.springcommerce.controller;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springcommerce.profile.domain.Address;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.AddressService;
import org.springcommerce.profile.service.AddressStandardizationService;
import org.springcommerce.profile.service.UserService;
import org.springcommerce.profile.service.addressValidation.AddressStandarizationResponse;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class AddressFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    private AddressService addressService;
    private AddressStandardizationService addressStandardizationService;
    private UserService userService;

    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    public void setAddressStandardizationService(AddressStandardizationService addressStandardizationService) {
        this.addressStandardizationService = addressStandardizationService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    protected Object formBackingObject(HttpServletRequest request)
                                throws ServletException {
        Address createAddress = new Address();

        if (request.getParameter("addressId") != null) {
            createAddress = addressService.readAddressById(Long.valueOf(request.getParameter("addressId")));

            //TODO: Need to have a arch. discussion whether we want to access the entity directly
            // or do we need an object at this level - Priya.
            //createAddress.setAddress(address);
        }

        return createAddress;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Address address = (Address) command;
        User user = userService.readUserByUsername(auth.getName());

        if (request.getParameter("addressId") != null) {
            address = addressService.readAddressById(Long.valueOf(request.getParameter("addressId")));
        } else {
            List<Address> addressList = addressService.readAddressByUserId(user.getId());

            for (Iterator<Address> itr = addressList.iterator(); itr.hasNext();) {
                Address addressItr = (Address) itr.next();

                if (address.getAddressName().equalsIgnoreCase(addressItr.getAddressName())) {
                    errors.rejectValue("addressName", "addressName.duplicate", new Object[] { new String(address.getAddressName()) }, null);
                }
            }
        }

        //  For USPS test server, only certain addresses would work.  Rest will throw an error. Please make sure you check addressVerification.txt file
        AddressStandarizationResponse standardizedResponse = addressStandardizationService.standardizeAddress(address);
        if (standardizedResponse.isErrorDetected()) {
            logger.debug("Address verification Failed. Please check the address and try again");
            address.setStandardized(false);
            errors.rejectValue("zipCode", "addressVerification.failed", null, null);
        } else {
            address.setStandardized(true);
            address = standardizedResponse.getAddress();
        }

        address.setUser(user);

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }

        addressService.saveAddress(address);
        mav.addObject("saved", true);

        return mav;
    }
}
