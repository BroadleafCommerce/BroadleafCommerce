/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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

import org.broadleafcommerce.core.web.checkout.model.GiftCardInfoForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Component("blGiftCardInfoFormValidator")
public class GiftCardInfoFormValidator implements Validator {

    @Override
    @SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        return clazz.equals(GiftCardInfoForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GiftCardInfoForm giftCardInfoForm = (GiftCardInfoForm) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "giftCardNumber", "giftCardNumber.required");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "giftCardEmailAddress", "giftCardEmailAddress.required");

        //if (!errors.hasErrors()) {
        //    if (!GenericValidator.isEmail(giftCardInfoForm.getGiftCardEmailAddress())) {
        //        errors.rejectValue("giftCardEmailAddress", "giftCardEmailAddress.invalid", null, null);
        //    }
        //}
    }
}

