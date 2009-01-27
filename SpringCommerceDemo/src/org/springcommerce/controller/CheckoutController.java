package org.springcommerce.controller;

import java.net.BindException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springcommerce.controller.validator.CheckoutValidator;
import org.springcommerce.order.domain.Order;
import org.springcommerce.order.domain.OrderPayment;
import org.springcommerce.order.domain.OrderShipping;
import org.springcommerce.order.service.OrderService;
import org.springcommerce.profile.domain.Address;
import org.springcommerce.profile.domain.ContactInfo;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.service.AddressService;
import org.springcommerce.profile.service.AddressStandardizationService;
import org.springcommerce.profile.service.ContactInfoService;
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
	ContactInfoService contactInfoService;
	AddressService addressService;
	AddressStandardizationService addressStandardizationService;
	
	private String successView;
	
	public CheckoutController()
    {
        setCommandClass(Checkout.class);
    }

	@Override
	protected Object formBackingObject(HttpServletRequest request)
    throws ServletException {
		Checkout checkout = new Checkout();

		Order order;
		
		OrderShipping orderShipping = new OrderShipping();
		orderShipping.setAddress(new Address());

		OrderPayment orderPayment = new OrderPayment();
		orderPayment.setAddress(new Address());

		ContactInfo contactInfo = new ContactInfo();
				

		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
        User user = userService.readUserByUsername(auth.getName());                
        List<ContactInfo> contactInfos = contactInfoService.readContactInfoByUserId(user.getId());
        List<Address> addressList = addressService.readAddressByUserId(user.getId());
        
        order = orderService.getCurrentBasketForUser(user);
        
        checkout.setUserContactInfo(contactInfos);        
        checkout.setAddressList(addressList);
        checkout.setOrder(orderService.getCurrentBasketForUserId(user.getId()));
        checkout.setOrderItems(orderService.getItemsForOrder(order.getId()));
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
        	String contactIndex = checkout.getSelectedContactInfoId();  
        	if(contactIndex != null && !contactIndex.equals("")){
        		checkout.setContactInfo(checkout.getUserContactInfo().get(Integer.parseInt(contactIndex)-1));
        	}else{
        		validator.validatePageContactInformation(command, errors);         		
        	}
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
            User user = userService.readUserByUsername(auth.getName());
            checkout.getContactInfo().setUser(user);
        	orderService.addContactInfoToOrder(checkout.getOrder(),checkout.getContactInfo());
            break ;
        case 1:
        	String shippingAddressId = checkout.getSelectedShippingAddressId();
        	if(! StringUtils.isEmpty(shippingAddressId)){
        		Iterator<Address> itr=checkout.getAddressList().iterator();
        		while(itr.hasNext()){
        			Address address = (Address)itr.next();
        			if(address.getId().equals(Long.parseLong(shippingAddressId))){
        				checkout.getOrderShipping().setAddress(address);
        				break;
        			}
        		}
        	}else{
        		validator.validateShippingAddressInformation(command, errors);
        		Address address = checkout.getOrderShipping().getAddress();
        		if(!errors.hasErrors()){
        			AddressStandarizationResponse standardizedResponse = addressStandardizationService.standardizeAddress(address);
        			if (standardizedResponse.isErrorDetected()) {
        				address.setStandardized(false);
        				errors.rejectValue("orderShipping.address.addressLine1", "addressVerification.failed", null, null);
        			} 
        		}
        	}
        	orderService.addShippingToOrder(checkout.getOrder(), checkout.getOrderShipping());
            break ;
        case 2:
        	String billingAddressId = checkout.getSelectedBillingAddressId();
        	if(! StringUtils.isEmpty(billingAddressId)){
        		Iterator<Address> itr=checkout.getAddressList().iterator();
        		while(itr.hasNext()){
        			Address address = (Address)itr.next();
        			if(address.getId().equals(Long.parseLong(billingAddressId))){
        				checkout.getOrderPayment().setAddress(address);
        				break;
        			}
        		}
        	}else{
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
        	}
        	orderService.addPaymentToOrder(checkout.getOrder(),checkout.getOrderPayment());
            break ;
        case 3:
        	
        	finish = true;
        	break;
        default:
        }
        if (finish)                                                  
        {
        	orderService.confirmOrder(checkout.getOrder());
        }
    }

	@Override
	protected ModelAndView processFinish(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2,
			org.springframework.validation.BindException arg3) throws Exception {
		// TODO Auto-generated method stub
        ModelAndView mav = new ModelAndView(getSuccessView());
        return mav;
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

	public ContactInfoService getContactInfoService() {
		return contactInfoService;
	}

	public void setContactInfoService(ContactInfoService contactInfoService) {
		this.contactInfoService = contactInfoService;
	}	
	
	public void setAddressStandardizationService(
			AddressStandardizationService addressStandardizationService) {
		this.addressStandardizationService = addressStandardizationService;
	}

	public void setAddressService(AddressService addressService) {
		this.addressService = addressService;
	}

	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}	
	
	

}
