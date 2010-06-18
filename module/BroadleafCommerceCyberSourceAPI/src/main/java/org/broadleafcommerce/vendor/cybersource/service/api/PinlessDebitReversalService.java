/**
 * PinlessDebitReversalService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PinlessDebitReversalService  implements java.io.Serializable {
    private java.lang.String pinlessDebitRequestID;

    private java.lang.String pinlessDebitRequestToken;

    private java.lang.String reconciliationID;

    private java.lang.String run;  // attribute

    public PinlessDebitReversalService() {
    }

    public PinlessDebitReversalService(
           java.lang.String pinlessDebitRequestID,
           java.lang.String pinlessDebitRequestToken,
           java.lang.String reconciliationID,
           java.lang.String run) {
           this.pinlessDebitRequestID = pinlessDebitRequestID;
           this.pinlessDebitRequestToken = pinlessDebitRequestToken;
           this.reconciliationID = reconciliationID;
           this.run = run;
    }


    /**
     * Gets the pinlessDebitRequestID value for this PinlessDebitReversalService.
     * 
     * @return pinlessDebitRequestID
     */
    public java.lang.String getPinlessDebitRequestID() {
        return pinlessDebitRequestID;
    }


    /**
     * Sets the pinlessDebitRequestID value for this PinlessDebitReversalService.
     * 
     * @param pinlessDebitRequestID
     */
    public void setPinlessDebitRequestID(java.lang.String pinlessDebitRequestID) {
        this.pinlessDebitRequestID = pinlessDebitRequestID;
    }


    /**
     * Gets the pinlessDebitRequestToken value for this PinlessDebitReversalService.
     * 
     * @return pinlessDebitRequestToken
     */
    public java.lang.String getPinlessDebitRequestToken() {
        return pinlessDebitRequestToken;
    }


    /**
     * Sets the pinlessDebitRequestToken value for this PinlessDebitReversalService.
     * 
     * @param pinlessDebitRequestToken
     */
    public void setPinlessDebitRequestToken(java.lang.String pinlessDebitRequestToken) {
        this.pinlessDebitRequestToken = pinlessDebitRequestToken;
    }


    /**
     * Gets the reconciliationID value for this PinlessDebitReversalService.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this PinlessDebitReversalService.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the run value for this PinlessDebitReversalService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PinlessDebitReversalService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PinlessDebitReversalService)) return false;
        PinlessDebitReversalService other = (PinlessDebitReversalService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.pinlessDebitRequestID==null && other.getPinlessDebitRequestID()==null) || 
             (this.pinlessDebitRequestID!=null &&
              this.pinlessDebitRequestID.equals(other.getPinlessDebitRequestID()))) &&
            ((this.pinlessDebitRequestToken==null && other.getPinlessDebitRequestToken()==null) || 
             (this.pinlessDebitRequestToken!=null &&
              this.pinlessDebitRequestToken.equals(other.getPinlessDebitRequestToken()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
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
        if (getPinlessDebitRequestID() != null) {
            _hashCode += getPinlessDebitRequestID().hashCode();
        }
        if (getPinlessDebitRequestToken() != null) {
            _hashCode += getPinlessDebitRequestToken().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PinlessDebitReversalService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitReversalService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinlessDebitRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pinlessDebitRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinlessDebitRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pinlessDebitRequestToken"));
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
