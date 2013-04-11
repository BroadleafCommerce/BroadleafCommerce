/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.checkout.validator;

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.core.web.checkout.model.CheckoutForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("blCheckoutFormValidator")
public class CheckoutFormValidator implements Validator {

    @SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        return clazz.equals(CheckoutForm.class);
    }

    public void validate(Object obj, Errors errors) {
        CheckoutForm checkoutForm = (CheckoutForm) obj;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.addressLine1", "addressLine1.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.phonePrimary", "phone.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.city", "city.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.postalCode", "postalCode.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.lastName", "lastName.required");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "shippingAddress.addressLine1", "addressLine1.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "shippingAddress.city", "city.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "shippingAddress.postalCode", "postalCode.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "shippingAddress.firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "shippingAddress.lastName", "lastName.required");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardNumber", "creditCardNumber.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardCvvCode", "creditCardCvvCode.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardExpMonth", "creditCardExpMonth.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardExpYear", "creditCardExpYear.required");

        if (!errors.hasErrors()) {
            if (!GenericValidator.isEmail(checkoutForm.getEmailAddress())) {
                errors.rejectValue("emailAddress", "emailAddress.invalid", null, null);
            }
        }
    }
}

