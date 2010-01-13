/**
 * PayPalEcDoPaymentReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalEcDoPaymentReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String paypalToken;

    private java.lang.String transactionId;

    private java.lang.String paypalTransactiontype;

    private java.lang.String paymentType;

    private java.lang.String paypalOrderTime;

    private java.lang.String paypalAmount;

    private java.lang.String paypalFeeAmount;

    private java.lang.String paypalTaxAmount;

    private java.lang.String paypalExchangeRate;

    private java.lang.String paypalPaymentStatus;

    private java.lang.String paypalPendingReason;

    private java.lang.String orderId;

    private java.lang.String paypalReasonCode;

    private java.lang.String amount;

    private java.lang.String currency;

    private java.lang.String correlationID;

    private java.lang.String errorCode;

    private java.lang.String paypalBillingAgreementId;

    public PayPalEcDoPaymentReply() {
    }

    public PayPalEcDoPaymentReply(
           java.math.BigInteger reasonCode,
           java.lang.String paypalToken,
           java.lang.String transactionId,
           java.lang.String paypalTransactiontype,
           java.lang.String paymentType,
           java.lang.String paypalOrderTime,
           java.lang.String paypalAmount,
           java.lang.String paypalFeeAmount,
           java.lang.String paypalTaxAmount,
           java.lang.String paypalExchangeRate,
           java.lang.String paypalPaymentStatus,
           java.lang.String paypalPendingReason,
           java.lang.String orderId,
           java.lang.String paypalReasonCode,
           java.lang.String amount,
           java.lang.String currency,
           java.lang.String correlationID,
           java.lang.String errorCode,
           java.lang.String paypalBillingAgreementId) {
           this.reasonCode = reasonCode;
           this.paypalToken = paypalToken;
           this.transactionId = transactionId;
           this.paypalTransactiontype = paypalTransactiontype;
           this.paymentType = paymentType;
           this.paypalOrderTime = paypalOrderTime;
           this.paypalAmount = paypalAmount;
           this.paypalFeeAmount = paypalFeeAmount;
           this.paypalTaxAmount = paypalTaxAmount;
           this.paypalExchangeRate = paypalExchangeRate;
           this.paypalPaymentStatus = paypalPaymentStatus;
           this.paypalPendingReason = paypalPendingReason;
           this.orderId = orderId;
           this.paypalReasonCode = paypalReasonCode;
           this.amount = amount;
           this.currency = currency;
           this.correlationID = correlationID;
           this.errorCode = errorCode;
           this.paypalBillingAgreementId = paypalBillingAgreementId;
    }


    /**
     * Gets the reasonCode value for this PayPalEcDoPaymentReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalEcDoPaymentReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the paypalToken value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalToken
     */
    public java.lang.String getPaypalToken() {
        return paypalToken;
    }


    /**
     * Sets the paypalToken value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalToken
     */
    public void setPaypalToken(java.lang.String paypalToken) {
        this.paypalToken = paypalToken;
    }


    /**
     * Gets the transactionId value for this PayPalEcDoPaymentReply.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this PayPalEcDoPaymentReply.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the paypalTransactiontype value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalTransactiontype
     */
    public java.lang.String getPaypalTransactiontype() {
        return paypalTransactiontype;
    }


    /**
     * Sets the paypalTransactiontype value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalTransactiontype
     */
    public void setPaypalTransactiontype(java.lang.String paypalTransactiontype) {
        this.paypalTransactiontype = paypalTransactiontype;
    }


    /**
     * Gets the paymentType value for this PayPalEcDoPaymentReply.
     * 
     * @return paymentType
     */
    public java.lang.String getPaymentType() {
        return paymentType;
    }


    /**
     * Sets the paymentType value for this PayPalEcDoPaymentReply.
     * 
     * @param paymentType
     */
    public void setPaymentType(java.lang.String paymentType) {
        this.paymentType = paymentType;
    }


    /**
     * Gets the paypalOrderTime value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalOrderTime
     */
    public java.lang.String getPaypalOrderTime() {
        return paypalOrderTime;
    }


    /**
     * Sets the paypalOrderTime value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalOrderTime
     */
    public void setPaypalOrderTime(java.lang.String paypalOrderTime) {
        this.paypalOrderTime = paypalOrderTime;
    }


    /**
     * Gets the paypalAmount value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalAmount
     */
    public java.lang.String getPaypalAmount() {
        return paypalAmount;
    }


    /**
     * Sets the paypalAmount value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalAmount
     */
    public void setPaypalAmount(java.lang.String paypalAmount) {
        this.paypalAmount = paypalAmount;
    }


    /**
     * Gets the paypalFeeAmount value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalFeeAmount
     */
    public java.lang.String getPaypalFeeAmount() {
        return paypalFeeAmount;
    }


    /**
     * Sets the paypalFeeAmount value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalFeeAmount
     */
    public void setPaypalFeeAmount(java.lang.String paypalFeeAmount) {
        this.paypalFeeAmount = paypalFeeAmount;
    }


    /**
     * Gets the paypalTaxAmount value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalTaxAmount
     */
    public java.lang.String getPaypalTaxAmount() {
        return paypalTaxAmount;
    }


    /**
     * Sets the paypalTaxAmount value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalTaxAmount
     */
    public void setPaypalTaxAmount(java.lang.String paypalTaxAmount) {
        this.paypalTaxAmount = paypalTaxAmount;
    }


    /**
     * Gets the paypalExchangeRate value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalExchangeRate
     */
    public java.lang.String getPaypalExchangeRate() {
        return paypalExchangeRate;
    }


    /**
     * Sets the paypalExchangeRate value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalExchangeRate
     */
    public void setPaypalExchangeRate(java.lang.String paypalExchangeRate) {
        this.paypalExchangeRate = paypalExchangeRate;
    }


    /**
     * Gets the paypalPaymentStatus value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalPaymentStatus
     */
    public java.lang.String getPaypalPaymentStatus() {
        return paypalPaymentStatus;
    }


    /**
     * Sets the paypalPaymentStatus value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalPaymentStatus
     */
    public void setPaypalPaymentStatus(java.lang.String paypalPaymentStatus) {
        this.paypalPaymentStatus = paypalPaymentStatus;
    }


    /**
     * Gets the paypalPendingReason value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalPendingReason
     */
    public java.lang.String getPaypalPendingReason() {
        return paypalPendingReason;
    }


    /**
     * Sets the paypalPendingReason value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalPendingReason
     */
    public void setPaypalPendingReason(java.lang.String paypalPendingReason) {
        this.paypalPendingReason = paypalPendingReason;
    }


    /**
     * Gets the orderId value for this PayPalEcDoPaymentReply.
     * 
     * @return orderId
     */
    public java.lang.String getOrderId() {
        return orderId;
    }


    /**
     * Sets the orderId value for this PayPalEcDoPaymentReply.
     * 
     * @param orderId
     */
    public void setOrderId(java.lang.String orderId) {
        this.orderId = orderId;
    }


    /**
     * Gets the paypalReasonCode value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalReasonCode
     */
    public java.lang.String getPaypalReasonCode() {
        return paypalReasonCode;
    }


    /**
     * Sets the paypalReasonCode value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalReasonCode
     */
    public void setPaypalReasonCode(java.lang.String paypalReasonCode) {
        this.paypalReasonCode = paypalReasonCode;
    }


    /**
     * Gets the amount value for this PayPalEcDoPaymentReply.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this PayPalEcDoPaymentReply.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the currency value for this PayPalEcDoPaymentReply.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this PayPalEcDoPaymentReply.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the correlationID value for this PayPalEcDoPaymentReply.
     * 
     * @return correlationID
     */
    public java.lang.String getCorrelationID() {
        return correlationID;
    }


    /**
     * Sets the correlationID value for this PayPalEcDoPaymentReply.
     * 
     * @param correlationID
     */
    public void setCorrelationID(java.lang.String correlationID) {
        this.correlationID = correlationID;
    }


    /**
     * Gets the errorCode value for this PayPalEcDoPaymentReply.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this PayPalEcDoPaymentReply.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the paypalBillingAgreementId value for this PayPalEcDoPaymentReply.
     * 
     * @return paypalBillingAgreementId
     */
    public java.lang.String getPaypalBillingAgreementId() {
        return paypalBillingAgreementId;
    }


    /**
     * Sets the paypalBillingAgreementId value for this PayPalEcDoPaymentReply.
     * 
     * @param paypalBillingAgreementId
     */
    public void setPaypalBillingAgreementId(java.lang.String paypalBillingAgreementId) {
        this.paypalBillingAgreementId = paypalBillingAgreementId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalEcDoPaymentReply)) return false;
        PayPalEcDoPaymentReply other = (PayPalEcDoPaymentReply) obj;
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
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.paypalTransactiontype==null && other.getPaypalTransactiontype()==null) || 
             (this.paypalTransactiontype!=null &&
              this.paypalTransactiontype.equals(other.getPaypalTransactiontype()))) &&
            ((this.paymentType==null && other.getPaymentType()==null) || 
             (this.paymentType!=null &&
              this.paymentType.equals(other.getPaymentType()))) &&
            ((this.paypalOrderTime==null && other.getPaypalOrderTime()==null) || 
             (this.paypalOrderTime!=null &&
              this.paypalOrderTime.equals(other.getPaypalOrderTime()))) &&
            ((this.paypalAmount==null && other.getPaypalAmount()==null) || 
             (this.paypalAmount!=null &&
              this.paypalAmount.equals(other.getPaypalAmount()))) &&
            ((this.paypalFeeAmount==null && other.getPaypalFeeAmount()==null) || 
             (this.paypalFeeAmount!=null &&
              this.paypalFeeAmount.equals(other.getPaypalFeeAmount()))) &&
            ((this.paypalTaxAmount==null && other.getPaypalTaxAmount()==null) || 
             (this.paypalTaxAmount!=null &&
              this.paypalTaxAmount.equals(other.getPaypalTaxAmount()))) &&
            ((this.paypalExchangeRate==null && other.getPaypalExchangeRate()==null) || 
             (this.paypalExchangeRate!=null &&
              this.paypalExchangeRate.equals(other.getPaypalExchangeRate()))) &&
            ((this.paypalPaymentStatus==null && other.getPaypalPaymentStatus()==null) || 
             (this.paypalPaymentStatus!=null &&
              this.paypalPaymentStatus.equals(other.getPaypalPaymentStatus()))) &&
            ((this.paypalPendingReason==null && other.getPaypalPendingReason()==null) || 
             (this.paypalPendingReason!=null &&
              this.paypalPendingReason.equals(other.getPaypalPendingReason()))) &&
            ((this.orderId==null && other.getOrderId()==null) || 
             (this.orderId!=null &&
              this.orderId.equals(other.getOrderId()))) &&
            ((this.paypalReasonCode==null && other.getPaypalReasonCode()==null) || 
             (this.paypalReasonCode!=null &&
              this.paypalReasonCode.equals(other.getPaypalReasonCode()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.correlationID==null && other.getCorrelationID()==null) || 
             (this.correlationID!=null &&
              this.correlationID.equals(other.getCorrelationID()))) &&
            ((this.errorCode==null && other.getErrorCode()==null) || 
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.paypalBillingAgreementId==null && other.getPaypalBillingAgreementId()==null) || 
             (this.paypalBillingAgreementId!=null &&
              this.paypalBillingAgreementId.equals(other.getPaypalBillingAgreementId())));
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
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getPaypalTransactiontype() != null) {
            _hashCode += getPaypalTransactiontype().hashCode();
        }
        if (getPaymentType() != null) {
            _hashCode += getPaymentType().hashCode();
        }
        if (getPaypalOrderTime() != null) {
            _hashCode += getPaypalOrderTime().hashCode();
        }
        if (getPaypalAmount() != null) {
            _hashCode += getPaypalAmount().hashCode();
        }
        if (getPaypalFeeAmount() != null) {
            _hashCode += getPaypalFeeAmount().hashCode();
        }
        if (getPaypalTaxAmount() != null) {
            _hashCode += getPaypalTaxAmount().hashCode();
        }
        if (getPaypalExchangeRate() != null) {
            _hashCode += getPaypalExchangeRate().hashCode();
        }
        if (getPaypalPaymentStatus() != null) {
            _hashCode += getPaypalPaymentStatus().hashCode();
        }
        if (getPaypalPendingReason() != null) {
            _hashCode += getPaypalPendingReason().hashCode();
        }
        if (getOrderId() != null) {
            _hashCode += getOrderId().hashCode();
        }
        if (getPaypalReasonCode() != null) {
            _hashCode += getPaypalReasonCode().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getCorrelationID() != null) {
            _hashCode += getCorrelationID().hashCode();
        }
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getPaypalBillingAgreementId() != null) {
            _hashCode += getPaypalBillingAgreementId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalEcDoPaymentReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcDoPaymentReply"));
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
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "transactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalTransactiontype");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalTransactiontype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalOrderTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalOrderTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalFeeAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalFeeAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalExchangeRate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalExchangeRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalPaymentStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalPaymentStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalPendingReason");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalPendingReason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "orderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalReasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalReasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "currency"));
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
        elemField.setFieldName("paypalBillingAgreementId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementId"));
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
