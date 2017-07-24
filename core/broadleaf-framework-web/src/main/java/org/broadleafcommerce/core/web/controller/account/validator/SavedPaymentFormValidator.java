/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.broadleafcommerce.common.web.form.BroadleafFormType;
import org.broadleafcommerce.common.web.validator.BroadleafCommonAddressValidator;
import org.broadleafcommerce.core.web.controller.account.SavedPaymentForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Chris Kittrell (ckittrell)
 * @author Jacob Mitash
 */
@Component("blSavedPaymentFormValidator")
public class SavedPaymentFormValidator extends BroadleafCommonAddressValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SavedPaymentForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        SavedPaymentForm savedPaymentForm = (SavedPaymentForm) obj;
        super.validate(BroadleafFormType.SAVED_PAYMENT_FORM, savedPaymentForm.getAddress(), errors);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "paymentName", "savedPayments.paymentName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "paymentToken", "savedPayments.paymentToken.required");
    }

}
