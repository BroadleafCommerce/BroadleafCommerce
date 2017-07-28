package org.broadleafcommerce.core.web.checkout.validator;

import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blCheckoutPaymentInfoFormValidator")
public class CheckoutPaymentInfoFormValidator extends PaymentInfoFormValidator {

    @Override
    public void validate(Object obj, Errors errors) {
        PaymentInfoForm paymentInfoForm = (PaymentInfoForm) obj;

        if (paymentInfoForm.getUseCustomerPayment()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "customerPaymentId", "checkout.paymentMethod.customerPaymentId.required");
        } else {
            super.validate(obj, errors);

            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddress.required");
        }
    }
}
