/**
 * PayerAuthEnrollReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayerAuthEnrollReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String acsURL;

    private java.lang.String commerceIndicator;

    private java.lang.String eci;

    private java.lang.String paReq;

    private java.lang.String proxyPAN;

    private java.lang.String xid;

    private java.lang.String proofXML;

    private java.lang.String ucafCollectionIndicator;

    private java.lang.String veresEnrolled;

    public PayerAuthEnrollReply() {
    }

    public PayerAuthEnrollReply(
           java.math.BigInteger reasonCode,
           java.lang.String acsURL,
           java.lang.String commerceIndicator,
           java.lang.String eci,
           java.lang.String paReq,
           java.lang.String proxyPAN,
           java.lang.String xid,
           java.lang.String proofXML,
           java.lang.String ucafCollectionIndicator,
           java.lang.String veresEnrolled) {
           this.reasonCode = reasonCode;
           this.acsURL = acsURL;
           this.commerceIndicator = commerceIndicator;
           this.eci = eci;
           this.paReq = paReq;
           this.proxyPAN = proxyPAN;
           this.xid = xid;
           this.proofXML = proofXML;
           this.ucafCollectionIndicator = ucafCollectionIndicator;
           this.veresEnrolled = veresEnrolled;
    }


    /**
     * Gets the reasonCode value for this PayerAuthEnrollReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayerAuthEnrollReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the acsURL value for this PayerAuthEnrollReply.
     * 
     * @return acsURL
     */
    public java.lang.String getAcsURL() {
        return acsURL;
    }


    /**
     * Sets the acsURL value for this PayerAuthEnrollReply.
     * 
     * @param acsURL
     */
    public void setAcsURL(java.lang.String acsURL) {
        this.acsURL = acsURL;
    }


    /**
     * Gets the commerceIndicator value for this PayerAuthEnrollReply.
     * 
     * @return commerceIndicator
     */
    public java.lang.String getCommerceIndicator() {
        return commerceIndicator;
    }


    /**
     * Sets the commerceIndicator value for this PayerAuthEnrollReply.
     * 
     * @param commerceIndicator
     */
    public void setCommerceIndicator(java.lang.String commerceIndicator) {
        this.commerceIndicator = commerceIndicator;
    }


    /**
     * Gets the eci value for this PayerAuthEnrollReply.
     * 
     * @return eci
     */
    public java.lang.String getEci() {
        return eci;
    }


    /**
     * Sets the eci value for this PayerAuthEnrollReply.
     * 
     * @param eci
     */
    public void setEci(java.lang.String eci) {
        this.eci = eci;
    }


    /**
     * Gets the paReq value for this PayerAuthEnrollReply.
     * 
     * @return paReq
     */
    public java.lang.String getPaReq() {
        return paReq;
    }


    /**
     * Sets the paReq value for this PayerAuthEnrollReply.
     * 
     * @param paReq
     */
    public void setPaReq(java.lang.String paReq) {
        this.paReq = paReq;
    }


    /**
     * Gets the proxyPAN value for this PayerAuthEnrollReply.
     * 
     * @return proxyPAN
     */
    public java.lang.String getProxyPAN() {
        return proxyPAN;
    }


    /**
     * Sets the proxyPAN value for this PayerAuthEnrollReply.
     * 
     * @param proxyPAN
     */
    public void setProxyPAN(java.lang.String proxyPAN) {
        this.proxyPAN = proxyPAN;
    }


    /**
     * Gets the xid value for this PayerAuthEnrollReply.
     * 
     * @return xid
     */
    public java.lang.String getXid() {
        return xid;
    }


    /**
     * Sets the xid value for this PayerAuthEnrollReply.
     * 
     * @param xid
     */
    public void setXid(java.lang.String xid) {
        this.xid = xid;
    }


    /**
     * Gets the proofXML value for this PayerAuthEnrollReply.
     * 
     * @return proofXML
     */
    public java.lang.String getProofXML() {
        return proofXML;
    }


    /**
     * Sets the proofXML value for this PayerAuthEnrollReply.
     * 
     * @param proofXML
     */
    public void setProofXML(java.lang.String proofXML) {
        this.proofXML = proofXML;
    }


    /**
     * Gets the ucafCollectionIndicator value for this PayerAuthEnrollReply.
     * 
     * @return ucafCollectionIndicator
     */
    public java.lang.String getUcafCollectionIndicator() {
        return ucafCollectionIndicator;
    }


    /**
     * Sets the ucafCollectionIndicator value for this PayerAuthEnrollReply.
     * 
     * @param ucafCollectionIndicator
     */
    public void setUcafCollectionIndicator(java.lang.String ucafCollectionIndicator) {
        this.ucafCollectionIndicator = ucafCollectionIndicator;
    }


    /**
     * Gets the veresEnrolled value for this PayerAuthEnrollReply.
     * 
     * @return veresEnrolled
     */
    public java.lang.String getVeresEnrolled() {
        return veresEnrolled;
    }


    /**
     * Sets the veresEnrolled value for this PayerAuthEnrollReply.
     * 
     * @param veresEnrolled
     */
    public void setVeresEnrolled(java.lang.String veresEnrolled) {
        this.veresEnrolled = veresEnrolled;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayerAuthEnrollReply)) return false;
        PayerAuthEnrollReply other = (PayerAuthEnrollReply) obj;
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
            ((this.acsURL==null && other.getAcsURL()==null) || 
             (this.acsURL!=null &&
              this.acsURL.equals(other.getAcsURL()))) &&
            ((this.commerceIndicator==null && other.getCommerceIndicator()==null) || 
             (this.commerceIndicator!=null &&
              this.commerceIndicator.equals(other.getCommerceIndicator()))) &&
            ((this.eci==null && other.getEci()==null) || 
             (this.eci!=null &&
              this.eci.equals(other.getEci()))) &&
            ((this.paReq==null && other.getPaReq()==null) || 
             (this.paReq!=null &&
              this.paReq.equals(other.getPaReq()))) &&
            ((this.proxyPAN==null && other.getProxyPAN()==null) || 
             (this.proxyPAN!=null &&
              this.proxyPAN.equals(other.getProxyPAN()))) &&
            ((this.xid==null && other.getXid()==null) || 
             (this.xid!=null &&
              this.xid.equals(other.getXid()))) &&
            ((this.proofXML==null && other.getProofXML()==null) || 
             (this.proofXML!=null &&
              this.proofXML.equals(other.getProofXML()))) &&
            ((this.ucafCollectionIndicator==null && other.getUcafCollectionIndicator()==null) || 
             (this.ucafCollectionIndicator!=null &&
              this.ucafCollectionIndicator.equals(other.getUcafCollectionIndicator()))) &&
            ((this.veresEnrolled==null && other.getVeresEnrolled()==null) || 
             (this.veresEnrolled!=null &&
              this.veresEnrolled.equals(other.getVeresEnrolled())));
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
        if (getAcsURL() != null) {
            _hashCode += getAcsURL().hashCode();
        }
        if (getCommerceIndicator() != null) {
            _hashCode += getCommerceIndicator().hashCode();
        }
        if (getEci() != null) {
            _hashCode += getEci().hashCode();
        }
        if (getPaReq() != null) {
            _hashCode += getPaReq().hashCode();
        }
        if (getProxyPAN() != null) {
            _hashCode += getProxyPAN().hashCode();
        }
        if (getXid() != null) {
            _hashCode += getXid().hashCode();
        }
        if (getProofXML() != null) {
            _hashCode += getProofXML().hashCode();
        }
        if (getUcafCollectionIndicator() != null) {
            _hashCode += getUcafCollectionIndicator().hashCode();
        }
        if (getVeresEnrolled() != null) {
            _hashCode += getVeresEnrolled().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayerAuthEnrollReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthEnrollReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acsURL");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "acsURL"));
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
        elemField.setFieldName("paReq");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paReq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("proxyPAN");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "proxyPAN"));
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
        elemField.setFieldName("proofXML");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "proofXML"));
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
        elemField.setFieldName("veresEnrolled");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "veresEnrolled"));
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
