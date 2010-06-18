/**
 * PayPalAuthorizationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalAuthorizationService  implements java.io.Serializable {
    private java.lang.String paypalOrderId;

    private java.lang.String paypalEcOrderSetupRequestID;

    private java.lang.String paypalEcOrderSetupRequestToken;

    private java.lang.String paypalDoRefTransactionRequestID;

    private java.lang.String paypalDoRefTransactionRequestToken;

    private java.lang.String paypalCustomerEmail;

    private java.lang.String run;  // attribute

    public PayPalAuthorizationService() {
    }

    public PayPalAuthorizationService(
           java.lang.String paypalOrderId,
           java.lang.String paypalEcOrderSetupRequestID,
           java.lang.String paypalEcOrderSetupRequestToken,
           java.lang.String paypalDoRefTransactionRequestID,
           java.lang.String paypalDoRefTransactionRequestToken,
           java.lang.String paypalCustomerEmail,
           java.lang.String run) {
           this.paypalOrderId = paypalOrderId;
           this.paypalEcOrderSetupRequestID = paypalEcOrderSetupRequestID;
           this.paypalEcOrderSetupRequestToken = paypalEcOrderSetupRequestToken;
           this.paypalDoRefTransactionRequestID = paypalDoRefTransactionRequestID;
           this.paypalDoRefTransactionRequestToken = paypalDoRefTransactionRequestToken;
           this.paypalCustomerEmail = paypalCustomerEmail;
           this.run = run;
    }


    /**
     * Gets the paypalOrderId value for this PayPalAuthorizationService.
     * 
     * @return paypalOrderId
     */
    public java.lang.String getPaypalOrderId() {
        return paypalOrderId;
    }


    /**
     * Sets the paypalOrderId value for this PayPalAuthorizationService.
     * 
     * @param paypalOrderId
     */
    public void setPaypalOrderId(java.lang.String paypalOrderId) {
        this.paypalOrderId = paypalOrderId;
    }


    /**
     * Gets the paypalEcOrderSetupRequestID value for this PayPalAuthorizationService.
     * 
     * @return paypalEcOrderSetupRequestID
     */
    public java.lang.String getPaypalEcOrderSetupRequestID() {
        return paypalEcOrderSetupRequestID;
    }


    /**
     * Sets the paypalEcOrderSetupRequestID value for this PayPalAuthorizationService.
     * 
     * @param paypalEcOrderSetupRequestID
     */
    public void setPaypalEcOrderSetupRequestID(java.lang.String paypalEcOrderSetupRequestID) {
        this.paypalEcOrderSetupRequestID = paypalEcOrderSetupRequestID;
    }


    /**
     * Gets the paypalEcOrderSetupRequestToken value for this PayPalAuthorizationService.
     * 
     * @return paypalEcOrderSetupRequestToken
     */
    public java.lang.String getPaypalEcOrderSetupRequestToken() {
        return paypalEcOrderSetupRequestToken;
    }


    /**
     * Sets the paypalEcOrderSetupRequestToken value for this PayPalAuthorizationService.
     * 
     * @param paypalEcOrderSetupRequestToken
     */
    public void setPaypalEcOrderSetupRequestToken(java.lang.String paypalEcOrderSetupRequestToken) {
        this.paypalEcOrderSetupRequestToken = paypalEcOrderSetupRequestToken;
    }


    /**
     * Gets the paypalDoRefTransactionRequestID value for this PayPalAuthorizationService.
     * 
     * @return paypalDoRefTransactionRequestID
     */
    public java.lang.String getPaypalDoRefTransactionRequestID() {
        return paypalDoRefTransactionRequestID;
    }


    /**
     * Sets the paypalDoRefTransactionRequestID value for this PayPalAuthorizationService.
     * 
     * @param paypalDoRefTransactionRequestID
     */
    public void setPaypalDoRefTransactionRequestID(java.lang.String paypalDoRefTransactionRequestID) {
        this.paypalDoRefTransactionRequestID = paypalDoRefTransactionRequestID;
    }


    /**
     * Gets the paypalDoRefTransactionRequestToken value for this PayPalAuthorizationService.
     * 
     * @return paypalDoRefTransactionRequestToken
     */
    public java.lang.String getPaypalDoRefTransactionRequestToken() {
        return paypalDoRefTransactionRequestToken;
    }


    /**
     * Sets the paypalDoRefTransactionRequestToken value for this PayPalAuthorizationService.
     * 
     * @param paypalDoRefTransactionRequestToken
     */
    public void setPaypalDoRefTransactionRequestToken(java.lang.String paypalDoRefTransactionRequestToken) {
        this.paypalDoRefTransactionRequestToken = paypalDoRefTransactionRequestToken;
    }


    /**
     * Gets the paypalCustomerEmail value for this PayPalAuthorizationService.
     * 
     * @return paypalCustomerEmail
     */
    public java.lang.String getPaypalCustomerEmail() {
        return paypalCustomerEmail;
    }


    /**
     * Sets the paypalCustomerEmail value for this PayPalAuthorizationService.
     * 
     * @param paypalCustomerEmail
     */
    public void setPaypalCustomerEmail(java.lang.String paypalCustomerEmail) {
        this.paypalCustomerEmail = paypalCustomerEmail;
    }


    /**
     * Gets the run value for this PayPalAuthorizationService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayPalAuthorizationService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalAuthorizationService)) return false;
        PayPalAuthorizationService other = (PayPalAuthorizationService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paypalOrderId==null && other.getPaypalOrderId()==null) || 
             (this.paypalOrderId!=null &&
              this.paypalOrderId.equals(other.getPaypalOrderId()))) &&
            ((this.paypalEcOrderSetupRequestID==null && other.getPaypalEcOrderSetupRequestID()==null) || 
             (this.paypalEcOrderSetupRequestID!=null &&
              this.paypalEcOrderSetupRequestID.equals(other.getPaypalEcOrderSetupRequestID()))) &&
            ((this.paypalEcOrderSetupRequestToken==null && other.getPaypalEcOrderSetupRequestToken()==null) || 
             (this.paypalEcOrderSetupRequestToken!=null &&
              this.paypalEcOrderSetupRequestToken.equals(other.getPaypalEcOrderSetupRequestToken()))) &&
            ((this.paypalDoRefTransactionRequestID==null && other.getPaypalDoRefTransactionRequestID()==null) || 
             (this.paypalDoRefTransactionRequestID!=null &&
              this.paypalDoRefTransactionRequestID.equals(other.getPaypalDoRefTransactionRequestID()))) &&
            ((this.paypalDoRefTransactionRequestToken==null && other.getPaypalDoRefTransactionRequestToken()==null) || 
             (this.paypalDoRefTransactionRequestToken!=null &&
              this.paypalDoRefTransactionRequestToken.equals(other.getPaypalDoRefTransactionRequestToken()))) &&
            ((this.paypalCustomerEmail==null && other.getPaypalCustomerEmail()==null) || 
             (this.paypalCustomerEmail!=null &&
              this.paypalCustomerEmail.equals(other.getPaypalCustomerEmail()))) &&
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
        if (getPaypalOrderId() != null) {
            _hashCode += getPaypalOrderId().hashCode();
        }
        if (getPaypalEcOrderSetupRequestID() != null) {
            _hashCode += getPaypalEcOrderSetupRequestID().hashCode();
        }
        if (getPaypalEcOrderSetupRequestToken() != null) {
            _hashCode += getPaypalEcOrderSetupRequestToken().hashCode();
        }
        if (getPaypalDoRefTransactionRequestID() != null) {
            _hashCode += getPaypalDoRefTransactionRequestID().hashCode();
        }
        if (getPaypalDoRefTransactionRequestToken() != null) {
            _hashCode += getPaypalDoRefTransactionRequestToken().hashCode();
        }
        if (getPaypalCustomerEmail() != null) {
            _hashCode += getPaypalCustomerEmail().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalAuthorizationService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthorizationService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalOrderId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalOrderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalEcOrderSetupRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalEcOrderSetupRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalEcOrderSetupRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalEcOrderSetupRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalDoRefTransactionRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalDoRefTransactionRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalDoRefTransactionRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalDoRefTransactionRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalCustomerEmail");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalCustomerEmail"));
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
