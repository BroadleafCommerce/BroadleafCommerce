/**
 * CCAuthReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class CCAuthReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String amount;

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

    private org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals;

    private java.lang.String fxQuoteID;

    private java.lang.String fxQuoteRate;

    private java.lang.String fxQuoteType;

    private java.lang.String fxQuoteExpirationDateTime;

    private java.lang.String authRecord;

    private java.lang.String merchantAdviceCode;

    private java.lang.String merchantAdviceCodeRaw;

    private java.lang.String cavvResponseCode;

    private java.lang.String cavvResponseCodeRaw;

    private java.lang.String authenticationXID;

    private java.lang.String authorizationXID;

    private java.lang.String processorCardType;

    private java.lang.String accountBalance;

    private java.lang.String forwardCode;

    private java.lang.String enhancedDataEnabled;

    private java.lang.String referralResponseNumber;

    private java.lang.String subResponseCode;

    private java.lang.String approvedAmount;

    private java.lang.String creditLine;

    private java.lang.String approvedTerms;

    private java.lang.String paymentNetworkTransactionID;

    private java.lang.String cardCategory;

    public CCAuthReply() {
    }

    public CCAuthReply(
           java.math.BigInteger reasonCode,
           java.lang.String amount,
           java.lang.String authorizationCode,
           java.lang.String avsCode,
           java.lang.String avsCodeRaw,
           java.lang.String cvCode,
           java.lang.String cvCodeRaw,
           java.lang.String personalIDCode,
           java.lang.String authorizedDateTime,
           java.lang.String processorResponse,
           java.lang.String bmlAccountNumber,
           java.lang.String authFactorCode,
           java.lang.String reconciliationID,
           org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals,
           java.lang.String fxQuoteID,
           java.lang.String fxQuoteRate,
           java.lang.String fxQuoteType,
           java.lang.String fxQuoteExpirationDateTime,
           java.lang.String authRecord,
           java.lang.String merchantAdviceCode,
           java.lang.String merchantAdviceCodeRaw,
           java.lang.String cavvResponseCode,
           java.lang.String cavvResponseCodeRaw,
           java.lang.String authenticationXID,
           java.lang.String authorizationXID,
           java.lang.String processorCardType,
           java.lang.String accountBalance,
           java.lang.String forwardCode,
           java.lang.String enhancedDataEnabled,
           java.lang.String referralResponseNumber,
           java.lang.String subResponseCode,
           java.lang.String approvedAmount,
           java.lang.String creditLine,
           java.lang.String approvedTerms,
           java.lang.String paymentNetworkTransactionID,
           java.lang.String cardCategory) {
           this.reasonCode = reasonCode;
           this.amount = amount;
           this.authorizationCode = authorizationCode;
           this.avsCode = avsCode;
           this.avsCodeRaw = avsCodeRaw;
           this.cvCode = cvCode;
           this.cvCodeRaw = cvCodeRaw;
           this.personalIDCode = personalIDCode;
           this.authorizedDateTime = authorizedDateTime;
           this.processorResponse = processorResponse;
           this.bmlAccountNumber = bmlAccountNumber;
           this.authFactorCode = authFactorCode;
           this.reconciliationID = reconciliationID;
           this.fundingTotals = fundingTotals;
           this.fxQuoteID = fxQuoteID;
           this.fxQuoteRate = fxQuoteRate;
           this.fxQuoteType = fxQuoteType;
           this.fxQuoteExpirationDateTime = fxQuoteExpirationDateTime;
           this.authRecord = authRecord;
           this.merchantAdviceCode = merchantAdviceCode;
           this.merchantAdviceCodeRaw = merchantAdviceCodeRaw;
           this.cavvResponseCode = cavvResponseCode;
           this.cavvResponseCodeRaw = cavvResponseCodeRaw;
           this.authenticationXID = authenticationXID;
           this.authorizationXID = authorizationXID;
           this.processorCardType = processorCardType;
           this.accountBalance = accountBalance;
           this.forwardCode = forwardCode;
           this.enhancedDataEnabled = enhancedDataEnabled;
           this.referralResponseNumber = referralResponseNumber;
           this.subResponseCode = subResponseCode;
           this.approvedAmount = approvedAmount;
           this.creditLine = creditLine;
           this.approvedTerms = approvedTerms;
           this.paymentNetworkTransactionID = paymentNetworkTransactionID;
           this.cardCategory = cardCategory;
    }


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
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this CCAuthReply.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
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
     * Gets the fundingTotals value for this CCAuthReply.
     * 
     * @return fundingTotals
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals getFundingTotals() {
        return fundingTotals;
    }


    /**
     * Sets the fundingTotals value for this CCAuthReply.
     * 
     * @param fundingTotals
     */
    public void setFundingTotals(org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals) {
        this.fundingTotals = fundingTotals;
    }


    /**
     * Gets the fxQuoteID value for this CCAuthReply.
     * 
     * @return fxQuoteID
     */
    public java.lang.String getFxQuoteID() {
        return fxQuoteID;
    }


    /**
     * Sets the fxQuoteID value for this CCAuthReply.
     * 
     * @param fxQuoteID
     */
    public void setFxQuoteID(java.lang.String fxQuoteID) {
        this.fxQuoteID = fxQuoteID;
    }


    /**
     * Gets the fxQuoteRate value for this CCAuthReply.
     * 
     * @return fxQuoteRate
     */
    public java.lang.String getFxQuoteRate() {
        return fxQuoteRate;
    }


    /**
     * Sets the fxQuoteRate value for this CCAuthReply.
     * 
     * @param fxQuoteRate
     */
    public void setFxQuoteRate(java.lang.String fxQuoteRate) {
        this.fxQuoteRate = fxQuoteRate;
    }


    /**
     * Gets the fxQuoteType value for this CCAuthReply.
     * 
     * @return fxQuoteType
     */
    public java.lang.String getFxQuoteType() {
        return fxQuoteType;
    }


    /**
     * Sets the fxQuoteType value for this CCAuthReply.
     * 
     * @param fxQuoteType
     */
    public void setFxQuoteType(java.lang.String fxQuoteType) {
        this.fxQuoteType = fxQuoteType;
    }


    /**
     * Gets the fxQuoteExpirationDateTime value for this CCAuthReply.
     * 
     * @return fxQuoteExpirationDateTime
     */
    public java.lang.String getFxQuoteExpirationDateTime() {
        return fxQuoteExpirationDateTime;
    }


    /**
     * Sets the fxQuoteExpirationDateTime value for this CCAuthReply.
     * 
     * @param fxQuoteExpirationDateTime
     */
    public void setFxQuoteExpirationDateTime(java.lang.String fxQuoteExpirationDateTime) {
        this.fxQuoteExpirationDateTime = fxQuoteExpirationDateTime;
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
    public java.lang.String getAccountBalance() {
        return accountBalance;
    }


    /**
     * Sets the accountBalance value for this CCAuthReply.
     * 
     * @param accountBalance
     */
    public void setAccountBalance(java.lang.String accountBalance) {
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
    public java.lang.String getApprovedAmount() {
        return approvedAmount;
    }


    /**
     * Sets the approvedAmount value for this CCAuthReply.
     * 
     * @param approvedAmount
     */
    public void setApprovedAmount(java.lang.String approvedAmount) {
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

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CCAuthReply)) return false;
        CCAuthReply other = (CCAuthReply) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.reasonCode==null && other.getReasonCode()==null) || 
             (this.reasonCode!=null &&
              this.reasonCode.equals(other.getReasonCode()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.authorizationCode==null && other.getAuthorizationCode()==null) || 
             (this.authorizationCode!=null &&
              this.authorizationCode.equals(other.getAuthorizationCode()))) &&
            ((this.avsCode==null && other.getAvsCode()==null) || 
             (this.avsCode!=null &&
              this.avsCode.equals(other.getAvsCode()))) &&
            ((this.avsCodeRaw==null && other.getAvsCodeRaw()==null) || 
             (this.avsCodeRaw!=null &&
              this.avsCodeRaw.equals(other.getAvsCodeRaw()))) &&
            ((this.cvCode==null && other.getCvCode()==null) || 
             (this.cvCode!=null &&
              this.cvCode.equals(other.getCvCode()))) &&
            ((this.cvCodeRaw==null && other.getCvCodeRaw()==null) || 
             (this.cvCodeRaw!=null &&
              this.cvCodeRaw.equals(other.getCvCodeRaw()))) &&
            ((this.personalIDCode==null && other.getPersonalIDCode()==null) || 
             (this.personalIDCode!=null &&
              this.personalIDCode.equals(other.getPersonalIDCode()))) &&
            ((this.authorizedDateTime==null && other.getAuthorizedDateTime()==null) || 
             (this.authorizedDateTime!=null &&
              this.authorizedDateTime.equals(other.getAuthorizedDateTime()))) &&
            ((this.processorResponse==null && other.getProcessorResponse()==null) || 
             (this.processorResponse!=null &&
              this.processorResponse.equals(other.getProcessorResponse()))) &&
            ((this.bmlAccountNumber==null && other.getBmlAccountNumber()==null) || 
             (this.bmlAccountNumber!=null &&
              this.bmlAccountNumber.equals(other.getBmlAccountNumber()))) &&
            ((this.authFactorCode==null && other.getAuthFactorCode()==null) || 
             (this.authFactorCode!=null &&
              this.authFactorCode.equals(other.getAuthFactorCode()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.fundingTotals==null && other.getFundingTotals()==null) || 
             (this.fundingTotals!=null &&
              this.fundingTotals.equals(other.getFundingTotals()))) &&
            ((this.fxQuoteID==null && other.getFxQuoteID()==null) || 
             (this.fxQuoteID!=null &&
              this.fxQuoteID.equals(other.getFxQuoteID()))) &&
            ((this.fxQuoteRate==null && other.getFxQuoteRate()==null) || 
             (this.fxQuoteRate!=null &&
              this.fxQuoteRate.equals(other.getFxQuoteRate()))) &&
            ((this.fxQuoteType==null && other.getFxQuoteType()==null) || 
             (this.fxQuoteType!=null &&
              this.fxQuoteType.equals(other.getFxQuoteType()))) &&
            ((this.fxQuoteExpirationDateTime==null && other.getFxQuoteExpirationDateTime()==null) || 
             (this.fxQuoteExpirationDateTime!=null &&
              this.fxQuoteExpirationDateTime.equals(other.getFxQuoteExpirationDateTime()))) &&
            ((this.authRecord==null && other.getAuthRecord()==null) || 
             (this.authRecord!=null &&
              this.authRecord.equals(other.getAuthRecord()))) &&
            ((this.merchantAdviceCode==null && other.getMerchantAdviceCode()==null) || 
             (this.merchantAdviceCode!=null &&
              this.merchantAdviceCode.equals(other.getMerchantAdviceCode()))) &&
            ((this.merchantAdviceCodeRaw==null && other.getMerchantAdviceCodeRaw()==null) || 
             (this.merchantAdviceCodeRaw!=null &&
              this.merchantAdviceCodeRaw.equals(other.getMerchantAdviceCodeRaw()))) &&
            ((this.cavvResponseCode==null && other.getCavvResponseCode()==null) || 
             (this.cavvResponseCode!=null &&
              this.cavvResponseCode.equals(other.getCavvResponseCode()))) &&
            ((this.cavvResponseCodeRaw==null && other.getCavvResponseCodeRaw()==null) || 
             (this.cavvResponseCodeRaw!=null &&
              this.cavvResponseCodeRaw.equals(other.getCavvResponseCodeRaw()))) &&
            ((this.authenticationXID==null && other.getAuthenticationXID()==null) || 
             (this.authenticationXID!=null &&
              this.authenticationXID.equals(other.getAuthenticationXID()))) &&
            ((this.authorizationXID==null && other.getAuthorizationXID()==null) || 
             (this.authorizationXID!=null &&
              this.authorizationXID.equals(other.getAuthorizationXID()))) &&
            ((this.processorCardType==null && other.getProcessorCardType()==null) || 
             (this.processorCardType!=null &&
              this.processorCardType.equals(other.getProcessorCardType()))) &&
            ((this.accountBalance==null && other.getAccountBalance()==null) || 
             (this.accountBalance!=null &&
              this.accountBalance.equals(other.getAccountBalance()))) &&
            ((this.forwardCode==null && other.getForwardCode()==null) || 
             (this.forwardCode!=null &&
              this.forwardCode.equals(other.getForwardCode()))) &&
            ((this.enhancedDataEnabled==null && other.getEnhancedDataEnabled()==null) || 
             (this.enhancedDataEnabled!=null &&
              this.enhancedDataEnabled.equals(other.getEnhancedDataEnabled()))) &&
            ((this.referralResponseNumber==null && other.getReferralResponseNumber()==null) || 
             (this.referralResponseNumber!=null &&
              this.referralResponseNumber.equals(other.getReferralResponseNumber()))) &&
            ((this.subResponseCode==null && other.getSubResponseCode()==null) || 
             (this.subResponseCode!=null &&
              this.subResponseCode.equals(other.getSubResponseCode()))) &&
            ((this.approvedAmount==null && other.getApprovedAmount()==null) || 
             (this.approvedAmount!=null &&
              this.approvedAmount.equals(other.getApprovedAmount()))) &&
            ((this.creditLine==null && other.getCreditLine()==null) || 
             (this.creditLine!=null &&
              this.creditLine.equals(other.getCreditLine()))) &&
            ((this.approvedTerms==null && other.getApprovedTerms()==null) || 
             (this.approvedTerms!=null &&
              this.approvedTerms.equals(other.getApprovedTerms()))) &&
            ((this.paymentNetworkTransactionID==null && other.getPaymentNetworkTransactionID()==null) || 
             (this.paymentNetworkTransactionID!=null &&
              this.paymentNetworkTransactionID.equals(other.getPaymentNetworkTransactionID()))) &&
            ((this.cardCategory==null && other.getCardCategory()==null) || 
             (this.cardCategory!=null &&
              this.cardCategory.equals(other.getCardCategory())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getReasonCode() != null) {
            _hashCode += getReasonCode().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getAuthorizationCode() != null) {
            _hashCode += getAuthorizationCode().hashCode();
        }
        if (getAvsCode() != null) {
            _hashCode += getAvsCode().hashCode();
        }
        if (getAvsCodeRaw() != null) {
            _hashCode += getAvsCodeRaw().hashCode();
        }
        if (getCvCode() != null) {
            _hashCode += getCvCode().hashCode();
        }
        if (getCvCodeRaw() != null) {
            _hashCode += getCvCodeRaw().hashCode();
        }
        if (getPersonalIDCode() != null) {
            _hashCode += getPersonalIDCode().hashCode();
        }
        if (getAuthorizedDateTime() != null) {
            _hashCode += getAuthorizedDateTime().hashCode();
        }
        if (getProcessorResponse() != null) {
            _hashCode += getProcessorResponse().hashCode();
        }
        if (getBmlAccountNumber() != null) {
            _hashCode += getBmlAccountNumber().hashCode();
        }
        if (getAuthFactorCode() != null) {
            _hashCode += getAuthFactorCode().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getFundingTotals() != null) {
            _hashCode += getFundingTotals().hashCode();
        }
        if (getFxQuoteID() != null) {
            _hashCode += getFxQuoteID().hashCode();
        }
        if (getFxQuoteRate() != null) {
            _hashCode += getFxQuoteRate().hashCode();
        }
        if (getFxQuoteType() != null) {
            _hashCode += getFxQuoteType().hashCode();
        }
        if (getFxQuoteExpirationDateTime() != null) {
            _hashCode += getFxQuoteExpirationDateTime().hashCode();
        }
        if (getAuthRecord() != null) {
            _hashCode += getAuthRecord().hashCode();
        }
        if (getMerchantAdviceCode() != null) {
            _hashCode += getMerchantAdviceCode().hashCode();
        }
        if (getMerchantAdviceCodeRaw() != null) {
            _hashCode += getMerchantAdviceCodeRaw().hashCode();
        }
        if (getCavvResponseCode() != null) {
            _hashCode += getCavvResponseCode().hashCode();
        }
        if (getCavvResponseCodeRaw() != null) {
            _hashCode += getCavvResponseCodeRaw().hashCode();
        }
        if (getAuthenticationXID() != null) {
            _hashCode += getAuthenticationXID().hashCode();
        }
        if (getAuthorizationXID() != null) {
            _hashCode += getAuthorizationXID().hashCode();
        }
        if (getProcessorCardType() != null) {
            _hashCode += getProcessorCardType().hashCode();
        }
        if (getAccountBalance() != null) {
            _hashCode += getAccountBalance().hashCode();
        }
        if (getForwardCode() != null) {
            _hashCode += getForwardCode().hashCode();
        }
        if (getEnhancedDataEnabled() != null) {
            _hashCode += getEnhancedDataEnabled().hashCode();
        }
        if (getReferralResponseNumber() != null) {
            _hashCode += getReferralResponseNumber().hashCode();
        }
        if (getSubResponseCode() != null) {
            _hashCode += getSubResponseCode().hashCode();
        }
        if (getApprovedAmount() != null) {
            _hashCode += getApprovedAmount().hashCode();
        }
        if (getCreditLine() != null) {
            _hashCode += getCreditLine().hashCode();
        }
        if (getApprovedTerms() != null) {
            _hashCode += getApprovedTerms().hashCode();
        }
        if (getPaymentNetworkTransactionID() != null) {
            _hashCode += getPaymentNetworkTransactionID().hashCode();
        }
        if (getCardCategory() != null) {
            _hashCode += getCardCategory().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CCAuthReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authorizationCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "avsCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsCodeRaw");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "avsCodeRaw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cvCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvCodeRaw");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cvCodeRaw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("personalIDCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "personalIDCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authorizedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processorResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "processorResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bmlAccountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bmlAccountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authFactorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authFactorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reconciliationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reconciliationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fundingTotals");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fundingTotals"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FundingTotals"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteRate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteExpirationDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteExpirationDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantAdviceCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantAdviceCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantAdviceCodeRaw");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantAdviceCodeRaw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cavvResponseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cavvResponseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cavvResponseCodeRaw");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cavvResponseCodeRaw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticationXID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authenticationXID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationXID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authorizationXID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processorCardType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "processorCardType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "accountBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forwardCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "forwardCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enhancedDataEnabled");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "enhancedDataEnabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("referralResponseNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "referralResponseNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subResponseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "subResponseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("approvedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "approvedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditLine");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "creditLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("approvedTerms");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "approvedTerms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentNetworkTransactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentNetworkTransactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardCategory");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardCategory"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
