/**
 * BankTransferRefundService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class BankTransferRefundService  implements java.io.Serializable {
    private java.lang.String bankTransferRequestID;

    private java.lang.String bankTransferRealTimeRequestID;

    private java.lang.String reconciliationID;

    private java.lang.String bankTransferRealTimeReconciliationID;

    private java.lang.String bankTransferRequestToken;

    private java.lang.String bankTransferRealTimeRequestToken;

    private java.lang.String run;  // attribute

    public BankTransferRefundService() {
    }

    public BankTransferRefundService(
           java.lang.String bankTransferRequestID,
           java.lang.String bankTransferRealTimeRequestID,
           java.lang.String reconciliationID,
           java.lang.String bankTransferRealTimeReconciliationID,
           java.lang.String bankTransferRequestToken,
           java.lang.String bankTransferRealTimeRequestToken,
           java.lang.String run) {
           this.bankTransferRequestID = bankTransferRequestID;
           this.bankTransferRealTimeRequestID = bankTransferRealTimeRequestID;
           this.reconciliationID = reconciliationID;
           this.bankTransferRealTimeReconciliationID = bankTransferRealTimeReconciliationID;
           this.bankTransferRequestToken = bankTransferRequestToken;
           this.bankTransferRealTimeRequestToken = bankTransferRealTimeRequestToken;
           this.run = run;
    }


    /**
     * Gets the bankTransferRequestID value for this BankTransferRefundService.
     * 
     * @return bankTransferRequestID
     */
    public java.lang.String getBankTransferRequestID() {
        return bankTransferRequestID;
    }


    /**
     * Sets the bankTransferRequestID value for this BankTransferRefundService.
     * 
     * @param bankTransferRequestID
     */
    public void setBankTransferRequestID(java.lang.String bankTransferRequestID) {
        this.bankTransferRequestID = bankTransferRequestID;
    }


    /**
     * Gets the bankTransferRealTimeRequestID value for this BankTransferRefundService.
     * 
     * @return bankTransferRealTimeRequestID
     */
    public java.lang.String getBankTransferRealTimeRequestID() {
        return bankTransferRealTimeRequestID;
    }


    /**
     * Sets the bankTransferRealTimeRequestID value for this BankTransferRefundService.
     * 
     * @param bankTransferRealTimeRequestID
     */
    public void setBankTransferRealTimeRequestID(java.lang.String bankTransferRealTimeRequestID) {
        this.bankTransferRealTimeRequestID = bankTransferRealTimeRequestID;
    }


    /**
     * Gets the reconciliationID value for this BankTransferRefundService.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this BankTransferRefundService.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the bankTransferRealTimeReconciliationID value for this BankTransferRefundService.
     * 
     * @return bankTransferRealTimeReconciliationID
     */
    public java.lang.String getBankTransferRealTimeReconciliationID() {
        return bankTransferRealTimeReconciliationID;
    }


    /**
     * Sets the bankTransferRealTimeReconciliationID value for this BankTransferRefundService.
     * 
     * @param bankTransferRealTimeReconciliationID
     */
    public void setBankTransferRealTimeReconciliationID(java.lang.String bankTransferRealTimeReconciliationID) {
        this.bankTransferRealTimeReconciliationID = bankTransferRealTimeReconciliationID;
    }


    /**
     * Gets the bankTransferRequestToken value for this BankTransferRefundService.
     * 
     * @return bankTransferRequestToken
     */
    public java.lang.String getBankTransferRequestToken() {
        return bankTransferRequestToken;
    }


    /**
     * Sets the bankTransferRequestToken value for this BankTransferRefundService.
     * 
     * @param bankTransferRequestToken
     */
    public void setBankTransferRequestToken(java.lang.String bankTransferRequestToken) {
        this.bankTransferRequestToken = bankTransferRequestToken;
    }


    /**
     * Gets the bankTransferRealTimeRequestToken value for this BankTransferRefundService.
     * 
     * @return bankTransferRealTimeRequestToken
     */
    public java.lang.String getBankTransferRealTimeRequestToken() {
        return bankTransferRealTimeRequestToken;
    }


    /**
     * Sets the bankTransferRealTimeRequestToken value for this BankTransferRefundService.
     * 
     * @param bankTransferRealTimeRequestToken
     */
    public void setBankTransferRealTimeRequestToken(java.lang.String bankTransferRealTimeRequestToken) {
        this.bankTransferRealTimeRequestToken = bankTransferRealTimeRequestToken;
    }


    /**
     * Gets the run value for this BankTransferRefundService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this BankTransferRefundService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BankTransferRefundService)) return false;
        BankTransferRefundService other = (BankTransferRefundService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bankTransferRequestID==null && other.getBankTransferRequestID()==null) || 
             (this.bankTransferRequestID!=null &&
              this.bankTransferRequestID.equals(other.getBankTransferRequestID()))) &&
            ((this.bankTransferRealTimeRequestID==null && other.getBankTransferRealTimeRequestID()==null) || 
             (this.bankTransferRealTimeRequestID!=null &&
              this.bankTransferRealTimeRequestID.equals(other.getBankTransferRealTimeRequestID()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.bankTransferRealTimeReconciliationID==null && other.getBankTransferRealTimeReconciliationID()==null) || 
             (this.bankTransferRealTimeReconciliationID!=null &&
              this.bankTransferRealTimeReconciliationID.equals(other.getBankTransferRealTimeReconciliationID()))) &&
            ((this.bankTransferRequestToken==null && other.getBankTransferRequestToken()==null) || 
             (this.bankTransferRequestToken!=null &&
              this.bankTransferRequestToken.equals(other.getBankTransferRequestToken()))) &&
            ((this.bankTransferRealTimeRequestToken==null && other.getBankTransferRealTimeRequestToken()==null) || 
             (this.bankTransferRealTimeRequestToken!=null &&
              this.bankTransferRealTimeRequestToken.equals(other.getBankTransferRealTimeRequestToken()))) &&
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
        if (getBankTransferRequestID() != null) {
            _hashCode += getBankTransferRequestID().hashCode();
        }
        if (getBankTransferRealTimeRequestID() != null) {
            _hashCode += getBankTransferRealTimeRequestID().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getBankTransferRealTimeReconciliationID() != null) {
            _hashCode += getBankTransferRealTimeReconciliationID().hashCode();
        }
        if (getBankTransferRequestToken() != null) {
            _hashCode += getBankTransferRequestToken().hashCode();
        }
        if (getBankTransferRealTimeRequestToken() != null) {
            _hashCode += getBankTransferRealTimeRequestToken().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BankTransferRefundService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRefundService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRealTimeRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRealTimeRequestID"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRealTimeReconciliationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRealTimeReconciliationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRealTimeRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRealTimeRequestToken"));
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
