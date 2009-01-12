package org.springcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springcommerce.profile.domain.Address;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.AddressService;
import org.springcommerce.profile.service.UserService;
import org.springcommerce.util.CreateAddress;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class AddressFormController extends SimpleFormController {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    private AddressService addressService;
    private UserService userService;

    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    protected Object formBackingObject(HttpServletRequest request)
                                throws ServletException {
        return new CreateAddress();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByUsername(auth.getName());
        logger.debug("**** in onSubmit");

        CreateAddress createAddress = (CreateAddress) command;
        logger.debug("Address line1: " + createAddress.getAddressLine1());

        Address address = new Address();
        address.setAddressName(createAddress.getAddressName());
        address.setAddressLine1(createAddress.getAddressLine1());
        address.setAddressLine2(createAddress.getAddressLine2());
        address.setStateCode(createAddress.getState());
        address.setZipCode(createAddress.getZipCode());
        address.setCity(createAddress.getCity());
        address.setUser(user);

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (!errors.hasErrors()) {
            addressService.saveAddress(address);
        }

        mav.addObject("saved", true);

        return mav;
    }
}
