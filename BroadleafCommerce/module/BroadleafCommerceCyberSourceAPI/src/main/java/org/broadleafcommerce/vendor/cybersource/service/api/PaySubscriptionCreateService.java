/**
 * PaySubscriptionCreateService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PaySubscriptionCreateService  implements java.io.Serializable {
    private java.lang.String paymentRequestID;

    private java.lang.String paymentRequestToken;

    private java.lang.String disableAutoAuth;

    private java.lang.String run;  // attribute

    public PaySubscriptionCreateService() {
    }

    public PaySubscriptionCreateService(
           java.lang.String paymentRequestID,
           java.lang.String paymentRequestToken,
           java.lang.String disableAutoAuth,
           java.lang.String run) {
           this.paymentRequestID = paymentRequestID;
           this.paymentRequestToken = paymentRequestToken;
           this.disableAutoAuth = disableAutoAuth;
           this.run = run;
    }


    /**
     * Gets the paymentRequestID value for this PaySubscriptionCreateService.
     * 
     * @return paymentRequestID
     */
    public java.lang.String getPaymentRequestID() {
        return paymentRequestID;
    }


    /**
     * Sets the paymentRequestID value for this PaySubscriptionCreateService.
     * 
     * @param paymentRequestID
     */
    public void setPaymentRequestID(java.lang.String paymentRequestID) {
        this.paymentRequestID = paymentRequestID;
    }


    /**
     * Gets the paymentRequestToken value for this PaySubscriptionCreateService.
     * 
     * @return paymentRequestToken
     */
    public java.lang.String getPaymentRequestToken() {
        return paymentRequestToken;
    }


    /**
     * Sets the paymentRequestToken value for this PaySubscriptionCreateService.
     * 
     * @param paymentRequestToken
     */
    public void setPaymentRequestToken(java.lang.String paymentRequestToken) {
        this.paymentRequestToken = paymentRequestToken;
    }


    /**
     * Gets the disableAutoAuth value for this PaySubscriptionCreateService.
     * 
     * @return disableAutoAuth
     */
    public java.lang.String getDisableAutoAuth() {
        return disableAutoAuth;
    }


    /**
     * Sets the disableAutoAuth value for this PaySubscriptionCreateService.
     * 
     * @param disableAutoAuth
     */
    public void setDisableAutoAuth(java.lang.String disableAutoAuth) {
        this.disableAutoAuth = disableAutoAuth;
    }


    /**
     * Gets the run value for this PaySubscriptionCreateService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PaySubscriptionCreateService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PaySubscriptionCreateService)) return false;
        PaySubscriptionCreateService other = (PaySubscriptionCreateService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paymentRequestID==null && other.getPaymentRequestID()==null) || 
             (this.paymentRequestID!=null &&
              this.paymentRequestID.equals(other.getPaymentRequestID()))) &&
            ((this.paymentRequestToken==null && other.getPaymentRequestToken()==null) || 
             (this.paymentRequestToken!=null &&
              this.paymentRequestToken.equals(other.getPaymentRequestToken()))) &&
            ((this.disableAutoAuth==null && other.getDisableAutoAuth()==null) || 
             (this.disableAutoAuth!=null &&
              this.disableAutoAuth.equals(other.getDisableAutoAuth()))) &&
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
        if (getPaymentRequestID() != null) {
            _hashCode += getPaymentRequestID().hashCode();
        }
        if (getPaymentRequestToken() != null) {
            _hashCode += getPaymentRequestToken().hashCode();
        }
        if (getDisableAutoAuth() != null) {
            _hashCode += getDisableAutoAuth().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PaySubscriptionCreateService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionCreateService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("disableAutoAuth");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "disableAutoAuth"));
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
