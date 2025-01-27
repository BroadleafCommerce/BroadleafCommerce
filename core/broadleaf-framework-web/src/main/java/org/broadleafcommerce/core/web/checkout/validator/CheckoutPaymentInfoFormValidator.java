/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blCheckoutPaymentInfoFormValidator")
public class CheckoutPaymentInfoFormValidator extends PaymentInfoFormValidator {

    @Autowired
    protected Environment environment;

    @Override
    public void validate(Object obj, Errors errors) {
        PaymentInfoForm paymentInfoForm = (PaymentInfoForm) obj;

        if (paymentInfoForm.getShouldUseCustomerPayment()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(
                    errors, "customerPaymentId", "checkout.paymentMethod.customerPaymentId.required"
            );
        } else {
            super.validate(obj, errors);

            String emailAddress = paymentInfoForm.getEmailAddress();
            emailAddress = Boolean.parseBoolean(environment.getProperty("blc.site.enable.xssWrapper", "false"))
                    ? ESAPI.encoder().decodeForHTML(emailAddress)
                    : emailAddress;
            if (!EmailValidator.getInstance().isValid(emailAddress)) {
                errors.rejectValue(
                        "emailAddress", "emailAddress.invalid", null, null
                );
                if (StringUtils.isNotEmpty(emailAddress)) {
                    paymentInfoForm.setEmailAddress(HtmlUtils.htmlEscape(emailAddress));
                }
            }
        }
    }

}
