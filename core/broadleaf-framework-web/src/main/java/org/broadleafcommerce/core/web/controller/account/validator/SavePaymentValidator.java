package org.broadleafcommerce.core.web.controller.account.validator;

import org.broadleafcommerce.core.web.controller.account.SavePaymentForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Jacob Mitash
 */
@Component("blSavePaymentValidator")
public class SavePaymentValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SavePaymentForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SavePaymentForm savePaymentForm = (SavePaymentForm) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personName", "personName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "paymentName", "paymentName.required");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastFourDigits", "lastFourDigits.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expiration", "expiration.required");

        if(!savePaymentForm.getExpiration().trim().isEmpty() && !savePaymentForm.getExpiration().trim().matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
            errors.reject("expiration.invalid");
        }
    }
}
