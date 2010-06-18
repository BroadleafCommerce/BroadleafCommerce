/**
 * PayPalRefundService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalRefundService  implements java.io.Serializable {
    private java.lang.String paypalDoCaptureRequestID;

    private java.lang.String paypalDoCaptureRequestToken;

    private java.lang.String paypalCaptureId;

    private java.lang.String paypalNote;

    private java.lang.String run;  // attribute

    public PayPalRefundService() {
    }

    public PayPalRefundService(
           java.lang.String paypalDoCaptureRequestID,
           java.lang.String paypalDoCaptureRequestToken,
           java.lang.String paypalCaptureId,
           java.lang.String paypalNote,
           java.lang.String run) {
           this.paypalDoCaptureRequestID = paypalDoCaptureRequestID;
           this.paypalDoCaptureRequestToken = paypalDoCaptureRequestToken;
           this.paypalCaptureId = paypalCaptureId;
           this.paypalNote = paypalNote;
           this.run = run;
    }


    /**
     * Gets the paypalDoCaptureRequestID value for this PayPalRefundService.
     * 
     * @return paypalDoCaptureRequestID
     */
    public java.lang.String getPaypalDoCaptureRequestID() {
        return paypalDoCaptureRequestID;
    }


    /**
     * Sets the paypalDoCaptureRequestID value for this PayPalRefundService.
     * 
     * @param paypalDoCaptureRequestID
     */
    public void setPaypalDoCaptureRequestID(java.lang.String paypalDoCaptureRequestID) {
        this.paypalDoCaptureRequestID = paypalDoCaptureRequestID;
    }


    /**
     * Gets the paypalDoCaptureRequestToken value for this PayPalRefundService.
     * 
     * @return paypalDoCaptureRequestToken
     */
    public java.lang.String getPaypalDoCaptureRequestToken() {
        return paypalDoCaptureRequestToken;
    }


    /**
     * Sets the paypalDoCaptureRequestToken value for this PayPalRefundService.
     * 
     * @param paypalDoCaptureRequestToken
     */
    public void setPaypalDoCaptureRequestToken(java.lang.String paypalDoCaptureRequestToken) {
        this.paypalDoCaptureRequestToken = paypalDoCaptureRequestToken;
    }


    /**
     * Gets the paypalCaptureId value for this PayPalRefundService.
     * 
     * @return paypalCaptureId
     */
    public java.lang.String getPaypalCaptureId() {
        return paypalCaptureId;
    }


    /**
     * Sets the paypalCaptureId value for this PayPalRefundService.
     * 
     * @param paypalCaptureId
     */
    public void setPaypalCaptureId(java.lang.String paypalCaptureId) {
        this.paypalCaptureId = paypalCaptureId;
    }


    /**
     * Gets the paypalNote value for this PayPalRefundService.
     * 
     * @return paypalNote
     */
    public java.lang.String getPaypalNote() {
        return paypalNote;
    }


    /**
     * Sets the paypalNote value for this PayPalRefundService.
     * 
     * @param paypalNote
     */
    public void setPaypalNote(java.lang.String paypalNote) {
        this.paypalNote = paypalNote;
    }


    /**
     * Gets the run value for this PayPalRefundService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayPalRefundService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalRefundService)) return false;
        PayPalRefundService other = (PayPalRefundService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paypalDoCaptureRequestID==null && other.getPaypalDoCaptureRequestID()==null) || 
             (this.paypalDoCaptureRequestID!=null &&
              this.paypalDoCaptureRequestID.equals(other.getPaypalDoCaptureRequestID()))) &&
            ((this.paypalDoCaptureRequestToken==null && other.getPaypalDoCaptureRequestToken()==null) || 
             (this.paypalDoCaptureRequestToken!=null &&
              this.paypalDoCaptureRequestToken.equals(other.getPaypalDoCaptureRequestToken()))) &&
            ((this.paypalCaptureId==null && other.getPaypalCaptureId()==null) || 
             (this.paypalCaptureId!=null &&
              this.paypalCaptureId.equals(other.getPaypalCaptureId()))) &&
            ((this.paypalNote==null && other.getPaypalNote()==null) || 
             (this.paypalNote!=null &&
              this.paypalNote.equals(other.getPaypalNote()))) &&
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
        if (getPaypalDoCaptureRequestID() != null) {
            _hashCode += getPaypalDoCaptureRequestID().hashCode();
        }
        if (getPaypalDoCaptureRequestToken() != null) {
            _hashCode += getPaypalDoCaptureRequestToken().hashCode();
        }
        if (getPaypalCaptureId() != null) {
            _hashCode += getPaypalCaptureId().hashCode();
        }
        if (getPaypalNote() != null) {
            _hashCode += getPaypalNote().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalRefundService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalRefundService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalDoCaptureRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalDoCaptureRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalDoCaptureRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalDoCaptureRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalCaptureId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalCaptureId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalNote");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalNote"));
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
