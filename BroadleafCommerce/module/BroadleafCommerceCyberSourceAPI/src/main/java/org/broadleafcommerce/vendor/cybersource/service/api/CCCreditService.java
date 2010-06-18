/**
 * CCCreditService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class CCCreditService  implements java.io.Serializable {
    private java.lang.String captureRequestID;

    private java.lang.String reconciliationID;

    private java.lang.String partialPaymentID;

    private java.lang.String purchasingLevel;

    private java.lang.String industryDatatype;

    private java.lang.String commerceIndicator;

    private java.lang.String billPayment;

    private java.lang.String authorizationXID;

    private java.lang.String occurrenceNumber;

    private java.lang.String authCode;

    private java.lang.String captureRequestToken;

    private java.lang.String merchantReceiptNumber;

    private java.lang.String checksumKey;

    private java.lang.String aggregatorID;

    private java.lang.String run;  // attribute

    public CCCreditService() {
    }

    public CCCreditService(
           java.lang.String captureRequestID,
           java.lang.String reconciliationID,
           java.lang.String partialPaymentID,
           java.lang.String purchasingLevel,
           java.lang.String industryDatatype,
           java.lang.String commerceIndicator,
           java.lang.String billPayment,
           java.lang.String authorizationXID,
           java.lang.String occurrenceNumber,
           java.lang.String authCode,
           java.lang.String captureRequestToken,
           java.lang.String merchantReceiptNumber,
           java.lang.String checksumKey,
           java.lang.String aggregatorID,
           java.lang.String run) {
           this.captureRequestID = captureRequestID;
           this.reconciliationID = reconciliationID;
           this.partialPaymentID = partialPaymentID;
           this.purchasingLevel = purchasingLevel;
           this.industryDatatype = industryDatatype;
           this.commerceIndicator = commerceIndicator;
           this.billPayment = billPayment;
           this.authorizationXID = authorizationXID;
           this.occurrenceNumber = occurrenceNumber;
           this.authCode = authCode;
           this.captureRequestToken = captureRequestToken;
           this.merchantReceiptNumber = merchantReceiptNumber;
           this.checksumKey = checksumKey;
           this.aggregatorID = aggregatorID;
           this.run = run;
    }


    /**
     * Gets the captureRequestID value for this CCCreditService.
     * 
     * @return captureRequestID
     */
    public java.lang.String getCaptureRequestID() {
        return captureRequestID;
    }


    /**
     * Sets the captureRequestID value for this CCCreditService.
     * 
     * @param captureRequestID
     */
    public void setCaptureRequestID(java.lang.String captureRequestID) {
        this.captureRequestID = captureRequestID;
    }


    /**
     * Gets the reconciliationID value for this CCCreditService.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this CCCreditService.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the partialPaymentID value for this CCCreditService.
     * 
     * @return partialPaymentID
     */
    public java.lang.String getPartialPaymentID() {
        return partialPaymentID;
    }


    /**
     * Sets the partialPaymentID value for this CCCreditService.
     * 
     * @param partialPaymentID
     */
    public void setPartialPaymentID(java.lang.String partialPaymentID) {
        this.partialPaymentID = partialPaymentID;
    }


    /**
     * Gets the purchasingLevel value for this CCCreditService.
     * 
     * @return purchasingLevel
     */
    public java.lang.String getPurchasingLevel() {
        return purchasingLevel;
    }


    /**
     * Sets the purchasingLevel value for this CCCreditService.
     * 
     * @param purchasingLevel
     */
    public void setPurchasingLevel(java.lang.String purchasingLevel) {
        this.purchasingLevel = purchasingLevel;
    }


    /**
     * Gets the industryDatatype value for this CCCreditService.
     * 
     * @return industryDatatype
     */
    public java.lang.String getIndustryDatatype() {
        return industryDatatype;
    }


    /**
     * Sets the industryDatatype value for this CCCreditService.
     * 
     * @param industryDatatype
     */
    public void setIndustryDatatype(java.lang.String industryDatatype) {
        this.industryDatatype = industryDatatype;
    }


    /**
     * Gets the commerceIndicator value for this CCCreditService.
     * 
     * @return commerceIndicator
     */
    public java.lang.String getCommerceIndicator() {
        return commerceIndicator;
    }


    /**
     * Sets the commerceIndicator value for this CCCreditService.
     * 
     * @param commerceIndicator
     */
    public void setCommerceIndicator(java.lang.String commerceIndicator) {
        this.commerceIndicator = commerceIndicator;
    }


    /**
     * Gets the billPayment value for this CCCreditService.
     * 
     * @return billPayment
     */
    public java.lang.String getBillPayment() {
        return billPayment;
    }


    /**
     * Sets the billPayment value for this CCCreditService.
     * 
     * @param billPayment
     */
    public void setBillPayment(java.lang.String billPayment) {
        this.billPayment = billPayment;
    }


    /**
     * Gets the authorizationXID value for this CCCreditService.
     * 
     * @return authorizationXID
     */
    public java.lang.String getAuthorizationXID() {
        return authorizationXID;
    }


    /**
     * Sets the authorizationXID value for this CCCreditService.
     * 
     * @param authorizationXID
     */
    public void setAuthorizationXID(java.lang.String authorizationXID) {
        this.authorizationXID = authorizationXID;
    }


    /**
     * Gets the occurrenceNumber value for this CCCreditService.
     * 
     * @return occurrenceNumber
     */
    public java.lang.String getOccurrenceNumber() {
        return occurrenceNumber;
    }


    /**
     * Sets the occurrenceNumber value for this CCCreditService.
     * 
     * @param occurrenceNumber
     */
    public void setOccurrenceNumber(java.lang.String occurrenceNumber) {
        this.occurrenceNumber = occurrenceNumber;
    }


    /**
     * Gets the authCode value for this CCCreditService.
     * 
     * @return authCode
     */
    public java.lang.String getAuthCode() {
        return authCode;
    }


    /**
     * Sets the authCode value for this CCCreditService.
     * 
     * @param authCode
     */
    public void setAuthCode(java.lang.String authCode) {
        this.authCode = authCode;
    }


    /**
     * Gets the captureRequestToken value for this CCCreditService.
     * 
     * @return captureRequestToken
     */
    public java.lang.String getCaptureRequestToken() {
        return captureRequestToken;
    }


    /**
     * Sets the captureRequestToken value for this CCCreditService.
     * 
     * @param captureRequestToken
     */
    public void setCaptureRequestToken(java.lang.String captureRequestToken) {
        this.captureRequestToken = captureRequestToken;
    }


    /**
     * Gets the merchantReceiptNumber value for this CCCreditService.
     * 
     * @return merchantReceiptNumber
     */
    public java.lang.String getMerchantReceiptNumber() {
        return merchantReceiptNumber;
    }


    /**
     * Sets the merchantReceiptNumber value for this CCCreditService.
     * 
     * @param merchantReceiptNumber
     */
    public void setMerchantReceiptNumber(java.lang.String merchantReceiptNumber) {
        this.merchantReceiptNumber = merchantReceiptNumber;
    }


    /**
     * Gets the checksumKey value for this CCCreditService.
     * 
     * @return checksumKey
     */
    public java.lang.String getChecksumKey() {
        return checksumKey;
    }


    /**
     * Sets the checksumKey value for this CCCreditService.
     * 
     * @param checksumKey
     */
    public void setChecksumKey(java.lang.String checksumKey) {
        this.checksumKey = checksumKey;
    }


    /**
     * Gets the aggregatorID value for this CCCreditService.
     * 
     * @return aggregatorID
     */
    public java.lang.String getAggregatorID() {
        return aggregatorID;
    }


    /**
     * Sets the aggregatorID value for this CCCreditService.
     * 
     * @param aggregatorID
     */
    public void setAggregatorID(java.lang.String aggregatorID) {
        this.aggregatorID = aggregatorID;
    }


    /**
     * Gets the run value for this CCCreditService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this CCCreditService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CCCreditService)) return false;
        CCCreditService other = (CCCreditService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.captureRequestID==null && other.getCaptureRequestID()==null) || 
             (this.captureRequestID!=null &&
              this.captureRequestID.equals(other.getCaptureRequestID()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.partialPaymentID==null && other.getPartialPaymentID()==null) || 
             (this.partialPaymentID!=null &&
              this.partialPaymentID.equals(other.getPartialPaymentID()))) &&
            ((this.purchasingLevel==null && other.getPurchasingLevel()==null) || 
             (this.purchasingLevel!=null &&
              this.purchasingLevel.equals(other.getPurchasingLevel()))) &&
            ((this.industryDatatype==null && other.getIndustryDatatype()==null) || 
             (this.industryDatatype!=null &&
              this.industryDatatype.equals(other.getIndustryDatatype()))) &&
            ((this.commerceIndicator==null && other.getCommerceIndicator()==null) || 
             (this.commerceIndicator!=null &&
              this.commerceIndicator.equals(other.getCommerceIndicator()))) &&
            ((this.billPayment==null && other.getBillPayment()==null) || 
             (this.billPayment!=null &&
              this.billPayment.equals(other.getBillPayment()))) &&
            ((this.authorizationXID==null && other.getAuthorizationXID()==null) || 
             (this.authorizationXID!=null &&
              this.authorizationXID.equals(other.getAuthorizationXID()))) &&
            ((this.occurrenceNumber==null && other.getOccurrenceNumber()==null) || 
             (this.occurrenceNumber!=null &&
              this.occurrenceNumber.equals(other.getOccurrenceNumber()))) &&
            ((this.authCode==null && other.getAuthCode()==null) || 
             (this.authCode!=null &&
              this.authCode.equals(other.getAuthCode()))) &&
            ((this.captureRequestToken==null && other.getCaptureRequestToken()==null) || 
             (this.captureRequestToken!=null &&
              this.captureRequestToken.equals(other.getCaptureRequestToken()))) &&
            ((this.merchantReceiptNumber==null && other.getMerchantReceiptNumber()==null) || 
             (this.merchantReceiptNumber!=null &&
              this.merchantReceiptNumber.equals(other.getMerchantReceiptNumber()))) &&
            ((this.checksumKey==null && other.getChecksumKey()==null) || 
             (this.checksumKey!=null &&
              this.checksumKey.equals(other.getChecksumKey()))) &&
            ((this.aggregatorID==null && other.getAggregatorID()==null) || 
             (this.aggregatorID!=null &&
              this.aggregatorID.equals(other.getAggregatorID()))) &&
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
        if (getCaptureRequestID() != null) {
            _hashCode += getCaptureRequestID().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getPartialPaymentID() != null) {
            _hashCode += getPartialPaymentID().hashCode();
        }
        if (getPurchasingLevel() != null) {
            _hashCode += getPurchasingLevel().hashCode();
        }
        if (getIndustryDatatype() != null) {
            _hashCode += getIndustryDatatype().hashCode();
        }
        if (getCommerceIndicator() != null) {
            _hashCode += getCommerceIndicator().hashCode();
        }
        if (getBillPayment() != null) {
            _hashCode += getBillPayment().hashCode();
        }
        if (getAuthorizationXID() != null) {
            _hashCode += getAuthorizationXID().hashCode();
        }
        if (getOccurrenceNumber() != null) {
            _hashCode += getOccurrenceNumber().hashCode();
        }
        if (getAuthCode() != null) {
            _hashCode += getAuthCode().hashCode();
        }
        if (getCaptureRequestToken() != null) {
            _hashCode += getCaptureRequestToken().hashCode();
        }
        if (getMerchantReceiptNumber() != null) {
            _hashCode += getMerchantReceiptNumber().hashCode();
        }
        if (getChecksumKey() != null) {
            _hashCode += getChecksumKey().hashCode();
        }
        if (getAggregatorID() != null) {
            _hashCode += getAggregatorID().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CCCreditService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCreditService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("captureRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "captureRequestID"));
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
        elemField.setFieldName("partialPaymentID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "partialPaymentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchasingLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchasingLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("industryDatatype");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "industryDatatype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commerceIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "commerceIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billPayment");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "billPayment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationXID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authorizationXID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("occurrenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "occurrenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("captureRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "captureRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantReceiptNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantReceiptNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checksumKey");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "checksumKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aggregatorID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "aggregatorID"));
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
