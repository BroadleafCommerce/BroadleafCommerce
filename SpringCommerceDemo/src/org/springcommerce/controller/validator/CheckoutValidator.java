package org.springcommerce.controller.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springcommerce.util.Checkout;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class CheckoutValidator implements Validator {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    private static final String REGEX_VALID_PHONE = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(Checkout.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {
    }

    public void validatePageContactInformation(Object obj, Errors errors){
    	Checkout checkout = (Checkout) obj;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactInfo.primaryPhone", "primaryPhone.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactInfo.secondaryPhone", "secondaryPhone.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactInfo.email", "email.required");
        
        if(!checkout.getContactInfo().getPrimaryPhone().matches(REGEX_VALID_PHONE)){
        	errors.rejectValue("contactInfo.primaryPhone", "contactInfo.phone.invalid",null, null);
        }
        
        if(!checkout.getContactInfo().getPrimaryPhone().matches(REGEX_VALID_PHONE)){
            	errors.rejectValue("contactInfo.secondaryPhone", "contactInfo.phone.invalid",null, null);
        }
        
        if(!GenericValidator.isEmail(checkout.getContactInfo().getEmail())){
        	errors.rejectValue("contactInfo.email", "contactInfo.email.invalid", null, null);
        }
    	
    }
    
    public void validateShippingAddressInformation(Object obj, Errors errors){
         ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orderShipping.address.addressLine1", "addressLine1.required");
         ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orderShipping.address.city", "city.required");
         ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orderShipping.address.stateCode", "state.required");
    }
    
    public void validateBillingAddressInformation(Object obj, Errors errors){
    	//TODO: add credit card validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orderPayment.address.addressLine1", "addressLine1.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orderPayment.address.city", "city.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orderPayment.address.stateCode", "state.required");
   }
}
