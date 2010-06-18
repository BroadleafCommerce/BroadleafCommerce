/**
 * Batch.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class Batch  implements java.io.Serializable {
    private java.lang.String batchID;

    private java.lang.String recordID;

    public Batch() {
    }

    public Batch(
           java.lang.String batchID,
           java.lang.String recordID) {
           this.batchID = batchID;
           this.recordID = recordID;
    }


    /**
     * Gets the batchID value for this Batch.
     * 
     * @return batchID
     */
    public java.lang.String getBatchID() {
        return batchID;
    }


    /**
     * Sets the batchID value for this Batch.
     * 
     * @param batchID
     */
    public void setBatchID(java.lang.String batchID) {
        this.batchID = batchID;
    }


    /**
     * Gets the recordID value for this Batch.
     * 
     * @return recordID
     */
    public java.lang.String getRecordID() {
        return recordID;
    }


    /**
     * Sets the recordID value for this Batch.
     * 
     * @param recordID
     */
    public void setRecordID(java.lang.String recordID) {
        this.recordID = recordID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Batch)) return false;
        Batch other = (Batch) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.batchID==null && other.getBatchID()==null) || 
             (this.batchID!=null &&
              this.batchID.equals(other.getBatchID()))) &&
            ((this.recordID==null && other.getRecordID()==null) || 
             (this.recordID!=null &&
              this.recordID.equals(other.getRecordID())));
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
        if (getBatchID() != null) {
            _hashCode += getBatchID().hashCode();
        }
        if (getRecordID() != null) {
            _hashCode += getRecordID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Batch.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Batch"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batchID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "batchID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "recordID"));
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
