/**
 * FundTransfer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class FundTransfer  implements java.io.Serializable {
    private java.lang.String accountNumber;

    private java.lang.String accountName;

    private java.lang.String bankCheckDigit;

    private java.lang.String iban;

    public FundTransfer() {
    }

    public FundTransfer(
           java.lang.String accountNumber,
           java.lang.String accountName,
           java.lang.String bankCheckDigit,
           java.lang.String iban) {
           this.accountNumber = accountNumber;
           this.accountName = accountName;
           this.bankCheckDigit = bankCheckDigit;
           this.iban = iban;
    }


    /**
     * Gets the accountNumber value for this FundTransfer.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this FundTransfer.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the accountName value for this FundTransfer.
     * 
     * @return accountName
     */
    public java.lang.String getAccountName() {
        return accountName;
    }


    /**
     * Sets the accountName value for this FundTransfer.
     * 
     * @param accountName
     */
    public void setAccountName(java.lang.String accountName) {
        this.accountName = accountName;
    }


    /**
     * Gets the bankCheckDigit value for this FundTransfer.
     * 
     * @return bankCheckDigit
     */
    public java.lang.String getBankCheckDigit() {
        return bankCheckDigit;
    }


    /**
     * Sets the bankCheckDigit value for this FundTransfer.
     * 
     * @param bankCheckDigit
     */
    public void setBankCheckDigit(java.lang.String bankCheckDigit) {
        this.bankCheckDigit = bankCheckDigit;
    }


    /**
     * Gets the iban value for this FundTransfer.
     * 
     * @return iban
     */
    public java.lang.String getIban() {
        return iban;
    }


    /**
     * Sets the iban value for this FundTransfer.
     * 
     * @param iban
     */
    public void setIban(java.lang.String iban) {
        this.iban = iban;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FundTransfer)) return false;
        FundTransfer other = (FundTransfer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.accountName==null && other.getAccountName()==null) || 
             (this.accountName!=null &&
              this.accountName.equals(other.getAccountName()))) &&
            ((this.bankCheckDigit==null && other.getBankCheckDigit()==null) || 
             (this.bankCheckDigit!=null &&
              this.bankCheckDigit.equals(other.getBankCheckDigit()))) &&
            ((this.iban==null && other.getIban()==null) || 
             (this.iban!=null &&
              this.iban.equals(other.getIban())));
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
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getAccountName() != null) {
            _hashCode += getAccountName().hashCode();
        }
        if (getBankCheckDigit() != null) {
            _hashCode += getBankCheckDigit().hashCode();
        }
        if (getIban() != null) {
            _hashCode += getIban().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FundTransfer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FundTransfer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "accountName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankCheckDigit");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankCheckDigit"));
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
