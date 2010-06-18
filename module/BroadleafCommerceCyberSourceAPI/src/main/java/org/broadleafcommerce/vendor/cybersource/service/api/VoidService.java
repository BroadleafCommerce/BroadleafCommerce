/**
 * VoidService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class VoidService  implements java.io.Serializable {
    private java.lang.String voidRequestID;

    private java.lang.String voidRequestToken;

    private java.lang.String run;  // attribute

    public VoidService() {
    }

    public VoidService(
           java.lang.String voidRequestID,
           java.lang.String voidRequestToken,
           java.lang.String run) {
           this.voidRequestID = voidRequestID;
           this.voidRequestToken = voidRequestToken;
           this.run = run;
    }


    /**
     * Gets the voidRequestID value for this VoidService.
     * 
     * @return voidRequestID
     */
    public java.lang.String getVoidRequestID() {
        return voidRequestID;
    }


    /**
     * Sets the voidRequestID value for this VoidService.
     * 
     * @param voidRequestID
     */
    public void setVoidRequestID(java.lang.String voidRequestID) {
        this.voidRequestID = voidRequestID;
    }


    /**
     * Gets the voidRequestToken value for this VoidService.
     * 
     * @return voidRequestToken
     */
    public java.lang.String getVoidRequestToken() {
        return voidRequestToken;
    }


    /**
     * Sets the voidRequestToken value for this VoidService.
     * 
     * @param voidRequestToken
     */
    public void setVoidRequestToken(java.lang.String voidRequestToken) {
        this.voidRequestToken = voidRequestToken;
    }


    /**
     * Gets the run value for this VoidService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this VoidService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoidService)) return false;
        VoidService other = (VoidService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.voidRequestID==null && other.getVoidRequestID()==null) || 
             (this.voidRequestID!=null &&
              this.voidRequestID.equals(other.getVoidRequestID()))) &&
            ((this.voidRequestToken==null && other.getVoidRequestToken()==null) || 
             (this.voidRequestToken!=null &&
              this.voidRequestToken.equals(other.getVoidRequestToken()))) &&
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
        if (getVoidRequestID() != null) {
            _hashCode += getVoidRequestID().hashCode();
        }
        if (getVoidRequestToken() != null) {
            _hashCode += getVoidRequestToken().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoidService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "VoidService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voidRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "voidRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voidRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "voidRequestToken"));
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
