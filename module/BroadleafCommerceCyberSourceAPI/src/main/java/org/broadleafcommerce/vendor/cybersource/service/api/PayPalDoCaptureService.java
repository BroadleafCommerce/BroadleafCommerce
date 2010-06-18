/**
 * PayPalDoCaptureService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalDoCaptureService  implements java.io.Serializable {
    private java.lang.String paypalAuthorizationId;

    private java.lang.String completeType;

    private java.lang.String paypalEcDoPaymentRequestID;

    private java.lang.String paypalEcDoPaymentRequestToken;

    private java.lang.String paypalAuthorizationRequestID;

    private java.lang.String paypalAuthorizationRequestToken;

    private java.lang.String invoiceNumber;

    private java.lang.String run;  // attribute

    public PayPalDoCaptureService() {
    }

    public PayPalDoCaptureService(
           java.lang.String paypalAuthorizationId,
           java.lang.String completeType,
           java.lang.String paypalEcDoPaymentRequestID,
           java.lang.String paypalEcDoPaymentRequestToken,
           java.lang.String paypalAuthorizationRequestID,
           java.lang.String paypalAuthorizationRequestToken,
           java.lang.String invoiceNumber,
           java.lang.String run) {
           this.paypalAuthorizationId = paypalAuthorizationId;
           this.completeType = completeType;
           this.paypalEcDoPaymentRequestID = paypalEcDoPaymentRequestID;
           this.paypalEcDoPaymentRequestToken = paypalEcDoPaymentRequestToken;
           this.paypalAuthorizationRequestID = paypalAuthorizationRequestID;
           this.paypalAuthorizationRequestToken = paypalAuthorizationRequestToken;
           this.invoiceNumber = invoiceNumber;
           this.run = run;
    }


    /**
     * Gets the paypalAuthorizationId value for this PayPalDoCaptureService.
     * 
     * @return paypalAuthorizationId
     */
    public java.lang.String getPaypalAuthorizationId() {
        return paypalAuthorizationId;
    }


    /**
     * Sets the paypalAuthorizationId value for this PayPalDoCaptureService.
     * 
     * @param paypalAuthorizationId
     */
    public void setPaypalAuthorizationId(java.lang.String paypalAuthorizationId) {
        this.paypalAuthorizationId = paypalAuthorizationId;
    }


    /**
     * Gets the completeType value for this PayPalDoCaptureService.
     * 
     * @return completeType
     */
    public java.lang.String getCompleteType() {
        return completeType;
    }


    /**
     * Sets the completeType value for this PayPalDoCaptureService.
     * 
     * @param completeType
     */
    public void setCompleteType(java.lang.String completeType) {
        this.completeType = completeType;
    }


    /**
     * Gets the paypalEcDoPaymentRequestID value for this PayPalDoCaptureService.
     * 
     * @return paypalEcDoPaymentRequestID
     */
    public java.lang.String getPaypalEcDoPaymentRequestID() {
        return paypalEcDoPaymentRequestID;
    }


    /**
     * Sets the paypalEcDoPaymentRequestID value for this PayPalDoCaptureService.
     * 
     * @param paypalEcDoPaymentRequestID
     */
    public void setPaypalEcDoPaymentRequestID(java.lang.String paypalEcDoPaymentRequestID) {
        this.paypalEcDoPaymentRequestID = paypalEcDoPaymentRequestID;
    }


    /**
     * Gets the paypalEcDoPaymentRequestToken value for this PayPalDoCaptureService.
     * 
     * @return paypalEcDoPaymentRequestToken
     */
    public java.lang.String getPaypalEcDoPaymentRequestToken() {
        return paypalEcDoPaymentRequestToken;
    }


    /**
     * Sets the paypalEcDoPaymentRequestToken value for this PayPalDoCaptureService.
     * 
     * @param paypalEcDoPaymentRequestToken
     */
    public void setPaypalEcDoPaymentRequestToken(java.lang.String paypalEcDoPaymentRequestToken) {
        this.paypalEcDoPaymentRequestToken = paypalEcDoPaymentRequestToken;
    }


    /**
     * Gets the paypalAuthorizationRequestID value for this PayPalDoCaptureService.
     * 
     * @return paypalAuthorizationRequestID
     */
    public java.lang.String getPaypalAuthorizationRequestID() {
        return paypalAuthorizationRequestID;
    }


    /**
     * Sets the paypalAuthorizationRequestID value for this PayPalDoCaptureService.
     * 
     * @param paypalAuthorizationRequestID
     */
    public void setPaypalAuthorizationRequestID(java.lang.String paypalAuthorizationRequestID) {
        this.paypalAuthorizationRequestID = paypalAuthorizationRequestID;
    }


    /**
     * Gets the paypalAuthorizationRequestToken value for this PayPalDoCaptureService.
     * 
     * @return paypalAuthorizationRequestToken
     */
    public java.lang.String getPaypalAuthorizationRequestToken() {
        return paypalAuthorizationRequestToken;
    }


    /**
     * Sets the paypalAuthorizationRequestToken value for this PayPalDoCaptureService.
     * 
     * @param paypalAuthorizationRequestToken
     */
    public void setPaypalAuthorizationRequestToken(java.lang.String paypalAuthorizationRequestToken) {
        this.paypalAuthorizationRequestToken = paypalAuthorizationRequestToken;
    }


    /**
     * Gets the invoiceNumber value for this PayPalDoCaptureService.
     * 
     * @return invoiceNumber
     */
    public java.lang.String getInvoiceNumber() {
        return invoiceNumber;
    }


    /**
     * Sets the invoiceNumber value for this PayPalDoCaptureService.
     * 
     * @param invoiceNumber
     */
    public void setInvoiceNumber(java.lang.String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    /**
     * Gets the run value for this PayPalDoCaptureService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayPalDoCaptureService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalDoCaptureService)) return false;
        PayPalDoCaptureService other = (PayPalDoCaptureService) obj;
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
            ((this.completeType==null && other.getCompleteType()==null) || 
             (this.completeType!=null &&
              this.completeType.equals(other.getCompleteType()))) &&
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
            ((this.invoiceNumber==null && other.getInvoiceNumber()==null) || 
             (this.invoiceNumber!=null &&
              this.invoiceNumber.equals(other.getInvoiceNumber()))) &&
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
        if (getCompleteType() != null) {
            _hashCode += getCompleteType().hashCode();
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
        if (getInvoiceNumber() != null) {
            _hashCode += getInvoiceNumber().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalDoCaptureService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoCaptureService"));
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
        elemField.setFieldName("completeType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "completeType"));
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
        elemField.setFieldName("invoiceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "invoiceNumber"));
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
