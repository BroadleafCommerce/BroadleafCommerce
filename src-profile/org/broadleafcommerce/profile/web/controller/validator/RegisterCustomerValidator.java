package org.broadleafcommerce.profile.web.controller.validator;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.form.CustomerRegistrationForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class RegisterCustomerValidator implements Validator {
    private static final String REGEX_VALID_NAME = "[A-Za-z'. ]{1,80}";
    private static final String REGEX_VALID_PASSWORD = "[0-9A-Za-z]{4,15}";

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private CustomerService customerService;

    public RegisterCustomerValidator() {}

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(CustomerRegistrationForm.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        //TODO: need to add some more validation
        CustomerRegistrationForm user = (CustomerRegistrationForm) obj;

        Customer customerFromDb = customerService.readCustomerByUsername(user.getUsername());

        if (customerFromDb != null) {
            errors.rejectValue("username", "username.used", null, null);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "passwordConfirm.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");

        if (!errors.hasErrors()) {
            if (!user.getFirstName().matches(REGEX_VALID_NAME)) {
                errors.rejectValue("firstName", "firstName.invalid", null, null);
            }

            if (!user.getLastName().matches(REGEX_VALID_NAME)) {
                errors.rejectValue("lastName", "lastName.invalid", null, null);
            }

            if (!user.getPassword().matches(REGEX_VALID_PASSWORD)) {
                errors.rejectValue("password", "password.invalid", null, null);
            }

            if (!user.getPassword().equals(user.getPasswordConfirm())) {
                errors.rejectValue("password", "passwordConfirm.invalid", null, null);
            }

            if (!GenericValidator.isEmail(user.getEmailAddress())) {
                errors.rejectValue("emailAddress", "emailAddress.invalid", null, null);
            }
        }
    }
}
