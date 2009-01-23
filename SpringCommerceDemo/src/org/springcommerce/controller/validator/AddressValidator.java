package org.springcommerce.controller.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.domain.Address;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class AddressValidator implements Validator {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(Address.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressName", "addressName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLine1", "addressLine1.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "city.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "stateCode", "state.required");
    }
}
