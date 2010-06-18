/**
 * ChinaPaymentReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ChinaPaymentReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String requestDateTime;

    private java.lang.String amount;

    private java.lang.String currency;

    private java.lang.String reconciliationID;

    private java.lang.String formData;

    private java.lang.String verifyFailure;

    private java.lang.String verifyInProcess;

    private java.lang.String verifySuccess;

    public ChinaPaymentReply() {
    }

    public ChinaPaymentReply(
           java.math.BigInteger reasonCode,
           java.lang.String requestDateTime,
           java.lang.String amount,
           java.lang.String currency,
           java.lang.String reconciliationID,
           java.lang.String formData,
           java.lang.String verifyFailure,
           java.lang.String verifyInProcess,
           java.lang.String verifySuccess) {
           this.reasonCode = reasonCode;
           this.requestDateTime = requestDateTime;
           this.amount = amount;
           this.currency = currency;
           this.reconciliationID = reconciliationID;
           this.formData = formData;
           this.verifyFailure = verifyFailure;
           this.verifyInProcess = verifyInProcess;
           this.verifySuccess = verifySuccess;
    }


    /**
     * Gets the reasonCode value for this ChinaPaymentReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this ChinaPaymentReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the requestDateTime value for this ChinaPaymentReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this ChinaPaymentReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the amount value for this ChinaPaymentReply.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this ChinaPaymentReply.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the currency value for this ChinaPaymentReply.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this ChinaPaymentReply.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the reconciliationID value for this ChinaPaymentReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this ChinaPaymentReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the formData value for this ChinaPaymentReply.
     * 
     * @return formData
     */
    public java.lang.String getFormData() {
        return formData;
    }


    /**
     * Sets the formData value for this ChinaPaymentReply.
     * 
     * @param formData
     */
    public void setFormData(java.lang.String formData) {
        this.formData = formData;
    }


    /**
     * Gets the verifyFailure value for this ChinaPaymentReply.
     * 
     * @return verifyFailure
     */
    public java.lang.String getVerifyFailure() {
        return verifyFailure;
    }


    /**
     * Sets the verifyFailure value for this ChinaPaymentReply.
     * 
     * @param verifyFailure
     */
    public void setVerifyFailure(java.lang.String verifyFailure) {
        this.verifyFailure = verifyFailure;
    }


    /**
     * Gets the verifyInProcess value for this ChinaPaymentReply.
     * 
     * @return verifyInProcess
     */
    public java.lang.String getVerifyInProcess() {
        return verifyInProcess;
    }


    /**
     * Sets the verifyInProcess value for this ChinaPaymentReply.
     * 
     * @param verifyInProcess
     */
    public void setVerifyInProcess(java.lang.String verifyInProcess) {
        this.verifyInProcess = verifyInProcess;
    }


    /**
     * Gets the verifySuccess value for this ChinaPaymentReply.
     * 
     * @return verifySuccess
     */
    public java.lang.String getVerifySuccess() {
        return verifySuccess;
    }


    /**
     * Sets the verifySuccess value for this ChinaPaymentReply.
     * 
     * @param verifySuccess
     */
    public void setVerifySuccess(java.lang.String verifySuccess) {
        this.verifySuccess = verifySuccess;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChinaPaymentReply)) return false;
        ChinaPaymentReply other = (ChinaPaymentReply) obj;
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
            ((this.requestDateTime==null && other.getRequestDateTime()==null) || 
             (this.requestDateTime!=null &&
              this.requestDateTime.equals(other.getRequestDateTime()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.formData==null && other.getFormData()==null) || 
             (this.formData!=null &&
              this.formData.equals(other.getFormData()))) &&
            ((this.verifyFailure==null && other.getVerifyFailure()==null) || 
             (this.verifyFailure!=null &&
              this.verifyFailure.equals(other.getVerifyFailure()))) &&
            ((this.verifyInProcess==null && other.getVerifyInProcess()==null) || 
             (this.verifyInProcess!=null &&
              this.verifyInProcess.equals(other.getVerifyInProcess()))) &&
            ((this.verifySuccess==null && other.getVerifySuccess()==null) || 
             (this.verifySuccess!=null &&
              this.verifySuccess.equals(other.getVerifySuccess())));
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
        if (getRequestDateTime() != null) {
            _hashCode += getRequestDateTime().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getFormData() != null) {
            _hashCode += getFormData().hashCode();
        }
        if (getVerifyFailure() != null) {
            _hashCode += getVerifyFailure().hashCode();
        }
        if (getVerifyInProcess() != null) {
            _hashCode += getVerifyInProcess().hashCode();
        }
        if (getVerifySuccess() != null) {
            _hashCode += getVerifySuccess().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ChinaPaymentReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaPaymentReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "requestDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "currency"));
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
        elemField.setFieldName("formData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "formData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verifyFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "verifyFailure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verifyInProcess");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "verifyInProcess"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verifySuccess");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "verifySuccess"));
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
