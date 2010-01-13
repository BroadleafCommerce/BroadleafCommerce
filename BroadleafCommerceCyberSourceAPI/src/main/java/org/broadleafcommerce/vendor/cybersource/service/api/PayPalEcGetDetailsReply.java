/**
 * PayPalEcGetDetailsReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalEcGetDetailsReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String paypalToken;

    private java.lang.String payer;

    private java.lang.String payerId;

    private java.lang.String payerStatus;

    private java.lang.String payerSalutation;

    private java.lang.String payerFirstname;

    private java.lang.String payerMiddlename;

    private java.lang.String payerLastname;

    private java.lang.String payerSuffix;

    private java.lang.String payerCountry;

    private java.lang.String payerBusiness;

    private java.lang.String shipToName;

    private java.lang.String shipToAddress1;

    private java.lang.String shipToAddress2;

    private java.lang.String shipToCity;

    private java.lang.String shipToState;

    private java.lang.String shipToCountry;

    private java.lang.String shipToZip;

    private java.lang.String addressStatus;

    private java.lang.String payerPhone;

    private java.lang.String avsCode;

    private java.lang.String correlationID;

    private java.lang.String errorCode;

    private java.lang.String street1;

    private java.lang.String street2;

    private java.lang.String city;

    private java.lang.String state;

    private java.lang.String postalCode;

    private java.lang.String countryCode;

    private java.lang.String countryName;

    private java.lang.String addressID;

    private java.lang.String paypalBillingAgreementAcceptedStatus;

    public PayPalEcGetDetailsReply() {
    }

    public PayPalEcGetDetailsReply(
           java.math.BigInteger reasonCode,
           java.lang.String paypalToken,
           java.lang.String payer,
           java.lang.String payerId,
           java.lang.String payerStatus,
           java.lang.String payerSalutation,
           java.lang.String payerFirstname,
           java.lang.String payerMiddlename,
           java.lang.String payerLastname,
           java.lang.String payerSuffix,
           java.lang.String payerCountry,
           java.lang.String payerBusiness,
           java.lang.String shipToName,
           java.lang.String shipToAddress1,
           java.lang.String shipToAddress2,
           java.lang.String shipToCity,
           java.lang.String shipToState,
           java.lang.String shipToCountry,
           java.lang.String shipToZip,
           java.lang.String addressStatus,
           java.lang.String payerPhone,
           java.lang.String avsCode,
           java.lang.String correlationID,
           java.lang.String errorCode,
           java.lang.String street1,
           java.lang.String street2,
           java.lang.String city,
           java.lang.String state,
           java.lang.String postalCode,
           java.lang.String countryCode,
           java.lang.String countryName,
           java.lang.String addressID,
           java.lang.String paypalBillingAgreementAcceptedStatus) {
           this.reasonCode = reasonCode;
           this.paypalToken = paypalToken;
           this.payer = payer;
           this.payerId = payerId;
           this.payerStatus = payerStatus;
           this.payerSalutation = payerSalutation;
           this.payerFirstname = payerFirstname;
           this.payerMiddlename = payerMiddlename;
           this.payerLastname = payerLastname;
           this.payerSuffix = payerSuffix;
           this.payerCountry = payerCountry;
           this.payerBusiness = payerBusiness;
           this.shipToName = shipToName;
           this.shipToAddress1 = shipToAddress1;
           this.shipToAddress2 = shipToAddress2;
           this.shipToCity = shipToCity;
           this.shipToState = shipToState;
           this.shipToCountry = shipToCountry;
           this.shipToZip = shipToZip;
           this.addressStatus = addressStatus;
           this.payerPhone = payerPhone;
           this.avsCode = avsCode;
           this.correlationID = correlationID;
           this.errorCode = errorCode;
           this.street1 = street1;
           this.street2 = street2;
           this.city = city;
           this.state = state;
           this.postalCode = postalCode;
           this.countryCode = countryCode;
           this.countryName = countryName;
           this.addressID = addressID;
           this.paypalBillingAgreementAcceptedStatus = paypalBillingAgreementAcceptedStatus;
    }


    /**
     * Gets the reasonCode value for this PayPalEcGetDetailsReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalEcGetDetailsReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the paypalToken value for this PayPalEcGetDetailsReply.
     * 
     * @return paypalToken
     */
    public java.lang.String getPaypalToken() {
        return paypalToken;
    }


    /**
     * Sets the paypalToken value for this PayPalEcGetDetailsReply.
     * 
     * @param paypalToken
     */
    public void setPaypalToken(java.lang.String paypalToken) {
        this.paypalToken = paypalToken;
    }


    /**
     * Gets the payer value for this PayPalEcGetDetailsReply.
     * 
     * @return payer
     */
    public java.lang.String getPayer() {
        return payer;
    }


    /**
     * Sets the payer value for this PayPalEcGetDetailsReply.
     * 
     * @param payer
     */
    public void setPayer(java.lang.String payer) {
        this.payer = payer;
    }


    /**
     * Gets the payerId value for this PayPalEcGetDetailsReply.
     * 
     * @return payerId
     */
    public java.lang.String getPayerId() {
        return payerId;
    }


    /**
     * Sets the payerId value for this PayPalEcGetDetailsReply.
     * 
     * @param payerId
     */
    public void setPayerId(java.lang.String payerId) {
        this.payerId = payerId;
    }


    /**
     * Gets the payerStatus value for this PayPalEcGetDetailsReply.
     * 
     * @return payerStatus
     */
    public java.lang.String getPayerStatus() {
        return payerStatus;
    }


    /**
     * Sets the payerStatus value for this PayPalEcGetDetailsReply.
     * 
     * @param payerStatus
     */
    public void setPayerStatus(java.lang.String payerStatus) {
        this.payerStatus = payerStatus;
    }


    /**
     * Gets the payerSalutation value for this PayPalEcGetDetailsReply.
     * 
     * @return payerSalutation
     */
    public java.lang.String getPayerSalutation() {
        return payerSalutation;
    }


    /**
     * Sets the payerSalutation value for this PayPalEcGetDetailsReply.
     * 
     * @param payerSalutation
     */
    public void setPayerSalutation(java.lang.String payerSalutation) {
        this.payerSalutation = payerSalutation;
    }


    /**
     * Gets the payerFirstname value for this PayPalEcGetDetailsReply.
     * 
     * @return payerFirstname
     */
    public java.lang.String getPayerFirstname() {
        return payerFirstname;
    }


    /**
     * Sets the payerFirstname value for this PayPalEcGetDetailsReply.
     * 
     * @param payerFirstname
     */
    public void setPayerFirstname(java.lang.String payerFirstname) {
        this.payerFirstname = payerFirstname;
    }


    /**
     * Gets the payerMiddlename value for this PayPalEcGetDetailsReply.
     * 
     * @return payerMiddlename
     */
    public java.lang.String getPayerMiddlename() {
        return payerMiddlename;
    }


    /**
     * Sets the payerMiddlename value for this PayPalEcGetDetailsReply.
     * 
     * @param payerMiddlename
     */
    public void setPayerMiddlename(java.lang.String payerMiddlename) {
        this.payerMiddlename = payerMiddlename;
    }


    /**
     * Gets the payerLastname value for this PayPalEcGetDetailsReply.
     * 
     * @return payerLastname
     */
    public java.lang.String getPayerLastname() {
        return payerLastname;
    }


    /**
     * Sets the payerLastname value for this PayPalEcGetDetailsReply.
     * 
     * @param payerLastname
     */
    public void setPayerLastname(java.lang.String payerLastname) {
        this.payerLastname = payerLastname;
    }


    /**
     * Gets the payerSuffix value for this PayPalEcGetDetailsReply.
     * 
     * @return payerSuffix
     */
    public java.lang.String getPayerSuffix() {
        return payerSuffix;
    }


    /**
     * Sets the payerSuffix value for this PayPalEcGetDetailsReply.
     * 
     * @param payerSuffix
     */
    public void setPayerSuffix(java.lang.String payerSuffix) {
        this.payerSuffix = payerSuffix;
    }


    /**
     * Gets the payerCountry value for this PayPalEcGetDetailsReply.
     * 
     * @return payerCountry
     */
    public java.lang.String getPayerCountry() {
        return payerCountry;
    }


    /**
     * Sets the payerCountry value for this PayPalEcGetDetailsReply.
     * 
     * @param payerCountry
     */
    public void setPayerCountry(java.lang.String payerCountry) {
        this.payerCountry = payerCountry;
    }


    /**
     * Gets the payerBusiness value for this PayPalEcGetDetailsReply.
     * 
     * @return payerBusiness
     */
    public java.lang.String getPayerBusiness() {
        return payerBusiness;
    }


    /**
     * Sets the payerBusiness value for this PayPalEcGetDetailsReply.
     * 
     * @param payerBusiness
     */
    public void setPayerBusiness(java.lang.String payerBusiness) {
        this.payerBusiness = payerBusiness;
    }


    /**
     * Gets the shipToName value for this PayPalEcGetDetailsReply.
     * 
     * @return shipToName
     */
    public java.lang.String getShipToName() {
        return shipToName;
    }


    /**
     * Sets the shipToName value for this PayPalEcGetDetailsReply.
     * 
     * @param shipToName
     */
    public void setShipToName(java.lang.String shipToName) {
        this.shipToName = shipToName;
    }


    /**
     * Gets the shipToAddress1 value for this PayPalEcGetDetailsReply.
     * 
     * @return shipToAddress1
     */
    public java.lang.String getShipToAddress1() {
        return shipToAddress1;
    }


    /**
     * Sets the shipToAddress1 value for this PayPalEcGetDetailsReply.
     * 
     * @param shipToAddress1
     */
    public void setShipToAddress1(java.lang.String shipToAddress1) {
        this.shipToAddress1 = shipToAddress1;
    }


    /**
     * Gets the shipToAddress2 value for this PayPalEcGetDetailsReply.
     * 
     * @return shipToAddress2
     */
    public java.lang.String getShipToAddress2() {
        return shipToAddress2;
    }


    /**
     * Sets the shipToAddress2 value for this PayPalEcGetDetailsReply.
     * 
     * @param shipToAddress2
     */
    public void setShipToAddress2(java.lang.String shipToAddress2) {
        this.shipToAddress2 = shipToAddress2;
    }


    /**
     * Gets the shipToCity value for this PayPalEcGetDetailsReply.
     * 
     * @return shipToCity
     */
    public java.lang.String getShipToCity() {
        return shipToCity;
    }


    /**
     * Sets the shipToCity value for this PayPalEcGetDetailsReply.
     * 
     * @param shipToCity
     */
    public void setShipToCity(java.lang.String shipToCity) {
        this.shipToCity = shipToCity;
    }


    /**
     * Gets the shipToState value for this PayPalEcGetDetailsReply.
     * 
     * @return shipToState
     */
    public java.lang.String getShipToState() {
        return shipToState;
    }


    /**
     * Sets the shipToState value for this PayPalEcGetDetailsReply.
     * 
     * @param shipToState
     */
    public void setShipToState(java.lang.String shipToState) {
        this.shipToState = shipToState;
    }


    /**
     * Gets the shipToCountry value for this PayPalEcGetDetailsReply.
     * 
     * @return shipToCountry
     */
    public java.lang.String getShipToCountry() {
        return shipToCountry;
    }


    /**
     * Sets the shipToCountry value for this PayPalEcGetDetailsReply.
     * 
     * @param shipToCountry
     */
    public void setShipToCountry(java.lang.String shipToCountry) {
        this.shipToCountry = shipToCountry;
    }


    /**
     * Gets the shipToZip value for this PayPalEcGetDetailsReply.
     * 
     * @return shipToZip
     */
    public java.lang.String getShipToZip() {
        return shipToZip;
    }


    /**
     * Sets the shipToZip value for this PayPalEcGetDetailsReply.
     * 
     * @param shipToZip
     */
    public void setShipToZip(java.lang.String shipToZip) {
        this.shipToZip = shipToZip;
    }


    /**
     * Gets the addressStatus value for this PayPalEcGetDetailsReply.
     * 
     * @return addressStatus
     */
    public java.lang.String getAddressStatus() {
        return addressStatus;
    }


    /**
     * Sets the addressStatus value for this PayPalEcGetDetailsReply.
     * 
     * @param addressStatus
     */
    public void setAddressStatus(java.lang.String addressStatus) {
        this.addressStatus = addressStatus;
    }


    /**
     * Gets the payerPhone value for this PayPalEcGetDetailsReply.
     * 
     * @return payerPhone
     */
    public java.lang.String getPayerPhone() {
        return payerPhone;
    }


    /**
     * Sets the payerPhone value for this PayPalEcGetDetailsReply.
     * 
     * @param payerPhone
     */
    public void setPayerPhone(java.lang.String payerPhone) {
        this.payerPhone = payerPhone;
    }


    /**
     * Gets the avsCode value for this PayPalEcGetDetailsReply.
     * 
     * @return avsCode
     */
    public java.lang.String getAvsCode() {
        return avsCode;
    }


    /**
     * Sets the avsCode value for this PayPalEcGetDetailsReply.
     * 
     * @param avsCode
     */
    public void setAvsCode(java.lang.String avsCode) {
        this.avsCode = avsCode;
    }


    /**
     * Gets the correlationID value for this PayPalEcGetDetailsReply.
     * 
     * @return correlationID
     */
    public java.lang.String getCorrelationID() {
        return correlationID;
    }


    /**
     * Sets the correlationID value for this PayPalEcGetDetailsReply.
     * 
     * @param correlationID
     */
    public void setCorrelationID(java.lang.String correlationID) {
        this.correlationID = correlationID;
    }


    /**
     * Gets the errorCode value for this PayPalEcGetDetailsReply.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this PayPalEcGetDetailsReply.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the street1 value for this PayPalEcGetDetailsReply.
     * 
     * @return street1
     */
    public java.lang.String getStreet1() {
        return street1;
    }


    /**
     * Sets the street1 value for this PayPalEcGetDetailsReply.
     * 
     * @param street1
     */
    public void setStreet1(java.lang.String street1) {
        this.street1 = street1;
    }


    /**
     * Gets the street2 value for this PayPalEcGetDetailsReply.
     * 
     * @return street2
     */
    public java.lang.String getStreet2() {
        return street2;
    }


    /**
     * Sets the street2 value for this PayPalEcGetDetailsReply.
     * 
     * @param street2
     */
    public void setStreet2(java.lang.String street2) {
        this.street2 = street2;
    }


    /**
     * Gets the city value for this PayPalEcGetDetailsReply.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }


    /**
     * Sets the city value for this PayPalEcGetDetailsReply.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }


    /**
     * Gets the state value for this PayPalEcGetDetailsReply.
     * 
     * @return state
     */
    public java.lang.String getState() {
        return state;
    }


    /**
     * Sets the state value for this PayPalEcGetDetailsReply.
     * 
     * @param state
     */
    public void setState(java.lang.String state) {
        this.state = state;
    }


    /**
     * Gets the postalCode value for this PayPalEcGetDetailsReply.
     * 
     * @return postalCode
     */
    public java.lang.String getPostalCode() {
        return postalCode;
    }


    /**
     * Sets the postalCode value for this PayPalEcGetDetailsReply.
     * 
     * @param postalCode
     */
    public void setPostalCode(java.lang.String postalCode) {
        this.postalCode = postalCode;
    }


    /**
     * Gets the countryCode value for this PayPalEcGetDetailsReply.
     * 
     * @return countryCode
     */
    public java.lang.String getCountryCode() {
        return countryCode;
    }


    /**
     * Sets the countryCode value for this PayPalEcGetDetailsReply.
     * 
     * @param countryCode
     */
    public void setCountryCode(java.lang.String countryCode) {
        this.countryCode = countryCode;
    }


    /**
     * Gets the countryName value for this PayPalEcGetDetailsReply.
     * 
     * @return countryName
     */
    public java.lang.String getCountryName() {
        return countryName;
    }


    /**
     * Sets the countryName value for this PayPalEcGetDetailsReply.
     * 
     * @param countryName
     */
    public void setCountryName(java.lang.String countryName) {
        this.countryName = countryName;
    }


    /**
     * Gets the addressID value for this PayPalEcGetDetailsReply.
     * 
     * @return addressID
     */
    public java.lang.String getAddressID() {
        return addressID;
    }


    /**
     * Sets the addressID value for this PayPalEcGetDetailsReply.
     * 
     * @param addressID
     */
    public void setAddressID(java.lang.String addressID) {
        this.addressID = addressID;
    }


    /**
     * Gets the paypalBillingAgreementAcceptedStatus value for this PayPalEcGetDetailsReply.
     * 
     * @return paypalBillingAgreementAcceptedStatus
     */
    public java.lang.String getPaypalBillingAgreementAcceptedStatus() {
        return paypalBillingAgreementAcceptedStatus;
    }


    /**
     * Sets the paypalBillingAgreementAcceptedStatus value for this PayPalEcGetDetailsReply.
     * 
     * @param paypalBillingAgreementAcceptedStatus
     */
    public void setPaypalBillingAgreementAcceptedStatus(java.lang.String paypalBillingAgreementAcceptedStatus) {
        this.paypalBillingAgreementAcceptedStatus = paypalBillingAgreementAcceptedStatus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalEcGetDetailsReply)) return false;
        PayPalEcGetDetailsReply other = (PayPalEcGetDetailsReply) obj;
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
            ((this.paypalToken==null && other.getPaypalToken()==null) || 
             (this.paypalToken!=null &&
              this.paypalToken.equals(other.getPaypalToken()))) &&
            ((this.payer==null && other.getPayer()==null) || 
             (this.payer!=null &&
              this.payer.equals(other.getPayer()))) &&
            ((this.payerId==null && other.getPayerId()==null) || 
             (this.payerId!=null &&
              this.payerId.equals(other.getPayerId()))) &&
            ((this.payerStatus==null && other.getPayerStatus()==null) || 
             (this.payerStatus!=null &&
              this.payerStatus.equals(other.getPayerStatus()))) &&
            ((this.payerSalutation==null && other.getPayerSalutation()==null) || 
             (this.payerSalutation!=null &&
              this.payerSalutation.equals(other.getPayerSalutation()))) &&
            ((this.payerFirstname==null && other.getPayerFirstname()==null) || 
             (this.payerFirstname!=null &&
              this.payerFirstname.equals(other.getPayerFirstname()))) &&
            ((this.payerMiddlename==null && other.getPayerMiddlename()==null) || 
             (this.payerMiddlename!=null &&
              this.payerMiddlename.equals(other.getPayerMiddlename()))) &&
            ((this.payerLastname==null && other.getPayerLastname()==null) || 
             (this.payerLastname!=null &&
              this.payerLastname.equals(other.getPayerLastname()))) &&
            ((this.payerSuffix==null && other.getPayerSuffix()==null) || 
             (this.payerSuffix!=null &&
              this.payerSuffix.equals(other.getPayerSuffix()))) &&
            ((this.payerCountry==null && other.getPayerCountry()==null) || 
             (this.payerCountry!=null &&
              this.payerCountry.equals(other.getPayerCountry()))) &&
            ((this.payerBusiness==null && other.getPayerBusiness()==null) || 
             (this.payerBusiness!=null &&
              this.payerBusiness.equals(other.getPayerBusiness()))) &&
            ((this.shipToName==null && other.getShipToName()==null) || 
             (this.shipToName!=null &&
              this.shipToName.equals(other.getShipToName()))) &&
            ((this.shipToAddress1==null && other.getShipToAddress1()==null) || 
             (this.shipToAddress1!=null &&
              this.shipToAddress1.equals(other.getShipToAddress1()))) &&
            ((this.shipToAddress2==null && other.getShipToAddress2()==null) || 
             (this.shipToAddress2!=null &&
              this.shipToAddress2.equals(other.getShipToAddress2()))) &&
            ((this.shipToCity==null && other.getShipToCity()==null) || 
             (this.shipToCity!=null &&
              this.shipToCity.equals(other.getShipToCity()))) &&
            ((this.shipToState==null && other.getShipToState()==null) || 
             (this.shipToState!=null &&
              this.shipToState.equals(other.getShipToState()))) &&
            ((this.shipToCountry==null && other.getShipToCountry()==null) || 
             (this.shipToCountry!=null &&
              this.shipToCountry.equals(other.getShipToCountry()))) &&
            ((this.shipToZip==null && other.getShipToZip()==null) || 
             (this.shipToZip!=null &&
              this.shipToZip.equals(other.getShipToZip()))) &&
            ((this.addressStatus==null && other.getAddressStatus()==null) || 
             (this.addressStatus!=null &&
              this.addressStatus.equals(other.getAddressStatus()))) &&
            ((this.payerPhone==null && other.getPayerPhone()==null) || 
             (this.payerPhone!=null &&
              this.payerPhone.equals(other.getPayerPhone()))) &&
            ((this.avsCode==null && other.getAvsCode()==null) || 
             (this.avsCode!=null &&
              this.avsCode.equals(other.getAvsCode()))) &&
            ((this.correlationID==null && other.getCorrelationID()==null) || 
             (this.correlationID!=null &&
              this.correlationID.equals(other.getCorrelationID()))) &&
            ((this.errorCode==null && other.getErrorCode()==null) || 
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.street1==null && other.getStreet1()==null) || 
             (this.street1!=null &&
              this.street1.equals(other.getStreet1()))) &&
            ((this.street2==null && other.getStreet2()==null) || 
             (this.street2!=null &&
              this.street2.equals(other.getStreet2()))) &&
            ((this.city==null && other.getCity()==null) || 
             (this.city!=null &&
              this.city.equals(other.getCity()))) &&
            ((this.state==null && other.getState()==null) || 
             (this.state!=null &&
              this.state.equals(other.getState()))) &&
            ((this.postalCode==null && other.getPostalCode()==null) || 
             (this.postalCode!=null &&
              this.postalCode.equals(other.getPostalCode()))) &&
            ((this.countryCode==null && other.getCountryCode()==null) || 
             (this.countryCode!=null &&
              this.countryCode.equals(other.getCountryCode()))) &&
            ((this.countryName==null && other.getCountryName()==null) || 
             (this.countryName!=null &&
              this.countryName.equals(other.getCountryName()))) &&
            ((this.addressID==null && other.getAddressID()==null) || 
             (this.addressID!=null &&
              this.addressID.equals(other.getAddressID()))) &&
            ((this.paypalBillingAgreementAcceptedStatus==null && other.getPaypalBillingAgreementAcceptedStatus()==null) || 
             (this.paypalBillingAgreementAcceptedStatus!=null &&
              this.paypalBillingAgreementAcceptedStatus.equals(other.getPaypalBillingAgreementAcceptedStatus())));
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
        if (getPaypalToken() != null) {
            _hashCode += getPaypalToken().hashCode();
        }
        if (getPayer() != null) {
            _hashCode += getPayer().hashCode();
        }
        if (getPayerId() != null) {
            _hashCode += getPayerId().hashCode();
        }
        if (getPayerStatus() != null) {
            _hashCode += getPayerStatus().hashCode();
        }
        if (getPayerSalutation() != null) {
            _hashCode += getPayerSalutation().hashCode();
        }
        if (getPayerFirstname() != null) {
            _hashCode += getPayerFirstname().hashCode();
        }
        if (getPayerMiddlename() != null) {
            _hashCode += getPayerMiddlename().hashCode();
        }
        if (getPayerLastname() != null) {
            _hashCode += getPayerLastname().hashCode();
        }
        if (getPayerSuffix() != null) {
            _hashCode += getPayerSuffix().hashCode();
        }
        if (getPayerCountry() != null) {
            _hashCode += getPayerCountry().hashCode();
        }
        if (getPayerBusiness() != null) {
            _hashCode += getPayerBusiness().hashCode();
        }
        if (getShipToName() != null) {
            _hashCode += getShipToName().hashCode();
        }
        if (getShipToAddress1() != null) {
            _hashCode += getShipToAddress1().hashCode();
        }
        if (getShipToAddress2() != null) {
            _hashCode += getShipToAddress2().hashCode();
        }
        if (getShipToCity() != null) {
            _hashCode += getShipToCity().hashCode();
        }
        if (getShipToState() != null) {
            _hashCode += getShipToState().hashCode();
        }
        if (getShipToCountry() != null) {
            _hashCode += getShipToCountry().hashCode();
        }
        if (getShipToZip() != null) {
            _hashCode += getShipToZip().hashCode();
        }
        if (getAddressStatus() != null) {
            _hashCode += getAddressStatus().hashCode();
        }
        if (getPayerPhone() != null) {
            _hashCode += getPayerPhone().hashCode();
        }
        if (getAvsCode() != null) {
            _hashCode += getAvsCode().hashCode();
        }
        if (getCorrelationID() != null) {
            _hashCode += getCorrelationID().hashCode();
        }
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getStreet1() != null) {
            _hashCode += getStreet1().hashCode();
        }
        if (getStreet2() != null) {
            _hashCode += getStreet2().hashCode();
        }
        if (getCity() != null) {
            _hashCode += getCity().hashCode();
        }
        if (getState() != null) {
            _hashCode += getState().hashCode();
        }
        if (getPostalCode() != null) {
            _hashCode += getPostalCode().hashCode();
        }
        if (getCountryCode() != null) {
            _hashCode += getCountryCode().hashCode();
        }
        if (getCountryName() != null) {
            _hashCode += getCountryName().hashCode();
        }
        if (getAddressID() != null) {
            _hashCode += getAddressID().hashCode();
        }
        if (getPaypalBillingAgreementAcceptedStatus() != null) {
            _hashCode += getPaypalBillingAgreementAcceptedStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalEcGetDetailsReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcGetDetailsReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payer");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerSalutation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerSalutation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerFirstname");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerFirstname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerMiddlename");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerMiddlename"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerLastname");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerLastname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerSuffix");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerSuffix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerBusiness");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerBusiness"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToAddress1");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToAddress1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToAddress2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToAddress2"));
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
        elemField.setFieldName("shipToCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipToZip");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipToZip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "addressStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerPhone");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerPhone"));
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
        elemField.setFieldName("correlationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "correlationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "errorCode"));
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
        elemField.setFieldName("city");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "city"));
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
        elemField.setFieldName("postalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "postalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "countryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "countryName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "addressID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalBillingAgreementAcceptedStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementAcceptedStatus"));
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
