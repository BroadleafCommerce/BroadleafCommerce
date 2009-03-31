package org.broadleafcommerce.profile.service.validator;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public class BaseResponse {

    private Errors errors;

    public BaseResponse(Object target, String objectName) {
        errors = new BindException(target, objectName);
    }

    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public Errors getErrors() {
        return errors;
    }
}
