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
