/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.controller.validator;

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

/**
 * @author bpolster
 */
@Component("blRegisterCustomerValidator")
public class RegisterCustomerValidator implements Validator {

    private String validatePasswordExpression = "[^\\s]{6,}";

    @Resource(name="blCustomerService")
    private CustomerService customerService;

    public RegisterCustomerValidator() {}

    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return clazz.equals(RegisterCustomerForm.class);
    }
    
    public void validate(Object obj, Errors errors) {
        validate(obj, errors, false);
    }

    public void validate(Object obj, Errors errors, boolean useEmailForUsername) {
        RegisterCustomerForm form = (RegisterCustomerForm) obj;

        Customer customerFromDb = customerService.readCustomerByUsername(form.getCustomer().getUsername());

        if (customerFromDb != null && customerFromDb.isRegistered()) {
            if (useEmailForUsername) {
                errors.rejectValue("customer.emailAddress", "emailAddress.used", null, null);
            } else {
                errors.rejectValue("customer.username", "username.used", null, null);
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "passwordConfirm.required");
        
        errors.pushNestedPath("customer");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");
        errors.popNestedPath();


        if (!errors.hasErrors()) {

            if (!form.getPassword().matches(getValidatePasswordExpression())) {
                errors.rejectValue("password", "password.invalid", null, null);
            }

            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                errors.rejectValue("password", "passwordConfirm.invalid", null, null);
            }

            if (!GenericValidator.isEmail(form.getCustomer().getEmailAddress())) {
                errors.rejectValue("customer.emailAddress", "emailAddress.invalid", null, null);
            }
        }
    }

    public String getValidatePasswordExpression() {
        return BLCSystemProperty.resolveSystemProperty("validate.password", validatePasswordExpression);
    }

    public void setValidatePasswordExpression(String validatePasswordExpression) {
        this.validatePasswordExpression = validatePasswordExpression;
    }        
}
