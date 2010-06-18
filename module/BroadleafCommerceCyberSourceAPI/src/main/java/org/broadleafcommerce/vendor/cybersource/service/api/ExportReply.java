/**
 * ExportReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ExportReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.math.BigInteger ipCountryConfidence;

    private java.lang.String infoCode;

    public ExportReply() {
    }

    public ExportReply(
           java.math.BigInteger reasonCode,
           java.math.BigInteger ipCountryConfidence,
           java.lang.String infoCode) {
           this.reasonCode = reasonCode;
           this.ipCountryConfidence = ipCountryConfidence;
           this.infoCode = infoCode;
    }


    /**
     * Gets the reasonCode value for this ExportReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this ExportReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the ipCountryConfidence value for this ExportReply.
     * 
     * @return ipCountryConfidence
     */
    public java.math.BigInteger getIpCountryConfidence() {
        return ipCountryConfidence;
    }


    /**
     * Sets the ipCountryConfidence value for this ExportReply.
     * 
     * @param ipCountryConfidence
     */
    public void setIpCountryConfidence(java.math.BigInteger ipCountryConfidence) {
        this.ipCountryConfidence = ipCountryConfidence;
    }


    /**
     * Gets the infoCode value for this ExportReply.
     * 
     * @return infoCode
     */
    public java.lang.String getInfoCode() {
        return infoCode;
    }


    /**
     * Sets the infoCode value for this ExportReply.
     * 
     * @param infoCode
     */
    public void setInfoCode(java.lang.String infoCode) {
        this.infoCode = infoCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExportReply)) return false;
        ExportReply other = (ExportReply) obj;
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
            ((this.ipCountryConfidence==null && other.getIpCountryConfidence()==null) || 
             (this.ipCountryConfidence!=null &&
              this.ipCountryConfidence.equals(other.getIpCountryConfidence()))) &&
            ((this.infoCode==null && other.getInfoCode()==null) || 
             (this.infoCode!=null &&
              this.infoCode.equals(other.getInfoCode())));
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
        if (getIpCountryConfidence() != null) {
            _hashCode += getIpCountryConfidence().hashCode();
        }
        if (getInfoCode() != null) {
            _hashCode += getInfoCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExportReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ExportReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipCountryConfidence");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ipCountryConfidence"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("infoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "infoCode"));
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
