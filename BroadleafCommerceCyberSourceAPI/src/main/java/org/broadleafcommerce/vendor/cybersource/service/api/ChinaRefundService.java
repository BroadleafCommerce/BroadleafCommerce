/**
 * ChinaRefundService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ChinaRefundService  implements java.io.Serializable {
    private java.lang.String chinaPaymentRequestID;

    private java.lang.String chinaPaymentRequestToken;

    private java.lang.String refundReason;

    private java.lang.String run;  // attribute

    public ChinaRefundService() {
    }

    public ChinaRefundService(
           java.lang.String chinaPaymentRequestID,
           java.lang.String chinaPaymentRequestToken,
           java.lang.String refundReason,
           java.lang.String run) {
           this.chinaPaymentRequestID = chinaPaymentRequestID;
           this.chinaPaymentRequestToken = chinaPaymentRequestToken;
           this.refundReason = refundReason;
           this.run = run;
    }


    /**
     * Gets the chinaPaymentRequestID value for this ChinaRefundService.
     * 
     * @return chinaPaymentRequestID
     */
    public java.lang.String getChinaPaymentRequestID() {
        return chinaPaymentRequestID;
    }


    /**
     * Sets the chinaPaymentRequestID value for this ChinaRefundService.
     * 
     * @param chinaPaymentRequestID
     */
    public void setChinaPaymentRequestID(java.lang.String chinaPaymentRequestID) {
        this.chinaPaymentRequestID = chinaPaymentRequestID;
    }


    /**
     * Gets the chinaPaymentRequestToken value for this ChinaRefundService.
     * 
     * @return chinaPaymentRequestToken
     */
    public java.lang.String getChinaPaymentRequestToken() {
        return chinaPaymentRequestToken;
    }


    /**
     * Sets the chinaPaymentRequestToken value for this ChinaRefundService.
     * 
     * @param chinaPaymentRequestToken
     */
    public void setChinaPaymentRequestToken(java.lang.String chinaPaymentRequestToken) {
        this.chinaPaymentRequestToken = chinaPaymentRequestToken;
    }


    /**
     * Gets the refundReason value for this ChinaRefundService.
     * 
     * @return refundReason
     */
    public java.lang.String getRefundReason() {
        return refundReason;
    }


    /**
     * Sets the refundReason value for this ChinaRefundService.
     * 
     * @param refundReason
     */
    public void setRefundReason(java.lang.String refundReason) {
        this.refundReason = refundReason;
    }


    /**
     * Gets the run value for this ChinaRefundService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this ChinaRefundService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChinaRefundService)) return false;
        ChinaRefundService other = (ChinaRefundService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.chinaPaymentRequestID==null && other.getChinaPaymentRequestID()==null) || 
             (this.chinaPaymentRequestID!=null &&
              this.chinaPaymentRequestID.equals(other.getChinaPaymentRequestID()))) &&
            ((this.chinaPaymentRequestToken==null && other.getChinaPaymentRequestToken()==null) || 
             (this.chinaPaymentRequestToken!=null &&
              this.chinaPaymentRequestToken.equals(other.getChinaPaymentRequestToken()))) &&
            ((this.refundReason==null && other.getRefundReason()==null) || 
             (this.refundReason!=null &&
              this.refundReason.equals(other.getRefundReason()))) &&
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
        if (getChinaPaymentRequestID() != null) {
            _hashCode += getChinaPaymentRequestID().hashCode();
        }
        if (getChinaPaymentRequestToken() != null) {
            _hashCode += getChinaPaymentRequestToken().hashCode();
        }
        if (getRefundReason() != null) {
            _hashCode += getRefundReason().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ChinaRefundService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaRefundService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chinaPaymentRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "chinaPaymentRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chinaPaymentRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "chinaPaymentRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refundReason");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "refundReason"));
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
