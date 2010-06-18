/**
 * CCCaptureReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class CCCaptureReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String requestDateTime;

    private java.lang.String amount;

    private java.lang.String reconciliationID;

    private org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals;

    private java.lang.String fxQuoteID;

    private java.lang.String fxQuoteRate;

    private java.lang.String fxQuoteType;

    private java.lang.String fxQuoteExpirationDateTime;

    private java.lang.String purchasingLevel3Enabled;

    private java.lang.String enhancedDataEnabled;

    public CCCaptureReply() {
    }

    public CCCaptureReply(
           java.math.BigInteger reasonCode,
           java.lang.String requestDateTime,
           java.lang.String amount,
           java.lang.String reconciliationID,
           org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals,
           java.lang.String fxQuoteID,
           java.lang.String fxQuoteRate,
           java.lang.String fxQuoteType,
           java.lang.String fxQuoteExpirationDateTime,
           java.lang.String purchasingLevel3Enabled,
           java.lang.String enhancedDataEnabled) {
           this.reasonCode = reasonCode;
           this.requestDateTime = requestDateTime;
           this.amount = amount;
           this.reconciliationID = reconciliationID;
           this.fundingTotals = fundingTotals;
           this.fxQuoteID = fxQuoteID;
           this.fxQuoteRate = fxQuoteRate;
           this.fxQuoteType = fxQuoteType;
           this.fxQuoteExpirationDateTime = fxQuoteExpirationDateTime;
           this.purchasingLevel3Enabled = purchasingLevel3Enabled;
           this.enhancedDataEnabled = enhancedDataEnabled;
    }


    /**
     * Gets the reasonCode value for this CCCaptureReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this CCCaptureReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the requestDateTime value for this CCCaptureReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this CCCaptureReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the amount value for this CCCaptureReply.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this CCCaptureReply.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the reconciliationID value for this CCCaptureReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this CCCaptureReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the fundingTotals value for this CCCaptureReply.
     * 
     * @return fundingTotals
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals getFundingTotals() {
        return fundingTotals;
    }


    /**
     * Sets the fundingTotals value for this CCCaptureReply.
     * 
     * @param fundingTotals
     */
    public void setFundingTotals(org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals) {
        this.fundingTotals = fundingTotals;
    }


    /**
     * Gets the fxQuoteID value for this CCCaptureReply.
     * 
     * @return fxQuoteID
     */
    public java.lang.String getFxQuoteID() {
        return fxQuoteID;
    }


    /**
     * Sets the fxQuoteID value for this CCCaptureReply.
     * 
     * @param fxQuoteID
     */
    public void setFxQuoteID(java.lang.String fxQuoteID) {
        this.fxQuoteID = fxQuoteID;
    }


    /**
     * Gets the fxQuoteRate value for this CCCaptureReply.
     * 
     * @return fxQuoteRate
     */
    public java.lang.String getFxQuoteRate() {
        return fxQuoteRate;
    }


    /**
     * Sets the fxQuoteRate value for this CCCaptureReply.
     * 
     * @param fxQuoteRate
     */
    public void setFxQuoteRate(java.lang.String fxQuoteRate) {
        this.fxQuoteRate = fxQuoteRate;
    }


    /**
     * Gets the fxQuoteType value for this CCCaptureReply.
     * 
     * @return fxQuoteType
     */
    public java.lang.String getFxQuoteType() {
        return fxQuoteType;
    }


    /**
     * Sets the fxQuoteType value for this CCCaptureReply.
     * 
     * @param fxQuoteType
     */
    public void setFxQuoteType(java.lang.String fxQuoteType) {
        this.fxQuoteType = fxQuoteType;
    }


    /**
     * Gets the fxQuoteExpirationDateTime value for this CCCaptureReply.
     * 
     * @return fxQuoteExpirationDateTime
     */
    public java.lang.String getFxQuoteExpirationDateTime() {
        return fxQuoteExpirationDateTime;
    }


    /**
     * Sets the fxQuoteExpirationDateTime value for this CCCaptureReply.
     * 
     * @param fxQuoteExpirationDateTime
     */
    public void setFxQuoteExpirationDateTime(java.lang.String fxQuoteExpirationDateTime) {
        this.fxQuoteExpirationDateTime = fxQuoteExpirationDateTime;
    }


    /**
     * Gets the purchasingLevel3Enabled value for this CCCaptureReply.
     * 
     * @return purchasingLevel3Enabled
     */
    public java.lang.String getPurchasingLevel3Enabled() {
        return purchasingLevel3Enabled;
    }


    /**
     * Sets the purchasingLevel3Enabled value for this CCCaptureReply.
     * 
     * @param purchasingLevel3Enabled
     */
    public void setPurchasingLevel3Enabled(java.lang.String purchasingLevel3Enabled) {
        this.purchasingLevel3Enabled = purchasingLevel3Enabled;
    }


    /**
     * Gets the enhancedDataEnabled value for this CCCaptureReply.
     * 
     * @return enhancedDataEnabled
     */
    public java.lang.String getEnhancedDataEnabled() {
        return enhancedDataEnabled;
    }


    /**
     * Sets the enhancedDataEnabled value for this CCCaptureReply.
     * 
     * @param enhancedDataEnabled
     */
    public void setEnhancedDataEnabled(java.lang.String enhancedDataEnabled) {
        this.enhancedDataEnabled = enhancedDataEnabled;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CCCaptureReply)) return false;
        CCCaptureReply other = (CCCaptureReply) obj;
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
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.fundingTotals==null && other.getFundingTotals()==null) || 
             (this.fundingTotals!=null &&
              this.fundingTotals.equals(other.getFundingTotals()))) &&
            ((this.fxQuoteID==null && other.getFxQuoteID()==null) || 
             (this.fxQuoteID!=null &&
              this.fxQuoteID.equals(other.getFxQuoteID()))) &&
            ((this.fxQuoteRate==null && other.getFxQuoteRate()==null) || 
             (this.fxQuoteRate!=null &&
              this.fxQuoteRate.equals(other.getFxQuoteRate()))) &&
            ((this.fxQuoteType==null && other.getFxQuoteType()==null) || 
             (this.fxQuoteType!=null &&
              this.fxQuoteType.equals(other.getFxQuoteType()))) &&
            ((this.fxQuoteExpirationDateTime==null && other.getFxQuoteExpirationDateTime()==null) || 
             (this.fxQuoteExpirationDateTime!=null &&
              this.fxQuoteExpirationDateTime.equals(other.getFxQuoteExpirationDateTime()))) &&
            ((this.purchasingLevel3Enabled==null && other.getPurchasingLevel3Enabled()==null) || 
             (this.purchasingLevel3Enabled!=null &&
              this.purchasingLevel3Enabled.equals(other.getPurchasingLevel3Enabled()))) &&
            ((this.enhancedDataEnabled==null && other.getEnhancedDataEnabled()==null) || 
             (this.enhancedDataEnabled!=null &&
              this.enhancedDataEnabled.equals(other.getEnhancedDataEnabled())));
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
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getFundingTotals() != null) {
            _hashCode += getFundingTotals().hashCode();
        }
        if (getFxQuoteID() != null) {
            _hashCode += getFxQuoteID().hashCode();
        }
        if (getFxQuoteRate() != null) {
            _hashCode += getFxQuoteRate().hashCode();
        }
        if (getFxQuoteType() != null) {
            _hashCode += getFxQuoteType().hashCode();
        }
        if (getFxQuoteExpirationDateTime() != null) {
            _hashCode += getFxQuoteExpirationDateTime().hashCode();
        }
        if (getPurchasingLevel3Enabled() != null) {
            _hashCode += getPurchasingLevel3Enabled().hashCode();
        }
        if (getEnhancedDataEnabled() != null) {
            _hashCode += getEnhancedDataEnabled().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CCCaptureReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCaptureReply"));
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
        elemField.setFieldName("reconciliationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reconciliationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fundingTotals");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fundingTotals"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FundingTotals"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteRate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteExpirationDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteExpirationDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchasingLevel3Enabled");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchasingLevel3Enabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enhancedDataEnabled");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "enhancedDataEnabled"));
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
