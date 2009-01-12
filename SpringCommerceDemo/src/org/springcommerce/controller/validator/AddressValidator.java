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
        return CreateAddress.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
    	CreateAddress createAddress = (CreateAddress) obj;
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressName", "field.required", "Address Name Required field");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLine1", "field.required", "Address Line 1 Required field");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "field.required", "City Required field");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "field.required", "State Required field");

    }
}
