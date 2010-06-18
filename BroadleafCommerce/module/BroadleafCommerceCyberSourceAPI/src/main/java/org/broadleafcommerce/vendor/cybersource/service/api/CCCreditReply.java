/**
 * CCCreditReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class CCCreditReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String requestDateTime;

    private java.lang.String amount;

    private java.lang.String reconciliationID;

    private java.lang.String purchasingLevel3Enabled;

    private java.lang.String enhancedDataEnabled;

    private java.lang.String authorizationXID;

    private java.lang.String forwardCode;

    public CCCreditReply() {
    }

    public CCCreditReply(
           java.math.BigInteger reasonCode,
           java.lang.String requestDateTime,
           java.lang.String amount,
           java.lang.String reconciliationID,
           java.lang.String purchasingLevel3Enabled,
           java.lang.String enhancedDataEnabled,
           java.lang.String authorizationXID,
           java.lang.String forwardCode) {
           this.reasonCode = reasonCode;
           this.requestDateTime = requestDateTime;
           this.amount = amount;
           this.reconciliationID = reconciliationID;
           this.purchasingLevel3Enabled = purchasingLevel3Enabled;
           this.enhancedDataEnabled = enhancedDataEnabled;
           this.authorizationXID = authorizationXID;
           this.forwardCode = forwardCode;
    }


    /**
     * Gets the reasonCode value for this CCCreditReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this CCCreditReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the requestDateTime value for this CCCreditReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this CCCreditReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the amount value for this CCCreditReply.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this CCCreditReply.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the reconciliationID value for this CCCreditReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this CCCreditReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the purchasingLevel3Enabled value for this CCCreditReply.
     * 
     * @return purchasingLevel3Enabled
     */
    public java.lang.String getPurchasingLevel3Enabled() {
        return purchasingLevel3Enabled;
    }


    /**
     * Sets the purchasingLevel3Enabled value for this CCCreditReply.
     * 
     * @param purchasingLevel3Enabled
     */
    public void setPurchasingLevel3Enabled(java.lang.String purchasingLevel3Enabled) {
        this.purchasingLevel3Enabled = purchasingLevel3Enabled;
    }


    /**
     * Gets the enhancedDataEnabled value for this CCCreditReply.
     * 
     * @return enhancedDataEnabled
     */
    public java.lang.String getEnhancedDataEnabled() {
        return enhancedDataEnabled;
    }


    /**
     * Sets the enhancedDataEnabled value for this CCCreditReply.
     * 
     * @param enhancedDataEnabled
     */
    public void setEnhancedDataEnabled(java.lang.String enhancedDataEnabled) {
        this.enhancedDataEnabled = enhancedDataEnabled;
    }


    /**
     * Gets the authorizationXID value for this CCCreditReply.
     * 
     * @return authorizationXID
     */
    public java.lang.String getAuthorizationXID() {
        return authorizationXID;
    }


    /**
     * Sets the authorizationXID value for this CCCreditReply.
     * 
     * @param authorizationXID
     */
    public void setAuthorizationXID(java.lang.String authorizationXID) {
        this.authorizationXID = authorizationXID;
    }


    /**
     * Gets the forwardCode value for this CCCreditReply.
     * 
     * @return forwardCode
     */
    public java.lang.String getForwardCode() {
        return forwardCode;
    }


    /**
     * Sets the forwardCode value for this CCCreditReply.
     * 
     * @param forwardCode
     */
    public void setForwardCode(java.lang.String forwardCode) {
        this.forwardCode = forwardCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CCCreditReply)) return false;
        CCCreditReply other = (CCCreditReply) obj;
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
            ((this.purchasingLevel3Enabled==null && other.getPurchasingLevel3Enabled()==null) || 
             (this.purchasingLevel3Enabled!=null &&
              this.purchasingLevel3Enabled.equals(other.getPurchasingLevel3Enabled()))) &&
            ((this.enhancedDataEnabled==null && other.getEnhancedDataEnabled()==null) || 
             (this.enhancedDataEnabled!=null &&
              this.enhancedDataEnabled.equals(other.getEnhancedDataEnabled()))) &&
            ((this.authorizationXID==null && other.getAuthorizationXID()==null) || 
             (this.authorizationXID!=null &&
              this.authorizationXID.equals(other.getAuthorizationXID()))) &&
            ((this.forwardCode==null && other.getForwardCode()==null) || 
             (this.forwardCode!=null &&
              this.forwardCode.equals(other.getForwardCode())));
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
        if (getPurchasingLevel3Enabled() != null) {
            _hashCode += getPurchasingLevel3Enabled().hashCode();
        }
        if (getEnhancedDataEnabled() != null) {
            _hashCode += getEnhancedDataEnabled().hashCode();
        }
        if (getAuthorizationXID() != null) {
            _hashCode += getAuthorizationXID().hashCode();
        }
        if (getForwardCode() != null) {
            _hashCode += getForwardCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CCCreditReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCreditReply"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationXID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authorizationXID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forwardCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "forwardCode"));
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
