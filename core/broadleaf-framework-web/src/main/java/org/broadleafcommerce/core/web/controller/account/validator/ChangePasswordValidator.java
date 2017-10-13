/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.controller.account.validator;

import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

@Component("blChangePasswordValidator")
public class ChangePasswordValidator implements Validator {

    private static final String DEFAULT_VALID_PASSWORD_REGEX = "[^\\s]{6,}";

    @Resource(name = "blCustomerService")
    protected CustomerService customerService;

    @Value("${enable.current.password.check:true}")
    protected Boolean enableCurrentPasswordCheck = true;

    public void validate(PasswordChange passwordChange, Errors errors) {

        String currentPassword = passwordChange.getCurrentPassword();
        String password = passwordChange.getNewPassword();
        String passwordConfirm = passwordChange.getNewPasswordConfirm();

        if (enableCurrentPasswordCheck) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", "currentPassword.required");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "newPassword.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPasswordConfirm", "newPasswordConfirm.required");

        if (!errors.hasErrors()) {
            //validate current password
            if (enableCurrentPasswordCheck && !customerService.isPasswordValid(currentPassword, CustomerState.getCustomer().getPassword(), CustomerState.getCustomer())) {
                errors.rejectValue("currentPassword", "currentPassword.invalid");
            }
            //password and confirm password fields must be equal
            if (!passwordConfirm.equals(password)) {
                errors.rejectValue("newPasswordConfirm", "newPasswordConfirm.invalid");
            }
            //restrict password characteristics
            if (!password.matches(getValidPasswordRegex())) {
                errors.rejectValue("newPassword", "newPassword.invalid");
            }
        }

    }

    public static String getValidPasswordRegex() {
        return BLCSystemProperty.resolveSystemProperty("password.valid.regex", DEFAULT_VALID_PASSWORD_REGEX);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {}

}
