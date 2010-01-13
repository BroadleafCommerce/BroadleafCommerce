/**
 * PayerAuthValidateService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayerAuthValidateService  implements java.io.Serializable {
    private java.lang.String signedPARes;

    private java.lang.String run;  // attribute

    public PayerAuthValidateService() {
    }

    public PayerAuthValidateService(
           java.lang.String signedPARes,
           java.lang.String run) {
           this.signedPARes = signedPARes;
           this.run = run;
    }


    /**
     * Gets the signedPARes value for this PayerAuthValidateService.
     * 
     * @return signedPARes
     */
    public java.lang.String getSignedPARes() {
        return signedPARes;
    }


    /**
     * Sets the signedPARes value for this PayerAuthValidateService.
     * 
     * @param signedPARes
     */
    public void setSignedPARes(java.lang.String signedPARes) {
        this.signedPARes = signedPARes;
    }


    /**
     * Gets the run value for this PayerAuthValidateService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayerAuthValidateService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayerAuthValidateService)) return false;
        PayerAuthValidateService other = (PayerAuthValidateService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.signedPARes==null && other.getSignedPARes()==null) || 
             (this.signedPARes!=null &&
              this.signedPARes.equals(other.getSignedPARes()))) &&
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
        if (getSignedPARes() != null) {
            _hashCode += getSignedPARes().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayerAuthValidateService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthValidateService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signedPARes");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "signedPARes"));
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
