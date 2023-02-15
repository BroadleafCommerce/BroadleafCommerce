/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.service.exception;

public class ProductOptionValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    protected String attributeName;
    protected String attributeValue;
    protected String validationString;
    protected String errorMessage;
    protected String errorCode;

    public ProductOptionValidationException() {
        super();
    }

    public ProductOptionValidationException(String message, String errorCode, String attributeName, String attributeValue, String validationString, String errorMessage, Throwable cause) {
        super(message, cause);
        setAttributeName(attributeName);
        setAttributeValue(attributeValue);
        setErrorMessage(errorMessage);
        setValidationString(validationString);
        setErrorCode(errorCode);
    }

    public ProductOptionValidationException(String message, String errorCode, String attributeName, String attributeValue, String validationString, String errorMessage) {
        super(message);
        setAttributeName(attributeName);
        setAttributeValue(attributeValue);
        setErrorMessage(errorMessage);
        setValidationString(validationString);
        setErrorCode(errorCode);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }


    public String getValidationString() {
        return validationString;
    }

    public void setValidationString(String validationString) {
        this.validationString = validationString;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
