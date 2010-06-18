/**
 * CCAuthReversalService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class CCAuthReversalService  implements java.io.Serializable {
    private java.lang.String authRequestID;

    private java.lang.String authRequestToken;

    private java.lang.String run;  // attribute

    public CCAuthReversalService() {
    }

    public CCAuthReversalService(
           java.lang.String authRequestID,
           java.lang.String authRequestToken,
           java.lang.String run) {
           this.authRequestID = authRequestID;
           this.authRequestToken = authRequestToken;
           this.run = run;
    }


    /**
     * Gets the authRequestID value for this CCAuthReversalService.
     * 
     * @return authRequestID
     */
    public java.lang.String getAuthRequestID() {
        return authRequestID;
    }


    /**
     * Sets the authRequestID value for this CCAuthReversalService.
     * 
     * @param authRequestID
     */
    public void setAuthRequestID(java.lang.String authRequestID) {
        this.authRequestID = authRequestID;
    }


    /**
     * Gets the authRequestToken value for this CCAuthReversalService.
     * 
     * @return authRequestToken
     */
    public java.lang.String getAuthRequestToken() {
        return authRequestToken;
    }


    /**
     * Sets the authRequestToken value for this CCAuthReversalService.
     * 
     * @param authRequestToken
     */
    public void setAuthRequestToken(java.lang.String authRequestToken) {
        this.authRequestToken = authRequestToken;
    }


    /**
     * Gets the run value for this CCAuthReversalService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this CCAuthReversalService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CCAuthReversalService)) return false;
        CCAuthReversalService other = (CCAuthReversalService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.authRequestID==null && other.getAuthRequestID()==null) || 
             (this.authRequestID!=null &&
              this.authRequestID.equals(other.getAuthRequestID()))) &&
            ((this.authRequestToken==null && other.getAuthRequestToken()==null) || 
             (this.authRequestToken!=null &&
              this.authRequestToken.equals(other.getAuthRequestToken()))) &&
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
        if (getAuthRequestID() != null) {
            _hashCode += getAuthRequestID().hashCode();
        }
        if (getAuthRequestToken() != null) {
            _hashCode += getAuthRequestToken().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CCAuthReversalService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthReversalService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authRequestToken"));
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
