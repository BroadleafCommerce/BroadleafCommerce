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

