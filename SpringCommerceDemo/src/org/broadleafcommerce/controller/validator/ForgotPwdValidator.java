package org.broadleafcommerce.controller.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.util.PasswordChange;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ForgotPwdValidator implements Validator {
    protected final Log logger = LogFactory.getLog(getClass());
    private static final String REGEX_VALID_PASSWORD = "[0-9A-Za-z]{4,15}";

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(PasswordChange.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {
    	PasswordChange passwordChange = (PasswordChange) obj;
    	if(!passwordChange.getNewPassword().matches(REGEX_VALID_PASSWORD)){
        	errors.rejectValue("password", "password.invalid", null, null);
        }
        
        if(!passwordChange.getNewPassword().equals(passwordChange.getNewPasswordConfirm())){
        	errors.rejectValue("password", "passwordConfirm.invalid", null, null);
        }
    }
}