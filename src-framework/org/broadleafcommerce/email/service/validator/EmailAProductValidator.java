package org.broadleafcommerce.email.service.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.EmailValidator;
import org.broadleafcommerce.email.domain.EmailAProduct;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EmailAProductValidator implements Validator {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return EmailAProduct.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    	EmailAProduct emailAProduct = (EmailAProduct) target;
		if (emailAProduct.getSenderEmail() == null || "".equals(emailAProduct.getSenderEmail())) {
			errors.reject("emailAProduct.senderEmail.empty", "Please specify your email address.");
		} else if (!EmailValidator.getInstance().isValid(emailAProduct.getSenderEmail())) {
			errors.reject("emailAProduct.senderEmail.invalid", "The email address specified for you is invalid.");
		}

		if (emailAProduct.getRecipientEmail() == null || "".equals(emailAProduct.getRecipientEmail())) {
			errors.reject("emailAProduct.recipientEmail.empty", "Please specify your friend's email address.");
		} else if (!EmailValidator.getInstance().isValid(emailAProduct.getRecipientEmail())) {
			errors.reject("emailAProduct.recipientEmail.invalid", "The email address specified for your friend is invalid.");
		}
    }
}
