/**
 * PayPalAuthReversalService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalAuthReversalService  implements java.io.Serializable {
    private java.lang.String paypalAuthorizationId;

    private java.lang.String paypalEcDoPaymentRequestID;

    private java.lang.String paypalEcDoPaymentRequestToken;

    private java.lang.String paypalAuthorizationRequestID;

    private java.lang.String paypalAuthorizationRequestToken;

    private java.lang.String paypalEcOrderSetupRequestID;

    private java.lang.String paypalEcOrderSetupRequestToken;

    private java.lang.String run;  // attribute

    public PayPalAuthReversalService() {
    }

    public PayPalAuthReversalService(
           java.lang.String paypalAuthorizationId,
           java.lang.String paypalEcDoPaymentRequestID,
           java.lang.String paypalEcDoPaymentRequestToken,
           java.lang.String paypalAuthorizationRequestID,
           java.lang.String paypalAuthorizationRequestToken,
           java.lang.String paypalEcOrderSetupRequestID,
           java.lang.String paypalEcOrderSetupRequestToken,
           java.lang.String run) {
           this.paypalAuthorizationId = paypalAuthorizationId;
           this.paypalEcDoPaymentRequestID = paypalEcDoPaymentRequestID;
           this.paypalEcDoPaymentRequestToken = paypalEcDoPaymentRequestToken;
           this.paypalAuthorizationRequestID = paypalAuthorizationRequestID;
           this.paypalAuthorizationRequestToken = paypalAuthorizationRequestToken;
           this.paypalEcOrderSetupRequestID = paypalEcOrderSetupRequestID;
           this.paypalEcOrderSetupRequestToken = paypalEcOrderSetupRequestToken;
           this.run = run;
    }


    /**
     * Gets the paypalAuthorizationId value for this PayPalAuthReversalService.
     * 
     * @return paypalAuthorizationId
     */
    public java.lang.String getPaypalAuthorizationId() {
        return paypalAuthorizationId;
    }


    /**
     * Sets the paypalAuthorizationId value for this PayPalAuthReversalService.
     * 
     * @param paypalAuthorizationId
     */
    public void setPaypalAuthorizationId(java.lang.String paypalAuthorizationId) {
        this.paypalAuthorizationId = paypalAuthorizationId;
    }


    /**
     * Gets the paypalEcDoPaymentRequestID value for this PayPalAuthReversalService.
     * 
     * @return paypalEcDoPaymentRequestID
     */
    public java.lang.String getPaypalEcDoPaymentRequestID() {
        return paypalEcDoPaymentRequestID;
    }


    /**
     * Sets the paypalEcDoPaymentRequestID value for this PayPalAuthReversalService.
     * 
     * @param paypalEcDoPaymentRequestID
     */
    public void setPaypalEcDoPaymentRequestID(java.lang.String paypalEcDoPaymentRequestID) {
        this.paypalEcDoPaymentRequestID = paypalEcDoPaymentRequestID;
    }


    /**
     * Gets the paypalEcDoPaymentRequestToken value for this PayPalAuthReversalService.
     * 
     * @return paypalEcDoPaymentRequestToken
     */
    public java.lang.String getPaypalEcDoPaymentRequestToken() {
        return paypalEcDoPaymentRequestToken;
    }


    /**
     * Sets the paypalEcDoPaymentRequestToken value for this PayPalAuthReversalService.
     * 
     * @param paypalEcDoPaymentRequestToken
     */
    public void setPaypalEcDoPaymentRequestToken(java.lang.String paypalEcDoPaymentRequestToken) {
        this.paypalEcDoPaymentRequestToken = paypalEcDoPaymentRequestToken;
    }


    /**
     * Gets the paypalAuthorizationRequestID value for this PayPalAuthReversalService.
     * 
     * @return paypalAuthorizationRequestID
     */
    public java.lang.String getPaypalAuthorizationRequestID() {
        return paypalAuthorizationRequestID;
    }


    /**
     * Sets the paypalAuthorizationRequestID value for this PayPalAuthReversalService.
     * 
     * @param paypalAuthorizationRequestID
     */
    public void setPaypalAuthorizationRequestID(java.lang.String paypalAuthorizationRequestID) {
        this.paypalAuthorizationRequestID = paypalAuthorizationRequestID;
    }


    /**
     * Gets the paypalAuthorizationRequestToken value for this PayPalAuthReversalService.
     * 
     * @return paypalAuthorizationRequestToken
     */
    public java.lang.String getPaypalAuthorizationRequestToken() {
        return paypalAuthorizationRequestToken;
    }


    /**
     * Sets the paypalAuthorizationRequestToken value for this PayPalAuthReversalService.
     * 
     * @param paypalAuthorizationRequestToken
     */
    public void setPaypalAuthorizationRequestToken(java.lang.String paypalAuthorizationRequestToken) {
        this.paypalAuthorizationRequestToken = paypalAuthorizationRequestToken;
    }


    /**
     * Gets the paypalEcOrderSetupRequestID value for this PayPalAuthReversalService.
     * 
     * @return paypalEcOrderSetupRequestID
     */
    public java.lang.String getPaypalEcOrderSetupRequestID() {
        return paypalEcOrderSetupRequestID;
    }


    /**
     * Sets the paypalEcOrderSetupRequestID value for this PayPalAuthReversalService.
     * 
     * @param paypalEcOrderSetupRequestID
     */
    public void setPaypalEcOrderSetupRequestID(java.lang.String paypalEcOrderSetupRequestID) {
        this.paypalEcOrderSetupRequestID = paypalEcOrderSetupRequestID;
    }


    /**
     * Gets the paypalEcOrderSetupRequestToken value for this PayPalAuthReversalService.
     * 
     * @return paypalEcOrderSetupRequestToken
     */
    public java.lang.String getPaypalEcOrderSetupRequestToken() {
        return paypalEcOrderSetupRequestToken;
    }


    /**
     * Sets the paypalEcOrderSetupRequestToken value for this PayPalAuthReversalService.
     * 
     * @param paypalEcOrderSetupRequestToken
     */
    public void setPaypalEcOrderSetupRequestToken(java.lang.String paypalEcOrderSetupRequestToken) {
        this.paypalEcOrderSetupRequestToken = paypalEcOrderSetupRequestToken;
    }


    /**
     * Gets the run value for this PayPalAuthReversalService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayPalAuthReversalService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalAuthReversalService)) return false;
        PayPalAuthReversalService other = (PayPalAuthReversalService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paypalAuthorizationId==null && other.getPaypalAuthorizationId()==null) || 
             (this.paypalAuthorizationId!=null &&
              this.paypalAuthorizationId.equals(other.getPaypalAuthorizationId()))) &&
            ((this.paypalEcDoPaymentRequestID==null && other.getPaypalEcDoPaymentRequestID()==null) || 
             (this.paypalEcDoPaymentRequestID!=null &&
              this.paypalEcDoPaymentRequestID.equals(other.getPaypalEcDoPaymentRequestID()))) &&
            ((this.paypalEcDoPaymentRequestToken==null && other.getPaypalEcDoPaymentRequestToken()==null) || 
             (this.paypalEcDoPaymentRequestToken!=null &&
              this.paypalEcDoPaymentRequestToken.equals(other.getPaypalEcDoPaymentRequestToken()))) &&
            ((this.paypalAuthorizationRequestID==null && other.getPaypalAuthorizationRequestID()==null) || 
             (this.paypalAuthorizationRequestID!=null &&
              this.paypalAuthorizationRequestID.equals(other.getPaypalAuthorizationRequestID()))) &&
            ((this.paypalAuthorizationRequestToken==null && other.getPaypalAuthorizationRequestToken()==null) || 
             (this.paypalAuthorizationRequestToken!=null &&
              this.paypalAuthorizationRequestToken.equals(other.getPaypalAuthorizationRequestToken()))) &&
            ((this.paypalEcOrderSetupRequestID==null && other.getPaypalEcOrderSetupRequestID()==null) || 
             (this.paypalEcOrderSetupRequestID!=null &&
              this.paypalEcOrderSetupRequestID.equals(other.getPaypalEcOrderSetupRequestID()))) &&
            ((this.paypalEcOrderSetupRequestToken==null && other.getPaypalEcOrderSetupRequestToken()==null) || 
             (this.paypalEcOrderSetupRequestToken!=null &&
              this.paypalEcOrderSetupRequestToken.equals(other.getPaypalEcOrderSetupRequestToken()))) &&
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
        if (getPaypalAuthorizationId() != null) {
            _hashCode += getPaypalAuthorizationId().hashCode();
        }
        if (getPaypalEcDoPaymentRequestID() != null) {
            _hashCode += getPaypalEcDoPaymentRequestID().hashCode();
        }
        if (getPaypalEcDoPaymentRequestToken() != null) {
            _hashCode += getPaypalEcDoPaymentRequestToken().hashCode();
        }
        if (getPaypalAuthorizationRequestID() != null) {
            _hashCode += getPaypalAuthorizationRequestID().hashCode();
        }
        if (getPaypalAuthorizationRequestToken() != null) {
            _hashCode += getPaypalAuthorizationRequestToken().hashCode();
        }
        if (getPaypalEcOrderSetupRequestID() != null) {
            _hashCode += getPaypalEcOrderSetupRequestID().hashCode();
        }
        if (getPaypalEcOrderSetupRequestToken() != null) {
            _hashCode += getPaypalEcOrderSetupRequestToken().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalAuthReversalService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthReversalService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalAuthorizationId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalAuthorizationId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalEcDoPaymentRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalEcDoPaymentRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalEcDoPaymentRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalEcDoPaymentRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalAuthorizationRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalAuthorizationRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalAuthorizationRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalAuthorizationRequestToken"));
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
