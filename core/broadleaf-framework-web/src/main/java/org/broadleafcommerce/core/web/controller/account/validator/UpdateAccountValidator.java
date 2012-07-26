package org.broadleafcommerce.core.web.controller.account.validator;

import javax.annotation.Resource;

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.core.web.controller.account.UpdateAccountForm;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component("blUpdateAccountValidator")
public class UpdateAccountValidator implements Validator {

    @Resource(name="blCustomerService")
    CustomerService customerService;

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

            if (customerMatchingNewEmail != null && CustomerState.getCustomer().getId() != customerMatchingNewEmail.getId()) {
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
