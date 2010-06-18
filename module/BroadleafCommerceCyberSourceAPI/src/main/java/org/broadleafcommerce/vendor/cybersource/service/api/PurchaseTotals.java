/**
 * PurchaseTotals.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PurchaseTotals  implements java.io.Serializable {
    private java.lang.String currency;

    private java.lang.String discountAmount;

    private java.lang.String taxAmount;

    private java.lang.String dutyAmount;

    private java.lang.String grandTotalAmount;

    private java.lang.String freightAmount;

    private java.lang.String foreignAmount;

    private java.lang.String foreignCurrency;

    private java.lang.String exchangeRate;

    private java.lang.String exchangeRateTimeStamp;

    public PurchaseTotals() {
    }

    public PurchaseTotals(
           java.lang.String currency,
           java.lang.String discountAmount,
           java.lang.String taxAmount,
           java.lang.String dutyAmount,
           java.lang.String grandTotalAmount,
           java.lang.String freightAmount,
           java.lang.String foreignAmount,
           java.lang.String foreignCurrency,
           java.lang.String exchangeRate,
           java.lang.String exchangeRateTimeStamp) {
           this.currency = currency;
           this.discountAmount = discountAmount;
           this.taxAmount = taxAmount;
           this.dutyAmount = dutyAmount;
           this.grandTotalAmount = grandTotalAmount;
           this.freightAmount = freightAmount;
           this.foreignAmount = foreignAmount;
           this.foreignCurrency = foreignCurrency;
           this.exchangeRate = exchangeRate;
           this.exchangeRateTimeStamp = exchangeRateTimeStamp;
    }


    /**
     * Gets the currency value for this PurchaseTotals.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this PurchaseTotals.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the discountAmount value for this PurchaseTotals.
     * 
     * @return discountAmount
     */
    public java.lang.String getDiscountAmount() {
        return discountAmount;
    }


    /**
     * Sets the discountAmount value for this PurchaseTotals.
     * 
     * @param discountAmount
     */
    public void setDiscountAmount(java.lang.String discountAmount) {
        this.discountAmount = discountAmount;
    }


    /**
     * Gets the taxAmount value for this PurchaseTotals.
     * 
     * @return taxAmount
     */
    public java.lang.String getTaxAmount() {
        return taxAmount;
    }


    /**
     * Sets the taxAmount value for this PurchaseTotals.
     * 
     * @param taxAmount
     */
    public void setTaxAmount(java.lang.String taxAmount) {
        this.taxAmount = taxAmount;
    }


    /**
     * Gets the dutyAmount value for this PurchaseTotals.
     * 
     * @return dutyAmount
     */
    public java.lang.String getDutyAmount() {
        return dutyAmount;
    }


    /**
     * Sets the dutyAmount value for this PurchaseTotals.
     * 
     * @param dutyAmount
     */
    public void setDutyAmount(java.lang.String dutyAmount) {
        this.dutyAmount = dutyAmount;
    }


    /**
     * Gets the grandTotalAmount value for this PurchaseTotals.
     * 
     * @return grandTotalAmount
     */
    public java.lang.String getGrandTotalAmount() {
        return grandTotalAmount;
    }


    /**
     * Sets the grandTotalAmount value for this PurchaseTotals.
     * 
     * @param grandTotalAmount
     */
    public void setGrandTotalAmount(java.lang.String grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }


    /**
     * Gets the freightAmount value for this PurchaseTotals.
     * 
     * @return freightAmount
     */
    public java.lang.String getFreightAmount() {
        return freightAmount;
    }


    /**
     * Sets the freightAmount value for this PurchaseTotals.
     * 
     * @param freightAmount
     */
    public void setFreightAmount(java.lang.String freightAmount) {
        this.freightAmount = freightAmount;
    }


    /**
     * Gets the foreignAmount value for this PurchaseTotals.
     * 
     * @return foreignAmount
     */
    public java.lang.String getForeignAmount() {
        return foreignAmount;
    }


    /**
     * Sets the foreignAmount value for this PurchaseTotals.
     * 
     * @param foreignAmount
     */
    public void setForeignAmount(java.lang.String foreignAmount) {
        this.foreignAmount = foreignAmount;
    }


    /**
     * Gets the foreignCurrency value for this PurchaseTotals.
     * 
     * @return foreignCurrency
     */
    public java.lang.String getForeignCurrency() {
        return foreignCurrency;
    }


    /**
     * Sets the foreignCurrency value for this PurchaseTotals.
     * 
     * @param foreignCurrency
     */
    public void setForeignCurrency(java.lang.String foreignCurrency) {
        this.foreignCurrency = foreignCurrency;
    }


    /**
     * Gets the exchangeRate value for this PurchaseTotals.
     * 
     * @return exchangeRate
     */
    public java.lang.String getExchangeRate() {
        return exchangeRate;
    }


    /**
     * Sets the exchangeRate value for this PurchaseTotals.
     * 
     * @param exchangeRate
     */
    public void setExchangeRate(java.lang.String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


    /**
     * Gets the exchangeRateTimeStamp value for this PurchaseTotals.
     * 
     * @return exchangeRateTimeStamp
     */
    public java.lang.String getExchangeRateTimeStamp() {
        return exchangeRateTimeStamp;
    }


    /**
     * Sets the exchangeRateTimeStamp value for this PurchaseTotals.
     * 
     * @param exchangeRateTimeStamp
     */
    public void setExchangeRateTimeStamp(java.lang.String exchangeRateTimeStamp) {
        this.exchangeRateTimeStamp = exchangeRateTimeStamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PurchaseTotals)) return false;
        PurchaseTotals other = (PurchaseTotals) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.discountAmount==null && other.getDiscountAmount()==null) || 
             (this.discountAmount!=null &&
              this.discountAmount.equals(other.getDiscountAmount()))) &&
            ((this.taxAmount==null && other.getTaxAmount()==null) || 
             (this.taxAmount!=null &&
              this.taxAmount.equals(other.getTaxAmount()))) &&
            ((this.dutyAmount==null && other.getDutyAmount()==null) || 
             (this.dutyAmount!=null &&
              this.dutyAmount.equals(other.getDutyAmount()))) &&
            ((this.grandTotalAmount==null && other.getGrandTotalAmount()==null) || 
             (this.grandTotalAmount!=null &&
              this.grandTotalAmount.equals(other.getGrandTotalAmount()))) &&
            ((this.freightAmount==null && other.getFreightAmount()==null) || 
             (this.freightAmount!=null &&
              this.freightAmount.equals(other.getFreightAmount()))) &&
            ((this.foreignAmount==null && other.getForeignAmount()==null) || 
             (this.foreignAmount!=null &&
              this.foreignAmount.equals(other.getForeignAmount()))) &&
            ((this.foreignCurrency==null && other.getForeignCurrency()==null) || 
             (this.foreignCurrency!=null &&
              this.foreignCurrency.equals(other.getForeignCurrency()))) &&
            ((this.exchangeRate==null && other.getExchangeRate()==null) || 
             (this.exchangeRate!=null &&
              this.exchangeRate.equals(other.getExchangeRate()))) &&
            ((this.exchangeRateTimeStamp==null && other.getExchangeRateTimeStamp()==null) || 
             (this.exchangeRateTimeStamp!=null &&
              this.exchangeRateTimeStamp.equals(other.getExchangeRateTimeStamp())));
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
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getDiscountAmount() != null) {
            _hashCode += getDiscountAmount().hashCode();
        }
        if (getTaxAmount() != null) {
            _hashCode += getTaxAmount().hashCode();
        }
        if (getDutyAmount() != null) {
            _hashCode += getDutyAmount().hashCode();
        }
        if (getGrandTotalAmount() != null) {
            _hashCode += getGrandTotalAmount().hashCode();
        }
        if (getFreightAmount() != null) {
            _hashCode += getFreightAmount().hashCode();
        }
        if (getForeignAmount() != null) {
            _hashCode += getForeignAmount().hashCode();
        }
        if (getForeignCurrency() != null) {
            _hashCode += getForeignCurrency().hashCode();
        }
        if (getExchangeRate() != null) {
            _hashCode += getExchangeRate().hashCode();
        }
        if (getExchangeRateTimeStamp() != null) {
            _hashCode += getExchangeRateTimeStamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PurchaseTotals.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PurchaseTotals"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("discountAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "discountAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "taxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dutyAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "dutyAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("grandTotalAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "grandTotalAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("freightAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "freightAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("foreignAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "foreignAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("foreignCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "foreignCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exchangeRate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "exchangeRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exchangeRateTimeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "exchangeRateTimeStamp"));
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
