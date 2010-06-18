/**
 * BankTransferReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class BankTransferReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String accountHolder;

    private java.lang.String accountNumber;

    private java.lang.String amount;

    private java.lang.String bankName;

    private java.lang.String bankCity;

    private java.lang.String bankCountry;

    private java.lang.String paymentReference;

    private java.lang.String processorResponse;

    private java.lang.String bankSwiftCode;

    private java.lang.String bankSpecialID;

    private java.lang.String requestDateTime;

    private java.lang.String reconciliationID;

    private java.lang.String iban;

    private java.lang.String bankCode;

    private java.lang.String branchCode;

    public BankTransferReply() {
    }

    public BankTransferReply(
           java.math.BigInteger reasonCode,
           java.lang.String accountHolder,
           java.lang.String accountNumber,
           java.lang.String amount,
           java.lang.String bankName,
           java.lang.String bankCity,
           java.lang.String bankCountry,
           java.lang.String paymentReference,
           java.lang.String processorResponse,
           java.lang.String bankSwiftCode,
           java.lang.String bankSpecialID,
           java.lang.String requestDateTime,
           java.lang.String reconciliationID,
           java.lang.String iban,
           java.lang.String bankCode,
           java.lang.String branchCode) {
           this.reasonCode = reasonCode;
           this.accountHolder = accountHolder;
           this.accountNumber = accountNumber;
           this.amount = amount;
           this.bankName = bankName;
           this.bankCity = bankCity;
           this.bankCountry = bankCountry;
           this.paymentReference = paymentReference;
           this.processorResponse = processorResponse;
           this.bankSwiftCode = bankSwiftCode;
           this.bankSpecialID = bankSpecialID;
           this.requestDateTime = requestDateTime;
           this.reconciliationID = reconciliationID;
           this.iban = iban;
           this.bankCode = bankCode;
           this.branchCode = branchCode;
    }


    /**
     * Gets the reasonCode value for this BankTransferReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this BankTransferReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the accountHolder value for this BankTransferReply.
     * 
     * @return accountHolder
     */
    public java.lang.String getAccountHolder() {
        return accountHolder;
    }


    /**
     * Sets the accountHolder value for this BankTransferReply.
     * 
     * @param accountHolder
     */
    public void setAccountHolder(java.lang.String accountHolder) {
        this.accountHolder = accountHolder;
    }


    /**
     * Gets the accountNumber value for this BankTransferReply.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this BankTransferReply.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the amount value for this BankTransferReply.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this BankTransferReply.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the bankName value for this BankTransferReply.
     * 
     * @return bankName
     */
    public java.lang.String getBankName() {
        return bankName;
    }


    /**
     * Sets the bankName value for this BankTransferReply.
     * 
     * @param bankName
     */
    public void setBankName(java.lang.String bankName) {
        this.bankName = bankName;
    }


    /**
     * Gets the bankCity value for this BankTransferReply.
     * 
     * @return bankCity
     */
    public java.lang.String getBankCity() {
        return bankCity;
    }


    /**
     * Sets the bankCity value for this BankTransferReply.
     * 
     * @param bankCity
     */
    public void setBankCity(java.lang.String bankCity) {
        this.bankCity = bankCity;
    }


    /**
     * Gets the bankCountry value for this BankTransferReply.
     * 
     * @return bankCountry
     */
    public java.lang.String getBankCountry() {
        return bankCountry;
    }


    /**
     * Sets the bankCountry value for this BankTransferReply.
     * 
     * @param bankCountry
     */
    public void setBankCountry(java.lang.String bankCountry) {
        this.bankCountry = bankCountry;
    }


    /**
     * Gets the paymentReference value for this BankTransferReply.
     * 
     * @return paymentReference
     */
    public java.lang.String getPaymentReference() {
        return paymentReference;
    }


    /**
     * Sets the paymentReference value for this BankTransferReply.
     * 
     * @param paymentReference
     */
    public void setPaymentReference(java.lang.String paymentReference) {
        this.paymentReference = paymentReference;
    }


    /**
     * Gets the processorResponse value for this BankTransferReply.
     * 
     * @return processorResponse
     */
    public java.lang.String getProcessorResponse() {
        return processorResponse;
    }


    /**
     * Sets the processorResponse value for this BankTransferReply.
     * 
     * @param processorResponse
     */
    public void setProcessorResponse(java.lang.String processorResponse) {
        this.processorResponse = processorResponse;
    }


    /**
     * Gets the bankSwiftCode value for this BankTransferReply.
     * 
     * @return bankSwiftCode
     */
    public java.lang.String getBankSwiftCode() {
        return bankSwiftCode;
    }


    /**
     * Sets the bankSwiftCode value for this BankTransferReply.
     * 
     * @param bankSwiftCode
     */
    public void setBankSwiftCode(java.lang.String bankSwiftCode) {
        this.bankSwiftCode = bankSwiftCode;
    }


    /**
     * Gets the bankSpecialID value for this BankTransferReply.
     * 
     * @return bankSpecialID
     */
    public java.lang.String getBankSpecialID() {
        return bankSpecialID;
    }


    /**
     * Sets the bankSpecialID value for this BankTransferReply.
     * 
     * @param bankSpecialID
     */
    public void setBankSpecialID(java.lang.String bankSpecialID) {
        this.bankSpecialID = bankSpecialID;
    }


    /**
     * Gets the requestDateTime value for this BankTransferReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this BankTransferReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the reconciliationID value for this BankTransferReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this BankTransferReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the iban value for this BankTransferReply.
     * 
     * @return iban
     */
    public java.lang.String getIban() {
        return iban;
    }


    /**
     * Sets the iban value for this BankTransferReply.
     * 
     * @param iban
     */
    public void setIban(java.lang.String iban) {
        this.iban = iban;
    }


    /**
     * Gets the bankCode value for this BankTransferReply.
     * 
     * @return bankCode
     */
    public java.lang.String getBankCode() {
        return bankCode;
    }


    /**
     * Sets the bankCode value for this BankTransferReply.
     * 
     * @param bankCode
     */
    public void setBankCode(java.lang.String bankCode) {
        this.bankCode = bankCode;
    }


    /**
     * Gets the branchCode value for this BankTransferReply.
     * 
     * @return branchCode
     */
    public java.lang.String getBranchCode() {
        return branchCode;
    }


    /**
     * Sets the branchCode value for this BankTransferReply.
     * 
     * @param branchCode
     */
    public void setBranchCode(java.lang.String branchCode) {
        this.branchCode = branchCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BankTransferReply)) return false;
        BankTransferReply other = (BankTransferReply) obj;
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
            ((this.accountHolder==null && other.getAccountHolder()==null) || 
             (this.accountHolder!=null &&
              this.accountHolder.equals(other.getAccountHolder()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.bankName==null && other.getBankName()==null) || 
             (this.bankName!=null &&
              this.bankName.equals(other.getBankName()))) &&
            ((this.bankCity==null && other.getBankCity()==null) || 
             (this.bankCity!=null &&
              this.bankCity.equals(other.getBankCity()))) &&
            ((this.bankCountry==null && other.getBankCountry()==null) || 
             (this.bankCountry!=null &&
              this.bankCountry.equals(other.getBankCountry()))) &&
            ((this.paymentReference==null && other.getPaymentReference()==null) || 
             (this.paymentReference!=null &&
              this.paymentReference.equals(other.getPaymentReference()))) &&
            ((this.processorResponse==null && other.getProcessorResponse()==null) || 
             (this.processorResponse!=null &&
              this.processorResponse.equals(other.getProcessorResponse()))) &&
            ((this.bankSwiftCode==null && other.getBankSwiftCode()==null) || 
             (this.bankSwiftCode!=null &&
              this.bankSwiftCode.equals(other.getBankSwiftCode()))) &&
            ((this.bankSpecialID==null && other.getBankSpecialID()==null) || 
             (this.bankSpecialID!=null &&
              this.bankSpecialID.equals(other.getBankSpecialID()))) &&
            ((this.requestDateTime==null && other.getRequestDateTime()==null) || 
             (this.requestDateTime!=null &&
              this.requestDateTime.equals(other.getRequestDateTime()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.iban==null && other.getIban()==null) || 
             (this.iban!=null &&
              this.iban.equals(other.getIban()))) &&
            ((this.bankCode==null && other.getBankCode()==null) || 
             (this.bankCode!=null &&
              this.bankCode.equals(other.getBankCode()))) &&
            ((this.branchCode==null && other.getBranchCode()==null) || 
             (this.branchCode!=null &&
              this.branchCode.equals(other.getBranchCode())));
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
        if (getAccountHolder() != null) {
            _hashCode += getAccountHolder().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getBankName() != null) {
            _hashCode += getBankName().hashCode();
        }
        if (getBankCity() != null) {
            _hashCode += getBankCity().hashCode();
        }
        if (getBankCountry() != null) {
            _hashCode += getBankCountry().hashCode();
        }
        if (getPaymentReference() != null) {
            _hashCode += getPaymentReference().hashCode();
        }
        if (getProcessorResponse() != null) {
            _hashCode += getProcessorResponse().hashCode();
        }
        if (getBankSwiftCode() != null) {
            _hashCode += getBankSwiftCode().hashCode();
        }
        if (getBankSpecialID() != null) {
            _hashCode += getBankSpecialID().hashCode();
        }
        if (getRequestDateTime() != null) {
            _hashCode += getRequestDateTime().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getIban() != null) {
            _hashCode += getIban().hashCode();
        }
        if (getBankCode() != null) {
            _hashCode += getBankCode().hashCode();
        }
        if (getBranchCode() != null) {
            _hashCode += getBranchCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BankTransferReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountHolder");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "accountHolder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "accountNumber"));
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
        elemField.setFieldName("bankName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankCity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentReference");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentReference"));
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
        elemField.setFieldName("bankSwiftCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankSwiftCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankSpecialID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankSpecialID"));
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
        elemField.setFieldName("reconciliationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reconciliationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iban");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "iban"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("branchCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "branchCode"));
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
