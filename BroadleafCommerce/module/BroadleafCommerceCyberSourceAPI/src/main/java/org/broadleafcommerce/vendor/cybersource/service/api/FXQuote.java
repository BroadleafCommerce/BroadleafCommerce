/**
 * FXQuote.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class FXQuote  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String rate;

    private java.lang.String type;

    private java.lang.String expirationDateTime;

    private java.lang.String currency;

    private java.lang.String fundingCurrency;

    private java.lang.String receivedDateTime;

    public FXQuote() {
    }

    public FXQuote(
           java.lang.String id,
           java.lang.String rate,
           java.lang.String type,
           java.lang.String expirationDateTime,
           java.lang.String currency,
           java.lang.String fundingCurrency,
           java.lang.String receivedDateTime) {
           this.id = id;
           this.rate = rate;
           this.type = type;
           this.expirationDateTime = expirationDateTime;
           this.currency = currency;
           this.fundingCurrency = fundingCurrency;
           this.receivedDateTime = receivedDateTime;
    }


    /**
     * Gets the id value for this FXQuote.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this FXQuote.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the rate value for this FXQuote.
     * 
     * @return rate
     */
    public java.lang.String getRate() {
        return rate;
    }


    /**
     * Sets the rate value for this FXQuote.
     * 
     * @param rate
     */
    public void setRate(java.lang.String rate) {
        this.rate = rate;
    }


    /**
     * Gets the type value for this FXQuote.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this FXQuote.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the expirationDateTime value for this FXQuote.
     * 
     * @return expirationDateTime
     */
    public java.lang.String getExpirationDateTime() {
        return expirationDateTime;
    }


    /**
     * Sets the expirationDateTime value for this FXQuote.
     * 
     * @param expirationDateTime
     */
    public void setExpirationDateTime(java.lang.String expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }


    /**
     * Gets the currency value for this FXQuote.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this FXQuote.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the fundingCurrency value for this FXQuote.
     * 
     * @return fundingCurrency
     */
    public java.lang.String getFundingCurrency() {
        return fundingCurrency;
    }


    /**
     * Sets the fundingCurrency value for this FXQuote.
     * 
     * @param fundingCurrency
     */
    public void setFundingCurrency(java.lang.String fundingCurrency) {
        this.fundingCurrency = fundingCurrency;
    }


    /**
     * Gets the receivedDateTime value for this FXQuote.
     * 
     * @return receivedDateTime
     */
    public java.lang.String getReceivedDateTime() {
        return receivedDateTime;
    }


    /**
     * Sets the receivedDateTime value for this FXQuote.
     * 
     * @param receivedDateTime
     */
    public void setReceivedDateTime(java.lang.String receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FXQuote)) return false;
        FXQuote other = (FXQuote) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.rate==null && other.getRate()==null) || 
             (this.rate!=null &&
              this.rate.equals(other.getRate()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.expirationDateTime==null && other.getExpirationDateTime()==null) || 
             (this.expirationDateTime!=null &&
              this.expirationDateTime.equals(other.getExpirationDateTime()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.fundingCurrency==null && other.getFundingCurrency()==null) || 
             (this.fundingCurrency!=null &&
              this.fundingCurrency.equals(other.getFundingCurrency()))) &&
            ((this.receivedDateTime==null && other.getReceivedDateTime()==null) || 
             (this.receivedDateTime!=null &&
              this.receivedDateTime.equals(other.getReceivedDateTime())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getRate() != null) {
            _hashCode += getRate().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getExpirationDateTime() != null) {
            _hashCode += getExpirationDateTime().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getFundingCurrency() != null) {
            _hashCode += getFundingCurrency().hashCode();
        }
        if (getReceivedDateTime() != null) {
            _hashCode += getReceivedDateTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FXQuote.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FXQuote"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "rate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expirationDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "expirationDateTime"));
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
        elemField.setFieldName("fundingCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fundingCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "receivedDateTime"));
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
