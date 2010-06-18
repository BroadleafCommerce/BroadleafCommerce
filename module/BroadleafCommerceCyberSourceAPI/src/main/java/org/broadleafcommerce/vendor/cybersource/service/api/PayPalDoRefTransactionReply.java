/**
 * PayPalDoRefTransactionReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalDoRefTransactionReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String paypalBillingAgreementId;

    private java.lang.String transactionId;

    private java.lang.String paypalTransactionType;

    private java.lang.String paypalPaymentType;

    private java.lang.String paypalOrderTime;

    private java.lang.String paypalAmount;

    private java.lang.String currency;

    private java.lang.String paypalTaxAmount;

    private java.lang.String paypalExchangeRate;

    private java.lang.String paypalPaymentStatus;

    private java.lang.String paypalPendingReason;

    private java.lang.String paypalReasonCode;

    private java.lang.String errorCode;

    private java.lang.String correlationID;

    public PayPalDoRefTransactionReply() {
    }

    public PayPalDoRefTransactionReply(
           java.math.BigInteger reasonCode,
           java.lang.String paypalBillingAgreementId,
           java.lang.String transactionId,
           java.lang.String paypalTransactionType,
           java.lang.String paypalPaymentType,
           java.lang.String paypalOrderTime,
           java.lang.String paypalAmount,
           java.lang.String currency,
           java.lang.String paypalTaxAmount,
           java.lang.String paypalExchangeRate,
           java.lang.String paypalPaymentStatus,
           java.lang.String paypalPendingReason,
           java.lang.String paypalReasonCode,
           java.lang.String errorCode,
           java.lang.String correlationID) {
           this.reasonCode = reasonCode;
           this.paypalBillingAgreementId = paypalBillingAgreementId;
           this.transactionId = transactionId;
           this.paypalTransactionType = paypalTransactionType;
           this.paypalPaymentType = paypalPaymentType;
           this.paypalOrderTime = paypalOrderTime;
           this.paypalAmount = paypalAmount;
           this.currency = currency;
           this.paypalTaxAmount = paypalTaxAmount;
           this.paypalExchangeRate = paypalExchangeRate;
           this.paypalPaymentStatus = paypalPaymentStatus;
           this.paypalPendingReason = paypalPendingReason;
           this.paypalReasonCode = paypalReasonCode;
           this.errorCode = errorCode;
           this.correlationID = correlationID;
    }


    /**
     * Gets the reasonCode value for this PayPalDoRefTransactionReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalDoRefTransactionReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the paypalBillingAgreementId value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalBillingAgreementId
     */
    public java.lang.String getPaypalBillingAgreementId() {
        return paypalBillingAgreementId;
    }


    /**
     * Sets the paypalBillingAgreementId value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalBillingAgreementId
     */
    public void setPaypalBillingAgreementId(java.lang.String paypalBillingAgreementId) {
        this.paypalBillingAgreementId = paypalBillingAgreementId;
    }


    /**
     * Gets the transactionId value for this PayPalDoRefTransactionReply.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this PayPalDoRefTransactionReply.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the paypalTransactionType value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalTransactionType
     */
    public java.lang.String getPaypalTransactionType() {
        return paypalTransactionType;
    }


    /**
     * Sets the paypalTransactionType value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalTransactionType
     */
    public void setPaypalTransactionType(java.lang.String paypalTransactionType) {
        this.paypalTransactionType = paypalTransactionType;
    }


    /**
     * Gets the paypalPaymentType value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalPaymentType
     */
    public java.lang.String getPaypalPaymentType() {
        return paypalPaymentType;
    }


    /**
     * Sets the paypalPaymentType value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalPaymentType
     */
    public void setPaypalPaymentType(java.lang.String paypalPaymentType) {
        this.paypalPaymentType = paypalPaymentType;
    }


    /**
     * Gets the paypalOrderTime value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalOrderTime
     */
    public java.lang.String getPaypalOrderTime() {
        return paypalOrderTime;
    }


    /**
     * Sets the paypalOrderTime value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalOrderTime
     */
    public void setPaypalOrderTime(java.lang.String paypalOrderTime) {
        this.paypalOrderTime = paypalOrderTime;
    }


    /**
     * Gets the paypalAmount value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalAmount
     */
    public java.lang.String getPaypalAmount() {
        return paypalAmount;
    }


    /**
     * Sets the paypalAmount value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalAmount
     */
    public void setPaypalAmount(java.lang.String paypalAmount) {
        this.paypalAmount = paypalAmount;
    }


    /**
     * Gets the currency value for this PayPalDoRefTransactionReply.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this PayPalDoRefTransactionReply.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the paypalTaxAmount value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalTaxAmount
     */
    public java.lang.String getPaypalTaxAmount() {
        return paypalTaxAmount;
    }


    /**
     * Sets the paypalTaxAmount value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalTaxAmount
     */
    public void setPaypalTaxAmount(java.lang.String paypalTaxAmount) {
        this.paypalTaxAmount = paypalTaxAmount;
    }


    /**
     * Gets the paypalExchangeRate value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalExchangeRate
     */
    public java.lang.String getPaypalExchangeRate() {
        return paypalExchangeRate;
    }


    /**
     * Sets the paypalExchangeRate value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalExchangeRate
     */
    public void setPaypalExchangeRate(java.lang.String paypalExchangeRate) {
        this.paypalExchangeRate = paypalExchangeRate;
    }


    /**
     * Gets the paypalPaymentStatus value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalPaymentStatus
     */
    public java.lang.String getPaypalPaymentStatus() {
        return paypalPaymentStatus;
    }


    /**
     * Sets the paypalPaymentStatus value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalPaymentStatus
     */
    public void setPaypalPaymentStatus(java.lang.String paypalPaymentStatus) {
        this.paypalPaymentStatus = paypalPaymentStatus;
    }


    /**
     * Gets the paypalPendingReason value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalPendingReason
     */
    public java.lang.String getPaypalPendingReason() {
        return paypalPendingReason;
    }


    /**
     * Sets the paypalPendingReason value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalPendingReason
     */
    public void setPaypalPendingReason(java.lang.String paypalPendingReason) {
        this.paypalPendingReason = paypalPendingReason;
    }


    /**
     * Gets the paypalReasonCode value for this PayPalDoRefTransactionReply.
     * 
     * @return paypalReasonCode
     */
    public java.lang.String getPaypalReasonCode() {
        return paypalReasonCode;
    }


    /**
     * Sets the paypalReasonCode value for this PayPalDoRefTransactionReply.
     * 
     * @param paypalReasonCode
     */
    public void setPaypalReasonCode(java.lang.String paypalReasonCode) {
        this.paypalReasonCode = paypalReasonCode;
    }


    /**
     * Gets the errorCode value for this PayPalDoRefTransactionReply.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this PayPalDoRefTransactionReply.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the correlationID value for this PayPalDoRefTransactionReply.
     * 
     * @return correlationID
     */
    public java.lang.String getCorrelationID() {
        return correlationID;
    }


    /**
     * Sets the correlationID value for this PayPalDoRefTransactionReply.
     * 
     * @param correlationID
     */
    public void setCorrelationID(java.lang.String correlationID) {
        this.correlationID = correlationID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalDoRefTransactionReply)) return false;
        PayPalDoRefTransactionReply other = (PayPalDoRefTransactionReply) obj;
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
            ((this.paypalBillingAgreementId==null && other.getPaypalBillingAgreementId()==null) || 
             (this.paypalBillingAgreementId!=null &&
              this.paypalBillingAgreementId.equals(other.getPaypalBillingAgreementId()))) &&
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.paypalTransactionType==null && other.getPaypalTransactionType()==null) || 
             (this.paypalTransactionType!=null &&
              this.paypalTransactionType.equals(other.getPaypalTransactionType()))) &&
            ((this.paypalPaymentType==null && other.getPaypalPaymentType()==null) || 
             (this.paypalPaymentType!=null &&
              this.paypalPaymentType.equals(other.getPaypalPaymentType()))) &&
            ((this.paypalOrderTime==null && other.getPaypalOrderTime()==null) || 
             (this.paypalOrderTime!=null &&
              this.paypalOrderTime.equals(other.getPaypalOrderTime()))) &&
            ((this.paypalAmount==null && other.getPaypalAmount()==null) || 
             (this.paypalAmount!=null &&
              this.paypalAmount.equals(other.getPaypalAmount()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
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
            ((this.paypalReasonCode==null && other.getPaypalReasonCode()==null) || 
             (this.paypalReasonCode!=null &&
              this.paypalReasonCode.equals(other.getPaypalReasonCode()))) &&
            ((this.errorCode==null && other.getErrorCode()==null) || 
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.correlationID==null && other.getCorrelationID()==null) || 
             (this.correlationID!=null &&
              this.correlationID.equals(other.getCorrelationID())));
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
        if (getPaypalBillingAgreementId() != null) {
            _hashCode += getPaypalBillingAgreementId().hashCode();
        }
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getPaypalTransactionType() != null) {
            _hashCode += getPaypalTransactionType().hashCode();
        }
        if (getPaypalPaymentType() != null) {
            _hashCode += getPaypalPaymentType().hashCode();
        }
        if (getPaypalOrderTime() != null) {
            _hashCode += getPaypalOrderTime().hashCode();
        }
        if (getPaypalAmount() != null) {
            _hashCode += getPaypalAmount().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
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
        if (getPaypalReasonCode() != null) {
            _hashCode += getPaypalReasonCode().hashCode();
        }
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getCorrelationID() != null) {
            _hashCode += getCorrelationID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalDoRefTransactionReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoRefTransactionReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalBillingAgreementId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementId"));
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
        elemField.setFieldName("paypalTransactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalTransactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalPaymentType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalPaymentType"));
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
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "currency"));
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
        elemField.setFieldName("paypalReasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalReasonCode"));
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
        elemField.setFieldName("correlationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "correlationID"));
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
