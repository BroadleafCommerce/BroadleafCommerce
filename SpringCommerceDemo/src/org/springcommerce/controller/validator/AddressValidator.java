package org.springcommerce.controller.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.util.CreateAddress;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class AddressValidator implements Validator {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());


    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(CreateAddress.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {
    	CreateAddress createAddress = (CreateAddress) obj;
    	/*if(createAddress.getAddressName().isEmpty()){
    		errors.rejectValue("addressName", "field.required", null,"Value required.");
    	}*/
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressName", "addressName.required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLine1", "addressLine1.required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "city.required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "state.required");
    	
    }
}
