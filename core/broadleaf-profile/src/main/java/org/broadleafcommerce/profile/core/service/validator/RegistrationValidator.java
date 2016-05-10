/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.service.validator;

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("blRegistrationValidator")
public class RegistrationValidator implements Validator {

    public static final String DEFAULT_VALID_NAME_REGEX = "[A-Za-z'. ]{1,80}";

    public static final String DEFAULT_VALID_PASSWORD_REGEX = "[0-9A-Za-z]{4,15}";

    private String validNameRegex = DEFAULT_VALID_NAME_REGEX;

    private String validPasswordRegex = DEFAULT_VALID_PASSWORD_REGEX;

    public void validate(Customer customer, String password, String passwordConfirm, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "passwordConfirm.required");
        errors.pushNestedPath("customer");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");
        errors.popNestedPath();

        if (errors.hasErrors()){
            if (!passwordConfirm.equals(password)) {
                errors.rejectValue("passwordConfirm", "invalid"); 
            }
            if (!customer.getFirstName().matches(validNameRegex)) {
                errors.rejectValue("firstName", "firstName.invalid", null, null);
            }

            if (!customer.getLastName().matches(validNameRegex)) {
                errors.rejectValue("lastName", "lastName.invalid", null, null);
            }

            if (!customer.getPassword().matches(validPasswordRegex)) {
                errors.rejectValue("password", "password.invalid", null, null);
            }

            if (!password.equals(passwordConfirm)) {
                errors.rejectValue("password", "passwordConfirm.invalid", null, null);
            }

            if (!GenericValidator.isEmail(customer.getEmailAddress())) {
                errors.rejectValue("emailAddress", "emailAddress.invalid", null, null);
            }
        }
    }

    public String getValidNameRegex() {
        return validNameRegex;
    }

    public void setValidNameRegex(String validNameRegex) {
        this.validNameRegex = validNameRegex;
    }

    public String getValidPasswordRegex() {
        return validPasswordRegex;
    }

    public void setValidPasswordRegex(String validPasswordRegex) {
        this.validPasswordRegex = validPasswordRegex;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
    }
}
