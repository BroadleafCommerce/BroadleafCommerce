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
        return new CreateAddress();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
                             throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CreateAddress createAddress = (CreateAddress) command;

        User user = userService.readUserByUsername(auth.getName());
        List<Address> addressList = addressService.readAddressByUserId(user.getId());

        for (Iterator<Address> itr = addressList.iterator(); itr.hasNext();) {
            Address address = (Address) itr.next();

            if (createAddress.getAddressName().equalsIgnoreCase(address.getAddressName())) {
                errors.rejectValue("addressName", "addressName.duplicate", new Object[] { new String(address.getAddressName()) }, null);
            }
        }

        Address address = new Address();
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
