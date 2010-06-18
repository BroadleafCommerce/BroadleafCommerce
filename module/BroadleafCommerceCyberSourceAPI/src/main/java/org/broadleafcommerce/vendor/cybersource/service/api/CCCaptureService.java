/**
 * CCCaptureService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class CCCaptureService  implements java.io.Serializable {
    private java.lang.String authType;

    private java.lang.String verbalAuthCode;

    private java.lang.String authRequestID;

    private java.lang.String reconciliationID;

    private java.lang.String partialPaymentID;

    private java.lang.String purchasingLevel;

    private java.lang.String industryDatatype;

    private java.lang.String authRequestToken;

    private java.lang.String merchantReceiptNumber;

    private java.lang.String posData;

    private java.lang.String transactionID;

    private java.lang.String checksumKey;

    private java.lang.String run;  // attribute

    public CCCaptureService() {
    }

    public CCCaptureService(
           java.lang.String authType,
           java.lang.String verbalAuthCode,
           java.lang.String authRequestID,
           java.lang.String reconciliationID,
           java.lang.String partialPaymentID,
           java.lang.String purchasingLevel,
           java.lang.String industryDatatype,
           java.lang.String authRequestToken,
           java.lang.String merchantReceiptNumber,
           java.lang.String posData,
           java.lang.String transactionID,
           java.lang.String checksumKey,
           java.lang.String run) {
           this.authType = authType;
           this.verbalAuthCode = verbalAuthCode;
           this.authRequestID = authRequestID;
           this.reconciliationID = reconciliationID;
           this.partialPaymentID = partialPaymentID;
           this.purchasingLevel = purchasingLevel;
           this.industryDatatype = industryDatatype;
           this.authRequestToken = authRequestToken;
           this.merchantReceiptNumber = merchantReceiptNumber;
           this.posData = posData;
           this.transactionID = transactionID;
           this.checksumKey = checksumKey;
           this.run = run;
    }


    /**
     * Gets the authType value for this CCCaptureService.
     * 
     * @return authType
     */
    public java.lang.String getAuthType() {
        return authType;
    }


    /**
     * Sets the authType value for this CCCaptureService.
     * 
     * @param authType
     */
    public void setAuthType(java.lang.String authType) {
        this.authType = authType;
    }


    /**
     * Gets the verbalAuthCode value for this CCCaptureService.
     * 
     * @return verbalAuthCode
     */
    public java.lang.String getVerbalAuthCode() {
        return verbalAuthCode;
    }


    /**
     * Sets the verbalAuthCode value for this CCCaptureService.
     * 
     * @param verbalAuthCode
     */
    public void setVerbalAuthCode(java.lang.String verbalAuthCode) {
        this.verbalAuthCode = verbalAuthCode;
    }


    /**
     * Gets the authRequestID value for this CCCaptureService.
     * 
     * @return authRequestID
     */
    public java.lang.String getAuthRequestID() {
        return authRequestID;
    }


    /**
     * Sets the authRequestID value for this CCCaptureService.
     * 
     * @param authRequestID
     */
    public void setAuthRequestID(java.lang.String authRequestID) {
        this.authRequestID = authRequestID;
    }


    /**
     * Gets the reconciliationID value for this CCCaptureService.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this CCCaptureService.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the partialPaymentID value for this CCCaptureService.
     * 
     * @return partialPaymentID
     */
    public java.lang.String getPartialPaymentID() {
        return partialPaymentID;
    }


    /**
     * Sets the partialPaymentID value for this CCCaptureService.
     * 
     * @param partialPaymentID
     */
    public void setPartialPaymentID(java.lang.String partialPaymentID) {
        this.partialPaymentID = partialPaymentID;
    }


    /**
     * Gets the purchasingLevel value for this CCCaptureService.
     * 
     * @return purchasingLevel
     */
    public java.lang.String getPurchasingLevel() {
        return purchasingLevel;
    }


    /**
     * Sets the purchasingLevel value for this CCCaptureService.
     * 
     * @param purchasingLevel
     */
    public void setPurchasingLevel(java.lang.String purchasingLevel) {
        this.purchasingLevel = purchasingLevel;
    }


    /**
     * Gets the industryDatatype value for this CCCaptureService.
     * 
     * @return industryDatatype
     */
    public java.lang.String getIndustryDatatype() {
        return industryDatatype;
    }


    /**
     * Sets the industryDatatype value for this CCCaptureService.
     * 
     * @param industryDatatype
     */
    public void setIndustryDatatype(java.lang.String industryDatatype) {
        this.industryDatatype = industryDatatype;
    }


    /**
     * Gets the authRequestToken value for this CCCaptureService.
     * 
     * @return authRequestToken
     */
    public java.lang.String getAuthRequestToken() {
        return authRequestToken;
    }


    /**
     * Sets the authRequestToken value for this CCCaptureService.
     * 
     * @param authRequestToken
     */
    public void setAuthRequestToken(java.lang.String authRequestToken) {
        this.authRequestToken = authRequestToken;
    }


    /**
     * Gets the merchantReceiptNumber value for this CCCaptureService.
     * 
     * @return merchantReceiptNumber
     */
    public java.lang.String getMerchantReceiptNumber() {
        return merchantReceiptNumber;
    }


    /**
     * Sets the merchantReceiptNumber value for this CCCaptureService.
     * 
     * @param merchantReceiptNumber
     */
    public void setMerchantReceiptNumber(java.lang.String merchantReceiptNumber) {
        this.merchantReceiptNumber = merchantReceiptNumber;
    }


    /**
     * Gets the posData value for this CCCaptureService.
     * 
     * @return posData
     */
    public java.lang.String getPosData() {
        return posData;
    }


    /**
     * Sets the posData value for this CCCaptureService.
     * 
     * @param posData
     */
    public void setPosData(java.lang.String posData) {
        this.posData = posData;
    }


    /**
     * Gets the transactionID value for this CCCaptureService.
     * 
     * @return transactionID
     */
    public java.lang.String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this CCCaptureService.
     * 
     * @param transactionID
     */
    public void setTransactionID(java.lang.String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the checksumKey value for this CCCaptureService.
     * 
     * @return checksumKey
     */
    public java.lang.String getChecksumKey() {
        return checksumKey;
    }


    /**
     * Sets the checksumKey value for this CCCaptureService.
     * 
     * @param checksumKey
     */
    public void setChecksumKey(java.lang.String checksumKey) {
        this.checksumKey = checksumKey;
    }


    /**
     * Gets the run value for this CCCaptureService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this CCCaptureService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CCCaptureService)) return false;
        CCCaptureService other = (CCCaptureService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.authType==null && other.getAuthType()==null) || 
             (this.authType!=null &&
              this.authType.equals(other.getAuthType()))) &&
            ((this.verbalAuthCode==null && other.getVerbalAuthCode()==null) || 
             (this.verbalAuthCode!=null &&
              this.verbalAuthCode.equals(other.getVerbalAuthCode()))) &&
            ((this.authRequestID==null && other.getAuthRequestID()==null) || 
             (this.authRequestID!=null &&
              this.authRequestID.equals(other.getAuthRequestID()))) &&
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
            ((this.authRequestToken==null && other.getAuthRequestToken()==null) || 
             (this.authRequestToken!=null &&
              this.authRequestToken.equals(other.getAuthRequestToken()))) &&
            ((this.merchantReceiptNumber==null && other.getMerchantReceiptNumber()==null) || 
             (this.merchantReceiptNumber!=null &&
              this.merchantReceiptNumber.equals(other.getMerchantReceiptNumber()))) &&
            ((this.posData==null && other.getPosData()==null) || 
             (this.posData!=null &&
              this.posData.equals(other.getPosData()))) &&
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            ((this.checksumKey==null && other.getChecksumKey()==null) || 
             (this.checksumKey!=null &&
              this.checksumKey.equals(other.getChecksumKey()))) &&
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
        if (getAuthType() != null) {
            _hashCode += getAuthType().hashCode();
        }
        if (getVerbalAuthCode() != null) {
            _hashCode += getVerbalAuthCode().hashCode();
        }
        if (getAuthRequestID() != null) {
            _hashCode += getAuthRequestID().hashCode();
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
        if (getAuthRequestToken() != null) {
            _hashCode += getAuthRequestToken().hashCode();
        }
        if (getMerchantReceiptNumber() != null) {
            _hashCode += getMerchantReceiptNumber().hashCode();
        }
        if (getPosData() != null) {
            _hashCode += getPosData().hashCode();
        }
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        if (getChecksumKey() != null) {
            _hashCode += getChecksumKey().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CCCaptureService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCaptureService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verbalAuthCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "verbalAuthCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authRequestID"));
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
        elemField.setFieldName("authRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authRequestToken"));
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
        elemField.setFieldName("posData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "posData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "transactionID"));
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
