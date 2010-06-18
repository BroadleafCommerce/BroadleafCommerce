/**
 * AFSReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class AFSReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.math.BigInteger afsResult;

    private java.math.BigInteger hostSeverity;

    private java.lang.String consumerLocalTime;

    private java.lang.String afsFactorCode;

    private java.lang.String addressInfoCode;

    private java.lang.String hotlistInfoCode;

    private java.lang.String internetInfoCode;

    private java.lang.String phoneInfoCode;

    private java.lang.String suspiciousInfoCode;

    private java.lang.String velocityInfoCode;

    private java.lang.String identityInfoCode;

    private java.lang.String ipCountry;

    private java.lang.String ipState;

    private java.lang.String ipCity;

    private java.lang.String ipRoutingMethod;

    private java.lang.String scoreModelUsed;

    private java.lang.String binCountry;

    private java.lang.String cardAccountType;

    private java.lang.String cardScheme;

    private java.lang.String cardIssuer;

    private org.broadleafcommerce.vendor.cybersource.service.api.DeviceFingerprint deviceFingerprint;

    public AFSReply() {
    }

    public AFSReply(
           java.math.BigInteger reasonCode,
           java.math.BigInteger afsResult,
           java.math.BigInteger hostSeverity,
           java.lang.String consumerLocalTime,
           java.lang.String afsFactorCode,
           java.lang.String addressInfoCode,
           java.lang.String hotlistInfoCode,
           java.lang.String internetInfoCode,
           java.lang.String phoneInfoCode,
           java.lang.String suspiciousInfoCode,
           java.lang.String velocityInfoCode,
           java.lang.String identityInfoCode,
           java.lang.String ipCountry,
           java.lang.String ipState,
           java.lang.String ipCity,
           java.lang.String ipRoutingMethod,
           java.lang.String scoreModelUsed,
           java.lang.String binCountry,
           java.lang.String cardAccountType,
           java.lang.String cardScheme,
           java.lang.String cardIssuer,
           org.broadleafcommerce.vendor.cybersource.service.api.DeviceFingerprint deviceFingerprint) {
           this.reasonCode = reasonCode;
           this.afsResult = afsResult;
           this.hostSeverity = hostSeverity;
           this.consumerLocalTime = consumerLocalTime;
           this.afsFactorCode = afsFactorCode;
           this.addressInfoCode = addressInfoCode;
           this.hotlistInfoCode = hotlistInfoCode;
           this.internetInfoCode = internetInfoCode;
           this.phoneInfoCode = phoneInfoCode;
           this.suspiciousInfoCode = suspiciousInfoCode;
           this.velocityInfoCode = velocityInfoCode;
           this.identityInfoCode = identityInfoCode;
           this.ipCountry = ipCountry;
           this.ipState = ipState;
           this.ipCity = ipCity;
           this.ipRoutingMethod = ipRoutingMethod;
           this.scoreModelUsed = scoreModelUsed;
           this.binCountry = binCountry;
           this.cardAccountType = cardAccountType;
           this.cardScheme = cardScheme;
           this.cardIssuer = cardIssuer;
           this.deviceFingerprint = deviceFingerprint;
    }


    /**
     * Gets the reasonCode value for this AFSReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this AFSReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the afsResult value for this AFSReply.
     * 
     * @return afsResult
     */
    public java.math.BigInteger getAfsResult() {
        return afsResult;
    }


    /**
     * Sets the afsResult value for this AFSReply.
     * 
     * @param afsResult
     */
    public void setAfsResult(java.math.BigInteger afsResult) {
        this.afsResult = afsResult;
    }


    /**
     * Gets the hostSeverity value for this AFSReply.
     * 
     * @return hostSeverity
     */
    public java.math.BigInteger getHostSeverity() {
        return hostSeverity;
    }


    /**
     * Sets the hostSeverity value for this AFSReply.
     * 
     * @param hostSeverity
     */
    public void setHostSeverity(java.math.BigInteger hostSeverity) {
        this.hostSeverity = hostSeverity;
    }


    /**
     * Gets the consumerLocalTime value for this AFSReply.
     * 
     * @return consumerLocalTime
     */
    public java.lang.String getConsumerLocalTime() {
        return consumerLocalTime;
    }


    /**
     * Sets the consumerLocalTime value for this AFSReply.
     * 
     * @param consumerLocalTime
     */
    public void setConsumerLocalTime(java.lang.String consumerLocalTime) {
        this.consumerLocalTime = consumerLocalTime;
    }


    /**
     * Gets the afsFactorCode value for this AFSReply.
     * 
     * @return afsFactorCode
     */
    public java.lang.String getAfsFactorCode() {
        return afsFactorCode;
    }


    /**
     * Sets the afsFactorCode value for this AFSReply.
     * 
     * @param afsFactorCode
     */
    public void setAfsFactorCode(java.lang.String afsFactorCode) {
        this.afsFactorCode = afsFactorCode;
    }


    /**
     * Gets the addressInfoCode value for this AFSReply.
     * 
     * @return addressInfoCode
     */
    public java.lang.String getAddressInfoCode() {
        return addressInfoCode;
    }


    /**
     * Sets the addressInfoCode value for this AFSReply.
     * 
     * @param addressInfoCode
     */
    public void setAddressInfoCode(java.lang.String addressInfoCode) {
        this.addressInfoCode = addressInfoCode;
    }


    /**
     * Gets the hotlistInfoCode value for this AFSReply.
     * 
     * @return hotlistInfoCode
     */
    public java.lang.String getHotlistInfoCode() {
        return hotlistInfoCode;
    }


    /**
     * Sets the hotlistInfoCode value for this AFSReply.
     * 
     * @param hotlistInfoCode
     */
    public void setHotlistInfoCode(java.lang.String hotlistInfoCode) {
        this.hotlistInfoCode = hotlistInfoCode;
    }


    /**
     * Gets the internetInfoCode value for this AFSReply.
     * 
     * @return internetInfoCode
     */
    public java.lang.String getInternetInfoCode() {
        return internetInfoCode;
    }


    /**
     * Sets the internetInfoCode value for this AFSReply.
     * 
     * @param internetInfoCode
     */
    public void setInternetInfoCode(java.lang.String internetInfoCode) {
        this.internetInfoCode = internetInfoCode;
    }


    /**
     * Gets the phoneInfoCode value for this AFSReply.
     * 
     * @return phoneInfoCode
     */
    public java.lang.String getPhoneInfoCode() {
        return phoneInfoCode;
    }


    /**
     * Sets the phoneInfoCode value for this AFSReply.
     * 
     * @param phoneInfoCode
     */
    public void setPhoneInfoCode(java.lang.String phoneInfoCode) {
        this.phoneInfoCode = phoneInfoCode;
    }


    /**
     * Gets the suspiciousInfoCode value for this AFSReply.
     * 
     * @return suspiciousInfoCode
     */
    public java.lang.String getSuspiciousInfoCode() {
        return suspiciousInfoCode;
    }


    /**
     * Sets the suspiciousInfoCode value for this AFSReply.
     * 
     * @param suspiciousInfoCode
     */
    public void setSuspiciousInfoCode(java.lang.String suspiciousInfoCode) {
        this.suspiciousInfoCode = suspiciousInfoCode;
    }


    /**
     * Gets the velocityInfoCode value for this AFSReply.
     * 
     * @return velocityInfoCode
     */
    public java.lang.String getVelocityInfoCode() {
        return velocityInfoCode;
    }


    /**
     * Sets the velocityInfoCode value for this AFSReply.
     * 
     * @param velocityInfoCode
     */
    public void setVelocityInfoCode(java.lang.String velocityInfoCode) {
        this.velocityInfoCode = velocityInfoCode;
    }


    /**
     * Gets the identityInfoCode value for this AFSReply.
     * 
     * @return identityInfoCode
     */
    public java.lang.String getIdentityInfoCode() {
        return identityInfoCode;
    }


    /**
     * Sets the identityInfoCode value for this AFSReply.
     * 
     * @param identityInfoCode
     */
    public void setIdentityInfoCode(java.lang.String identityInfoCode) {
        this.identityInfoCode = identityInfoCode;
    }


    /**
     * Gets the ipCountry value for this AFSReply.
     * 
     * @return ipCountry
     */
    public java.lang.String getIpCountry() {
        return ipCountry;
    }


    /**
     * Sets the ipCountry value for this AFSReply.
     * 
     * @param ipCountry
     */
    public void setIpCountry(java.lang.String ipCountry) {
        this.ipCountry = ipCountry;
    }


    /**
     * Gets the ipState value for this AFSReply.
     * 
     * @return ipState
     */
    public java.lang.String getIpState() {
        return ipState;
    }


    /**
     * Sets the ipState value for this AFSReply.
     * 
     * @param ipState
     */
    public void setIpState(java.lang.String ipState) {
        this.ipState = ipState;
    }


    /**
     * Gets the ipCity value for this AFSReply.
     * 
     * @return ipCity
     */
    public java.lang.String getIpCity() {
        return ipCity;
    }


    /**
     * Sets the ipCity value for this AFSReply.
     * 
     * @param ipCity
     */
    public void setIpCity(java.lang.String ipCity) {
        this.ipCity = ipCity;
    }


    /**
     * Gets the ipRoutingMethod value for this AFSReply.
     * 
     * @return ipRoutingMethod
     */
    public java.lang.String getIpRoutingMethod() {
        return ipRoutingMethod;
    }


    /**
     * Sets the ipRoutingMethod value for this AFSReply.
     * 
     * @param ipRoutingMethod
     */
    public void setIpRoutingMethod(java.lang.String ipRoutingMethod) {
        this.ipRoutingMethod = ipRoutingMethod;
    }


    /**
     * Gets the scoreModelUsed value for this AFSReply.
     * 
     * @return scoreModelUsed
     */
    public java.lang.String getScoreModelUsed() {
        return scoreModelUsed;
    }


    /**
     * Sets the scoreModelUsed value for this AFSReply.
     * 
     * @param scoreModelUsed
     */
    public void setScoreModelUsed(java.lang.String scoreModelUsed) {
        this.scoreModelUsed = scoreModelUsed;
    }


    /**
     * Gets the binCountry value for this AFSReply.
     * 
     * @return binCountry
     */
    public java.lang.String getBinCountry() {
        return binCountry;
    }


    /**
     * Sets the binCountry value for this AFSReply.
     * 
     * @param binCountry
     */
    public void setBinCountry(java.lang.String binCountry) {
        this.binCountry = binCountry;
    }


    /**
     * Gets the cardAccountType value for this AFSReply.
     * 
     * @return cardAccountType
     */
    public java.lang.String getCardAccountType() {
        return cardAccountType;
    }


    /**
     * Sets the cardAccountType value for this AFSReply.
     * 
     * @param cardAccountType
     */
    public void setCardAccountType(java.lang.String cardAccountType) {
        this.cardAccountType = cardAccountType;
    }


    /**
     * Gets the cardScheme value for this AFSReply.
     * 
     * @return cardScheme
     */
    public java.lang.String getCardScheme() {
        return cardScheme;
    }


    /**
     * Sets the cardScheme value for this AFSReply.
     * 
     * @param cardScheme
     */
    public void setCardScheme(java.lang.String cardScheme) {
        this.cardScheme = cardScheme;
    }


    /**
     * Gets the cardIssuer value for this AFSReply.
     * 
     * @return cardIssuer
     */
    public java.lang.String getCardIssuer() {
        return cardIssuer;
    }


    /**
     * Sets the cardIssuer value for this AFSReply.
     * 
     * @param cardIssuer
     */
    public void setCardIssuer(java.lang.String cardIssuer) {
        this.cardIssuer = cardIssuer;
    }


    /**
     * Gets the deviceFingerprint value for this AFSReply.
     * 
     * @return deviceFingerprint
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DeviceFingerprint getDeviceFingerprint() {
        return deviceFingerprint;
    }


    /**
     * Sets the deviceFingerprint value for this AFSReply.
     * 
     * @param deviceFingerprint
     */
    public void setDeviceFingerprint(org.broadleafcommerce.vendor.cybersource.service.api.DeviceFingerprint deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AFSReply)) return false;
        AFSReply other = (AFSReply) obj;
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
            ((this.afsResult==null && other.getAfsResult()==null) || 
             (this.afsResult!=null &&
              this.afsResult.equals(other.getAfsResult()))) &&
            ((this.hostSeverity==null && other.getHostSeverity()==null) || 
             (this.hostSeverity!=null &&
              this.hostSeverity.equals(other.getHostSeverity()))) &&
            ((this.consumerLocalTime==null && other.getConsumerLocalTime()==null) || 
             (this.consumerLocalTime!=null &&
              this.consumerLocalTime.equals(other.getConsumerLocalTime()))) &&
            ((this.afsFactorCode==null && other.getAfsFactorCode()==null) || 
             (this.afsFactorCode!=null &&
              this.afsFactorCode.equals(other.getAfsFactorCode()))) &&
            ((this.addressInfoCode==null && other.getAddressInfoCode()==null) || 
             (this.addressInfoCode!=null &&
              this.addressInfoCode.equals(other.getAddressInfoCode()))) &&
            ((this.hotlistInfoCode==null && other.getHotlistInfoCode()==null) || 
             (this.hotlistInfoCode!=null &&
              this.hotlistInfoCode.equals(other.getHotlistInfoCode()))) &&
            ((this.internetInfoCode==null && other.getInternetInfoCode()==null) || 
             (this.internetInfoCode!=null &&
              this.internetInfoCode.equals(other.getInternetInfoCode()))) &&
            ((this.phoneInfoCode==null && other.getPhoneInfoCode()==null) || 
             (this.phoneInfoCode!=null &&
              this.phoneInfoCode.equals(other.getPhoneInfoCode()))) &&
            ((this.suspiciousInfoCode==null && other.getSuspiciousInfoCode()==null) || 
             (this.suspiciousInfoCode!=null &&
              this.suspiciousInfoCode.equals(other.getSuspiciousInfoCode()))) &&
            ((this.velocityInfoCode==null && other.getVelocityInfoCode()==null) || 
             (this.velocityInfoCode!=null &&
              this.velocityInfoCode.equals(other.getVelocityInfoCode()))) &&
            ((this.identityInfoCode==null && other.getIdentityInfoCode()==null) || 
             (this.identityInfoCode!=null &&
              this.identityInfoCode.equals(other.getIdentityInfoCode()))) &&
            ((this.ipCountry==null && other.getIpCountry()==null) || 
             (this.ipCountry!=null &&
              this.ipCountry.equals(other.getIpCountry()))) &&
            ((this.ipState==null && other.getIpState()==null) || 
             (this.ipState!=null &&
              this.ipState.equals(other.getIpState()))) &&
            ((this.ipCity==null && other.getIpCity()==null) || 
             (this.ipCity!=null &&
              this.ipCity.equals(other.getIpCity()))) &&
            ((this.ipRoutingMethod==null && other.getIpRoutingMethod()==null) || 
             (this.ipRoutingMethod!=null &&
              this.ipRoutingMethod.equals(other.getIpRoutingMethod()))) &&
            ((this.scoreModelUsed==null && other.getScoreModelUsed()==null) || 
             (this.scoreModelUsed!=null &&
              this.scoreModelUsed.equals(other.getScoreModelUsed()))) &&
            ((this.binCountry==null && other.getBinCountry()==null) || 
             (this.binCountry!=null &&
              this.binCountry.equals(other.getBinCountry()))) &&
            ((this.cardAccountType==null && other.getCardAccountType()==null) || 
             (this.cardAccountType!=null &&
              this.cardAccountType.equals(other.getCardAccountType()))) &&
            ((this.cardScheme==null && other.getCardScheme()==null) || 
             (this.cardScheme!=null &&
              this.cardScheme.equals(other.getCardScheme()))) &&
            ((this.cardIssuer==null && other.getCardIssuer()==null) || 
             (this.cardIssuer!=null &&
              this.cardIssuer.equals(other.getCardIssuer()))) &&
            ((this.deviceFingerprint==null && other.getDeviceFingerprint()==null) || 
             (this.deviceFingerprint!=null &&
              this.deviceFingerprint.equals(other.getDeviceFingerprint())));
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
        if (getAfsResult() != null) {
            _hashCode += getAfsResult().hashCode();
        }
        if (getHostSeverity() != null) {
            _hashCode += getHostSeverity().hashCode();
        }
        if (getConsumerLocalTime() != null) {
            _hashCode += getConsumerLocalTime().hashCode();
        }
        if (getAfsFactorCode() != null) {
            _hashCode += getAfsFactorCode().hashCode();
        }
        if (getAddressInfoCode() != null) {
            _hashCode += getAddressInfoCode().hashCode();
        }
        if (getHotlistInfoCode() != null) {
            _hashCode += getHotlistInfoCode().hashCode();
        }
        if (getInternetInfoCode() != null) {
            _hashCode += getInternetInfoCode().hashCode();
        }
        if (getPhoneInfoCode() != null) {
            _hashCode += getPhoneInfoCode().hashCode();
        }
        if (getSuspiciousInfoCode() != null) {
            _hashCode += getSuspiciousInfoCode().hashCode();
        }
        if (getVelocityInfoCode() != null) {
            _hashCode += getVelocityInfoCode().hashCode();
        }
        if (getIdentityInfoCode() != null) {
            _hashCode += getIdentityInfoCode().hashCode();
        }
        if (getIpCountry() != null) {
            _hashCode += getIpCountry().hashCode();
        }
        if (getIpState() != null) {
            _hashCode += getIpState().hashCode();
        }
        if (getIpCity() != null) {
            _hashCode += getIpCity().hashCode();
        }
        if (getIpRoutingMethod() != null) {
            _hashCode += getIpRoutingMethod().hashCode();
        }
        if (getScoreModelUsed() != null) {
            _hashCode += getScoreModelUsed().hashCode();
        }
        if (getBinCountry() != null) {
            _hashCode += getBinCountry().hashCode();
        }
        if (getCardAccountType() != null) {
            _hashCode += getCardAccountType().hashCode();
        }
        if (getCardScheme() != null) {
            _hashCode += getCardScheme().hashCode();
        }
        if (getCardIssuer() != null) {
            _hashCode += getCardIssuer().hashCode();
        }
        if (getDeviceFingerprint() != null) {
            _hashCode += getDeviceFingerprint().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AFSReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "AFSReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("afsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "afsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostSeverity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "hostSeverity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consumerLocalTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "consumerLocalTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("afsFactorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "afsFactorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressInfoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "addressInfoCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotlistInfoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "hotlistInfoCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("internetInfoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "internetInfoCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phoneInfoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "phoneInfoCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("suspiciousInfoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "suspiciousInfoCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("velocityInfoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "velocityInfoCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identityInfoCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "identityInfoCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ipCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipState");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ipState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipCity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ipCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipRoutingMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ipRoutingMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scoreModelUsed");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "scoreModelUsed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("binCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "binCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardAccountType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardAccountType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardScheme");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardScheme"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardIssuer");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardIssuer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceFingerprint");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "deviceFingerprint"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DeviceFingerprint"));
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
