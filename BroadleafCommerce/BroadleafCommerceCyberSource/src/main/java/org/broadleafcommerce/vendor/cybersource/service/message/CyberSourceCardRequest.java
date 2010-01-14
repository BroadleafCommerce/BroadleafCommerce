/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.vendor.cybersource.service.message;

public class CyberSourceCardRequest extends CyberSourcePaymentRequest {
	
	private static final long serialVersionUID = 1L;
	
	private java.lang.String fullName;
    private java.lang.String accountNumber;
    private java.math.BigInteger expirationMonth;
    private java.math.BigInteger expirationYear;
    private java.lang.String cvIndicator;
    private java.lang.String cvNumber;
    private java.lang.String cardType;
    private java.lang.String issueNumber;
    private java.math.BigInteger startMonth;
    private java.math.BigInteger startYear;
    private java.lang.String pin;
    private java.lang.String bin;

    /**
     * Gets the fullName value for this Card.
     * 
     * @return fullName
     */
    public java.lang.String getFullName() {
        return fullName;
    }

    /**
     * Sets the fullName value for this Card.
     * 
     * @param fullName
     */
    public void setFullName(java.lang.String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the accountNumber value for this Card.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber value for this Card.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the expirationMonth value for this Card.
     * 
     * @return expirationMonth
     */
    public java.math.BigInteger getExpirationMonth() {
        return expirationMonth;
    }

    /**
     * Sets the expirationMonth value for this Card.
     * 
     * @param expirationMonth
     */
    public void setExpirationMonth(java.math.BigInteger expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    /**
     * Gets the expirationYear value for this Card.
     * 
     * @return expirationYear
     */
    public java.math.BigInteger getExpirationYear() {
        return expirationYear;
    }

    /**
     * Sets the expirationYear value for this Card.
     * 
     * @param expirationYear
     */
    public void setExpirationYear(java.math.BigInteger expirationYear) {
        this.expirationYear = expirationYear;
    }

    /**
     * Gets the cvIndicator value for this Card.
     * 
     * @return cvIndicator
     */
    public java.lang.String getCvIndicator() {
        return cvIndicator;
    }

    /**
     * Sets the cvIndicator value for this Card.
     * 
     * @param cvIndicator
     */
    public void setCvIndicator(java.lang.String cvIndicator) {
        this.cvIndicator = cvIndicator;
    }

    /**
     * Gets the cvNumber value for this Card.
     * 
     * @return cvNumber
     */
    public java.lang.String getCvNumber() {
        return cvNumber;
    }

    /**
     * Sets the cvNumber value for this Card.
     * 
     * @param cvNumber
     */
    public void setCvNumber(java.lang.String cvNumber) {
        this.cvNumber = cvNumber;
    }

    /**
     * Gets the cardType value for this Card.
     * 
     * @return cardType
     */
    public java.lang.String getCardType() {
        return cardType;
    }

    /**
     * Sets the cardType value for this Card.
     * 
     * @param cardType
     */
    public void setCardType(java.lang.String cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets the issueNumber value for this Card.
     * 
     * @return issueNumber
     */
    public java.lang.String getIssueNumber() {
        return issueNumber;
    }

    /**
     * Sets the issueNumber value for this Card.
     * 
     * @param issueNumber
     */
    public void setIssueNumber(java.lang.String issueNumber) {
        this.issueNumber = issueNumber;
    }

    /**
     * Gets the startMonth value for this Card.
     * 
     * @return startMonth
     */
    public java.math.BigInteger getStartMonth() {
        return startMonth;
    }

    /**
     * Sets the startMonth value for this Card.
     * 
     * @param startMonth
     */
    public void setStartMonth(java.math.BigInteger startMonth) {
        this.startMonth = startMonth;
    }

    /**
     * Gets the startYear value for this Card.
     * 
     * @return startYear
     */
    public java.math.BigInteger getStartYear() {
        return startYear;
    }

    /**
     * Sets the startYear value for this Card.
     * 
     * @param startYear
     */
    public void setStartYear(java.math.BigInteger startYear) {
        this.startYear = startYear;
    }

    /**
     * Gets the pin value for this Card.
     * 
     * @return pin
     */
    public java.lang.String getPin() {
        return pin;
    }

    /**
     * Sets the pin value for this Card.
     * 
     * @param pin
     */
    public void setPin(java.lang.String pin) {
        this.pin = pin;
    }

    /**
     * Gets the bin value for this Card.
     * 
     * @return bin
     */
    public java.lang.String getBin() {
        return bin;
    }

    /**
     * Sets the bin value for this Card.
     * 
     * @param bin
     */
    public void setBin(java.lang.String bin) {
        this.bin = bin;
    }

}
