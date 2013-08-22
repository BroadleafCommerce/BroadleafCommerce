/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
