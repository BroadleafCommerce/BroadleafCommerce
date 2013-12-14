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

import org.broadleafcommerce.core.web.checkout.model.CreditCardInfoForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blCreditCardInfoFormValidator")
public class CreditCardInfoFormValidator implements Validator {

    @SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        return clazz.equals(CreditCardInfoForm.class);
    }

    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardName", "creditCardName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardNumber", "creditCardNumber.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardCvvCode", "creditCardCvvCode.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardExpMonth", "creditCardExpMonth.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "creditCardExpYear", "creditCardExpYear.required");
    }
}
