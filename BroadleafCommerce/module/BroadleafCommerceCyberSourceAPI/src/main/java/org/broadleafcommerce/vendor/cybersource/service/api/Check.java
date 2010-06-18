/**
 * Check.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class Check  implements java.io.Serializable {
    private java.lang.String fullName;

    private java.lang.String accountNumber;

    private java.lang.String accountType;

    private java.lang.String bankTransitNumber;

    private java.lang.String checkNumber;

    private java.lang.String secCode;

    private java.lang.String accountEncoderID;

    private java.lang.String authenticateID;

    private java.lang.String paymentInfo;

    public Check() {
    }

    public Check(
           java.lang.String fullName,
           java.lang.String accountNumber,
           java.lang.String accountType,
           java.lang.String bankTransitNumber,
           java.lang.String checkNumber,
           java.lang.String secCode,
           java.lang.String accountEncoderID,
           java.lang.String authenticateID,
           java.lang.String paymentInfo) {
           this.fullName = fullName;
           this.accountNumber = accountNumber;
           this.accountType = accountType;
           this.bankTransitNumber = bankTransitNumber;
           this.checkNumber = checkNumber;
           this.secCode = secCode;
           this.accountEncoderID = accountEncoderID;
           this.authenticateID = authenticateID;
           this.paymentInfo = paymentInfo;
    }


    /**
     * Gets the fullName value for this Check.
     * 
     * @return fullName
     */
    public java.lang.String getFullName() {
        return fullName;
    }


    /**
     * Sets the fullName value for this Check.
     * 
     * @param fullName
     */
    public void setFullName(java.lang.String fullName) {
        this.fullName = fullName;
    }


    /**
     * Gets the accountNumber value for this Check.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this Check.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the accountType value for this Check.
     * 
     * @return accountType
     */
    public java.lang.String getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType value for this Check.
     * 
     * @param accountType
     */
    public void setAccountType(java.lang.String accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the bankTransitNumber value for this Check.
     * 
     * @return bankTransitNumber
     */
    public java.lang.String getBankTransitNumber() {
        return bankTransitNumber;
    }


    /**
     * Sets the bankTransitNumber value for this Check.
     * 
     * @param bankTransitNumber
     */
    public void setBankTransitNumber(java.lang.String bankTransitNumber) {
        this.bankTransitNumber = bankTransitNumber;
    }


    /**
     * Gets the checkNumber value for this Check.
     * 
     * @return checkNumber
     */
    public java.lang.String getCheckNumber() {
        return checkNumber;
    }


    /**
     * Sets the checkNumber value for this Check.
     * 
     * @param checkNumber
     */
    public void setCheckNumber(java.lang.String checkNumber) {
        this.checkNumber = checkNumber;
    }


    /**
     * Gets the secCode value for this Check.
     * 
     * @return secCode
     */
    public java.lang.String getSecCode() {
        return secCode;
    }


    /**
     * Sets the secCode value for this Check.
     * 
     * @param secCode
     */
    public void setSecCode(java.lang.String secCode) {
        this.secCode = secCode;
    }


    /**
     * Gets the accountEncoderID value for this Check.
     * 
     * @return accountEncoderID
     */
    public java.lang.String getAccountEncoderID() {
        return accountEncoderID;
    }


    /**
     * Sets the accountEncoderID value for this Check.
     * 
     * @param accountEncoderID
     */
    public void setAccountEncoderID(java.lang.String accountEncoderID) {
        this.accountEncoderID = accountEncoderID;
    }


    /**
     * Gets the authenticateID value for this Check.
     * 
     * @return authenticateID
     */
    public java.lang.String getAuthenticateID() {
        return authenticateID;
    }


    /**
     * Sets the authenticateID value for this Check.
     * 
     * @param authenticateID
     */
    public void setAuthenticateID(java.lang.String authenticateID) {
        this.authenticateID = authenticateID;
    }


    /**
     * Gets the paymentInfo value for this Check.
     * 
     * @return paymentInfo
     */
    public java.lang.String getPaymentInfo() {
        return paymentInfo;
    }


    /**
     * Sets the paymentInfo value for this Check.
     * 
     * @param paymentInfo
     */
    public void setPaymentInfo(java.lang.String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Check)) return false;
        Check other = (Check) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.fullName==null && other.getFullName()==null) || 
             (this.fullName!=null &&
              this.fullName.equals(other.getFullName()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.accountType==null && other.getAccountType()==null) || 
             (this.accountType!=null &&
              this.accountType.equals(other.getAccountType()))) &&
            ((this.bankTransitNumber==null && other.getBankTransitNumber()==null) || 
             (this.bankTransitNumber!=null &&
              this.bankTransitNumber.equals(other.getBankTransitNumber()))) &&
            ((this.checkNumber==null && other.getCheckNumber()==null) || 
             (this.checkNumber!=null &&
              this.checkNumber.equals(other.getCheckNumber()))) &&
            ((this.secCode==null && other.getSecCode()==null) || 
             (this.secCode!=null &&
              this.secCode.equals(other.getSecCode()))) &&
            ((this.accountEncoderID==null && other.getAccountEncoderID()==null) || 
             (this.accountEncoderID!=null &&
              this.accountEncoderID.equals(other.getAccountEncoderID()))) &&
            ((this.authenticateID==null && other.getAuthenticateID()==null) || 
             (this.authenticateID!=null &&
              this.authenticateID.equals(other.getAuthenticateID()))) &&
            ((this.paymentInfo==null && other.getPaymentInfo()==null) || 
             (this.paymentInfo!=null &&
              this.paymentInfo.equals(other.getPaymentInfo())));
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
        if (getFullName() != null) {
            _hashCode += getFullName().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getAccountType() != null) {
            _hashCode += getAccountType().hashCode();
        }
        if (getBankTransitNumber() != null) {
            _hashCode += getBankTransitNumber().hashCode();
        }
        if (getCheckNumber() != null) {
            _hashCode += getCheckNumber().hashCode();
        }
        if (getSecCode() != null) {
            _hashCode += getSecCode().hashCode();
        }
        if (getAccountEncoderID() != null) {
            _hashCode += getAccountEncoderID().hashCode();
        }
        if (getAuthenticateID() != null) {
            _hashCode += getAuthenticateID().hashCode();
        }
        if (getPaymentInfo() != null) {
            _hashCode += getPaymentInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Check.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Check"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fullName"));
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
        elemField.setFieldName("accountType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "accountType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransitNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransitNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "checkNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "secCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountEncoderID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "accountEncoderID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticateID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authenticateID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentInfo"));
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
