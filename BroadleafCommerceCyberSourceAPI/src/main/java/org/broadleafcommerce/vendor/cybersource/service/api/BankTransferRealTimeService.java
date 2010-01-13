/**
 * BankTransferRealTimeService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class BankTransferRealTimeService  implements java.io.Serializable {
    private java.lang.String bankTransferRealTimeType;

    private java.lang.String run;  // attribute

    public BankTransferRealTimeService() {
    }

    public BankTransferRealTimeService(
           java.lang.String bankTransferRealTimeType,
           java.lang.String run) {
           this.bankTransferRealTimeType = bankTransferRealTimeType;
           this.run = run;
    }


    /**
     * Gets the bankTransferRealTimeType value for this BankTransferRealTimeService.
     * 
     * @return bankTransferRealTimeType
     */
    public java.lang.String getBankTransferRealTimeType() {
        return bankTransferRealTimeType;
    }


    /**
     * Sets the bankTransferRealTimeType value for this BankTransferRealTimeService.
     * 
     * @param bankTransferRealTimeType
     */
    public void setBankTransferRealTimeType(java.lang.String bankTransferRealTimeType) {
        this.bankTransferRealTimeType = bankTransferRealTimeType;
    }


    /**
     * Gets the run value for this BankTransferRealTimeService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this BankTransferRealTimeService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BankTransferRealTimeService)) return false;
        BankTransferRealTimeService other = (BankTransferRealTimeService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bankTransferRealTimeType==null && other.getBankTransferRealTimeType()==null) || 
             (this.bankTransferRealTimeType!=null &&
              this.bankTransferRealTimeType.equals(other.getBankTransferRealTimeType()))) &&
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
        if (getBankTransferRealTimeType() != null) {
            _hashCode += getBankTransferRealTimeType().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BankTransferRealTimeService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRealTimeService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRealTimeType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRealTimeType"));
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
