/**
 * PayerAuthValidateReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayerAuthValidateReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String authenticationResult;

    private java.lang.String authenticationStatusMessage;

    private java.lang.String cavv;

    private java.lang.String cavvAlgorithm;

    private java.lang.String commerceIndicator;

    private java.lang.String eci;

    private java.lang.String eciRaw;

    private java.lang.String xid;

    private java.lang.String ucafAuthenticationData;

    private java.lang.String ucafCollectionIndicator;

    private java.lang.String paresStatus;

    public PayerAuthValidateReply() {
    }

    public PayerAuthValidateReply(
           java.math.BigInteger reasonCode,
           java.lang.String authenticationResult,
           java.lang.String authenticationStatusMessage,
           java.lang.String cavv,
           java.lang.String cavvAlgorithm,
           java.lang.String commerceIndicator,
           java.lang.String eci,
           java.lang.String eciRaw,
           java.lang.String xid,
           java.lang.String ucafAuthenticationData,
           java.lang.String ucafCollectionIndicator,
           java.lang.String paresStatus) {
           this.reasonCode = reasonCode;
           this.authenticationResult = authenticationResult;
           this.authenticationStatusMessage = authenticationStatusMessage;
           this.cavv = cavv;
           this.cavvAlgorithm = cavvAlgorithm;
           this.commerceIndicator = commerceIndicator;
           this.eci = eci;
           this.eciRaw = eciRaw;
           this.xid = xid;
           this.ucafAuthenticationData = ucafAuthenticationData;
           this.ucafCollectionIndicator = ucafCollectionIndicator;
           this.paresStatus = paresStatus;
    }


    /**
     * Gets the reasonCode value for this PayerAuthValidateReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayerAuthValidateReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the authenticationResult value for this PayerAuthValidateReply.
     * 
     * @return authenticationResult
     */
    public java.lang.String getAuthenticationResult() {
        return authenticationResult;
    }


    /**
     * Sets the authenticationResult value for this PayerAuthValidateReply.
     * 
     * @param authenticationResult
     */
    public void setAuthenticationResult(java.lang.String authenticationResult) {
        this.authenticationResult = authenticationResult;
    }


    /**
     * Gets the authenticationStatusMessage value for this PayerAuthValidateReply.
     * 
     * @return authenticationStatusMessage
     */
    public java.lang.String getAuthenticationStatusMessage() {
        return authenticationStatusMessage;
    }


    /**
     * Sets the authenticationStatusMessage value for this PayerAuthValidateReply.
     * 
     * @param authenticationStatusMessage
     */
    public void setAuthenticationStatusMessage(java.lang.String authenticationStatusMessage) {
        this.authenticationStatusMessage = authenticationStatusMessage;
    }


    /**
     * Gets the cavv value for this PayerAuthValidateReply.
     * 
     * @return cavv
     */
    public java.lang.String getCavv() {
        return cavv;
    }


    /**
     * Sets the cavv value for this PayerAuthValidateReply.
     * 
     * @param cavv
     */
    public void setCavv(java.lang.String cavv) {
        this.cavv = cavv;
    }


    /**
     * Gets the cavvAlgorithm value for this PayerAuthValidateReply.
     * 
     * @return cavvAlgorithm
     */
    public java.lang.String getCavvAlgorithm() {
        return cavvAlgorithm;
    }


    /**
     * Sets the cavvAlgorithm value for this PayerAuthValidateReply.
     * 
     * @param cavvAlgorithm
     */
    public void setCavvAlgorithm(java.lang.String cavvAlgorithm) {
        this.cavvAlgorithm = cavvAlgorithm;
    }


    /**
     * Gets the commerceIndicator value for this PayerAuthValidateReply.
     * 
     * @return commerceIndicator
     */
    public java.lang.String getCommerceIndicator() {
        return commerceIndicator;
    }


    /**
     * Sets the commerceIndicator value for this PayerAuthValidateReply.
     * 
     * @param commerceIndicator
     */
    public void setCommerceIndicator(java.lang.String commerceIndicator) {
        this.commerceIndicator = commerceIndicator;
    }


    /**
     * Gets the eci value for this PayerAuthValidateReply.
     * 
     * @return eci
     */
    public java.lang.String getEci() {
        return eci;
    }


    /**
     * Sets the eci value for this PayerAuthValidateReply.
     * 
     * @param eci
     */
    public void setEci(java.lang.String eci) {
        this.eci = eci;
    }


    /**
     * Gets the eciRaw value for this PayerAuthValidateReply.
     * 
     * @return eciRaw
     */
    public java.lang.String getEciRaw() {
        return eciRaw;
    }


    /**
     * Sets the eciRaw value for this PayerAuthValidateReply.
     * 
     * @param eciRaw
     */
    public void setEciRaw(java.lang.String eciRaw) {
        this.eciRaw = eciRaw;
    }


    /**
     * Gets the xid value for this PayerAuthValidateReply.
     * 
     * @return xid
     */
    public java.lang.String getXid() {
        return xid;
    }


    /**
     * Sets the xid value for this PayerAuthValidateReply.
     * 
     * @param xid
     */
    public void setXid(java.lang.String xid) {
        this.xid = xid;
    }


    /**
     * Gets the ucafAuthenticationData value for this PayerAuthValidateReply.
     * 
     * @return ucafAuthenticationData
     */
    public java.lang.String getUcafAuthenticationData() {
        return ucafAuthenticationData;
    }


    /**
     * Sets the ucafAuthenticationData value for this PayerAuthValidateReply.
     * 
     * @param ucafAuthenticationData
     */
    public void setUcafAuthenticationData(java.lang.String ucafAuthenticationData) {
        this.ucafAuthenticationData = ucafAuthenticationData;
    }


    /**
     * Gets the ucafCollectionIndicator value for this PayerAuthValidateReply.
     * 
     * @return ucafCollectionIndicator
     */
    public java.lang.String getUcafCollectionIndicator() {
        return ucafCollectionIndicator;
    }


    /**
     * Sets the ucafCollectionIndicator value for this PayerAuthValidateReply.
     * 
     * @param ucafCollectionIndicator
     */
    public void setUcafCollectionIndicator(java.lang.String ucafCollectionIndicator) {
        this.ucafCollectionIndicator = ucafCollectionIndicator;
    }


    /**
     * Gets the paresStatus value for this PayerAuthValidateReply.
     * 
     * @return paresStatus
     */
    public java.lang.String getParesStatus() {
        return paresStatus;
    }


    /**
     * Sets the paresStatus value for this PayerAuthValidateReply.
     * 
     * @param paresStatus
     */
    public void setParesStatus(java.lang.String paresStatus) {
        this.paresStatus = paresStatus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayerAuthValidateReply)) return false;
        PayerAuthValidateReply other = (PayerAuthValidateReply) obj;
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
            ((this.authenticationResult==null && other.getAuthenticationResult()==null) || 
             (this.authenticationResult!=null &&
              this.authenticationResult.equals(other.getAuthenticationResult()))) &&
            ((this.authenticationStatusMessage==null && other.getAuthenticationStatusMessage()==null) || 
             (this.authenticationStatusMessage!=null &&
              this.authenticationStatusMessage.equals(other.getAuthenticationStatusMessage()))) &&
            ((this.cavv==null && other.getCavv()==null) || 
             (this.cavv!=null &&
              this.cavv.equals(other.getCavv()))) &&
            ((this.cavvAlgorithm==null && other.getCavvAlgorithm()==null) || 
             (this.cavvAlgorithm!=null &&
              this.cavvAlgorithm.equals(other.getCavvAlgorithm()))) &&
            ((this.commerceIndicator==null && other.getCommerceIndicator()==null) || 
             (this.commerceIndicator!=null &&
              this.commerceIndicator.equals(other.getCommerceIndicator()))) &&
            ((this.eci==null && other.getEci()==null) || 
             (this.eci!=null &&
              this.eci.equals(other.getEci()))) &&
            ((this.eciRaw==null && other.getEciRaw()==null) || 
             (this.eciRaw!=null &&
              this.eciRaw.equals(other.getEciRaw()))) &&
            ((this.xid==null && other.getXid()==null) || 
             (this.xid!=null &&
              this.xid.equals(other.getXid()))) &&
            ((this.ucafAuthenticationData==null && other.getUcafAuthenticationData()==null) || 
             (this.ucafAuthenticationData!=null &&
              this.ucafAuthenticationData.equals(other.getUcafAuthenticationData()))) &&
            ((this.ucafCollectionIndicator==null && other.getUcafCollectionIndicator()==null) || 
             (this.ucafCollectionIndicator!=null &&
              this.ucafCollectionIndicator.equals(other.getUcafCollectionIndicator()))) &&
            ((this.paresStatus==null && other.getParesStatus()==null) || 
             (this.paresStatus!=null &&
              this.paresStatus.equals(other.getParesStatus())));
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
        if (getAuthenticationResult() != null) {
            _hashCode += getAuthenticationResult().hashCode();
        }
        if (getAuthenticationStatusMessage() != null) {
            _hashCode += getAuthenticationStatusMessage().hashCode();
        }
        if (getCavv() != null) {
            _hashCode += getCavv().hashCode();
        }
        if (getCavvAlgorithm() != null) {
            _hashCode += getCavvAlgorithm().hashCode();
        }
        if (getCommerceIndicator() != null) {
            _hashCode += getCommerceIndicator().hashCode();
        }
        if (getEci() != null) {
            _hashCode += getEci().hashCode();
        }
        if (getEciRaw() != null) {
            _hashCode += getEciRaw().hashCode();
        }
        if (getXid() != null) {
            _hashCode += getXid().hashCode();
        }
        if (getUcafAuthenticationData() != null) {
            _hashCode += getUcafAuthenticationData().hashCode();
        }
        if (getUcafCollectionIndicator() != null) {
            _hashCode += getUcafCollectionIndicator().hashCode();
        }
        if (getParesStatus() != null) {
            _hashCode += getParesStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayerAuthValidateReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthValidateReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticationResult");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authenticationResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticationStatusMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authenticationStatusMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cavv");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cavv"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cavvAlgorithm");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cavvAlgorithm"));
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
        elemField.setFieldName("eci");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "eci"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eciRaw");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "eciRaw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("xid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "xid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ucafAuthenticationData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ucafAuthenticationData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ucafCollectionIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ucafCollectionIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paresStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paresStatus"));
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
