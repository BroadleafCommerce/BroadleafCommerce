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
import org.springcommerce.profile.service.UserService;
import org.springcommerce.util.CreateAddress;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class AddressFormController extends SimpleFormController {
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
        CreateAddress createAddress = new CreateAddress();

        if (request.getParameter("addressId") != null) {
            Address address = addressService.readAddressById(Long.valueOf(request.getParameter("addressId")));
            createAddress.setAddressName(address.getAddressName());
            createAddress.setAddressLine1(address.getAddressLine1());
            createAddress.setAddressLine2(address.getAddressLine2());
            createAddress.setCity(address.getCity());
            createAddress.setState(address.getStateCode());
            createAddress.setZipCode(address.getZipCode());
        }

        return createAddress;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Address address;
        CreateAddress createAddress = (CreateAddress) command;
        User user = userService.readUserByUsername(auth.getName());

        if (request.getParameter("addressId") != null) {
            address = addressService.readAddressById(Long.valueOf(request.getParameter("addressId")));
        } else {
            List<Address> addressList = addressService.readAddressByUserId(user.getId());
            for (Iterator<Address> itr = addressList.iterator(); itr.hasNext();) {
                address = (Address) itr.next();
                if (createAddress.getAddressName().equalsIgnoreCase(address.getAddressName())) {
                    errors.rejectValue("addressName", "addressName.duplicate", new Object[] { new String(address.getAddressName()) }, null);
                }
            }
            address = new Address();
        }

        address.setAddressName(createAddress.getAddressName());
        address.setAddressLine1(createAddress.getAddressLine1());
        address.setAddressLine2(createAddress.getAddressLine2());
        address.setStateCode(createAddress.getState());
        address.setZipCode(createAddress.getZipCode());
        address.setCity(createAddress.getCity());
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
