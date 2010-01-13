/**
 * UCAF.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class UCAF  implements java.io.Serializable {
    private java.lang.String authenticationData;

    private java.lang.String collectionIndicator;

    public UCAF() {
    }

    public UCAF(
           java.lang.String authenticationData,
           java.lang.String collectionIndicator) {
           this.authenticationData = authenticationData;
           this.collectionIndicator = collectionIndicator;
    }


    /**
     * Gets the authenticationData value for this UCAF.
     * 
     * @return authenticationData
     */
    public java.lang.String getAuthenticationData() {
        return authenticationData;
    }


    /**
     * Sets the authenticationData value for this UCAF.
     * 
     * @param authenticationData
     */
    public void setAuthenticationData(java.lang.String authenticationData) {
        this.authenticationData = authenticationData;
    }


    /**
     * Gets the collectionIndicator value for this UCAF.
     * 
     * @return collectionIndicator
     */
    public java.lang.String getCollectionIndicator() {
        return collectionIndicator;
    }


    /**
     * Sets the collectionIndicator value for this UCAF.
     * 
     * @param collectionIndicator
     */
    public void setCollectionIndicator(java.lang.String collectionIndicator) {
        this.collectionIndicator = collectionIndicator;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UCAF)) return false;
        UCAF other = (UCAF) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.authenticationData==null && other.getAuthenticationData()==null) || 
             (this.authenticationData!=null &&
              this.authenticationData.equals(other.getAuthenticationData()))) &&
            ((this.collectionIndicator==null && other.getCollectionIndicator()==null) || 
             (this.collectionIndicator!=null &&
              this.collectionIndicator.equals(other.getCollectionIndicator())));
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
        if (getAuthenticationData() != null) {
            _hashCode += getAuthenticationData().hashCode();
        }
        if (getCollectionIndicator() != null) {
            _hashCode += getCollectionIndicator().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UCAF.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "UCAF"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticationData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authenticationData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("collectionIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "collectionIndicator"));
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
