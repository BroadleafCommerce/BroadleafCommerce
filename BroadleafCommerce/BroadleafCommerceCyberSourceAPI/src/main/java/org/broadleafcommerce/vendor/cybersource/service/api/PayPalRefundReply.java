/**
 * PayPalRefundReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalRefundReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String transactionId;

    private java.lang.String paypalNetRefundAmount;

    private java.lang.String paypalFeeRefundAmount;

    private java.lang.String paypalGrossRefundAmount;

    private java.lang.String correlationID;

    private java.lang.String errorCode;

    public PayPalRefundReply() {
    }

    public PayPalRefundReply(
           java.math.BigInteger reasonCode,
           java.lang.String transactionId,
           java.lang.String paypalNetRefundAmount,
           java.lang.String paypalFeeRefundAmount,
           java.lang.String paypalGrossRefundAmount,
           java.lang.String correlationID,
           java.lang.String errorCode) {
           this.reasonCode = reasonCode;
           this.transactionId = transactionId;
           this.paypalNetRefundAmount = paypalNetRefundAmount;
           this.paypalFeeRefundAmount = paypalFeeRefundAmount;
           this.paypalGrossRefundAmount = paypalGrossRefundAmount;
           this.correlationID = correlationID;
           this.errorCode = errorCode;
    }


    /**
     * Gets the reasonCode value for this PayPalRefundReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalRefundReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the transactionId value for this PayPalRefundReply.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this PayPalRefundReply.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the paypalNetRefundAmount value for this PayPalRefundReply.
     * 
     * @return paypalNetRefundAmount
     */
    public java.lang.String getPaypalNetRefundAmount() {
        return paypalNetRefundAmount;
    }


    /**
     * Sets the paypalNetRefundAmount value for this PayPalRefundReply.
     * 
     * @param paypalNetRefundAmount
     */
    public void setPaypalNetRefundAmount(java.lang.String paypalNetRefundAmount) {
        this.paypalNetRefundAmount = paypalNetRefundAmount;
    }


    /**
     * Gets the paypalFeeRefundAmount value for this PayPalRefundReply.
     * 
     * @return paypalFeeRefundAmount
     */
    public java.lang.String getPaypalFeeRefundAmount() {
        return paypalFeeRefundAmount;
    }


    /**
     * Sets the paypalFeeRefundAmount value for this PayPalRefundReply.
     * 
     * @param paypalFeeRefundAmount
     */
    public void setPaypalFeeRefundAmount(java.lang.String paypalFeeRefundAmount) {
        this.paypalFeeRefundAmount = paypalFeeRefundAmount;
    }


    /**
     * Gets the paypalGrossRefundAmount value for this PayPalRefundReply.
     * 
     * @return paypalGrossRefundAmount
     */
    public java.lang.String getPaypalGrossRefundAmount() {
        return paypalGrossRefundAmount;
    }


    /**
     * Sets the paypalGrossRefundAmount value for this PayPalRefundReply.
     * 
     * @param paypalGrossRefundAmount
     */
    public void setPaypalGrossRefundAmount(java.lang.String paypalGrossRefundAmount) {
        this.paypalGrossRefundAmount = paypalGrossRefundAmount;
    }


    /**
     * Gets the correlationID value for this PayPalRefundReply.
     * 
     * @return correlationID
     */
    public java.lang.String getCorrelationID() {
        return correlationID;
    }


    /**
     * Sets the correlationID value for this PayPalRefundReply.
     * 
     * @param correlationID
     */
    public void setCorrelationID(java.lang.String correlationID) {
        this.correlationID = correlationID;
    }


    /**
     * Gets the errorCode value for this PayPalRefundReply.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this PayPalRefundReply.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalRefundReply)) return false;
        PayPalRefundReply other = (PayPalRefundReply) obj;
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
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.paypalNetRefundAmount==null && other.getPaypalNetRefundAmount()==null) || 
             (this.paypalNetRefundAmount!=null &&
              this.paypalNetRefundAmount.equals(other.getPaypalNetRefundAmount()))) &&
            ((this.paypalFeeRefundAmount==null && other.getPaypalFeeRefundAmount()==null) || 
             (this.paypalFeeRefundAmount!=null &&
              this.paypalFeeRefundAmount.equals(other.getPaypalFeeRefundAmount()))) &&
            ((this.paypalGrossRefundAmount==null && other.getPaypalGrossRefundAmount()==null) || 
             (this.paypalGrossRefundAmount!=null &&
              this.paypalGrossRefundAmount.equals(other.getPaypalGrossRefundAmount()))) &&
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
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getPaypalNetRefundAmount() != null) {
            _hashCode += getPaypalNetRefundAmount().hashCode();
        }
        if (getPaypalFeeRefundAmount() != null) {
            _hashCode += getPaypalFeeRefundAmount().hashCode();
        }
        if (getPaypalGrossRefundAmount() != null) {
            _hashCode += getPaypalGrossRefundAmount().hashCode();
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
        new org.apache.axis.description.TypeDesc(PayPalRefundReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalRefundReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
        elemField.setFieldName("paypalNetRefundAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalNetRefundAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalFeeRefundAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalFeeRefundAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalGrossRefundAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalGrossRefundAmount"));
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
