/**
 * FXRatesReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class FXRatesReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private org.broadleafcommerce.vendor.cybersource.service.api.FXQuote[] quote;

    public FXRatesReply() {
    }

    public FXRatesReply(
           java.math.BigInteger reasonCode,
           org.broadleafcommerce.vendor.cybersource.service.api.FXQuote[] quote) {
           this.reasonCode = reasonCode;
           this.quote = quote;
    }


    /**
     * Gets the reasonCode value for this FXRatesReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this FXRatesReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the quote value for this FXRatesReply.
     * 
     * @return quote
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.FXQuote[] getQuote() {
        return quote;
    }


    /**
     * Sets the quote value for this FXRatesReply.
     * 
     * @param quote
     */
    public void setQuote(org.broadleafcommerce.vendor.cybersource.service.api.FXQuote[] quote) {
        this.quote = quote;
    }

    public org.broadleafcommerce.vendor.cybersource.service.api.FXQuote getQuote(int i) {
        return this.quote[i];
    }

    public void setQuote(int i, org.broadleafcommerce.vendor.cybersource.service.api.FXQuote _value) {
        this.quote[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FXRatesReply)) return false;
        FXRatesReply other = (FXRatesReply) obj;
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
            ((this.quote==null && other.getQuote()==null) || 
             (this.quote!=null &&
              java.util.Arrays.equals(this.quote, other.getQuote())));
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
        if (getQuote() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQuote());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQuote(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FXRatesReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FXRatesReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quote");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "quote"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FXQuote"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
