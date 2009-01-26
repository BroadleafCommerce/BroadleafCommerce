package org.springcommerce.controller;

import java.net.BindException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springcommerce.controller.validator.CheckoutValidator;
import org.springcommerce.order.domain.OrderPayment;
import org.springcommerce.order.domain.OrderShipping;
import org.springcommerce.order.service.OrderService;
import org.springcommerce.profile.domain.Address;
import org.springcommerce.profile.domain.ContactInfo;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.AddressStandardizationService;
import org.springcommerce.profile.service.UserService;
import org.springcommerce.profile.service.addressValidation.AddressStandarizationResponse;
import org.springcommerce.util.Checkout;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

public class CheckoutController extends AbstractWizardFormController {
	
	UserService userService;
	OrderService orderService;
	AddressStandardizationService addressStandardizationService;
	
	public CheckoutController()
    {
        setCommandClass(Checkout.class);
    }

	@Override
	protected Object formBackingObject(HttpServletRequest request)
    throws ServletException {
		Checkout checkout = new Checkout();

		OrderShipping orderShipping = new OrderShipping();
		orderShipping.setAddress(new Address());

		OrderPayment orderPayment = new OrderPayment();
		orderPayment.setAddress(new Address());

		ContactInfo contactInfo = new ContactInfo();
				
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
        User user = userService.readUserByUsername(auth.getName());
        
        checkout.setOrder(orderService.getCurrentBasketForUserId(user.getId()));
        checkout.setContactInfo(contactInfo);
        checkout.setOrderShipping(orderShipping);
        checkout.setOrderPayment(orderPayment);        
        return checkout;
		
	}
    protected ModelAndView processFinish(HttpServletRequest request, 
                                         HttpServletResponse response,
                                         Object command, BindException errors)
            throws Exception
    {
    	Checkout checkout = (Checkout) command;
        return new ModelAndView("printCommand", "command", checkout) ;
    }

    protected ModelAndView processCancel(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object command, BindException errors)
            throws Exception
    {
        return new ModelAndView("printCommand", "command",
                                "survey form submission cancelled") ;

    }

    @Override
	protected void validatePage(Object command, Errors errors,       
                                int page, boolean finish)
    {
    	Checkout checkout = (Checkout) command;
    	CheckoutValidator validator = (CheckoutValidator)getValidator();

        switch (page)                                                
        {
        case 0:   
			validator.validatePageContactInformation(command, errors); 
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
            User user = userService.readUserByUsername(auth.getName());
            checkout.getContactInfo().setUser(user);
        	orderService.addContactInfoToOrder(checkout.getOrder(),checkout.getContactInfo());
            break ;
        case 1:
        	validator.validateShippingAddressInformation(command, errors);
        	Address address = checkout.getOrderShipping().getAddress();
        	if(!errors.hasErrors()){
        		AddressStandarizationResponse standardizedResponse = addressStandardizationService.standardizeAddress(address);
        		if (standardizedResponse.isErrorDetected()) {
        				address.setStandardized(false);
        				errors.rejectValue("orderShipping.address.addressLine1", "addressVerification.failed", null, null);
        		} 
        	}
        	orderService.addShippingToOrder(checkout.getOrder(), checkout.getOrderShipping());
            break ;
        case 2:
        	validator.validateBillingAddressInformation(command, errors);
        	Address billingAddress = checkout.getOrderPayment().getAddress();
        	if(!errors.hasErrors()){
        		AddressStandarizationResponse standardizedResponse = addressStandardizationService.standardizeAddress(billingAddress);
        		if (standardizedResponse.isErrorDetected()) {
        				logger.debug("Address verification Failed. Please check the address and try again");
        				billingAddress.setStandardized(false);
        				errors.rejectValue("orderPayment.address.addressLine1", "addressVerification.failed", null, null);
        		} 
        	}
        	orderService.addPaymentToOrder(checkout.getOrder(),checkout.getOrderPayment());
            break ;
        case 3:
        	break;
        default:
        }
        if (finish)                                                  
        {
        }
    }

	@Override
	protected ModelAndView processFinish(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2,
			org.springframework.validation.BindException arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setAddressStandardizationService(
			AddressStandardizationService addressStandardizationService) {
		this.addressStandardizationService = addressStandardizationService;
	}

	
	

}
