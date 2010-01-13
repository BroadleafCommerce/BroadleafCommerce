/**
 * Card.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class Card  implements java.io.Serializable {
    private java.lang.String fullName;

    private java.lang.String accountNumber;

    private java.math.BigInteger expirationMonth;

    private java.math.BigInteger expirationYear;

    private java.lang.String cvIndicator;

    private java.lang.String cvNumber;

    private java.lang.String cardType;

    private java.lang.String issueNumber;

    private java.math.BigInteger startMonth;

    private java.math.BigInteger startYear;

    private java.lang.String pin;

    private java.lang.String accountEncoderID;

    private java.lang.String bin;

    public Card() {
    }

    public Card(
           java.lang.String fullName,
           java.lang.String accountNumber,
           java.math.BigInteger expirationMonth,
           java.math.BigInteger expirationYear,
           java.lang.String cvIndicator,
           java.lang.String cvNumber,
           java.lang.String cardType,
           java.lang.String issueNumber,
           java.math.BigInteger startMonth,
           java.math.BigInteger startYear,
           java.lang.String pin,
           java.lang.String accountEncoderID,
           java.lang.String bin) {
           this.fullName = fullName;
           this.accountNumber = accountNumber;
           this.expirationMonth = expirationMonth;
           this.expirationYear = expirationYear;
           this.cvIndicator = cvIndicator;
           this.cvNumber = cvNumber;
           this.cardType = cardType;
           this.issueNumber = issueNumber;
           this.startMonth = startMonth;
           this.startYear = startYear;
           this.pin = pin;
           this.accountEncoderID = accountEncoderID;
           this.bin = bin;
    }


    /**
     * Gets the fullName value for this Card.
     * 
     * @return fullName
     */
    public java.lang.String getFullName() {
        return fullName;
    }


    /**
     * Sets the fullName value for this Card.
     * 
     * @param fullName
     */
    public void setFullName(java.lang.String fullName) {
        this.fullName = fullName;
    }


    /**
     * Gets the accountNumber value for this Card.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this Card.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the expirationMonth value for this Card.
     * 
     * @return expirationMonth
     */
    public java.math.BigInteger getExpirationMonth() {
        return expirationMonth;
    }


    /**
     * Sets the expirationMonth value for this Card.
     * 
     * @param expirationMonth
     */
    public void setExpirationMonth(java.math.BigInteger expirationMonth) {
        this.expirationMonth = expirationMonth;
    }


    /**
     * Gets the expirationYear value for this Card.
     * 
     * @return expirationYear
     */
    public java.math.BigInteger getExpirationYear() {
        return expirationYear;
    }


    /**
     * Sets the expirationYear value for this Card.
     * 
     * @param expirationYear
     */
    public void setExpirationYear(java.math.BigInteger expirationYear) {
        this.expirationYear = expirationYear;
    }


    /**
     * Gets the cvIndicator value for this Card.
     * 
     * @return cvIndicator
     */
    public java.lang.String getCvIndicator() {
        return cvIndicator;
    }


    /**
     * Sets the cvIndicator value for this Card.
     * 
     * @param cvIndicator
     */
    public void setCvIndicator(java.lang.String cvIndicator) {
        this.cvIndicator = cvIndicator;
    }


    /**
     * Gets the cvNumber value for this Card.
     * 
     * @return cvNumber
     */
    public java.lang.String getCvNumber() {
        return cvNumber;
    }


    /**
     * Sets the cvNumber value for this Card.
     * 
     * @param cvNumber
     */
    public void setCvNumber(java.lang.String cvNumber) {
        this.cvNumber = cvNumber;
    }


    /**
     * Gets the cardType value for this Card.
     * 
     * @return cardType
     */
    public java.lang.String getCardType() {
        return cardType;
    }


    /**
     * Sets the cardType value for this Card.
     * 
     * @param cardType
     */
    public void setCardType(java.lang.String cardType) {
        this.cardType = cardType;
    }


    /**
     * Gets the issueNumber value for this Card.
     * 
     * @return issueNumber
     */
    public java.lang.String getIssueNumber() {
        return issueNumber;
    }


    /**
     * Sets the issueNumber value for this Card.
     * 
     * @param issueNumber
     */
    public void setIssueNumber(java.lang.String issueNumber) {
        this.issueNumber = issueNumber;
    }


    /**
     * Gets the startMonth value for this Card.
     * 
     * @return startMonth
     */
    public java.math.BigInteger getStartMonth() {
        return startMonth;
    }


    /**
     * Sets the startMonth value for this Card.
     * 
     * @param startMonth
     */
    public void setStartMonth(java.math.BigInteger startMonth) {
        this.startMonth = startMonth;
    }


    /**
     * Gets the startYear value for this Card.
     * 
     * @return startYear
     */
    public java.math.BigInteger getStartYear() {
        return startYear;
    }


    /**
     * Sets the startYear value for this Card.
     * 
     * @param startYear
     */
    public void setStartYear(java.math.BigInteger startYear) {
        this.startYear = startYear;
    }


    /**
     * Gets the pin value for this Card.
     * 
     * @return pin
     */
    public java.lang.String getPin() {
        return pin;
    }


    /**
     * Sets the pin value for this Card.
     * 
     * @param pin
     */
    public void setPin(java.lang.String pin) {
        this.pin = pin;
    }


    /**
     * Gets the accountEncoderID value for this Card.
     * 
     * @return accountEncoderID
     */
    public java.lang.String getAccountEncoderID() {
        return accountEncoderID;
    }


    /**
     * Sets the accountEncoderID value for this Card.
     * 
     * @param accountEncoderID
     */
    public void setAccountEncoderID(java.lang.String accountEncoderID) {
        this.accountEncoderID = accountEncoderID;
    }


    /**
     * Gets the bin value for this Card.
     * 
     * @return bin
     */
    public java.lang.String getBin() {
        return bin;
    }


    /**
     * Sets the bin value for this Card.
     * 
     * @param bin
     */
    public void setBin(java.lang.String bin) {
        this.bin = bin;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Card)) return false;
        Card other = (Card) obj;
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
            ((this.expirationMonth==null && other.getExpirationMonth()==null) || 
             (this.expirationMonth!=null &&
              this.expirationMonth.equals(other.getExpirationMonth()))) &&
            ((this.expirationYear==null && other.getExpirationYear()==null) || 
             (this.expirationYear!=null &&
              this.expirationYear.equals(other.getExpirationYear()))) &&
            ((this.cvIndicator==null && other.getCvIndicator()==null) || 
             (this.cvIndicator!=null &&
              this.cvIndicator.equals(other.getCvIndicator()))) &&
            ((this.cvNumber==null && other.getCvNumber()==null) || 
             (this.cvNumber!=null &&
              this.cvNumber.equals(other.getCvNumber()))) &&
            ((this.cardType==null && other.getCardType()==null) || 
             (this.cardType!=null &&
              this.cardType.equals(other.getCardType()))) &&
            ((this.issueNumber==null && other.getIssueNumber()==null) || 
             (this.issueNumber!=null &&
              this.issueNumber.equals(other.getIssueNumber()))) &&
            ((this.startMonth==null && other.getStartMonth()==null) || 
             (this.startMonth!=null &&
              this.startMonth.equals(other.getStartMonth()))) &&
            ((this.startYear==null && other.getStartYear()==null) || 
             (this.startYear!=null &&
              this.startYear.equals(other.getStartYear()))) &&
            ((this.pin==null && other.getPin()==null) || 
             (this.pin!=null &&
              this.pin.equals(other.getPin()))) &&
            ((this.accountEncoderID==null && other.getAccountEncoderID()==null) || 
             (this.accountEncoderID!=null &&
              this.accountEncoderID.equals(other.getAccountEncoderID()))) &&
            ((this.bin==null && other.getBin()==null) || 
             (this.bin!=null &&
              this.bin.equals(other.getBin())));
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
        if (getExpirationMonth() != null) {
            _hashCode += getExpirationMonth().hashCode();
        }
        if (getExpirationYear() != null) {
            _hashCode += getExpirationYear().hashCode();
        }
        if (getCvIndicator() != null) {
            _hashCode += getCvIndicator().hashCode();
        }
        if (getCvNumber() != null) {
            _hashCode += getCvNumber().hashCode();
        }
        if (getCardType() != null) {
            _hashCode += getCardType().hashCode();
        }
        if (getIssueNumber() != null) {
            _hashCode += getIssueNumber().hashCode();
        }
        if (getStartMonth() != null) {
            _hashCode += getStartMonth().hashCode();
        }
        if (getStartYear() != null) {
            _hashCode += getStartYear().hashCode();
        }
        if (getPin() != null) {
            _hashCode += getPin().hashCode();
        }
        if (getAccountEncoderID() != null) {
            _hashCode += getAccountEncoderID().hashCode();
        }
        if (getBin() != null) {
            _hashCode += getBin().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Card.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Card"));
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
        elemField.setFieldName("expirationMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "expirationMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expirationYear");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "expirationYear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cvIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cvNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issueNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "issueNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "startMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startYear");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "startYear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pin");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pin"));
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
        elemField.setFieldName("bin");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bin"));
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
