/**
 * PayPalButtonCreateReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalButtonCreateReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String encryptedFormData;

    private java.lang.String unencryptedFormData;

    private java.lang.String requestDateTime;

    private java.lang.String reconciliationID;

    private java.lang.String buttonType;

    public PayPalButtonCreateReply() {
    }

    public PayPalButtonCreateReply(
           java.math.BigInteger reasonCode,
           java.lang.String encryptedFormData,
           java.lang.String unencryptedFormData,
           java.lang.String requestDateTime,
           java.lang.String reconciliationID,
           java.lang.String buttonType) {
           this.reasonCode = reasonCode;
           this.encryptedFormData = encryptedFormData;
           this.unencryptedFormData = unencryptedFormData;
           this.requestDateTime = requestDateTime;
           this.reconciliationID = reconciliationID;
           this.buttonType = buttonType;
    }


    /**
     * Gets the reasonCode value for this PayPalButtonCreateReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalButtonCreateReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the encryptedFormData value for this PayPalButtonCreateReply.
     * 
     * @return encryptedFormData
     */
    public java.lang.String getEncryptedFormData() {
        return encryptedFormData;
    }


    /**
     * Sets the encryptedFormData value for this PayPalButtonCreateReply.
     * 
     * @param encryptedFormData
     */
    public void setEncryptedFormData(java.lang.String encryptedFormData) {
        this.encryptedFormData = encryptedFormData;
    }


    /**
     * Gets the unencryptedFormData value for this PayPalButtonCreateReply.
     * 
     * @return unencryptedFormData
     */
    public java.lang.String getUnencryptedFormData() {
        return unencryptedFormData;
    }


    /**
     * Sets the unencryptedFormData value for this PayPalButtonCreateReply.
     * 
     * @param unencryptedFormData
     */
    public void setUnencryptedFormData(java.lang.String unencryptedFormData) {
        this.unencryptedFormData = unencryptedFormData;
    }


    /**
     * Gets the requestDateTime value for this PayPalButtonCreateReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this PayPalButtonCreateReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the reconciliationID value for this PayPalButtonCreateReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this PayPalButtonCreateReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the buttonType value for this PayPalButtonCreateReply.
     * 
     * @return buttonType
     */
    public java.lang.String getButtonType() {
        return buttonType;
    }


    /**
     * Sets the buttonType value for this PayPalButtonCreateReply.
     * 
     * @param buttonType
     */
    public void setButtonType(java.lang.String buttonType) {
        this.buttonType = buttonType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalButtonCreateReply)) return false;
        PayPalButtonCreateReply other = (PayPalButtonCreateReply) obj;
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
            ((this.encryptedFormData==null && other.getEncryptedFormData()==null) || 
             (this.encryptedFormData!=null &&
              this.encryptedFormData.equals(other.getEncryptedFormData()))) &&
            ((this.unencryptedFormData==null && other.getUnencryptedFormData()==null) || 
             (this.unencryptedFormData!=null &&
              this.unencryptedFormData.equals(other.getUnencryptedFormData()))) &&
            ((this.requestDateTime==null && other.getRequestDateTime()==null) || 
             (this.requestDateTime!=null &&
              this.requestDateTime.equals(other.getRequestDateTime()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.buttonType==null && other.getButtonType()==null) || 
             (this.buttonType!=null &&
              this.buttonType.equals(other.getButtonType())));
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
        if (getEncryptedFormData() != null) {
            _hashCode += getEncryptedFormData().hashCode();
        }
        if (getUnencryptedFormData() != null) {
            _hashCode += getUnencryptedFormData().hashCode();
        }
        if (getRequestDateTime() != null) {
            _hashCode += getRequestDateTime().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getButtonType() != null) {
            _hashCode += getButtonType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalButtonCreateReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalButtonCreateReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("encryptedFormData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "encryptedFormData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unencryptedFormData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "unencryptedFormData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "requestDateTime"));
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
        elemField.setFieldName("buttonType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "buttonType"));
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
