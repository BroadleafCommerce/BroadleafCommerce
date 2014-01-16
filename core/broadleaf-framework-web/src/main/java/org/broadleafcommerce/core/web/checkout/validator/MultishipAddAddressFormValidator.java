/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.checkout.validator;

import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("blMultishipAddAddressFormValidator")
public class MultishipAddAddressFormValidator implements Validator {

    @SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        return clazz.equals(ShippingInfoForm.class);
    }

    public void validate(Object obj, Errors errors) {
        ShippingInfoForm shippingInfoForm = (ShippingInfoForm) obj;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.firstName", "firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.lastName", "lastName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.addressLine1", "addressLine1.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.city", "city.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.postalCode", "postalCode.required");

        if (shippingInfoForm.getAddress().getCountry() == null) {
            errors.rejectValue("address.country", "country.required", null, null);
        }
        if (shippingInfoForm.getAddress().getState() == null) {
            errors.rejectValue("address.state", "state.required", null, null);
        }
    }
}
