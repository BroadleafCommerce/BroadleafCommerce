/**
 * ECDebitReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ECDebitReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String settlementMethod;

    private java.lang.String requestDateTime;

    private java.lang.String amount;

    private java.math.BigInteger verificationLevel;

    private java.lang.String processorTransactionID;

    private java.lang.String reconciliationID;

    private java.lang.String processorResponse;

    private java.lang.String avsCode;

    private java.lang.String avsCodeRaw;

    private java.lang.String verificationCode;

    private java.lang.String verificationCodeRaw;

    private java.lang.String correctedAccountNumber;

    private java.lang.String correctedRoutingNumber;

    public ECDebitReply() {
    }

    public ECDebitReply(
           java.math.BigInteger reasonCode,
           java.lang.String settlementMethod,
           java.lang.String requestDateTime,
           java.lang.String amount,
           java.math.BigInteger verificationLevel,
           java.lang.String processorTransactionID,
           java.lang.String reconciliationID,
           java.lang.String processorResponse,
           java.lang.String avsCode,
           java.lang.String avsCodeRaw,
           java.lang.String verificationCode,
           java.lang.String verificationCodeRaw,
           java.lang.String correctedAccountNumber,
           java.lang.String correctedRoutingNumber) {
           this.reasonCode = reasonCode;
           this.settlementMethod = settlementMethod;
           this.requestDateTime = requestDateTime;
           this.amount = amount;
           this.verificationLevel = verificationLevel;
           this.processorTransactionID = processorTransactionID;
           this.reconciliationID = reconciliationID;
           this.processorResponse = processorResponse;
           this.avsCode = avsCode;
           this.avsCodeRaw = avsCodeRaw;
           this.verificationCode = verificationCode;
           this.verificationCodeRaw = verificationCodeRaw;
           this.correctedAccountNumber = correctedAccountNumber;
           this.correctedRoutingNumber = correctedRoutingNumber;
    }


    /**
     * Gets the reasonCode value for this ECDebitReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this ECDebitReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the settlementMethod value for this ECDebitReply.
     * 
     * @return settlementMethod
     */
    public java.lang.String getSettlementMethod() {
        return settlementMethod;
    }


    /**
     * Sets the settlementMethod value for this ECDebitReply.
     * 
     * @param settlementMethod
     */
    public void setSettlementMethod(java.lang.String settlementMethod) {
        this.settlementMethod = settlementMethod;
    }


    /**
     * Gets the requestDateTime value for this ECDebitReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this ECDebitReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the amount value for this ECDebitReply.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this ECDebitReply.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the verificationLevel value for this ECDebitReply.
     * 
     * @return verificationLevel
     */
    public java.math.BigInteger getVerificationLevel() {
        return verificationLevel;
    }


    /**
     * Sets the verificationLevel value for this ECDebitReply.
     * 
     * @param verificationLevel
     */
    public void setVerificationLevel(java.math.BigInteger verificationLevel) {
        this.verificationLevel = verificationLevel;
    }


    /**
     * Gets the processorTransactionID value for this ECDebitReply.
     * 
     * @return processorTransactionID
     */
    public java.lang.String getProcessorTransactionID() {
        return processorTransactionID;
    }


    /**
     * Sets the processorTransactionID value for this ECDebitReply.
     * 
     * @param processorTransactionID
     */
    public void setProcessorTransactionID(java.lang.String processorTransactionID) {
        this.processorTransactionID = processorTransactionID;
    }


    /**
     * Gets the reconciliationID value for this ECDebitReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this ECDebitReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the processorResponse value for this ECDebitReply.
     * 
     * @return processorResponse
     */
    public java.lang.String getProcessorResponse() {
        return processorResponse;
    }


    /**
     * Sets the processorResponse value for this ECDebitReply.
     * 
     * @param processorResponse
     */
    public void setProcessorResponse(java.lang.String processorResponse) {
        this.processorResponse = processorResponse;
    }


    /**
     * Gets the avsCode value for this ECDebitReply.
     * 
     * @return avsCode
     */
    public java.lang.String getAvsCode() {
        return avsCode;
    }


    /**
     * Sets the avsCode value for this ECDebitReply.
     * 
     * @param avsCode
     */
    public void setAvsCode(java.lang.String avsCode) {
        this.avsCode = avsCode;
    }


    /**
     * Gets the avsCodeRaw value for this ECDebitReply.
     * 
     * @return avsCodeRaw
     */
    public java.lang.String getAvsCodeRaw() {
        return avsCodeRaw;
    }


    /**
     * Sets the avsCodeRaw value for this ECDebitReply.
     * 
     * @param avsCodeRaw
     */
    public void setAvsCodeRaw(java.lang.String avsCodeRaw) {
        this.avsCodeRaw = avsCodeRaw;
    }


    /**
     * Gets the verificationCode value for this ECDebitReply.
     * 
     * @return verificationCode
     */
    public java.lang.String getVerificationCode() {
        return verificationCode;
    }


    /**
     * Sets the verificationCode value for this ECDebitReply.
     * 
     * @param verificationCode
     */
    public void setVerificationCode(java.lang.String verificationCode) {
        this.verificationCode = verificationCode;
    }


    /**
     * Gets the verificationCodeRaw value for this ECDebitReply.
     * 
     * @return verificationCodeRaw
     */
    public java.lang.String getVerificationCodeRaw() {
        return verificationCodeRaw;
    }


    /**
     * Sets the verificationCodeRaw value for this ECDebitReply.
     * 
     * @param verificationCodeRaw
     */
    public void setVerificationCodeRaw(java.lang.String verificationCodeRaw) {
        this.verificationCodeRaw = verificationCodeRaw;
    }


    /**
     * Gets the correctedAccountNumber value for this ECDebitReply.
     * 
     * @return correctedAccountNumber
     */
    public java.lang.String getCorrectedAccountNumber() {
        return correctedAccountNumber;
    }


    /**
     * Sets the correctedAccountNumber value for this ECDebitReply.
     * 
     * @param correctedAccountNumber
     */
    public void setCorrectedAccountNumber(java.lang.String correctedAccountNumber) {
        this.correctedAccountNumber = correctedAccountNumber;
    }


    /**
     * Gets the correctedRoutingNumber value for this ECDebitReply.
     * 
     * @return correctedRoutingNumber
     */
    public java.lang.String getCorrectedRoutingNumber() {
        return correctedRoutingNumber;
    }


    /**
     * Sets the correctedRoutingNumber value for this ECDebitReply.
     * 
     * @param correctedRoutingNumber
     */
    public void setCorrectedRoutingNumber(java.lang.String correctedRoutingNumber) {
        this.correctedRoutingNumber = correctedRoutingNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ECDebitReply)) return false;
        ECDebitReply other = (ECDebitReply) obj;
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
            ((this.settlementMethod==null && other.getSettlementMethod()==null) || 
             (this.settlementMethod!=null &&
              this.settlementMethod.equals(other.getSettlementMethod()))) &&
            ((this.requestDateTime==null && other.getRequestDateTime()==null) || 
             (this.requestDateTime!=null &&
              this.requestDateTime.equals(other.getRequestDateTime()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.verificationLevel==null && other.getVerificationLevel()==null) || 
             (this.verificationLevel!=null &&
              this.verificationLevel.equals(other.getVerificationLevel()))) &&
            ((this.processorTransactionID==null && other.getProcessorTransactionID()==null) || 
             (this.processorTransactionID!=null &&
              this.processorTransactionID.equals(other.getProcessorTransactionID()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.processorResponse==null && other.getProcessorResponse()==null) || 
             (this.processorResponse!=null &&
              this.processorResponse.equals(other.getProcessorResponse()))) &&
            ((this.avsCode==null && other.getAvsCode()==null) || 
             (this.avsCode!=null &&
              this.avsCode.equals(other.getAvsCode()))) &&
            ((this.avsCodeRaw==null && other.getAvsCodeRaw()==null) || 
             (this.avsCodeRaw!=null &&
              this.avsCodeRaw.equals(other.getAvsCodeRaw()))) &&
            ((this.verificationCode==null && other.getVerificationCode()==null) || 
             (this.verificationCode!=null &&
              this.verificationCode.equals(other.getVerificationCode()))) &&
            ((this.verificationCodeRaw==null && other.getVerificationCodeRaw()==null) || 
             (this.verificationCodeRaw!=null &&
              this.verificationCodeRaw.equals(other.getVerificationCodeRaw()))) &&
            ((this.correctedAccountNumber==null && other.getCorrectedAccountNumber()==null) || 
             (this.correctedAccountNumber!=null &&
              this.correctedAccountNumber.equals(other.getCorrectedAccountNumber()))) &&
            ((this.correctedRoutingNumber==null && other.getCorrectedRoutingNumber()==null) || 
             (this.correctedRoutingNumber!=null &&
              this.correctedRoutingNumber.equals(other.getCorrectedRoutingNumber())));
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
        if (getSettlementMethod() != null) {
            _hashCode += getSettlementMethod().hashCode();
        }
        if (getRequestDateTime() != null) {
            _hashCode += getRequestDateTime().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getVerificationLevel() != null) {
            _hashCode += getVerificationLevel().hashCode();
        }
        if (getProcessorTransactionID() != null) {
            _hashCode += getProcessorTransactionID().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getProcessorResponse() != null) {
            _hashCode += getProcessorResponse().hashCode();
        }
        if (getAvsCode() != null) {
            _hashCode += getAvsCode().hashCode();
        }
        if (getAvsCodeRaw() != null) {
            _hashCode += getAvsCodeRaw().hashCode();
        }
        if (getVerificationCode() != null) {
            _hashCode += getVerificationCode().hashCode();
        }
        if (getVerificationCodeRaw() != null) {
            _hashCode += getVerificationCodeRaw().hashCode();
        }
        if (getCorrectedAccountNumber() != null) {
            _hashCode += getCorrectedAccountNumber().hashCode();
        }
        if (getCorrectedRoutingNumber() != null) {
            _hashCode += getCorrectedRoutingNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ECDebitReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECDebitReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("settlementMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "settlementMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("verificationLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "verificationLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processorTransactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "processorTransactionID"));
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
        elemField.setFieldName("processorResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "processorResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "avsCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsCodeRaw");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "avsCodeRaw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verificationCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "verificationCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verificationCodeRaw");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "verificationCodeRaw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("correctedAccountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "correctedAccountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("correctedRoutingNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "correctedRoutingNumber"));
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
