/**
 * PayPalCreditService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalCreditService  implements java.io.Serializable {
    private java.lang.String payPalPaymentRequestID;

    private java.lang.String reconciliationID;

    private java.lang.String payPalPaymentRequestToken;

    private java.lang.String run;  // attribute

    public PayPalCreditService() {
    }

    public PayPalCreditService(
           java.lang.String payPalPaymentRequestID,
           java.lang.String reconciliationID,
           java.lang.String payPalPaymentRequestToken,
           java.lang.String run) {
           this.payPalPaymentRequestID = payPalPaymentRequestID;
           this.reconciliationID = reconciliationID;
           this.payPalPaymentRequestToken = payPalPaymentRequestToken;
           this.run = run;
    }


    /**
     * Gets the payPalPaymentRequestID value for this PayPalCreditService.
     * 
     * @return payPalPaymentRequestID
     */
    public java.lang.String getPayPalPaymentRequestID() {
        return payPalPaymentRequestID;
    }


    /**
     * Sets the payPalPaymentRequestID value for this PayPalCreditService.
     * 
     * @param payPalPaymentRequestID
     */
    public void setPayPalPaymentRequestID(java.lang.String payPalPaymentRequestID) {
        this.payPalPaymentRequestID = payPalPaymentRequestID;
    }


    /**
     * Gets the reconciliationID value for this PayPalCreditService.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this PayPalCreditService.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the payPalPaymentRequestToken value for this PayPalCreditService.
     * 
     * @return payPalPaymentRequestToken
     */
    public java.lang.String getPayPalPaymentRequestToken() {
        return payPalPaymentRequestToken;
    }


    /**
     * Sets the payPalPaymentRequestToken value for this PayPalCreditService.
     * 
     * @param payPalPaymentRequestToken
     */
    public void setPayPalPaymentRequestToken(java.lang.String payPalPaymentRequestToken) {
        this.payPalPaymentRequestToken = payPalPaymentRequestToken;
    }


    /**
     * Gets the run value for this PayPalCreditService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayPalCreditService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalCreditService)) return false;
        PayPalCreditService other = (PayPalCreditService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.payPalPaymentRequestID==null && other.getPayPalPaymentRequestID()==null) || 
             (this.payPalPaymentRequestID!=null &&
              this.payPalPaymentRequestID.equals(other.getPayPalPaymentRequestID()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.payPalPaymentRequestToken==null && other.getPayPalPaymentRequestToken()==null) || 
             (this.payPalPaymentRequestToken!=null &&
              this.payPalPaymentRequestToken.equals(other.getPayPalPaymentRequestToken()))) &&
            ((this.run==null && other.getRun()==null) || 
             (this.run!=null &&
              this.run.equals(other.getRun())));
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
        if (getPayPalPaymentRequestID() != null) {
            _hashCode += getPayPalPaymentRequestID().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getPayPalPaymentRequestToken() != null) {
            _hashCode += getPayPalPaymentRequestToken().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalCreditService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreditService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalPaymentRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalPaymentRequestID"));
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
        elemField.setFieldName("payPalPaymentRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalPaymentRequestToken"));
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
