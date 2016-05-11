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

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.core.web.controller.account.UpdateAccountForm;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.annotation.Resource;


@Component("blUpdateAccountValidator")
public class UpdateAccountValidator implements Validator {

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    public void validate(UpdateAccountForm form, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.required");

        if (!errors.hasErrors()) {

            //is this a valid email address?
            if (!GenericValidator.isEmail(form.getEmailAddress())) {
                errors.rejectValue("emailAddress", "emailAddress.invalid");
            }

            //check email address to see if it is already in use by another customer
            Customer customerMatchingNewEmail = customerService.readCustomerByEmail(form.getEmailAddress());

            if (customerMatchingNewEmail != null && !CustomerState.getCustomer().getId().equals(customerMatchingNewEmail.getId())) {
                //customer found with new email entered, and it is not the current customer
                errors.rejectValue("emailAddress", "emailAddress.used");
            }

        }

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

}
