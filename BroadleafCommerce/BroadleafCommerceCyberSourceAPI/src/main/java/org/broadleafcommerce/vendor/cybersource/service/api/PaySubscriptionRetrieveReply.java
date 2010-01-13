/**
 * PaySubscriptionRetrieveReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PaySubscriptionRetrieveReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String approvalRequired;

    private java.lang.String automaticRenew;

    private java.lang.String cardAccountNumber;

    private java.lang.String cardExpirationMonth;

    private java.lang.String cardExpirationYear;

    private java.lang.String cardIssueNumber;

    private java.lang.String cardStartMonth;

    private java.lang.String cardStartYear;

    private java.lang.String cardType;

    private java.lang.String checkAccountNumber;

    private java.lang.String checkAccountType;

    private java.lang.String checkBankTransitNumber;

    private java.lang.String checkSecCode;

    private java.lang.String checkAuthenticateID;

    private java.lang.String city;

    private java.lang.String comments;

    private java.lang.String companyName;

    private java.lang.String country;

    private java.lang.String currency;

    private java.lang.String customerAccountID;

    private java.lang.String email;

    private java.lang.String endDate;

    private java.lang.String firstName;

    private java.lang.String frequency;

    private java.lang.String lastName;

    private java.lang.String merchantReferenceCode;

    private java.lang.String paymentMethod;

    private java.lang.String paymentsRemaining;

    private java.lang.String phoneNumber;

    private java.lang.String postalCode;

    private java.lang.String recurringAmount;

    private java.lang.String setupAmount;

    private java.lang.String startDate;

    private java.lang.String state;

    private java.lang.String status;

    private java.lang.String street1;

    private java.lang.String street2;

    private java.lang.String subscriptionID;

    private java.lang.String title;

    private java.lang.String totalPayments;

    private java.lang.String shipToFirstName;

    private java.lang.String shipToLastName;

    private java.lang.String shipToStreet1;

    private java.lang.String shipToStreet2;

    private java.lang.String shipToCity;

    private java.lang.String shipToState;

    private java.lang.String shipToPostalCode;

    private java.lang.String shipToCompany;

    private java.lang.String shipToCountry;

    private java.lang.String billPayment;

    private java.lang.String merchantDefinedDataField1;

    private java.lang.String merchantDefinedDataField2;

    private java.lang.String merchantDefinedDataField3;

    private java.lang.String merchantDefinedDataField4;

    private java.lang.String merchantSecureDataField1;

    private java.lang.String merchantSecureDataField2;

    private java.lang.String merchantSecureDataField3;

    private java.lang.String merchantSecureDataField4;

    public PaySubscriptionRetrieveReply() {
    }

    public PaySubscriptionRetrieveReply(
           java.math.BigInteger reasonCode,
           java.lang.String approvalRequired,
           java.lang.String automaticRenew,
           java.lang.String cardAccountNumber,
           java.lang.String cardExpirationMonth,
           java.lang.String cardExpirationYear,
           java.lang.String cardIssueNumber,
           java.lang.String cardStartMonth,
           java.lang.String cardStartYear,
           java.lang.String cardType,
           java.lang.String checkAccountNumber,
           java.lang.String checkAccountType,
           java.lang.String checkBankTransitNumber,
           java.lang.String checkSecCode,
           java.lang.String checkAuthenticateID,
           java.lang.String city,
           java.lang.String comments,
           java.lang.String companyName,
           java.lang.String country,
           java.lang.String currency,
           java.lang.String customerAccountID,
           java.lang.String email,
           java.lang.String endDate,
           java.lang.String firstName,
           java.lang.String frequency,
           java.lang.String lastName,
           java.lang.String merchantReferenceCode,
           java.lang.String paymentMethod,
           java.lang.String paymentsRemaining,
           java.lang.String phoneNumber,
           java.lang.String postalCode,
           java.lang.String recurringAmount,
           java.lang.String setupAmount,
           java.lang.String startDate,
           java.lang.String state,
           java.lang.String status,
           java.lang.String street1,
           java.lang.String street2,
           java.lang.String subscriptionID,
           java.lang.String title,
           java.lang.String totalPayments,
           java.lang.String shipToFirstName,
           java.lang.String shipToLastName,
           java.lang.String shipToStreet1,
           java.lang.String shipToStreet2,
           java.lang.String shipToCity,
           java.lang.String shipToState,
           java.lang.String shipToPostalCode,
           java.lang.String shipToCompany,
           java.lang.String shipToCountry,
           java.lang.String billPayment,
           java.lang.String merchantDefinedDataField1,
           java.lang.String merchantDefinedDataField2,
           java.lang.String merchantDefinedDataField3,
           java.lang.String merchantDefinedDataField4,
           java.lang.String merchantSecureDataField1,
           java.lang.String merchantSecureDataField2,
           java.lang.String merchantSecureDataField3,
           java.lang.String merchantSecureDataField4) {
           this.reasonCode = reasonCode;
           this.approvalRequired = approvalRequired;
           this.automaticRenew = automaticRenew;
           this.cardAccountNumber = cardAccountNumber;
           this.cardExpirationMonth = cardExpirationMonth;
           this.cardExpirationYear = cardExpirationYear;
           this.cardIssueNumber = cardIssueNumber;
           this.cardStartMonth = cardStartMonth;
           this.cardStartYear = cardStartYear;
           this.cardType = cardType;
           this.checkAccountNumber = checkAccountNumber;
           this.checkAccountType = checkAccountType;
           this.checkBankTransitNumber = checkBankTransitNumber;
           this.checkSecCode = checkSecCode;
           this.checkAuthenticateID = checkAuthenticateID;
           this.city = city;
           this.comments = comments;
           this.companyName = companyName;
           this.country = country;
           this.currency = currency;
           this.customerAccountID = customerAccountID;
           this.email = email;
           this.endDate = endDate;
           this.firstName = firstName;
           this.frequency = frequency;
           this.lastName = lastName;
           this.merchantReferenceCode = merchantReferenceCode;
           this.paymentMethod = paymentMethod;
           this.paymentsRemaining = paymentsRemaining;
           this.phoneNumber = phoneNumber;
           this.postalCode = postalCode;
           this.recurringAmount = recurringAmount;
           this.setupAmount = setupAmount;
           this.startDate = startDate;
           this.state = state;
           this.status = status;
           this.street1 = street1;
           this.street2 = street2;
           this.subscriptionID = subscriptionID;
           this.title = title;
           this.totalPayments = totalPayments;
           this.shipToFirstName = shipToFirstName;
           this.shipToLastName = shipToLastName;
           this.shipToStreet1 = shipToStreet1;
           this.shipToStreet2 = shipToStreet2;
           this.shipToCity = shipToCity;
           this.shipToState = shipToState;
           this.shipToPostalCode = shipToPostalCode;
           this.shipToCompany = shipToCompany;
           this.shipToCountry = shipToCountry;
           this.billPayment = billPayment;
           this.merchantDefinedDataField1 = merchantDefinedDataField1;
           this.merchantDefinedDataField2 = merchantDefinedDataField2;
           this.merchantDefinedDataField3 = merchantDefinedDataField3;
           this.merchantDefinedDataField4 = merchantDefinedDataField4;
           this.merchantSecureDataField1 = merchantSecureDataField1;
           this.merchantSecureDataField2 = merchantSecureDataField2;
           this.merchantSecureDataField3 = merchantSecureDataField3;
           this.merchantSecureDataField4 = merchantSecureDataField4;
    }


    /**
     * Gets the reasonCode value for this PaySubscriptionRetrieveReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PaySubscriptionRetrieveReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the approvalRequired value for this PaySubscriptionRetrieveReply.
     * 
     * @return approvalRequired
     */
    public java.lang.String getApprovalRequired() {
        return approvalRequired;
    }


    /**
     * Sets the approvalRequired value for this PaySubscriptionRetrieveReply.
     * 
     * @param approvalRequired
     */
    public void setApprovalRequired(java.lang.String approvalRequired) {
        this.approvalRequired = approvalRequired;
    }


    /**
     * Gets the automaticRenew value for this PaySubscriptionRetrieveReply.
     * 
     * @return automaticRenew
     */
    public java.lang.String getAutomaticRenew() {
        return automaticRenew;
    }


    /**
     * Sets the automaticRenew value for this PaySubscriptionRetrieveReply.
     * 
     * @param automaticRenew
     */
    public void setAutomaticRenew(java.lang.String automaticRenew) {
        this.automaticRenew = automaticRenew;
    }


    /**
     * Gets the cardAccountNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @return cardAccountNumber
     */
    public java.lang.String getCardAccountNumber() {
        return cardAccountNumber;
    }


    /**
     * Sets the cardAccountNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @param cardAccountNumber
     */
    public void setCardAccountNumber(java.lang.String cardAccountNumber) {
        this.cardAccountNumber = cardAccountNumber;
    }


    /**
     * Gets the cardExpirationMonth value for this PaySubscriptionRetrieveReply.
     * 
     * @return cardExpirationMonth
     */
    public java.lang.String getCardExpirationMonth() {
        return cardExpirationMonth;
    }


    /**
     * Sets the cardExpirationMonth value for this PaySubscriptionRetrieveReply.
     * 
     * @param cardExpirationMonth
     */
    public void setCardExpirationMonth(java.lang.String cardExpirationMonth) {
        this.cardExpirationMonth = cardExpirationMonth;
    }


    /**
     * Gets the cardExpirationYear value for this PaySubscriptionRetrieveReply.
     * 
     * @return cardExpirationYear
     */
    public java.lang.String getCardExpirationYear() {
        return cardExpirationYear;
    }


    /**
     * Sets the cardExpirationYear value for this PaySubscriptionRetrieveReply.
     * 
     * @param cardExpirationYear
     */
    public void setCardExpirationYear(java.lang.String cardExpirationYear) {
        this.cardExpirationYear = cardExpirationYear;
    }


    /**
     * Gets the cardIssueNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @return cardIssueNumber
     */
    public java.lang.String getCardIssueNumber() {
        return cardIssueNumber;
    }


    /**
     * Sets the cardIssueNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @param cardIssueNumber
     */
    public void setCardIssueNumber(java.lang.String cardIssueNumber) {
        this.cardIssueNumber = cardIssueNumber;
    }


    /**
     * Gets the cardStartMonth value for this PaySubscriptionRetrieveReply.
     * 
     * @return cardStartMonth
     */
    public java.lang.String getCardStartMonth() {
        return cardStartMonth;
    }


    /**
     * Sets the cardStartMonth value for this PaySubscriptionRetrieveReply.
     * 
     * @param cardStartMonth
     */
    public void setCardStartMonth(java.lang.String cardStartMonth) {
        this.cardStartMonth = cardStartMonth;
    }


    /**
     * Gets the cardStartYear value for this PaySubscriptionRetrieveReply.
     * 
     * @return cardStartYear
     */
    public java.lang.String getCardStartYear() {
        return cardStartYear;
    }


    /**
     * Sets the cardStartYear value for this PaySubscriptionRetrieveReply.
     * 
     * @param cardStartYear
     */
    public void setCardStartYear(java.lang.String cardStartYear) {
        this.cardStartYear = cardStartYear;
    }


    /**
     * Gets the cardType value for this PaySubscriptionRetrieveReply.
     * 
     * @return cardType
     */
    public java.lang.String getCardType() {
        return cardType;
    }


    /**
     * Sets the cardType value for this PaySubscriptionRetrieveReply.
     * 
     * @param cardType
     */
    public void setCardType(java.lang.String cardType) {
        this.cardType = cardType;
    }


    /**
     * Gets the checkAccountNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @return checkAccountNumber
     */
    public java.lang.String getCheckAccountNumber() {
        return checkAccountNumber;
    }


    /**
     * Sets the checkAccountNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @param checkAccountNumber
     */
    public void setCheckAccountNumber(java.lang.String checkAccountNumber) {
        this.checkAccountNumber = checkAccountNumber;
    }


    /**
     * Gets the checkAccountType value for this PaySubscriptionRetrieveReply.
     * 
     * @return checkAccountType
     */
    public java.lang.String getCheckAccountType() {
        return checkAccountType;
    }


    /**
     * Sets the checkAccountType value for this PaySubscriptionRetrieveReply.
     * 
     * @param checkAccountType
     */
    public void setCheckAccountType(java.lang.String checkAccountType) {
        this.checkAccountType = checkAccountType;
    }


    /**
     * Gets the checkBankTransitNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @return checkBankTransitNumber
     */
    public java.lang.String getCheckBankTransitNumber() {
        return checkBankTransitNumber;
    }


    /**
     * Sets the checkBankTransitNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @param checkBankTransitNumber
     */
    public void setCheckBankTransitNumber(java.lang.String checkBankTransitNumber) {
        this.checkBankTransitNumber = checkBankTransitNumber;
    }


    /**
     * Gets the checkSecCode value for this PaySubscriptionRetrieveReply.
     * 
     * @return checkSecCode
     */
    public java.lang.String getCheckSecCode() {
        return checkSecCode;
    }


    /**
     * Sets the checkSecCode value for this PaySubscriptionRetrieveReply.
     * 
     * @param checkSecCode
     */
    public void setCheckSecCode(java.lang.String checkSecCode) {
        this.checkSecCode = checkSecCode;
    }


    /**
     * Gets the checkAuthenticateID value for this PaySubscriptionRetrieveReply.
     * 
     * @return checkAuthenticateID
     */
    public java.lang.String getCheckAuthenticateID() {
        return checkAuthenticateID;
    }


    /**
     * Sets the checkAuthenticateID value for this PaySubscriptionRetrieveReply.
     * 
     * @param checkAuthenticateID
     */
    public void setCheckAuthenticateID(java.lang.String checkAuthenticateID) {
        this.checkAuthenticateID = checkAuthenticateID;
    }


    /**
     * Gets the city value for this PaySubscriptionRetrieveReply.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }


    /**
     * Sets the city value for this PaySubscriptionRetrieveReply.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }


    /**
     * Gets the comments value for this PaySubscriptionRetrieveReply.
     * 
     * @return comments
     */
    public java.lang.String getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this PaySubscriptionRetrieveReply.
     * 
     * @param comments
     */
    public void setComments(java.lang.String comments) {
        this.comments = comments;
    }


    /**
     * Gets the companyName value for this PaySubscriptionRetrieveReply.
     * 
     * @return companyName
     */
    public java.lang.String getCompanyName() {
        return companyName;
    }


    /**
     * Sets the companyName value for this PaySubscriptionRetrieveReply.
     * 
     * @param companyName
     */
    public void setCompanyName(java.lang.String companyName) {
        this.companyName = companyName;
    }


    /**
     * Gets the country value for this PaySubscriptionRetrieveReply.
     * 
     * @return country
     */
    public java.lang.String getCountry() {
        return country;
    }


    /**
     * Sets the country value for this PaySubscriptionRetrieveReply.
     * 
     * @param country
     */
    public void setCountry(java.lang.String country) {
        this.country = country;
    }


    /**
     * Gets the currency value for this PaySubscriptionRetrieveReply.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this PaySubscriptionRetrieveReply.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the customerAccountID value for this PaySubscriptionRetrieveReply.
     * 
     * @return customerAccountID
     */
    public java.lang.String getCustomerAccountID() {
        return customerAccountID;
    }


    /**
     * Sets the customerAccountID value for this PaySubscriptionRetrieveReply.
     * 
     * @param customerAccountID
     */
    public void setCustomerAccountID(java.lang.String customerAccountID) {
        this.customerAccountID = customerAccountID;
    }


    /**
     * Gets the email value for this PaySubscriptionRetrieveReply.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this PaySubscriptionRetrieveReply.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the endDate value for this PaySubscriptionRetrieveReply.
     * 
     * @return endDate
     */
    public java.lang.String getEndDate() {
        return endDate;
    }


    /**
     * Sets the endDate value for this PaySubscriptionRetrieveReply.
     * 
     * @param endDate
     */
    public void setEndDate(java.lang.String endDate) {
        this.endDate = endDate;
    }


    /**
     * Gets the firstName value for this PaySubscriptionRetrieveReply.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this PaySubscriptionRetrieveReply.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the frequency value for this PaySubscriptionRetrieveReply.
     * 
     * @return frequency
     */
    public java.lang.String getFrequency() {
        return frequency;
    }


    /**
     * Sets the frequency value for this PaySubscriptionRetrieveReply.
     * 
     * @param frequency
     */
    public void setFrequency(java.lang.String frequency) {
        this.frequency = frequency;
    }


    /**
     * Gets the lastName value for this PaySubscriptionRetrieveReply.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this PaySubscriptionRetrieveReply.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the merchantReferenceCode value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantReferenceCode
     */
    public java.lang.String getMerchantReferenceCode() {
        return merchantReferenceCode;
    }


    /**
     * Sets the merchantReferenceCode value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantReferenceCode
     */
    public void setMerchantReferenceCode(java.lang.String merchantReferenceCode) {
        this.merchantReferenceCode = merchantReferenceCode;
    }


    /**
     * Gets the paymentMethod value for this PaySubscriptionRetrieveReply.
     * 
     * @return paymentMethod
     */
    public java.lang.String getPaymentMethod() {
        return paymentMethod;
    }


    /**
     * Sets the paymentMethod value for this PaySubscriptionRetrieveReply.
     * 
     * @param paymentMethod
     */
    public void setPaymentMethod(java.lang.String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    /**
     * Gets the paymentsRemaining value for this PaySubscriptionRetrieveReply.
     * 
     * @return paymentsRemaining
     */
    public java.lang.String getPaymentsRemaining() {
        return paymentsRemaining;
    }


    /**
     * Sets the paymentsRemaining value for this PaySubscriptionRetrieveReply.
     * 
     * @param paymentsRemaining
     */
    public void setPaymentsRemaining(java.lang.String paymentsRemaining) {
        this.paymentsRemaining = paymentsRemaining;
    }


    /**
     * Gets the phoneNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @return phoneNumber
     */
    public java.lang.String getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Sets the phoneNumber value for this PaySubscriptionRetrieveReply.
     * 
     * @param phoneNumber
     */
    public void setPhoneNumber(java.lang.String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    /**
     * Gets the postalCode value for this PaySubscriptionRetrieveReply.
     * 
     * @return postalCode
     */
    public java.lang.String getPostalCode() {
        return postalCode;
    }


    /**
     * Sets the postalCode value for this PaySubscriptionRetrieveReply.
     * 
     * @param postalCode
     */
    public void setPostalCode(java.lang.String postalCode) {
        this.postalCode = postalCode;
    }


    /**
     * Gets the recurringAmount value for this PaySubscriptionRetrieveReply.
     * 
     * @return recurringAmount
     */
    public java.lang.String getRecurringAmount() {
        return recurringAmount;
    }


    /**
     * Sets the recurringAmount value for this PaySubscriptionRetrieveReply.
     * 
     * @param recurringAmount
     */
    public void setRecurringAmount(java.lang.String recurringAmount) {
        this.recurringAmount = recurringAmount;
    }


    /**
     * Gets the setupAmount value for this PaySubscriptionRetrieveReply.
     * 
     * @return setupAmount
     */
    public java.lang.String getSetupAmount() {
        return setupAmount;
    }


    /**
     * Sets the setupAmount value for this PaySubscriptionRetrieveReply.
     * 
     * @param setupAmount
     */
    public void setSetupAmount(java.lang.String setupAmount) {
        this.setupAmount = setupAmount;
    }


    /**
     * Gets the startDate value for this PaySubscriptionRetrieveReply.
     * 
     * @return startDate
     */
    public java.lang.String getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate value for this PaySubscriptionRetrieveReply.
     * 
     * @param startDate
     */
    public void setStartDate(java.lang.String startDate) {
        this.startDate = startDate;
    }


    /**
     * Gets the state value for this PaySubscriptionRetrieveReply.
     * 
     * @return state
     */
    public java.lang.String getState() {
        return state;
    }


    /**
     * Sets the state value for this PaySubscriptionRetrieveReply.
     * 
     * @param state
     */
    public void setState(java.lang.String state) {
        this.state = state;
    }


    /**
     * Gets the status value for this PaySubscriptionRetrieveReply.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this PaySubscriptionRetrieveReply.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the street1 value for this PaySubscriptionRetrieveReply.
     * 
     * @return street1
     */
    public java.lang.String getStreet1() {
        return street1;
    }


    /**
     * Sets the street1 value for this PaySubscriptionRetrieveReply.
     * 
     * @param street1
     */
    public void setStreet1(java.lang.String street1) {
        this.street1 = street1;
    }


    /**
     * Gets the street2 value for this PaySubscriptionRetrieveReply.
     * 
     * @return street2
     */
    public java.lang.String getStreet2() {
        return street2;
    }


    /**
     * Sets the street2 value for this PaySubscriptionRetrieveReply.
     * 
     * @param street2
     */
    public void setStreet2(java.lang.String street2) {
        this.street2 = street2;
    }


    /**
     * Gets the subscriptionID value for this PaySubscriptionRetrieveReply.
     * 
     * @return subscriptionID
     */
    public java.lang.String getSubscriptionID() {
        return subscriptionID;
    }


    /**
     * Sets the subscriptionID value for this PaySubscriptionRetrieveReply.
     * 
     * @param subscriptionID
     */
    public void setSubscriptionID(java.lang.String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }


    /**
     * Gets the title value for this PaySubscriptionRetrieveReply.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this PaySubscriptionRetrieveReply.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the totalPayments value for this PaySubscriptionRetrieveReply.
     * 
     * @return totalPayments
     */
    public java.lang.String getTotalPayments() {
        return totalPayments;
    }


    /**
     * Sets the totalPayments value for this PaySubscriptionRetrieveReply.
     * 
     * @param totalPayments
     */
    public void setTotalPayments(java.lang.String totalPayments) {
        this.totalPayments = totalPayments;
    }


    /**
     * Gets the shipToFirstName value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToFirstName
     */
    public java.lang.String getShipToFirstName() {
        return shipToFirstName;
    }


    /**
     * Sets the shipToFirstName value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToFirstName
     */
    public void setShipToFirstName(java.lang.String shipToFirstName) {
        this.shipToFirstName = shipToFirstName;
    }


    /**
     * Gets the shipToLastName value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToLastName
     */
    public java.lang.String getShipToLastName() {
        return shipToLastName;
    }


    /**
     * Sets the shipToLastName value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToLastName
     */
    public void setShipToLastName(java.lang.String shipToLastName) {
        this.shipToLastName = shipToLastName;
    }


    /**
     * Gets the shipToStreet1 value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToStreet1
     */
    public java.lang.String getShipToStreet1() {
        return shipToStreet1;
    }


    /**
     * Sets the shipToStreet1 value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToStreet1
     */
    public void setShipToStreet1(java.lang.String shipToStreet1) {
        this.shipToStreet1 = shipToStreet1;
    }


    /**
     * Gets the shipToStreet2 value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToStreet2
     */
    public java.lang.String getShipToStreet2() {
        return shipToStreet2;
    }


    /**
     * Sets the shipToStreet2 value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToStreet2
     */
    public void setShipToStreet2(java.lang.String shipToStreet2) {
        this.shipToStreet2 = shipToStreet2;
    }


    /**
     * Gets the shipToCity value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToCity
     */
    public java.lang.String getShipToCity() {
        return shipToCity;
    }


    /**
     * Sets the shipToCity value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToCity
     */
    public void setShipToCity(java.lang.String shipToCity) {
        this.shipToCity = shipToCity;
    }


    /**
     * Gets the shipToState value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToState
     */
    public java.lang.String getShipToState() {
        return shipToState;
    }


    /**
     * Sets the shipToState value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToState
     */
    public void setShipToState(java.lang.String shipToState) {
        this.shipToState = shipToState;
    }


    /**
     * Gets the shipToPostalCode value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToPostalCode
     */
    public java.lang.String getShipToPostalCode() {
        return shipToPostalCode;
    }


    /**
     * Sets the shipToPostalCode value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToPostalCode
     */
    public void setShipToPostalCode(java.lang.String shipToPostalCode) {
        this.shipToPostalCode = shipToPostalCode;
    }


    /**
     * Gets the shipToCompany value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToCompany
     */
    public java.lang.String getShipToCompany() {
        return shipToCompany;
    }


    /**
     * Sets the shipToCompany value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToCompany
     */
    public void setShipToCompany(java.lang.String shipToCompany) {
        this.shipToCompany = shipToCompany;
    }


    /**
     * Gets the shipToCountry value for this PaySubscriptionRetrieveReply.
     * 
     * @return shipToCountry
     */
    public java.lang.String getShipToCountry() {
        return shipToCountry;
    }


    /**
     * Sets the shipToCountry value for this PaySubscriptionRetrieveReply.
     * 
     * @param shipToCountry
     */
    public void setShipToCountry(java.lang.String shipToCountry) {
        this.shipToCountry = shipToCountry;
    }


    /**
     * Gets the billPayment value for this PaySubscriptionRetrieveReply.
     * 
     * @return billPayment
     */
    public java.lang.String getBillPayment() {
        return billPayment;
    }


    /**
     * Sets the billPayment value for this PaySubscriptionRetrieveReply.
     * 
     * @param billPayment
     */
    public void setBillPayment(java.lang.String billPayment) {
        this.billPayment = billPayment;
    }


    /**
     * Gets the merchantDefinedDataField1 value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantDefinedDataField1
     */
    public java.lang.String getMerchantDefinedDataField1() {
        return merchantDefinedDataField1;
    }


    /**
     * Sets the merchantDefinedDataField1 value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantDefinedDataField1
     */
    public void setMerchantDefinedDataField1(java.lang.String merchantDefinedDataField1) {
        this.merchantDefinedDataField1 = merchantDefinedDataField1;
    }


    /**
     * Gets the merchantDefinedDataField2 value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantDefinedDataField2
     */
    public java.lang.String getMerchantDefinedDataField2() {
        return merchantDefinedDataField2;
    }


    /**
     * Sets the merchantDefinedDataField2 value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantDefinedDataField2
     */
    public void setMerchantDefinedDataField2(java.lang.String merchantDefinedDataField2) {
        this.merchantDefinedDataField2 = merchantDefinedDataField2;
    }


    /**
     * Gets the merchantDefinedDataField3 value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantDefinedDataField3
     */
    public java.lang.String getMerchantDefinedDataField3() {
        return merchantDefinedDataField3;
    }


    /**
     * Sets the merchantDefinedDataField3 value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantDefinedDataField3
     */
    public void setMerchantDefinedDataField3(java.lang.String merchantDefinedDataField3) {
        this.merchantDefinedDataField3 = merchantDefinedDataField3;
    }


    /**
     * Gets the merchantDefinedDataField4 value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantDefinedDataField4
     */
    public java.lang.String getMerchantDefinedDataField4() {
        return merchantDefinedDataField4;
    }


    /**
     * Sets the merchantDefinedDataField4 value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantDefinedDataField4
     */
    public void setMerchantDefinedDataField4(java.lang.String merchantDefinedDataField4) {
        this.merchantDefinedDataField4 = merchantDefinedDataField4;
    }


    /**
     * Gets the merchantSecureDataField1 value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantSecureDataField1
     */
    public java.lang.String getMerchantSecureDataField1() {
        return merchantSecureDataField1;
    }


    /**
     * Sets the merchantSecureDataField1 value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantSecureDataField1
     */
    public void setMerchantSecureDataField1(java.lang.String merchantSecureDataField1) {
        this.merchantSecureDataField1 = merchantSecureDataField1;
    }


    /**
     * Gets the merchantSecureDataField2 value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantSecureDataField2
     */
    public java.lang.String getMerchantSecureDataField2() {
        return merchantSecureDataField2;
    }


    /**
     * Sets the merchantSecureDataField2 value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantSecureDataField2
     */
    public void setMerchantSecureDataField2(java.lang.String merchantSecureDataField2) {
        this.merchantSecureDataField2 = merchantSecureDataField2;
    }


    /**
     * Gets the merchantSecureDataField3 value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantSecureDataField3
     */
    public java.lang.String getMerchantSecureDataField3() {
        return merchantSecureDataField3;
    }


    /**
     * Sets the merchantSecureDataField3 value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantSecureDataField3
     */
    public void setMerchantSecureDataField3(java.lang.String merchantSecureDataField3) {
        this.merchantSecureDataField3 = merchantSecureDataField3;
    }


    /**
     * Gets the merchantSecureDataField4 value for this PaySubscriptionRetrieveReply.
     * 
     * @return merchantSecureDataField4
     */
    public java.lang.String getMerchantSecureDataField4() {
        return merchantSecureDataField4;
    }


    /**
     * Sets the merchantSecureDataField4 value for this PaySubscriptionRetrieveReply.
     * 
     * @param merchantSecureDataField4
     */
    public void setMerchantSecureDataField4(java.lang.String merchantSecureDataField4) {
        this.merchantSecureDataField4 = merchantSecureDataField4;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PaySubscriptionRetrieveReply)) return false;
        PaySubscriptionRetrieveReply other = (PaySubscriptionRetrieveReply) obj;
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
            ((this.approvalRequired==null && other.getApprovalRequired()==null) || 
             (this.approvalRequired!=null &&
              this.approvalRequired.equals(other.getApprovalRequired()))) &&
            ((this.automaticRenew==null && other.getAutomaticRenew()==null) || 
             (this.automaticRenew!=null &&
              this.automaticRenew.equals(other.getAutomaticRenew()))) &&
            ((this.cardAccountNumber==null && other.getCardAccountNumber()==null) || 
             (this.cardAccountNumber!=null &&
              this.cardAccountNumber.equals(other.getCardAccountNumber()))) &&
            ((this.cardExpirationMonth==null && other.getCardExpirationMonth()==null) || 
             (this.cardExpirationMonth!=null &&
              this.cardExpirationMonth.equals(other.getCardExpirationMonth()))) &&
            ((this.cardExpirationYear==null && other.getCardExpirationYear()==null) || 
             (this.cardExpirationYear!=null &&
              this.cardExpirationYear.equals(other.getCardExpirationYear()))) &&
            ((this.cardIssueNumber==null && other.getCardIssueNumber()==null) || 
             (this.cardIssueNumber!=null &&
              this.cardIssueNumber.equals(other.getCardIssueNumber()))) &&
            ((this.cardStartMonth==null && other.getCardStartMonth()==null) || 
             (this.cardStartMonth!=null &&
              this.cardStartMonth.equals(other.getCardStartMonth()))) &&
            ((this.cardStartYear==null && other.getCardStartYear()==null) || 
             (this.cardStartYear!=null &&
              this.cardStartYear.equals(other.getCardStartYear()))) &&
            ((this.cardType==null && other.getCardType()==null) || 
             (this.cardType!=null &&
              this.cardType.equals(other.getCardType()))) &&
            ((this.checkAccountNumber==null && other.getCheckAccountNumber()==null) || 
             (this.checkAccountNumber!=null &&
              this.checkAccountNumber.equals(other.getCheckAccountNumber()))) &&
            ((this.checkAccountType==null && other.getCheckAccountType()==null) || 
             (this.checkAccountType!=null &&
              this.checkAccountType.equals(other.getCheckAccountType()))) &&
            ((this.checkBankTransitNumber==null && other.getCheckBankTransitNumber()==null) || 
             (this.checkBankTransitNumber!=null &&
              this.checkBankTransitNumber.equals(other.getCheckBankTransitNumber()))) &&
            ((this.checkSecCode==null && other.getCheckSecCode()==null) || 
             (this.checkSecCode!=null &&
              this.checkSecCode.equals(other.getCheckSecCode()))) &&
            ((this.checkAuthenticateID==null && other.getCheckAuthenticateID()==null) || 
             (this.checkAuthenticateID!=null &&
              this.checkAuthenticateID.equals(other.getCheckAuthenticateID()))) &&
            ((this.city==null && other.getCity()==null) || 
             (this.city!=null &&
              this.city.equals(other.getCity()))) &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              this.comments.equals(other.getComments()))) &&
            ((this.companyName==null && other.getCompanyName()==null) || 
             (this.companyName!=null &&
              this.companyName.equals(other.getCompanyName()))) &&
            ((this.country==null && other.getCountry()==null) || 
             (this.country!=null &&
              this.country.equals(other.getCountry()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.customerAccountID==null && other.getCustomerAccountID()==null) || 
             (this.customerAccountID!=null &&
              this.customerAccountID.equals(other.getCustomerAccountID()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.endDate==null && other.getEndDate()==null) || 
             (this.endDate!=null &&
              this.endDate.equals(other.getEndDate()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.frequency==null && other.getFrequency()==null) || 
             (this.frequency!=null &&
              this.frequency.equals(other.getFrequency()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.merchantReferenceCode==null && other.getMerchantReferenceCode()==null) || 
             (this.merchantReferenceCode!=null &&
              this.merchantReferenceCode.equals(other.getMerchantReferenceCode()))) &&
            ((this.paymentMethod==null && other.getPaymentMethod()==null) || 
             (this.paymentMethod!=null &&
              this.paymentMethod.equals(other.getPaymentMethod()))) &&
            ((this.paymentsRemaining==null && other.getPaymentsRemaining()==null) || 
             (this.paymentsRemaining!=null &&
              this.paymentsRemaining.equals(other.getPaymentsRemaining()))) &&
            ((this.phoneNumber==null && other.getPhoneNumber()==null) || 
             (this.phoneNumber!=null &&
              this.phoneNumber.equals(other.getPhoneNumber()))) &&
            ((this.postalCode==null && other.getPostalCode()==null) || 
             (this.postalCode!=null &&
              this.postalCode.equals(other.getPostalCode()))) &&
            ((this.recurringAmount==null && other.getRecurringAmount()==null) || 
             (this.recurringAmount!=null &&
              this.recurringAmount.equals(other.getRecurringAmount()))) &&
            ((this.setupAmount==null && other.getSetupAmount()==null) || 
             (this.setupAmount!=null &&
              this.setupAmount.equals(other.getSetupAmount()))) &&
            ((this.startDate==null && other.getStartDate()==null) || 
             (this.startDate!=null &&
              this.startDate.equals(other.getStartDate()))) &&
            ((this.state==null && other.getState()==null) || 
             (this.state!=null &&
              this.state.equals(other.getState()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.street1==null && other.getStreet1()==null) || 
             (this.street1!=null &&
              this.street1.equals(other.getStreet1()))) &&
            ((this.street2==null && other.getStreet2()==null) || 
             (this.street2!=null &&
              this.street2.equals(other.getStreet2()))) &&
            ((this.subscriptionID==null && other.getSubscriptionID()==null) || 
             (this.subscriptionID!=null &&
              this.subscriptionID.equals(other.getSubscriptionID()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.totalPayments==null && other.getTotalPayments()==null) || 
             (this.totalPayments!=null &&
              this.totalPayments.equals(other.getTotalPayments()))) &&
            ((this.shipToFirstName==null && other.getShipToFirstName()==null) || 
             (this.shipToFirstName!=null &&
              this.shipToFirstName.equals(other.getShipToFirstName()))) &&
            ((this.shipToLastName==null && other.getShipToLastName()==null) || 
             (this.shipToLastName!=null &&
              this.shipToLastName.equals(other.getShipToLastName()))) &&
            ((this.shipToStreet1==null && other.getShipToStreet1()==null) || 
             (this.shipToStreet1!=null &&
              this.shipToStreet1.equals(other.getShipToStreet1()))) &&
            ((this.shipToStreet2==null && other.getShipToStreet2()==null) || 
             (this.shipToStreet2!=null &&
              this.shipToStreet2.equals(other.getShipToStreet2()))) &&
            ((this.shipToCity==null && other.getShipToCity()==null) || 
             (this.shipToCity!=null &&
              this.shipToCity.equals(other.getShipToCity()))) &&
            ((this.shipToState==null && other.getShipToState()==null) || 
             (this.shipToState!=null &&
              this.shipToState.equals(other.getShipToState()))) &&
            ((this.shipToPostalCode==null && other.getShipToPostalCode()==null) || 
             (this.shipToPostalCode!=null &&
              this.shipToPostalCode.equals(other.getShipToPostalCode()))) &&
            ((this.shipToCompany==null && other.getShipToCompany()==null) || 
             (this.shipToCompany!=null &&
              this.shipToCompany.equals(other.getShipToCompany()))) &&
            ((this.shipToCountry==null && other.getShipToCountry()==null) || 
             (this.shipToCountry!=null &&
              this.shipToCountry.equals(other.getShipToCountry()))) &&
            ((this.billPayment==null && other.getBillPayment()==null) || 
             (this.billPayment!=null &&
              this.billPayment.equals(other.getBillPayment()))) &&
            ((this.merchantDefinedDataField1==null && other.getMerchantDefinedDataField1()==null) || 
             (this.merchantDefinedDataField1!=null &&
              this.merchantDefinedDataField1.equals(other.getMerchantDefinedDataField1()))) &&
            ((this.merchantDefinedDataField2==null && other.getMerchantDefinedDataField2()==null) || 
             (this.merchantDefinedDataField2!=null &&
              this.merchantDefinedDataField2.equals(other.getMerchantDefinedDataField2()))) &&
            ((this.merchantDefinedDataField3==null && other.getMerchantDefinedDataField3()==null) || 
             (this.merchantDefinedDataField3!=null &&
              this.merchantDefinedDataField3.equals(other.getMerchantDefinedDataField3()))) &&
            ((this.merchantDefinedDataField4==null && other.getMerchantDefinedDataField4()==null) || 
             (this.merchantDefinedDataField4!=null &&
              this.merchantDefinedDataField4.equals(other.getMerchantDefinedDataField4()))) &&
            ((this.merchantSecureDataField1==null && other.getMerchantSecureDataField1()==null) || 
             (this.merchantSecureDataField1!=null &&
              this.merchantSecureDataField1.equals(other.getMerchantSecureDataField1()))) &&
            ((this.merchantSecureDataField2==null && other.getMerchantSecureDataField2()==null) || 
             (this.merchantSecureDataField2!=null &&
              this.merchantSecureDataField2.equals(other.getMerchantSecureDataField2()))) &&
            ((this.merchantSecureDataField3==null && other.getMerchantSecureDataField3()==null) || 
             (this.merchantSecureDataField3!=null &&
              this.merchantSecureDataField3.equals(other.getMerchantSecureDataField3()))) &&
            ((this.merchantSecureDataField4==null && other.getMerchantSecureDataField4()==null) || 
             (this.merchantSecureDataField4!=null &&
              this.merchantSecureDataField4.equals(other.getMerchantSecureDataField4())));
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
        if (getApprovalRequired() != null) {
            _hashCode += getApprovalRequired().hashCode();
        }
        if (getAutomaticRenew() != null) {
            _hashCode += getAutomaticRenew().hashCode();
        }
        if (getCardAccountNumber() != null) {
            _hashCode += getCardAccountNumber().hashCode();
        }
        if (getCardExpirationMonth() != null) {
            _hashCode += getCardExpirationMonth().hashCode();
        }
        if (getCardExpirationYear() != null) {
            _hashCode += getCardExpirationYear().hashCode();
        }
        if (getCardIssueNumber() != null) {
            _hashCode += getCardIssueNumber().hashCode();
        }
        if (getCardStartMonth() != null) {
            _hashCode += getCardStartMonth().hashCode();
        }
        if (getCardStartYear() != null) {
            _hashCode += getCardStartYear().hashCode();
        }
        if (getCardType() != null) {
            _hashCode += getCardType().hashCode();
        }
        if (getCheckAccountNumber() != null) {
            _hashCode += getCheckAccountNumber().hashCode();
        }
        if (getCheckAccountType() != null) {
            _hashCode += getCheckAccountType().hashCode();
        }
        if (getCheckBankTransitNumber() != null) {
            _hashCode += getCheckBankTransitNumber().hashCode();
        }
        if (getCheckSecCode() != null) {
            _hashCode += getCheckSecCode().hashCode();
        }
        if (getCheckAuthenticateID() != null) {
            _hashCode += getCheckAuthenticateID().hashCode();
        }
        if (getCity() != null) {
            _hashCode += getCity().hashCode();
        }
        if (getComments() != null) {
            _hashCode += getComments().hashCode();
        }
        if (getCompanyName() != null) {
            _hashCode += getCompanyName().hashCode();
        }
        if (getCountry() != null) {
            _hashCode += getCountry().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getCustomerAccountID() != null) {
            _hashCode += getCustomerAccountID().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getEndDate() != null) {
            _hashCode += getEndDate().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getFrequency() != null) {
            _hashCode += getFrequency().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getMerchantReferenceCode() != null) {
            _hashCode += getMerchantReferenceCode().hashCode();
        }
        if (getPaymentMethod() != null) {
            _hashCode += getPaymentMethod().hashCode();
        }
        if (getPaymentsRemaining() != null) {
            _hashCode += getPaymentsRemaining().hashCode();
        }
        if (getPhoneNumber() != null) {
            _hashCode += getPhoneNumber().hashCode();
        }
        if (getPostalCode() != null) {
            _hashCode += getPostalCode().hashCode();
        }
        if (getRecurringAmount() != null) {
            _hashCode += getRecurringAmount().hashCode();
        }
        if (getSetupAmount() != null) {
            _hashCode += getSetupAmount().hashCode();
        }
        if (getStartDate() != null) {
            _hashCode += getStartDate().hashCode();
        }
        if (getState() != null) {
            _hashCode += getState().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getStreet1() != null) {
            _hashCode += getStreet1().hashCode();
        }
        if (getStreet2() != null) {
            _hashCode += getStreet2().hashCode();
        }
        if (getSubscriptionID() != null) {
            _hashCode += getSubscriptionID().hashCode();
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getTotalPayments() != null) {
            _hashCode += getTotalPayments().hashCode();
        }
        if (getShipToFirstName() != null) {
            _hashCode += getShipToFirstName().hashCode();
        }
        if (getShipToLastName() != null) {
            _hashCode += getShipToLastName().hashCode();
        }
        if (getShipToStreet1() != null) {
            _hashCode += getShipToStreet1().hashCode();
        }
        if (getShipToStreet2() != null) {
            _hashCode += getShipToStreet2().hashCode();
        }
        if (getShipToCity() != null) {
            _hashCode += getShipToCity().hashCode();
        }
        if (getShipToState() != null) {
            _hashCode += getShipToState().hashCode();
        }
        if (getShipToPostalCode() != null) {
            _hashCode += getShipToPostalCode().hashCode();
        }
        if (getShipToCompany() != null) {
            _hashCode += getShipToCompany().hashCode();
        }
        if (getShipToCountry() != null) {
            _hashCode += getShipToCountry().hashCode();
        }
        if (getBillPayment() != null) {
            _hashCode += getBillPayment().hashCode();
        }
        if (getMerchantDefinedDataField1() != null) {
            _hashCode += getMerchantDefinedDataField1().hashCode();
        }
        if (getMerchantDefinedDataField2() != null) {
            _hashCode += getMerchantDefinedDataField2().hashCode();
        }
        if (getMerchantDefinedDataField3() != null) {
            _hashCode += getMerchantDefinedDataField3().hashCode();
        }
        if (getMerchantDefinedDataField4() != null) {
            _hashCode += getMerchantDefinedDataField4().hashCode();
        }
        if (getMerchantSecureDataField1() != null) {
            _hashCode += getMerchantSecureDataField1().hashCode();
        }
        if (getMerchantSecureDataField2() != null) {
            _hashCode += getMerchantSecureDataField2().hashCode();
        }
        if (getMerchantSecureDataField3() != null) {
            _hashCode += getMerchantSecureDataField3().hashCode();
        }
        if (getMerchantSecureDataField4() != null) {
            _hashCode += getMerchantSecureDataField4().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PaySubscriptionRetrieveReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionRetrieveReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("approvalRequired");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "approvalRequired"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("automaticRenew");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "automaticRenew"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardAccountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardAccountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardExpirationMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardExpirationMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardExpirationYear");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardExpirationYear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardIssueNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardIssueNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardStartMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardStartMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardStartYear");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardStartYear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkAccountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "checkAccountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkAccountType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "checkAccountType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkBankTransitNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "checkBankTransitNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkSecCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "checkSecCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkAuthenticateID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "checkAuthenticateID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("city");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "city"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comments");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "comments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "companyName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "country"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerAccountID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "customerAccountID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "endDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("frequency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "frequency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantReferenceCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantReferenceCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentsRemaining");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentsRemaining"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phoneNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "phoneNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "postalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recurringAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "recurringAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("setupAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "setupAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "startDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("street1");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "street1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("street2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "street2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriptionID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "subscriptionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalPayments");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "totalPayments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToFirstName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToFirstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToLastName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToLastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToStreet1");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToStreet1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToStreet2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToStreet2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToCity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToState");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToPostalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToPostalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToCompany");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToCompany"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billPayment");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "billPayment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantDefinedDataField1");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantDefinedDataField1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantDefinedDataField2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantDefinedDataField2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantDefinedDataField3");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantDefinedDataField3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantDefinedDataField4");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantDefinedDataField4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantSecureDataField1");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantSecureDataField1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantSecureDataField2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantSecureDataField2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantSecureDataField3");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantSecureDataField3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantSecureDataField4");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantSecureDataField4"));
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
