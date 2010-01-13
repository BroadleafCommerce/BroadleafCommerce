/**
 * PayPalDoCaptureReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalDoCaptureReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String authorizationId;

    private java.lang.String transactionId;

    private java.lang.String parentTransactionId;

    private java.lang.String paypalReceiptId;

    private java.lang.String paypalTransactiontype;

    private java.lang.String paypalPaymentType;

    private java.lang.String paypalOrderTime;

    private java.lang.String paypalPaymentGrossAmount;

    private java.lang.String paypalFeeAmount;

    private java.lang.String paypalTaxAmount;

    private java.lang.String paypalExchangeRate;

    private java.lang.String paypalPaymentStatus;

    private java.lang.String amount;

    private java.lang.String currency;

    private java.lang.String correlationID;

    private java.lang.String errorCode;

    public PayPalDoCaptureReply() {
    }

    public PayPalDoCaptureReply(
           java.math.BigInteger reasonCode,
           java.lang.String authorizationId,
           java.lang.String transactionId,
           java.lang.String parentTransactionId,
           java.lang.String paypalReceiptId,
           java.lang.String paypalTransactiontype,
           java.lang.String paypalPaymentType,
           java.lang.String paypalOrderTime,
           java.lang.String paypalPaymentGrossAmount,
           java.lang.String paypalFeeAmount,
           java.lang.String paypalTaxAmount,
           java.lang.String paypalExchangeRate,
           java.lang.String paypalPaymentStatus,
           java.lang.String amount,
           java.lang.String currency,
           java.lang.String correlationID,
           java.lang.String errorCode) {
           this.reasonCode = reasonCode;
           this.authorizationId = authorizationId;
           this.transactionId = transactionId;
           this.parentTransactionId = parentTransactionId;
           this.paypalReceiptId = paypalReceiptId;
           this.paypalTransactiontype = paypalTransactiontype;
           this.paypalPaymentType = paypalPaymentType;
           this.paypalOrderTime = paypalOrderTime;
           this.paypalPaymentGrossAmount = paypalPaymentGrossAmount;
           this.paypalFeeAmount = paypalFeeAmount;
           this.paypalTaxAmount = paypalTaxAmount;
           this.paypalExchangeRate = paypalExchangeRate;
           this.paypalPaymentStatus = paypalPaymentStatus;
           this.amount = amount;
           this.currency = currency;
           this.correlationID = correlationID;
           this.errorCode = errorCode;
    }


    /**
     * Gets the reasonCode value for this PayPalDoCaptureReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalDoCaptureReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the authorizationId value for this PayPalDoCaptureReply.
     * 
     * @return authorizationId
     */
    public java.lang.String getAuthorizationId() {
        return authorizationId;
    }


    /**
     * Sets the authorizationId value for this PayPalDoCaptureReply.
     * 
     * @param authorizationId
     */
    public void setAuthorizationId(java.lang.String authorizationId) {
        this.authorizationId = authorizationId;
    }


    /**
     * Gets the transactionId value for this PayPalDoCaptureReply.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this PayPalDoCaptureReply.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the parentTransactionId value for this PayPalDoCaptureReply.
     * 
     * @return parentTransactionId
     */
    public java.lang.String getParentTransactionId() {
        return parentTransactionId;
    }


    /**
     * Sets the parentTransactionId value for this PayPalDoCaptureReply.
     * 
     * @param parentTransactionId
     */
    public void setParentTransactionId(java.lang.String parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }


    /**
     * Gets the paypalReceiptId value for this PayPalDoCaptureReply.
     * 
     * @return paypalReceiptId
     */
    public java.lang.String getPaypalReceiptId() {
        return paypalReceiptId;
    }


    /**
     * Sets the paypalReceiptId value for this PayPalDoCaptureReply.
     * 
     * @param paypalReceiptId
     */
    public void setPaypalReceiptId(java.lang.String paypalReceiptId) {
        this.paypalReceiptId = paypalReceiptId;
    }


    /**
     * Gets the paypalTransactiontype value for this PayPalDoCaptureReply.
     * 
     * @return paypalTransactiontype
     */
    public java.lang.String getPaypalTransactiontype() {
        return paypalTransactiontype;
    }


    /**
     * Sets the paypalTransactiontype value for this PayPalDoCaptureReply.
     * 
     * @param paypalTransactiontype
     */
    public void setPaypalTransactiontype(java.lang.String paypalTransactiontype) {
        this.paypalTransactiontype = paypalTransactiontype;
    }


    /**
     * Gets the paypalPaymentType value for this PayPalDoCaptureReply.
     * 
     * @return paypalPaymentType
     */
    public java.lang.String getPaypalPaymentType() {
        return paypalPaymentType;
    }


    /**
     * Sets the paypalPaymentType value for this PayPalDoCaptureReply.
     * 
     * @param paypalPaymentType
     */
    public void setPaypalPaymentType(java.lang.String paypalPaymentType) {
        this.paypalPaymentType = paypalPaymentType;
    }


    /**
     * Gets the paypalOrderTime value for this PayPalDoCaptureReply.
     * 
     * @return paypalOrderTime
     */
    public java.lang.String getPaypalOrderTime() {
        return paypalOrderTime;
    }


    /**
     * Sets the paypalOrderTime value for this PayPalDoCaptureReply.
     * 
     * @param paypalOrderTime
     */
    public void setPaypalOrderTime(java.lang.String paypalOrderTime) {
        this.paypalOrderTime = paypalOrderTime;
    }


    /**
     * Gets the paypalPaymentGrossAmount value for this PayPalDoCaptureReply.
     * 
     * @return paypalPaymentGrossAmount
     */
    public java.lang.String getPaypalPaymentGrossAmount() {
        return paypalPaymentGrossAmount;
    }


    /**
     * Sets the paypalPaymentGrossAmount value for this PayPalDoCaptureReply.
     * 
     * @param paypalPaymentGrossAmount
     */
    public void setPaypalPaymentGrossAmount(java.lang.String paypalPaymentGrossAmount) {
        this.paypalPaymentGrossAmount = paypalPaymentGrossAmount;
    }


    /**
     * Gets the paypalFeeAmount value for this PayPalDoCaptureReply.
     * 
     * @return paypalFeeAmount
     */
    public java.lang.String getPaypalFeeAmount() {
        return paypalFeeAmount;
    }


    /**
     * Sets the paypalFeeAmount value for this PayPalDoCaptureReply.
     * 
     * @param paypalFeeAmount
     */
    public void setPaypalFeeAmount(java.lang.String paypalFeeAmount) {
        this.paypalFeeAmount = paypalFeeAmount;
    }


    /**
     * Gets the paypalTaxAmount value for this PayPalDoCaptureReply.
     * 
     * @return paypalTaxAmount
     */
    public java.lang.String getPaypalTaxAmount() {
        return paypalTaxAmount;
    }


    /**
     * Sets the paypalTaxAmount value for this PayPalDoCaptureReply.
     * 
     * @param paypalTaxAmount
     */
    public void setPaypalTaxAmount(java.lang.String paypalTaxAmount) {
        this.paypalTaxAmount = paypalTaxAmount;
    }


    /**
     * Gets the paypalExchangeRate value for this PayPalDoCaptureReply.
     * 
     * @return paypalExchangeRate
     */
    public java.lang.String getPaypalExchangeRate() {
        return paypalExchangeRate;
    }


    /**
     * Sets the paypalExchangeRate value for this PayPalDoCaptureReply.
     * 
     * @param paypalExchangeRate
     */
    public void setPaypalExchangeRate(java.lang.String paypalExchangeRate) {
        this.paypalExchangeRate = paypalExchangeRate;
    }


    /**
     * Gets the paypalPaymentStatus value for this PayPalDoCaptureReply.
     * 
     * @return paypalPaymentStatus
     */
    public java.lang.String getPaypalPaymentStatus() {
        return paypalPaymentStatus;
    }


    /**
     * Sets the paypalPaymentStatus value for this PayPalDoCaptureReply.
     * 
     * @param paypalPaymentStatus
     */
    public void setPaypalPaymentStatus(java.lang.String paypalPaymentStatus) {
        this.paypalPaymentStatus = paypalPaymentStatus;
    }


    /**
     * Gets the amount value for this PayPalDoCaptureReply.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this PayPalDoCaptureReply.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the currency value for this PayPalDoCaptureReply.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this PayPalDoCaptureReply.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the correlationID value for this PayPalDoCaptureReply.
     * 
     * @return correlationID
     */
    public java.lang.String getCorrelationID() {
        return correlationID;
    }


    /**
     * Sets the correlationID value for this PayPalDoCaptureReply.
     * 
     * @param correlationID
     */
    public void setCorrelationID(java.lang.String correlationID) {
        this.correlationID = correlationID;
    }


    /**
     * Gets the errorCode value for this PayPalDoCaptureReply.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this PayPalDoCaptureReply.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalDoCaptureReply)) return false;
        PayPalDoCaptureReply other = (PayPalDoCaptureReply) obj;
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
            ((this.authorizationId==null && other.getAuthorizationId()==null) || 
             (this.authorizationId!=null &&
              this.authorizationId.equals(other.getAuthorizationId()))) &&
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.parentTransactionId==null && other.getParentTransactionId()==null) || 
             (this.parentTransactionId!=null &&
              this.parentTransactionId.equals(other.getParentTransactionId()))) &&
            ((this.paypalReceiptId==null && other.getPaypalReceiptId()==null) || 
             (this.paypalReceiptId!=null &&
              this.paypalReceiptId.equals(other.getPaypalReceiptId()))) &&
            ((this.paypalTransactiontype==null && other.getPaypalTransactiontype()==null) || 
             (this.paypalTransactiontype!=null &&
              this.paypalTransactiontype.equals(other.getPaypalTransactiontype()))) &&
            ((this.paypalPaymentType==null && other.getPaypalPaymentType()==null) || 
             (this.paypalPaymentType!=null &&
              this.paypalPaymentType.equals(other.getPaypalPaymentType()))) &&
            ((this.paypalOrderTime==null && other.getPaypalOrderTime()==null) || 
             (this.paypalOrderTime!=null &&
              this.paypalOrderTime.equals(other.getPaypalOrderTime()))) &&
            ((this.paypalPaymentGrossAmount==null && other.getPaypalPaymentGrossAmount()==null) || 
             (this.paypalPaymentGrossAmount!=null &&
              this.paypalPaymentGrossAmount.equals(other.getPaypalPaymentGrossAmount()))) &&
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
              this.errorCode.equals(other.getErrorCode())));
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
        if (getAuthorizationId() != null) {
            _hashCode += getAuthorizationId().hashCode();
        }
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getParentTransactionId() != null) {
            _hashCode += getParentTransactionId().hashCode();
        }
        if (getPaypalReceiptId() != null) {
            _hashCode += getPaypalReceiptId().hashCode();
        }
        if (getPaypalTransactiontype() != null) {
            _hashCode += getPaypalTransactiontype().hashCode();
        }
        if (getPaypalPaymentType() != null) {
            _hashCode += getPaypalPaymentType().hashCode();
        }
        if (getPaypalOrderTime() != null) {
            _hashCode += getPaypalOrderTime().hashCode();
        }
        if (getPaypalPaymentGrossAmount() != null) {
            _hashCode += getPaypalPaymentGrossAmount().hashCode();
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalDoCaptureReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoCaptureReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authorizationId"));
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
        elemField.setFieldName("parentTransactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "parentTransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalReceiptId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalReceiptId"));
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
        elemField.setFieldName("paypalPaymentGrossAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalPaymentGrossAmount"));
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
