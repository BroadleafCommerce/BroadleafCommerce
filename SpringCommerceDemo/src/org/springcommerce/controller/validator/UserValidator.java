package org.springcommerce.controller.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.domain.User;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    private static final String REGEX_VALID_NAME = "[A-Za-z'. ]{1,80}";
    private static final String REGEX_VALID_PASSWORD = "[0-9A-Za-z]{4,15}";

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        User user = (User) obj;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");
        
        if(!user.getFirstName().matches(REGEX_VALID_NAME)){
        	errors.rejectValue("username", "username.invalid",null, null);
        }
        if(!user.getLastName().matches(REGEX_VALID_NAME)){
        	errors.rejectValue("lastName", "lastName.invalid", null, null);
        }
        if(!user.getPassword().matches(REGEX_VALID_PASSWORD)){
        	errors.rejectValue("password", "password.invalid", null, null);
        }
        //TODO: need to add some more validation for email address, etc..
    }
}
