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

package org.broadleafcommerce.vendor.cybersource.service.payment.message;

import org.broadleafcommerce.common.money.Money;

/**
 * 
 * @author jfischer
 *
 */
public class CyberSourceAuthResponse  implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private java.math.BigInteger reasonCode;
    private Money amount;
    private java.lang.String authorizationCode;
    private java.lang.String avsCode;
    private java.lang.String avsCodeRaw;
    private java.lang.String cvCode;
    private java.lang.String cvCodeRaw;
    private java.lang.String personalIDCode;
    private java.lang.String authorizedDateTime;
    private java.lang.String processorResponse;
    private java.lang.String bmlAccountNumber;
    private java.lang.String authFactorCode;
    private java.lang.String reconciliationID;
    private java.lang.String authRecord;
    private java.lang.String merchantAdviceCode;
    private java.lang.String merchantAdviceCodeRaw;
    private java.lang.String cavvResponseCode;
    private java.lang.String cavvResponseCodeRaw;
    private java.lang.String authenticationXID;
    private java.lang.String authorizationXID;
    private java.lang.String processorCardType;
    private Money accountBalance;
    private java.lang.String forwardCode;
    private java.lang.String enhancedDataEnabled;
    private java.lang.String referralResponseNumber;
    private java.lang.String subResponseCode;
    private Money approvedAmount;
    private java.lang.String creditLine;
    private java.lang.String approvedTerms;
    private java.lang.String paymentNetworkTransactionID;
    private java.lang.String cardCategory;

    /**
     * Gets the reasonCode value for this CCAuthReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }

    /**
     * Sets the reasonCode value for this CCAuthReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * Gets the amount value for this CCAuthReply.
     * 
     * @return amount
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * Sets the amount value for this CCAuthReply.
     * 
     * @param amount
     */
    public void setAmount(Money amount) {
        this.amount = amount;
    }

    /**
     * Gets the authorizationCode value for this CCAuthReply.
     * 
     * @return authorizationCode
     */
    public java.lang.String getAuthorizationCode() {
        return authorizationCode;
    }

    /**
     * Sets the authorizationCode value for this CCAuthReply.
     * 
     * @param authorizationCode
     */
    public void setAuthorizationCode(java.lang.String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    /**
     * Gets the avsCode value for this CCAuthReply.
     * 
     * @return avsCode
     */
    public java.lang.String getAvsCode() {
        return avsCode;
    }

    /**
     * Sets the avsCode value for this CCAuthReply.
     * 
     * @param avsCode
     */
    public void setAvsCode(java.lang.String avsCode) {
        this.avsCode = avsCode;
    }

    /**
     * Gets the avsCodeRaw value for this CCAuthReply.
     * 
     * @return avsCodeRaw
     */
    public java.lang.String getAvsCodeRaw() {
        return avsCodeRaw;
    }

    /**
     * Sets the avsCodeRaw value for this CCAuthReply.
     * 
     * @param avsCodeRaw
     */
    public void setAvsCodeRaw(java.lang.String avsCodeRaw) {
        this.avsCodeRaw = avsCodeRaw;
    }

    /**
     * Gets the cvCode value for this CCAuthReply.
     * 
     * @return cvCode
     */
    public java.lang.String getCvCode() {
        return cvCode;
    }

    /**
     * Sets the cvCode value for this CCAuthReply.
     * 
     * @param cvCode
     */
    public void setCvCode(java.lang.String cvCode) {
        this.cvCode = cvCode;
    }

    /**
     * Gets the cvCodeRaw value for this CCAuthReply.
     * 
     * @return cvCodeRaw
     */
    public java.lang.String getCvCodeRaw() {
        return cvCodeRaw;
    }

    /**
     * Sets the cvCodeRaw value for this CCAuthReply.
     * 
     * @param cvCodeRaw
     */
    public void setCvCodeRaw(java.lang.String cvCodeRaw) {
        this.cvCodeRaw = cvCodeRaw;
    }

    /**
     * Gets the personalIDCode value for this CCAuthReply.
     * 
     * @return personalIDCode
     */
    public java.lang.String getPersonalIDCode() {
        return personalIDCode;
    }

    /**
     * Sets the personalIDCode value for this CCAuthReply.
     * 
     * @param personalIDCode
     */
    public void setPersonalIDCode(java.lang.String personalIDCode) {
        this.personalIDCode = personalIDCode;
    }

    /**
     * Gets the authorizedDateTime value for this CCAuthReply.
     * 
     * @return authorizedDateTime
     */
    public java.lang.String getAuthorizedDateTime() {
        return authorizedDateTime;
    }

    /**
     * Sets the authorizedDateTime value for this CCAuthReply.
     * 
     * @param authorizedDateTime
     */
    public void setAuthorizedDateTime(java.lang.String authorizedDateTime) {
        this.authorizedDateTime = authorizedDateTime;
    }

    /**
     * Gets the processorResponse value for this CCAuthReply.
     * 
     * @return processorResponse
     */
    public java.lang.String getProcessorResponse() {
        return processorResponse;
    }

    /**
     * Sets the processorResponse value for this CCAuthReply.
     * 
     * @param processorResponse
     */
    public void setProcessorResponse(java.lang.String processorResponse) {
        this.processorResponse = processorResponse;
    }

    /**
     * Gets the bmlAccountNumber value for this CCAuthReply.
     * 
     * @return bmlAccountNumber
     */
    public java.lang.String getBmlAccountNumber() {
        return bmlAccountNumber;
    }

    /**
     * Sets the bmlAccountNumber value for this CCAuthReply.
     * 
     * @param bmlAccountNumber
     */
    public void setBmlAccountNumber(java.lang.String bmlAccountNumber) {
        this.bmlAccountNumber = bmlAccountNumber;
    }


    /**
     * Gets the authFactorCode value for this CCAuthReply.
     * 
     * @return authFactorCode
     */
    public java.lang.String getAuthFactorCode() {
        return authFactorCode;
    }

    /**
     * Sets the authFactorCode value for this CCAuthReply.
     * 
     * @param authFactorCode
     */
    public void setAuthFactorCode(java.lang.String authFactorCode) {
        this.authFactorCode = authFactorCode;
    }

    /**
     * Gets the reconciliationID value for this CCAuthReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }

    /**
     * Sets the reconciliationID value for this CCAuthReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }

    /**
     * Gets the authRecord value for this CCAuthReply.
     * 
     * @return authRecord
     */
    public java.lang.String getAuthRecord() {
        return authRecord;
    }

    /**
     * Sets the authRecord value for this CCAuthReply.
     * 
     * @param authRecord
     */
    public void setAuthRecord(java.lang.String authRecord) {
        this.authRecord = authRecord;
    }

    /**
     * Gets the merchantAdviceCode value for this CCAuthReply.
     * 
     * @return merchantAdviceCode
     */
    public java.lang.String getMerchantAdviceCode() {
        return merchantAdviceCode;
    }

    /**
     * Sets the merchantAdviceCode value for this CCAuthReply.
     * 
     * @param merchantAdviceCode
     */
    public void setMerchantAdviceCode(java.lang.String merchantAdviceCode) {
        this.merchantAdviceCode = merchantAdviceCode;
    }

    /**
     * Gets the merchantAdviceCodeRaw value for this CCAuthReply.
     * 
     * @return merchantAdviceCodeRaw
     */
    public java.lang.String getMerchantAdviceCodeRaw() {
        return merchantAdviceCodeRaw;
    }

    /**
     * Sets the merchantAdviceCodeRaw value for this CCAuthReply.
     * 
     * @param merchantAdviceCodeRaw
     */
    public void setMerchantAdviceCodeRaw(java.lang.String merchantAdviceCodeRaw) {
        this.merchantAdviceCodeRaw = merchantAdviceCodeRaw;
    }

    /**
     * Gets the cavvResponseCode value for this CCAuthReply.
     * 
     * @return cavvResponseCode
     */
    public java.lang.String getCavvResponseCode() {
        return cavvResponseCode;
    }

    /**
     * Sets the cavvResponseCode value for this CCAuthReply.
     * 
     * @param cavvResponseCode
     */
    public void setCavvResponseCode(java.lang.String cavvResponseCode) {
        this.cavvResponseCode = cavvResponseCode;
    }

    /**
     * Gets the cavvResponseCodeRaw value for this CCAuthReply.
     * 
     * @return cavvResponseCodeRaw
     */
    public java.lang.String getCavvResponseCodeRaw() {
        return cavvResponseCodeRaw;
    }

    /**
     * Sets the cavvResponseCodeRaw value for this CCAuthReply.
     * 
     * @param cavvResponseCodeRaw
     */
    public void setCavvResponseCodeRaw(java.lang.String cavvResponseCodeRaw) {
        this.cavvResponseCodeRaw = cavvResponseCodeRaw;
    }

    /**
     * Gets the authenticationXID value for this CCAuthReply.
     * 
     * @return authenticationXID
     */
    public java.lang.String getAuthenticationXID() {
        return authenticationXID;
    }

    /**
     * Sets the authenticationXID value for this CCAuthReply.
     * 
     * @param authenticationXID
     */
    public void setAuthenticationXID(java.lang.String authenticationXID) {
        this.authenticationXID = authenticationXID;
    }

    /**
     * Gets the authorizationXID value for this CCAuthReply.
     * 
     * @return authorizationXID
     */
    public java.lang.String getAuthorizationXID() {
        return authorizationXID;
    }

    /**
     * Sets the authorizationXID value for this CCAuthReply.
     * 
     * @param authorizationXID
     */
    public void setAuthorizationXID(java.lang.String authorizationXID) {
        this.authorizationXID = authorizationXID;
    }

    /**
     * Gets the processorCardType value for this CCAuthReply.
     * 
     * @return processorCardType
     */
    public java.lang.String getProcessorCardType() {
        return processorCardType;
    }

    /**
     * Sets the processorCardType value for this CCAuthReply.
     * 
     * @param processorCardType
     */
    public void setProcessorCardType(java.lang.String processorCardType) {
        this.processorCardType = processorCardType;
    }

    /**
     * Gets the accountBalance value for this CCAuthReply.
     * 
     * @return accountBalance
     */
    public Money getAccountBalance() {
        return accountBalance;
    }

    /**
     * Sets the accountBalance value for this CCAuthReply.
     * 
     * @param accountBalance
     */
    public void setAccountBalance(Money accountBalance) {
        this.accountBalance = accountBalance;
    }

    /**
     * Gets the forwardCode value for this CCAuthReply.
     * 
     * @return forwardCode
     */
    public java.lang.String getForwardCode() {
        return forwardCode;
    }

    /**
     * Sets the forwardCode value for this CCAuthReply.
     * 
     * @param forwardCode
     */
    public void setForwardCode(java.lang.String forwardCode) {
        this.forwardCode = forwardCode;
    }

    /**
     * Gets the enhancedDataEnabled value for this CCAuthReply.
     * 
     * @return enhancedDataEnabled
     */
    public java.lang.String getEnhancedDataEnabled() {
        return enhancedDataEnabled;
    }

    /**
     * Sets the enhancedDataEnabled value for this CCAuthReply.
     * 
     * @param enhancedDataEnabled
     */
    public void setEnhancedDataEnabled(java.lang.String enhancedDataEnabled) {
        this.enhancedDataEnabled = enhancedDataEnabled;
    }

    /**
     * Gets the referralResponseNumber value for this CCAuthReply.
     * 
     * @return referralResponseNumber
     */
    public java.lang.String getReferralResponseNumber() {
        return referralResponseNumber;
    }

    /**
     * Sets the referralResponseNumber value for this CCAuthReply.
     * 
     * @param referralResponseNumber
     */
    public void setReferralResponseNumber(java.lang.String referralResponseNumber) {
        this.referralResponseNumber = referralResponseNumber;
    }

    /**
     * Gets the subResponseCode value for this CCAuthReply.
     * 
     * @return subResponseCode
     */
    public java.lang.String getSubResponseCode() {
        return subResponseCode;
    }

    /**
     * Sets the subResponseCode value for this CCAuthReply.
     * 
     * @param subResponseCode
     */
    public void setSubResponseCode(java.lang.String subResponseCode) {
        this.subResponseCode = subResponseCode;
    }

    /**
     * Gets the approvedAmount value for this CCAuthReply.
     * 
     * @return approvedAmount
     */
    public Money getApprovedAmount() {
        return approvedAmount;
    }

    /**
     * Sets the approvedAmount value for this CCAuthReply.
     * 
     * @param approvedAmount
     */
    public void setApprovedAmount(Money approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    /**
     * Gets the creditLine value for this CCAuthReply.
     * 
     * @return creditLine
     */
    public java.lang.String getCreditLine() {
        return creditLine;
    }

    /**
     * Sets the creditLine value for this CCAuthReply.
     * 
     * @param creditLine
     */
    public void setCreditLine(java.lang.String creditLine) {
        this.creditLine = creditLine;
    }

    /**
     * Gets the approvedTerms value for this CCAuthReply.
     * 
     * @return approvedTerms
     */
    public java.lang.String getApprovedTerms() {
        return approvedTerms;
    }

    /**
     * Sets the approvedTerms value for this CCAuthReply.
     * 
     * @param approvedTerms
     */
    public void setApprovedTerms(java.lang.String approvedTerms) {
        this.approvedTerms = approvedTerms;
    }

    /**
     * Gets the paymentNetworkTransactionID value for this CCAuthReply.
     * 
     * @return paymentNetworkTransactionID
     */
    public java.lang.String getPaymentNetworkTransactionID() {
        return paymentNetworkTransactionID;
    }

    /**
     * Sets the paymentNetworkTransactionID value for this CCAuthReply.
     * 
     * @param paymentNetworkTransactionID
     */
    public void setPaymentNetworkTransactionID(java.lang.String paymentNetworkTransactionID) {
        this.paymentNetworkTransactionID = paymentNetworkTransactionID;
    }

    /**
     * Gets the cardCategory value for this CCAuthReply.
     * 
     * @return cardCategory
     */
    public java.lang.String getCardCategory() {
        return cardCategory;
    }

    /**
     * Sets the cardCategory value for this CCAuthReply.
     * 
     * @param cardCategory
     */
    public void setCardCategory(java.lang.String cardCategory) {
        this.cardCategory = cardCategory;
    }

}
