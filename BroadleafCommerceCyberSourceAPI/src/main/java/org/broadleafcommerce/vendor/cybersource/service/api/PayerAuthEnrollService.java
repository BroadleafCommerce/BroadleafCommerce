/**
 * PayerAuthEnrollService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayerAuthEnrollService  implements java.io.Serializable {
    private java.lang.String httpAccept;

    private java.lang.String httpUserAgent;

    private java.lang.String merchantName;

    private java.lang.String merchantURL;

    private java.lang.String purchaseDescription;

    private java.lang.String purchaseTime;

    private java.lang.String countryCode;

    private java.lang.String acquirerBin;

    private java.lang.String loginID;

    private java.lang.String password;

    private java.lang.String merchantID;

    private java.lang.String run;  // attribute

    public PayerAuthEnrollService() {
    }

    public PayerAuthEnrollService(
           java.lang.String httpAccept,
           java.lang.String httpUserAgent,
           java.lang.String merchantName,
           java.lang.String merchantURL,
           java.lang.String purchaseDescription,
           java.lang.String purchaseTime,
           java.lang.String countryCode,
           java.lang.String acquirerBin,
           java.lang.String loginID,
           java.lang.String password,
           java.lang.String merchantID,
           java.lang.String run) {
           this.httpAccept = httpAccept;
           this.httpUserAgent = httpUserAgent;
           this.merchantName = merchantName;
           this.merchantURL = merchantURL;
           this.purchaseDescription = purchaseDescription;
           this.purchaseTime = purchaseTime;
           this.countryCode = countryCode;
           this.acquirerBin = acquirerBin;
           this.loginID = loginID;
           this.password = password;
           this.merchantID = merchantID;
           this.run = run;
    }


    /**
     * Gets the httpAccept value for this PayerAuthEnrollService.
     * 
     * @return httpAccept
     */
    public java.lang.String getHttpAccept() {
        return httpAccept;
    }


    /**
     * Sets the httpAccept value for this PayerAuthEnrollService.
     * 
     * @param httpAccept
     */
    public void setHttpAccept(java.lang.String httpAccept) {
        this.httpAccept = httpAccept;
    }


    /**
     * Gets the httpUserAgent value for this PayerAuthEnrollService.
     * 
     * @return httpUserAgent
     */
    public java.lang.String getHttpUserAgent() {
        return httpUserAgent;
    }


    /**
     * Sets the httpUserAgent value for this PayerAuthEnrollService.
     * 
     * @param httpUserAgent
     */
    public void setHttpUserAgent(java.lang.String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }


    /**
     * Gets the merchantName value for this PayerAuthEnrollService.
     * 
     * @return merchantName
     */
    public java.lang.String getMerchantName() {
        return merchantName;
    }


    /**
     * Sets the merchantName value for this PayerAuthEnrollService.
     * 
     * @param merchantName
     */
    public void setMerchantName(java.lang.String merchantName) {
        this.merchantName = merchantName;
    }


    /**
     * Gets the merchantURL value for this PayerAuthEnrollService.
     * 
     * @return merchantURL
     */
    public java.lang.String getMerchantURL() {
        return merchantURL;
    }


    /**
     * Sets the merchantURL value for this PayerAuthEnrollService.
     * 
     * @param merchantURL
     */
    public void setMerchantURL(java.lang.String merchantURL) {
        this.merchantURL = merchantURL;
    }


    /**
     * Gets the purchaseDescription value for this PayerAuthEnrollService.
     * 
     * @return purchaseDescription
     */
    public java.lang.String getPurchaseDescription() {
        return purchaseDescription;
    }


    /**
     * Sets the purchaseDescription value for this PayerAuthEnrollService.
     * 
     * @param purchaseDescription
     */
    public void setPurchaseDescription(java.lang.String purchaseDescription) {
        this.purchaseDescription = purchaseDescription;
    }


    /**
     * Gets the purchaseTime value for this PayerAuthEnrollService.
     * 
     * @return purchaseTime
     */
    public java.lang.String getPurchaseTime() {
        return purchaseTime;
    }


    /**
     * Sets the purchaseTime value for this PayerAuthEnrollService.
     * 
     * @param purchaseTime
     */
    public void setPurchaseTime(java.lang.String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }


    /**
     * Gets the countryCode value for this PayerAuthEnrollService.
     * 
     * @return countryCode
     */
    public java.lang.String getCountryCode() {
        return countryCode;
    }


    /**
     * Sets the countryCode value for this PayerAuthEnrollService.
     * 
     * @param countryCode
     */
    public void setCountryCode(java.lang.String countryCode) {
        this.countryCode = countryCode;
    }


    /**
     * Gets the acquirerBin value for this PayerAuthEnrollService.
     * 
     * @return acquirerBin
     */
    public java.lang.String getAcquirerBin() {
        return acquirerBin;
    }


    /**
     * Sets the acquirerBin value for this PayerAuthEnrollService.
     * 
     * @param acquirerBin
     */
    public void setAcquirerBin(java.lang.String acquirerBin) {
        this.acquirerBin = acquirerBin;
    }


    /**
     * Gets the loginID value for this PayerAuthEnrollService.
     * 
     * @return loginID
     */
    public java.lang.String getLoginID() {
        return loginID;
    }


    /**
     * Sets the loginID value for this PayerAuthEnrollService.
     * 
     * @param loginID
     */
    public void setLoginID(java.lang.String loginID) {
        this.loginID = loginID;
    }


    /**
     * Gets the password value for this PayerAuthEnrollService.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this PayerAuthEnrollService.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the merchantID value for this PayerAuthEnrollService.
     * 
     * @return merchantID
     */
    public java.lang.String getMerchantID() {
        return merchantID;
    }


    /**
     * Sets the merchantID value for this PayerAuthEnrollService.
     * 
     * @param merchantID
     */
    public void setMerchantID(java.lang.String merchantID) {
        this.merchantID = merchantID;
    }


    /**
     * Gets the run value for this PayerAuthEnrollService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayerAuthEnrollService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayerAuthEnrollService)) return false;
        PayerAuthEnrollService other = (PayerAuthEnrollService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.httpAccept==null && other.getHttpAccept()==null) || 
             (this.httpAccept!=null &&
              this.httpAccept.equals(other.getHttpAccept()))) &&
            ((this.httpUserAgent==null && other.getHttpUserAgent()==null) || 
             (this.httpUserAgent!=null &&
              this.httpUserAgent.equals(other.getHttpUserAgent()))) &&
            ((this.merchantName==null && other.getMerchantName()==null) || 
             (this.merchantName!=null &&
              this.merchantName.equals(other.getMerchantName()))) &&
            ((this.merchantURL==null && other.getMerchantURL()==null) || 
             (this.merchantURL!=null &&
              this.merchantURL.equals(other.getMerchantURL()))) &&
            ((this.purchaseDescription==null && other.getPurchaseDescription()==null) || 
             (this.purchaseDescription!=null &&
              this.purchaseDescription.equals(other.getPurchaseDescription()))) &&
            ((this.purchaseTime==null && other.getPurchaseTime()==null) || 
             (this.purchaseTime!=null &&
              this.purchaseTime.equals(other.getPurchaseTime()))) &&
            ((this.countryCode==null && other.getCountryCode()==null) || 
             (this.countryCode!=null &&
              this.countryCode.equals(other.getCountryCode()))) &&
            ((this.acquirerBin==null && other.getAcquirerBin()==null) || 
             (this.acquirerBin!=null &&
              this.acquirerBin.equals(other.getAcquirerBin()))) &&
            ((this.loginID==null && other.getLoginID()==null) || 
             (this.loginID!=null &&
              this.loginID.equals(other.getLoginID()))) &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.merchantID==null && other.getMerchantID()==null) || 
             (this.merchantID!=null &&
              this.merchantID.equals(other.getMerchantID()))) &&
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
        if (getHttpAccept() != null) {
            _hashCode += getHttpAccept().hashCode();
        }
        if (getHttpUserAgent() != null) {
            _hashCode += getHttpUserAgent().hashCode();
        }
        if (getMerchantName() != null) {
            _hashCode += getMerchantName().hashCode();
        }
        if (getMerchantURL() != null) {
            _hashCode += getMerchantURL().hashCode();
        }
        if (getPurchaseDescription() != null) {
            _hashCode += getPurchaseDescription().hashCode();
        }
        if (getPurchaseTime() != null) {
            _hashCode += getPurchaseTime().hashCode();
        }
        if (getCountryCode() != null) {
            _hashCode += getCountryCode().hashCode();
        }
        if (getAcquirerBin() != null) {
            _hashCode += getAcquirerBin().hashCode();
        }
        if (getLoginID() != null) {
            _hashCode += getLoginID().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getMerchantID() != null) {
            _hashCode += getMerchantID().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayerAuthEnrollService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthEnrollService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("httpAccept");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "httpAccept"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("httpUserAgent");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "httpUserAgent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantURL");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchaseDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchaseTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "countryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acquirerBin");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "acquirerBin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loginID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "loginID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantID"));
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
